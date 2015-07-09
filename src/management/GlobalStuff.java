package management;

import java.util.HashMap;

import org.bson.Document;


public class GlobalStuff{

	//Non-volatile globals
	//these do not get updated by the set globals function 
	public static final long MINUTE_IN_MILLISECONDS = 60000L;
	public static final long HOUR_IN_MILLISECONDS = 3600000L;
	public static final long DAY_IN_MILLISECONDS = 86400000L;
	public static final long WEEK_IN_MILLISECONDS = 604800000L;
	public static final String DOPEST_MAN_ALIVE = "BO JANG";

	//Volatile globals
	//These are set/updated by the findAndSetGlobalVars function in databasehandler which reads the
	//values from the GlobalVariables collection (which we can manually edit to tweak the values) and 
	//sets the values of these to match those.
	//The default values can be found in the main method of Director. They are used to initialize the
	//GlobalVariables collection when it does not exist	
	
	//NOTICE****************************************NOTICE***************************************NOTICE
	//IF YOU ARE ADDING A VOLATILE GLOBAL VARIABLE YOU MUST:
	//1: Edit GlobalStuff's setGlobalVars method (for updating its value from the db)
	//2: Edit GlobalStuff's getGlobalVars method (for the default value)
	
	//the min/max times between follow runnable runs
	public static long FOLLOW_TIME_MIN;
	public static long FOLLOW_TIME_MAX;
	public static long FOLLOW_TIME_INCUBATED_MIN;
	public static long FOLLOW_TIME_INCUBATED_MAX;
	//number of milliseconds between big account runnable runs
	public static long BIG_ACCOUNT_TIME;
	public static int BIG_ACCOUNT_STRIKES_FOR_OUT;
	public static int BIG_ACCOUNT_OUTS_FOR_REMOVAL;
	//Base amount of accounts to follow before applying the formula
	public static long FOLLOWING_BASE_CAP;
	//this is the probability of a post occurring at any given minute
	public static double ALPHA;
	//the minimum allowed time 
	public static long MIN_POST_TIME_INTERVAL;
	//the number of milliseconds between TwitterRunnable runs
	public static long TWITTER_RUNNABLE_INTERVAL;
	//The size of the content sample in dbhandler's getRandomContent
	public static long CONTENT_SAMPLE_SIZE;
	//The min time between two accesses of the same content by the same account
	public static long MIN_TIME_BETWEEN_ACCESSES;
	//The max width and height of an image in the Approval GUI
	public static long MAX_IMAGE_DIMENSION;
	//The directory where the logs live
	public static String LOG_DIRECTORY;
	//The rate at which the timer tasks fire (only for twitter, bigacc, and follow runnables)
	public static long TIMER_TASK_FIRE_RATE;
	//the minimum amount of time Maintenance should ever take before starting the API calling section
	//(to ensure that rate limits cannot be exceeded)
	public static long MAINTENANCE_SNOOZE_TIME;
	//the number of tries uploadPic in TwitterRunnable has before giving up 
	public static long UPLOAD_PIC_ATTEMPT_LIMIT;
	//the max number of milliseconds the maintenance thread can run for before being cut off and erroring
	public static long MAX_MAINTENANCE_RUN_TIME;
	//the number of milliseconds the imaging getting thread can run for before failing/returning null
	public static long MAX_IMAGE_FETCH_TIME;
	//the directory where images are stored
	public static String PICS_DIR;

	//NOTICE****************************************NOTICE***************************************NOTICE

	//Other globals, non changeable via the database, but still mutable by the code
	//the map of schwergsy account index to the last time they had a post
	public static HashMap<Integer, Long> lastPostTimeMap;
	//account id's + account name prefixes (e.g. 0bigacc) mapped to a counter indicating the number of
	//times their timertask has fired without running the runnable. When it reaches a specified amount 
	//the runnable will run and the counter will be reset to 0
	public static HashMap<String, Integer> numberOfRuns;
	
	//This is the formula to determine how many accounts to follow
	public static int getNumToUnfollow(int sizeFollowers, int sizeFollowing){
		int numToUnfollow = 0;
		if (sizeFollowing >= FOLLOWING_BASE_CAP){
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
			Maintenance.writeLog("We have negative followers or following or "
					+ "Jon is an idiot", null, -1);
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
		TIMER_TASK_FIRE_RATE = globalVars.getLong("TIMER_TASK_FIRE_RATE");
		MAINTENANCE_SNOOZE_TIME = globalVars.getLong("MAINTENANCE_SNOOZE_TIME");
		UPLOAD_PIC_ATTEMPT_LIMIT = globalVars.getLong("UPLOAD_PIC_ATTEMPT_LIMIT");
		MAX_MAINTENANCE_RUN_TIME = globalVars.getLong("MAX_MAINTENANCE_RUN_TIME");
		MAX_IMAGE_FETCH_TIME = globalVars.getLong("MAX_IMAGE_FETCH_TIME");
		PICS_DIR = globalVars.getString("PICS_DIR");
	}

	public static HashMap<String,Object> getDefaultGlobalVars(){
		HashMap<String, Object> globalVars = new HashMap<String, Object>();
		//to prevent exceeding the limit of following more than 1000 people per day, make sure that
		//FOLLOW_TIME_MIN never goes below 1.44 minutes (86400 ms)
		globalVars.put("FOLLOW_TIME_MIN", 86400L);
		globalVars.put("FOLLOW_TIME_MAX", 123430L);
		globalVars.put("FOLLOW_TIME_INCUBATED_MIN", GlobalStuff.MINUTE_IN_MILLISECONDS * 3);
		globalVars.put("FOLLOW_TIME_INCUBATED_MAX", GlobalStuff.MINUTE_IN_MILLISECONDS * 4);
		//This is an arbitrary time a little over 15 minutes to prevent rate limit problems
		globalVars.put("BIG_ACCOUNT_TIME", GlobalStuff.MINUTE_IN_MILLISECONDS * 16);
		globalVars.put("BIG_ACCOUNT_STRIKES_FOR_OUT", 3);
		globalVars.put("BIG_ACCOUNT_OUTS_FOR_REMOVAL", 3);
		globalVars.put("FOLLOWING_BASE_CAP", 2000);
		//ie we want it to post every 25 minutes on average, but will not post more frequently than
		//once every MIN_POST_TIME_INTERVAL (15 for now) minutes so 25 - 15 = 10
		globalVars.put("ALPHA", 1.0/10.0);
		//ie 15 minutes
		globalVars.put("MIN_POST_TIME_INTERVAL", GlobalStuff.MINUTE_IN_MILLISECONDS * 15);
		//the number of milliseconds between twitter runnable runs
		globalVars.put("TWITTER_RUNNABLE_INTERVAL", GlobalStuff.MINUTE_IN_MILLISECONDS);
		globalVars.put("CONTENT_SAMPLE_SIZE", 100L);
		globalVars.put("MIN_TIME_BETWEEN_ACCESSES", GlobalStuff.WEEK_IN_MILLISECONDS);
		globalVars.put("MAX_IMAGE_DIMENSION", 700L);
		globalVars.put("LOG_DIRECTORY", "logs/");
		//timer tasks fire every 1 seconds
		//BEWARE DO NOT EVER MAKE THIS GREATER THAN MAINTENANCE_SNOOZE_TIME OR DUPLICATE RUNNABLES MAY
		//OCCUR IN THE CREATE RUNNABLES METHODS OF TIMERFACTORY
		globalVars.put("TIMER_TASK_FIRE_RATE", 1000L);
		//Maintenance waits 15 min before twitter api calling section for rate limits to reset
		//BEWARE DO NOT EVER MAKE THIS LESS THAN TIMER_TASK_FIRE_RATE OR DUPLICATE RUNNABLES MAY OCCUR
		//IN THE CREATE RUNNABLES METHODS OF TIMERFACTORY
		globalVars.put("MAINTENANCE_SNOOZE_TIME", GlobalStuff.MINUTE_IN_MILLISECONDS * 15L);
		globalVars.put("UPLOAD_PIC_ATTEMPT_LIMIT", 10L);
		//default max run time for maintenance is 4 hours
		globalVars.put("MAX_MAINTENANCE_RUN_TIME", GlobalStuff.HOUR_IN_MILLISECONDS * 4L);
		//scooping an image should not take more than 20 seconds
		globalVars.put("MAX_IMAGE_FETCH_TIME", 20000L);	
		globalVars.put("PICS_DIR", "pics/");	

		return globalVars;
	}
}