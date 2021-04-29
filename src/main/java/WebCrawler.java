import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

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
		//fetch
		HtmlFetcher.fetch(url, 3);
		
		//TODO 
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	public void buildData(URL url) throws IOException {
		//creates first task, gives it to the work queue, and increments pending
		queue.execute(new Task(url));
	}
	
	private class Task implements Runnable {
		private final URL url;
		
		public Task(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
