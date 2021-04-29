import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeSet;

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
	
	public void build(URL url) throws IOException {
		//call link parser
		//using sockets to download the HTML
		HttpsFetcher.openConnection(url);
		//fetch
		HtmlFetcher.fetch(url, 3);
		
		//TODO 
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	public void buildData(String html, URL url) throws IOException {
		//creates first task, gives it to the work queue, and increments pending
		queue.execute(new Task(html, url));
	}
	
	private class Task implements Runnable {
		private final String html;
		
		private final URL url;
		
		public Task(String html, URL url) {
			this.html = html;
			this.url = url;
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			//remove any HTML unnecessary comments and block elements
			String stripped = HtmlCleaner.stripBlockElements(html);
			//parses remaining URLs and add to the list
			ArrayList<URL> list = LinkParser.getValidLinks(url, stripped);
			//remove remaining HTML tags and certain block elements from the provided text
			String cleaned = HtmlCleaner.stripHtml(stripped);
			
			int counter = 1; //position start at index 1
			Stemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			for (String word : TextParser.parse(cleaned)) {
				local.add(stemmer.stem(word).toString(), url.toString(), counter);
			}
			
			invertedIndex.addAll(local);
			
		}
		
	}
}
