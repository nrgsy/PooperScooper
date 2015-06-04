package management;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

//sets the global maintenance flag for director
public class Maintenance {

	//The key is index + runnable type
	public static HashMap<String, Boolean> runStatus; 

	//flag that determines whether maintenance is occuring (runnables check this and pause themselves)
	public static boolean flagSet;

	public static void cleanBigAccs() throws UnknownHostException, FuckinUpKPException{
		for(int index = 0; index<DataBaseHandler.getCollectionSize("SchwergsyAccounts"); index++){
			Long[] bigAcc = DataBaseHandler.getSchwergsyAccountArray(index, "bigAccounts").toArray(new Long[DataBaseHandler.getSchwergsyAccountArraySize(index, "bigAccounts")]);
			Long[] bigAccWhiteList = DataBaseHandler.getSchwergsyAccountArray(index, "bigAccountsWhiteList").toArray(new Long[DataBaseHandler.getSchwergsyAccountArraySize(index, "bigAccountsWhiteList")]);
			HashSet<Long> toAddToBigAccWhiteList = new HashSet<Long>();
			HashSet<Long> bigAccWhiteListSet = new HashSet<Long>();

			for(Long id : bigAccWhiteList){
				bigAccWhiteListSet.add(id);
			}

			for(Long id : bigAcc){
				if(!bigAccWhiteListSet.contains(id)){
					toAddToBigAccWhiteList.add(id);
				}
			}

			for(Long id : toAddToBigAccWhiteList){
				DataBaseHandler.addBigAccWhiteList(index, id);
			}
		}
	}

	public static void cleanToFollows() throws UnknownHostException, FuckinUpKPException{
		for(int index = 0; index<DataBaseHandler.getCollectionSize("SchwergsyAccounts"); index++){
			Long[] toFollow = DataBaseHandler.getSchwergsyAccountArray(index, "toFollow").toArray(new Long[DataBaseHandler.getToFollowSize(index)]);
			Long[] whiteList = DataBaseHandler.getSchwergsyAccountArray(index, "bigAccountsWhiteList").toArray(new Long[DataBaseHandler.getSchwergsyAccountArraySize(index, "whiteList")]);
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
			DataBaseHandler.addWhitelist(index, toAddToWhiteList.toArray(new Long[toAddToWhiteList.size()]));	
		}
	}

	public static void performMaintenance() throws Exception {
		Maintenance.writeLog("Maintenance Started");
		long ogStartTime = new Date().getTime();
		flagSet = true;

		//chill in an infinite loop until all the threads kill themselves
		boolean somethingStillRunning = true;
		while (somethingStillRunning) {

			//look for a status that's true (indicating that something's still running)
			somethingStillRunning = false;
			for (boolean status : runStatus.values()) {
				if (status) {
					somethingStillRunning = true;
					break;
				}
			}

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Maintenance.writeLog("It took " + (new Date().getTime() - ogStartTime)
				+ " ms for all the timers to die");

		/*TODO the actual maintenance, Order task such that fastest tasks are done first,
		 * consider having a maintenance cutoff if it runs for like 4 hours
		 * old content garbage collection
		 * sweep through links in all pending and regular content checking for validity
		 * get big accounts (because of high api call amount)
		 */

		//Section that doesn't use api calls (runs first because we wait 15 min before using any api calls)
		///////////////////////////////////////////////////////////////////////////////////////////////
		long nonAPIstartTime = new Date().getTime();

		//get the global variables from the GlobalVariables collection to set the ones in GlobalStuff
		try {
			DataBaseHandler.findAndSetGlobalVars();
		} catch (UnknownHostException e) {
			Maintenance.writeLog("***ERROR*** failed to find ***ERROR***");
			e.printStackTrace();
		}

		//cleans up and syncs bigAccounts with bigAccountsWhiteList
		try {
			cleanBigAccs();
		} catch (UnknownHostException | FuckinUpKPException e) {
			Maintenance.writeLog("***ERROR*** failed to clean BigAccs ***ERROR***");
			e.printStackTrace();
		}

		//cleans up and syncs toFollow with whiteList
		try {
			cleanToFollows();
		} catch (UnknownHostException | FuckinUpKPException e) {
			Maintenance.writeLog("***ERROR*** failed to clean ToFollows ***ERROR***");
			e.printStackTrace();
		}

		Maintenance.writeLog("It took " + (new Date().getTime() - nonAPIstartTime)
				+ " ms for the non-API-calling section to complete");
		///////////////////////////////////////////////////////////////////////////////////////////////

		//don't start api call section until 15 minutes from start has passed
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

		//start all the timers because they all suicide when they see maintenance flag is set
		TimerFactory.createTimers();

		Maintenance.writeLog("It took " + (new Date().getTime() - APIstartTime)
				+ " ms for the API-calling section to complete");
		///////////////////////////////////////////////////////////////////////////////////////////////

		flagSet = false;
		Maintenance.writeLog("Maintenance Complete, total time elapsed = " +
				(new Date().getTime() - ogStartTime) + " ms");
	}

	//TODO add parameters indicating which schwergsy account the message is associated with
	public static void writeLog(String message, String subDir) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date now = new Date();
		String strDate = sdf.format(now);
		String output = strDate + " ----- " + message;
		System.out.println(output);
		
		if (subDir == null) {
			subDir = "default"; 
		}
		
		String dir = "logs/" + subDir + "/";
		
		if (!new File(dir).exists()) {
			new File(dir).mkdirs();
		}
		Calendar cal = Calendar.getInstance();
				
		//name the file the current date
		String fileName = dir + (cal.get(Calendar.MONTH) + 1) +  "-" + cal.get(Calendar.DAY_OF_MONTH) +
				 "-" + cal.get(Calendar.YEAR);
		
		try {
		    FileWriter fw = new FileWriter(fileName,true); //the true will append the new data
		    fw.write(output + "\n"); //appends the string to the file
		    fw.close();
		} catch (IOException e) {
			System.out.println("***ERROR*** Failed to write to log file ***ERROR***");
			e.printStackTrace();
			return;
		}
	}

	public static void writeLog(String message) { writeLog(message, null); }

	public static void writeLog() { writeLog(""); }
}