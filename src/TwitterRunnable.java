import java.io.File;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterRunnable implements Runnable {
	Twitter bird = null;

	public TwitterRunnable (int index){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("/*INSERT DAL STUFF*/")
		.setOAuthConsumerSecret("/*INSERT DAL STUFF*/")
		.setOAuthAccessToken("/*INSERT DAL STUFF*/")
		.setOAuthAccessTokenSecret("/*INSERT DAL STUFF*/");
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
	}
	
	public TwitterRunnable(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("uHQV3x8pHZD7jzteRwUIw")
		.setOAuthConsumerSecret("OxfLKbnhfvPB8cpe5Rthex1yDR5l0I7ztHLaZXnXhmg")
		.setOAuthAccessToken("2175141374-5Gg6WRBpW1NxRMNt5UsEUA95sPVaW3a566naNVI")
		.setOAuthAccessTokenSecret("Jz2nLsKm59bbGwCxtg7sXDyfqIo7AqO6JsvWpGoEEux8t");
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
	}
	
	
	
	public void uploadPic(File file, String message,Twitter twitter) throws Exception  {
	    try{
	        StatusUpdate status = new StatusUpdate(message);
	        status.setMedia(file);
	        twitter.updateStatus(status);}
	    catch(TwitterException e){
	        System.out.println("Pic Upload error" + e.getErrorMessage());
	        throw e;
	    }
	}
	
	
	
	public void run(){
		try {
			Status status = bird.updateStatus("Got that morning pump...");
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}