package com.denver.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.denver.constant.DenverConstant;
import com.denver.exception.DenverValidationException;
import com.denver.model.Bag;
import com.denver.model.ConveyorRoute;
import com.denver.model.Departure;

/**
 * This file is used to read input file and initialize data required for conveyor routing system.
 *
 * This file will read input file line by line, parse it and create following data.
 * 		1. Conveyor system graph with nodes and routes.
 *      2. Flight departure details along with flight ID.
 *      3. Bag details from source to flight
 *
 * @author  SACHIN PATHADE
 */
public final class InputProcessor {
	private final static Logger logger = Logger.getLogger(InputProcessor.class);

	private static enum SECTION_TYPE {
		BAGS, CONVEYOR_SYSTEM, DEPARTURES
	}

	private static SECTION_TYPE currentSectionType = null;

	/**
	 * Default private constructor to not allow object creation as its not required
	 * for classed which does have only static methods.
	 */
	private InputProcessor() {
	}

	/**
	 * Process bag information. Read bag data from input list of bag and create bag data.
	 */
	private static void processBags(List<String> wordList) {
		if (wordList == null || wordList.size() < DenverConstant.BAG_INFO.values().length) {
			logger.error("Less than " + DenverConstant.BAG_INFO.values().length + " words found in bag info." + wordList);
			return;
		}

		DepartureAndBagInputContainer.CONTAINER.addBag(new Bag(
				wordList.get(DenverConstant.BAG_INFO.BAG_NUMBER.ordinal()),
				wordList.get(DenverConstant.BAG_INFO.ENTRY_POINT.ordinal()),
				wordList.get(DenverConstant.BAG_INFO.FLIGHT_ID.ordinal())
		));
	};

	/**
	 * Process conveyor system. Read conveyor data from input list of words and create conveyor graph.
	 */
	private static void processConveyorSystem(List<String> wordList) {
		if (wordList == null || wordList.size() < DenverConstant.CONVEYOR_INFO.values().length) {
			logger.error("Less than " + DenverConstant.CONVEYOR_INFO.values().length + " words found in conveyor info." + wordList);
			return;
		}

		try {
			ConveyorSystemGraph.GRAPH.addConveyorRoute(new ConveyorRoute(
					wordList.get(DenverConstant.CONVEYOR_INFO.NODE1.ordinal()),
					wordList.get(DenverConstant.CONVEYOR_INFO.NODE2.ordinal()),
					Integer.parseInt(wordList.get(DenverConstant.CONVEYOR_INFO.TRAVEL_TIME.ordinal()))));
		} catch (NumberFormatException e) {
			logger.error("Failed to format travel time, skipping record for " + wordList);
			return;
		}
	}

	/**
	 * Process Departures. Read departure data from input list of words and create departure.
	 */
	private static void processDepartures(List<String> wordList) {
		if (wordList == null || wordList.size() < DenverConstant.DEPARTURE_INFO.values().length) {
			logger.error("Less than " + DenverConstant.DEPARTURE_INFO.values().length + " words found in departure info" + wordList);
			return;
		}

		DepartureAndBagInputContainer.CONTAINER.addDeparture(new Departure(
				wordList.get(DenverConstant.DEPARTURE_INFO.FLIGHT_ID.ordinal()),
				wordList.get(DenverConstant.DEPARTURE_INFO.FLIGHT_GATE.ordinal()),
				wordList.get(DenverConstant.DEPARTURE_INFO.DESTINATION.ordinal()),
				wordList.get(DenverConstant.DEPARTURE_INFO.FLIGHT_TIME.ordinal())));
	}

	/**
	 * Process input file line read from input file.
	 * Based on current section type set, it parses given line.
	 * @param inputFileLine : Input file line which contains data
	 */
	private static void processInputFileLine(String inputFileLine) {
		try {
			//Return if input file line is empty.
			if (inputFileLine.length() == 0) {
				return;
			}

			//Parse data based on space(s)
			List<String> wordList = Arrays.asList(inputFileLine.split("[ ]+"));

			//Parse as per current section type noted.
			switch (currentSectionType) {
			case CONVEYOR_SYSTEM:
				processConveyorSystem(wordList);
				break;
			case DEPARTURES:
				processDepartures(wordList);
				break;
			case BAGS:
				processBags(wordList);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error("Error processing line " + inputFileLine);
		}
	}

	/**
	 * This is public method which will expects file path for file which contains input for conveyor system.
	 *
	 * File will be read line by line, when line which contains, '# Section', then it marks start of
	 * given section. Based on sections, it parses following lines till it finds another section.
	 *
	 * @param filePath : Path for file which contains input required for conveyor system.
	 * @throws DenverValidationException
	 */
	public static boolean processInput(String filePath) throws DenverValidationException {
		//Return if file path is null or empty
		if (filePath == null || filePath.length() == 0) {
			throw new DenverValidationException("Null of empty file");
		}

		try {
			File inputFile = new File(filePath);
			if (inputFile.isFile()) {
				FileInputStream fis = new FileInputStream(inputFile);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(fis));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (line.contains(DenverConstant.SECTION)) {
						//Set current section type and continue
						if (line.contains(DenverConstant.CONVEYOR_SYSTEM)) {
							currentSectionType = SECTION_TYPE.CONVEYOR_SYSTEM;
						} else if (line.contains(DenverConstant.DEPARTURES)) {
							currentSectionType = SECTION_TYPE.DEPARTURES;
						} else if (line.contains(DenverConstant.BAGS)) {
							currentSectionType = SECTION_TYPE.BAGS;
						}
					} else {
						//Process non section line as it contains valid input data
						processInputFileLine(line);
					}
				}
				reader.close();

				if(ConveyorSystemGraph.GRAPH.getConveyorNodeSet() == null ||
						ConveyorSystemGraph.GRAPH.getConveyorNodeSet().size() == 0) {
					logger.error("No nodes or edges found");
					throw new DenverValidationException("No nodes or edges found");
				}

				if(DepartureAndBagInputContainer.CONTAINER.getBagList() == null ||
						DepartureAndBagInputContainer.CONTAINER.getBagList().size() == 0) {
					logger.error("No bag information found");
					throw new DenverValidationException("No bag information found");
				}


				return true;
			} else {
				logger.error("Input path doesn't point to valid directory");
				throw new DenverValidationException("Input path doesn't point to valid directory");
			}
		} catch (Exception e) {
			throw new DenverValidationException("Error processing input file");
		}
	}
}
