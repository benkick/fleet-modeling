package org.fleet.utils;

import java.util.HashMap;
import java.util.Map;

import org.fleet.types.Household;
import org.fleet.types.Households;

/**
 * @author benkick
 *
 */
public class HouseholdUtils {
	Map<Integer, Integer> noOfVeh2NoOfHHCnt;
	int totalVehInAllHHCnt;
	
	public HouseholdUtils(){
		this.noOfVeh2NoOfHHCnt = new HashMap<>();
		this.totalVehInAllHHCnt = 0;
	}

	public void countVehPerHH(Households households){
		for(Household hh : households.getHouseholds().values()){
			int vehInHH = hh.getVehInHH().getVehicles().size();
			totalVehInAllHHCnt += vehInHH;
			if(!noOfVeh2NoOfHHCnt.containsKey(vehInHH)){
				noOfVeh2NoOfHHCnt.put(vehInHH, 1);
			} else {
				int cntSoFar = noOfVeh2NoOfHHCnt.get(vehInHH);
				int cntNew = cntSoFar+1;
				noOfVeh2NoOfHHCnt.put(vehInHH, cntNew);
			}
		}
	}

	public Map<Integer, Integer> getNoOfVeh2NoOfHHCount() {
		return noOfVeh2NoOfHHCnt;
	}

	public int getTotalVehInAllHHCnt() {
		return totalVehInAllHHCnt;
	}
}
