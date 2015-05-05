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

import content.RedditScraper;


/**
 * @author Bojangles and McChrpchrp
 *
 */
public class Director {

	public static HashMap<String, Boolean> runStatus; 

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
	private static TimerTask createTwitterRunnableTimerTask(Twitter bird, int index, String key){
		return new TimerTask() {
			@Override
			public void run() {

				if (!sMaintenance.flagSet) {
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
	
	private static TimerTask createFollowRunnableTimerTask(Twitter bird, int index, String key){
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
	
	private static TimerTask createBigAccRunnableTimerTask(Twitter bird, int index, String key){
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
						Thread.sleep(5000);
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
				
				//call "initialize global vars" to copy variables from the global config file to Globalstuff class

				
				
				Maintenance.flagSet = false;
				System.out.println("maintenance complete");
			}};
	}

	/**
	 * @param args
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public static void main(String[]args) throws UnknownHostException, Exception{

		Date nextOccurrenceOf3am = getNextTime(new Date(), 3);

		//The timer who's task fires once a day to do the maintenance tasks
		new Timer().scheduleAtFixedRate(createMaintenanceTimerTask(), nextOccurrenceOf3am, GlobalStuff.DAY_IN_MILLISECONDS);

		long scrapetime = GlobalStuff.DAY_IN_MILLISECONDS;

		for(int id =0; id < DataBaseHandler.getCollectionSize("SchwergsyAccounts"); id++){
			final BasicDBObject info = DataBaseHandler.getAuthorizationInfo(id);			
			
			String cusKey = (String) info.get("customerKey");
			String customKey = cusKey + "twitter";
			
			long followtime_min = GlobalStuff.FOLLOW_TIME_MIN;
			long followtime_max = GlobalStuff.FOLLOW_TIME_MAX;
			long posttime_min = GlobalStuff.POST_TIME_MIN;
			long posttime_max = GlobalStuff.POST_TIME_MAX;
			Random r = new Random();
			long followtime = followtime_min+((long)(r.nextDouble()*(followtime_max-followtime_min)));
			long posttime = posttime_min+((long)(r.nextDouble()*(posttime_max-posttime_min)));
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
			new Timer().scheduleAtFixedRate(createTwitterRunnableTimerTask(twitter, id, cusKey), 0L, 60000L);
			new Timer().scheduleAtFixedRate(createFollowRunnableTimerTask(twitter, id, cusKey), 0L, followtime);
			new Timer().scheduleAtFixedRate(createBigAccRunnableTimerTask(twitter, id, cusKey), 0L, bigacctime);


		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				new Thread(new RedditScraper()).start();
			}},0L, scrapetime);
	}
}
