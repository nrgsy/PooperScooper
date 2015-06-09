package management;



import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MongoFuckAround {

	public static void main(String[]args) throws Exception{
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("GMOjVyxhfLv2zFfPDV2fTIqb2")
		.setOAuthConsumerSecret("aP5TqAMzGQp27T6cAU6SOVz5n7yn7DmPOmCLpKl1wkI4PYB0Ak")
		.setOAuthAccessToken("2417418090-S5ufxoMkWN1inBPKUVNbUupMpmNWqZSfPLPbkre")
		.setOAuthAccessTokenSecret("JlbLo4guz0b7TVUdPnjjHQojVBDTY6HqcYy5yzGhFXWXm");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter bird = tf.getInstance();
		
		System.out.println(bird.getId());
		
		System.out.println(TwitterHandler.getUserSuggestions(bird, 0).get(0).get(3).getName());
	}
}
