import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.Set;

/**
 * A nested inverted index to store words, the locations the words were found in, 
 * and the positions in the locations where the words were found in.
 * 
 * @author Charles Sy
 *
 */
public class InvertedIndex {
	/**
	 * Multiple-leveled nested TreeMap that serves as an inverted index
	 */
	private final TreeMap<String, TreeMap<String, Set<Integer>>> map;
	
	/**
	 * Stores word count
	 */
	private final TreeMap<String, Integer> wordCount;
	
	/**
	 * Constructor defines map
	 */
	public InvertedIndex() {
		this.map = new TreeMap<String, TreeMap<String, Set<Integer>>>(); 
		this.wordCount =  new TreeMap<>();
	}
	
	/**
	 * Adds the word, location, and position.
	 *
	 * @param word the word found
	 * @param location the location the word was found
	 * @param position the position the word was found in the location
	 */
	public void add(String word, String location, Integer position) {
		map.putIfAbsent(word, new TreeMap<>());
		map.get(word).putIfAbsent(location, new TreeSet<>());
		map.get(word).get(location).add(position);
		wordCount.put(location, position);
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
		if (map.get(word) != null) {
			return map.get(word).size();
		}
		return -1;
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
		if (contains(word, location)) {
			return map.get(word).get(location).size();
		}
		return -1;
	}
	
	/**
	 * Determines whether the word is stored in the index.
	 *
	 * @param word the word to lookup
	 * @return {@true} if the location is stored in the index
	 */
	public boolean contains(String word) {
		return map.containsKey(word);
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
		return (map.containsKey(word) && map.get(word).containsKey(location));
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
	public boolean contains(String word, String location, Integer position) {
		return (contains(word, location) && map.get(word).get(location).contains(position));
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
	public Collection<String> get(String word) {
		if (map.containsKey(word)) {
			return Collections.unmodifiableCollection(map.get(word).keySet());
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
		if (contains(word, location)) { 
			return Collections.unmodifiableCollection(map.get(word).get(location));
		}
		return Collections.emptySet();
	}
	
	@Override
	public String toString() {
		return this.map.toString();
	}
	
	/**
	 * Calls SimpleJsonWriter's asInvertedIndex method
	 * 
	 * @param path the path given by user or default path if otherwise
	 * @throws IOException if an IO error occurs
	 */
	public void toJsonInvertedIndex(Path path) throws IOException {
		SimpleJsonWriter.asNested(map, path);
	}
	
	
	/**
	 * Performs exact search
	 * 
	 * @param line the parsed words from a single line of the query file
	 * @return sorted list of search results
	 */
	public List<SingleSearchResult> exactSearch(TreeSet<String> line) {
		Map<String, SingleSearchResult> temp = new TreeMap<>();
		List<SingleSearchResult> listExact = new ArrayList<>();
		for (String word : line) {
			//check if word is stored in inverted index
			if (contains(word)) {
				for (String path : get(word)) {
					if (!temp.containsKey(path)) {
						temp.put(path, 
								new SingleSearchResult(path, wordCount.get(path), get(word, path).size()));
					}
					else {
						temp.get(path).setMatches(get(word, path).size());
					}
				}
			}
		}
		listExact = temp.values().stream().collect(Collectors.toList()); //copies values from temp to list
		Collections.sort(listExact);
		return listExact;
	}
	
	/**
	 * Performs partial search
	 * 
	 * @param line the parsed words from a single line of the query file
	 * @return sorted list of search results
	 */
	public List<SingleSearchResult> partialSearch(TreeSet<String> line) {
		Map<String, SingleSearchResult> temp = new TreeMap<>();
		List<SingleSearchResult> listPartial = new ArrayList<>();
		for (String word : line) {
			var iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				var entry = iterator.next();
				if (entry.getKey().startsWith(word)) {
					for (String path : get(entry.getKey())) {
						if (!temp.containsKey(path)) {
							temp.put(path, new SingleSearchResult(path, 
									wordCount.get(path), get(entry.getKey(), path).size()));
						} 
						else {
							temp.get(path).setMatches(get(entry.getKey(), path).size());
						}
					}
				}
			}
		}
		listPartial = temp.values().stream().collect(Collectors.toList()); //copies values from temp to list
		Collections.sort(listPartial);
		return listPartial;
	}
	
	/**
	 * Calls SimpleJsonWriter's asObject method
	 * 
	 * @param path the path given by user or default path if otherwise
	 * @throws IOException if an IO error occurs
	 */
	public void toJsonObject(Path path) throws IOException {
		SimpleJsonWriter.asObject(wordCount, path);
	}
}