package management;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;

import content.RedditScraper;

//sets the global maintenance flag for director
public class Maintenance {

	//The key is index + runnable type
	public static HashMap<String, Boolean> runStatus;

	//flag that determines whether maintenance is occuring (runnables check this and pause themselves)
	public static boolean flagSet;

	private static void resetBigAccountHarvestIndexes() throws UnknownHostException {
		for (int index = 0; index < DataBaseHandler.getCollectionSize("SchwergsyAccounts"); index++) {
			DataBaseHandler.editBigAccountHarvestIndex(index, 0);
		}
	}

	private static void cleanToFollows() throws UnknownHostException, FuckinUpKPException {
		for (int index = 0; index < DataBaseHandler.getCollectionSize("SchwergsyAccounts"); index++) {
			ArrayList<Long> toFollow = DataBaseHandler.getSchwergsyAccountArray(index, "toFollow");
			ArrayList<Long> whiteList = DataBaseHandler.getSchwergsyAccountArray(index, "whiteList");
			HashSet<Long> toAddToWhiteList = new HashSet<Long>();
			HashSet<Long> whiteListSet = new HashSet<Long>();

			for (Long id : whiteList) {
				whiteListSet.add(id);
			}

			for (Long id : toFollow) {
				if (!whiteListSet.contains(id)) {
					toAddToWhiteList.add(id);
				}
			}
			DataBaseHandler.addWhitelist(index, new ArrayList<Long>(toAddToWhiteList));
		}
	}

	/**
	 * Sets the flag to true and waits for the schwergsy timertasks to cancel.
	 * WARNING DOES NOT UNSET MAINTENACE FLAG
	 */
	public static void safeShutDownAccounts() {

		Maintenance.writeLog("Shutting down accounts", "maintenance");

		flagSet = true;
		boolean somethingStillRunning = true;
		while (somethingStillRunning) {

			//look for a status that's true (indicating that something's still running)
			somethingStillRunning = false;
			for (Entry<String, Boolean> status : runStatus.entrySet()) {
				if (status.getValue()) {
					somethingStillRunning = true;
					break;
				}
			}

			if (somethingStillRunning) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Maintenance.writeLog("Something fucked up in safeShutDownAccounts\n" + 
							Maintenance.getStackTrace(e), null, -1);
				}
			}
		}
		Maintenance.writeLog("All accounts shut down successfully", "maintenance");
	}

	/**
	 * Sets the scrapers' shutdownRequest to true and waits for the content snatch to finish.
	 * WARNING DOES NOT UNSET shutdownRequest
	 */
	public static void safeShutDownScrapers() {

		Maintenance.writeLog("Shutting down scrapers", "maintenance");

		RedditScraper.shutdownRequest = true;
		while (RedditScraper.isSnatching) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Maintenance.writeLog("Something fucked up in safeShutDownScrapers\n" +
						Maintenance.getStackTrace(e), "maintenance", -1);
			}
		}

		Maintenance.writeLog("All scrapers shut down successfully", "maintenance");
		deleteResidualPics();
	}


	//wait for everything to stop and exit
	public static void safeShutdownSystem() {
		try {

			if (flagSet) {
				Maintenance.writeLog("Waiting for Maintenance flag to become false", "maintenance");
			}			
			//wait for maintenance to complete if running;
			while (flagSet) {
				Thread.sleep(1000);
			}

			//shutdown (leaves maintenance flag as true)
			safeShutDownAccounts();
			//leaves shutdown request as true
			safeShutDownScrapers();

			Maintenance.writeLog("Shutdown Complete", "maintenance");
			System.exit(0);
		}
		catch (Exception e) {
			Maintenance.writeLog("Safe shutdown sequence fucked up. YOLO shutting down anyway\n" + 
					Maintenance.getStackTrace(e), "maintenance", -1);
			System.exit(0);
		}
	}

	/**
	 * Deletes any pics in the GlobalStuff.PICS_DIR directory if any exist
	 */
	public static void deleteResidualPics() {

		Maintenance.writeLog("Deleting all pics from " + GlobalStuff.PICS_DIR + " if any exist",
				"maintenance");
		File picsDir = new File(GlobalStuff.PICS_DIR);
		for(File f : picsDir.listFiles()) {
			f.delete(); 
		} 
	}

	/**
	 * Attempt maintenance, stop it if too much time passes
	 * 
	 * @throws Exception
	 */
	public static void attemptMaintenance() {

		//run maintenance on a separate thread so it can be cancelled if it takes too long
		Thread maintenanceThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Maintenance.performMaintenance();
				} catch (Exception e) {
					Maintenance.writeLog("Something fucked up in the performMaintenance\n" + 
							Maintenance.getStackTrace(e), -1);
				}
			}
		});

		long endTimeMillis = System.currentTimeMillis() + GlobalStuff.MAX_MAINTENANCE_RUN_TIME;

		maintenanceThread.start();

		//monitor it and shut it down if it takes too long. Sets Maintenance scrapers flags to
		//false just in case perform maintenance hasn't gotten to it yet.
		while (maintenanceThread.isAlive()) {

			if (System.currentTimeMillis() > endTimeMillis) {

				Maintenance.writeLog("Maintenance attempt unsuccessful. maintenanceThread was alive "
						+ "for too long. Interrupting maintenanceThread.", "maintenance", -1);
				maintenanceThread.interrupt();
				return;
			}
			//so that we're not checking constantly, just once a second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Maintenance.writeLog("Sleep interrupted for some reason", "maintenance", -1);
			}
		}
		Maintenance.writeLog("Maintenance Attempt Complete", "maintenance");
	}

	/**
	 * Do the maintenance, usually runs daily. This can be interrupted if it takes too long
	 * 
	 * @throws Exception
	 */
	public static void performMaintenance() {

		try {

			TimerFactory.globalTimer.cancel();
			TimerFactory.globalTimer.purge();

			if (flagSet) {
				Maintenance.writeLog("Cannot perform maintenance while maintenance is already"
						+ " running (flagSet was true). Exiting performMaintenance", "maintenance", 1);
				return;
			}

			Maintenance.writeLog("Maintenance Started", "maintenance");
			long ogStartTime = new Date().getTime();

			//shutdown (leaves maintenance flag as true)
			safeShutDownAccounts();
			//leaves shutdown request as true
			safeShutDownScrapers();

			Maintenance.writeLog("It took " + (new Date().getTime() - ogStartTime)
					+ " ms for all the timers to die", "maintenance");

			//Section that doesn't use api calls (runs first because we wait 15 min before using any api calls)
			///////////////////////////////////////////////////////////////////////////////////////////////
			long nonAPIstartTime = new Date().getTime();





			//TODO rewrite this so setting 
			//remove any accounts flagged for deletion and remap all ids
			//DataBaseHandler.removeSchwergsyAccountsAndRemapIDs();







			//get the global variables from the GlobalVariables collection to set the ones in GlobalStuff
			DataBaseHandler.findAndSetGlobalVars();

			//cleans up and syncs toFollow with whiteList
			try {
				cleanToFollows();
			} catch (UnknownHostException | FuckinUpKPException e) {
				Maintenance.writeLog("failed to clean ToFollows\n" +
						Maintenance.getStackTrace(e), "maintenance", -1);
			}

			//resets bigAccountHarvestIndexes to 0
			try {
				resetBigAccountHarvestIndexes();
			} catch (UnknownHostException e) {
				Maintenance.writeLog("failed to reset bigAccountHarvestIndexes\n" +
						Maintenance.getStackTrace(e), "maintenance", -1);
			}

			Maintenance.writeLog("It took " + (new Date().getTime() - nonAPIstartTime)
					+ " ms for the non-API-calling section to complete", "maintenance");
			///////////////////////////////////////////////////////////////////////////////////////////////

			//don't start api call section until 15 minutes from start has passed
			Maintenance.writeLog("Waiting 15 minutes for rate limits to reset", "maintenance");
			while ((new Date().getTime()) < nonAPIstartTime + GlobalStuff.MAINTENANCE_SNOOZE_TIME) {
				//wait 10 seconds before trying again
				Thread.sleep(10000);
			}

			//Section that uses api calls
			///////////////////////////////////////////////////////////////////////////////////////////////
			long APIstartTime = new Date().getTime();

			//update followers for each schwergsy account
			long size = DataBaseHandler.getCollectionSize("SchwergsyAccounts");
			for (int i = 0; i < size; i++) {
				if (!DataBaseHandler.isSuspended(i)) {
					DataBaseHandler.updateFollowers(i);
				}
			}

			//unset maintenance and scraper flags
			RedditScraper.shutdownRequest = false;
			flagSet = false;

			//start all the timers because they all suicide when they see maintenance flag is set
			TimerFactory.scheduleAllSchwergsyTimers();
			new Thread(new RedditScraper()).start();

			Maintenance.writeLog("It took " + (new Date().getTime() - APIstartTime)
					+ " ms for the API-calling section to complete", "maintenance");
			///////////////////////////////////////////////////////////////////////////////////////////////

			Maintenance.writeLog("Maintenance Complete, total time elapsed = " +
					(new Date().getTime() - ogStartTime) + " ms", "maintenance");
		}
		catch(InterruptedException e) {
			//Here we recover nice from an interruption trigger when perform maintenance takes too long
			Maintenance.writeLog("performMaintenance was interrupted. Recovering safely"
					+ " and restarting scrapers and schwergsy timers", "maintenance", 1);

			TimerFactory.globalTimer.cancel();
			TimerFactory.globalTimer.purge();

			//shutdown everything just in case
			safeShutDownAccounts();	//leaves maintenance flag as true
			safeShutDownScrapers(); //leaves shutdown request as true
			//unset maintenance and scraper flags
			RedditScraper.shutdownRequest = false;
			Maintenance.flagSet = false;

			TimerFactory.scheduleAllSchwergsyTimers();
			new Thread(new RedditScraper()).start();
		}
		catch(Exception e) {
			//Do the same as the above catch block, but print that's its an error
			
			Maintenance.writeLog("Something unexpected happened in performMaintenance\n" +
					Maintenance.getStackTrace(e), "maintenance", -1);

			TimerFactory.globalTimer.cancel();
			TimerFactory.globalTimer.purge();

			//shutdown everything just in case
			safeShutDownAccounts();	//leaves maintenance flag as true
			safeShutDownScrapers(); //leaves shutdown request as true
			//unset maintenance and scraper flags
			RedditScraper.shutdownRequest = false;
			Maintenance.flagSet = false;

			TimerFactory.scheduleAllSchwergsyTimers();
			new Thread(new RedditScraper()).start();
		}
	}

	/**
	 * Prints the message to console and writes it to the appropriate log
	 * 
	 * @param message the message to print
	 * @param subDir The directory within the logs folder that this log will be put in/written to\
	 * @param errorStatus indicates the error status of the message to be printed; -1 indicates error,
	 * 1 indicates warning, 0 indicates that its a regular message (not error or warning)
	 */
	public static void writeLog(String message, String subDir, int errorStatus) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date now = new Date();
		String strDate = sdf.format(now);

		String errorString;
		switch (errorStatus) {
		case -1 :
			errorString = " ***ERROR*** ";
			//also write all errors to the KP directory
			writeLog(errorString + message + errorString, "KP");
			break;
		case 0 :
			errorString = " ";
			break;
		case 1 :
			errorString = " ***WARNING*** ";
			break;
		default :
			System.out.println("***ERROR*** Unexpected argument for errorStatus ***ERROR***");
			return;
		}

		String output = strDate + " --" + errorString + message + errorString;

		//only write to console if not an error (because it will write to console in the case statement
		//above when it recursively called writeLog)
		if (errorStatus != -1) {
			System.out.println(output);
		}

		if (subDir == null) {
			subDir = "default"; 
		}

		String dir = GlobalStuff.LOG_DIRECTORY + subDir + "/";

		if (!new File(dir).exists()) {
			new File(dir).mkdirs();
		}
		Calendar cal = Calendar.getInstance();

		//name the file the current date
		String fileName = dir + (cal.get(Calendar.MONTH) + 1) +  "-" + cal.get(Calendar.DAY_OF_MONTH) +
				"-" + cal.get(Calendar.YEAR) + ".txt";

		try {
			FileWriter fw = new FileWriter(fileName,true); //the true will append the new data
			fw.write(output + "\n"); //appends the string to the file
			fw.close();

			//also write the message to allLogs
			FileWriter fw2 = new FileWriter(
					GlobalStuff.LOG_DIRECTORY + (cal.get(Calendar.MONTH) + 1) +  "-" +
							cal.get(Calendar.DAY_OF_MONTH) + "-" +
							cal.get(Calendar.YEAR) + "-allLogs.txt", true);
			fw2.write(output + "\n");
			fw2.close();
		} catch (IOException e) {
			System.out.println("***ERROR*** Failed to write to log file ***ERROR***");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * @param index The index of the schwergsy account
	 * @param errorStatus the error status of the message
	 */
	public static void writeLog(String message, int index, int errorStatus) { 
		String name = (String) DataBaseHandler.getSchwergsyAccount(index).get("name");
		writeLog(message, name, errorStatus);
	}

	//default error staus is 0 (normal message, not error or warning)
	public static void writeLog(String message, String subDir) {
		writeLog(message, subDir, 0);
	}

	/**
	 * @param index The index of the schwergsy account
	 */
	public static void writeLog(String message, int index) { 
		writeLog(message, index, 0);
	}

	public static void writeLog(String message) {
		writeLog(message, null);
	}

	public static String getStackTrace(Exception e){
		String error = e.toString() + "\n";
		for(StackTraceElement elem : e.getStackTrace()){
			error += elem.toString();
			error += "\n";
		}
		return error;
	}
}