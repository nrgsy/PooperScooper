package management;


import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
	
	public class GlobalStuff{
	
		//Non-volatile globals
		//these do not get updated by the set globals function 
		public static final long MINUTE_IN_MILLISECONDS = 60000;
		public static final long DAY_IN_MILLISECONDS = 86400000;
		public static final long WEEK_IN_MILLISECONDS = 604800000;
		public static final String DOPEST_MAN_ALIVE = "BO JANG";
		
		//Volatile globals
		//These are set/updated by the findAndSetGlobalVars function in databasehandler which reads the values
		//from the GlobalVariables collection (which we can manually edit to tweak the values) and sets the
		//values of these to match those.
		//The default values can be found in the main method of Director. They are used to initialize the
		//GlobalVariables collection when it does not exist	
		//NOTICE****************************************NOTICE***************************************NOTICE
		//IF YOU ARE ADDING A VOLATILE GLOBAL VARIABLE YOU MUST:
		//1: Edit GlobalStuff's setGlobalVars method (for updating its value from the db)
		//2: Edit GlobalStuff's getGlobalVars method (for the default value)
		public static long FOLLOW_TIME_MIN;
		public static long FOLLOW_TIME_MAX;
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
		//interval between TwitterRunnable runs
		public static long TWITTER_RUNNABLE_INTERVAL;
		//The size of the content sample in dbhandler's getRandomContent
		public static long CONTENT_SAMPLE_SIZE;
		//The min time between two accesses of the same content by the same account
		public static long MIN_TIME_BETWEEN_ACCESSES;
		//The max width and height of an image in the Approval GUI
		public static long MAX_IMAGE_DIMENSION;
		//NOTICE****************************************NOTICE***************************************NOTICE
		
		//Other globals, non changeable via the database, but still mutable by the code
		//the map of schwergsy account index to the last time they had a post
		public static HashMap<Integer, Long> lastPostTimeMap;
		
		
		//This is the formula to determine how many accounts to follow
		//TODO Need to revise this formula, it's shit
		public static int GET_NUM_TO_UNFOLLOW(int sizeFollowers, int sizeFollowing){
			int numToUnfollow =
					(int)(FOLLOWING_BASE_CAP+(Math.log(sizeFollowers)/Math.log(100))) - sizeFollowing;
			return numToUnfollow >= 0 ? numToUnfollow : 0;
		}
		
		/**
		 * 	//TODO uses the given dbobject to set (or initialize) the global variables in GlobalStuff
		 * 
		 * @param globalVars the BasicDBObject containing the global variables to initialize with 
		 * (typically pulled from the GlobalVariables collection of the database)
		 */
		public static synchronized void setGlobalVars(Document globalVars) {
			FOLLOW_TIME_MIN = globalVars.getLong("FOLLOW_TIME_MIN");
			FOLLOW_TIME_MAX = globalVars.getLong("FOLLOW_TIME_MAX");
			FOLLOW_TIME_INCUBATED_MIN = globalVars.getLong("FOLLOW_TIME_INCUBATED_MIN");
			FOLLOW_TIME_INCUBATED_MAX = globalVars.getLong("FOLLOW_TIME_INCUBATED_MAX");	
			FOLLOWING_BASE_CAP = globalVars.getInteger("FOLLOWING_BASE_CAP");
			BIG_ACCOUNT_OUTS_FOR_REMOVAL = globalVars.getInteger("BIG_ACCOUNT_OUTS_FOR_REMOVAL");
			BIG_ACCOUNT_STRIKES_FOR_OUT = globalVars.getInteger("BIG_ACCOUNT_STRIKES_FOR_OUT");
			ALPHA = globalVars.getDouble("ALPHA");
			MIN_POST_TIME_INTERVAL = globalVars.getLong("MIN_POST_TIME_INTERVAL");
			TWITTER_RUNNABLE_INTERVAL = globalVars.getLong("TWITTER_RUNNABLE_INTERVAL");
			CONTENT_SAMPLE_SIZE = globalVars.getLong("CONTENT_SAMPLE_SIZE");
			MIN_TIME_BETWEEN_ACCESSES = globalVars.getLong("MIN_TIME_BETWEEN_ACCESSES");
			MAX_IMAGE_DIMENSION = globalVars.getLong("MAX_IMAGE_DIMENSION");
		}
		
		public static HashMap<String,Object> getGlobalVars(){
			HashMap<String, Object> globalVars = new HashMap<String, Object>();
			globalVars.put("FOLLOW_TIME_MIN", 86400L);
			globalVars.put("FOLLOW_TIME_MAX", 123430L);
			globalVars.put("FOLLOW_TIME_INCUBATED_MIN", 180000L);
			globalVars.put("FOLLOW_TIME_INCUBATED_MAX", 240000L);
			globalVars.put("BIG_ACCOUNT_STRIKES_FOR_OUT", 3);
			globalVars.put("BIG_ACCOUNT_OUTS_FOR_REMOVAL", 3);
			globalVars.put("FOLLOWING_BASE_CAP", 1000);
			globalVars.put("ALPHA", 1.0/25.0);
			globalVars.put("MIN_POST_TIME_INTERVAL", 900000L);
			globalVars.put("TWITTER_RUNNABLE_INTERVAL", 60000L);
			globalVars.put("CONTENT_SAMPLE_SIZE", 100L);
			globalVars.put("MIN_TIME_BETWEEN_ACCESSES", GlobalStuff.WEEK_IN_MILLISECONDS);
			globalVars.put("MAX_IMAGE_DIMENSION", 700L);
			
			return globalVars;
		}

	}