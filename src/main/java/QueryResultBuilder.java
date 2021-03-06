import java.io.IOException;
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
public class QueryResultBuilder implements QueryResultBuilderInterface {
	/**
	 * The inverted index to search
	 */
	private final InvertedIndex invertedIndex;
	
	/**
	 * Stores single search results
	 */
	private final Map<String, Collection<InvertedIndex.SingleSearchResult>> results;
	
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
	 * @param line the line to be cleaned and parsed
	 * @param exact the flag that determines what type of search to perform
	 */
	@Override
	public void parseQuery(String line, boolean exact) {
		TreeSet<String> set = TextFileStemmer.uniqueStems(line);
		if (!set.isEmpty()) {
			String cleaned = String.join(" ", set);
			if (!results.containsKey(cleaned)) {
				results.put(cleaned, invertedIndex.search(set, exact));
			}
		}
	}
	
	/**
	 * Calls SimpleJsonWriter's asNestedResult method
	 * 
	 * @param path the path given by user or default path if otherwise
	 * @throws IOException if an IO error occurs
	 */
	@Override
	public void toJsonNestedResult(Path path) throws IOException {
		SimpleJsonWriter.asNestedResult(results, path);
	}
}