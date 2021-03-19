import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class Driver {
	
	private static final Charset UTF8 = null;
	/** The stemmer to use for the cleaning methods. */
	public static final Stemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);

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
			try {
				InvertedIndexBuilder.add(invertedIndex, map.getPath("-text"));
			} catch (IOException e) {
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
				InvertedIndex.toJson(invertedIndex, map.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				System.out.println("Error: Unable to write the inverted index to file: " + map.getPath("-index").toString());
			}
		}
		
		if (map.hasFlag("-counts")) {
			try {
//				SimpleJsonWriter.asObject(invertedIndex.count(map.getPath("-counts").toString()), map.getPath("-counts", Path.of("counts.json")));
				TreeMap<String, Integer> wordCount = new TreeMap<>();
				wordCount.put(map.getPath("-counts").toString(), invertedIndex.size());
				SimpleJsonWriter.asObject(wordCount, map.getPath("-counts", Path.of("counts.json")));
			} catch (IOException e) {
				System.out.println("Error: Unable to write the inverted index to file: " + map.getPath("-index").toString());
			}
		}
		
		if (map.hasFlag("-query") && map.hasValue("-query")) {
			Path path = map.getPath("-query");
			//splitting the cleaned line into words by whitespace
			Function<String, String[]> tokenize = s -> s.split("\\s+");
			//cleaning the line of any non-alphabetic chars and converting the remaining chars to lowercase
			Function<String, String> clean = s -> s.replaceAll("[^A-z\\s]+", " ").toLowerCase(); 
			Supplier<TreeSet<String>> collector = TreeSet::new; //removes duplicates and sorts alphabetically
			try (
				BufferedReader reader = Files.newBufferedReader(path, UTF8);
				Stream<String> lines = reader.lines();
			) {
				lines
					.flatMap(line -> Stream.of(tokenize.apply(line)))
					.map(clean)
					.map(word -> (String)stemmer.stem(word)) //stemming each word
					.collect(Collectors.toCollection(collector));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (map.hasFlag("-query") && !map.hasValue("-query")) {
			System.out.println("Warning: No value given to -query flag");
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}