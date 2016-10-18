package types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benkick
 *
 */
public class Households {
	
	private Map<Id<Household>, Household> households;
	
	public Households(){
		households = new HashMap<Id<Household>, Household>();
	}

	public Map<Id<Household>, Household> getHouseholds() {
		return households;
	}

	public void addHousehold(Household household) {
		if(!households.containsKey(household.getId())){
			households.put(household.getId(), household);
		}
		else{
			throw new RuntimeException("Household " + household.getId() + " already exists. Aborting...");
		}
	}
}