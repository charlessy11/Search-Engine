import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
	InvertedIndex invertedIndex; // TODO Missing keywords 
	
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the inverted index
	 */
	public InvertedIndexBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}
	
	// TODO Why should the method below take in the inverted index if you save it as a parameter of the class?
	/**
	 * Parses stemmed and cleaned words from file then 
	 * adds word, location, and position to inverted index.
	 * 
	 * @param invertedIndex the inverted index to add information into
	 * @param path the path of the file
	 * @throws IOException if an IO error occurs
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
		}
		else if(Files.isDirectory(path)) {
			//get each file from list of text files
			for (Path file : TextFileFinder.list(path)) {
				int position = 1; //position start at index 1
				//get each stemmed and cleaned word from list
				for (String word : TextFileStemmer.listStems(file)) {
					//add word, location, and position to inverted index
					invertedIndex.add(word, file.toString(), position);
					position++; //increment counter
				}
			}
		}
	}
	
	/*
	 * TODO Move the duplicate code above into a separate method.
	 */
	
	/*
	 * TODO Whenever you move data from one data structure (like a list) into
	 * another data structure (like an inverted index), there is an efficiency
	 * issue caused by the extra time and space the copy operation takes.
	 *
	 * This is a classic case where reusing your general code (in this case from
	 * TextFileStemmer) is not going to be the most efficient way forward. It is
	 * for efficiency reasons that we often have to create a less-general solution.
	 *
	 * Copy over some of the parsing and stemming logic here so as soon as you have
	 * a stemmed word, you add it directly to the inverted index instead of a list.
	 * Keep TextFileStemmer around, it will be useful again soon.
	 */

}