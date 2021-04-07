import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Builds Inverted Index
 * 
 * @author Charles Sy
 *
 */
public class InvertedIndexBuilder {
	/**
	 * The inverted index to build
	 */
	private final InvertedIndex invertedIndex;
	
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the inverted index
	 */
	public InvertedIndexBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}
	
	/**
	 * Checks for path type then adds data
	 * 
	 * @param path the path of the file
	 * @throws IOException if an IO error occurs
	 */
	public void add(Path path) throws IOException {
		//check if path is a regular file
		if (Files.isRegularFile(path)) {
			addData(path);
		}
		//check if path is a directory
		else if(Files.isDirectory(path)) {
			//get each file from list of text files
			for (Path file : TextFileFinder.list(path)) {
				addData(file);
			}
		}
	}
	
	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * then adds word, location, and position to inverted index.
	 * 
	 * @param path the path of the file
	 * @throws IOException if an IO error occurs
	 */
	public void addData(Path path) throws IOException {
		//open file for reading
		try (BufferedReader read = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			int position = 1; //position start at index 1
			Stemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			String line = null;
			String location = path.toString();
			while ((line = read.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					//add to inverted index
					invertedIndex.add(stemmer.stem(word).toString(), location, position);
					position++;
				}
			}
		}
	}
	
	// TODO QueryResultBuilder with results parseQuery toJsonNestedResult
	
	/**
	 * Stores single search results
	 */
	private final Map<String, Collection<SingleSearchResult>> results = new TreeMap<>();
	
	/**
	 * Cleans, parses and sorts each query line
	 * 
	 * @param path the path of the file
	 * @param exact to check if exact/partial search
	 * @throws IOException if an IO error occurs
	 */
	public void parseQuery(Path path, boolean exact) throws IOException {
		try (BufferedReader read = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = read.readLine()) != null) {
				//assigns set a set of unique, cleaned and stemmed words parsed from line
				TreeSet<String> set = TextFileStemmer.uniqueStems(line);
				if (exact == true) {
					if (!set.isEmpty()) {
						//puts a new string from set separated by spaces as key and list of search results as value
						results.put(String.join(" ", set), invertedIndex.exactSearch(set));
					}
				}
				else {
					if (!set.isEmpty()) {
						results.put(String.join(" ", set), invertedIndex.partialSearch(set));
					}
				}
			}
		}
	}
	
	
	/**
	 * Calls SimpleJsonWriter's asNestedResult method
	 * 
	 * @param path the path given by user or default path if otherwise
	 * @throws IOException if an IO error occurs
	 */
	public void toJsonNestedResult(Path path) throws IOException {
		SimpleJsonWriter.asNestedResult(results, path);
	}
}