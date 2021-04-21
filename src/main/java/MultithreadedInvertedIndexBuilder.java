import java.io.IOException;
import java.nio.file.Path;


/**
 * The thread safe and multithreaded inverted index builder.
 * 
 * @author Charles Sy
 *
 */
public class MultithreadedInvertedIndexBuilder extends InvertedIndexBuilder {
	
	/**
	 * The work queue
	 */
	private final WorkQueue queue;
	
	// TODO private final ConcurrentInvertedIndex invertedIndex;
	
	/**
	 * Constructor
	 * 
	 * @param invertedIndex the thread safe inverted index
	 * @param queue the work queue
	 */
	public MultithreadedInvertedIndexBuilder(ConcurrentInvertedIndex invertedIndex, WorkQueue queue) {
		super(invertedIndex);
		this.queue = queue;
		// TODO this.invertedIndex = ...
	}
	
	@Override
	public void add(Path path) throws IOException {
		super.add(path);
		try {
			queue.finish();
		} catch (InterruptedException e) {
			e.printStackTrace(); // TODO Thread.currentThread().interrupt()
		}
		queue.shutdown(); // TODO finish(), call shutdown in the context you create the work queue
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
			// TODO Now what? All of the data is in local, not in invertedIndex
			// TODO invertedIndex.addAll(local);
		}
	}
}
