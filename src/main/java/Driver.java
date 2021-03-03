import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
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
		
		// TODO -text flag without value? ---> warn the user
		
		//check whether "-text path" flag, value pair exists
		if (map.hasFlag("-text") && map.hasValue("-text")) {
			// TODO Move some of the building logic into another class (InvertedIndexBuilder or InvertedIndexFactory)
			
			//check if path is a regular file
			if (Files.isRegularFile(map.getPath("-text"))) {
				try {
					int position = 1; //position start at index 1
					//get each stemmed and cleaned word from list
					for (String word : TextFileStemmer.listStems(map.getPath("-text"))) {
						//add word, location, and position to inverted index
						invertedIndex.add(word, map.getString("-text"), position);
						position++; //increment position
					}	
				} catch (IOException e) {
					System.out.println("Error in opening file.");
				}
			}
			//check if path is a directory
			else if (Files.isDirectory(map.getPath("-text"))) {
				try {
					//get each file from list of text files
					for (Path file : TextFileFinder.list(map.getPath("-text"))) {
						int position = 1; //position start at index 1
						//get each stemmed and cleaned word from list
						for (String word : TextFileStemmer.listStems(file)) {
							//add word, location, and position to inverted index
							invertedIndex.add(word, file.toString(), position);
							position++; //increment position
						}
					}
				} catch (IOException e) {
					System.out.println("Error in opening file.");
				}
			}
		}
		
		//check for optional flag
		if (map.hasFlag("-index")) {
			try {
				//use given path (or index.json as the default output path) to print inverted index as JSON
				SimpleJsonWriter.asInvertedIndex(invertedIndex, map.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				// TODO Unable to write the inverted index to file: + path.toString()
				System.out.println("Error writing to file.");
			}
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}