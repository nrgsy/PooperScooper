package management;
	
	public class GlobalStuff{
	
		//Non-volatile globals
		//these do not get updated by the set globals function 
		public static long DAY_IN_MILLISECONDS = 86400000;
		public static long WEEK_IN_MILLISECONDS = 604800000;
		
		//Volatile globals
		//TODO These are set/updated by the setGlobalVars function in databasehandler which reads the values
		//from the global vars collection (which we can manually edit to tweak the values) and sets the values
		//of these to match thoses
		public static long FOLLOW_TIME_MIN = 86400L;
		public static long FOLLOW_TIME_MAX = 123430L;
		public static long POST_TIME_MIN = 900000L;
		public static long POST_TIME_MAX = 1500000L;
		public static long FOLLOW_TIME_INCUBATED_MIN = 180000L;
		public static long FOLLLOW_TIME_INCUBATED_MAX = 240000L;

		

	
	}