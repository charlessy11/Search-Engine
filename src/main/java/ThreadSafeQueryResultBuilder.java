import java.io.IOException;
import java.nio.file.Path;

public class ThreadSafeQueryResultBuilder extends QueryResultBuilder{

	/**
	 * The work queue for multithreading
	 */
	private final WorkQueue queue;
	
	public ThreadSafeQueryResultBuilder(ConcurrentInvertedIndex invertedIndex, WorkQueue queue) {
		super(invertedIndex);
		this.queue = queue;
	}
	
	@Override
	public void parseQuery(Path path, boolean exact) throws IOException {
		queue.execute(new Task(path, exact));
	}
	
	@Override
	public void toJsonNestedResult(Path path) throws IOException {
		super.toJsonNestedResult(path);
	}
	
	private class Task implements Runnable {
		
		private final Path path;
		private final boolean exact;
		
		public Task(Path path, boolean exact) {
			this.path = path;
			this.exact = exact;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
}
