import java.io.IOException;
import java.net.URL;
import java.util.HashSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * @author Charles Sy
 *
 */
public class WebCrawler {
	/**
	 * The work queue
	 */
	private final WorkQueue queue;
	
	/**
	 * The thread safe inverted index
	 */
	private final ConcurrentInvertedIndex invertedIndex;
	
	/**
	 * The set that keeps track of URLs being processed
	 */
	private final HashSet<URL> check;
	
	/**
	 * The maximum limit of URLs to crawl
	 */
	private int max;
	
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the thread safe inverted index
	 * @param queue the work queue
	 */
	public WebCrawler(WorkQueue queue, ConcurrentInvertedIndex invertedIndex) {
		this.queue = queue;
		this.invertedIndex = invertedIndex;
		this.check = new HashSet<>();
		this.max = 0;
	}
	
	/**
	 * Build the inverted index from a seed URL with a finite crawl
	 * 
	 * @param url the seed URL
	 * @param max the total number of URLs to crawl
	 * @throws IOException if an IO error occurs
	 */
	public void build(URL url, int max) throws IOException {
		this.max = max;
		check.add(url);
		
		queue.execute(new Task(url));
		
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
//	public void addURL(URL url) {
//		try {
//			build(url, 50);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * The non-static task class that provides functionality to threads in the runnable state.
	 * 
	 * @author Charles Sy
	 *
	 */
	private class Task implements Runnable {
		
		/**
		 * The URL to process
		 */
		private final URL url;
		
		/**
		 * Constructor
		 * 
		 * @param url the URL to process
		 */
		public Task(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
				String html = HtmlFetcher.fetch(url, 3);
				if (html == null) {
					return;
				}
				//remove any HTML comments and block elements that should not be considered for parsing links
				html = HtmlCleaner.stripBlockElements(html); 
				//gets each valid URL
				synchronized(check) {
					for (URL found : LinkParser.getValidLinks(url, html)) {
						if (check.size() < max && !check.contains(found)) {
							check.add(found);
							queue.execute(new Task(found));
						}
					}
				}
				//remove remaining HTML tags and certain block elements from the provided text
				String cleaned = HtmlCleaner.stripHtml(html);
				//Clean, parse, and stem the resulting text to populate the inverted index 
				InvertedIndex local = new InvertedIndex();
				int counter = 1; //position start at index 1
				Stemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
				for (String word : TextParser.parse(cleaned)) {
					local.add(stemmer.stem(word).toString(), url.toString(), counter);
					counter++;
				}
				invertedIndex.addAll(local);
		}
	}
}
