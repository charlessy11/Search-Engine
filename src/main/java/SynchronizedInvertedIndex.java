import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * A thread-safe version of IndvertedIndex using the synchronized keyword.
 * 
 * @author Charles Sy
 *
 */
public class SynchronizedInvertedIndex extends InvertedIndex {
	
	/**
	 * Constructor calls super class's constructor
	 */
	public SynchronizedInvertedIndex() {
		super();
	}
	
	@Override
	public synchronized void add(String word, String location, Integer position) {
		super.add(word, location, position);
	}
	
	@Override
	public synchronized int size() {
		return super.size();
	}
	
	@Override
	public synchronized int size(String word) {
		return super.size(word);
	}
	
	@Override
	public synchronized int size(String word, String location) {
		return super.size(word, location);
	}
	
	@Override
	public synchronized boolean contains(String word) {
		return super.contains(word);
	}
	
	@Override
	public synchronized boolean contains(String word, String location) {
		return super.contains(word, location);
	}
	
	@Override
	public synchronized boolean contains(String word, String location, Integer position) {
		return super.contains(word, location, position);
	}
	
	@Override
	public synchronized Collection<String> get() {
		return super.get();
	}
	
	@Override
	public synchronized Collection<String> get(String word) {
		return super.get(word);
	}
	
	@Override
	public synchronized Collection<Integer> get(String word, String location) {
		return super.get(word, location);
	}
	
	@Override
	public synchronized String toString() {
		return super.toString();
	}
	
	@Override
	public synchronized void toJsonInvertedIndex(Path path) throws IOException {
		super.toJsonInvertedIndex(path);
	}
	
	@Override
	public synchronized List<SingleSearchResult> exactSearch(TreeSet<String> line) {
		return super.exactSearch(line);
	}
	
	@Override
	public synchronized List<SingleSearchResult> partialSearch(TreeSet<String> line) {
		return super.partialSearch(line);
	}
	
	@Override
	public synchronized void toJsonObject(Path path) throws IOException {
		super.toJsonObject(path);
	}
}
