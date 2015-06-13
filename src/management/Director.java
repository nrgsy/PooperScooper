package management;


import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.mongodb.MongoClient;

import content.RedditScraper;

/**
 * @author Bojangles and McChrpchrp
 *
 */
public class Director {
	
	/**
	 * @param base
	 * @param hourOfDay
	 * @return
	 */
	public static Date getNextTime(Date base, int hourOfDay) {
		Calendar then = Calendar.getInstance();
		then.setTime(base);
		then.set(Calendar.HOUR_OF_DAY, hourOfDay);
		then.set(Calendar.MINUTE, 00);
		then.set(Calendar.SECOND, 0);
		then.set(Calendar.MILLISECOND, 0);
		if (then.getTime().before(base)) {
			then.add(Calendar.DAY_OF_YEAR, 1);
		}
		return then.getTime();
	}

	/**
	 * Runs all the threads and initializes the volatile variables in GlobalStuff
	 * 
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public static void runDirector() throws Exception {

			//Initialize all the shit
			DataBaseHandler.initGlobalVars();
			DataBaseHandler.findAndSetGlobalVars();
			Maintenance.writeLog("Starting Director");
			DataBaseHandler.mongoClient = new MongoClient();
			GlobalStuff.lastPostTimeMap = new HashMap<>();
			Maintenance.runStatus = new HashMap<>();
			Maintenance.doomedAccounts = new ArrayList<>();
			GlobalStuff.numberOfRuns = new HashMap<>();
			TimerFactory.globalTimer = new Timer();
			Date nextOccurrenceOf3am = getNextTime(new Date(), 3);
			//The timer who's task fires once a day to do the maintenance tasks
			new Timer().scheduleAtFixedRate(
					TimerFactory.createMaintenanceTimerTask(),
					nextOccurrenceOf3am,
					GlobalStuff.DAY_IN_MILLISECONDS);

			//create the initial timers
			TimerFactory.scheduleAllSchwergsyTimers();
			new Thread(new RedditScraper()).start();

	}
	
}
