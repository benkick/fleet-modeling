package org.fleet.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.fleet.types.Drivetrain;
import org.fleet.types.Household;
import org.fleet.types.Households;
import org.fleet.types.Id;
import org.fleet.types.Vehicle;
import org.fleet.types.Vehicles;
import org.fleet.utils.HouseholdUtils;
import org.fleet.utils.VehicleUtils;


/**
 * @author benkick
 *
 */

public class FleetModeling {
	private static final Logger log = Logger.getLogger(FleetModeling.class.getName());

	private final Random random;
	private final int noOfVeh;
	private final int noOfHH;
	private int baseYear;
	
	private final Vehicles vehicles;
	private final List<Id<Vehicle>> assignedVeh;
	private final List<Id<Vehicle>> remainingVeh;
	private final Households households;
	//TODO: How to treat company-owned, but (mainly) privately-used vehicles?
	//private Companies companies;

	private final HouseholdUtils hhUtils;
	private final VehicleUtils vehUtils;


	public FleetModeling(Random random, int noofveh, int noofhh, int baseyear){
		this.random = random;
		this.noOfVeh = noofveh;
		this.noOfHH = noofhh;
		this.baseYear = baseyear;
		
		this.vehicles = new Vehicles();
		//TODO: is assignedVeh really needed?
		this.assignedVeh = new ArrayList<Id<Vehicle>>();
		this.remainingVeh = new ArrayList<Id<Vehicle>>();
		this.households = new Households();
		this.hhUtils = new HouseholdUtils();
		this.vehUtils = new VehicleUtils();
	}
	
	public void preprocess() {
		log.info("Entering preprocessing...");
		generateInitialVehicles();
		generateInitialHouseholds();
		log.info("Leaving preprocessing...");
	}

	public void run(int iterations) {
		log.info("Entering simulation...");
		for(int i=0; i<iterations; i++){
			log.info("=================================================");
			log.info("Simulating transactions for year " + this.baseYear);
			log.info("=================================================");
//			scrapeVehicles();
			//The following could be influenced by many factors, e.g. how many vehicles die
			//TODO: Benjamin
//			generateNewVehicles();
//			assignNewVehicles2HH();
			//TODO: Marie
			modelSecondHandCarMarket();
			this.baseYear += 1;
		}
		log.info("Leaving simulation...");
	}

	public void postprocess() {
		log.info("Entering postprocessing...");
//		nothing so far
		log.info("Leaving postprocessing...");
		log.info("Shutting down.");
	}
	
	private void modelSecondHandCarMarket() {
		Vehicles vehForSale = chooseVehiclesForSale();
		log.info("Cars for sale in the secondhand car market: "+ vehForSale.getVehicles().size());
		Households sellingHHs = takeVehiclesFromHH(vehForSale);
		log.info("Number of households, which sold vehicles: " + sellingHHs.getHouseholds().size());
		Households buyingHHs = chooseBuyingHHs(sellingHHs);
		log.info("Households buying used cars: "+ buyingHHs.getHouseholds().size());
		
		clearSecondHandMarket();
		
		
	}

//	TODO: the if statement suppresses the throwing of the exception in the addHousehold method, but we add an household only when its not in the Households-Container yet...
	
	/**
	 * This method determines the households which sold vehicles and removes the vehicle from the household's vehInHH-container.
	 * 
	 * @param soldVehicles the vehicles that have been sold
	 * @return the households which sold cars
	 */
	private Households takeVehiclesFromHH(Vehicles soldVehicles){
		Households sellingHHs = new Households();
		for(Household  hh : this.households.getHouseholds().values()){
			for(Vehicle veh : soldVehicles.getVehicles().values()){
				if(hh.getVehInHH().getVehicles().containsKey(veh.getId())){
					hh.getVehInHH().removeVehicle(veh);
					if(!sellingHHs.getHouseholds().containsKey(hh.getId())){
						sellingHHs.addHousehold(hh);
//					}else{
//						log.info("Household " + hh.getId() + " is selling more than one car");
					}
				}
			}
		}
		return sellingHHs;
	}

	private void clearSecondHandMarket() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method computes the used vehicles that should be sold this year using the method markVehForSale.
	 * 
	 * @see markVehForSale 
	 * @return the vehicles that should be sold
	 */
	private Vehicles chooseVehiclesForSale(){
		Vehicles vehForSale = new Vehicles();
		for(Id<Vehicle> vid : this.assignedVeh){
			Vehicle veh = this.vehicles.getVehicles().get(vid);
			if(markVehForSale(veh)){
				vehForSale.addVehicle(veh);
			}
		}
		return vehForSale;
	}

	/**
	 * This method determines if the given vehicle will be sold or not, depending on the drivetrain of the vehicle.
	 * Probabilities were taken from the KBA website and then converted to the needed conditional probabilities.
	 * <p>
	 * See <a href= "http://www.kba.de/DE/Statistik/Fahrzeuge/Besitzumschreibungen/Umwelt/2015_u_umwelt_dusl.html?nn=664062">http://www.kba.de</a>
	 * 
	 * 
	 * @param veh the vehicle that should be sold or not
	 * @return true or false
	 * 
	 */
	private boolean markVehForSale(Vehicle veh){
		double rand = random.nextDouble();
		Drivetrain drivetrain = veh.getDt();
		boolean sellVeh = false;

		if(drivetrain.equals(Drivetrain.GASOLINE)){
			if(rand < 0.1645) sellVeh = true;
		}else if(drivetrain.equals(Drivetrain.DIESEL)){
			if(rand < 0.1665) sellVeh = true;
		}else if(drivetrain.equals(Drivetrain.NATURAL_GAS)){
			if(rand < 0.1687) sellVeh = true;
		}else if(drivetrain.equals(Drivetrain.HYBRID)){
			if(rand < 0.1361) sellVeh = true;
		}else if(drivetrain.equals(Drivetrain.BEV)){
			if(rand < 0.1266) sellVeh = true;
		}else{
			throw new RuntimeException("No transaction probability defined for drivetrain " + drivetrain + ". Aborting...");
		}
		return sellVeh;
	}

//  TODO: Now the probability for a vehicle purchase is a bit distorted, because HHs which sold more than one vehicle appear only once
	/*
	 * assumption: 80% of the households, that sold cars will buy a "new" used vehicle
	 * implication(some probability theory): 6,51 % of the households, that didn't sell cars, will buy a new old one 
	 */
	/**
	 * This method chooses the households which will buy used cars.
	 * <p>
	 * We assume that the probability to buy a used car is 80% if the household sold a car this year.
	 * The probability to buy a car is much lower if no car was sold. 
	 * 
	 * @param sellingHHs the households which sold cars this year 
	 * @return the households which want to buy used cars 
	 */
	private Households chooseBuyingHHs(Households sellingHHs){
		Households buyingHHs = new Households();
		for(Household hh : this.households.getHouseholds().values()){
			double rd = random.nextDouble();
			if (!(sellingHHs.getHouseholds().containsKey(hh.getId())) && rd<0.0651){
				buyingHHs.addHousehold(hh);
			}
			else if(sellingHHs.getHouseholds().containsKey(hh.getId()) && rd<0.8){
				buyingHHs.addHousehold(hh);
			}
		}
		return buyingHHs;
	}

	private void writeInitialVehicleInformation() {
		vehUtils.countDt2Vehicles(this.vehicles);
		for(Drivetrain dt : vehUtils.getDtCount().keySet()){
			log.info("Created " + dt + " vehicles: " + vehUtils.getDtCount().get(dt));
		}
	}	
	
	private void writeInitialHouseholdInformation(){
		hhUtils.countVehPerHH(this.households);
		for(Integer vehInHHClass : hhUtils.getNoOfVeh2NoOfHHCount().keySet()){
			log.info("Households with " + vehInHHClass + " vehicle(s): " + hhUtils.getNoOfVeh2NoOfHHCount().get(vehInHHClass) + " of " + households.getHouseholds().size() + " households.");
		}
		log.info("Vehicles assigned to households: " + hhUtils.getTotalVehInAllHHCnt() +  " (check sum: " + this.assignedVeh.size() + ").");
		int marketMismatchCnt = vehUtils.getTotalVehCnt() - hhUtils.getTotalVehInAllHHCnt();
		log.info("Vehicles not assigned to households: " + marketMismatchCnt); //is always >= 0 because otherwise some HH would not have gotten enough vehicles
	}

	private void generateInitialVehicles() {
		log.info("Entering initial vehicle generation...");
		for(int i=1;i<=noOfVeh;i++){
			Id<Vehicle> vid = Id.createVehicleId(i);
			Drivetrain dt = determineDrivetrain();
			Vehicle veh = new Vehicle(vid, dt);
			this.vehicles.addVehicle(veh);
			this.remainingVeh.add(veh.getId());
		}
		writeInitialVehicleInformation();
		log.info("Leaving initial vehicle generation...");
	}

	private void generateInitialHouseholds() {
		log.info("Entering initial household generation...");
		for(int i=1;i<=noOfHH;i++){
			Id<Household> hid = Id.createHouseholdId(i);
			Household hh = new Household(hid);
			assignVehicles2HH(hh);
			this.households.addHousehold(hh);
		}
		writeInitialHouseholdInformation();
		log.info("Leaving initial household generation...");
	}

	
	/**
	 * This method assigns a vehicle to the given household, depending on the number of vehicles that the household should have.
	 * 
	 * @param hh the household that should get a vehicle
	 * @see chooseVehicle
	 */
	private void assignVehicles2HH(Household hh) {
		Integer noOfVehInHH = determineNoOfVehInHH();
		for(int vehCnt=0; vehCnt<noOfVehInHH; vehCnt++){ //avoids adding a vehicle for noOfVehInHH = 0
			Vehicle veh = chooseVehicle();
			hh.getVehInHH().addVehicle(veh);
		}
	}

	/**
	 * This method chooses the vehicle that should be assigned to the household.
	 * 
	 * @return the vehicle that should be assigned  to the household
	 * @see assignVehicles2HH
	 */
	private Vehicle chooseVehicle(){
		Id<Vehicle> chosenVehId = null;
		Vehicle chosenVeh = null;
		if(this.remainingVeh.size()==0){
			throw new RuntimeException("All vehicles have already been assigned to households. Aborting...");
		}
		int rd = random.nextInt(this.remainingVeh.size()); 
		chosenVehId = remainingVeh.get(rd);
		chosenVeh = vehicles.getVehicles().get(chosenVehId);
		this.remainingVeh.remove(chosenVehId);
		this.assignedVeh.add(chosenVehId);
		return chosenVeh;
	}

	private Integer determineNoOfVehInHH() {
		Integer noOfCars;
		double rd = random.nextDouble();
//		log.info("Random number: " + rd);
		if(rd<0.178) noOfCars = 0;
		else if(rd<0.71) noOfCars = 1;
		else if(rd<0.953) noOfCars = 2;
		else if(rd<0.994) noOfCars = 3;
		else noOfCars = 4;
		return noOfCars;
	}

	private Drivetrain determineDrivetrain() {
		Drivetrain dt;
		double rd = random.nextDouble();
//		log.info("Random number: " + rd);
		if(rd<0.662){
			dt = Drivetrain.GASOLINE;
		} else if(rd<0.984){
			dt = Drivetrain.DIESEL;
		} else if(rd<0.9965){
			dt = Drivetrain.NATURAL_GAS;
		} else if(rd<0.9994){
			dt = Drivetrain.HYBRID;
		} else{
			dt = Drivetrain.BEV;
		}
		return dt;
	}
}