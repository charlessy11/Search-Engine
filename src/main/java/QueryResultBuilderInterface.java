import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The shared interface implemented by the single-threaded and multi-threaded
 * versions of the query result builder class.
 * 
 * @author Charles Sy
 *
 */
public interface QueryResultBuilderInterface {
	
	/**
	 * Opens and reads file line by line then cleans, parses and sorts each query line
	 * 
	 * @param path the path of the file
	 * @param exact to check if exact/partial search
	 * @throws IOException if an IO error occurs
	 */
	public default void parseQuery(Path path, boolean exact) throws IOException {
		try (BufferedReader read = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = read.readLine()) != null) {
				parseQuery(line, exact);
			}
		}
	}
	
	/**
	 * Cleans, parses and sorts each query line
	 * 
	 * @param line the line to be cleaned and parsed
	 * @param exact the flag that determines what type of search to perform
	 */
	public void parseQuery(String line, boolean exact);
	
	/**
	 * Calls SimpleJsonWriter's asNestedResult method
	 * 
	 * @param path the path given by user or default path if otherwise
	 * @throws IOException if an IO error occurs
	 */
	public void toJsonNestedResult(Path path) throws IOException;

}
