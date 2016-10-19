package org.fleet.core;

/**
 * @author benkick
 *
 */
public class RunFleetModeling {

	public static void main(String[] args) {
		FleetModeling fleetModeling = new FleetModeling();
		fleetModeling.initialize();
		fleetModeling.run();
		fleetModeling.postprocess();
	}

}
