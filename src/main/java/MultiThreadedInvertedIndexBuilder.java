import java.io.IOException;
import java.nio.file.Path;


/**
 * The thread safe and multithreaded inverted index builder.
 * 
 * @author Charles Sy
 *
 */
public class MultiThreadedInvertedIndexBuilder extends InvertedIndexBuilder {
	
	/**
	 * The work queue
	 */
	private final WorkQueue queue;
	
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the thread safe inverted index
	 * @param queue the work queue
	 */
	public MultiThreadedInvertedIndexBuilder(ConcurrentInvertedIndex invertedIndex, WorkQueue queue) {
		super(invertedIndex);
		this.queue = queue;
	}
	
	@Override
	public void add(Path path) throws IOException {
		super.add(path);
		try {
			queue.finish();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		queue.shutdown();
	}
	
	@Override 
	public void addData(Path path) throws IOException {
		//creates first task, gives it to the work queue, and increments pending
		queue.execute(new Task(path));
	}
	
	/**
	 * The non-static task class that provides functionality to threads in the runnable state.
	 * 
	 * @author Charles Sy
	 *
	 */
	private class Task implements Runnable {
		/**
		 * The path of the file
		 */
		private final Path path;
		
		/**
		 * Constructor
		 * 
		 * @param path the path of the file
		 */
		public Task(Path path) {
			this.path = path;
		}
		
		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			try {
				InvertedIndexBuilder.addData(path, local);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
