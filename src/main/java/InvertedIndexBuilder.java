import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.nio.charset.StandardCharsets;

/**
 * Builds Inverted Index
 * 
 * @author jett
 *
 */
public class InvertedIndexBuilder {
	
	/**
	 * The inverted index to build
	 */
	InvertedIndex invertedIndex;
	
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the inverted index to build
	 */
	public InvertedIndexBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}
	
	
	/**
	 * Parses stemmed and cleaned words from file then 
	 * adds word, location, and position to inverted index.
	 * 
	 * @param invertedIndex the inverted index to add information into
	 * @param path the path of the file
	 * @throws IOException if an IO error occurs
	 */
	public void add(InvertedIndex invertedIndex, Path path) throws IOException {
		//check if path is a regular file
		if (Files.isRegularFile(path)) {
			int position = 1; //position start at index 1
			//get each stemmed and cleaned word from list
			for (String word : TextFileStemmer.listStems(path)) {
				//add word, location, and position to inverted index
				invertedIndex.add(word, path.toString(), position);
				position++;
			}
		}
		else if(Files.isDirectory(path)) {
			//get each file from list of text files
			for (Path file : TextFileFinder.list(path)) {
				int position = 1; //position start at index 1
				//get each stemmed and cleaned word from list
				for (String word : TextFileStemmer.listStems(file)) {
					//add word, location, and position to inverted index
					invertedIndex.add(word, file.toString(), position);
					position++; //increment counter
				}
			}
		}
	}
	
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
		results.clear();
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