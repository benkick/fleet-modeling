package org.fleet.core;

import java.util.List;
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
public class NewVehicleMarket {
	private static final Logger log = Logger.getLogger(NewVehicleMarket.class.getName());

	public void model(Households households,
			List<Id<Household>> affectedHouseholds,
			Vehicles vehicles,
			List<Id<Vehicle>> assignedVeh) {
		log.info("\n" 
				+ "Simulating new vehicle market \n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++++++");
		
		log.info("Number of households with at least one scrapped vehicle: " + affectedHouseholds.size() + "\n"
				+ "They will replace the vehicles by new ones.");
		
		Vehicles newVehicles = generateNewVehicles();
		
	}

	private Vehicles generateNewVehicles() {
		// TODO Auto-generated method stub
		return null;
	}

}
