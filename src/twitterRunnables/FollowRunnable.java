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
		Maintenance.writeLog("New FollowRunnable created", index);
		this.index = index;
		bird = twitter;
		Maintenance.runStatus.put(index+"follow", true);	
	}
	
	/**
	 * @throws TwitterException
	 * @throws UnknownHostException 
	 */
	public void followAndFavoriteUsers() throws  UnknownHostException, TwitterException{
		if(DataBaseHandler.getToFollowSize(index)!=0){
			long id = DataBaseHandler.getOneToFollow(index);
			ResponseList<Status> tweets = null;
			//Favorites a tweet which is unique and not a response to another tweet, if available.
			Paging paging = new Paging();
			paging.setCount(50);
			
			ArrayList<ResponseList<Status>> ListTweets =
					TwitterHandler.getUserTimeline(bird, id, paging, index);
			if(ListTweets.isEmpty()){
				Maintenance.writeLog("Could not run getUserTimeline in FollowRunnable for account " + index,
						index, 1);
				return;
			}
			else{
				tweets = ListTweets.get(0);
			}
			for(Status tweet: tweets){
				if(!tweet.isRetweet() && tweet.getInReplyToScreenName() == null){
					TwitterHandler.favorite(bird,tweet.getId(), index);
					break;
				}
			}
			//Follows the person and adds it to the database.
			//only adds to database if twitter allows it, so
			//the logic is in TwitterHandler
			TwitterHandler.follow(bird,id, index);
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
		ArrayList<Long> unfollowArr = DataBaseHandler.popMultipleFollowing(index,
				GlobalStuff.getNumToUnfollow(sizeFollowers, sizeFollowing));
		for(Long id : unfollowArr){
			TwitterHandler.unfollow(bird,id, index);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Maintenance.writeLog("FollowRunnable run for account " + index, index);

		try {
			followAndFavoriteUsers();
			unfollowUsers();
		} catch (Exception e) {
			Maintenance.writeLog("FollowRunnable fucked up somewhere for account: " + index + "\n" +
		Maintenance.getStackTrace(e), index, -1);
		}
		Maintenance.runStatus.put(index+"follow", false);
	}

}

