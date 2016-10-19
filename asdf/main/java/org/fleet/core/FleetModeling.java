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
	private final int noOfHH = 10;
	private final int noOfVeh = 10;
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
	
	public void preprocess() {
		generateInitialVehicles();
		generateInitialHouseholds();
		writeInitialInformation();
	}

	public void run() {
//		scrapeVehicles();
//		generateNewVehicles();
	}

	public void postprocess() {
//		nothing so far
	}
	
	private void writeInitialInformation() {
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
			assignVehicles2HH(hh);
			this.households.addHousehold(hh);
		}
		log.info("Leaving generateInitialHouseholds");
	}

	private void assignVehicles2HH(Household hh) {
		Integer noOfVehInHH = determineNoOfVehInHH();
		for(int vehCnt=0; vehCnt<noOfVehInHH; vehCnt++){ //avoids adding a vehicle for noOfVehInHH = 0
			Vehicle veh = chooseVehicle();
//			log.info("Veh " + veh);
			hh.getVehInHH().addVehicle(veh);
		}
	}

	//TODO: it might happen that there are more/less vehicles than needed...
	//TODO: very ugly code...
	private Vehicle chooseVehicle() {
		log.info("Size " + this.vehicles.getVehicles().size());
		int rd = random.nextInt(this.vehicles.getVehicles().size());
		log.info("Random " + rd);
		
		while(this.vehicles.getVehicles().get(Id.createVehicleId(rd)) == null && this.vehicles.getVehicles().size()>0){
			rd = random.nextInt(this.vehicles.getVehicles().size());
//			log.info("RandomX " + rd);
		}
		
		Vehicle veh = this.vehicles.getVehicles().get(Id.createVehicleId(rd));
		log.info("Veh " + veh);
		this.vehicles.getVehicles().remove(Id.createVehicleId(rd));
		return veh;
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