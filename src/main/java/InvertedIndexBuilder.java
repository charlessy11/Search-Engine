import java.io.IOException;
import java.nio.file.Path;

public class InvertedIndexBuilder {
	
	/**
	 * Given a regular file, parses stemmed and cleaned words from file then 
	 * adds word, location, and position to inverted index.
	 * 
	 * @param invertedIndex the inverted index to add information into
	 * @param path the path of the file
	 * @throws IOException
	 */
	public static void addInfo_ifRegularFile(InvertedIndex invertedIndex, Path path) throws IOException {
		int position = 1; //position start at index 1
		//get each stemmed and cleaned word from list
		for (String word : TextFileStemmer.listStems(path)) {
			//add word, location, and position to inverted index
			invertedIndex.add(word, path.toString(), position);
			position++; //increment position
		}
	}
	
	/**
	 * Given a directory, parses stemmed and cleaned words from ".txt" and ".text" files then 
	 * adds word, location, and position to inverted index.
	 * 
	 * @param invertedIndex the inverted index to add information into
	 * @param path the path of the file
	 * @throws IOException
	 */
	public static void addInfo_ifDirectory(InvertedIndex invertedIndex, Path path) throws IOException {
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
	}	
}	
