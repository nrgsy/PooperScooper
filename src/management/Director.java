package management;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitterRunnables.FollowRunnable;
import twitterRunnables.TwitterRunnable;
import twitterRunnables.bigAccRunnable;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import content.RedditScraper;



/**
 * @author Bojangles and McChrpchrp
 *
 */
public class Director {

	//Can i get a comment describing this? especially what makes up the String key
	public static HashMap<String, Boolean> runStatus; 

	/**
	 * @param base
	 * @param hourOfDay
	 * @return
	 */
	public static Date getNextTime(Date base, int hourOfDay) {
		Calendar then = Calendar.getInstance();
		then.setTime(base);
		then.set(Calendar.HOUR_OF_DAY, hourOfDay);
		then.set(Calendar.MINUTE, 0);
		then.set(Calendar.SECOND, 0);
		then.set(Calendar.MILLISECOND, 0);
		if (then.getTime().before(base)) {
			then.add(Calendar.DAY_OF_YEAR, 1);
		}
		return then.getTime();
	}

	//TODO runstatus is not scheduled correctly. it would run immediately after runnable is instantiated, not finished.
	/**
	 * Phal can you give a description for the parameters?
	 * 
	 * @param bird
	 * @param index
	 * @param key
	 * @return
	 */
	private static TimerTask createTwitterRunnableTimerTask(final Twitter bird, final int index, final String key){
		return new TimerTask() {
			@Override
			public void run() {

				if (!Maintenance.flagSet) {
					runStatus.put(key, true);
					new TwitterRunnable(bird,index);
					runStatus.put(key, false);
				}

				else {
					runStatus.put(key, false);
				}
			}
		};
	}

	/**
	 * Phal can you give a description for the parameters?
	 * 
	 * @param bird
	 * @param index
	 * @param key
	 * @return
	 */
	private static TimerTask createFollowRunnableTimerTask(final Twitter bird, final int index, final String key){
		return new TimerTask() {
			@Override
			public void run() {

				if (!Maintenance.flagSet) {
					runStatus.put(key, true);
					new FollowRunnable(bird,index);
					runStatus.put(key, false);
				}

				else {
					runStatus.put(key, false);
				}
			}
		};
	}

	/**
	 * Phal can you give a description for the parameters?
	 * 
	 * @param bird
	 * @param index
	 * @param key
	 * @return
	 */
	private static TimerTask createBigAccRunnableTimerTask(final Twitter bird, final int index, final String key){
		return new TimerTask() {
			@Override
			public void run() {


				if (!Maintenance.flagSet) {
					runStatus.put(key, true);
					new bigAccRunnable(bird,index);
					runStatus.put(key, false);
				}

				else {
					runStatus.put(key, false);
				}
			}
		};
	}


	public static TimerTask createMaintenanceTimerTask() {

		return new TimerTask() {
			@Override
			public void run() {
				System.out.println("maintenance started");
				Maintenance.flagSet = true;				
				boolean activityExists = true;
				while (activityExists) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					//look for a status that's true (indicating that something's still running)
					boolean somethingStillRunning = false;
					for (boolean status : runStatus.values()) {
						if (status) {
							somethingStillRunning = true;
							break;
						}
					}

					if (!somethingStillRunning) {
						activityExists = false;
					}
				}	

				//TODO the actual maintenance
				//Update followers
				//old content garbage collection
				//get big accounts (because of high api call amount)

				//get the global variables from the GlobalVariables collection and set the ones in GlobalStuff
				try {
					DataBaseHandler.findAndSetGlobalVars();
				} catch (UnknownHostException e) {
					System.err.println("ERROR: failed to find ");
					e.printStackTrace();
				}

				Maintenance.flagSet = false;
				System.out.println("maintenance complete");
			}};
	}

	/**
	 * Runs all the threads and initializes the volatile variables in GlobalStuff
	 * 
	 * @param args
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public static void main(String[]args) throws UnknownHostException, Exception {

		//Setting the global variables in GlobalStuff
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection collection = db.getCollection("GlobalVariables");
		if (!db.collectionExists("GlobalVariables")) {
			System.out.println("Globals not found in db, initializing with defaults");

			//These are the default values to set the volatile variables to
			BasicDBObject globalVars = new BasicDBObject()
			.append("FOLLOW_TIME_MIN", 86400L)
			.append("FOLLOW_TIME_MAX", 123430L)
			.append("POST_TIME_MIN", 900000L)
			.append("POST_TIME_MAX", 1500000L)
			.append("FOLLOW_TIME_INCUBATED_MIN", 180000L)
			.append("FOLLOW_TIME_INCUBATED_MAX", 240000L);

			collection.insert(globalVars);
		}
		//set the global vars bases on the current state of the GlobalVariables collection
		DataBaseHandler.findAndSetGlobalVars();
		mongoClient.close();

		Date nextOccurrenceOf3am = getNextTime(new Date(), 3);
		//The timer who's task fires once a day to do the maintenance tasks
		new Timer().scheduleAtFixedRate(
				createMaintenanceTimerTask(),
				nextOccurrenceOf3am,
				GlobalStuff.DAY_IN_MILLISECONDS);

		long scrapetime = GlobalStuff.DAY_IN_MILLISECONDS;

		for(int id =0; id < DataBaseHandler.getCollectionSize("SchwergsyAccounts"); id++) {
			final BasicDBObject info = DataBaseHandler.getAuthorizationInfo(id);			

			String cusKey = (String) info.get("customerKey");

			long followtime_min = GlobalStuff.FOLLOW_TIME_MIN;
			long followtime_max = GlobalStuff.FOLLOW_TIME_MAX;
			long incubated_followtime_min = GlobalStuff.FOLLOW_TIME_INCUBATED_MIN;
			long incubated_followtime_max = GlobalStuff.FOLLOW_TIME_INCUBATED_MAX;
			long posttime_min = GlobalStuff.POST_TIME_MIN;
			long posttime_max = GlobalStuff.POST_TIME_MAX;

			Random r = new Random();
			long followtime = followtime_min+((long)(r.nextDouble()*(followtime_max-followtime_min)));
			long posttime = posttime_min+((long)(r.nextDouble()*(posttime_max-posttime_min)));
			long incubated_followtime = incubated_followtime_min +
					((long)r.nextDouble()*(incubated_followtime_max - incubated_followtime_min));
			long bigacctime =  0L; //TODO figure out rate for bigAcc scraping and harvesting

			//If in incubation, follows at a rate of 425 per day
			if((boolean) info.get("isIncubated")){
				followtime = 203250;
			}

			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			.setOAuthConsumerKey(cusKey)
			.setOAuthConsumerSecret(info.getString("customerSecret"))
			.setOAuthAccessToken(info.getString("authorizationKey"))
			.setOAuthAccessTokenSecret(info.getString("authorizationSecret"));
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();


			//TODO add in DateTime variable to check against to know when to run probability to post.
			new Timer().scheduleAtFixedRate(createTwitterRunnableTimerTask(twitter, id, cusKey+"twitter"), 0L, 60000L);
			new Timer().scheduleAtFixedRate(createFollowRunnableTimerTask(twitter, id, cusKey+"follow"), 0L, followtime);
			new Timer().scheduleAtFixedRate(createBigAccRunnableTimerTask(twitter, id, cusKey+"bigacc"), 0L, bigacctime);


			new Timer().scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					new Thread(new RedditScraper()).start();
				}},0L, scrapetime);
		}
	}
}
