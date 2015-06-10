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

import org.bson.Document;

import content.RedditScraper;

//sets the global maintenance flag for director
public class Maintenance {

	//The key is index + runnable type
	public static HashMap<String, Boolean> runStatus;

	//a list containing _id's of Schwergsy Accounts that will be deleted on
	//the next maintenance run
	public static ArrayList<Integer> doomedAccounts;

	//flag that determines whether maintenance is occuring (runnables check this and pause themselves)
	public static boolean flagSet;

	private static void resetBigAccountHarvestIndexes() throws UnknownHostException {
		for(int index = 0; index<DataBaseHandler.getCollectionSize("SchwergsyAccounts"); index++){
			DataBaseHandler.editBigAccountHarvestIndex(index, 0);
		}
	}

	private static void cleanBigAccs() throws UnknownHostException, FuckinUpKPException{
		for(int index = 0; index<DataBaseHandler.getCollectionSize("SchwergsyAccounts"); index++){
			HashSet<Long> bigAccWhiteListSet = new HashSet<Long>();
			ArrayList<Document> bigAcc = DataBaseHandler.getSchwergsyAccountArray(index, "bigAccounts");
			if(DataBaseHandler.getSchwergsyAccountArray(index, "bigAccountsWhiteList") != null){
				bigAccWhiteListSet = new HashSet((ArrayList<Long>)DataBaseHandler.getSchwergsyAccountArray(index, "bigAccountsWhiteList"));
			}
			HashSet<Long> toAddToBigAccWhiteList = new HashSet<Long>();

			for(Document bigAccount : bigAcc){
				if(!bigAccWhiteListSet.contains(bigAccount.getLong("user_id"))){
					toAddToBigAccWhiteList.add(bigAccount.getLong("user_id"));
				}
			}

			DataBaseHandler.addBigAccountsWhiteList(index, new ArrayList<Long>(toAddToBigAccWhiteList));
		}
	}

	private static void cleanToFollows() throws UnknownHostException, FuckinUpKPException{
		for(int index = 0; index<DataBaseHandler.getCollectionSize("SchwergsyAccounts"); index++){
			ArrayList<Long> toFollow = DataBaseHandler.getSchwergsyAccountArray(index, "toFollow");
			ArrayList<Long> whiteList = DataBaseHandler.getSchwergsyAccountArray(index, "whiteList");
			HashSet<Long> toAddToWhiteList = new HashSet<Long>();
			HashSet<Long> whiteListSet = new HashSet<Long>();

			for(Long id : whiteList){
				whiteListSet.add(id);
			}

			for(Long id : toFollow){
				if(!whiteListSet.contains(id)){
					toAddToWhiteList.add(id);
				}
			}
			DataBaseHandler.addWhitelist(index, new ArrayList<Long>(toAddToWhiteList));	
		}
	}

	public static void performMaintenance() throws Exception {

		if (flagSet) {
			Maintenance.writeLog("WARNING: Cannot perform maintenance while maintenance is already"
					+ " running (flagSet was true). Exiting performMaintenance", "maintenance");
			return;
		}

		Maintenance.writeLog("Maintenance Started", "maintenance");
		long ogStartTime = new Date().getTime();
		flagSet = true;

		//chill in an infinite loop until all the threads kill themselves
		boolean somethingStillRunning = true;
		while (somethingStillRunning) {

			//look for a status that's true (indicating that something's still running)
			somethingStillRunning = false;
			for (Entry<String, Boolean> status : runStatus.entrySet()) {
				if (status.getValue()) {
					Maintenance.writeLog(status.getKey()+ " is still running. Waiting for it to end...", "maintenance");
					somethingStillRunning = true;
					break;
				}
			}

			if (somethingStillRunning) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		Maintenance.writeLog("It took " + (new Date().getTime() - ogStartTime)
				+ " ms for all the timers to die", "maintenance");

		//Section that doesn't use api calls (runs first because we wait 15 min before using any api calls)
		///////////////////////////////////////////////////////////////////////////////////////////////
		long nonAPIstartTime = new Date().getTime();

		DataBaseHandler.removeSchwergsyAccountsAndRemapIDs();

		//get the global variables from the GlobalVariables collection to set the ones in GlobalStuff
		try {
			DataBaseHandler.findAndSetGlobalVars();
		} catch (UnknownHostException e) {
			Maintenance.writeLog("***ERROR*** failed to find ***ERROR***", "maintenance");
			e.printStackTrace();
		}

		//cleans up and syncs bigAccounts with bigAccountsWhiteList
		try {
			cleanBigAccs();
		} catch (UnknownHostException | FuckinUpKPException e) {
			Maintenance.writeLog("***ERROR*** failed to clean BigAccs ***ERROR***", "maintenance");
			e.printStackTrace();
		}

		//cleans up and syncs toFollow with whiteList
		try {
			cleanToFollows();
		} catch (UnknownHostException | FuckinUpKPException e) {
			Maintenance.writeLog("***ERROR*** failed to clean ToFollows ***ERROR***", "maintenance");
			e.printStackTrace();
		}

		//resets bigAccountHarvestIndexes to 0
		try {
			resetBigAccountHarvestIndexes();
		} catch (UnknownHostException e) {
			Maintenance.writeLog("***ERROR*** failed to reset bigAccountHarvestIndexes ***ERROR***",
					"maintenance");
			e.printStackTrace();
		}

		Maintenance.writeLog("It took " + (new Date().getTime() - nonAPIstartTime)
				+ " ms for the non-API-calling section to complete", "maintenance");
		///////////////////////////////////////////////////////////////////////////////////////////////

		//don't start api call section until 15 minutes from start has passed
		Maintenance.writeLog("Waiting 15 minutes for rate limits to reset", "maintenance");
		while ((new Date().getTime()) < nonAPIstartTime + GlobalStuff.MINUTE_IN_MILLISECONDS * 15) {
			//wait 10 seconds before trying again
			Thread.sleep(10000);
		}

		//Section that uses api calls
		///////////////////////////////////////////////////////////////////////////////////////////////
		long APIstartTime = new Date().getTime();

		//update followers for each schwergsy account
		long size = DataBaseHandler.getCollectionSize("SchwergsyAccounts");
		for (int i = 0; i < size; i++) {
			DataBaseHandler.updateFollowers(i);
		}

		flagSet = false;
		
		//start all the timers because they all suicide when they see maintenance flag is set
		TimerFactory.createTimers();
		new Thread(new RedditScraper()).start();

		Maintenance.writeLog("It took " + (new Date().getTime() - APIstartTime)
				+ " ms for the API-calling section to complete", "maintenance");
		///////////////////////////////////////////////////////////////////////////////////////////////

		Maintenance.writeLog("Maintenance Complete, total time elapsed = " +
				(new Date().getTime() - ogStartTime) + " ms", "maintenance");
	}

	/**
	 * Prints the message to console and writes it to the appropriate log
	 * 
	 * @param message the message to print
	 * @param subDir The directory within the logs folder that this log will be put in/written to
	 */
	public static void writeLog(String message, String subDir) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date now = new Date();
		String strDate = sdf.format(now);
		String output = strDate + " ----- " + message;
		System.out.println(output);

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
	 */
	public static void writeLog(String message, int index) { 
		String name = (String) DataBaseHandler.getSchwergsyAccount(index).get("name");
		writeLog(message, name);
	}

	public static void writeLog(String message) { writeLog(message, null); }

	public static void writeLog() { writeLog(""); }
}