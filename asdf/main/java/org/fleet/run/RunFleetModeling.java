package org.fleet.run;

import java.util.Random;

import org.fleet.core.FleetModeling;

/**
 * @author benkick
 *
 */
public class RunFleetModeling {
	
	private final static Random random = new Random(1331);
	private final static int noOfVeh = 1200;
	private final static int noOfHH = 1000;
	private final static int baseYear = 2016;
	private final static int noOfIterations = 10;

	public static void main(String[] args) {
		FleetModeling fleetModeling = new FleetModeling(
				random,
				noOfVeh,
				noOfHH,
				baseYear
				);
		fleetModeling.preprocess();
		fleetModeling.run(noOfIterations);
		fleetModeling.postprocess();
	}
}