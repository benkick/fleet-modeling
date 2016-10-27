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
	
	private final Vehicles vehicles;
	private final List<Id<Vehicle>> assignedVeh;
	private List<Id<Vehicle>> leftVeh;
	private final Households households;
	//TODO: How to treat company-owned, but (mainly) privately-used vehicles?
	//private Companies companies;

	private final HouseholdUtils hhUtils;
	private final VehicleUtils vehUtils;

	public FleetModeling(Random random, int noofveh, int noofhh){
		this.random = random;
		this.noOfVeh = noofveh;
		this.noOfHH = noofhh;
		
		this.vehicles = new Vehicles();
		this.assignedVeh = new ArrayList<Id<Vehicle>>();
		this.leftVeh = new ArrayList<Id<Vehicle>>();
		this.households = new Households();
		this.hhUtils = new HouseholdUtils();
		this.vehUtils = new VehicleUtils();
	}
	
	public void preprocess() {
		log.info("Entering preprocessing...");
		generateInitialVehicles();
		generateInitialHouseholds();
		writeSecondHandCarMarket();
		log.info("Leaving preprocessing...");
	}

	public void run() {
		log.info("Entering simulation...");
//		scrapeVehicles();
		//The following could be influenced by many factors, e.g. how many vehicles die
		//TODO: Benjamin
//		generateNewVehicles();
//		assignNewVehicles2HH();
		//TODO: Marie
//		modelSecondHandCarMarket();
		log.info("Leaving simulation...");
	}

	public void postprocess() {
		log.info("Entering postprocessing...");
//		nothing so far
		log.info("Leaving postprocessing...");
		log.info("Shutting down.");
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
		log.info("Vehicles assigned to households: " + hhUtils.getTotalVehInAllHHCnt() +  "(check sum: " + this.assignedVeh.size() + ").");
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
			this.leftVeh.add(veh.getId());
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

	private void assignVehicles2HH(Household hh) {
		Integer noOfVehInHH = determineNoOfVehInHH();
		for(int vehCnt=0; vehCnt<noOfVehInHH; vehCnt++){ //avoids adding a vehicle for noOfVehInHH = 0
			Vehicle veh = chooseVehicle();
			hh.getVehInHH().addVehicle(veh);
		}
	}

	//TODO: it might happen that there are more/less vehicles than needed...
	//TODO: zufälliges Ziehen ohne zurücklegen > performanter machen?
//	private Vehicle chooseVehicle() {
//		Vehicle chosenVeh = null;
//		Map<Id<Vehicle>, Vehicle> availVeh = this.vehicles.getVehicles();
//		if(this.assignedVeh.size()>=availVeh.size()){
//			throw new RuntimeException("All vehicles have already been assigned to households. Aborting...");
//		}
//		
//		for(int i=0; i<availVeh.size(); i++){
//			int rd = random.nextInt(availVeh.size()) + 1;
//			log.info("Random " +rd);
//			chosenVeh = availVeh.get(Id.createVehicleId(rd));
//			log.info("Chosen veh " + chosenVeh);
//			if(!this.assignedVeh.contains(chosenVeh.getId())){
//				this.assignedVeh.add(chosenVeh.getId());
//				return chosenVeh;
//			} else { 
//				//chosen vehicle already assigned, go to next random vehicle
//			}
//		}
//		return chosenVeh;
//	}

// new chooseVehicle method, runs without errors, but does it do what we want? Marie, october 21th 
	private Vehicle chooseVehicle(){
		Id<Vehicle> chosenVehId = null;
		Vehicle chosenVeh = null;
		if(this.leftVeh.size()==0){
			throw new RuntimeException("All vehicles have already been assigned to households. Aborting...");
		}
		int rd = random.nextInt(this.leftVeh.size()); 
		chosenVehId = leftVeh.get(rd);
		chosenVeh = vehicles.getVehicles().get(chosenVehId);
		this.leftVeh.remove(chosenVehId);
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

	//TODO: not all drivetrains considered!
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
	//private void modelSecondHandCarMarket(){
	//	Vehicles vehForSale = chooseVehForSale();
	//	Households buyingHH = chooseBuyingHH();
	//}
	
	//TODO: do not iterate over vehicles - iterate over assignedVeh
	private Vehicles chooseVehForSale(){
		Vehicles vehForSale = new Vehicles();
		ArrayList <Id<Vehicle>> vehIdForSale = new ArrayList<Id<Vehicle>>();
		for(Map.Entry<Id<Vehicle>,Vehicle> entry : this.vehicles.getVehicles().entrySet()){
			double rd = random.nextDouble();
			if(sellUsedVeh(entry.getValue(),rd)){
				vehForSale.getVehicles().put(entry.getKey(), entry.getValue()) ; 
			}	
		}
		return vehForSale;
	}
	
	private boolean sellUsedVeh(Vehicle veh, Double rand){
		Drivetrain drivetrain = veh.getDt();
		if(drivetrain.equals(Drivetrain.GASOLINE) && rand < 0.1645){
			return true;
		}else if(drivetrain.equals(Drivetrain.DIESEL) && rand < 0.1665){
			return true;
		}else if(drivetrain.equals(Drivetrain.NATURAL_GAS) && rand < 0.1687){
			return true;
		}else if(drivetrain.equals(Drivetrain.HYBRID) && rand < 0.1361){
			return true;
		}else if(drivetrain.equals(Drivetrain.HYBRID) && rand < 0.1266){
			return true;
		}else{
			return false;
		}
	}
	private void writeSecondHandCarMarket(){
		Vehicles example = chooseVehForSale();
		log.info("Cars for sale in the secondhand car market: "+ example.getVehicles().size());
		Households buyingHH = chooseBuyingHH();
		log.info("Households buying used cars: "+ buyingHH.getHouseholds().size());
		
	}
	
	private Households chooseBuyingHH(){
		Households buyingHH = new Households();
		for(Map.Entry<Id<Household>,Household> entry : this.households.getHouseholds().entrySet()){
			double rd = random.nextDouble();
			if(rd<0.2){
				buyingHH.getHouseholds().put(entry.getKey(), entry.getValue());
			}
		}
		return buyingHH;
	}
}