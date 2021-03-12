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
	
	// TODO Throw the exceptions
	// TODO Only the code interacting with the user should output to the console
	
	/**
	 * Parses stemmed and cleaned words from file then 
	 * adds word, location, and position to inverted index.
	 * 
	 * @param invertedIndex the inverted index to add information into
	 * @param path the path of the file
	 */
	public static void add(InvertedIndex invertedIndex, Path path) {
		//check if path is a regular file
		if (Files.isRegularFile(path)) {
			int position = 1; //position start at index 1
			try {
				//get each stemmed and cleaned word from list
				for (String word : TextFileStemmer.listStems(path)) {
					//add word, location, and position to inverted index
					invertedIndex.add(word, path.toString(), position);
					position++; //increment position
				}
			} catch (IOException e) {
				System.out.println("Error: Unable to open file.");
			}
		}
		else if(Files.isDirectory(path)) {
			try {
				//get each file from list of text files
				for (Path file : TextFileFinder.list(path)) {
					int position = 1; //position start at index 1
					//get each stemmed and cleaned word from list
					for (String word : TextFileStemmer.listStems(file)) {
						//add word, location, and position to inverted index
						invertedIndex.add(word, file.toString(), position);
						position++; //increment position
					}
				}
			} catch (IOException e) {
				System.out.println("Error: Unable to open file.");
			}
		}
	}
}