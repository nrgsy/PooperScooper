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


		final int numCalls = 5;
		
		map.put(1, 100);

		Integer x = map.get(1);
		
		System.out.println(x);

		
		new Timer().schedule(new TimerTask() {
			@Override
	        public void run() {
				int incrementedNumOfRemainingCalls =
						map.get(1) + numCalls;
				map.put(1, incrementedNumOfRemainingCalls);
	        }	
		}, 2000); 
		
		map.put(1, 50);

		System.out.println(map.get(1));
		
		Thread.sleep(3000);

		System.out.println(map.get(1));





	}
}
