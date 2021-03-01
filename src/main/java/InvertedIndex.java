import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map;
import java.util.Set;

public class InvertedIndex {
	
	/**
	 * Multiple-leveled nested TreeMap that serves as an inverted index
	 */
	public final Map <String, Map<String, Set<Integer>>> map;
	
	/**
	 * Constructor defines map
	 */
	public InvertedIndex() {
		this.map = new TreeMap<String, Map<String, Set<Integer>>>(); 
	}
	
	/**
	 * Adds the word, location, and position.
	 *
	 * @param word the word found
	 * @param location the location the word was found
	 * @param position the position the word was found in the location
	 */
	public void add(String content, String location, Integer position) {
		map.putIfAbsent(content, new TreeMap<>());
		map.get(content).putIfAbsent(location, new TreeSet<>());
		map.get(content).get(location).add(position); //adds index
	}
	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return 0 if the index is empty, otherwise the number of contents in the
	 *         index
	 */
	public int size() {
		return map.size();
	}
	
	/**
	 * Returns the number of locations the word was found in.
	 *
	 * @param word the word to lookup
	 * @return 0 if the location is not in the index or has no words, otherwise
	 *         the number of words stored for that element
	 */
	public int size(String word) {
		return map.get(word).size();
	}
	
	/**
	 * Returns the number of positions found from the location the word was found in.
	 *
	 * @param word the word to lookup
	 * @param location the location to lookup
	 * @return 0 if the location is not in the index or has no words, otherwise
	 *         the number of words stored for that element
	 */
	public int size(String word, String location) {
		return map.get(word).get(location).size();
	}
	
	/**
	 * Determines whether the word is stored in the index.
	 *
	 * @param word the word to lookup
	 * @return {@true} if the location is stored in the index
	 */
	public boolean contains(String word) {
		if (map.containsKey(word)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determines whether the word is stored in the index and the location is
	 * stored for that word.
	 *
	 * @param word the word to lookup
	 * @param location the location in that word to lookup
	 * @return {@true} if the location and word is stored in the index
	 */
	public boolean contains(String word, String location) {
		if (map.containsKey(word) && map.get(word).containsKey(location)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determines whether the word is stored in the index, the location is
	 * stored for that word, and the position is stored for that location
	 *
	 * @param word the word to lookup
	 * @param location the location in that word to lookup
	 * @param position the position in that location where the word was found to lookup
	 * @return {@true} if the location and word is stored in the index
	 */
	public boolean contains(String content, String location, Integer position) {
		if (map.containsKey(content) && map.get(content).containsKey(location) && map.get(content).get(location).contains(position)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns an unmodifiable view of the words stored in the index.
	 *
	 * @return an unmodifiable view of the words stored in the index
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<String> get() {
		return Collections.unmodifiableCollection(map.keySet());
	}
	
	/**
	 * Returns an unmodifiable view of the locations stored in the index for the
	 * provided word, or an empty collection if the word is not in the
	 * index.
	 *
	 * @param word the word to lookup
	 * @return an unmodifiable view of the locations stored for the word
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
	 * provided location where the word is found, or an empty collection if the word is not in the
	 * index.
	 *
	 * @param word the word to lookup
	 * @param location the location to lookup
	 * @return an unmodifiable view of the positions stored for the locations the word is found in
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<Integer> get(String word, String location) {
		if (map.get(word).containsKey(location)) {
			return Collections.unmodifiableCollection(map.get(word).get(location));
		}
		return Collections.emptySet();
	}
	
	@Override
	public String toString() {
		return this.map.toString();
	}
}
