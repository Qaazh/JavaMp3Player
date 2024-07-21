package studiplayer.audio;

import java.util.Comparator;

public class DurationComparator<T extends AudioFile> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        // Check for null and throw an exception
        if (o1 == null || o2 == null) {
            throw new RuntimeException("Cannot compare null AudioFiles");
        }
        if (o1 instanceof SampledFile && o2 instanceof SampledFile) {
            // Cast AudioFiles to SampledFiles to access getDuration()
            SampledFile s1 = (SampledFile) o1;
            SampledFile s2 = (SampledFile) o2;
            // Compare the durations
            return Long.compare(s1.getDuration(), s2.getDuration());
        } else {
            // If AudioFiles not instances of SampledFile -> durations = 0
            return -1;
        }
    }
}

