import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterRunnable implements Runnable {
	private Twitter bird = null;

	public TwitterRunnable (String OAuthConsumerKey, String OAuthConsumerSecret, String OAuthAccessToken, String OAuthAccessTokenSecret){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(OAuthConsumerKey)
		  .setOAuthConsumerSecret(OAuthConsumerSecret)
		  .setOAuthAccessToken(OAuthAccessToken)
		  .setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
	}
	
	
	// temp testing constructor
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
	
	
	//handles actual uploading to twitter
	public void uploadPicTwitter(File file, String message,Twitter twitter) throws Exception  {
		twitter = bird;
	    try{
	        StatusUpdate status = new StatusUpdate(message);
	        status.setMedia(file);
	        twitter.updateStatus(status);}
	    catch(TwitterException e){
	        System.out.println("Pic Upload error" + e.getErrorMessage());
	        throw e;
	    }
	}
	
	
	//handles downloading image, updating db, and deleting image after upload
	public void uploadPic(){
		ImageManipulator imgman = new ImageManipulator();
		Twitter blah = null;
		File loe = null;
		try {
			AssContent assContent = DataBaseHandler.getRandomishAssImage(GlobalStuff.DATABASE_NAME, GlobalStuff.COLLECTION_NAME);

			//creates temp image and puts file location in loe
			loe = new File(imgman.getImageFile(assContent.getLink()));
			TwitterRunnable lol = new TwitterRunnable();
			lol.uploadPicTwitter(loe,assContent.getCaption(),blah);
			loe.delete();

			//update db
			assContent.setLastAccessDate(new Date());
			assContent.setTimesAccessed(assContent.getTimesAccessed()+1);
		}
		catch (Exception e) {
			System.out.println("Temp download of pic failed "+loe);
			e.printStackTrace();
		}
	}
	
	
		
	public void prettyRateLimit(){
		try {
			for(Map.Entry<String, RateLimitStatus> element : bird.getRateLimitStatus().entrySet()){
				System.out.println(element+"\n");
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
	
	
	@Override
	public void run(){
		//Only run when not 3AM to allow database maintenance
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		
		if(hours != 3){
		try {
			uploadPic();	
		} catch (Exception e) {
			System.out.println("TwitterRunnable b trippin");
			e.printStackTrace();
		}
		}
		
	}
	
	
	public static void main(String[]args){
		new TwitterRunnable().prettyRateLimit();
	}
}