package org.fleet.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.fleet.types.Household;
import org.fleet.types.Households;
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

	public void scrapVehicles(Vehicles vehicles, List<Id<Vehicle>> assignedVeh, Households households, int currentYear) {
		log.info("\n" 
				+ "Scrapping vehicles \n"
				+ "-------------------------------------------------");
		List<Id<Vehicle>> scrappedVeh = new ArrayList<>();
		
		for(Vehicle veh : vehicles.getVehicles().values()){
			int vehAge = currentYear - veh.getYm();
			boolean scrap = checkForFailure(vehAge);
			if(scrap){
				scrappedVeh.add(veh.getId());
			}
		}
		//TODO: Is there a more elegant way to update vehicles simultaneously everywhere?
		removeFromAssignedVehicles(assignedVeh, scrappedVeh);
		removeFromVeh(vehicles, scrappedVeh);
		removeFromHHs(households, scrappedVeh);
		log.info("Scrapped vehicles: " + scrappedVeh.size());
	}

	//TODO: Related to above: is there a more elegant way to remove vehicles from households?
	private void removeFromHHs(Households households, List<Id<Vehicle>> scrappedVeh) {
		for(Household hh : households.getHouseholds().values()){
			List<Id<Vehicle>> vids = new ArrayList<>();
			for(Id<Vehicle> vid : hh.getVehInHH().getVehicles().keySet()){
				vids.add(vid);
			}
			
			for(Id<Vehicle> vid : vids){
				if(scrappedVeh.contains(vid)){
					hh.getVehInHH().removeVehicle(hh.getVehInHH().getVehicles().get(vid));
				}
			}
		}
	}

	private void removeFromVeh(Vehicles vehicles, List<Id<Vehicle>> scrappedVeh) {
		for(Id<Vehicle> vid : scrappedVeh){
			vehicles.removeVehicle(vehicles.getVehicles().get(vid));
		}
	}

	private void removeFromAssignedVehicles(List<Id<Vehicle>> assignedVeh, List<Id<Vehicle>> scrappedVeh) {
		for(Id<Vehicle> vid : scrappedVeh){
			assignedVeh.remove(vid);
		}
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
		/* @Marie: TODO: The following is wrongly implemented.
		 * 
		 * Drawing EVERY YEAR with the respective survival probabilities results
		 * in too low survival rates for the joint distribution of cars
		 * that are still on the road after e.g. x years.
		 * 
		 * (Should be 80% after 10 years and 5% after 20 years, but is with this code 52% after 10 years).
		 * 
		 * --> Check theory on survival/hazard function for REPEATED DRAWS!
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
//		if(failure) System.out.println(vehAge + "; " +  rd + "; " + survivalProb);
		return failure;
	}
}
