import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A thread-safe version of IndvertedIndex using a custom read/write lock.
 * 
 * @author Charles Sy
 *
 */
public class ConcurrentInvertedIndex extends InvertedIndex {
	/** The lock used to protect concurrent access to the underlying inverted index. */
	private final SimpleReadWriteLock lock;
	
	/**
	 * Constructor calls super class's constructor and initializes lock
	 */
	public ConcurrentInvertedIndex() {
		super();
		lock = new SimpleReadWriteLock();
	}
	
	@Override
	public void add(String word, String location, Integer position) {
		lock.writeLock().lock();

		try {
			super.add(word, location, position);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void addAll(Collection<String> words, Path path) {
		lock.writeLock().lock();

		try {
			super.addAll(words, path);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void addAll(InvertedIndex other) {
		lock.writeLock().lock();

		try {
			super.addAll(other);;
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public int size() {
		lock.readLock().lock();

		try {
			return super.size();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int size(String word) {
		lock.readLock().lock();

		try {
			return super.size(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int size(String word, String location) {
		lock.readLock().lock();

		try {
			return super.size(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean contains(String word) {
		lock.readLock().lock();

		try {
			return super.contains(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean contains(String word, String location) {
		lock.readLock().lock();

		try {
			return super.contains(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean contains(String word, String location, Integer position) {
		lock.readLock().lock();

		try {
			return super.contains(word, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Collection<String> get() {
		lock.readLock().lock();

		try {
			return super.get();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Collection<String> get(String word) {
		lock.readLock().lock();

		try {
			return super.get(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Collection<Integer> get(String word, String location) {
		lock.readLock().lock();

		try {
			return super.get(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public String toString() {
		lock.readLock().lock();

		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public List<SingleSearchResult> exactSearch(Set<String> line) {
		lock.readLock().lock();

		try {
			return super.exactSearch(line);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public List<SingleSearchResult> partialSearch(Set<String> line) {
		lock.readLock().lock();

		try {
			return super.partialSearch(line);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void toJsonInvertedIndex(Path path) throws IOException {
		lock.readLock().lock();
		
		try {
			super.toJsonInvertedIndex(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void toJsonObject(Path path) throws IOException {
		lock.readLock().lock();
		
		try {
			super.toJsonObject(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}
}
