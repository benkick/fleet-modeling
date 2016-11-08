package org.fleet.types;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author benkick
 *
 */
public class Households {
	
	private SortedMap<Id<Household>, Household> households;
	private Vehicles vehiclesOfHHs;
	
	public Households(){
		this.households = new TreeMap<Id<Household>, Household>();
		this.vehiclesOfHHs = null;
	}

	public SortedMap<Id<Household>, Household> getHouseholds() {
		return this.households;
	}

	public Vehicles getVehiclesOfHHs(){
		this.vehiclesOfHHs = new Vehicles();
		for(Household hh : this.households.values()){
			for(Vehicle veh : hh.getVehInHH().getVehicles().values()){
				this.vehiclesOfHHs.addVehicle(veh);
			}
		}
		return this.vehiclesOfHHs;
	}

	public void addHousehold(Household household) {
		if(this.households.get(household.getId()) == null){
			this.households.put(household.getId(), household);
		}
		else{
			throw new RuntimeException("Household " + household.getId() + " already exists. Aborting...");
		}
	}
}