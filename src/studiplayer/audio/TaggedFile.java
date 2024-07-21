package studiplayer.audio;
import java.util.Map;

import studiplayer.basic.TagReader;

public class TaggedFile extends SampledFile {
	String album;
	
	public TaggedFile() {
		// TODO Auto-generated constructor stub
	}
	
	public TaggedFile(String path) throws NotPlayableException{
		super(path);
		readAndStoreTags();
	}
	
	public String getAlbum() {
		return album != null ? album : "";
	}
	public void setAlbum(String album) {
        this.album = album;
    }
	
	public void readAndStoreTags() throws NotPlayableException {
		try {
			Map<String, Object> tagMap = TagReader.readTags(pathname);
			if (tagMap.containsKey("album")) {
        	    album = ((String) tagMap.get("album")).strip();
        	}
	
			if (tagMap.containsKey("author")) {
        	    author = ((String) tagMap.get("author")).strip();
        	}
	
        	if (tagMap.containsKey("title")) {
        	    title = ((String) tagMap.get("title")).strip();
        	}
	
       	 	if (tagMap.containsKey("duration")) {
        	    duration = (Long) tagMap.get("duration");
        	}
		}catch(Exception e) {
	        throw new NotPlayableException(pathname, "Error trying to read tags", e);
	    }
    }
	
	@Override
    public String toString() {
        String basicAlbumDetail = super.toString();
        if (album != null && !album.isEmpty()) {
            return basicAlbumDetail + " - " + album + " - " + formatDuration();
        } else {
            return basicAlbumDetail + " - " + formatDuration();
        }
    }

}
