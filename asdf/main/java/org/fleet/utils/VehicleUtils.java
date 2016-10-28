package org.fleet.utils;

import java.util.SortedMap;
import java.util.TreeMap;

import org.fleet.types.Drivetrain;
import org.fleet.types.Vehicle;
import org.fleet.types.Vehicles;

/**
 * @author benkick
 *
 */
public class VehicleUtils {
	
	public SortedMap<Drivetrain, Integer> getDt2Vehicles(Vehicles vehicles) {
		SortedMap<Drivetrain, Integer> dtCnt = new TreeMap<>();
		for(Drivetrain drive : Drivetrain.values()){ //populate dtCnt with all dt, so no dt is forgotten
			dtCnt.put(drive, 0);
		}
		for(Vehicle veh : vehicles.getVehicles().values()){
			Drivetrain dt = veh.getDt();
			int cntSoFar = dtCnt.get(dt);
			int cntNew = cntSoFar+1;
			dtCnt.put(dt, cntNew);
		}
		return dtCnt;
	}

	public double getAvgVehilceAge(Vehicles vehicles, int currentYear) {
		Double avgVehAge = null;
		int vehCnt = 0;
		int totalVehAge = 0;
		for(Vehicle veh : vehicles.getVehicles().values()){
			vehCnt++;
			int vehAge = currentYear - veh.getYm();
			totalVehAge += vehAge;
			
		}
		avgVehAge = (double) totalVehAge / (double) vehCnt;
		return avgVehAge;
	}
}