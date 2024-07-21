package studiplayer.audio;
import java.io.File;
public abstract class AudioFile {
	String pathname;
	String filename;
	String author;
	String title;
	
	// Default constructor
    public AudioFile() {
        
    }
    
    public AudioFile(String pathname) throws NotPlayableException {
        this.pathname = pathname;
        parsePathname(pathname); // Parse the pathname
        parseFilename(this.filename); // Parse the filename
        
     // Check if the file is readable
        File file = new File(pathname);
        if (!file.canRead()) {
            throw new NotPlayableException("File nicht readable: ", pathname);
        }
    }

	public void parsePathname (String path) {
		//Remove leading and trailing spaces
        path = path.trim();
        
        // Check OS
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            //Windows
            path = path.replace('/', '\\');
        } else {
        	// If OS is Linux
        	if (path.matches("[A-Za-z]:.*")) {
                String drive = path.substring(0, 1);
                path = "/" + drive + path.substring(2); // Insert the drive letter
            }
        	
            path = path.replace('\\', '/');
        }
        
        /* Replace backslashes with forward slashes
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '\\') { //check if char is backslash -> change it
                path = path.substring(0, i) + '/' + path.substring(i + 1);
            }
        }*/
        
        //Replace consecutive slashes with a single slash
        path = path.replaceAll("/{2,}", "/");
        path = path.replaceAll("\\\\{2,}", "\\\\"); 
        
        //save modified path 
        this.pathname = path;
        
        //Extract filename
        int lastslashId;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            lastslashId = path.lastIndexOf('\\');
        } else {
            lastslashId = path.lastIndexOf('/');
        }
 
        if(lastslashId >=0) {
            this.filename = path.substring(lastslashId + 1).trim();
        }else {
            this.filename = path.trim(); // no separator
        }
	}
	
	public void parseFilename(String filename) {
		// Find the position of " - "
		int dashId = filename.indexOf(" - ");
	    
		// Dash is found
	    if (dashId != -1) {
	    	// Extract author and title from delimiter
		    this.author = filename.substring(0, dashId).trim();
	        this.title = filename.substring(dashId + 3).trim(); // Skip " - " and trim spaces
		} else {
			//no dash found
		    this.author = ""; // Set an empty string for author
	        this.title = filename.trim(); //consider the whole filename as the title
	    }
	    
	    // Find the position of the last dot . 
	    int dotId = this.title.lastIndexOf(".");
        if (dotId != -1) {
        	// Remove the file extension from the title	
        	this.title = this.title.substring(0, dotId).trim();
	    }else {
	        // No dot found in the title
	        // Remove leading and trailing spaces from the title
	        this.title = this.title.trim();
	    }
		       
	}
	
	public String getPathname() {
		return pathname;
	}

	public String getFilename() {
		return filename;
	}
	
	public String getAuthor() {
		return author;
		
	}
	
	public String getTitle() {
		return title;
		
	}

	public String toString() {
		String author = this.getAuthor();
	    String title = this.getTitle();

	    if (author == null) {
	        author = "";
	    }

	    if (title == null) {
	        title = "";
	    }

	    if (author.isEmpty() || title.equals("-")) {
	        return title;
	    } else {
	    	// Concatenate
	        return author + " - " + title;
	    }
	}
	
	public abstract void play() throws NotPlayableException;
    public abstract void togglePause();
    public abstract void stop();
    public abstract String formatDuration();
    public abstract String formatPosition();
}
