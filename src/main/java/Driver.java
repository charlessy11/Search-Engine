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

		ArgumentMap map = new ArgumentMap(args); //parses command-line arguments
		InvertedIndex invertedIndex = new InvertedIndex();
		InvertedIndexBuilder builder = new InvertedIndexBuilder(invertedIndex);
		
		//check whether "-text path" flag, value pair exists
		if (map.hasFlag("-text") && map.hasValue("-text")) {
			try {
				builder.add(map.getPath("-text"));
			} catch (IOException e) {
				System.out.println("Error: Unable to add data to the inverted index.");
			}
		}
		else if (map.hasFlag("-text") && !map.hasValue("-text")) {
			System.out.println("Warning: No value given to -text flag");
		}
		
		//check for optional flag
		if (map.hasFlag("-index")) {
			try {
				//use given path (or index.json as the default output path) to print inverted index as JSON
				invertedIndex.toJsonInvertedIndex(map.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				if (map.hasValue("-index")) {
					System.out.println("Error: Unable to write the inverted index to file: " + map.getPath("-index").toString());
				}
				else {
					System.out.println("Error: Unable to write the inverted index to file: " + Path.of("index.json").toString());
				}
			}
		}
		
		//optional flag to output all of the locations and their word count
		if (map.hasFlag("-counts")) {
			try {
				//use given path (or counts.json as the default output path) to print pretty JSON
				invertedIndex.toJsonObject(map.getPath("-counts", Path.of("counts.json")));
			} catch (IOException e) {
				System.out.println("Error: Unable to calculate total amount of stemmed words.");
				}
			}
				
		//indicates that a search should be performed
		if (map.hasFlag("-query") && map.hasValue("-query")) {
			try {
				//optional flag that indicates all search operations performed should be exact search
				if (map.hasFlag("-exact")) {
					builder.parseQuery(map.getPath("-query"), true);
				}
				//partial search
				else {
					builder.parseQuery(map.getPath("-query"), false);
				}
			} catch (IOException e) {
				System.out.println("Error: No search performed.");
			}
		}
				
		//optional flag that indicates the next argument is the path to use for the search results output file
		if (map.hasFlag("-results")) {
			try {
				builder.toJsonNestedResult(map.getPath("-results", Path.of("results.json")));
			} catch (IOException e) {
				System.out.println("Warning: No output file produced of search results but still performed the search operation..");
			}
		}
		
		//perform multithreading
		if (map.hasFlag("-threads")) {
			
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}