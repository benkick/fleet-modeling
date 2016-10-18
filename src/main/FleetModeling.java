package main;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import types.Drivetrain;
import types.Household;
import types.Households;
import types.Id;
import types.Vehicle;
import types.Vehicles;


/**
 * @author benkick
 *
 */
public class FleetModeling {
//	private static final Logger log = new Logger();
	
	private final int noOfHH = 1000;
	private final int noOfVeh = 1000;
	
	private Vehicles vehicles;
	private Households households;
	//TODO: How to treat company-owned, but (mainly) privately-used vehicles?
	//private Companies companies;
	
	public FleetModeling(){
		this.vehicles = new Vehicles();
		this.households = new Households();
	}
	
	public void run() {
//		scrapeVehicles();
//		generateNewVehicles();
	}

	public void initialize() {
		generateInitialVehicles();
		generateInitialHouseholds();
	}

	private void generateInitialVehicles() {
		for(int i=1;i<=noOfVeh;i++){
			Id<Vehicle> vid = Id.createVehicleId(i);
			Drivetrain dt = determineDrivetrain();
			Vehicle veh = new Vehicle(vid, dt);
			this.vehicles.addVehicle(veh);
		}
	}

	private void generateInitialHouseholds() {
		for(int i=1;i<=noOfHH;i++){
			Id<Household> hid = Id.createHouseholdId(i);
			Household hh = new Household(hid);
			this.households.addHousehold(hh);
		}
	}

	private Drivetrain determineDrivetrain() {
		Drivetrain dt;
		double rd = ThreadLocalRandom.current().nextDouble();
		if(rd<0.662) dt = Drivetrain.GASOLINE;
		else if (rd<0.984) dt = Drivetrain.DIESEL;
		else if (rd<0.9965) dt = Drivetrain.NATURAL_GAS;
		else if (rd<0.9994) dt = Drivetrain.HYBRID;
		else dt = Drivetrain.BEV;
		//TODO: PHEV not considered!
		//TODO: Fuel cell not considered!
		return dt;
	}
}