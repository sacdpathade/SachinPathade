package com.denver.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.denver.constant.DenverConstant;
import com.denver.model.Bag;
import com.denver.model.Departure;


/**
 * Used to store information collected from input file such as bags and departures
 * Used enum to create only one instance.
 * 
 * @author SACHIN PATHADE
 */
public enum DepartureAndBagInputContainer {
	CONTAINER;

	private final static Logger logger = Logger.getLogger(DepartureAndBagInputContainer.class);

	/*List of bags*/
	final private List<Bag> bagList;

	/* Map of flight ID <-> Departure information*/
	final private Map<String, Departure> departureMap;


	private DepartureAndBagInputContainer() {
		departureMap = new HashMap<String, Departure>();
		bagList = new ArrayList<Bag>();
	}

	/**
	 * Add bag to bag list if its not already added.
	 * @param bag
	 */
	public void addBag(Bag bag) {
		logger.debug("Adding bag  " + bag + " to container");
		if (!bagList.contains(bag)) {
			bagList.add(bag);
		}
	}

	/**
	 * Add departure to departure map with FlightID as key.
	 * @param departure
	 */
	public void addDeparture(Departure departure) {
		logger.debug("Adding departure  " + departure + " to container");
		departureMap.put(departure.getFlightId(), departure);
	}

	/**
	 * Clear departure map and bagList at a time if required.
	 */
	public void clear() {
		departureMap.clear();
		bagList.clear();
	}

	/**
	 * Retrieve bag destination by given bag.
	 *
	 * It will use FlightID from bag object to identify departure information which contains gate number
	 * where bag needs to travelled.
	 *
	 * In case if Flight ID is 'ARRIVAL', that means flight just arrived, so it needs to go to BaggageClaim
	 * section by default.
	 *
	 * @param bag : Bag for which destination needs to be identified.
	 * @return : Destination conveyor node where bag needs to be travelled.
	 */
	public String getBagDestination(Bag bag) {
		logger.debug("Retrieving bag destination");
		if (bag.getFlightId().equals(DenverConstant.ARRIVAL)) {
			if(ConveyorSystemGraph.GRAPH.getConveyorNodeSet().contains(DenverConstant.BAGGAGECLAIM) ) {
				logger.info("Returning baggage claim as bag destination");
				return DenverConstant.BAGGAGECLAIM;
			}
		} else {
			Departure departure = departureMap.get(bag.getFlightId());
			if (departure != null) {
				String flightGate = departure.getFlightGate();
				logger.debug("Returning " + flightGate + " as bag destination");
				return flightGate;
			}
		}
		return null;
	}

	/**
	 * Retrieve bag list
	 */
	public List<Bag> getBagList() {
		return bagList;
	}

	/**
	 * Retrieve departure map
	 */
	public Map<String, Departure> getDepartureMap() {
		return departureMap;
	}
}
