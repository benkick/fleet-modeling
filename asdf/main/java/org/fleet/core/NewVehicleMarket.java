package org.fleet.core;

import java.util.List;
import java.util.logging.Logger;

import org.fleet.types.Household;
import org.fleet.types.Id;

/**
 * @author benkick
 *
 */
public class NewVehicleMarket {
	private static final Logger log = Logger.getLogger(NewVehicleMarket.class.getName());

	public void model(List<Id<Household>> affectedHouseholds) {
		log.info("\n" 
				+ "Simulating new vehicle market \n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++++++");
		
		log.info("Number of households with at least one scrapped car: " + affectedHouseholds.size());
	}

}
