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

		//		ConfigurationBuilder cb = new ConfigurationBuilder();
		//		cb.setDebugEnabled(true)
		//		.setOAuthConsumerKey("GMOjVyxhfLv2zFfPDV2fTIqb2")
		//		.setOAuthConsumerSecret("aP5TqAMzGQp27T6cAU6SOVz5n7yn7DmPOmCLpKl1wkI4PYB0Ak")
		//		.setOAuthAccessToken("2417418090-S5ufxoMkWN1inBPKUVNbUupMpmNWqZSfPLPbkre")
		//		.setOAuthAccessTokenSecret("JlbLo4guz0b7TVUdPnjjHQojVBDTY6HqcYy5yzGhFXWXm");
		//		TwitterFactory tf = new TwitterFactory(cb.build());
		//		Twitter bird = tf.getInstance();
		//		
		//		System.out.println(bird.getId());


		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				System.out.println("running");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("done running");


			}
		};



		new Timer().scheduleAtFixedRate(task, 0L, 1000);





	}
}
