package management;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import content.RedditScraper;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitterRunnables.TwitterRunnable;

public class MongoFuckAround {

	public static ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();

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


		//		TimerTask task = new TimerTask() {
		//
		//			@Override
		//			public void run() {
		//
		//				System.out.println("running");
		//				try {
		//					Thread.sleep(5000);
		//				} catch (InterruptedException e) {
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
		//				System.out.println("done running");
		//
		//
		//			}
		//		};
		//
		//
		//
		//		new Timer().scheduleAtFixedRate(task, 0L, 1000);

		//		String type = "pendingass";
		//		String baseType;
		//		String prefix;
		//		
		//		baseType = type.substring(6);
		//		prefix = type.substring(6, type.length());
		//
		//		System.out.println(baseType);
		//		System.out.println(prefix);

		//		System.out.println(DataBaseHandler.getFollowingSize(0));
		//		System.out.println(DataBaseHandler.getFollowingSize(1));
		//		System.out.println(DataBaseHandler.getFollowingSize(2));


		//TODO ConcurrentHashMap

		map.put(1, 0);
		map.put(2, 0);
		
		Integer x = map.get(3);
		Integer y = map.get(1);

		
		System.out.println(x);
		System.out.println(y);


//		new Thread() {
//			public void run() {
//				while (true) {
//					map.put(1, map.get(1) + 1);
//					//map.put(2, map.get(2) + 1);
//				}
//			}
//		}.start();
//		
//		new Thread() {
//			public void run() {
//				while (true) {
//					map.put(1, map.get(1) * -1);
//					//map.put(2, map.get(2) * -1);
//				}
//			}
//		}.start();
//
//		while (true) {
//			
//			System.out.println("1 is: " + map.get(1));
//			//System.out.println("2 is: " + map.get(2));
//
//			
//			Thread.sleep(1000);
//		}






	}
}
