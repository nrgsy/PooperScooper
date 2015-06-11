package management;

import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.bson.Document;
import twitter4j.Twitter;
import twitterRunnables.FollowRunnable;
import twitterRunnables.TwitterRunnable;
import twitterRunnables.bigAccRunnable;

public class TimerFactory {

	public static Timer globalTimer =  new Timer();

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
				String TimerTaskID = index+"twitter";
				Maintenance.writeLog("TwitterRunnableTimerTask fired");
				if (!Maintenance.flagSet) {
				
	
					new TwitterRunnable(bird,index).run();
					
				}
				else {
					Maintenance.writeLog("Skipped creation of TwitterRunnable because maintenance "
							+ "flag is set, cancelling this timertask");
					Maintenance.runStatus.put(TimerTaskID, false);
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
				String TimerTaskID = index+"follow";
				Maintenance.writeLog("FollowRunnableTimerTask fired");
				if (!Maintenance.flagSet) {
					new FollowRunnable(bird,index).run();
				}
				else {
					Maintenance.writeLog("Skipped creation of FollowRunnable because maintenance "
							+ "flag is set, cancelling this timertask");
					Maintenance.runStatus.put(TimerTaskID, false);
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
				String TimerTaskID = index+"bigAcc";
				Maintenance.writeLog("BigAccRunnableTimerTask fired");
				if (!Maintenance.flagSet) {
					updateRuns(TimerTaskID);
					if(GlobalStuff.numberOfRuns.get(TimerTaskID)==GlobalStuff.BIG_ACCOUNT_RUNS){
						new bigAccRunnable(bird,index, DataBaseHandler.getBigAccountHarvestIndex(index)).run();
					}}
				else{
					Maintenance.writeLog("Skipped creation of bigAccRunnable because maintenance "
							+ "flag is set, cancelling this timertask");
					Maintenance.runStatus.put(TimerTaskID, false);
					this.cancel();
				}

			}
		};
	}
	private static void updateRuns(String TimerTaskID){
		GlobalStuff.numberOfRuns.put(TimerTaskID, 
				GlobalStuff.numberOfRuns.get(TimerTaskID)+1);
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
	public static void createAllSchwergsyTimers() throws UnknownHostException, Exception {
		Maintenance.writeLog("Creating globalTimer");

		globalTimer = new Timer();

		Maintenance.writeLog("Creating timers for all accounts");

		for(int id = 0; id < DataBaseHandler.getCollectionSize("SchwergsyAccounts"); id++) {
			if(!DataBaseHandler.isSuspended(id)){
				createTimers(id);
			}
			else{
				Maintenance.writeLog("***WARNING*** Timers not created for index: " + id +" due to suspension.");
			}
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
		long  TwitterRunnableInterval = GlobalStuff.TWITTER_RUNNABLE_INTERVAL;

		globalTimer.scheduleAtFixedRate(createTwitterRunnableTimerTask(twitter, id), 0L, GlobalStuff.MINUTE_IN_MILLISECONDS);
		globalTimer.scheduleAtFixedRate(createFollowRunnableTimerTask(twitter, id), 0L, followtime);
		globalTimer.scheduleAtFixedRate(createBigAccRunnableTimerTask(twitter, id), 0L, GlobalStuff.MINUTE_IN_MILLISECONDS);
	}
}
