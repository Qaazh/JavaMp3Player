package studiplayer.audio;
import studiplayer.basic.BasicPlayer;

public abstract class SampledFile extends AudioFile {
	
	long duration;
	public SampledFile() {
		// TODO Auto-generated constructor stub
	}
	
	public SampledFile(String path) throws NotPlayableException {
		super(path);
	}
	
	@Override
    public void play() throws NotPlayableException {
		try {
			// Play file
        	BasicPlayer.play(getPathname());
		}catch(Exception e) {
			throw new NotPlayableException(getPathname(), "Error playing song", e);
		}
    }

    @Override
    public void togglePause() {
        //Pause File
    	BasicPlayer.togglePause();
    }

    @Override
    public void stop() {
        //Stop playback
    	BasicPlayer.stop();
    }
    
    public long getDuration() {
    	return duration;
    }
    
    @Override
    public String formatDuration() {
    	return timeFormatter(getDuration());
    }
    
    @Override
    public String formatPosition() {
    	return timeFormatter(BasicPlayer.getPosition());
    }
    
    public static String timeFormatter(long timeInMicroSeconds) {
    	if (timeInMicroSeconds < 0) {
            throw new RuntimeException("Error negative time value" + timeInMicroSeconds);
        }

        long totSec = timeInMicroSeconds / 1000000;
        long min = totSec / 60;
        long sec = totSec % 60;

        if (min > 99 || sec > 59) {
            throw new RuntimeException("Time error when displaying in \"mm:ss\" format: " + timeInMicroSeconds);
        }

        return String.format("%02d:%02d", min, sec);
    }  
}
