package management;



import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitterRunnables.TwitterRunnable;

public class MongoFuckAround {

	public static void main(String[]args) throws Exception{
		
//		DataBaseHandler.newContent("dude in skirt", "http://i.imgur.com/vx3glwO.jpg", "pendingass");
//		DataBaseHandler.newContent("girl in a bed", "http://i.imgur.com/x0ZSk6c.jpg", "pendingass");
//		DataBaseHandler.newContent("archer", "http://i.imgur.com/Rsed2ln.jpg", "pendingass");
//		DataBaseHandler.newContent("lawn", "http://i.imgur.com/gg0gBej.jpg", "pendingass");
		
		//System.out.println(DataBaseHandler.getOneToFollow(0));

		//DataBaseHandler.moveBigAccountToEnd(0, 0);
		//DataBaseHandler.insertSchwergsyAccount("WorkoutGetSwole", "uHQV3x8pHZD7jzteRwUIw", "OxfLKbnhfvPB8cpe5Rthex1yDR5l0I7ztHLaZXnXhmg", "2175141374-5Gg6WRBpW1NxRMNt5UsEUA95sPVaW3a566naNVI", "Jz2nLsKm59bbGwCxtg7sXDyfqIo7AqO6JsvWpGoEEux8t", false, false);
		//System.out.println(DataBaseHandler.getOneToFollow(0));
		//System.out.println(DataBaseHandler.getFollowersSize(0));
		
		
		//DataBaseHandler.prettyPrintStatistics("HandsomeBeards");
		
		Timer timer = new Timer();
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				

				System.out.println("yo");
			
			}
		};
		
		TimerTask task2 = new TimerTask() {
			@Override
			public void run() {
				

				System.out.println("eyyy");
				
			}
		};

		
		timer.scheduleAtFixedRate(task, 0L, 700);
		timer.scheduleAtFixedRate(task2, 0L, 500);
		
		System.out.println("cancelling");

		//timer.cancel();
		//timer.purge();
		
		//timer = new Timer();
		
		//timer.scheduleAtFixedRate(task2, 0L, 500);

		
		
	}
}
