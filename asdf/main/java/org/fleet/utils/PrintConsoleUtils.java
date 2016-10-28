package org.fleet.utils;

import java.util.SortedMap;
import java.util.logging.Logger;

import org.fleet.types.Drivetrain;
import org.fleet.types.Households;
import org.fleet.types.Vehicles;

/**
 * @author benkick
 *
 */
public class PrintConsoleUtils {
	private static final Logger log = Logger.getLogger(PrintConsoleUtils.class.getName());
	
	public void printVehicleInformation(Vehicles vehicles, int currentYear) {
		VehicleUtils vehUtils = new VehicleUtils();
		SortedMap<Drivetrain, Integer> dtCnt = vehUtils.getDt2Vehicles(vehicles);
		for(Drivetrain dt : dtCnt.keySet()){
			log.info(dt + " vehicles: " + dtCnt.get(dt) + " of " + vehicles.getVehicles().size() + " vehicles.");
		}
		double avgAge = vehUtils.getAvgVehilceAge(vehicles, currentYear);
		log.info("The average vehicle age is: " + avgAge + " years.");
	}	
	
	public void printHouseholdInformation(Households households){
		HouseholdUtils hhUtils = new HouseholdUtils();
		hhUtils.countVehPerHH(households);
		for(Integer vehInHHClass : hhUtils.getNoOfVeh2NoOfHHCount().keySet()){
			log.info("Households with " + vehInHHClass + " vehicle(s): " + hhUtils.getNoOfVeh2NoOfHHCount().get(vehInHHClass) + " of " + households.getHouseholds().size() + " households.");
		}
		log.info("Vehicles assigned to households: " + hhUtils.getTotalVehInAllHHCnt());
	}
	
	public void printMarketMissmatchInformation(Households households, Vehicles vehicles){
		HouseholdUtils hhUtils = new HouseholdUtils();
		hhUtils.countVehPerHH(households);
		if(hhUtils.getTotalVehInAllHHCnt() == null){
			log.warning("Vehicles in households were not counted yet. \n"
					+ "No market mismatch can be calculated.");
		} else {
			int marketMismatchCnt = vehicles.getVehicles().size() - hhUtils.getTotalVehInAllHHCnt();
			log.info("Vehicles not assigned to households: " + marketMismatchCnt); //is always >= 0 because otherwise some HH would not have gotten enough vehicles
		}
	}
}
