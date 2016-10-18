package types;


/**
 * @author benkick
 *
 */
public class Vehicle {
	private final Id<Vehicle> id;
	private final Drivetrain dt;
	
	public Vehicle(final Id<Vehicle> id, final Drivetrain dt){
		this.id = id;
		this.dt = dt;
	}
	
	public Id<Vehicle> getId() {
		return this.id;
	}

	public Drivetrain getDt() {
		return this.dt;
	}

}
