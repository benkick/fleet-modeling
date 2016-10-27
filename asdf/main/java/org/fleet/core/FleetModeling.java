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


/**
 * @author benkick
 *
 */
public class FleetModeling {
	private static final Logger log = Logger.getLogger(FleetModeling.class.getName());
	
	private static final Random random = new Random(1331);
	private static final int noOfHH = 10000;
	private static final int noOfVeh = 12000;
	private int gasolineCounter = 0;
	private int dieselCounter = 0;
	private int gasCounter = 0;
	private int hybridCounter = 0;
	private int bevCounter = 0;
	
	private Vehicles vehicles;
	private List<Id<Vehicle>> assignedVeh;
	private List<Id<Vehicle>> leftVeh;
	private Households households;
	//TODO: How to treat company-owned, but (mainly) privately-used vehicles?
	//private Companies companies;
	
	public FleetModeling(){
		this.vehicles = new Vehicles();
		this.assignedVeh = new ArrayList<Id<Vehicle>>();
		this.leftVeh = new ArrayList<Id<Vehicle>>();
		this.households = new Households();
	}
	
	public void preprocess() {
		generateInitialVehicles();
		generateInitialHouseholds();
		writeInitialInformation();
		writeSecondHandCarMarket();
	}

	public void run() {
//		scrapeVehicles();
		//TODO: The following could be influenced by many factors, e.g. how many vehicles die
//		generateNewVehicles();
//		assignNewVehicles2HH();
//		modelSecondHandCarMarket();
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
		
		
		int zeroVehCnt = 0;
		int oneVehCnt = 0;
		int twoVehCnt = 0;
		int threeVehCnt = 0;
		int fourVehCnt = 0;
		int totalVehInHHCnt = 0;
		for(Household hh : households.getHouseholds().values()){
			int vehInHH = hh.getVehInHH().getVehicles().size();
			totalVehInHHCnt += vehInHH;
			if(vehInHH==0) zeroVehCnt++;
			else if(vehInHH==1) oneVehCnt++;
			else if(vehInHH==2) twoVehCnt++;
			else if(vehInHH==3) threeVehCnt++;
			else if(vehInHH==4) fourVehCnt++;
			else throw new RuntimeException("This should not happen.");
		}
		log.info("Households with zero vehicles: " + zeroVehCnt + " of " + households.getHouseholds().size() + " households.");
		log.info("Households with one vehicles: " + oneVehCnt + " of " + households.getHouseholds().size() + " households.");
		log.info("Households with two vehicles: " + twoVehCnt + " of " + households.getHouseholds().size() + " households.");
		log.info("Households with three vehicles: " + threeVehCnt + " of " + households.getHouseholds().size() + " households.");
		log.info("Households with four vehicles: " + fourVehCnt + " of " + households.getHouseholds().size() + " households.");
		
		log.info("In total, " + totalVehInHHCnt + " vehicles are assigned to households.");
		log.info("Check sum: " + this.assignedVeh.size());
		log.info("Check sum left Vehicles: " + this.leftVeh.size());
		
		int totalVehCnt = gasolineCounter + dieselCounter + gasCounter + hybridCounter + bevCounter;
		int marketMismatchCnt = totalVehCnt - totalVehInHHCnt;
		log.info("In total, " + marketMismatchCnt + " are left on the market."); //is always >= 0 because otherwise the HH did not get enough vehicles
	}

	private void generateInitialVehicles() {
		log.info("Entering generateInitialVehicles");
		for(int i=1;i<=noOfVeh;i++){
			Id<Vehicle> vid = Id.createVehicleId(i);
			Drivetrain dt = determineDrivetrain();
			Vehicle veh = new Vehicle(vid, dt);
			this.vehicles.addVehicle(veh);
			this.leftVeh.add(veh.getId());
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