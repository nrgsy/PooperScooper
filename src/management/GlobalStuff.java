package management;

import com.mongodb.BasicDBObject;
	
	public class GlobalStuff{
	
		//Non-volatile globals
		//these do not get updated by the set globals function 
		public static final long DAY_IN_MILLISECONDS = 86400000;
		public static final long WEEK_IN_MILLISECONDS = 604800000;
		public static final String DOPEST_MAN_ALIVE = "DJ TJ";
		
		//Volatile globals
		//TODO These are set/updated by the setGlobalVars function in databasehandler which reads the values
		//from the GlobalVariables collection (which we can manually edit to tweak the values) and sets the
		//values of these to match those.
		//The default values can be found in the main method of Director. They are used to initialize the
		//GlobalVariables collection when it does not exist
		
		
		//*******************************NOTICE*******************************
		//IF YOU ARE ADDING A VOLATILE GLOBAL VARIABLE YOU MUST:
		//edit setGlobalVars method (for updating its value from the db)
		//AND edit DataBaseHandler.initGlobalVars (for default values)
		public static long FOLLOW_TIME_MIN;
		public static long FOLLOW_TIME_MAX;
		public static long POST_TIME_MIN;
		public static long POST_TIME_MAX;
		public static long FOLLOW_TIME_INCUBATED_MIN;
		public static long FOLLOW_TIME_INCUBATED_MAX;
		public static int BIG_ACCOUNT_STRIKES_FOR_OUT;
		public static int BIG_ACCOUNT_OUTS_FOR_REMOVAL;
		//Base amount of accounts to follow before applying the formula
		public static long FOLLOWING_BASE_CAP;
		//this is the probability of a post occurring at any given minute
		public static double ALPHA;
		
		//these two are the safeguards for posting. They ensure that MAX_NUMER_OF_POSTS + 1 posts can never 
		//happen in a POST_TIME_CONSTANT minute interval.
		public static long MAX_NUMER_OF_POSTS;
		public static long POST_TIME_CONSTANT;
		//*******************************NOTICE*******************************
		
		//This is the formula to determine how many accounts to follow
		//TODO Need to revise this formula, it's shit
		public static int GET_NUM_TO_UNFOLLOW(int sizeFollowers, int sizeFollowing){
			int numToUnfollow = (int)(FOLLOWING_BASE_CAP+(Math.log(sizeFollowers)/Math.log(100))) - sizeFollowing;
			return numToUnfollow >= 0 ? numToUnfollow : 0;
		}
		
		/**
		 * 	//TODO uses the given dbobject to set (or initialize) the global variables in GlobalStuff
		 * 
		 * @param globalVars the BasicDBObject containing the global variables to initialize with 
		 * (typically pulled from the GlobalVariables collection of the database)
		 */
		public static synchronized void setGlobalVars(BasicDBObject globalVars) {
		
			FOLLOW_TIME_MIN = globalVars.getLong("FOLLOW_TIME_MIN");
			FOLLOW_TIME_MAX = globalVars.getLong("FOLLOW_TIME_MAX");
			POST_TIME_MIN = globalVars.getLong("POST_TIME_MIN");
			POST_TIME_MAX = globalVars.getLong("POST_TIME_MAX");
			FOLLOW_TIME_INCUBATED_MIN = globalVars.getLong("FOLLOW_TIME_INCUBATED_MIN");
			FOLLOW_TIME_INCUBATED_MAX = globalVars.getLong("FOLLOW_TIME_INCUBATED_MAX");	
			FOLLOWING_BASE_CAP = globalVars.getInt("FOLLOWING_BASE_CAP");
			BIG_ACCOUNT_OUTS_FOR_REMOVAL = globalVars.getInt("BIG_ACCOUNT_OUTS_FOR_REMOVAL");
			BIG_ACCOUNT_STRIKES_FOR_OUT = globalVars.getInt("BIG_ACCOUNT_STRIKES_FOR_OUT");
		}
		//*******************************NOTICE*******************************

	}