import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.nio.charset.StandardCharsets;

/**
 * Builds Inverted Index
 * 
 * @author jett
 *
 */
public class InvertedIndexBuilder {
	
	public static TreeMap<String, Integer> wordCount = new TreeMap<>();
	
	InvertedIndex invertedIndex;
	public InvertedIndexBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}
	/**
	 * Parses stemmed and cleaned words from file then 
	 * adds word, location, and position to inverted index.
	 * 
	 * @param invertedIndex the inverted index to add information into
	 * @param path the path of the file
	 * @throws IOException 
	 */
	public void add(InvertedIndex invertedIndex, Path path) throws IOException {
		//check if path is a regular file
		if (Files.isRegularFile(path)) {
			int position = 1; //position start at index 1
			//get each stemmed and cleaned word from list
			for (String word : TextFileStemmer.listStems(path)) {
				//add word, location, and position to inverted index
				invertedIndex.add(word, path.toString(), position);
				position++;
			}
			if (position > 1) {
				wordCount.put(path.toString(), position-1);
			}
		}
		else if(Files.isDirectory(path)) {
			//get each file from list of text files
			for (Path file : TextFileFinder.list(path)) {
				int position = 1; //position start at index 1
				//get each stemmed and cleaned word from list
				for (String word : TextFileStemmer.listStems(file)) {
					//add word, location, and position to inverted index
					invertedIndex.add(word, file.toString(), position);
					wordCount.put(file.toString(), position);
					position++; //increment counter
				}
			}
		}
	}
	
	Map<String, List<SingleSearchResult>> results = new TreeMap<>();
	
	public void parseQuery(Path path) throws IOException {
		try (BufferedReader read = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = read.readLine()) != null) {
				//assigns set a set of unique, cleaned and stemmed words parsed from line
				TreeSet<String> set = TextFileStemmer.uniqueStems(line);
				if (!set.isEmpty()) {
					//puts a new string from set separated by spaces as key and list of search results as value
					results.put(String.join(" ", set), invertedIndex.exactSearch(set));
				}
			}
		}
		System.out.println(results);
	}	
}