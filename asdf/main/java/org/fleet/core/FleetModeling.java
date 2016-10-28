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
	
	private final Vehicles vehicles;
	private final List<Id<Vehicle>> assignedVeh;
	private final List<Id<Vehicle>> remainingVeh;
	private final Households households;
	//TODO: How to treat company-owned, but (mainly) privately-used vehicles?
	//private Companies companies;
	
	private final PrintConsoleUtils printUtils;

	public FleetModeling(Random random, int noofveh, int noofhh, int baseyear){
		this.random = random;
		this.noOfVeh = noofveh;
		this.noOfHH = noofhh;
		this.currentYear = baseyear;
		
		this.vehicles = new Vehicles();
		//TODO: is assignedVeh really needed?
		this.assignedVeh = new ArrayList<Id<Vehicle>>();
		this.remainingVeh = new ArrayList<Id<Vehicle>>();
		this.households = new Households();
		
		this.printUtils = new PrintConsoleUtils();
	}
	
	public void preprocess() {
		log.info("Entering preprocessing...");
		generateInitialVehicles();
		generateInitialHouseholds();
		log.info("Leaving preprocessing...");
	}

	public void run(int iterations) {
		log.info("Entering simulation...");
		VehicleScrapage scr = new VehicleScrapage();
		PrimaryMarket pcm = new PrimaryMarket();
		SecondhandMarket scm = new SecondhandMarket(this.random);
		for(int i=0; i<iterations; i++){
			log.info("\n=================================================\n"
					+ "Simulating transactions for year " + this.currentYear + "\n"
					+ "=================================================");
			//TODO: 
			scr.scrapeVehicles();
			//TODO: Benjamin
			pcm.model();
			//TODO: Marie
			scm.model(this.households, this.vehicles, this.assignedVeh);
			this.currentYear += 1;
		}
		log.info("Leaving simulation...");
	}

	public void postprocess() {
		log.info("Entering postprocessing...");
//		nothing so far
		log.info("Leaving postprocessing...");
		log.info("Shutting down.");
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
		this.printUtils.printVehicleInformation(this.vehicles);
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
	 * This method determines the drivetrain of a vehicle.
	 * Probabilities are taken from an initial distribution on the KBA website.
	 * <p>
	 * See <a http://www.kba.de/DE/Statistik/Fahrzeuge/Bestand/Umwelt/2016_b_umwelt_dusl.html>http://www.kba.de</a>
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