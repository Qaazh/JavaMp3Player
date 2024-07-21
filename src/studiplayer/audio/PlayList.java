package studiplayer.audio;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PlayList implements Iterable<AudioFile>{
    // List to store AudioFile references
    private List<AudioFile> audioFiles;
    private String search;
    private SortCriterion sortCriterion;
    // Variable for current position in the playlist
    private ControllablePlayListIterator<AudioFile> iterator;
    Boolean flag = true;

    public PlayList() {
        // Initialize LinkedList + set the current pos to 0
        audioFiles = new LinkedList<AudioFile>();
        setSortCriterion(SortCriterion.DEFAULT);
    }
    
    public PlayList (String m3uPathname) {
    	this();
    	loadFromM3U(m3uPathname);
    }
    // Getter
    public List<AudioFile> getList() {
        return this.audioFiles;
    }

    // Add an AudioFile
    public void add(AudioFile file) {
        this.audioFiles.add(file);
        resetIterator();
    }

    // Remove an AudioFile
    public void remove(AudioFile file) {
        this.audioFiles.remove(file);
        resetIterator();
    }

    // Get number of AudioFiles
    public int size() {
        return this.audioFiles.size();
    }

    // Get current AudioFile
    public AudioFile currentAudioFile() {
    	if (iterator == null)
            return null;    
    	return iterator.current();
    }

    public void nextSong() {
    	if (iterator == null || !iterator.hasNext())
        	resetIterator();
        else
        {
        	if (flag)
        	{
        		flag = false;
        		iterator.next();
        	}
        	iterator.next();
        }
    }
    
    public void saveAsM3U(String pathname) {
        FileWriter writing = null;
        try {
            writing = new FileWriter(pathname);

            // Loop through each AudioFile in list
            for (AudioFile song : audioFiles) {
                // Write pathname of AudioFile to file
                writing.write(song.getPathname() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error while saving the playlist: " + e.getMessage());
        } finally {
            if (writing != null) {
                try {
                    // Close FileWriter
                    writing.close();
                } catch (IOException e) {
                    System.out.println("Error while closing the FileWriter: " + e.getMessage());
                }
            }
        }
    }
    
    public void loadFromM3U(String pathname) {
    	//if the file exists
        File f = new File(pathname);
        if (!f.exists() || f.isDirectory()) {
            throw new RuntimeException("File does not exist: " + pathname);
        }
    	
    	// Empty playlist
        audioFiles.clear();
        resetIterator();
        BufferedReader read = null;
        
        try {
        	read = new BufferedReader(new FileReader(pathname));
            String line;
            while ((line = read.readLine()) != null) {
                // Ignore empty lines and comment lines
                if (!line.trim().isEmpty() && !line.startsWith("#")) {
                    try {
                        // Create AudioFile and add it to playlist
                        AudioFile audioFile = AudioFileFactory.createAudioFile(line);
                        audioFiles.add(audioFile);
                    } catch (NotPlayableException e) {
                        // Output error and continue with next line
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading the playlist: " + e.getMessage());
        }
    }
    
 // Resets iterator to beginning of playlist
    public void resetIterator() {
    	flag = true;
    	this.iterator = new ControllablePlayListIterator<>(audioFiles, search, sortCriterion);
    }

    public SortCriterion getSortCriterion() {
        return sortCriterion;
    }

    public void setSortCriterion(SortCriterion sortCriterion) {
        this.sortCriterion = sortCriterion;
        resetIterator();
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
        resetIterator();
    }

    @Override
    public Iterator<AudioFile> iterator() {
        return new ControllablePlayListIterator<>(audioFiles, search, sortCriterion);
    }

    public void jumpToAudioFile(AudioFile file) {
    	resetIterator();
		while (iterator.hasNext())
            if (iterator.next().equals(file))
                break;
		flag = false;
    }
    public String toString()
	{
		return audioFiles.toString();
	}
}
