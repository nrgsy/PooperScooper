package management;

import java.util.HashMap;

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
		//1: Edit GlobalStuff's setGlobalVars method (for updating its value from the db)
		//2: Edit DatabaseHandler's initGlobalVars method (for the default value)
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
		//the minimum allowed time 
		public static long MIN_POST_TIME_INTERVAL;
		//The size of the content sample in dbhandler's getRandomContent
		public static long CONTENT_SAMPLE_SIZE;
		//The min time between two accesses of the same content by the same account
		public static long MIN_TIME_BETWEEN_ACCESSES;
		//*******************************NOTICE*******************************
		
		//Other globals, non changeable via the database, but still mutable by the code
		//the map of schwergsy account index to the last time they had a post
		public static HashMap<Integer, Long> lastPostTimeMap;
		
		
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
			ALPHA = globalVars.getDouble("ALPHA");
			MIN_POST_TIME_INTERVAL = globalVars.getLong("MIN_POST_TIME_INTERVAL");
			CONTENT_SAMPLE_SIZE = globalVars.getLong("CONTENT_SAMPLE_SIZE");
			MIN_TIME_BETWEEN_ACCESSES = globalVars.getLong("MIN_TIME_BETWEEN_ACCESSES");
		}

	}