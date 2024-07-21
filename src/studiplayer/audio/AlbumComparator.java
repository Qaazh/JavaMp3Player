package studiplayer.audio;

import java.util.Comparator;

public class AlbumComparator<T extends AudioFile> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        // Check if AudioFiles are TaggedFile
        if (o1 instanceof TaggedFile && o2 instanceof TaggedFile) {
            TaggedFile t1 = (TaggedFile) o1;
            TaggedFile t2 = (TaggedFile) o2;
            return t1.getAlbum().compareToIgnoreCase(t2.getAlbum());
        } else if (o1 instanceof TaggedFile) {
            // TaggedFile greater than a non-TaggedFile
            return 1;
        } else if (o2 instanceof TaggedFile) {
            // non-TaggedFile less than a TaggedFile
            return -1;
        } else {
            // Both non-TaggedFile equal
            return 0;
        }
    }
}


