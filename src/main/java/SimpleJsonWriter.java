import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
		else {
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
			var entry1 = iterator.next();
			quote(entry1.getKey(), writer, 1);
			writer.write(": ");
			writer.write(entry1.getValue().toString());
			while (iterator.hasNext()) {
				var entry2 = iterator.next();
				writer.write(',');
				writer.write('\n');
				quote(entry2.getKey(), writer, 1);
				writer.write(": ");
				writer.write(entry2.getValue().toString());
			}
			writer.write('\n');
			writer.write('}');
		}
		else {
			writer.write('}');
		}
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
			var entry1 = iterator.next();
			quote(entry1.getKey(), writer, level);
			writer.write(": ");
			SimpleJsonWriter.asArray(entry1.getValue(), writer, level);
			while (iterator.hasNext()) {
				var entry2 = iterator.next();
				writer.write(',');
				writer.write('\n');
				quote(entry2.getKey(), writer, level);
				writer.write(": ");
				SimpleJsonWriter.asArray(entry2.getValue(), writer, level);
			}
			writer.write('\n');
			indent("}", writer, 1);
		}
		else {
			writer.write('}');
		}
	}
	
	/**
	 * Writes the elements as a pretty JSON object with a nested inverted index.
	 * 
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asInvertedIndex(InvertedIndex elements, Writer writer,
			int level) throws IOException {
		writer.write('{');
		writer.write('\n');
		int curr_size = 0;
		if (curr_size != elements.size()) {
			var iterator = elements.get().iterator();		
			var entry1 = iterator.next();
			quote(entry1, writer, 1);
			writer.write(": ");
			SimpleJsonWriter.asNestedArray(elements.map.get(entry1), writer, 2);
			while (iterator.hasNext()) {
				var entry2 = iterator.next();
				writer.write(',');
				writer.write('\n');
				quote(entry2, writer, 1);
				writer.write(": ");
				SimpleJsonWriter.asNestedArray(elements.map.get(entry2), writer, 2);
			}
			writer.write('\n');
			writer.write('}');
		}
		else {
			writer.write('}');
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
	public static void asInvertedIndex(InvertedIndex elements, Path path) throws IOException {
		try (
				BufferedWriter writer = Files.newBufferedWriter(path,
						StandardCharsets.UTF_8)
		) {
			asInvertedIndex(elements, writer, 0);
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
	public static String asInvertedIndex (InvertedIndex elements) {
		try {
			StringWriter writer = new StringWriter();
			asInvertedIndex(elements, writer, 0);
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