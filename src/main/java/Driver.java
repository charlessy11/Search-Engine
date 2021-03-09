import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

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
		
		//check whether "-text path" flag, value pair exists
		if (map.hasFlag("-text") && map.hasValue("-text")) {
			InvertedIndexBuilder.add(invertedIndex, map.getPath("-text"));
		}
		else if (map.hasFlag("-text") && !map.hasValue("-text")) {
			System.out.println("Warning: No value given to -text flag");
		}
		
		//check for optional flag
		if (map.hasFlag("-index")) {
			try {
				//use given path (or index.json as the default output path) to print inverted index as JSON
				SimpleJsonWriter.asInvertedIndex(invertedIndex, map.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				System.out.println("Error: Unable to write the inverted index to file: " + map.getPath("-index").toString());
			}
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}