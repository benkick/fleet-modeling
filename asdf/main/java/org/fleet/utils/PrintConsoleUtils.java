package org.fleet.utils;

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
	
	private final HouseholdUtils hhUtils;
	private final VehicleUtils vehUtils;
	
	public PrintConsoleUtils(){
		this.hhUtils = new HouseholdUtils();
		this.vehUtils = new VehicleUtils();
	}

	public void printVehicleInformation(Vehicles vehicles) {
		vehUtils.countDt2Vehicles(vehicles);
		for(Drivetrain dt : vehUtils.getDtCount().keySet()){
			log.info(dt + " vehicles: " + vehUtils.getDtCount().get(dt) + " of " + vehUtils.getTotalVehCnt() + " vehicles.");
		}
	}	
	
	public void printHouseholdInformation(Households households){
		hhUtils.countVehPerHH(households);
		for(Integer vehInHHClass : hhUtils.getNoOfVeh2NoOfHHCount().keySet()){
			log.info("Households with " + vehInHHClass + " vehicle(s): " + hhUtils.getNoOfVeh2NoOfHHCount().get(vehInHHClass) + " of " + households.getHouseholds().size() + " households.");
		}
		log.info("Vehicles assigned to households: " + hhUtils.getTotalVehInAllHHCnt());
	}
	
	public void printMarketMissmatchInformation(Households households, Vehicles vehicles){
		if(vehUtils.getTotalVehCnt() == null || hhUtils.getTotalVehInAllHHCnt() == null){
			log.warning("Vehicles and/or vehicles in households were not counted yet. \n"
					+ "No market mismatch can be calculated.");
		} else {
			int marketMismatchCnt = vehUtils.getTotalVehCnt() - hhUtils.getTotalVehInAllHHCnt();
			log.info("Vehicles not assigned to households: " + marketMismatchCnt); //is always >= 0 because otherwise some HH would not have gotten enough vehicles
		}
	}
}
