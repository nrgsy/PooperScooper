package management;


import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;

import com.mongodb.MongoClient;

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
		then.set(Calendar.MINUTE, 0);
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
	 * @param args
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public static void main(String[]args) throws UnknownHostException, Exception {

		Maintenance.writeLog("Starting Director");
		
		//Initialize all the shit
		DataBaseHandler.mongoClient = new MongoClient();
		GlobalStuff.lastPostTimeMap = new HashMap<Integer, Long>();
		DataBaseHandler.initGlobalVars();
		DataBaseHandler.findAndSetGlobalVars();
		Maintenance.runStatus = new HashMap<>();
		Maintenance.doomedAccounts = new ArrayList<Integer>();

		Date nextOccurrenceOf3am = getNextTime(new Date(), 3);
		//The timer who's task fires once a day to do the maintenance tasks
		new Timer().scheduleAtFixedRate(
				TimerFactory.createMaintenanceTimerTask(),
				nextOccurrenceOf3am,
				GlobalStuff.DAY_IN_MILLISECONDS);

		//create the initial timers
		TimerFactory.createTimers();
	}
}
