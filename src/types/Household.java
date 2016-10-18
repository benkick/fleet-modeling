package types;

/**
 * @author benkick
 *
 */
public class Household {
	private final Id<Household> id;
	
	public Household(final Id<Household> id){
		this.id = id;
	}
	
	public Id<Household> getId() {
		return this.id;
	}
}
