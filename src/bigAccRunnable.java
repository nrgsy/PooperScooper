import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
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
	//TODO when getting new bigAccs, check that it doesn't exist already in bigAccs
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

		HashSet<Long> AllCandidates = new HashSet<Long>(); 
		Long[] AllCandidatesArr;
		long latestTweet = 0;

		if(DataBaseHandler.getBigAccountsSize(index)!=0){
			ArrayList<Long> AllRTerIDs = null;
			ResponseList<Status> OwnTweets = bird.getHomeTimeline();

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
					if(tweet.isRetweeted() && tweet.getRetweetedStatus().getUser().getFollowersCount()>5000
							&& tweet.getRetweetedStatus().getUser().getId() != bird.getId()){
						AllCandidates.add(tweet.getRetweetedStatus().getUser().getId());
					}
				}
			}
		}
		else{
			ResponseList<User> suggestedUsers = bird.getUserSuggestions("funny");
			int limit = 3;
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
			Paging query = new Paging();
			query.setCount(5);
			ResponseList<Status> timeline = bird.getUserTimeline(id, query);
			ArrayList<Status> noRTTimeline = new ArrayList<Status>();
			int count = 0;
			int totalRTs = 0;
			long firstTime = 0;
			long lastTime = 0;
			for(Status tweet: timeline){
				if(!tweet.isRetweet()){
					noRTTimeline.add(tweet);
				}
			}
			for(Status tweet: noRTTimeline){
				count++;
				totalRTs+= tweet.getRetweetCount();
				if(count == 1){
					firstTime += tweet.getCreatedAt().getTime();
				}
				if(count == noRTTimeline.size()){
					lastTime += tweet.getCreatedAt().getTime();
					latestTweet = tweet.getId();
				}
			}

			if(count>0){
				long avgTime = (lastTime-firstTime)/count;
				int avgRTs = totalRTs/count;

				if(avgRTs>=30 && avgTime<=86400000){
					DataBaseHandler.addBigAccount(index, id, latestTweet);
				}
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
			ArrayList<Status> NoRTTweets = new ArrayList<Status>();
			for(Status tweet: tweets){
				if(!tweet.isRetweet()){
					NoRTTweets.add(tweet);
				}
			}
			for(Status tweet :NoRTTweets){
				if(isAtRateLimit("/statuses/retweets/:id")){
					Thread.sleep(900000);
				}
				long[] toFollows = bird.getRetweeterIds(tweet.getId(), 100, -1).getIDs();
				for(long id : toFollows){
					toFollowSet.add(id);
				}
			}
			if(toFollowSet.size()==0){
				if(DataBaseHandler.getBigAccountStrikes(index, bigAccIndex)==2){
					DataBaseHandler.editBigAccountStrikes(index, bigAccIndex, 0);
					DataBaseHandler.moveBigAccountToEnd(index, bigAccIndex);
				}
				else{
					DataBaseHandler.editBigAccountStrikes(index, bigAccIndex, 
							DataBaseHandler.getBigAccountStrikes(index, bigAccIndex) +1);
				}
			}
			else{
				DataBaseHandler.addToFollow(index, (Long[])toFollowSet.toArray());
			}
			bigAccIndex++;
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
		try {
			findBigAccounts();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FuckinUpKPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args){
		new Thread(new bigAccRunnable()).start();
	}

}
