package twitterRunnables;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

import management.DataBaseHandler;
import management.FuckinUpKPException;
import management.GlobalStuff;
import management.Maintenance;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
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
	public void followAndFavoriteUsers() throws TwitterException, UnknownHostException{
		if(DataBaseHandler.getToFollowSize(index)!=0){
			long id = DataBaseHandler.getOneToFollow(index);
			
			//Follows the person
			bird.createFriendship(id);
			
			//Favorites a tweet which is unique and not a response to another tweet, if available.
			Paging paging = new Paging();
			paging.setCount(50);
			ResponseList<Status> tweets = bird.getUserTimeline(id, paging);
			for(Status tweet: tweets){
				if(!tweet.isRetweet() && tweet.getInReplyToScreenName().equals(null)){
					bird.createFavorite(tweet.getId());
					break;
				}
			}
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
		//TODO get a ratio
		Long[] unfollowArr = DataBaseHandler.popMultipleFollowing(index, GlobalStuff.GET_NUM_TO_UNFOLLOW(sizeFollowers, sizeFollowing));
		for(int i =0; i<unfollowArr.length; i++){
			bird.destroyFriendship(unfollowArr[i]);
		}
	}
	
	//Need to add functionality to remove bigAccounts which are exhausted
	/**
	 * @throws FuckinUpKPException 
	 * 
	 */
	//TODO update this part to reflect changes made by bigAccRunnable
//	public void getToFollow() throws TwitterException,UnknownHostException, FuckinUpKPException{
//		List<Status> statuses = null;
//		String longToString = "";
//		long[] rters_ids;
//		int statuses_size = 15;
//		try {
//			statuses=bird.getUserTimeline(DataBaseHandler.getBigAccount(index));
//			if(statuses.size()<=statuses_size){
//				statuses_size = statuses.size();
//			}
//			//If more than 15 tweets are returned, sort by tweets with most retweets first
//			else{
//				Collections.sort(statuses, new Comparator<Status>() {
//					@Override
//					public int compare(Status t1, Status t2) {
//						int rts1 = t1.getRetweetCount();
//						int rts2 = t2.getRetweetCount();
//
//						if (rts1 == rts2)
//							return 0;
//						else if (rts1 > rts2)
//							return 1;
//						else
//							return -1;
//					}
//				});
//			}
//			
//			//need to add in check so that exhausted bigAccounts are removed
//			for(int i = 0; i<statuses_size; i++){
//				rters_ids = bird.getRetweeterIds(Long.valueOf(statuses.get(i).getId()),100).getIDs();
//				for(long user_id : rters_ids){
//					if(!DataBaseHandler.isWhiteListed(index, user_id)){
//					DataBaseHandler.addElementToSchwergsArray(index, user_id, "toFollow");
//					}
//					System.out.println(longToString);
//				}
//			}
//		} catch (TwitterException e) {
//			System.out.println("Something in updateFollowers went wrong");
//			e.printStackTrace();
//		}
//	}

	
	/**
	 * @param init
	 * @throws TwitterException
	 * @throws UnknownHostException 
	 */
	public HashSet<Long> getFollowers() throws TwitterException, UnknownHostException{
		int ratecount = 0;
		IDs blah;
		blah = bird.getFollowersIDs(-1);
		HashSet<Long> followers = new HashSet<>();
		for(int i = 0; i < blah.getIDs().length; i++){
		    followers.add(blah.getIDs()[i]);
		}
		ratecount++;
		
		while(blah.getNextCursor()!=0 && ratecount<14){
			blah = (bird.getFollowersIDs(blah.getNextCursor()));
			for(int i = 0; i < blah.getIDs().length; i++){
				followers.add(blah.getIDs()[i]);
			}
			ratecount++;
		}
		
		return followers;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Maintenance.runStatus.put(index+"follow", true);
		try {
			//TODO stuff in here
		}
		finally{
			Maintenance.runStatus.put(index+"follow", false);
		}
	}
	
	public static void main(String[] args){
		(new Thread(new FollowRunnable(0))).start();
	}
	
}

