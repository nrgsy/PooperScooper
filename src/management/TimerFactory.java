package management;

import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;

import content.RedditScraper;
import twitter4j.GeoLocation;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitterRunnables.FollowRunnable;
import twitterRunnables.TwitterRunnable;
import twitterRunnables.bigAccRunnable;

public class TimerFactory {

	public static Timer globalTimer;

	/**
	 * @param Twitter created by Director
	 * @param int of SchwergsAccount index
	 * @return
	 */
	private static TimerTask createTwitterRunnableTimerTask(final Twitter bird, final int index){

		Maintenance.writeLog("creating TwitterRunnableTimerTask for account " + index, index);

		return new TimerTask() {
			@Override
			public void run() {
				String TimerTaskID = index+"twitter";
				if (!Maintenance.flagSet) {
					updateRuns(TimerTaskID);
					//the number of runs before firing = the runnable interval / the fire rate 
					if(GlobalStuff.numberOfRuns.get(TimerTaskID) >=
							GlobalStuff.TWITTER_RUNNABLE_INTERVAL/GlobalStuff.TIMER_TASK_FIRE_RATE) {
						GlobalStuff.numberOfRuns.put(index+"twitter", 0);
						new TwitterRunnable(bird,index).run();
					}
				}
				else {
					Maintenance.writeLog("Skipped creation of TwitterRunnable because maintenance "
							+ "flag is set, cancelling this timertask");
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
	private static TimerTask createFollowRunnableTimerTask(final Twitter bird,
			final int index,
			final long followTime) {

		Maintenance.writeLog("creating FollowRunnableTimerTask for account " + index, index);

		return new TimerTask() {
			@Override
			public void run() {
				String TimerTaskID = index+"follow";
				if (!Maintenance.flagSet) {
					updateRuns(TimerTaskID);
					//the number of runs before firing = the runnable interval / the fire rate 
					if(GlobalStuff.numberOfRuns.get(TimerTaskID) >=
							followTime/GlobalStuff.TIMER_TASK_FIRE_RATE) {
						GlobalStuff.numberOfRuns.put(index+"follow", 0);
						new FollowRunnable(bird,index).run();
					}
				}
				else {
					Maintenance.writeLog("Skipped creation of FollowRunnable because maintenance "
							+ "flag is set, cancelling this timertask");
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
	private static TimerTask createBigAccRunnableTimerTask(final Twitter bird, final int index) {

		Maintenance.writeLog("creating BigAccRunnableTimerTask for account " + index, index);

		return new TimerTask() {					
			@Override
			public void run() {
				String TimerTaskID = index+"bigAcc";
				if (!Maintenance.flagSet) {
					updateRuns(TimerTaskID);
					//the number of runs before firing = the runnable interval / the fire rate 
					if(GlobalStuff.numberOfRuns.get(TimerTaskID) >=
							GlobalStuff.BIG_ACCOUNT_TIME/GlobalStuff.TIMER_TASK_FIRE_RATE) {
						GlobalStuff.numberOfRuns.put(index+"bigAcc", 0);
						new bigAccRunnable(bird,
								index,
								DataBaseHandler.getBigAccountHarvestIndex(index)).run();
					}
				}
				else {
					Maintenance.writeLog("Skipped creation of bigAccRunnable because maintenance "
							+ "flag is set, cancelling this timertask");
					this.cancel();
				}

			}
		};
	}
	
	
	/**
	 * @param updateTime the 
	 * @return
	 */
	public static TimerTask createRateLimitUpdateTimerTask(final long updateTime) {

		Maintenance.writeLog("creating RateLimitUpdateTimerTask", "maintenance");

		return new TimerTask() {
			@Override
			public void run() {
				String TimerTaskID = "rateLimitUpdater";
				if (!Maintenance.flagSet) {
					updateRuns(TimerTaskID);
					//the number of runs before firing = the update interval / the fire rate 
					if(GlobalStuff.numberOfRuns.get(TimerTaskID) >=
							updateTime/GlobalStuff.TIMER_TASK_FIRE_RATE) {

						//Get the actual rate limits from twitter and sync them with what is
						//in rateLimitMapMap
						for(int id = 0;
								id < DataBaseHandler.getCollectionSize("SchwergsyAccounts");
								id++) {
							if(!DataBaseHandler.isSuspended(id)){
								Document info;
								try {
									info = DataBaseHandler.getAuthorizationInfo(id);
								} catch (Exception e) {
									Maintenance.writeLog("Failed to pull authorization info from the"
											+ " database. Cannot refresh rate limits for this "
											+ "account.\n" + Maintenance.getStackTrace(e),
											id, -1);
									//reset number of runs for rateLimitUpdater
									GlobalStuff.numberOfRuns.put("rateLimitUpdater", 0);
									return;
								}	
								
								Twitter twitter = TwitterHandler.getTwitter(info);	
								ConcurrentHashMap<String, Integer> remainingCallsMap;
								try {
									remainingCallsMap = TwitterHandler.getRemainingCallsMap(
													twitter.getRateLimitStatus());			
								} catch (TwitterException e) {
									Maintenance.writeLog("Failed to update rate limit for account with"
											+ " index: " + id + "\n"
											+ Maintenance.getStackTrace(e), id, -1);
									//reset number of runs for rateLimitUpdater
									GlobalStuff.numberOfRuns.put("rateLimitUpdater", 0);
									return;
								}
								TwitterHandler.remainingCallsMapMap.put(id, remainingCallsMap);
							}
							else{
								Maintenance.writeLog("rate limits not updated for index: " +
										id + " due to suspension.", id, 1);
							}
						}
						//reset number of runs for rateLimitUpdater
						GlobalStuff.numberOfRuns.put("rateLimitUpdater", 0);
					}
				}
				else {
					Maintenance.writeLog("Rate limit update because maintenance "
							+ "flag is set, cancelling this timertask");
					this.cancel();
				}		
			}};
	}

	private static void updateRuns(String TimerTaskID){
		GlobalStuff.numberOfRuns.put(TimerTaskID, 
				GlobalStuff.numberOfRuns.get(TimerTaskID) == null ? 0 :
					(GlobalStuff.numberOfRuns.get(TimerTaskID) + 1)
				);
	}
	
	public static TimerTask createMaintenanceTimerTask() {

		Maintenance.writeLog("creating MaintenanceTimerTask", "maintenance");

		return new TimerTask() {
			@Override
			public void run() {
				Maintenance.writeLog("MaintenanceTimerTask fired", "maintenance");
				try {
					Maintenance.attemptMaintenance();
				} catch (Exception e) {
					Maintenance.writeLog("Something fucked up in TimerFactory\n" + 
							Maintenance.getStackTrace(e), null, -1);
				}
			}};
	}
	
	/**
	 * Creates the Timers for all Schwergsy accounts
	 * 
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public static void scheduleAllSchwergsyTimers() {
		
		//reset ratLimitMapMap because we're starting fresh
		TwitterHandler.remainingCallsMapMap = new ConcurrentHashMap<>();
		
		Maintenance.writeLog("Initializing globalTimer");

		globalTimer = new Timer();

		Maintenance.writeLog("Scheduling timers for all accounts");

		for(int id = 0; id < DataBaseHandler.getCollectionSize("SchwergsyAccounts"); id++) {
			if(!DataBaseHandler.isSuspended(id)){
				scheduleTimers(id);
			}
			else{
				Maintenance.writeLog("Timers not created for index: " +
						id +" due to suspension.", id, 1);
			}
		}
		
		globalTimer.scheduleAtFixedRate(
				createRateLimitUpdateTimerTask(GlobalStuff.RATE_LIMIT_UPDATE_TIME),
				0L, GlobalStuff.TIMER_TASK_FIRE_RATE);	
	}

	/**
	 * Creates the Timers for the given Schwergsy account
	 * 
	 * @param id The ID of the Schwergsy accout
	 * @throws Exception 
	 */
	public static void scheduleTimers(int id) {

		Maintenance.writeLog("Creating timers for this account", id);

		Document info;
		try {
			info = DataBaseHandler.getAuthorizationInfo(id);
		} catch (Exception e) {
			Maintenance.writeLog("Failed to pull authorization info from the database. "
					+ "Timers cannot be created for this account.\n" + Maintenance.getStackTrace(e),
					id, -1);
			return;
		}		

		long followtime_min = GlobalStuff.FOLLOW_TIME_MIN;
		long followtime_max = GlobalStuff.FOLLOW_TIME_MAX;
		long incubated_followtime_min = GlobalStuff.FOLLOW_TIME_INCUBATED_MIN;
		long incubated_followtime_max = GlobalStuff.FOLLOW_TIME_INCUBATED_MAX;

		Random r = new Random();
		long followtime = followtime_min+((long)(r.nextDouble()*(followtime_max-followtime_min)));

		long incubated_followtime = incubated_followtime_min +
				((long)r.nextDouble()*(incubated_followtime_max - incubated_followtime_min));

		//If in incubation, follows at a rate of 425 per day
		if(info.getBoolean("isIncubated")){
			followtime = incubated_followtime;
		}

		Twitter twitter = TwitterHandler.getTwitter(info);

		globalTimer.scheduleAtFixedRate(
				createTwitterRunnableTimerTask(twitter,id),
				0L, GlobalStuff.TIMER_TASK_FIRE_RATE);
		globalTimer.scheduleAtFixedRate(
				createFollowRunnableTimerTask(twitter, id ,followtime),
				0L, GlobalStuff.TIMER_TASK_FIRE_RATE);
		globalTimer.scheduleAtFixedRate(
				createBigAccRunnableTimerTask(twitter, id),
				0L, GlobalStuff.TIMER_TASK_FIRE_RATE);
	}
}
