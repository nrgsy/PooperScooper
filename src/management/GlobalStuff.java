package management;
	
	public class GlobalStuff{
	
		//Non-volatile globals
		//these do not get updated by the set globals function 
		public static final long DAY_IN_MILLISECONDS = 86400000;
		public static final long WEEK_IN_MILLISECONDS = 604800000;
		
		//Volatile globals
		//TODO These are set/updated by the setGlobalVars function in databasehandler which reads the values
		//from the GlobalVariables collection (which we can manually edit to tweak the values) and sets the
		//values of these to match those.
		//The default values can be found in the main method of Director. They are used to initialize the
		//GlobalVariables collection when it does not exist
		
		
		//*******************************NOTICE*******************************
		//IF YOU ARE ADDING A VOLATILE GLOBAL VARIABLE YOU MUST:
		//edit DatabaseHandler's setGlobalVars method (for updating its value from the db)
		//AND edit Director's main method (for the default value)
		public static long FOLLOW_TIME_MIN;
		public static long FOLLOW_TIME_MAX;
		public static long POST_TIME_MIN;
		public static long POST_TIME_MAX;
		public static long FOLLOW_TIME_INCUBATED_MIN;
		public static long FOLLOW_TIME_INCUBATED_MAX;
		//Base amount of accounts to follow before applying the formula
		public static long FOLLOWING_BASE_CAP;
		//This is the formula to determine how many accounts to follow
		//TODO Need to revise this formula, it's shit
		public static int GET_NUM_TO_UNFOLLOW(int sizeFollowers, int sizeFollowing){
			int numToUnfollow = (int)(FOLLOWING_BASE_CAP+(Math.log(sizeFollowers)/Math.log(100))) - sizeFollowing;
			return numToUnfollow >= 0 ? numToUnfollow : 0;
		}
		//*******************************NOTICE*******************************


		

	
	}