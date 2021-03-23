
/**
 * A class that sorts and stores a single search result
 * @author jett
 *
 */
public class SingleSearchResult implements Comparable<SingleSearchResult> {

	String location;
	int count;
	int matches;
	
	public String getLocation() {
		return location;
	}
	
	public int getCount() {
		return count;
	}
	
	public int getMatches() {
		return matches;
	}
	
	public double setScore() {
		double score = (double)getMatches() / getCount();
		return score;
	}
	

	@Override
	public int compareTo(SingleSearchResult o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
//	@Override
//	public String toString() {
//		return;
//		
//	}
	
}
