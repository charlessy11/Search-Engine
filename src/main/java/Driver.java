import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		Instant start = Instant.now();

		ArgumentMap map = new ArgumentMap(args); //parses command-line arguments
		InvertedIndex invertedIndex;
		InvertedIndexBuilder indexBuilder;
		QueryResultBuilderInterface resultBuilder;
		WebCrawler crawler;
		
		int workerThreads = 0;
		WorkQueue queue = null;
		ConcurrentInvertedIndex threadSafe = new ConcurrentInvertedIndex();
		
		URL seed = null;
		int total = 0;
		
		//perform multithreading
		if (map.hasFlag("-html")) {
			try {
				if (map.hasFlag("-threads")) {
					workerThreads = map.getInteger("-threads", 5);
					if (workerThreads <= 0) {
						workerThreads = 5; //default value
					}
				} 
			} catch (NumberFormatException e) {
				System.out.println("Warning: Invalid input for amount of worker threads.");
				workerThreads = 5;
			}
			try {
				seed = new URL(map.getString("-html"));
				//optional flag
				if (map.hasFlag("-max")) {
					total = map.getInteger("-max", 1);
				}
			} catch (MalformedURLException e) {
				System.out.println("Warning: A malformed URL has occured.");
			} catch (NumberFormatException e) {
				System.out.println("Warning: Invalid input for total number of URLs to crawl.");
				total = 1;
			}
			//initialize workQueue to num of worker threads
			queue = new WorkQueue(workerThreads);
//			ConcurrentInvertedIndex threadSafe = new ConcurrentInvertedIndex();
			crawler = new WebCrawler(queue, threadSafe);
			try {
				crawler.build(seed, total);
			} catch (IOException e) {
				System.out.println("Error: Unable to crawl the web.");
			}
			//initialize invertedIndex to use thread safe version
			invertedIndex = threadSafe;
			//initialize inverted index builder to use thread safe version and work queue
			indexBuilder = new MultithreadedInvertedIndexBuilder(threadSafe, queue);
			//initialize query result builder to use thread safe version and work queue
			resultBuilder = new MultithreadedQueryResultBuilder(threadSafe, queue);
		}
		else {
			//perform single-threading
			invertedIndex = new InvertedIndex();
			indexBuilder = new InvertedIndexBuilder(invertedIndex);
			resultBuilder = new QueryResultBuilder(invertedIndex);
			crawler = new WebCrawler(queue, threadSafe);
		}
		
		//check whether "-text path" flag, value pair exists
		if (map.hasFlag("-text") && map.hasValue("-text")) {
			try {
				indexBuilder.add(map.getPath("-text"));
			} catch (IOException e) {
				System.out.println("Error: Unable to add data to the inverted index.");
			}
		}
		else if (map.hasFlag("-text") && !map.hasValue("-text")) {
			System.out.println("Warning: No value given to -text flag");
		}
		
		//check for optional flag
		if (map.hasFlag("-index")) {
			try {
				//use given path (or index.json as the default output path) to print inverted index as JSON
				invertedIndex.toJsonInvertedIndex(map.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				if (map.hasValue("-index")) {
					System.out.println("Error: Unable to write the inverted index to file: " + map.getPath("-index").toString());
				}
				else {
					System.out.println("Error: Unable to write the inverted index to file: " + Path.of("index.json").toString());
				}
			}
		}
		
		//optional flag to output all of the locations and their word count
		if (map.hasFlag("-counts")) {
			try {
				//use given path (or counts.json as the default output path) to print pretty JSON
				invertedIndex.toJsonObject(map.getPath("-counts", Path.of("counts.json")));
			} catch (IOException e) {
				System.out.println("Error: Unable to calculate total amount of stemmed words.");
			}
		}
				
		//indicates that a search should be performed
		if (map.hasFlag("-query") && map.hasValue("-query")) {
			try {
				//optional flag that determines if search performed must be exact/partial
				resultBuilder.parseQuery(map.getPath("-query"), map.hasFlag("-exact"));
			} catch (IOException e) {
				System.out.println("Error: No search performed.");
			}
		}
				
		//optional flag that indicates the next argument is the path to use for the search results output file
		if (map.hasFlag("-results")) {
			try {
				resultBuilder.toJsonNestedResult(map.getPath("-results", Path.of("results.json")));
			} catch (IOException e) {
				System.out.println("Warning: No output file produced of search results but still performed the search operation..");
			} 
		}
		
		
		int port;
		//indicates a search engine web server should be launched 
		if (map.hasFlag("-server")) {
			try {
				port = map.getInteger("-server", 8080);
			} catch (NumberFormatException e) {
				System.out.println("Warning: Invalid Port Number.");
				port = 8080;
			}
			
			try {
				Server server = new Server(port);
//				
//				ServletContextHandler servletContextHandler = null;
//				
//				servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//				servletContextHandler.setContextPath("/servlets");
//
//				DefaultHandler defaultHandler = new DefaultHandler();
//				defaultHandler.setServeIcon(true);
//
//				ContextHandler contextHandler = new ContextHandler("/favicon.ico");
//				contextHandler.setHandler(defaultHandler);

				SearchServlet searchServlet = new SearchServlet(resultBuilder, invertedIndex, indexBuilder, crawler);
				ServletHolder servletHolder = new ServletHolder(searchServlet);

				ServletHandler servletHandler = new ServletHandler();
				servletHandler.addServletWithMapping(servletHolder, "/");


				server.setHandler(servletHandler);
				server.start();
				server.join();

			} catch (Exception e) {
				System.out.println("Jetty server failed because " + e.getMessage());
			}
		}
		
		if (queue != null) { 
			queue.shutdown(); 
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}