import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
	 * Constructor
	 * 
	 * @param invertedIndex the thread safe inverted index
	 * @param queue the work queue
	 */
	public WebCrawler(WorkQueue queue, ConcurrentInvertedIndex invertedIndex) {
		this.queue = queue;
		this.invertedIndex = invertedIndex;
	}
	
	/**
	 * Build the inverted index from a seed URL with a finite crawl
	 * 
	 * @param url the seed URL
	 * @param max the total number of URLs to crawl
	 * @throws IOException if an IO error occurs
	 */
	public void build(URL url, int max) throws IOException {
		ArrayList<URL> list = new ArrayList<URL>();
		HashSet<String> check = new HashSet<String>();
		
		list.add(url);
		check.add(url.toString());
		
		for (int i = 0; i < list.size() && i < max; i++) {
			URL current = list.get(i);
			String html = HtmlFetcher.fetch(current, 3);
			if (html == null) {
				continue;
			}
			html = HtmlCleaner.stripBlockElements(html); 
			queue.execute(new Task(html, current));
			ArrayList<URL> temp = LinkParser.getValidLinks(current, html);
			for (URL found : temp) {
				if (!check.contains(found.toString())) {
					list.add(found);
					check.add(found.toString());
				}
			}
		}
		
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * The method that allows the queue to execute.
	 * 
	 * @param html the HTML to clean
	 * @param url the URL to process
	 * @throws IOException if an IO error occurs
	 */
//	public void buildData(String html, URL url) throws IOException {
//		//creates first task, gives it to the work queue, and increments pending
////		queue.execute(new Task(html, url));
//	}
	
	/**
	 * The non-static task class that provides functionality to threads in the runnable state.
	 * 
	 * @author Charles Sy
	 *
	 */
	private class Task implements Runnable {
		/**
		 * The HTML to clean
		 */
		private final String html;
		
		/**
		 * The URL the URL to process
		 */
		private final URL url;
		
		/**
		 * Constructor
		 * 
		 * @param html the HTML to clean
		 * @param url the URL to process
		 */
		public Task(String html, URL url) {
			this.html = html;
			this.url = url;
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();

			//remove remaining HTML tags and certain block elements from the provided text
			String cleaned = HtmlCleaner.stripHtml(html);
			
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
