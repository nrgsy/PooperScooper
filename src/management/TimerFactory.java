package management;

import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.bson.Document;
import content.RedditScraper;
import twitter4j.Twitter;
import twitterRunnables.FollowRunnable;
import twitterRunnables.TwitterRunnable;
import twitterRunnables.bigAccRunnable;

public class TimerFactory {

	public static TimerTask createRedditTimerTask(final boolean init) {

		Maintenance.writeLog("creating RedditTimerTask");

		return new TimerTask() {
			@Override
			public void run() {
				Maintenance.writeLog("RedditTimerTask fired");
				if (!Maintenance.flagSet){
					new RedditScraper(init).run();
				}
				else {
					Maintenance.writeLog("Skipped creation of RedditScraper because maintenance "
							+ "flag is set");
					this.cancel();
				}
			}};
	}
	
	/**
	 * @param Twitter created by Director
	 * @param int of SchwergsAccount index
	 * @return
	 */
	private static TimerTask createTwitterRunnableTimerTask(final Twitter bird, final int index){
		
		Maintenance.writeLog("creating TwitterRunnableTimerTask");
		
		return new TimerTask() {
			@Override
			public void run() {
				Maintenance.writeLog("TwitterRunnableTimerTask fired");
				if (!Maintenance.flagSet) {
					new TwitterRunnable(bird,index).run();
				}
				else {
					Maintenance.writeLog("Skipped creation of TwitterRunnable because maintenance "
							+ "flag is set");
					Maintenance.runStatus.put(index+"twitter", false);
					this.cancel();
				}
			}
		};
	}

	/**
	 * @param Twitter created by Director
	 * @param int of SchwergsAccount index
	 * @return
	 */
	private static TimerTask createFollowRunnableTimerTask(final Twitter bird, final int index){
		
		Maintenance.writeLog("creating FollowRunnableTimerTask");
		
		return new TimerTask() {
			@Override
			public void run() {
				Maintenance.writeLog("FollowRunnableTimerTask fired");
				if (!Maintenance.flagSet) {
					new FollowRunnable(bird,index).run();
				}
				else {
					Maintenance.writeLog("Skipped creation of FollowRunnable because maintenance "
							+ "flag is set");
					Maintenance.runStatus.put(index+"follow", false);
					this.cancel();
				}
			}
		};
	}

	/**
	 * @param Twitter created by Director
	 * @param int of SchwergsAccount index
	 * @return
	 */
	private static TimerTask createBigAccRunnableTimerTask(final Twitter bird, final int index){
		
		Maintenance.writeLog("creating BigAccRunnableTimerTask");
		
		return new TimerTask() {					
			@Override
			public void run() {
				Maintenance.writeLog("BigAccRunnableTimerTask fired");
				if (!Maintenance.flagSet) {
					new bigAccRunnable(bird,index, DataBaseHandler.getBigAccountHarvestIndex(index)).run();
				}
				else{
					Maintenance.writeLog("Skipped creation of bigAccRunnable because maintenance "
							+ "flag is set");
					Maintenance.runStatus.put(index+"bigAcc", false);
					this.cancel();
				}

			}
		};
	}
	
	public static TimerTask createMaintenanceTimerTask() {

		Maintenance.writeLog("creating MaintenanceTimerTask");
		
		return new TimerTask() {
			@Override
			public void run() {
				Maintenance.writeLog("MaintenanceTimerTask fired");
				try {
					Maintenance.performMaintenance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}};
	}
	
	/**
	 * Creates the Timers for all Schwergsy accounts
	 * 
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public static void createTimers(boolean init) throws UnknownHostException, Exception {
		
		Maintenance.writeLog("Creating timers for all accounts");
		
		long scrapetime = GlobalStuff.DAY_IN_MILLISECONDS;
		new Timer().scheduleAtFixedRate(createRedditTimerTask(init), 0L, scrapetime);

		for(int id = 0; id < DataBaseHandler.getCollectionSize("SchwergsyAccounts"); id++) {
			createTimers(id);
		}
	}
	
	/**
	 * Creates the Timers for the given Schwergsy account
	 * 
	 * @param id The ID of the Schwergsy accout
	 * @throws Exception 
	 */
	public static void createTimers(int id) {
		
		Maintenance.writeLog("Creating timers for account with id: " + id);
		
		Document info;
		try {
			info = DataBaseHandler.getAuthorizationInfo(id);
		} catch (Exception e) {
			Maintenance.writeLog("***ERROR*** Failed to pull authorization info for account with "
					+ "id: " + id + " from the database. Timers cannot be created. ***ERROR***");
			e.printStackTrace();
			return;
		}		

		long followtime_min = GlobalStuff.FOLLOW_TIME_MIN;
		long followtime_max = GlobalStuff.FOLLOW_TIME_MAX;
		long incubated_followtime_min = GlobalStuff.FOLLOW_TIME_INCUBATED_MIN;
		long incubated_followtime_max = GlobalStuff.FOLLOW_TIME_INCUBATED_MAX;
		long bigacctime = GlobalStuff.BIG_ACCOUNT_TIME;

		Random r = new Random();
		long followtime = followtime_min+((long)(r.nextDouble()*(followtime_max-followtime_min)));

		long incubated_followtime = incubated_followtime_min +
				((long)r.nextDouble()*(incubated_followtime_max - incubated_followtime_min));

		//If in incubation, follows at a rate of 425 per day
		if(info.getBoolean("isIncubated")){
			followtime = incubated_followtime;
		}

		Twitter twitter = TwitterHandler.getTwitter(info);

		new Timer().scheduleAtFixedRate(createTwitterRunnableTimerTask(twitter, id), 0L, GlobalStuff.TWITTER_RUNNABLE_INTERVAL);
		new Timer().scheduleAtFixedRate(createFollowRunnableTimerTask(twitter, id), 0L, followtime);
		new Timer().scheduleAtFixedRate(createBigAccRunnableTimerTask(twitter, id), 0L, bigacctime);
	}
}
