package management;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

import twitterRunnables.FollowRunnable;
import twitterRunnables.TwitterRunnable;

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
	
	/**
	 * @param args
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public static void main(String[]args) throws UnknownHostException, Exception{

		Date nextOccurrenceOf3am = getNextTime(new Date(), 3);
		
		new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				System.out.println("maintenance started");
				GlobalMaintenance.flagSet = true;				
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
				
				GlobalMaintenance.flagSet = false;
				System.out.println("maintenance complete");
			}}, nextOccurrenceOf3am, GlobalStuff.DAY_IN_MILLISECONDS);
		
		
		long scrapetime = GlobalStuff.DAY_IN_MILLISECONDS;

		for(int id =0; id < DataBaseHandler.getCollectionSize("SchwergsyAccounts"); id++){
			final BasicDBObject info = DataBaseHandler.getAuthorizationInfo(id);

			long followtime_min = 86400L;
			long followtime_max = 123430L;
			long posttime_min = 900000L;
			long posttime_max = 1500000L;
			Random r = new Random();
			long followtime = followtime_min+((long)(r.nextDouble()*(followtime_max-followtime_min)));
			long posttime = posttime_min+((long)(r.nextDouble()*(posttime_max-posttime_min)));

			//If in incubation, follows at a rate of 425 per day
			if((boolean) info.get("isIncubated")){
				followtime = 203250;
			}

			new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
				@Override
				public void run() {

					String cusKey = (String) info.get("customerKey");
					String customKey = cusKey + "twitter";
					if (!GlobalMaintenance.flagSet) {

						runStatus.put(customKey, true);
						new TwitterRunnable(cusKey,
								(String) info.get("customerSecret"),
								(String) info.get("authorizationKey"),
								(String) info.get("authorizationSecret"),
								0);
						runStatus.put(customKey, false);
					}
					else {
						runStatus.put(customKey, false);
					}
				}},0L, posttime);


			new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
				@Override
				public void run() {

					String cusKey = (String) info.get("customerKey");
					String customKey = cusKey + "follow";
					if (!GlobalMaintenance.flagSet) {

						runStatus.put(customKey, true);
						new FollowRunnable(cusKey,
								(String) info.get("customerSecret"),
								(String) info.get("authorizationKey"),
								(String) info.get("authorizationSecret"),
								0);
						runStatus.put(customKey, false);
					}
					else {
						runStatus.put(customKey, false);
					}

				}}, 0L, followtime);
		}

		new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				new Thread(new RedditScraper()).start();
			}},0L, scrapetime);
	}
}
