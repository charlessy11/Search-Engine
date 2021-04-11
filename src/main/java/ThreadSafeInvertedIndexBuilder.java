import java.io.IOException;
import java.nio.file.Path;

//make thread safe class that "public class Task" inner class that goes through each file
//create local index
//localIndex.addData(file)
//ConInvertedIndex.merge(localIndex)

public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {
	
	private final WorkQueue queue;
	
	public ThreadSafeInvertedIndexBuilder(WorkQueue queue, InvertedIndex invertedIndex) {
		super(invertedIndex);
		this.queue = queue;
	}
	
	@Override
	public synchronized void add(Path path) throws IOException {
		super.add(path);
	}
	
	@Override 
	public synchronized void addData(Path path) throws IOException {
		super.addData(path);
	}
}
