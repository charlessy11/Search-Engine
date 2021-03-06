import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
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
		if (map.get(word).get(location).add(position)) {
			//only update if current value is less than the new one
			wordCount.merge(location, position, Integer::max);
		}
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
	 * Helper method that determines what type of search to perform
	 * 
	 * @param queries the parsed words from a single line of the query file
	 * @param exact the flag that determines what type of search to perform
	 * @return a sorted list of search results
	 */
	public List<SingleSearchResult> search(Set<String> queries, boolean exact) {
		if (exact) {
			return exactSearch(queries);
		} else {
			return partialSearch(queries);
		}
	}
	
	/**
	 * Performs exact search
	 * 
	 * @param queries the parsed words from a single line of the query file
	 * @return sorted list of search results
	 */
	public List<SingleSearchResult> exactSearch(Set<String> queries) {
		/* Keeps track of values added. Necessary to easily lookup values that we've already processed, thus 
		 * eliminating duplicate paths and search results. Searching is faster w/ maps rather than lists. */
		Map<String, SingleSearchResult> check = new HashMap<>(); 
		List<SingleSearchResult> list = new ArrayList<>();
		//for each parsed word from set
		for (String word : queries) {
			if (contains(word)) {
				searchHelper(check, list, word);
			}
		}
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Performs partial search
	 * 
	 * @param queries the parsed words from a single line of the query file
	 * @return sorted list of search results
	 */
	public List<SingleSearchResult> partialSearch(Set<String> queries) {
		Map<String, SingleSearchResult> check = new HashMap<>();
		List<SingleSearchResult> list = new ArrayList<>();
		for (String query : queries) { 
			for (String word : map.tailMap(query).keySet()) {
				if (!word.startsWith(query)) {
					break;
				} 
				searchHelper(check, list, word);
			}
		}
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Helper function that deals with searching
	 * 
	 * @param check the hash map that keeps track of values added
	 * @param list the array list to add a single search result
	 * @param word the stemmed and cleaned word from the query line
	 */
	private void searchHelper(Map<String, SingleSearchResult> check, List<SingleSearchResult> list, String word) {
		//for each location stored in the inverted index
		for (String path : map.get(word).keySet()) {
			//check if map doesn't contain the location
			if (!check.containsKey(path)) {
				SingleSearchResult result = new SingleSearchResult(path);
				check.put(path, result);
				list.add(result);
			}
			//perform a match
			check.get(path).update(word);
		}
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
	
	/**
	 * Convenience method to add all words, location, and position to the inverted index
	 * 
	 * @param words the list of words
	 * @param path the path of the list
	 */
	public void addAll(Collection<String> words, Path path) {
		int position = 1;
		for (String word : words) {
			add(word, path.toString(), position);
			position++;
		}
	}
	
	/**
	 * Merges other inverted index to current inverted index
	 * 
	 * @param other the other inverted index
	 */
	public void addAll(InvertedIndex other) {
		// merge inverted index
		for (String word : other.map.keySet()) {
			if (this.map.containsKey(word)) {
				for (String location : other.map.get(word).keySet()) {
					if (this.map.get(word).containsKey(location)) {
						this.map.get(word).get(location).addAll(other.map.get(word).get(location));
					} else {
						this.map.get(word).put(location, other.map.get(word).get(location));
					}
				}
			} else {
				this.map.put(word, other.map.get(word));
			}
		}
		// merge word count
		for (String location : other.wordCount.keySet()) {
			wordCount.merge(location, other.wordCount.get(location), Integer::max);
		}
	}

	/**
	 * A non-static inner class that sorts and stores a single search result
	 * @author Charles Sy
	 *
	 */
	public class SingleSearchResult implements Comparable<SingleSearchResult> {
		/**
		 * The location of the text file
		 */
		private final String location;
		/**
		 * The total number of times any of the matching query words appear in the text file
		 */
		private int matches;
		/**
		 * The percent of words in the file that match the query
		 */
		private double score;
		
		/**
		 * Constructor
		 * 
		 * @param location the location of the text file
		 */
		public SingleSearchResult(String location) {
			this.location = location;
			this.matches = 0;
		}
		
		/**
		 * Getter
		 * 
		 * @return location
		 */
		public String getLocation() {
			return location;
		}
		
		/**
		 * Getter
		 * 
		 * @return total matches
		 */
		public int getMatches() {
			return matches;
		}
		
		/**
		 * Getter
		 * 
		 * @return score
		 */
		public double getScore() {
			return score;
		}
		

		/**
		 * Updates the amount of matches and calculates the score
		 * 
		 * @param word the word being matched
		 */
		private void update(String word) {
			matches += map.get(word).get(location).size();
			score = (double) matches / (double) wordCount.get(location);
		}
		
		@Override
		public int compareTo(SingleSearchResult other) {
			int result = Double.compare(other.score, this.score);
			if (result == 0) {
				result = Integer.compare(other.matches, this.matches);
			}
			if (result == 0) {
				result = this.location.compareToIgnoreCase(other.location);
			}
			return result;
		}
		
		@Override
		public String toString() {
			return location + " " + matches + " " + score;
		}
	}
}