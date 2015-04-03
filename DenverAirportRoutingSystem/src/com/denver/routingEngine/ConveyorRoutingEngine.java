package com.denver.routingEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.denver.exception.DenverValidationException;
import com.denver.input.ConveyorSystemGraph;
import com.denver.model.ConveyorRoute;

/**
 * Routing engine which will use data created by conveyor graph and find shortest
 * path from entry point of bag to exit point i.e. flight gate.
 * 
 * Assumption is its bidirectional graph as per sample output given.
 * 
 * This routing engine make use of nodes and routes identified by input processor.
 * This routing algorithm identifies shortest path for all nodes from given source
 * node for first request for given source node. It caches information created for
 * given source code for sub sequent usage to improve performance.
 * 
 * Here, we have used Dijkstra's shortest path algorith to find shortest travel path
 * http://en.wikipedia.org/wiki/Dijkstra's_algorithm
 * 
 * Following steps needs to be followed to identify shortest path.
 * 1. Assign every node distance value as 'infinity'. Assign 0 to source node.
 * 2. Set all nodes as unvisited except source node and add it to unvisited node set.
 * 3. For current node, calculate distance to all unvisited neighbors.If distance
 *    assigned to neighbor is more than distance of current + distance of current to
 *    neighbor, then set neighbor distance to later.
 * 4. Once all neighbors checked, mark current node visited and remove from unvisited.
 * 5. If destination node marked visited OR if unvisited set is empty, then Stop.
 * 6. Select unvisited node and mark it as current and go back to Step 3 above.
 * 
 *
 * @author SACHIN PATHADE
 *
 */
public class ConveyorRoutingEngine {

	/**** STATIC ATTRIBUTES ****/

	private final static Logger logger = Logger.getLogger(ConveyorRoutingEngine.class);

	/*Routing cache which will hold routing information for given source node*/
	private static Map<String, ConveyorRoutingEngine> engineRoutingCache = new HashMap<String, ConveyorRoutingEngine>();

	/**** INSTANCE ATTRIBUTES ****/

	//Set to store settled and unsettled nodes.
	private Set<String> settledNodes;
	private Set<String> unSettledNodes;

	//Map used to store distance for given node.
	private Map<String, Integer> distance;

	//Map used to store predecessors for given node.
	private Map<String, String> predecessors;

	//Set used to store edges and nodes of routing graph.
	Set<ConveyorRoute> edges = null;
	Set<String> nodes = null;

	/** PUBLIC STATIC METHODS**/

	/**
	 * Retrieve shortest path from given source node to target node.
	 * @param source : Source node
	 * @param target : Destination node.
	 * @return : Return list of nodes which indicates shortest path.
	 * @throws DenverValidationException
	 */
	public static List<String> getShortestPath(final String source, final String target) throws DenverValidationException {
		logger.info("Retrieving shortest path from " + source + " to " + target);
		if(isValidNode(source) && isValidNode(target)) {
			ConveyorRoutingEngine routingEngine = getRoutingEngine(source);
			List<String> shortestPathNodeList = routingEngine.getPath(target);
			logger.info("Shortest path from " + source + " to " + target + " => " + shortestPathNodeList);
			return shortestPathNodeList;
		} else {
			logger.error("Invalid source or target node");
			throw new DenverValidationException("Invalid nodes given for getPath()");
		}
	}

	/**
	 * Retrieve shortest distance from given source node to target node.
	 * @param source : Source node
	 * @param target : Destination node.
	 * @return : Return distance between source node to distance node.
	 * @throws DenverValidationException
	 */
	public static Integer getShortestDistance(final String source, final String target) throws DenverValidationException {
		logger.info("Retrieving shortest distance from " + source + " to " + target);
		if(isValidNode(source) && isValidNode(target)) {
			ConveyorRoutingEngine routingEngine = getRoutingEngine(source);
			Integer shortestDistance = routingEngine.getShortestDistance(target);
			logger.info("Shortest distance from " + source + " to " + target + " => " + shortestDistance);
			return shortestDistance;
		} else {
			logger.error("Invalid source or target node");
			throw new DenverValidationException("Invalid nodes given for getShortestDistance()");
		}
	}

	/** PRIVATE STATIC METHODS **/

	/**
	 * Check whether Conveyor system graph exist and whether it contains given node or not.
	 * 
	 * Returns false if given node doesn't exist in graph.
	 */
	private static boolean isValidNode(String node) {
		if(ConveyorSystemGraph.GRAPH.getConveyorNodeSet() == null) {
			return false;
		}
		return ConveyorSystemGraph.GRAPH.getConveyorNodeSet().contains(node);
	}

	/**
	 * Retrieve routing engine instance for given source.
	 * This method will create routing enginer for given source if doesn't exist already
	 * else it will return cached routing engine instance for given source.
	 * @param source : Node for which routing engine needs to be created.
	 * @return : Return instance of routing engine for given source point.
	 */
	private static ConveyorRoutingEngine getRoutingEngine(final String source) {
		if(engineRoutingCache.containsKey(source)) {
			logger.debug("Returning routing engine from cache for " + source);
			return engineRoutingCache.get(source);
		} else {
			logger.debug("Creating new routing engine for " + source);
			ConveyorRoutingEngine newRoutingEngine = new ConveyorRoutingEngine(source);
			engineRoutingCache.put(source, newRoutingEngine);
			return newRoutingEngine;
		}
	}

	/**
	 * Retrieve node at other end of edge, if one end of node has given input node.
	 * @param edge : Edge which needs to be analyzed for connecting nodes.
	 * @param node : Input node.
	 */
	private static String retrieveNodeConnectedByEdge(final ConveyorRoute edge, final String node) {
		if(edge.getSource().equals(node)) {
			return edge.getDestination();
		} else if (edge.getDestination().equals(node)) {
			return edge.getSource();
		}
		return null;
	}

	/** CONSTRUCTOR **/

	/**
	 * Private constructor to deny routing engine creation from outside.
	 * Factory pattern used to create routing engine object
	 * 
	 * Initialize nodes and edges from conveyor system graph.
	 * @param source : Source for which routing engine needs to be created
	 */
	private ConveyorRoutingEngine(final String source) {
		logger.info("Creating routing engine for " + source);

		this.nodes = ConveyorSystemGraph.GRAPH.getConveyorNodeSet();
		this.edges = ConveyorSystemGraph.GRAPH.getConveyorRouteSet();

		settledNodes = new HashSet<String>();
		unSettledNodes = new HashSet<String>();
		distance = new HashMap<String, Integer>();
		predecessors = new HashMap<String, String>();

		identifyShortestPathsForGivenSource(source);
	}

	/** PRIVATE INSTANCE METHODS **/

	/**
	 * Identifies shortest paths for all nodes from given source node.
	 * @param source : Source for which shortest paths will be created.
	 */
	private void identifyShortestPathsForGivenSource(String source) {
		//Set distance for initial node as Zero.
		distance.put(source, 0);

		//Add all nodes to unsettled nodes set.
		unSettledNodes.addAll(nodes);

		//Identify minimum node from unsettled nodes and reset its neighbor
		//distances till we have zero unsettled nodes.
		while (unSettledNodes.size() > 0) {
			String node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			resetNeighborDistanceToMinimal(node);
		}
	}

	/**
	 * Retrieve unsettled nodes which has minimum distance.
	 * Nodes will be settled down in increasing order of distance.
	 * @param ConveyorNodees : All unsettled nodes.
	 * @return : Return node which has minimum distance.
	 */
	private String getMinimum(Set<String> ConveyorNodees) {
		String minimum = null;
		for (String String : ConveyorNodees) {
			if (minimum == null) {
				minimum = String;
			} else {
				if (getShortestDistance(String) < getShortestDistance(minimum)) {
					minimum = String;
				}
			}
		}
		logger.debug("Returning minimum as" + minimum);
		return minimum;
	}

	/**
	 * Reset distance to neighbors to minimal possible.
	 * For each neighbor, will check its original distance and distance of current node +
	 * distance of current node to its neighbor. Whichever is less, that will be set
	 * as distance of neighbor node.
	 * @param node : Node whose neighbors distances will be analyzed and updated if required.
	 */
	private void resetNeighborDistanceToMinimal(String node) {
		List<String> adjacentNodes = getNeighbors(node);
		for (String target : adjacentNodes) {
			Integer newDistance = getShortestDistance(node) + getDistance(node, target);
			if (getShortestDistance(target) > newDistance) {
				logger.debug("Resetting " + target + " distance to "+ newDistance);
				distance.put(target, newDistance);
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}
	}

	/**
	 * Retrieve distance between given 2 nodes assuming they are connected by edge.
	 * This will identify edge which connects both given nodes and then return travel time.
	 * @param node
	 * @param target
	 * @return
	 */
	private int getDistance(String node, String target) {
		for (ConveyorRoute edge : edges) {
			if (edge.getSource().equals(node)
					&& edge.getDestination().equals(target)) {
				return edge.getTravelTime();
			} else if (edge.getSource().equals(target)
					&& edge.getDestination().equals(node)) {
				return edge.getTravelTime();
			}
		}
		throw new RuntimeException("Should not happen");
	}

	/**
	 * Retrieve list of all unsettled neighbors for given node.
	 * @param node : Node whose unsettled neighbors needs to be analyzed.
	 * @return : List of unsettled neighbors.
	 */
	private List<String> getNeighbors(String node) {
		List<String> neighbors = new ArrayList<String>();
		for (ConveyorRoute edge : edges) {
			String neighborNode = retrieveNodeConnectedByEdge(edge, node);
			if ((neighborNode != null) && (!isSettled(neighborNode))) {
				neighbors.add(neighborNode);
			}
		}
		return neighbors;
	}

	/**
	 * Retrieve shortest path of target node from source node for which
	 * current routing engine has been created
	 * @param target : Target node till which path needs to traced.
	 * @return : List of nodes which indicate path from source node to target node.
	 */
	private List<String> getPath(String target) {
		List<String> path = new LinkedList<String>();
		String step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Put it into the correct order as its calculated in reverse order
		Collections.reverse(path);
		return path;
	}

	/**
	 * Retrieve shortest distance from source to given target.
	 * It return Max value in case distance doesn't exist in distance array.
	 * @param target : Target till which distance needs to be calculated.
	 * @return : Distance from source node to target node.
	 */
	private int getShortestDistance(String target) {
		Integer d = distance.get(target);
		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}

	/**
	 * Check if given node is settled or not.
	 * @param String : Node which needs to be checked.
	 * @return : Return true if node settled, else false.
	 */
	private boolean isSettled(String String) {
		return settledNodes.contains(String);
	}
}
