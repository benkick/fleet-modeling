package org.fleet.types;

/**
 * @author benkick
 *
 */
public class Household {
	private final Id<Household> id;
	private final Vehicles vehInHH;
	
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
	
	public void addVehToHH(Vehicle veh) {
		this.vehInHH.addVehicle(veh);
		veh.linkHHToVeh(this);
	}
	
	public boolean removeVehfromHH(Vehicle veh) {
		return this.vehInHH.getVehicles().remove(veh.getId()) !=null;
	}
}