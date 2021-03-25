
/**
 * A class that sorts and stores a single search result
 * @author jett
 *
 */
public class SingleSearchResult implements Comparable<SingleSearchResult> {

	public String location;
	public int count;
	public int matches;
	private double score;
	
	public SingleSearchResult(String location, int count, int matches) {
		this.location = location;
		this.count = count;
		this.matches = matches;
		this.score = (double)matches / (double)count;;
	}
	
	public String getLocation() {
		return location;
	}
	
	public int getCount() {
		return count;
	}
	
	public int getMatches() {
		return matches;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setMatches(int size) {
		matches += size;
		score = (double)matches / (double)count;
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
