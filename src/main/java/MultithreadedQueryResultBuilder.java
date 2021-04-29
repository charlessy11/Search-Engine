import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * 
 * Move the parseQuery(Path, boolean) implementation as a default method in the interface
 * QueryResultBuilderInterface.super.parseQuery(path, exact);
 * 
 * Both classes then have its own private data
 * 
 */

/**
 * The multithreaded and thread-safe query result builder.
 * 
 * @author Charles Sy
 *
 */
public class MultithreadedQueryResultBuilder implements QueryResultBuilderInterface {

	/**
	 * The work queue for multithreading
	 */
	private final WorkQueue queue;
	
	/**
	 * The thread-safe inverted index to search
	 */
	private final ConcurrentInvertedIndex invertedIndex;
	
	/**
	 * Stores single search results
	 */
	private final Map<String, Collection<InvertedIndex.SingleSearchResult>> results;
	
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the multithreaded and thread-safe inverted index
	 * @param queue the work queue for multithreading
	 */
	public MultithreadedQueryResultBuilder(ConcurrentInvertedIndex invertedIndex, WorkQueue queue) {
		this.queue = queue;
		this.invertedIndex = invertedIndex;
		this.results = new TreeMap<>();
	}
	
	@Override
	public void parseQuery(Path path, boolean exact) throws IOException {
		QueryResultBuilderInterface.super.parseQuery(path, exact);
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	@Override
	public void parseQuery(String line, boolean exact) {
		queue.execute(new Task(line, exact));
	}
	
	@Override
	public void toJsonNestedResult(Path path) throws IOException {
		synchronized (results) {
			SimpleJsonWriter.asNestedResult(results, path);
		}
	}
	
	/**
	 * The non-static task class that provides functionality to threads in the runnable state.
	 * 
	 * @author Charles Sy
	 *
	 */
	private class Task implements Runnable {
		
		/**
		 * The line being cleaned and parsed
		 */
		private final String line;
		/**
		 * The flag that determines what type of search to perform
		 */
		private final boolean exact;
		
		/**
		 * Constructor
		 * 
		 * @param line the line being cleaned and parsed
		 * @param exact the flag that determines what type of search to perform
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}
		@Override
		public void run() {
			TreeSet<String> query = TextFileStemmer.uniqueStems(line);
			if (!query.isEmpty()) {
				String cleaned = String.join(" ", query);
				synchronized (results) {
					if (results.containsKey(cleaned)) {
						return;
					}
				}
				List<InvertedIndex.SingleSearchResult> list = invertedIndex.search(query, exact);
				synchronized (results) {
					results.put(cleaned, list);
				}
			}
		}
	}
}
