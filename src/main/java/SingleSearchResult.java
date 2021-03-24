
/**
 * A class that sorts and stores a single search result
 * @author jett
 *
 */
public class SingleSearchResult implements Comparable<SingleSearchResult> {

	public String location;
	public int count;
	public int matches;
	
	public SingleSearchResult(String location, int count, int matches) {
		this.location = location;
		this.count = count;
		this.matches = matches;
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
	
	public double updateScore() {
		double score = (double)getMatches() / getCount();
		return score;
	}
	
	public void setMatches(int count) {
		matches += count;
	}
	
	@Override
	public int compareTo(SingleSearchResult other) {
		int result = Double.compare(this.updateScore(), other.updateScore());
		if (result == 0) {
			result = Integer.compare(this.matches, other.matches);
		}
		if (result == 0) {
			result = this.location.compareToIgnoreCase(other.location);
		}
		return result;
	}
	
//	@Override
//	public String toString() {
//		return;
//		
//	}
	
}
