package twitterRunnables;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;

import management.DataBaseHandler;
import management.FuckinUpKPException;
import management.GlobalStuff;
import management.Maintenance;
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
	//TODO when getting new bigAccs, check that it doesn't exist already in bigAccs
	private Twitter bird;
	private int index;
 
	/**
	 * @param OAuthConsumerKey
	 * @param OAuthConsumerSecret
	 * @param OAuthAccessToken
	 * @param OAuthAccessTokenSecret
	 */
	public bigAccRunnable(Twitter twitter, int index){
		this.index = index;
		bird = twitter;
	}

	public bigAccRunnable(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("uHQV3x8pHZD7jzteRwUIw")
		.setOAuthConsumerSecret("OxfLKbnhfvPB8cpe5Rthex1yDR5l0I7ztHLaZXnXhmg")
		.setOAuthAccessToken("2175141374-5Gg6WRBpW1NxRMNt5UsEUA95sPVaW3a566naNVI")
		.setOAuthAccessTokenSecret("Jz2nLsKm59bbGwCxtg7sXDyfqIo7AqO6JsvWpGoEEux8t");
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
		this.index = 0;
	}


	public synchronized void findBigAccounts() throws TwitterException, InterruptedException, UnknownHostException, FuckinUpKPException{
		//TODO add latestTweet capability to bigAccount in DBH
		HashSet<Long> AllCandidates = new HashSet<Long>(); 
		Long[] AllCandidatesArr;
		long latestTweet = 0;

		//if the schwergsaccount has no bigaccounts and doesn't have enough followers to find more bigaccounts
		if(DataBaseHandler.getBigAccountsSize(index)!=0 && DataBaseHandler.getFollowersSize(index) > 100){
			ArrayList<Long> AllRTerIDs = new ArrayList<Long>();
			ResponseList<Status> OwnTweets = bird.getUserTimeline(bird.getId());

			if(OwnTweets.size()>15){
				//sorts by most retweets and cuts out tweets with little retweets
				Collections.sort(OwnTweets, new Comparator<Status>() {
					@Override
					public int compare(Status t1, Status t2) {
						int rts1 = t1.getRetweetCount();
						int rts2 = t2.getRetweetCount();
						if (rts1 == rts2)
							return 0;
						else if (rts1 > rts2)
							return -1;
						else
							return 1;
					}
				});
				while(OwnTweets.size()>1){
					OwnTweets.remove(1);
				}
			}

			for(Status tweet : OwnTweets){
				//gathers all retweeters' ids from tweets
				if(tweet.getRetweetCount()!=0){
					long[] RTerIDs = bird.getRetweeterIds(tweet.getId(), 100, -1).getIDs();
					for(long id : RTerIDs){

						AllRTerIDs.add(id);
					}
				}
			}

			while(AllRTerIDs.size()>50){
				//limits to only 50 retweeters
				AllRTerIDs.remove(50);
			}

			for(long id : AllRTerIDs){
				//gets 50 tweets from each retweeter
				Paging querySettings = new Paging();
				querySettings.setCount(50);
				ResponseList<Status> potentialBigAccs = bird.getUserTimeline(id, querySettings);
				for(Status tweet: potentialBigAccs){
					if(tweet.isRetweet() && tweet.getRetweetedStatus().getUser().getFollowersCount()>5000
							&& tweet.getRetweetedStatus().getUser().getId() != bird.getId()){
						//if the tweet is a retweet, is not from our own account, and the original tweeter has over
						//5000 followers, add that account as a candidate for a bigAccount
						AllCandidates.add(tweet.getRetweetedStatus().getUser().getId());
					}
				}
			}
		}
		
		//TODO make this part better
		else{
			ResponseList<User> suggestedUsers = bird.getUserSuggestions("funny");
			int limit = 1;
			for(User user : suggestedUsers){
				if(limit != 0){
					limit--;
					AllCandidates.add(user.getId());
				}
			}
		}

		AllCandidatesArr = Arrays.copyOf(AllCandidates.toArray(), AllCandidates.toArray().length, Long[].class);

		int maxCandidates = 100;
		if(AllCandidatesArr.length<maxCandidates){
			maxCandidates = AllCandidatesArr.length;
		}

		for(int i =0; i<maxCandidates; i++){
			System.out.println("considering candidate...");
			Long id = AllCandidatesArr[i];
			if(DataBaseHandler.isBigAccWhiteListed(index, id)){
				break;
			}
			
			Paging query = new Paging();
			query.setCount(200);
			ResponseList<Status> timeline = bird.getUserTimeline(id, query);
			ArrayList<Status> noRTTimeline = new ArrayList<Status>();
			int count = 0;
			int totalRTs = 0;
			long firstTime = 0;
			long lastTime = 0;
			
			//Gets only original tweets
			for(Status tweet: timeline){
				if(!tweet.isRetweet()){
					noRTTimeline.add(tweet);
				}
			}
			
			//Gets the total amount of retweets
			for(Status tweet: noRTTimeline){
				count++;
				totalRTs+= tweet.getRetweetCount();
				if(count == 1){
					firstTime += tweet.getCreatedAt().getTime();
					latestTweet = tweet.getId();
				}
				if(count == noRTTimeline.size()){
					lastTime += tweet.getCreatedAt().getTime();
					
				}
			}

			//adds a bigaccount if it averages 30 retweets per tweet and posts daily on average.
			if(count>0) {
				long avgTime = (lastTime-firstTime)/count;
				int avgRTs = totalRTs/count;

				if(avgRTs >= 30 && avgTime <= GlobalStuff.DAY_IN_MILLISECONDS){
					DataBaseHandler.addBigAccount(index, id, -1);
					DataBaseHandler.addBigAccWhiteList(index,id);
				}
			}
		}
	}

	public void harvestBigAccounts() throws UnknownHostException, TwitterException, InterruptedException, FuckinUpKPException{
		HashSet<Long> toFollowSet = new HashSet<Long>();
		Long[] toFollowSetArray;
		Long lastTweet = DataBaseHandler.getBigAccountLatestTweet(index,0);
		
		//Only gets the 5 latest tweets of the bigAccount candidate. If the bigaccount was harvested 
		//before, it only takes the tweetsafter the latest tweet used.
		Paging querySettings = new Paging();
		querySettings.setCount(5);
		if(lastTweet != -1){
			querySettings.setSinceId(lastTweet);
		}
		
		ResponseList<Status> tweets = bird.getUserTimeline(DataBaseHandler.getBigAccount(index, 0), querySettings);
		ArrayList<Status> NoRTTweets = new ArrayList<Status>();
		
		if(DataBaseHandler.getToFollowSize(index)<11900){
			
			//Makes sure the tweet is original to the bigAccount candidate
			for(Status tweet: tweets){
				if(!tweet.isRetweet()){
					NoRTTweets.add(tweet);
				}
			}
			
			//Gets ids of retweeters and puts it into toFollowSet and updates latestTweet for bigAccount
			//By using a HashSet, you get only unique retweeter ids.
			for(Status tweet :NoRTTweets){
				//Makes sure it won't pass the ratelimit
				if(isAtRateLimit("/statuses/retweets/:id")){
					break;
				}
				long[] toFollows = bird.getRetweeterIds(tweet.getId(), 100, -1).getIDs();
				for(long id : toFollows){
					toFollowSet.add(id);
				}
				DataBaseHandler.editBigAccountLatestTweet(index, 0, tweet.getId());
			}
			
			//If the retweeter is already in the whitelist, then remove that bitch
			toFollowSetArray = Arrays.copyOf(toFollowSet.toArray(), toFollowSet.toArray().length, Long[].class);
			for(Long user_id: toFollowSetArray){
				if(DataBaseHandler.isWhiteListed(index, user_id)){
					toFollowSet.remove(user_id);
				}
			}
			
			toFollowSetArray = Arrays.copyOf(toFollowSet.toArray(), toFollowSet.toArray().length, Long[].class);
			if(toFollowSetArray.length==0){
				if(DataBaseHandler.getBigAccountStrikes(index, 0)==2){
					if(DataBaseHandler.getBigAccountOuts(index, 0)==2){
						//if it gets 3 outs, it's removed from bigAccounts
						DataBaseHandler.deleteBigAccount(index, 0);
					}
					else{
						//if it gets 3 strikes, move it to the end of bigAccounts and reset strikes
						//and adds an out
						DataBaseHandler.editBigAccountStrikes(index, 0, 0);
						DataBaseHandler.editBigAccountOuts(index, 0, DataBaseHandler.getBigAccountOuts(index, 0));
						DataBaseHandler.moveBigAccountToEnd(index, 0);
					}
				}
				else{
					//if it gets a strike, add it to what it has now.
					DataBaseHandler.editBigAccountStrikes(index, 0, 
							DataBaseHandler.getBigAccountStrikes(index, 0) + 1);
				}
			}
			else{
				DataBaseHandler.addToFollow(index, toFollowSetArray);
				DataBaseHandler.addWhitelist(index, toFollowSetArray);
			}
		}
		System.out.println("done harvesting");
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
		Maintenance.runStatus.put(index+"bigAcc", true);
		try{
			//TODO stuff in here.
		}
		finally{
			Maintenance.runStatus.put(index+"follow", false);
		}

	}

	public static void main(String[] args){
		new Thread(new bigAccRunnable()).start();
	}

}
