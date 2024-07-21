package studiplayer.audio;

import java.util.Comparator;

public class AuthorComparator<T extends AudioFile> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        // Check for null, throw an exception
        if (o1 == null || o2 == null) {
            throw new RuntimeException("Cannot compare null AudioFiles");
        }
        // Compare the authors
        return o1.getAuthor().compareToIgnoreCase(o2.getAuthor());
    }
}



