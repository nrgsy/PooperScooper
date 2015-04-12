import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;

import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;


public class bigAccRunnable implements Runnable {

	private Twitter bird;
	private int index;

	/**
	 * @param OAuthConsumerKey
	 * @param OAuthConsumerSecret
	 * @param OAuthAccessToken
	 * @param OAuthAccessTokenSecret
	 */
	public bigAccRunnable(String OAuthConsumerKey, String OAuthConsumerSecret, String OAuthAccessToken, String OAuthAccessTokenSecret, int index){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(OAuthConsumerKey)
		.setOAuthConsumerSecret(OAuthConsumerSecret)
		.setOAuthAccessToken(OAuthAccessToken)
		.setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
		this.index = index;
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
	}

	public void findBigAccounts() throws TwitterException, InterruptedException{
		ArrayList<Long> AllCandidates = null; 
		ArrayList<Long> AllRTerIDs = null;
		ResponseList<Status> OwnTweets = bird.getHomeTimeline();
		long latestTweet;

		if(OwnTweets.size()>15){

			//sorts by most retweets near 0 index and cuts out tweets with little retweets
			Collections.sort(OwnTweets, new Comparator<Status>() {
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
			while(OwnTweets.size()>15){
				OwnTweets.remove(15);
			}
		}

		for(Status tweet : OwnTweets){
			if(tweet.getRetweetCount()!=0){
				long[] RTerIDs = bird.getRetweeterIds(tweet.getId(), 100).getIDs();
				for(long id : RTerIDs){
					AllRTerIDs.add(id);
				}
			}
		}

		while(AllRTerIDs.size()>50){
			AllRTerIDs.remove(50);
		}

		for(long id : AllRTerIDs){
			Paging querySettings = new Paging();
			querySettings.setCount(50);
			ResponseList<Status> potentialBigAccs = bird.getUserTimeline(id, querySettings);
			for(Status tweet: potentialBigAccs){
				if(tweet.isRetweeted() && tweet.getRetweetedStatus().getUser().getFollowersCount()>5000){
					AllCandidates.add(tweet.getRetweetedStatus().getUser().getId());
				}
			}
		}

		while(AllCandidates.size()>100){
			AllCandidates.remove(100);
		}

		for(long id : AllCandidates){
			ResponseList<Status> timeline = bird.getUserTimeline(id, new Paging(200));
			int count = 0;
			int totalRTs = 0;
			long firstTime = 0;
			long lastTime = 0;
			for(Status tweet: timeline){
				if(tweet.isRetweet()){
					timeline.remove(tweet);
				}
			}
			for(Status tweet: timeline){
				count++;
				totalRTs+= tweet.getRetweetCount();
				if(count == 1){
					firstTime += tweet.getCreatedAt().getTime();
				}
				if(count == timeline.size()){
					lastTime += tweet.getCreatedAt().getTime();
					latestTweet = tweet.getId();
				}
			}

			long avgTime = (lastTime-firstTime)/count;
			int avgRTs = totalRTs/count;

			if(avgRTs>=15 &&avgTime<=172800000){
				//TODO add "id" and "latestTweet" into bigAccount list
			}
		}
		Thread.sleep(900000);
	}

	public void harvestBigAccounts() throws UnknownHostException, TwitterException, InterruptedException{
		int bigAccIndex = 0;
		HashSet<Long> toFollowSet = new HashSet<Long>();
		while(DataBaseHandler.getToFollowSize(index)<11900 && bigAccIndex!= DataBaseHandler.getBigAccountsSize(index)){
			Paging querySettings = new Paging();
			querySettings.setCount(200);
			querySettings.setSinceId(DataBaseHandler.getBigAccountLatestTweet(index,bigAccIndex));
			ResponseList<Status> tweets = bird.getUserTimeline(DataBaseHandler.getBigAccount(index, bigAccIndex), querySettings);
			for(Status tweet: tweets){
				if(tweet.isRetweet()){
					tweets.remove(tweet);
				}
			}
			for(Status tweet :tweets){
				if(isAtRateLimit("/statuses/retweets/:id")){
					Thread.sleep(900000);
				}
				long[] toFollows = bird.getRetweeterIds(tweet.getId(), 100, -1).getIDs();
				for(long id : toFollows){
					toFollowSet.add(id);
				}
			}
			DataBaseHandler.addToFollow(index, (Long[])toFollowSet.toArray());
		}
	}

	public boolean isAtRateLimit(String endpoint) throws TwitterException{
		Map<String ,RateLimitStatus> rateLimitStatus = bird.getRateLimitStatus();
		RateLimitStatus status = rateLimitStatus.get(endpoint);
		if (status.getRemaining() == 0){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
