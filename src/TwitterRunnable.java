import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterRunnable implements Runnable {
	private Twitter bird = null;
	private boolean isIncubated;
	
	private final String DATABASE_NAME = "";
	private final String COLLECTION_NAME = "";

	public TwitterRunnable (String OAuthConsumerKey, String OAuthConsumerSecret, String OAuthAccessToken, String OAuthAccessTokenSecret, boolean Incubated){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(OAuthConsumerKey)
		.setOAuthConsumerSecret(OAuthConsumerSecret)
		.setOAuthAccessToken(OAuthAccessToken)
		.setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		isIncubated = Incubated;
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
	public void uploadPic() throws Exception{
		ImageManipulator imgman = new ImageManipulator();
		Twitter blah = null;
		AssImage assContent = DataBaseHandler.getRandomishAssImage(DATABASE_NAME, COLLECTION_NAME);
		
		//creates temp image and puts file location in loe
		File loe = new File(imgman.getImageFile(assContent.getLink()));
		TwitterRunnable lol = new TwitterRunnable();
		lol.uploadPicTwitter(loe,assContent.getCaption(),blah);
		loe.delete();
		
		//update db
		assContent.setLastAccessDate(new Date());
		assContent.setTimesAccessed(assContent.getTimesAccessed()+1);
	}
	
	
	public void updateFollowers(int index){
		
	}
	
	public Map<String, RateLimitStatus> followAndFavorite() throws TwitterException{
		bird.showUser(548850667).getStatus();
		return bird.getRateLimitStatus();
	}
	
	
	
	public void run(){
		try {
			//uploadPic();
			for(int i =0; i<(followAndFavorite().size(); i++){
			System.out.println(Arrays.toString((followAndFavorite()).entrySet().toArray()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[]args){
		

		
		new Thread(new TwitterRunnable()).start();
	}
}