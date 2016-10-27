package org.fleet.types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benkick
 *
 */
public class Vehicles {
	
	private Map<Id<Vehicle>, Vehicle> vehicles;
	
	public Vehicles(){
		vehicles = new HashMap<Id<Vehicle>, Vehicle>();
	}

	public Map<Id<Vehicle>, Vehicle> getVehicles() {
		return vehicles;
	}

	public void addVehicle(Vehicle vehicle) {
		if(!vehicles.containsKey(vehicle.getId())){
			vehicles.put(vehicle.getId(), vehicle);
		}
		else{
			throw new RuntimeException("Vehicle " + vehicle.getId() + " already exists. Aborting...");
		}
	}
	public void removeVehicle(Vehicle vehicle){
		if(vehicles.containsKey(vehicle.getId())){
			vehicles.remove(vehicle.getId());
		}else{
			throw new RuntimeException("Vehicle" + vehicle.getId() + "does not exist in this household. Aborting...");
		}
	}
}