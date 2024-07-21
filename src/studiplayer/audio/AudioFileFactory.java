package studiplayer.audio;

public class AudioFileFactory {

	public static AudioFile createAudioFile(String path) throws NotPlayableException {
        // Get the file extension
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i+1);
        }

        // Create the appropriate AudioFile instance based on the file extension
        switch (extension.toLowerCase()) {
            case "wav":
                return new WavFile(path);
            case "ogg":
            case "mp3":
                return new TaggedFile(path);
            default:
                throw new NotPlayableException("Unknown suffix for AudioFile \"", path + "\"");
        }
    }

}
