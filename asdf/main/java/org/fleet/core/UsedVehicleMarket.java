package org.fleet.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.fleet.types.Drivetrain;
import org.fleet.types.Household;
import org.fleet.types.Households;
import org.fleet.types.Id;
import org.fleet.types.Vehicle;
import org.fleet.types.Vehicles;

/**
 * @author bolz_ma, benkick
 *
 */

public class UsedVehicleMarket {
	private static final Logger log = Logger.getLogger(UsedVehicleMarket.class.getName());
	
	private final Random random;
	
	public UsedVehicleMarket(Random random) {
		this.random = random;
	}

	void model(Vehicles vehicles, List<Id<Vehicle>> assignedVeh, Households households) {
		log.info("\n" 
				+ "Simulating used vehicle market \n"
				+ "*************************************************");
		Vehicles vehForSale = chooseVehiclesForSale(vehicles, assignedVeh);
		List<Id<Vehicle>> remainingVeh = new ArrayList<Id<Vehicle>>(vehForSale.getVehicles().keySet());
		Households sellingHHs = chooseSellingHHs(vehForSale, households);
		Households buyingHHs = chooseBuyingHHs(sellingHHs, households);
		clearUsedVehicleMarket(vehForSale, remainingVeh, sellingHHs, buyingHHs);
	}

	/**
	 * This method computes the second-hand vehicles that are sold this year using the method markVehForSale.
	 * @param assignedVeh 
	 * @param vehicles 
	 * 
	 * @see markVehForSale 
	 * @return the vehicles that should be sold
	 */
	private Vehicles chooseVehiclesForSale(Vehicles vehicles, List<Id<Vehicle>> assignedVeh){
		Vehicles vehForSale = new Vehicles();
		for(Id<Vehicle> vid : assignedVeh){
			Vehicle veh = vehicles.getVehicles().get(vid);
			if(markVehForSale(veh)){
				vehForSale.addVehicle(veh);
			}
		}
		log.info("Vehicles for sale on second-hand market: " + vehForSale.getVehicles().size());
		return vehForSale;
	}

	/**
	 * This method determines if the given vehicle will be sold or not, depending on the drivetrain of the vehicle.
	 * Probabilities are taken from the KBA website and then converted to the needed conditional probabilities.
	 * <p>
	 * See <a href= "http://www.kba.de/DE/Statistik/Fahrzeuge/Besitzumschreibungen/Umwelt/2015_u_umwelt_dusl.html?nn=664062">http://www.kba.de</a>
	 * 
	 * @param veh the vehicle that should be sold or not
	 * @return true or false
	 */
	private boolean markVehForSale(Vehicle veh){
		boolean markForSale = false;
		double rand = this.random.nextDouble();
	
		Drivetrain drivetrain = veh.getDt();
	
		if(drivetrain.equals(Drivetrain.GASOLINE)){
			if(rand < 0.1645) markForSale = true;
		}else if(drivetrain.equals(Drivetrain.DIESEL)){
			if(rand < 0.1665) markForSale = true;
		}else if(drivetrain.equals(Drivetrain.NATURAL_GAS)){
			if(rand < 0.1687) markForSale = true;
		}else if(drivetrain.equals(Drivetrain.HYBRID)){
			if(rand < 0.1361) markForSale = true;
		}else if(drivetrain.equals(Drivetrain.BEV)){
			if(rand < 0.1266) markForSale = true;
		}else{
			throw new RuntimeException("No transaction probability defined for drivetrain " + drivetrain + ". Aborting...");
		}
		return markForSale;
	}

	/**
	 * This method determines the households selling vehicles.
	 * 
	 * @param soldVehicles the vehicles that are sold
	 * @param households all households
	 * @return the households selling at least one vehicle
	 */
	private Households chooseSellingHHs(Vehicles soldVehicles, Households households){
		Households sellingHHs = new Households();
		for(Household  hh : households.getHouseholds().values()){
			boolean isSelling = false;
			for(Vehicle veh : soldVehicles.getVehicles().values()){
				if(hh.getVehInHH().getVehicles().get(veh.getId()) != null){
					isSelling = true;
				}
			}
			if(isSelling) sellingHHs.addHousehold(hh);
		}
		log.info("Number of households selling at least one vehicle: " + sellingHHs.getHouseholds().size());
		return sellingHHs;
	}

	//  TODO: The probability for a vehicle purchase is a bit distorted, because HHs selling more than one vehicle appear only once
	/*
	 * Assumption: 80% of the households selling at least(?) one vehicle will buy at least(?) one "new" second-hand vehicle;
	 * Implication (some probability theory):
	 * - 6.51 % of the households not selling at least(?) one vehicle, will buy at least(?) one "new" second-hand vehicle
	 */
	/**
	 * This method chooses the households which will buy used cars.
	 * <p>
	 * We assume that the probability to buy at least(?) one second-hand vehicle is 0.8 if the household is selling at least(?) one vehicle this year.
	 * The probability to buy at least(?) one vehicle if no vehicle was sold is much lower.
	 * 
	 * @param sellingHHs the households selling at least(?) one vehicle this year 
	 * @param households all households
	 * @return the households which want to buy at least(?) one vehicle this year
	 */
	private Households chooseBuyingHHs(Households sellingHHs, Households households){
		Households buyingHHs = new Households();
		for(Household hh : households.getHouseholds().values()){
			double rd = this.random.nextDouble();
			boolean isBuying = false;
			
			if (sellingHHs.getHouseholds().get(hh.getId()) != null){
				if(rd<0.8) isBuying = true;
			} else {
				if(rd<0.0651) isBuying = true;
			}
			if(isBuying) buyingHHs.addHousehold(hh);
		}
		log.info("Number of households buying (at least?) one second-hand vehicle: "+ buyingHHs.getHouseholds().size());
		return buyingHHs;
	}

	private void clearUsedVehicleMarket(Vehicles vehForSale, List<Id<Vehicle>> remainingVeh, Households sellingHHs, Households buyingHHs){
		removeVehFromHHs(sellingHHs, vehForSale);
		int nonSuccessfulTransactions = 0;
		for(Household hh : buyingHHs.getHouseholds().values()){
			int noOfSoldVeh = determineNoOfSoldVeh(hh, vehForSale);
			int noOfBoughtVeh = determineNoOfBoughtVeh(hh, noOfSoldVeh);
			boolean boughtCar = buyCar(hh, vehForSale, remainingVeh, noOfBoughtVeh);
			if(!boughtCar) nonSuccessfulTransactions++;
		}
		//TODO: this counter does not make sense since it potentially counts the same household twice;
		//It also does not fit to the counter under buyCar()
		log.info("Number of non-successful vehicle transactions: " + nonSuccessfulTransactions);
	}

	private void removeVehFromHHs(Households sellingHHs, Vehicles vehForSale) {
		for(Household hh : sellingHHs.getHouseholds().values()){
			List<Id<Vehicle>> vids = new ArrayList<>();
			for(Id<Vehicle> vid : hh.getVehInHH().getVehicles().keySet()){
				vids.add(vid);
			}
			
			for(Id<Vehicle> vid : vids){
				if(vehForSale.getVehicles().containsKey(vid)){
					hh.getVehInHH().removeVehicle(hh.getVehInHH().getVehicles().get(vid));
				}
			}
		}
	}

	/**
	 * This method determines the number of cars that have been sold by this household
	 * 
	 * @param hh the household that sold cars
	 * @param vehForSale the vehicles on the second hand market
	 * 
	 * @return the number of vehicles that have been sold by the given household
	 */
		private int determineNoOfSoldVeh(Household hh, Vehicles vehForSale){
			int noOfSoldVeh = 0;
			for(Id<Vehicle> vid : hh.getVehInHH().getVehicles().keySet()){
				if(vehForSale.getVehicles().get(vid) != null){
					noOfSoldVeh++;
				}
			}
			return noOfSoldVeh;
		}

	//TODO: which probabilities can be used to determine how many cars are bought
	/*assumption that the probability of buying as many cars as were sold is 50%
	 *buy one more than was sold is 25%
	 *buy one less than was sold is 25% 
	 *
	 *assumption that if the household didn't sell a car but was chosen to buy at least one, the probability to buy one car is 95%,
	 * the probability to buy to is 5%
	 */
		private int determineNoOfBoughtVeh (Household hh, int noOfSoldVeh){
			double rd = random.nextDouble();
			int noOfBoughtVeh = 0;
			if(noOfSoldVeh != 0){
				if(rd < 0.5){
					noOfBoughtVeh = noOfSoldVeh;
				}else if(rd < 0.75){
					noOfBoughtVeh = noOfSoldVeh - 1; //this could result in noOfBoughtVeh = 0, Benjamin Nov'16
				}
				else{
					noOfBoughtVeh = noOfSoldVeh + 1;
				}
			}else{
				if(rd<0.95){
					noOfBoughtVeh = 1;
				}else{
					noOfBoughtVeh = 2;
				}
			}
			return noOfBoughtVeh;
		}

		//TODO: chooseVeh method for choosing the car that is bought by this household	
		private boolean buyCar(Household hh, Vehicles vehForSale, List<Id<Vehicle>> remainingVeh, int noOfBoughtVeh){
			boolean boughtCar = false;
			for(int i=0; i<noOfBoughtVeh; i++){
				Vehicle veh = chooseVeh(remainingVeh, vehForSale);
				if(veh == null){	
//					log.info("All vehicles in the second hand market have already been assigned. Household "+ hh.getId() +" can't buy a second-hand vehicle.");
				}else{
					hh.getVehInHH().addVehicle(veh);
					boughtCar = true;
				}
			}
			return boughtCar;
		}

	private Vehicle chooseVeh(List<Id<Vehicle>> remainingVeh, Vehicles vehForSale){
		Vehicle chosenVeh = null;
		if(remainingVeh.size()>0){
			int rd = this.random.nextInt(remainingVeh.size());
			Id<Vehicle> chosenVehId = remainingVeh.get(rd);
			chosenVeh = vehForSale.getVehicles().get(chosenVehId);
			remainingVeh.remove(chosenVehId);
		}
		return chosenVeh;
	}
}
