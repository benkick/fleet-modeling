package org.fleet.types;

/**
 * @author benkick
 *
 */
public class Household {
	private final Id<Household> id;
	
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