/**
 * Uebung 03
 * 
 * Ziele:
 * 
 * Mit der Übung soll gezeigt werden, wie „grössere“ Datenmengen mit den Methoden
 * der verschiedenen Klassen des Collections Frameworks (primär TreeMap, TreeSet) 
 * effizient verarbeitet werden können. 
 * 
 */
package ch.bnntd.bfh.pgrm.filesexc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bananatreedad.jntimestopper.JNTimeStopper;
import ch.bfh.modulBTX8051.Aufg14.base.Person;

/**
 * @author jnt
 *
 */
public class PersonDataProcessor {

	private static final Logger logger = LoggerFactory
			.getLogger(PersonDataProcessor.class);

	/**
	 * @param inputFile
	 *            - name of the file which should be read. Must always be
	 *            defined and can't be an empty string ("") or null.
	 * 
	 * @param outputFileName
	 *            - name of the file to write the correct lines to. If the name
	 *            is defined as null, no file is created.
	 * 
	 * @param outputErrorFileName
	 *            - name of the file to write the wrong lines to. If the name is
	 *            defined as null, no file is created.
	 * 
	 * @param logFileName
	 *            - the name of the file to which the log should be written to.
	 *            If the name is defined as null, no file is created.
	 * 
	 *            Die Methode gibt true zurück, wenn alle Zeilen der
	 *            Input-Daten das geforderte Format erfüllen.
	 * 
	 * @throws EmptyFileException
	 * @throws IOException
	 * 
	 */
	public static boolean dataAnalyzer(File inputFile, File outputFile,
			File outputErrorFile, File logFile) throws EmptyFileException {

		Scanner inputFileScanner = null;
		PrintWriter outputFileWriter = null;
		PrintWriter outputErrorFileWriter = null;
		PrintWriter logFileWriter = null;

		int processedLines = 0;
		int correctLines = 0;
		int wrongLines = 0;

		boolean returnValue = true;

		try {
			if (inputFile != null)
				inputFileScanner = new Scanner(inputFile, "UTF-8");
			else
				throw new IllegalArgumentException();

			// Three of the params can be defined as null,
			// then no files will be created.
			if (outputFile != null) {
				outputFileWriter = new PrintWriter(outputFile);
			}
			if (outputErrorFile != null) {
				outputErrorFileWriter = new PrintWriter(outputErrorFile);
			}
			if (logFile != null) {
				logFileWriter = new PrintWriter(logFile);
			}

			if (inputFileScanner.hasNextLine()) {

				if (logFileWriter != null)
					logFileWriter
							.println("-- For summary scroll to end of file.\n");

				while (inputFileScanner.hasNextLine()) {
					String line = inputFileScanner.nextLine();
					try {
						if (checkLine(line, logFileWriter)) {
							logger.debug("Line is valid.");
							correctLines++;
							if (outputFileWriter != null) {
								outputFileWriter.println(line);
							}
						} else {
							logger.debug("Line is invalid.");
							wrongLines++;
							if (outputFileWriter != null) {
								outputFileWriter.println(line);
							}
							returnValue = false;
						}
						processedLines++;
					} catch (IOException e) {
						logger.debug("Error with logfileWriter.");
					}
				}
			} else
				throw new EmptyFileException("Input file is empty.");

			if (logFileWriter != null) {
				logFileWriter.println();
				logFileWriter.println(
						"Data processing log:\n- Method processing the data:");

				// TODO ask for fastest way to get this name programmatically
				logFileWriter.println(
						"ch.bnntd.bfh.pgrm.filesexc.PersonDataProcessorTest.testGetNameOfActualMethod()");
				Date date = new Date();
				SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yyyy");
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

				logFileWriter.println("Date: " + dayFormat.format(date)
						+ " Time: " + timeFormat.format(date));
				logFileWriter
						.println("Input file name: " + inputFile.getName());
				logFileWriter
						.println("Output file name: " + outputFile.getName());
				logFileWriter.println(
						"Output error file name: " + outputErrorFile.getName());
				logFileWriter.println("Logfile name: " + logFile.getName());
				logFileWriter.println("Proccessed lines: " + processedLines);
				logFileWriter.println("Wrong lines: " + wrongLines);
				logFileWriter.println("Correct lines: " + correctLines);
			}

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			return false;
		} finally {
			if (inputFileScanner != null)
				inputFileScanner.close();
			if (outputFileWriter != null)
				outputFileWriter.close();
			if (outputErrorFileWriter != null)
				outputErrorFileWriter.close();
			if (logFileWriter != null)
				logFileWriter.close();
		}

		return returnValue;
	}

	// valid line:
	// 21196;Helfenstein;Ruth;1931;W
	/**
	 *
	 * @param line
	 * @return true if line is ok, false if not
	 * @throws IOException
	 */
	private static boolean checkLine(String line, PrintWriter logfileWriter)
			throws IOException {

		boolean logToFile = false;
		if (logfileWriter != null)
			logToFile = true;

		logger.debug("Checking line: [" + line + "]");
		if (logToFile)
			logfileWriter.println("Checking line: [" + line + "]");

		String[] splittedLine = line.split(";");
		boolean returnValue = true;
		String errorMessage = null;

		if (splittedLine.length != 5) {
			errorMessage = "Wrong line format: Line contains more or less than 5 entries.";
			logger.debug(errorMessage);
			if (logToFile) {
				logfileWriter.println(errorMessage);
				logfileWriter.println();
			}
			return false;
		}

		// check if one ore more entry is empty
		for (String string : splittedLine) {
			if (string.matches("")) {
				errorMessage = "One ore more entries are empty.";
				logger.debug(errorMessage);
				if (logToFile) {
					logfileWriter.println(errorMessage);
					logfileWriter.println();
				}
				return false;
			}
		}

		if (!splittedLine[0].matches("\\d+")) {
			errorMessage = "Wrong line format: ID contains not between 4 and 5 digits.";
			logger.debug(errorMessage);
			if (logToFile)
				logfileWriter.println(errorMessage);
			returnValue = false;
		}

		if (!splittedLine[1].matches("\\D+")) {
			errorMessage = "Wrong line format: Name contains numbers.";
			logger.debug(errorMessage);
			if (logToFile)
				logfileWriter.println(errorMessage);
			returnValue = false;
		}

		if (!splittedLine[2].matches("\\D+")) {
			errorMessage = "Wrong line format: Name contains numbers.";
			logger.debug(errorMessage);
			if (logToFile)
				logfileWriter.println(errorMessage);
			returnValue = false;
		}

		if (!splittedLine[3].matches("\\d\\d\\d\\d")) {
			errorMessage = "Wrong line format: Volume has other format than 4 digits.";
			logger.debug(errorMessage);
			if (logToFile)
				logfileWriter.println(errorMessage);
			returnValue = false;
		}

		if (!splittedLine[4].matches("W|M|w|m")) {
			errorMessage = "Wrong line format: Gender format contains other char than W/M/m/w.";
			logger.debug(errorMessage);
			if (logToFile)
				logfileWriter.println(errorMessage);
			returnValue = false;
		}

		if (logToFile) {
			if (returnValue)
				logfileWriter.println("Line valid.");
			logfileWriter.println();
		}
		return returnValue;
	}

	/**
	 * Reads the comma-separated values (CSV) of a given file line per line and
	 * loads the data into a TreeMap.
	 * 
	 * @return
	 * @throws EmptyFileException
	 */
	public static TreeMap<Long, Person> buildMapFromFile(File file)
			throws FileNotFoundException, EmptyFileException {

		if (file.exists()) {

			JNTimeStopper jnt = new JNTimeStopper();

			jnt.start();
			boolean isFileValid = dataAnalyzer(file, null, null, null);
			logger.debug("Analysing took " + jnt.stop() + "ms.");

			if (isFileValid) {

			}
		} else
			throw new FileNotFoundException();

		return new TreeMap<Long, Person>();
	}

	/**
	 * Merges two TreeMaps with the same data format.
	 * 
	 * @return Map<PID, Person> - the map with the ids
	 */
	public static TreeMap<Long, Person> mergeTwoMaps() {

		return new TreeMap<Long, Person>();
	}

	/**
	 * Reverses the order (according to the key) of a defined TreeMap.
	 * 
	 * @return the reversed map
	 */
	public static TreeMap<Long, Person> reverseTreeMap() {

		return new TreeMap<Long, Person>();
	}

	/**
	 * Allows to search values (lines/persons) in the defined TreeMap.
	 * 
	 * The possible options are: 􏰉 n:name - searches all lines/persons with the
	 * name name. 􏰉 f:forename - searches all lines/persons with the forename
	 * forename. 􏰉 y:yearOfBirth - searches all lines/persons with the year of
	 * birth yearOfBirth. 􏰉 g:gender - searches all lines/persons with the
	 * gender gender (M or W).
	 *
	 * 􏰉 Only one option can be defined at once. 􏰉 The possible options (n, f,
	 * y, g) and the delimiter (:) are checked. If the format is not met, an
	 * IllegalArgumentException is thrown.
	 *
	 *
	 * @return the Map<Long, Persons> containing the searched Persons
	 */
	public static TreeMap<Long, Person> searchPersons()
			throws IllegalArgumentException {

		return new TreeMap<Long, Person>();
	}

	/**
	 * Writes the data of a TreeMap to a file with a defined name.
	 * 
	 */
	public static void writeMapToFile(File file, Map<Long, Person> map) {

	}

	/**
	 * Writes the data of a TreeSet to a file with a defined name.
	 * 
	 * @param persons
	 */
	public static void writeKeysToFile(TreeSet<Person> persons) {

	}

	/**
	 * TreeSet<Person> writeNotUsedKeysToSet()
	 * 
	 * o Searches all keys (IDs) of the defined TreeMap that are not used.
	 * 
	 * o The search range can be defined with an initial and an end ID.
	 * 
	 * o Throws a NumberFormatException if the defined end ID isn't greater than
	 * the defined initial ID.
	 * 
	 * @return
	 */
	public static TreeSet<Person> writeNotUsedKeysToSet() {

		return new TreeSet<Person>();
	}

}