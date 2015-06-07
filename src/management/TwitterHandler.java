package management;


import java.net.UnknownHostException;
import java.util.HashSet;
import twitter4j.IDs;
import java.util.Map;
import org.bson.Document;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterHandler {

	/**
	 * @param info the authorization info from the account in the schwergsyAccounts collection
	 * @return
	 */
	public static Twitter getTwitter(Document info) {	
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(info.getString("customerKey"))
		.setOAuthConsumerSecret(info.getString("customerSecret"))
		.setOAuthAccessToken(info.getString("authorizationKey"))
		.setOAuthAccessTokenSecret(info.getString("authorizationSecret"));
		TwitterFactory tf = new TwitterFactory(cb.build());
		return tf.getInstance();
	}

	/**
	 * @param init
	 * @throws TwitterException
	 * @throws UnknownHostException 
	 */
	public static HashSet<Long> getFollowers(Twitter bird, int index) throws UnknownHostException{
		if(!DataBaseHandler.isSuspended(index)){
			int ratecount = 0;
			IDs blah;
			try {
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
			} catch (TwitterException e) {
				errorHandling(e,index);
				return null;
			}
		}
		else{
			return null;
		}
	}

	public static void updateStatus(Twitter twitter, StatusUpdate status, int index){
		if(!DataBaseHandler.isSuspended(index)){
			try {
				twitter.updateStatus(status);
			} catch (TwitterException e) {
				errorHandling(e,index);
			}
		}
	}

	public static void follow(Twitter twitter, long id, int index){
		if(!DataBaseHandler.isSuspended(index)){
			try {
				twitter.createFriendship(id);
			} catch (TwitterException e) {
				errorHandling(e,index);
			}
		}
	}

	public static void unfollow(Twitter twitter, long id, int index){
		if(!DataBaseHandler.isSuspended(index)){
			try {
				twitter.destroyFriendship(id);
			} catch (TwitterException e) {
				errorHandling(e,index);
			}
		}
	}

	public static ResponseList<Status> getUserTimeline(Twitter twitter, long id, int index){
		if(!DataBaseHandler.isSuspended(index)){
			try {
				return twitter.getUserTimeline(id);
			} catch (TwitterException e) {
				errorHandling(e,index);
				return null;
			}
		}
		else{
			return null;
		}

	}

	public static ResponseList<Status> getUserTimeline(Twitter twitter, long id, Paging query, int index){
		if(!DataBaseHandler.isSuspended(index)){
			try {
				return twitter.getUserTimeline(id, query);
			} catch (TwitterException e) {
				errorHandling(e,index);
				return null;
			}
		}
		else{
			return null;
		}

	}

	public static long[] getRetweeterIds(Twitter twitter, long id, int number, long sinceStatus, int index){
		if(!DataBaseHandler.isSuspended(index)){
			try {
				return twitter.getRetweeterIds(id, number, sinceStatus).getIDs();
			} catch (TwitterException e) {
				errorHandling(e,index);
				return null;
			}
		}
		else{
			return null;
		}
	}

	public static void favorite(Twitter twitter, long id, int index){
		if(!DataBaseHandler.isSuspended(index)){
			try{
				twitter.createFavorite(id);
			} catch (TwitterException e) {
				errorHandling(e,index);
			}
		}
	}

	public static boolean isAtRateLimit(Twitter twitter, String endpoint, int index){
		if(!DataBaseHandler.isSuspended(index)){
			Map<String, RateLimitStatus> rateLimitStatus;
			try {
				rateLimitStatus = twitter.getRateLimitStatus();
				RateLimitStatus status = rateLimitStatus.get(endpoint);
				if (status.getRemaining() == 0){
					return true;
				}
				else {
					return false;
				}
			} catch (TwitterException e) {
				errorHandling(e,index);
				return true;
			}
		}
		else{
			return true;
		}
	}

	private static void errorHandling(TwitterException e, int index){
		if(e.getErrorCode()==64){    
			DataBaseHandler.suspendSchwergsyAccount(index);
		}
	}
}
