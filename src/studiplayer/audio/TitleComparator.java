package studiplayer.audio;

import java.util.Comparator;

public class TitleComparator<T extends AudioFile> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        // Check null values and throw an exception
        if (o1 == null || o2 == null) {
            throw new RuntimeException("Cannot compare null AudioFiles");
        }
        // Compare the titles
        return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    }
}

