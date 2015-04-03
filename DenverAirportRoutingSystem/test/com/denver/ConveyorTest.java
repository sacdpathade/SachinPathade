package com.denver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.denver.exception.DenverValidationException;
import com.denver.input.DepartureAndBagInputContainer;
import com.denver.input.InputProcessor;
import com.denver.model.Bag;
import com.denver.routingEngine.ConveyorRoutingEngine;

public class ConveyorTest {

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

	@Before
	public void setup() throws DenverValidationException {
		InputProcessor.processInput(inputFilePath);
	}

	@Test
	public void testInputFile(){
		try {
			InputProcessor.processInput(inputFilePath);
		} catch (DenverValidationException e) {
			Assert.fail("Failed to process file");
		}
		return;
	}

	@Test
	public void testInvalidInput() {
		try {
			ConveyorRoutingEngine.getShortestPath(null, null);
		} catch (DenverValidationException e) {
			try {
				ConveyorRoutingEngine.getShortestDistance(null, null);
			} catch (DenverValidationException e1) {
				return;
			}
		}
		Assert.fail("Failed to identify invalid input data");
	}

	@Test
	public void testShortestDistance() {
		int distance = 0;
		try {
			distance = ConveyorRoutingEngine.getShortestDistance("A2", "A4");
		} catch (DenverValidationException e) {
			Assert.fail("Invalid exception caught");
		}
		Assert.assertTrue(distance == 2);
	}

	@Test
	public void testShortestPath() {
		List<String> shortestPath = null;
		try {
			shortestPath = ConveyorRoutingEngine.getShortestPath("A2", "A4");
		} catch (DenverValidationException e) {
			Assert.fail("Invalid exception caught");
		}
		Assert.assertTrue(shortestPath.size() == 3);
	}

	@Test
	public void testRoutingSystem() {
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
	}
}
