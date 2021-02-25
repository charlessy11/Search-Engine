import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map;
import java.util.Set;

public class InvertedIndex {
	
	/**
	 * Multiple-leveled nested HashMap that serves as an inverted index
	 */
	public final Map <String, Map<String, Set<Integer>>> map;
	
	/**
	 * Constructor defines map
	 */
	public InvertedIndex() {
		this.map = new TreeMap<String, Map<String, Set<Integer>>>(); 
	}
	
	/**
	 * Adds the content, word, and index.
	 *
	 * @param content the content found
	 * @param location the location the word was found
	 * @param position the position the content is found
	 */
	public void add(String content, String location, Integer position) {
		map.putIfAbsent(content, new TreeMap<>());
		map.get(content).putIfAbsent(location, new TreeSet<>());
		map.get(content).get(location).add(position); //adds index
	}
	/**
	 * Returns the number of contents stored in the index.
	 *
	 * @return 0 if the index is empty, otherwise the number of contents in the
	 *         index
	 */
	public int size() {
		return map.size();
	}
	
	/**
	 * Returns the number of locations the content is found in.
	 *
	 * @param content the content to lookup
	 * @return 0 if the location is not in the index or has no words, otherwise
	 *         the number of words stored for that element
	 */
	public int size(String content) {
		return map.get(content).size();
	}
	
	/**
	 * Returns the number of indices found from the location the content is found in.
	 *
	 * @param content the content to lookup
	 * @param location the location to lookup
	 * @return 0 if the location is not in the index or has no words, otherwise
	 *         the number of words stored for that element
	 */
	public int size(String content, String location) {
		return map.get(content).get(location).size();
	}
	
	/**
	 * Determines whether the content is stored in the index.
	 *
	 * @param content the content to lookup
	 * @return {@true} if the location is stored in the index
	 */
	public boolean contains(String content) {
		if (map.containsKey(content)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determines whether the content is stored in the index and the location is
	 * stored for that content.
	 *
	 * @param content the content to lookup
	 * @param location the location in that content to lookup
	 * @return {@true} if the location and word is stored in the index
	 */
	public boolean contains(String content, String location) {
		if (map.containsKey(content) && map.get(content).containsKey(location)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determines whether the content is stored in the index, the location is
	 * stored for that content, and the position is stored for that location
	 *
	 * @param content the content to lookup
	 * @param location the location in that content to lookup
	 * @param position the position in that location where the content is found to lookup
	 * @return {@true} if the location and word is stored in the index
	 */
	public boolean contains(String content, String location, Integer position) {
		if (map.containsKey(content) && map.get(content).containsKey(location) && map.get(content).get(location).contains(position)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns an unmodifiable view of the contents stored in the index.
	 *
	 * @return an unmodifiable view of the contents stored in the index
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<String> get() {
		return Collections.unmodifiableCollection(map.keySet());
	}
	
	/**
	 * Returns an unmodifiable view of the locations stored in the index for the
	 * provided content, or an empty collection if the content is not in the
	 * index.
	 *
	 * @param content the content to lookup
	 * @return an unmodifiable view of the location stored for the content
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<String> get(String context) {
		if (map.containsKey(context)) {
			return Collections.unmodifiableCollection(map.get(context).keySet());
		}
		return Collections.emptySet();
	}
	
	/**
	 * Returns an unmodifiable view of the positions stored in the index for the
	 * provided location where the content is found, or an empty collection if the content is not in the
	 * index.
	 *
	 * @param content the content to lookup
	 * @param location the location to lookup
	 * @return an unmodifiable view of the location stored for the content
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<Integer> get(String context, String location) {
		if (map.get(context).containsKey(location)) {
			return Collections.unmodifiableCollection(map.get(context).get(location));
		}
		return Collections.emptySet();
	}
	
	@Override
	public String toString() {
		return this.map.toString();
	}
	
}
