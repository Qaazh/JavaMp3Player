package studiplayer.audio;

public class NotPlayableException extends Exception {
	private String pathname;
	
	public NotPlayableException(String pathname, String message) {
		super(message);
		this.pathname = pathname;
	}
	
	public NotPlayableException(String pathname, Throwable t) {
		super(t);
		this.pathname = pathname;
	}
	
	public NotPlayableException(String pathname, String message, Throwable t) {
		super(message, t);
		this.pathname = pathname;
	}
	
	public String getPathname() {
		return pathname;
	}
	
	@Override
    public String toString() {
        return super.toString() + " (Pathname: " + pathname + ")";
    }

}
