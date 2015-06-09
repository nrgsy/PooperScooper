package management;


import java.util.HashMap;
import org.bson.Document;


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
	public static long BIG_ACCOUNT_TIME;
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
	//The directory where the logs live
	public static String LOG_DIRECTORY;
	//NOTICE****************************************NOTICE***************************************NOTICE

	//Other globals, non changeable via the database, but still mutable by the code
	//the map of schwergsy account index to the last time they had a post
	public static HashMap<Integer, Long> lastPostTimeMap;


	//This is the formula to determine how many accounts to follow
	public static int getNumToUnfollow(int sizeFollowers, int sizeFollowing){
		int numToUnfollow = 0;
		if (sizeFollowing > FOLLOWING_BASE_CAP){
			if(sizeFollowers < 100*FOLLOWING_BASE_CAP){
				numToUnfollow = sizeFollowing - 2000;
			}
			else if(sizeFollowers > 100*FOLLOWING_BASE_CAP){
				numToUnfollow = 100*sizeFollowing - sizeFollowers;
			}
		}
		else if (sizeFollowing < FOLLOWING_BASE_CAP) {
			numToUnfollow = 0;
		}
		else{
			Maintenance.writeLog("***ERROR*** We have negative followers or following or Jon is an idiot ***ERROR***");

		}
		return numToUnfollow >= 0 ? numToUnfollow : 0;
	}

	/**
	 * 
	 * @param globalVars the BasicDBObject containing the global variables to initialize with 
	 * (typically pulled from the GlobalVariables collection of the database)
	 */
	public static synchronized void setGlobalVars(Document globalVars) {
		FOLLOW_TIME_MIN = globalVars.getLong("FOLLOW_TIME_MIN");
		FOLLOW_TIME_MAX = globalVars.getLong("FOLLOW_TIME_MAX");
		FOLLOW_TIME_INCUBATED_MIN = globalVars.getLong("FOLLOW_TIME_INCUBATED_MIN");
		FOLLOW_TIME_INCUBATED_MAX = globalVars.getLong("FOLLOW_TIME_INCUBATED_MAX");	
		BIG_ACCOUNT_TIME = globalVars.getLong("BIG_ACCOUNT_TIME");
		FOLLOWING_BASE_CAP = globalVars.getInteger("FOLLOWING_BASE_CAP");
		BIG_ACCOUNT_OUTS_FOR_REMOVAL = globalVars.getInteger("BIG_ACCOUNT_OUTS_FOR_REMOVAL");
		BIG_ACCOUNT_STRIKES_FOR_OUT = globalVars.getInteger("BIG_ACCOUNT_STRIKES_FOR_OUT");
		ALPHA = globalVars.getDouble("ALPHA");
		MIN_POST_TIME_INTERVAL = globalVars.getLong("MIN_POST_TIME_INTERVAL");
		TWITTER_RUNNABLE_INTERVAL = globalVars.getLong("TWITTER_RUNNABLE_INTERVAL");
		CONTENT_SAMPLE_SIZE = globalVars.getLong("CONTENT_SAMPLE_SIZE");
		MIN_TIME_BETWEEN_ACCESSES = globalVars.getLong("MIN_TIME_BETWEEN_ACCESSES");
		MAX_IMAGE_DIMENSION = globalVars.getLong("MAX_IMAGE_DIMENSION");
		LOG_DIRECTORY = globalVars.getString("LOG_DIRECTORY");
	}

	public static HashMap<String,Object> getDefaultGlobalVars(){
		HashMap<String, Object> globalVars = new HashMap<String, Object>();
		//to prevent exceeding the limit of following more than 1000 people per day, make sure that
		//FOLLOW_TIME_MIN never goes below 1.44 minutes (86400 seconds)
		globalVars.put("FOLLOW_TIME_MIN", 86400L);
		globalVars.put("FOLLOW_TIME_MAX", 123430L);
		globalVars.put("FOLLOW_TIME_INCUBATED_MIN", 180000L);
		globalVars.put("FOLLOW_TIME_INCUBATED_MAX", 240000L);
		globalVars.put("BIG_ACCOUNT_TIME", 950000L);//This is an arbitrary time a little over 15 minutes to prevent rate limit problems
		globalVars.put("BIG_ACCOUNT_STRIKES_FOR_OUT", 3);
		globalVars.put("BIG_ACCOUNT_OUTS_FOR_REMOVAL", 3);
		globalVars.put("FOLLOWING_BASE_CAP", 2000);
		//ie we want it to post every 25 minutes on average, but will not post more frequently than
		//once every MIN_POST_TIME_INTERVAL (15 for now) minutes so 25 - 15 = 10
		globalVars.put("ALPHA", 1.0/10.0);
		//ie 15 minutes
		globalVars.put("MIN_POST_TIME_INTERVAL", 900000L);
		globalVars.put("TWITTER_RUNNABLE_INTERVAL", 60000L);
		globalVars.put("CONTENT_SAMPLE_SIZE", 100L);
		globalVars.put("MIN_TIME_BETWEEN_ACCESSES", GlobalStuff.WEEK_IN_MILLISECONDS);
		globalVars.put("MAX_IMAGE_DIMENSION", 700L);
		globalVars.put("LOG_DIRECTORY", "logs/");

		return globalVars;
	}

}