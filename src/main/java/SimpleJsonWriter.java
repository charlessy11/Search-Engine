import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class SimpleJsonWriter {
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Writer writer,
			int level) throws IOException {
		writer.write('[');
		writer.write('\n');
		if (!elements.isEmpty()) {
			Iterator<Integer> iterator = elements.iterator();
			indent(iterator.next().toString(), writer, level + 1);
			while (iterator.hasNext()) {
				writer.write(',');
				writer.write('\n');
				indent(iterator.next().toString(), writer, level + 1);
			}
			writer.write('\n');
			indent("]", writer, level);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer,
			int level) throws IOException {
		writer.write('{');
		writer.write('\n');
		if (!elements.isEmpty()) {
			Iterator<Entry<String, Integer>> iterator = elements.entrySet().iterator();
			var first = iterator.next();
			SimpleJsonWriter.writeObject(first, writer, level + 1);
			while (iterator.hasNext()) {
				var next = iterator.next();
				writer.write(',');
				writer.write('\n');
				SimpleJsonWriter.writeObject(next, writer, level + 1);
			}
			writer.write('\n');
			writer.write('}');
		}
		else {
			writer.write('}');
		}
	}
	
	/**
	 * Writes the entry for asObject function
	 * 
	 * @param entry the entry to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeObject(Entry<String, Integer> entry, Writer writer, int level) throws IOException {
		quote(entry.getKey(), writer, level);
		writer.write(": ");
		writer.write(entry.getValue().toString());
	}

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The
	 * generic notation used allows this method to be used for any type of map
	 * with any type of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(
			Map<String, ? extends Collection<Integer>> elements, Writer writer,
			int level) throws IOException {
		writer.write('{');
		writer.write('\n');
		if (!elements.isEmpty()) {
			var iterator = elements.entrySet().iterator();
			var first = iterator.next();
			SimpleJsonWriter.writeNestedArray(first, writer, level);
			while (iterator.hasNext()) {
				var next = iterator.next();
				writer.write(',');
				writer.write('\n');
				SimpleJsonWriter.writeNestedArray(next, writer, level);
			}
			writer.write('\n');
			indent("}", writer, level - 1);
		}
		else {
			writer.write('}');
		}
	}
	
	/**
	 * Writes the entry for asNestedArray function
	 * 
	 * @param entry the entry to write
	 * @param writer the writer to use
	 * @param level the level to use
	 * @throws IOException if an IO error occurs
	 */
	public static void writeNestedArray(Entry<String, ? extends Collection<Integer>> entry, 
			Writer writer, int level) throws IOException {
		quote(entry.getKey(), writer, level);
		writer.write(": ");
		SimpleJsonWriter.asArray(entry.getValue(), writer, level);
	}
	
	/**
	 * Writes the elements as a pretty JSON object with a nested inverted index.
	 * 
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNested(Map<String, TreeMap<String, Set<Integer>>> elements, Writer writer,
			int level) throws IOException {
		writer.write('{');
		writer.write('\n');
		int curr_size = 0;
		if (curr_size != elements.size()) {
			var iterator = elements.keySet().iterator();
			var first = iterator.next();
			quote(first, writer, level + 1);
			writer.write(": ");
			SimpleJsonWriter.asNestedArray(elements.get(first), writer, level + 2); 
			while (iterator.hasNext()) {
				var next = iterator.next();
				writer.write(',');
				writer.write('\n');
				quote(next, writer, level + 1);
				writer.write(": ");
				SimpleJsonWriter.asNestedArray(elements.get(next), writer, level + 2);
			}
			writer.write('\n');
			writer.write('}');
		}
		else {
			writer.write('}');
		}
	}
	
	/**
	 * Outputs the key results (location, total matches, and score) as as an array of nested JSON objects
	 * 
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asResult(Collection<InvertedIndex.SingleSearchResult> elements, Writer writer, int level) throws IOException {		
		if (!elements.isEmpty()) {
			Iterator<InvertedIndex.SingleSearchResult> iterator = elements.iterator();
			InvertedIndex.SingleSearchResult first = iterator.next();
			SimpleJsonWriter.writeResult(first, writer, level);
			while (iterator.hasNext()) {
				writer.write(',');
				writer.write('\n');
				InvertedIndex.SingleSearchResult next = iterator.next();
				SimpleJsonWriter.writeResult(next, writer, level);
			}
		}
	}
	
	/**
	 * Writes the entry for asResult function
	 * 
	 * @param entry the entry to write
	 * @param writer the writer to use
	 * @param level the level to use
	 * @throws IOException if an IO error occurs
	 */
	public static void writeResult(InvertedIndex.SingleSearchResult entry, Writer writer, int level) throws IOException {
		DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
		indent("{", writer, level + 1);
		writer.write('\n');
		quote("where", writer, level + 2);
		writer.write(": ");
		quote(entry.getLocation(), writer, level - 1);
		writer.write(',');
		writer.write('\n');
		quote("count", writer, level + 2);
		writer.write(": ");
		writer.write(Integer.toString(entry.getMatches()));
		writer.write(',');
		writer.write('\n');
		quote("score", writer, level + 2);
		writer.write(": ");
		writer.write(FORMATTER.format(entry.getScore()));
		writer.write('\n');
		indent("}", writer, level + 1);
	}
	
	/**
	 * Outputs the search results including the key queries as a JSON array of JSON objects
	 * 
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedResult(Map<String, ? extends Collection<InvertedIndex.SingleSearchResult>> elements, Writer writer,
			int level) throws IOException {
		writer.write("{");
		writer.write('\n');
		if (!elements.isEmpty()) {
			var iterator = elements.entrySet().iterator();		
			var entry1 = iterator.next();
			SimpleJsonWriter.writeNestedResult(entry1, writer, level);
			while (iterator.hasNext()) {
				var entry2 = iterator.next();
				writer.write(',');
				writer.write('\n');
				SimpleJsonWriter.writeNestedResult(entry2, writer, level);
			}	
			writer.write('\n');
			writer.write('}');
		}
	}
	
	/**
	 * Writes the entry for asNestedResult function
	 * 
	 * @param entry the entry to write
	 * @param writer the writer to use
	 * @param level the level to use
	 * @throws IOException if an IO error occurs
	 */
	public static void writeNestedResult(
		Entry<String, ? extends Collection<InvertedIndex.SingleSearchResult>> entry, Writer writer, int level) throws IOException {
		quote(entry.getKey(), writer, level + 1);
		writer.write(": ");
		writer.write("[");
		if (!entry.getValue().isEmpty()) {
			writer.write('\n');
			SimpleJsonWriter.asResult(entry.getValue(), writer, level + 1);
		}
		writer.write('\n');
		indent("]", writer, level + 1);
	}
	
	/**
	 * Writes the elements as a pretty JSON nested result to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asNestedResult(
			Map<String, ? extends Collection<InvertedIndex.SingleSearchResult>> elements, Path path) 
			throws IOException {
		try (
				BufferedWriter writer = Files.newBufferedWriter(path,
						StandardCharsets.UTF_8)
		) {
			asNestedResult(elements, writer, 0);
		}
	}
	
	/**
	 * Returns the elements as a nested pretty JSON nested result.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedResult(
			Map<String, ? extends Collection<InvertedIndex.SingleSearchResult>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedResult(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path)
			throws IOException {
		try (
				BufferedWriter writer = Files.newBufferedWriter(path,
						StandardCharsets.UTF_8)
		) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path)
			throws IOException {
		try (
				BufferedWriter writer = Files.newBufferedWriter(path,
						StandardCharsets.UTF_8)
		) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedArray(
			Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		try (
				BufferedWriter writer = Files.newBufferedWriter(path,
						StandardCharsets.UTF_8)
		) {
			asNestedArray(elements, writer, 0);
		}
	}
	
	/**
	 * Writes the elements as as a nested pretty JSON inverted index to file.
	 * 
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void asNested(Map<String, TreeMap<String, 
			Set<Integer>>> elements, Path path) throws IOException {
		try (
				BufferedWriter writer = Files.newBufferedWriter(path,
						StandardCharsets.UTF_8)
		) {
			asNested(elements, writer, 0);
		}	
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedArray(
			Map<String, ? extends Collection<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Returns the elements as a nested pretty JSON inverted index.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 */
	public static String asNested(Map<String, TreeMap<String, Set<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNested(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param level the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(String element, Writer writer, int level)
			throws IOException {
		writer.write("\t".repeat(level));
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "}
	 * quotation marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param level the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void quote(String element, Writer writer, int level)
			throws IOException {
		writer.write("\t".repeat(level));
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}
}