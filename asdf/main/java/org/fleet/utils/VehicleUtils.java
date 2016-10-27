package org.fleet.utils;

import java.util.Map;
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
	SortedMap<Drivetrain, Integer> dtCnt;
	int totalVehCnt;

	public VehicleUtils(){
		this.dtCnt = new TreeMap<>();
		for(Drivetrain drive : Drivetrain.values()){ //populate dtCnt with all dt, so no dt is forgotten
			this.dtCnt.put(drive, 0);
		}
		this.totalVehCnt = 0;
	}
	
	public void countDt2Vehicles(Vehicles vehicles) {
		for(Vehicle veh : vehicles.getVehicles().values()){
			totalVehCnt++;
			Drivetrain dt = veh.getDt();
			int cntSoFar = dtCnt.get(dt);
			int cntNew = cntSoFar+1;
			dtCnt.put(dt, cntNew);
		}
	}

	public Map<Drivetrain, Integer> getDtCount() {
		return dtCnt;
	}

	public int getTotalVehCnt() {
		return totalVehCnt;
	}
}