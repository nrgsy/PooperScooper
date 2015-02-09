import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;


public class FollowRunnable implements Runnable{
	private Twitter bird;
	
	public FollowRunnable(String OAuthConsumerKey, String OAuthConsumerSecret, String OAuthAccessToken, String OAuthAccessTokenSecret){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(OAuthConsumerKey)
		  .setOAuthConsumerSecret(OAuthConsumerSecret)
		  .setOAuthAccessToken(OAuthAccessToken)
		  .setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
	}
	
	public FollowRunnable(int lol){
		if(lol ==1){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("uHQV3x8pHZD7jzteRwUIw")
		  .setOAuthConsumerSecret("OxfLKbnhfvPB8cpe5Rthex1yDR5l0I7ztHLaZXnXhmg")
		  .setOAuthAccessToken("2175141374-5Gg6WRBpW1NxRMNt5UsEUA95sPVaW3a566naNVI")
		  .setOAuthAccessTokenSecret("Jz2nLsKm59bbGwCxtg7sXDyfqIo7AqO6JsvWpGoEEux8t");
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
		}
		else{
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey("42sz3hIV8JRBSLFPfF1VTQ")
			  .setOAuthConsumerSecret("sXcWyF4BoJMSxbEZu4lAgGBabBgPQndiRhB35zQWk")
			  .setOAuthAccessToken("2227975866-3TyxFxzLhQOqFpmlHZdZrvnp9ygl10Un41Tq1Dk")
			  .setOAuthAccessTokenSecret("e9cmTKAMWiLzkfdf4RwzhcmaE1I1gccKEcUxbpVUZugY4");
			TwitterFactory tf = new TwitterFactory(cb.build());
			bird = tf.getInstance();
		}
	}
	
	
	public void followAndFavoriteUsers() throws TwitterException{
		if(DataBaseHandler.getCollectionSize(GlobalStuff.DATABASE_NAME, "toFollow")!=0){
			long to_follow = Long.parseLong(/*DAL pop_toFollow*/, 10);
			bird.createFavorite(bird.createFriendship(to_follow).getStatus().getId());
			/*DAL addWhitelist(String.valueOf(to_follow))*/
			/*DAL addFollowing(String.valueOf(to_follow))*/
		}
	}
	
	//done in bulk, number unfollowed is respective to follower:following
	public void unfollowUsers(){
		int sizeFollowers = /*DAL.sizeFollowers*/;
		int sizeFollowing = /*DAL.sizeFollowing*/;
		while(3>sizeFollowers/sizeFollowing){
			bird.destroyFriendship(/*DAL.popFollowing*/);
			sizeFollowing--;
		}
	}
	
	//Gets user timeline of a big account, gets retweeters, appends to to_follow in db
	public void update_toFollow(){
		List<Status> statuses = null;
		String longToString = "";
		long[] rters_ids;
		int statuses_size = 15;
		try {
			statuses=bird.getUserTimeline(/*DAL get BigAccount*/);
			if(statuses.size()<=statuses_size){
				statuses_size = statuses.size();
			}
			//If more than 15 tweets are returned, sort by tweets with most retweets first
			else{
				Collections.sort(statuses, new Comparator<Status>() {
					@Override
					public int compare(Status t1, Status t2) {
						int rts1 = t1.getRetweetCount();
						int rts2 = t2.getRetweetCount();

						if (rts1 == rts2)
							return 0;
						else if (rts1 > rts2)
							return 1;
						else
							return -1;
					}
				});
			}

			for(int i = 0; i<statuses_size; i++){
				rters_ids = bird.getRetweeterIds(Long.valueOf(statuses.get(i).getId()),100).getIDs();
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

	}
	
	public void updateFollowers(boolean init) throws TwitterException{
		int count = 0;
		IDs blah;
		blah = bird.getFollowersIDs(-1);
		System.out.println(blah.getIDs().length);
		while(blah.getNextCursor()!=0 && count<14){
			blah = (bird.getFollowersIDs(blah.getNextCursor()));
			System.out.println(blah.getIDs().length);
			count++;
		}
	}

	public void initUpdateFollowing() throws TwitterException{
		int count = 0;
		IDs blah;
		blah = bird.getFriendsIDs(-1);
		System.out.println(blah.getIDs().length);
		while(blah.getNextCursor()!=0 && count<14){
			blah = (bird.getFriendsIDs(blah.getNextCursor()));
			System.out.println(blah.getIDs().length);
			count++;
		}
	}

	@Override
	public void run() {
			try {
				updateFollowers(true);
				initUpdateFollowing();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//		try {
//			followAndFavoriteUsers();
//			unfollowUsers();
//		} catch (TwitterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	public static void main(String[]args){
		new Thread(new FollowRunnable(1)).start();
	}


}
