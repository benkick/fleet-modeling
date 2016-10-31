package org.fleet.core;

import java.util.List;
import java.util.Random;
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
	
	private final Random random;

	public VehicleScrapping(Random random) {
		this.random = random;
	}

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
				assignedVeh.remove(veh.getId());
				vehicles.getVehicles().remove(veh);
				scrappedCnt++;
			}
		}
		log.info("Scrapped vehicles: " + scrappedCnt);
	}

	/**
	 * This method flags a vehicle for scrapping depending on
	 * the vehicle age and assumed survival probabilities.
	 * <p>
	 * See <a href= "https://www.researchgate.net/publication/46463264_Schatzung_der_Wirkung_umweltpolitischer_Massnahmen_im_Verkehrssektor_unter_Nutzung_der_Datenbasis_der_Gesamtrechnung_des_Statistischen_Bundesamtes">http://www.researchgate.net</a>
	 * 
	 * @param vehAge the age of a vehicle
	 * @return failure or not
	 */
	private boolean checkForFailure(int vehAge) {
		boolean failure = false;
		double rd = random.nextDouble();
		/* TODO: I am not sure whether drawing EVERY YEAR with the respective probabilities 
		 * actually results in the correct joint distribution of cars that are still
		 * on the road from a certain manufacturing year. Check REPEATED DRAWS!
		 */
		double survivalProb = 1.;
		if(vehAge<=5){
			//do nothing; minimum vehicle age is 5.
		} else if(vehAge<=20){
			survivalProb = -0.06 * vehAge + 1.3;
		} else if(vehAge<=30){
			survivalProb = -0.01 * vehAge + 0.3;
		} else {
			//TODO: How to deal with older cars?
			throw new RuntimeException("This case is not considered yet. Aborting...");
		}
		if(rd>survivalProb) failure = true;
		if(failure == true) System.out.println(vehAge + "; " +  rd + "; " + survivalProb);
		return failure;
	}
}
