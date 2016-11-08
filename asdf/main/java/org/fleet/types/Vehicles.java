package org.fleet.types;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author benkick
 *
 */
public class Vehicles {
	
	private SortedMap<Id<Vehicle>, Vehicle> vehicles;
	
	public Vehicles(){
		this.vehicles = new TreeMap<Id<Vehicle>, Vehicle>();
	}

	public SortedMap<Id<Vehicle>, Vehicle> getVehicles() {
		return this.vehicles;
	}

	public void addVehicle(Vehicle vehicle) {
		if(this.vehicles.get(vehicle.getId()) == null){
			this.vehicles.put(vehicle.getId(), vehicle);
		}
		else{
			throw new RuntimeException("Vehicle " + vehicle.getId() + " already exists. Aborting...");
		}
	}
	
	public Household removeVehicle(Vehicle vehicle){
		Household hh = null;
		if(this.vehicles.get(vehicle.getId()) != null){
			if(vehicle.hh !=null){
				hh = vehicle.hh;
				vehicle.hh.removeVehfromHH(vehicle);
			}
			this.vehicles.remove(vehicle.getId());
		}else{
			throw new RuntimeException("Vehicle " + vehicle.getId() + " does not exist. Aborting...");
		}
		return hh;
	}
}