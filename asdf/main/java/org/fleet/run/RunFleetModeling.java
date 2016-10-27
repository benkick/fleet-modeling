package org.fleet.run;

import java.util.Random;

import org.fleet.core.FleetModeling;

/**
 * @author benkick
 *
 */
public class RunFleetModeling {
	
	private final static Random random = new Random(1331);
	private final static int noOfVeh = 12000;
	private final static int noOfHH = 10000;

	public static void main(String[] args) {
		FleetModeling fleetModeling = new FleetModeling(
				random,
				noOfVeh,
				noOfHH
				);
		fleetModeling.preprocess();
		fleetModeling.run();
		fleetModeling.postprocess();
	}
}