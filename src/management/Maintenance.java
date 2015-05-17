package management;

import java.net.UnknownHostException;
import java.util.HashMap;

//sets the global maintenance flag for director
public class Maintenance {
	
	//Phal can i get a comment describing this? especially what makes up the String key
	public static HashMap<String, Boolean> runStatus; 
	
	//flag that determines whether maintenance is occuring (runnables check this and pause themselves)
	public static boolean flagSet;
	
	public static void performMaintenance() {
		System.out.println("maintenance started");
		Maintenance.flagSet = true;				
		boolean activityExists = true;
		while (activityExists) {
			try {
				Thread.sleep(3000);
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

		/*TODO the actual maintenance, Order task such that fastest tasks are done first,
		 * consider having a maintenance cutoff if it runs for like 4 hours
		 * call dbhandler's updateFollowers method for each schwergsy account
		 * old content garbage collection
		 * sweep through links in all pending and regular content checking for validity
		 * get big accounts (because of high api call amount)
		 */

		//get the global variables from the GlobalVariables collection to set the ones in GlobalStuff
		try {
			DataBaseHandler.findAndSetGlobalVars();
		} catch (UnknownHostException e) {
			System.err.println("ERROR: failed to find ");
			e.printStackTrace();
		}

		Maintenance.flagSet = false;
		System.out.println("maintenance complete");
	}



}