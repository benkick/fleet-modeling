package org.fleet.run;

import org.fleet.core.FleetModeling;

/**
 * @author benkick
 *
 */
public class RunFleetModeling {

	public static void main(String[] args) {
		FleetModeling fleetModeling = new FleetModeling();
		fleetModeling.preprocess();
		fleetModeling.run();
		fleetModeling.postprocess();
	}
}