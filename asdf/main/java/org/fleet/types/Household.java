package org.fleet.types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benkick
 *
 */
public class Household {
	private final Id<Household> id;
	
//	private Map<Id<Vehicle>, Vehicle> vehInHH;
	private Vehicles vehInHH;
	
	public Household(final Id<Household> id){
		this.id = id;
		this.vehInHH = new Vehicles();
	}
	
	public Id<Household> getId() {
		return this.id;
	}

	public Vehicles getVehInHH() {
		return vehInHH;
	}
}