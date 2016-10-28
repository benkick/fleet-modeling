package org.fleet.core;

import java.util.List;
import java.util.logging.Logger;

import org.fleet.types.Id;
import org.fleet.types.Vehicle;
import org.fleet.types.Vehicles;

/**
 * @author benkick
 *
 */
public class VehicleScrapping {
	private static final Logger log = Logger.getLogger(VehicleScrapping.class.getName());

	public void scrapVehicles(Vehicles vehicles, List<Id<Vehicle>> assignedVeh, int currentYear) {
		log.info("\n" 
				+ "Scrapping vehicles \n"
				+ "-------------------------------------------------");
		int scrappedCnt = 0;
		
		for(Vehicle veh : vehicles.getVehicles().values()){
			int vehAge = currentYear - veh.getYm();
			boolean scrap = checkForFailure(vehAge);
			if(scrap){
				//TODO: households etc.
				assignedVeh.remove(veh);
				scrappedCnt++;
			}
		}
		log.info("Scrapped vehicles: " + scrappedCnt);
	}

	private boolean checkForFailure(int vehAge) {
		boolean failure = false;
		if(vehAge<=5){
			//do nothing; minimum vehicle age is 5.
		} else if(vehAge<=20){
			//TODO: adjust
			failure = true;
		} else if(vehAge<=30){
			//TODO
		} else {
			//TODO
//			throw new RuntimeException("This case is not considered yet. Aborting...");
		}
		return failure;
	}
}
