import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterRunnable implements Runnable {
	private Twitter bird = null;
	private boolean isIncubated;

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
		AssImage assContent = DataBaseHandler.getRandomishAssImage(GlobalStuff.DATABASE_NAME, GlobalStuff.COLLECTION_NAME);
		
		//creates temp image and puts file location in loe
		File loe = new File(imgman.getImageFile(assContent.getLink()));
		TwitterRunnable lol = new TwitterRunnable();
		lol.uploadPicTwitter(loe,assContent.getCaption(),blah);
		loe.delete();
		
		//update db
		assContent.setLastAccessDate(new Date());
		assContent.setTimesAccessed(assContent.getTimesAccessed()+1);
	}
	
	//Gets user timeline of a big account, gets retweeters, appends to to_follow in db
	public void updateFollowers(int index){
		List<Status> statuses = null;
		String longToString = "";
		long[] rters_ids;
		int statuses_size = 15;
		try {
			statuses=bird.getUserTimeline(/*DAL get BigAccount*/);
			if(statuses.size()<statuses_size){
				statuses_size = statuses.size();
			}
			for(int i = 0; i<statuses_size; i++){
				//get rid of later
				rters_ids = bird.getRetweeterIds(Long.valueOf(statuses.get(i).getId()),100).getIDs();
				
				System.out.println(statuses.get(i).getId());
				for(long user_id : rters_ids){
					longToString = String.valueOf(user_id);
					/*DAL add to to_follow*/
					System.out.println(longToString);
				}
			}

		} catch (TwitterException e) {
			System.out.println("Something in updateFollowers went wrong");
			e.printStackTrace();
		}
		
		//get rid of later
		prettyRateLimit();
		
	}
	
	
	public void followAndFavorite(){
		
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
	
	
	
	public void run(){
		try {
			//uploadPic();
			updateFollowers(2);
			
		} catch (Exception e) {
			System.out.println("boi b trippin");
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[]args){
		new Thread(new TwitterRunnable()).start();
	}
}