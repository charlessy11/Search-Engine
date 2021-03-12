import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 *
 * @see TextParser
 */
public class TextFileStemmer {
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> listStems(String line, Stemmer stemmer) {
		//create arrayList
		ArrayList<String> list = new ArrayList<>();
		//parses line into string array
		String[] parsed = TextParser.parse(line);
		//adds cleaned and stemmed parsed words into arrayList
		for (String word : parsed) {
			list.add((String) stemmer.stem(word)); // TODO Don't downcast... use toString
		}
		return list;
	}
	
	/* TODO 
	public static void stemLine(String line, Stemmer stemmer, Collection<String> stems) {
		String[] parsed = TextParser.parse(line);
		for (String word : parsed) {
			stems.add(stemmer.stem(word).toString());
		}
	}
	*/

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #listStems(String, Stemmer)
	 */
	public static ArrayList<String> listStems(String line) {
		return listStems(line, new SnowballStemmer(DEFAULT));
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> listStems(Path inputFile) throws IOException {
		//open file for reading
		try (BufferedReader read = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			//create arrayList
			ArrayList<String> list = new ArrayList<>();
			// TODO Stemmer stemmer = ...
			String line = null;
			// only 1 line needs to be "in memory" at a time
			while ((line = read.readLine()) != null) {
				// TODO stemLine(line, stemmer, list)
				// TODO Duplicate code and efficiency issue
				//parses line into a string array
				String[] parsed = TextParser.parse(line);
				//adds cleaned and stemmed parsed words into arrayList
				for (String word : parsed) {
					list.addAll(uniqueStems(word)); // TODO Does this work?
				}
			}
			return list;
		}
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(String line) {
		return uniqueStems(line, new SnowballStemmer(DEFAULT));
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		// TODO Duplicate code, same downcast issue
		//create treeSet
		TreeSet<String> set = new TreeSet<>();
		//parse line into string array
		String[] parsed = TextParser.parse(line);
		//adds unique, cleaned and stemmed parsed words into treeSet
		for (String word : parsed) {
			set.add((String) stemmer.stem(word));
		}
		return set;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {
		// TODO Same issues
		//open file for reading
		try (BufferedReader read = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			//create treeSet
			TreeSet<String> set = new TreeSet<>();
			String line = null;
			while ((line = read.readLine()) != null) {
				//parses line into a string array
				String[] parsed = TextParser.parse(line);
				//adds unique, cleaned and stemmed parsed words into treeSet
				for (String word : parsed) {
					set.addAll(uniqueStems(word)); 
				}
			}
			return set;
		}
	}
}