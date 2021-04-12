import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

//make thread safe class that "public class Task" inner class that goes through each file
//create local index
//localIndex.addData(file)
//ConInvertedIndex.merge(localIndex)

public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {
	
	private final ConcurrentInvertedIndex index;
	private final WorkQueue queue;
	
	public ThreadSafeInvertedIndexBuilder(ConcurrentInvertedIndex index, WorkQueue queue) {
		super(index);
		this.index = index;
		this.queue = queue;
	}
	
	@Override
	public synchronized void add(Path path) throws IOException {
		super.add(path);
//		queue.finish();
	}
	
	@Override 
	public synchronized void addData(Path path) throws IOException {
//		super.addData(path);
		Task task = new Task(path);
		queue.execute(task);
	}
	
//	WorkQueue task = new WorkQueue();
	
	private class Task implements Runnable {
		private final Path path;
		
		public Task(Path path) {
			this.path = path;
//			task.incrementPending(); 
		}
		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			InvertedIndexBuilder.addData(path, local);
			
		}
		
	}
}
