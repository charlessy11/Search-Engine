import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.nio.charset.StandardCharsets;

/**
 * Builds Inverted Index
 * 
 * @author jett
 *
 */
public class InvertedIndexBuilder {
	
	public static TreeMap<String, Integer> wordCount = new TreeMap<>();
	/**
	 * Parses stemmed and cleaned words from file then 
	 * adds word, location, and position to inverted index.
	 * 
	 * @param invertedIndex the inverted index to add information into
	 * @param path the path of the file
	 * @throws IOException 
	 */
	public static void add(InvertedIndex invertedIndex, Path path) throws IOException {
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
	
	public static List<Set<String>> parseQuery(Path path) throws IOException {
		List<Set<String>> list = new ArrayList<>();
//		TreeSet<String> set = new TreeSet<>();
		try (BufferedReader read = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = read.readLine()) != null) {
				TreeSet<String> set = TextFileStemmer.uniqueStems(line);
				list.add(set);
//				InvertedIndex.exactSearch(set);
			}
		}
		return list;
	}	
}