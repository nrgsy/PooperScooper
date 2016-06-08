package management;


import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import twitter4j.IDs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;

import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterHandler {

	//This maps the index of each Schwergsy Account with it's rate limit map.
	//each account's rate limit map maps each endpoint with the number of remaining api calls
	//for that endpoint
	public static ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>> remainingCallsMapMap;

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
	 * 
	 * NOTICE can return null if something goes wrong
	 */
	public static HashSet<Long> getFollowers(Twitter bird, int index)throws TwitterException{
		
		String endPoint = "/followers/ids";	
		if(!isAtRateLimit(bird, endPoint, index)){
			
			int ratecount = 0;
			IDs blah;
			try {
				blah = bird.getFollowersIDs(-1);

				HashSet<Long> followers = new HashSet<Long>();
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
				//don't return if we have an error
				throw e;
			}
			finally {
				decrementRemainingCalls(index, endPoint, ratecount);		
			}
		}
		else {
			return null;
		}
	}

	public static HashSet<Long> initUpdateFollowing(Twitter twitter, int index) throws TwitterException{

		String endPoint = "/friends/ids";
		if(!isAtRateLimit(twitter, endPoint, index)){
			int ratecount = 0;
			try {
				IDs IDCollection;
				IDCollection = twitter.getFriendsIDs(-1);
				HashSet<Long> following = new HashSet<Long>();

				for(long id : IDCollection.getIDs()){
					following.add(id);
				}
				ratecount++;
				while(IDCollection.getNextCursor()!=0 && ratecount<14){
					IDCollection = (twitter.getFriendsIDs(IDCollection.getNextCursor()));
					for(long id : IDCollection.getIDs()){
						following.add(id);
					}
					ratecount++;
				}
				return following;
			} catch (TwitterException e) {
				errorHandling(e,index);
				//throw the error because we don't want to return if there was an exception
				throw e;
			}
			finally {
				decrementRemainingCalls(index, endPoint, ratecount);		
			}
		}
		else{
			return new HashSet<Long>();
		}
	}

	public static void updateStatus(Twitter twitter, StatusUpdate status, int index)
			throws TwitterException{

		try {
			twitter.updateStatus(status);
		} catch (TwitterException e) {
			errorHandling(e,index);
		}

	}

	public static void follow(Twitter twitter, long id, int index) throws TwitterException{

		try {
			twitter.createFriendship(id);
			ArrayList<Long> oneFollow = new ArrayList<Long>();
			oneFollow.add(id);
			DataBaseHandler.addFollowing(index, oneFollow);
		} catch (TwitterException e) {
			errorHandling(e,index);
		}

	}

	public static void unfollow(Twitter twitter, long id, int index) throws TwitterException{

		try {
			twitter.destroyFriendship(id);
		} catch (TwitterException e) {
			errorHandling(e,index);
		}

	}

	public static ArrayList<ResponseList<Status>> getUserTimeline(Twitter twitter, long id, int index)
			throws TwitterException{
		ArrayList<ResponseList<Status>> ListWrapper = new ArrayList<ResponseList<Status>>();
		String endPoint = "/statuses/user_timeline";
		if(!isAtRateLimit(twitter, endPoint, index)){
			try {
				ListWrapper.add(twitter.getUserTimeline(id));
				return ListWrapper;
			} catch (TwitterException e) {
				errorHandling(e,index);
				return ListWrapper;
			}
			finally {
				decrementRemainingCalls(index, endPoint, 1);		
			}
		}
		else{
			return ListWrapper;
		}

	}

	public static ArrayList<ResponseList<Status>> getUserTimeline(Twitter twitter, long id,
			Paging query, int index) throws TwitterException{
		ArrayList<ResponseList<Status>> ListWrapper = new ArrayList<ResponseList<Status>>();
		String endPoint = "/statuses/user_timeline";
		if(!isAtRateLimit(twitter, endPoint, index)){
			
			
			
			
			
			
			
//System.out.println("Recorded rate limit is: " + remainingCallsMapMap.get(index).get(endPoint) + "********************************");
//Map<String, RateLimitStatus> twitterMap = twitter.getRateLimitStatus();
//System.out.println("TwitterMap rate limit is: " + twitterMap.get(endPoint).getRemaining()+ "********************************");
//System.out.println("custom map rate limit is: " + TwitterHandler.getRemainingCallsMap(twitterMap).get(endPoint)
//		+ "********************************");
			
			



			
			try {
				ListWrapper.add(twitter.getUserTimeline(id, query));
				return ListWrapper;
			} catch (TwitterException e) {
				errorHandling(e,index);
				return ListWrapper;
			}
			finally {
				decrementRemainingCalls(index, endPoint, 1);		
			}
		}
		else{
			return ListWrapper;
		}
	}

	public static ArrayList<Long> getRetweeterIds(Twitter twitter, long id, int number,
			long sinceStatus, int index) throws TwitterException{
		String endPoint = "/statuses/retweeters/ids";
		if(!TwitterHandler.isAtRateLimit(twitter, endPoint, index)){
			try {
				long[] rtids = twitter.getRetweeterIds(id, number, sinceStatus).getIDs();
				ArrayList<Long> returnValue = new ArrayList<Long>();
				for(long ids : rtids){
					returnValue.add(ids);
				}
				return returnValue;
			} catch (TwitterException e) {
				errorHandling(e,index);
				return new ArrayList<Long>();
			}
			finally {
				decrementRemainingCalls(index, endPoint, 1);		
			}
		}
		else{
			return new ArrayList<Long>();
		}
	}

	public static ArrayList<ResponseList<User>> getUserSuggestions(Twitter twitter, int index)
			throws TwitterException{
		ArrayList<ResponseList<User>> returnval = new ArrayList<ResponseList<User>>();

		try{
			returnval.add(twitter.getUserSuggestions("entertainment"));
			return returnval;
		} catch (TwitterException e) {
			errorHandling(e,index);
			return returnval;
		}

	}

	public static void favorite(Twitter twitter, long id, int index) throws TwitterException{

		try{
			twitter.createFavorite(id);
		} catch (TwitterException e) {
			errorHandling(e,index);
		}

	}

	public static boolean isAtRateLimit(Twitter twitter, String endpoint, int index) 
			throws TwitterException{

		ConcurrentHashMap<String, Integer> remainingCallsMap = remainingCallsMapMap.get(index);

		try {
			//init if not initialized
			if (remainingCallsMap == null) {
				remainingCallsMap = TwitterHandler.getRemainingCallsMap(twitter.getRateLimitStatus());			
				remainingCallsMapMap.put(index, remainingCallsMap);
			}
		} catch (TwitterException e) {
			errorHandling(e,index);
			//we want the error to be handled higher up so throw here
			throw e;
		}

		Integer remainingCalls = remainingCallsMap.get(endpoint);
		if (remainingCalls <= 0){
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 
	 * Get a map of endpoints mapped to the number of remaining calls for the endpoints
	 * 
	 * @param twitterRateLimitStatusMap
	 * @return
	 */
	public static ConcurrentHashMap<String, Integer> getRemainingCallsMap(
			Map<String, RateLimitStatus> twitterRateLimitStatusMap) {

		ConcurrentHashMap<String, Integer> rateLimitStatusMap = new ConcurrentHashMap<>();
		
		Set<Entry<String, RateLimitStatus>> entrySet = twitterRateLimitStatusMap.entrySet();	
		for (Entry<String, RateLimitStatus> entry : entrySet) {
			rateLimitStatusMap.put(entry.getKey(), entry.getValue().getRemaining());
		}	
		return rateLimitStatusMap;
	}
	
	/**
	 * 
	 * @param index
	 * @param endPoint
	 * @param numCalls Number of calls to decrement by
	 */
	private static void decrementRemainingCalls(final int index, final String endPoint,
			final int numCalls) {
		
		int decrementedNumOfRemainingCalls = remainingCallsMapMap.get(index).get(endPoint) - numCalls;
		
		if (decrementedNumOfRemainingCalls < 0) {
			Maintenance.writeLog("Number of calls used exceeded the recorded rate limit!",
					"maintenance", -1);
			return;
		}
		
		remainingCallsMapMap.get(index).put(endPoint, decrementedNumOfRemainingCalls);
		//The rate limit refresh timer will reincrement the rate limit for us every 15 minutes
	}

	private static void errorHandling(TwitterException e, int index) throws TwitterException{

		switch(e.getErrorCode()) {		
		case 64:
			Maintenance.writeLog("Shit, this account has been suspended", index, -1);
			DataBaseHandler.suspendSchwergsyAccount(index);
			break;
		case 88:
			Maintenance.writeLog("Rate limit has been exceeded" + Maintenance.getStackTrace(e), index, -1);
			break;
		case 130:
			Maintenance.writeLog("Twitter is over capacity to fulfill this request", index, 1);
			break;
		case 131:
			Maintenance.writeLog("Twitter internal error", index, 1);
			break;
		case 161:
			Maintenance.writeLog("Unable to follow more people at this time", index, 1);
			break;
		case 226:
			Maintenance.writeLog("Twitter thinks this request was automated", index, 1);
			break;
		case -1:
			Maintenance.writeLog("The interwebs are probably fuckin up, "
					+ "TwitterHandler threw this error:\n" + e.toString(), index, 1);
			break;
		case 89:
			Maintenance.writeLog("Failed to validate some authorization info, "
					+ "TwitterHandler threw this error:\n" + e.toString(), index, 1);
			throw e;
		case 32:
			Maintenance.writeLog("Failed to validate some authorization info, "
					+ "TwitterHandler threw this error:\n" + e.toString(), index, 1);
			throw e;
		case 186:
			Maintenance.writeLog("Status too long, exceeded 140 char maximum, "
					+ "TwitterHandler threw this error:\n" + e.toString(), index, 1);
			throw e;
		case 34:
			Maintenance.writeLog("Some page does not exist, "
					+ "TwitterHandler threw this error:\n" + e.toString(), index, 1);
			throw e;
		default:
			Maintenance.writeLog("Something fucked up in Twitter handling\n" +
					Maintenance.getStackTrace(e), index, -1);
			throw e;
		}
	}
}