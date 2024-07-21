package studiplayer.audio;
import studiplayer.basic.WavParamReader;
public class WavFile extends SampledFile{

	public WavFile() {
	}
	
	public WavFile(String path) throws NotPlayableException {
		super(path);
		readAndSetDurationFromFile();
	}
	
	public void readAndSetDurationFromFile() throws NotPlayableException{
		try { 
			// Read the meta info
			WavParamReader.readParams(getPathname());

			//number of frames per second (frame rate)
			float fps = WavParamReader.getFrameRate();

			//number of frames of audio file
			long numberOfFps = WavParamReader.getNumberOfFrames();

			//total playing time and set duration
			this.duration = computeDuration(numberOfFps, fps);
		}catch(Exception e) {
			throw new NotPlayableException(pathname, "Something went wrong while reading parameters", e);
		}
    }

	public static long computeDuration(long numberOfFrames, float frameRate) {
	        //total playing time 
	        long duration = (long) ((numberOfFrames / frameRate) * 1000000);
	        return duration;
	}
	
	@Override
    public String toString() {
        return super.toString() + " - " + formatDuration();
    }

}
