package twitterRunnables;

import java.net.UnknownHostException;
import java.util.ArrayList;
import management.DataBaseHandler;
import management.GlobalStuff;
import management.Maintenance;
import management.TwitterHandler;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Bojangles and McChrpchrp
 *
 */
public class FollowRunnable implements Runnable{
	private Twitter bird;
	private int index;
	
	/**
	 * @param OAuthConsumerKey
	 * @param OAuthConsumerSecret
	 * @param OAuthAccessToken
	 * @param OAuthAccessTokenSecret
	 */
	public FollowRunnable(Twitter twitter, int index){
		
		this.index = index;
		bird = twitter;
		Maintenance.runStatus.put(index+"follow", true);
		
	}
	
	//this constructor only for testing
	/**
	 * @param lol
	 */
	public FollowRunnable(int lol){
		if(lol == 0){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("uHQV3x8pHZD7jzteRwUIw")
		  .setOAuthConsumerSecret("OxfLKbnhfvPB8cpe5Rthex1yDR5l0I7ztHLaZXnXhmg")
		  .setOAuthAccessToken("2175141374-5Gg6WRBpW1NxRMNt5UsEUA95sPVaW3a566naNVI")
		  .setOAuthAccessTokenSecret("Jz2nLsKm59bbGwCxtg7sXDyfqIo7AqO6JsvWpGoEEux8t");
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
		index = 0;
		Maintenance.runStatus.put(index+"follow", true);
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
	
	/**
	 * @throws TwitterException
	 * @throws UnknownHostException 
	 */
	public void followAndFavoriteUsers() throws  UnknownHostException, TwitterException{
		if(DataBaseHandler.getToFollowSize(index)!=0){
			long id = DataBaseHandler.getOneToFollow(index);
			
			//Favorites a tweet which is unique and not a response to another tweet, if available.
			Paging paging = new Paging();
			paging.setCount(50);
			//if something fucks up here, it's because TwitterHandler returns null and 
			//the ResponseList<Status> is null. not sure if you can iterate over null.
			//probably not.
			ResponseList<Status> tweets = TwitterHandler.getUserTimeline(bird, id, paging, index);
			for(Status tweet: tweets){
				if(!tweet.isRetweet() && tweet.getInReplyToScreenName().equals(null)){
					TwitterHandler.favorite(bird,tweet.getId(), index);
					break;
				}
			}
			//Follows the person
			TwitterHandler.follow(bird,id, index);
			ArrayList<Long> followed = new ArrayList<Long>();
			followed.add(id);
			
			DataBaseHandler.addFollowing(index, followed);
		}
	}
	
	/**
	 * done in bulk, number unfollowed is respective to follower:following
	 * @throws UnknownHostException 
	 * @throws TwitterException 
	 * 
	 */
	public void unfollowUsers() throws UnknownHostException, TwitterException{
		int sizeFollowers = DataBaseHandler.getFollowersSize(index);
		int sizeFollowing = DataBaseHandler.getFollowingSize(index);
		ArrayList<Long> unfollowArr = DataBaseHandler.popMultipleFollowing(index, GlobalStuff.getNumToUnfollow(sizeFollowers, sizeFollowing));
		for(Long id : unfollowArr){
			TwitterHandler.unfollow(bird,id, index);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			unfollowUsers();
			followAndFavoriteUsers();
		} catch (UnknownHostException | TwitterException e) {
			System.out.println(e.getStackTrace());
			Maintenance.writeLog("FollowRunnable fucked up somewhere", index);
		}

	}
	
	
	public static void main(String[] args){
		(new Thread(new FollowRunnable(0))).start();
	}
	
}

