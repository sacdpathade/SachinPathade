package com.denver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.denver.exception.DenverValidationException;
import com.denver.input.DepartureAndBagInputContainer;
import com.denver.input.InputProcessor;
import com.denver.model.Bag;
import com.denver.routingEngine.ConveyorRoutingEngine;

public class RoutingSystem {

	public static final String inputFilePath = "./test/resources/ConveyorRoutingInput.txt";

	public static void printInputFile() {
		try {
			File inputFile = new File(inputFilePath);
			if (inputFile.isFile()) {
				FileInputStream fis = new FileInputStream(inputFile);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(fis));
				String line = null;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void processInput() throws DenverValidationException {
		InputProcessor.processInput(inputFilePath);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			processInput();
			System.out.println("------------------------INPUT-----------------------");
			printInputFile();
			System.out.println("------------------------OUTPUT----------------------");
			for(Bag bag : DepartureAndBagInputContainer.CONTAINER.getBagList()) {
				String startNode = bag.getEntryPoint();
				String endNode = DepartureAndBagInputContainer.CONTAINER.getBagDestination(bag);

				List<String> nodePath = null;
				try {
					nodePath = ConveyorRoutingEngine.getShortestPath(startNode, endNode);
				} catch (DenverValidationException e) {
					System.out.println("Error retrieving shortest path from " + startNode + " to " + endNode);
				}
				Integer distance = -1;
				try {
					distance = ConveyorRoutingEngine.getShortestDistance(startNode, endNode);
				} catch (DenverValidationException e) {
					System.out.println("Error retrieving shortest distance from " + startNode + " to " + endNode);
				}

				if(nodePath != null) {
					System.out.print(bag.getBagNumber() + " ");
					for(String node : nodePath) {
						System.out.print(node + " ");
					}
					System.out.println(": " + distance);
				} else {
					System.out.println("Something wrong in routing system");
				}
			}
			System.out.println("------------------------END-------------------------");
		} catch (DenverValidationException e) {
			System.out.println("Error in executing routing system");
		}

	}

}
