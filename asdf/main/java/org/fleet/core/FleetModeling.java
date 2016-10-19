package org.fleet.core;

import java.util.Random;
import java.util.logging.Logger;

import org.fleet.types.Drivetrain;
import org.fleet.types.Household;
import org.fleet.types.Households;
import org.fleet.types.Id;
import org.fleet.types.Vehicle;
import org.fleet.types.Vehicles;


/**
 * @author benkick
 *
 */
public class FleetModeling {
	private static final Logger log = Logger.getLogger(FleetModeling.class.getName());
	
	private final Random random = new Random(1331);
	private final int noOfHH = 45000;
	private final int noOfVeh = 45000;
	private int gasolineCounter = 0;
	private int dieselCounter = 0;
	private int gasCounter = 0;
	private int hybridCounter = 0;
	private int bevCounter = 0;
	
	private Vehicles vehicles;
	private Households households;
	//TODO: How to treat company-owned, but (mainly) privately-used vehicles?
	//private Companies companies;
	
	public FleetModeling(){
		this.vehicles = new Vehicles();
		this.households = new Households();
	}
	
	public void initialize() {
		generateInitialVehicles();
		generateInitialHouseholds();
	}

	public void run() {
//		scrapeVehicles();
//		generateNewVehicles();
	}

	public void postprocess() {
		writeInformation();
	}
	
	private void writeInformation() {
		log.info("Created " + Drivetrain.GASOLINE + " vehicles: " + gasolineCounter);
		log.info("Created " + Drivetrain.DIESEL + " vehicles: " + dieselCounter);
		log.info("Created " + Drivetrain.NATURAL_GAS + " vehicles: " + gasCounter);
		log.info("Created " + Drivetrain.HYBRID + " vehicles: " + hybridCounter);
		log.info("Created " + Drivetrain.BEV + " vehicles: " + bevCounter);
	}

	private void generateInitialVehicles() {
		log.info("Entering generateInitialVehicles");
		for(int i=1;i<=noOfVeh;i++){
			Id<Vehicle> vid = Id.createVehicleId(i);
			Drivetrain dt = determineDrivetrain();
			Vehicle veh = new Vehicle(vid, dt);
			this.vehicles.addVehicle(veh);
		}
		log.info("Leaving generateInitialVehicles");
	}

	private void generateInitialHouseholds() {
		log.info("Entering generateInitialHouseholds");
		for(int i=1;i<=noOfHH;i++){
			Id<Household> hid = Id.createHouseholdId(i);
			Household hh = new Household(hid);
			this.households.addHousehold(hh);
		}
		log.info("Leaving generateInitialHouseholds");
	}

	private Drivetrain determineDrivetrain() {
		Drivetrain dt;
		double rd = random.nextDouble();
//		log.info("Random number: " + rd);
		if(rd<0.662){
			dt = Drivetrain.GASOLINE;
			gasolineCounter++;
		} else if(rd<0.984){
			dt = Drivetrain.DIESEL;
			dieselCounter++;
		} else if(rd<0.9965){
			dt = Drivetrain.NATURAL_GAS;
			gasCounter++;
		} else if(rd<0.9994){
			dt = Drivetrain.HYBRID;
			hybridCounter++;
		} else{
			//TODO: PHEV and FUEL_CELL not considered!
			dt = Drivetrain.BEV;
			bevCounter++;
		}
		return dt;
	}
}