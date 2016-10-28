package org.fleet.types;


/**
 * @author benkick
 *
 */
public class Vehicle {
	private final Id<Vehicle> id;
	private final int ym;
	private final Drivetrain dt;
	
	public Vehicle(final Id<Vehicle> id, final int ym, final Drivetrain dt){
		this.id = id;
		this.ym = ym;
		this.dt = dt;
	}
	
	public Id<Vehicle> getId() {
		return this.id;
	}

	public Drivetrain getDt() {
		return this.dt;
	}

	public int getYm() {
		return ym;
	}

}
