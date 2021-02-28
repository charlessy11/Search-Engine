//import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;
import java.util.ArrayList;
//import java.util.stream.Stream;

// Charles Sy

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class Driver {
	
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		Instant start = Instant.now();

		//build argument map
		ArgumentMap map = new ArgumentMap(args); //parses command-line arguments
		//build inverted index
		InvertedIndex invertedIndex = new InvertedIndex();
		//check for -text path 
		if (map.hasFlag("-text") && map.hasValue("-text")) {
			//check if path is a regular file
			if (Files.isRegularFile(map.getPath("-text"))) {
				//open file
//				try (BufferedReader br = new BufferedReader(new FileReader(map.getString("-text")))) {
					//reads then stem words from file and store in a list
					try {
						ArrayList<String> words = TextFileStemmer.listStems(map.getPath("-text"));
						int counter = 1; //start at index 1
						for (String word : words) {
							invertedIndex.add(word, map.getString("-text"), counter);
							counter++;
						}	
					} catch (IOException e) {
						System.out.println("Error in opening file.");
					}
//				} catch (IOException e) {
//					System.out.println("Error in opening file.");
//				}
			}
			//check if path is a directory
			else if (Files.isDirectory(map.getPath("-text"))) {
				try {
					//list all text files
					List<Path> textFiles = TextFileFinder.list(map.getPath("-text"));
					for (Path file : textFiles) {
						//open file for reading
//						try (BufferedReader br = Files.newBufferedReader(file)) {
							//reads then stem words from file and store in a list
							ArrayList<String> words = TextFileStemmer.listStems(file);
							int counter = 1; //start at index 1
							for (String word : words) {
								invertedIndex.add(word, map.getString("-text"), counter);
								counter++;
							}
//						}
					}
				} catch (IOException e) {
					System.out.println("Error in opening file.");
				}
			}

		}
		if (map.hasFlag("-index")) {
			//use given path or index.json as the default output path to print inverted index as JSON
			try {
				SimpleJsonWriter.asInvertedIndex(invertedIndex, map.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				System.out.println("Error writing to file.");
			}
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
