package studiplayer.audio;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ControllablePlayListIterator <T extends AudioFile> implements Iterator<T> {
    //list of audio files
	private List<T> files; 
    //current position
    private int currentIndex;
    private Boolean flag = true;

    //create a new iterator with list of files
    public ControllablePlayListIterator(List<T> files) {
        this.files = new LinkedList<T>(files);
    }
    
    public ControllablePlayListIterator(List<T> files, String search, SortCriterion sort) {
    	List<T> filesFilter = new LinkedList<>();
        
        // Filter list based on the search string
        for (T file : files) {
            if (search == null || search.isEmpty() ||
                file.getAuthor().contains(search) || 
                file.getTitle().contains(search) || 
                ((file instanceof TaggedFile) && ((TaggedFile) file).getAlbum().contains(search))) {
                filesFilter.add(file);
            }
        }

        // Sort filtered list based on the sort criterion
        if (sort != SortCriterion.DEFAULT) {
            Comparator<T> comparator = null;
            if (sort == SortCriterion.TITLE) {
                comparator = Comparator.comparing(AudioFile::getTitle);
            } else if (sort == SortCriterion.DURATION) {
                comparator = Comparator.comparingLong(file -> {
                    if (file instanceof SampledFile) {
                        return ((SampledFile) file).getDuration();
                    } else {
                        return 0L;
                    }
                });
            } else if (sort == SortCriterion.ALBUM) {
                comparator = new AlbumComparator<>(); // Use AlbumComparator for sorting by album
            } else if (sort == SortCriterion.AUTHOR) {
                comparator = new AuthorComparator<>();
            }
            // secondary sorting by title if albums (or authors) are =
            comparator = comparator.thenComparing(Comparator.comparing(AudioFile::getTitle));
            
            if (comparator != null) {
                filesFilter.sort(comparator);
            }
        }

        // Set the filtered and sorted list as the list to iterate over
        this.files = filesFilter;
        this.currentIndex = 0;
    }

    //Check if more files left in the list
    @Override
    public boolean hasNext() {
        return currentIndex +1 < files.size(); // If our current index is less than the size of the list, there are more files
    }

    //next file in the list
    @Override
    public T next() {
    	if (flag)
		{
			flag = false;
			return files.get(currentIndex);
		}
		else
        	return files.get(++currentIndex);
    }

 // Skip to specific file in the list
    public T jumpToAudioFile(AudioFile file) {
        for (T audioFile : files) {
            if (audioFile.equals(file)) {
                return audioFile;
            }
        }
        return null; //not found
    }
    
    public T current() {
    	if (!files.isEmpty())
    		return files.get(currentIndex);
    	return null;
    }
}
