package com.denver.input;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.denver.model.ConveyorRoute;

/**
 * Used to store graph information such as nodes and edges.
 * Used enum to create only one instance of graph.
 * 
 * @author SACHIN PATHADE
 */
public enum ConveyorSystemGraph {
	GRAPH;

	private final static Logger logger = Logger.getLogger(DepartureAndBagInputContainer.class);

	final private Set<String> conveyorNodeSet;
	final private Set<ConveyorRoute> conveyorRouteSet;

	/**
	 * Private constructor for not allowing external object creation.
	 */
	private ConveyorSystemGraph() {
		conveyorNodeSet = new HashSet<String>();
		conveyorRouteSet = new HashSet<ConveyorRoute>();
	}

	/**
	 * Add conveyor route to conveyor graph.
	 * It also adds nodes connecting conveyor route to node set.
	 * @param conveyorRoute : Conveyor route to be added.
	 */
	public void addConveyorRoute(ConveyorRoute conveyorRoute) {
		logger.debug("Adding route " + conveyorRoute);
		conveyorRouteSet.add(conveyorRoute);
		conveyorNodeSet.add(conveyorRoute.getSource());
		conveyorNodeSet.add(conveyorRoute.getDestination());
	}

	/**
	 * Clear graph in case if required.
	 */
	public void clearGraph() {
		logger.info("Clearing graph");
		conveyorNodeSet.clear();
		conveyorRouteSet.clear();
	}

	/**
	 * Retrieve conveyor node set
	 * @return : Return Set of conveyor nodes.
	 */
	public Set<String> getConveyorNodeSet() {
		return conveyorNodeSet;
	}

	/**
	 * Retrieve set of conveyor route set
	 * @return : Set of conveyor routes.
	 */
	public Set<ConveyorRoute> getConveyorRouteSet() {
		return conveyorRouteSet;
	}
}
