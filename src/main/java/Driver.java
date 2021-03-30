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
	// TODO Cleanup
	
//	private static final Charset UTF8 = null;
//	/** The stemmer to use for the cleaning methods. */
//	public static final Stemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);

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
				builder.add(invertedIndex, map.getPath("-text"));
			} catch (IOException e) {
				// TODO Are you sure that is why the above failed? Consider a different message.
				System.out.println("Error: Unable to open file.");
			}
		}
		else if (map.hasFlag("-text") && !map.hasValue("-text")) {
			System.out.println("Warning: No value given to -text flag");
		}
		
		
		//check for optional flag
		if (map.hasFlag("-index")) {
			try {
				//use given path (or index.json as the default output path) to print inverted index as JSON
				invertedIndex.toJson(map.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				// TODO Nice output, but the path output here might not be the path used above
				System.out.println("Error: Unable to write the inverted index to file: " + map.getPath("-index").toString());
			}
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}