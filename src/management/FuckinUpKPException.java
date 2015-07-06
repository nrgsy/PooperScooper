package management;


public class FuckinUpKPException extends Exception {

	private static final long serialVersionUID = 1L;
	
	//the string to print when thrown
	public FuckinUpKPException(String message) {
		super(message);
		Maintenance.writeLog("***KP***" + message + "***KP***", "KP");
	}
	
}
