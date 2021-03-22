
/**
 * A class that sorts and stores a single search result
 * @author jett
 *
 */
public class singleSearchResult implements Comparable<Object> {

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
	
	public int setScore() {
		int score = getMatches() / getCount();
		return score;
	}
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
