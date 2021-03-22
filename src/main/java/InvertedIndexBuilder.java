import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Builds Inverted Index
 * 
 * @author jett
 *
 */
public class InvertedIndexBuilder {
	
	/** The stemmer to use for the cleaning methods. */
	private static final Stemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
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
	
	public static Collection<String> parseQuery(Path path, Function<String, String> clean, 
			Function<String, String[]> tokenize, Supplier<TreeSet<String>> collector) throws IOException {
		try (
			BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			Stream<String> lines =	reader.lines();
//			Stream<String> lines = Files.newBufferedReader(path).lines();
		) {
			return lines
			.flatMap(line -> Stream.of(tokenize.apply(line)))
			.map(clean)
			.map(word -> (String)stemmer.stem(word)) //stemming each word
			.collect(Collectors.toCollection(collector));
		}
	}
}