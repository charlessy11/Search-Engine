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
	
//	private final ArrayList<URL> list;
	
	private final HashSet<URL> check;
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the thread safe inverted index
	 * @param queue the work queue
	 */
	public WebCrawler(WorkQueue queue, ConcurrentInvertedIndex invertedIndex) {
		this.queue = queue;
		this.invertedIndex = invertedIndex;
//		this.list = new ArrayList<>();
		this.check = new HashSet<>();
	}
	
	/**
	 * Build the inverted index from a seed URL with a finite crawl
	 * 
	 * @param url the seed URL
	 * @param max the total number of URLs to crawl
	 * @throws IOException if an IO error occurs
	 */
	public void build(URL url, int max) throws IOException {
		check.add(url);
		
		queue.execute(new Task(url, max));
		
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
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
//		private final String html;
		
		/**
		 * The URL the URL to process
		 */
		private final URL url;
		
		private final int max;
		
		/**
		 * Constructor
		 * 
		 * @param html the HTML to clean
		 * @param url the URL to process
		 */
		public Task(URL url, int max) {
//			this.html = html;
			this.url = url;
			this.max = max;
		}

		@Override
		public void run() {
			for (int i = 0; i < max; i++) {
				String html = HtmlFetcher.fetch(url, 3);
//				System.out.println(html);
//				if (fetched == null) {
//					continue;
//				}
				html = HtmlCleaner.stripBlockElements(html); 
//				System.out.println(html);

				for (URL found : LinkParser.getValidLinks(url, html)) {
					synchronized(check) {
						if (!check.contains(found)) {
//							synchronized(check) {
							check.add(found);
							queue.execute(new Task(found, max - 1));
//							}
						}
					}
				}
				//remove remaining HTML tags and certain block elements from the provided text
				String cleaned = HtmlCleaner.stripHtml(html);
//				System.out.println(cleaned);
				
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
}
