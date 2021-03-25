import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Set;

/**
 * A nested inverted index to store words, the locations the words were found in, 
 * and the positions in the locations where the words were found in.
 * 
 * @author Charles Sy
 *
 */
public class InvertedIndex {
	// TODO private
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
	public void add(String word, String location, Integer position) {
		map.putIfAbsent(word, new TreeMap<>());
		map.get(word).putIfAbsent(location, new TreeSet<>());
		map.get(word).get(location).add(position);
	}
	
//	public static void count(String location, Integer counter) {
//		TreeMap<String, Integer> wordCount = new TreeMap<>();
//		wordCount.put(location, counter);
//		SimpleJsonWriter.asObject(wordCount, map.getPath("-counts", Path.of("counts.json")));
//	}
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
		if (map.get(word).get(location) != null) {
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
	public boolean contains(String word, String location, Integer position) {
		if (map.containsKey(word) && map.get(word).containsKey(location) && map.get(word).get(location).contains(position)) {
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
		if (map.containsKey(word) && map.get(word).containsKey(location)) {
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
	 * @param invertedIndex the inverted index
	 * @param path the path given by user or default path if otherwise
	 * @throws IOException
	 */
	public static void toJson(InvertedIndex elements, Path path) throws IOException {
		SimpleJsonWriter.asInvertedIndex(elements, path);
	}
	
	public List<SingleSearchResult> exactSearch(TreeSet<String> line) {
		Map<String, SingleSearchResult> temp = new TreeMap<>();
		List<SingleSearchResult> listExact = new ArrayList<>();
		for (String word : line) {
//			var iterator = map.entrySet().iterator();
//			while (iterator.hasNext()) {
//				var entry = iterator.next();
//				if (contains(word)) {
//					for (String path : get(word)) {
//						if (!temp.containsKey(path)) {
////							temp.put(path, new SingleSearchResult(path, InvertedIndexBuilder.wordCount.get(path), get(word, path).size()));
//							SingleSearchResult results = new SingleSearchResult(path, InvertedIndexBuilder.wordCount.get(path), 
//									get(word, path).size());
//							listExact.add(results);
//							temp.put(path, results);
//						} 
//						else {
//							temp.get(path).setMatches(get(word, path).size());
////							temp.get(path).setMatches(map.get(entry.getKey()).get(path).size());
//						}
//					}
//				}
//			}
			//check if word is stored in inverted index
			if (contains(word)) {
				for (String path : get(word)) {
//				//create search result object
//				temp.putIfAbsent(path, new SingleSearchResult(path, InvertedIndexBuilder.wordCount.get(path), get(word, path).size()));
//				temp.get(path).setMatches(get(word, path).size());
					if (!temp.containsKey(path)) {
						temp.put(path, new SingleSearchResult(path, InvertedIndexBuilder.wordCount.get(path), get(word, path).size()));
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
	
	public List<SingleSearchResult> partialSearch(TreeSet<String> line) {
		Map<String, SingleSearchResult> temp = new TreeMap<>();
		List<SingleSearchResult> listPartial = new ArrayList<>();
		for (String word : line) {
//			var iterator = map.entrySet().iterator();
//			while (iterator.hasNext()) {
//				var entry = iterator.next();
//				if (entry.getKey().startsWith(word)) {
//					for (String path : get(word)) {
//						if (!temp.containsKey(path)) {
//							temp.put(path, new SingleSearchResult(path, 
//									InvertedIndexBuilder.wordCount.get(path), get(word, path).size()));
//						} 
//						else {
//							temp.get(path).setMatches(get(word, path).size());
//						}
//					}
//				}
//			}
//		}
			for (var key : map.entrySet()) {
				if (key.getKey().startsWith(word)) {
					for (String path : get(word)) {
						if (!temp.containsKey(path)) {
							temp.put(path, new SingleSearchResult(path, 
									InvertedIndexBuilder.wordCount.get(path), get(word, path).size()));
						}
						else {
							temp.get(path).setMatches(get(word, path).size());
						}
					}
				}
			}
		}
		listPartial = temp.values().stream().collect(Collectors.toList()); //copies values from temp to list
		Collections.sort(listPartial);
		System.out.println(listPartial);
		return listPartial;
	}
	
}