import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Builds Query Result
 * 
 * @author Charles Sy
 *
 */
public class QueryResultBuilder {
	
	/**
	 * The inverted index to search
	 */
	private final InvertedIndex invertedIndex;
	
	/**
	 * Stores single search results
	 */
	private final Map<String, Collection<SingleSearchResult>> results;
	
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the inverted index
	 */
	public QueryResultBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
		this.results = new TreeMap<>();
	}
	
	
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
				// TODO parseQuery(line, exact);
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
	
	/* TODO 
	public void parseQuery(String line, boolean exact) {
		TreeSet<String> set = TextFileStemmer.uniqueStems(line);
		
		if (!set.isEmpty()) {
			String cleaned = String.join(" ", set);
			
			if (!results.containsKey(cleaned)) {
				results.put(cleaned, invertedIndex.search(set, exact));
			}
		}
	}
	
	hello hello world!			> hello, world
	...
	...
	world HELLO world				> hello, world
	
	*/
	
	
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
