import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
	/**
	 * The inverted index to build
	 */
	private final InvertedIndex invertedIndex;
	
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the inverted index
	 */
	public InvertedIndexBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}
	
	/**
	 * Checks for path type then adds data
	 * 
	 * @param path the path of the file
	 * @throws IOException if an IO error occurs
	 */
	public void add(Path path) throws IOException {
		//check if path is a regular file
		if (Files.isRegularFile(path)) {
			addData(path);
		}
		//check if path is a directory
		else if(Files.isDirectory(path)) {
			//get each file from list of text files
			for (Path file : TextFileFinder.list(path)) {
				addData(file);
			}
		}
	}
	
	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * then adds word, location, and position to inverted index.
	 * 
	 * @param path the path of the file
	 * @throws IOException if an IO error occurs
	 */
	public void addData(Path path) throws IOException {
		//open file for reading
		try (BufferedReader read = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			int position = 1; //position start at index 1
			Stemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			String line = null;
			while ((line = read.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					//add to inverted index
					invertedIndex.add(stemmer.stem(word).toString(), path.toString(), position);
					position++;
				}
			}
		}
	}
}