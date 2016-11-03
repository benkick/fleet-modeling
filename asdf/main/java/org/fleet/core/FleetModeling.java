package org.fleet.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.fleet.types.Drivetrain;
import org.fleet.types.Household;
import org.fleet.types.Households;
import org.fleet.types.Id;
import org.fleet.types.Vehicle;
import org.fleet.types.Vehicles;
import org.fleet.utils.PrintConsoleUtils;

/**
 * @author benkick
 *
 */

public class FleetModeling {
	private static final Logger log = Logger.getLogger(FleetModeling.class.getName());

	private final Random random;
	private final int noOfVeh;
	private final int noOfHH;
	private int currentYear;
	
	private Vehicles vehicles;
	private final List<Id<Vehicle>> assignedVeh;
	private final List<Id<Vehicle>> remainingVeh;
	private final Households households;
	//TODO: How to treat company-owned, but (mainly) privately-used vehicles?
//	private Companies companies;
	
	private final PrintConsoleUtils printUtils;

	public FleetModeling(Random random, int noofveh, int noofhh, int baseyear){
		this.random = random;
		this.noOfVeh = noofveh;
		this.noOfHH = noofhh;
		this.currentYear = baseyear;
		
		this.vehicles = new Vehicles();
		this.assignedVeh = new ArrayList<Id<Vehicle>>();
		this.remainingVeh = new ArrayList<Id<Vehicle>>();
		this.households = new Households();
		
		this.printUtils = new PrintConsoleUtils();
	}
	
	public void preprocess() {
		log.info("Entering preprocessing...");
		generateInitialVehicles();
		generateInitialHouseholds();
		removeUnnecessaryVehicles();
		log.info("Leaving preprocessing...");
	}

	public void run(int iterations) {
		log.info("Entering simulation...");
		HouseholdUpdater hhu = new HouseholdUpdater();
		VehicleScrapping scr = new VehicleScrapping(this.random);
		NewVehicleMarket nvm = new NewVehicleMarket();
		UsedVehicleMarket uvm = new UsedVehicleMarket(this.random);
		for(int i=0; i<iterations; i++){
			log.info("\n=================================================\n"
					+ "Simulating transactions for year " + this.currentYear + "\n"
					+ "=================================================");
			/*
			 * TODO:
			 * - Fill with life
			 */
//			hhu.updateHouseholds(this.households);
			
			/*
			 * TODO:
			 * - Correct repeated draws
			 * - Make vehicle removal simultaneous and faster?
			 */
			scr.scrapVehicles(this.vehicles, this.assignedVeh, this.households, this.currentYear);
			
			/*
			 * TODO:
			 */
			nvm.model(scr.getAffectedHHs());
			
			/*
			 * TODO:
			 * - Perform buying/selling transactions
			 * - Think about households selling/buying more than one vehicle ("at least(?)"-comments)
			 */
			uvm.model(this.households, this.vehicles, this.assignedVeh);
			
			this.printUtils.printVehicleInformation(this.vehicles, this.currentYear);
			this.printUtils.printHouseholdInformation(this.households);
			
			this.currentYear += 1;
			
			scr.reset();
		}
		log.info("Leaving simulation...");
	}

	public void postprocess() {
		log.info("Entering postprocessing...");
//		nothing so far
		log.info("Leaving postprocessing...");
		log.info("Shutting down.");
	}

	private void removeUnnecessaryVehicles() {
		log.info("Entering removing unnecessary vehicles...");
		for(Id<Vehicle> vid : this.remainingVeh){
			this.vehicles.removeVehicle(this.vehicles.getVehicles().get(vid));
		}
		this.printUtils.printMarketMissmatchInformation(this.households, this.vehicles);
		log.info("Leaving removing unnecessary vehicles...");
	}

	private void generateInitialVehicles() {
		log.info("Entering initial vehicle generation...");
		for(int i=1;i<=noOfVeh;i++){
			Id<Vehicle> vid = Id.createVehicleId(i);
			Drivetrain dt = determineDrivetrain();
			int ym = determineYearOfManufacture();
			Vehicle veh = new Vehicle(vid, ym, dt);
			this.vehicles.addVehicle(veh);
			this.remainingVeh.add(veh.getId());
		}
		this.printUtils.printVehicleInformation(this.vehicles, this.currentYear);
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
		this.printUtils.printHouseholdInformation(this.households);
		this.printUtils.printMarketMissmatchInformation(this.households, this.vehicles);
		log.info("Leaving initial household generation...");
	}
	
	/**
	 * This method assigns vehicle(s) to a household, using a distribution of the number of vehicles per household.
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
	 * This method chooses a random vehicle from the available vehicles.
	 * 
	 * @return chosenVeh a random vehicle
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

	/**
	 * This method determines the number of vehicles a household has in the beginning.
	 * Probabilities are taken from a ADAC study.
	 * <p>
	 * See <a href=https://www.adac.de/_mmm/pdf/statistik_mobilitaet_in_deutschland_0111_46603.pdf>https://www.adac.de</a>
	 * 
	 * @return number of vehicles of a household.
	 */
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

	/**
	 * This method determines the year of manufacture of a vehicle.
	 * Probabilities are taken from the KBA website.
	 *  <p>
	 * See <a href=http://www.kba.de/SharedDocs/Publikationen/DE/Statistik/Fahrzeuge/FZ/Fachartikel/alter_20110415.pdf?__blob=publicationFile&>http://www.kba.de</a> 
	 * 
	 * @return year of manufacture
	 */
	private int determineYearOfManufacture() {
//		int ym = 2016;
		int ym = 0;
		double rd = random.nextDouble();
		if(rd<0.375){
			double rand = random.nextDouble();
			if(rand<0.2) ym = this.currentYear - 1;
			else if(rand<0.4) ym = this.currentYear - 2;
			else if(rand<0.6) ym = this.currentYear - 3;
			else if(rand<0.8) ym = this.currentYear - 4;
			else ym = this.currentYear - 5;
		} else if(rd<0.6875){
			double rand = random.nextDouble();
			if(rand<0.2) ym = this.currentYear - 6;
			else if(rand<0.4) ym = this.currentYear - 7;
			else if(rand<0.6) ym = this.currentYear - 8;
			else if(rand<0.8) ym = this.currentYear - 9;
			else ym = this.currentYear - 10;
		} else if(rd<0.875){
			double rand = random.nextDouble();
			if(rand<0.2) ym = this.currentYear - 11;
			else if(rand<0.4) ym = this.currentYear - 12;
			else if(rand<0.6) ym = this.currentYear - 13;
			else if(rand<0.8) ym = this.currentYear - 14;
			else ym = this.currentYear - 15;
		} else if(rd<0.9625){
			double rand = random.nextDouble();
			if(rand<0.2) ym = this.currentYear - 16;
			else if(rand<0.4) ym = this.currentYear - 17;
			else if(rand<0.6) ym = this.currentYear - 18;
			else if(rand<0.8) ym = this.currentYear - 19;
			else ym = this.currentYear - 20;
		} else {
			double rand = random.nextDouble();
			if(rand<0.1) ym = this.currentYear - 21;
			else if(rand<0.2) ym = this.currentYear - 22;
			else if(rand<0.3) ym = this.currentYear - 23;
			else if(rand<0.4) ym = this.currentYear - 24;
			else if(rand<0.5) ym = this.currentYear - 25;
			else if(rand<0.6) ym = this.currentYear - 26;
			else if(rand<0.7) ym = this.currentYear - 27;
			else if(rand<0.8) ym = this.currentYear - 28;
			else if(rand<0.9) ym = this.currentYear - 29;
			else ym = this.currentYear - 30;
		}
		return ym;
	}

	/**
	 * This method determines the drivetrain of a vehicle.
	 * Probabilities are taken from an initial distribution on the KBA website.
	 * <p>
	 * See <a href=http://www.kba.de/DE/Statistik/Fahrzeuge/Bestand/Umwelt/2016_b_umwelt_dusl.html>http://www.kba.de</a>
	 * 
	 * @return dt the drivetrain of a vehicle 
	 */
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