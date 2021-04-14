
// TODO Make this a public non-static inner class inside of inverted index

/**
 * A class that sorts and stores a single search result
 * @author jett
 *
 */
public class SingleSearchResult implements Comparable<SingleSearchResult> {
	// TODO Make private
	/**
	 * The location of the text file
	 */
	public String location; // TODO final
	/**
	 * The total number of word stems in each text file
	 */
	public int count; // TODO Remove and access directly wordCount
	/**
	 * The total number of times any of the matching query words appear in the text file
	 */
	public int matches;
	/**
	 * The percent of words in the file that match the query
	 */
	private double score;
	
	/**
	 * Constructor
	 * 
	 * @param location the location of the text file
	 * @param count the total number of word stems in each text file
	 * @param matches the total number of times any of the matching query words appear in the text file
	 */
	public SingleSearchResult(String location, int count, int matches) {
		this.location = location;
		this.count = count;
		this.matches = matches;
		this.score = (double)matches / (double)count;;
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
	 * @return total word count
	 */
	public int getCount() {
		return count;
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
	 * Setter
	 * 
	 * @param size the word count stored in the index
	 */
	public void setMatches(int size) { // TODO Remove
		matches += size;
		score = (double)matches / (double)count;
	}

	/* TODO 
	private void update(String word) {
		matches += map.get(word).get(where).size();
		score = (double) matches / (double) wordCount.get(where);
	}
	*/
	
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