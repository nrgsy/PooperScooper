package twitterRunnables;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import management.DataBaseHandler;
import management.FuckinUpKPException;
import management.GlobalStuff;
import management.Maintenance;
import management.TwitterHandler;
import twitter4j.Paging;
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
	private int bigAccountIndex;

	/**
	 * @param OAuthConsumerKey
	 * @param OAuthConsumerSecret
	 * @param OAuthAccessToken
	 * @param OAuthAccessTokenSecret
	 */
	public bigAccRunnable(Twitter twitter, int index, int bigAccountIndex){
		Maintenance.writeLog("New bigAccRunnable created");
		this.index = index;
		this.bigAccountIndex = bigAccountIndex;
		bird = twitter;
		Maintenance.runStatus.put(index+"bigAcc", true);
	}

	public bigAccRunnable(){
		Maintenance.writeLog("New bigAccRunnable created");
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("uHQV3x8pHZD7jzteRwUIw")
		.setOAuthConsumerSecret("OxfLKbnhfvPB8cpe5Rthex1yDR5l0I7ztHLaZXnXhmg")
		.setOAuthAccessToken("2175141374-5Gg6WRBpW1NxRMNt5UsEUA95sPVaW3a566naNVI")
		.setOAuthAccessTokenSecret("Jz2nLsKm59bbGwCxtg7sXDyfqIo7AqO6JsvWpGoEEux8t");
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
		this.index = 0;
		this.bigAccountIndex = DataBaseHandler.getBigAccountHarvestIndex(0);
		Maintenance.runStatus.put(index+"bigAcc", true);
	}

	public synchronized void findBigAccounts() throws TwitterException, InterruptedException, UnknownHostException, FuckinUpKPException{
		//TODO add latestTweet capability to bigAccount in DBH
		HashSet<Long> AllCandidates = new HashSet<Long>(); 
		ArrayList<Long> bigAccounts = new ArrayList<Long>();
		Iterator<Long> AllCandidatesIterator;
		int maxCandidates = 100;

		//if the schwergsaccount has no bigaccounts and doesn't have enough followers to find more bigaccounts
		if(DataBaseHandler.getBigAccountsSize(index)!=0 && DataBaseHandler.getFollowersSize(index) > 100){
			ArrayList<Long> AllRTerIDs = new ArrayList<Long>();
			//I know this is jank, but i can't make empty ResponseLists, so it's gotta be the way
			ResponseList<Status> OwnTweets = TwitterHandler.getUserTimeline(bird,bird.getId(), index).get(0);

			if(OwnTweets.size()>30){
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
					long[] RTerIDs = TwitterHandler.getRetweeterIds(bird, tweet.getId(), 100, -1, index);
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
				if(AllCandidates.size() == 100){
					break;
				}
				//gets 50 tweets from each retweeter
				Paging querySettings = new Paging();
				querySettings.setCount(50);
				ResponseList<Status> potentialBigAccs = TwitterHandler.getUserTimeline(bird, id, querySettings, index);
				for(Status tweet: potentialBigAccs){
					if(AllCandidates.size() == maxCandidates){
						break;
					}
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

		for (Iterator<Long> i = AllCandidates.iterator(); i.hasNext();) {
			Long user_id = i.next();
			if(DataBaseHandler.isBigAccWhiteListed(index, user_id)){
				i.remove();
			}
		}
		
		AllCandidatesIterator = AllCandidates.iterator(); 
			while(AllCandidates.size() > 300){
				AllCandidatesIterator.remove();
			}
		
		for(long id : AllCandidates){
			Maintenance.writeLog("considering candidate...", index);

			if(DataBaseHandler.isBigAccWhiteListed(index, id)){
				break;
			}

			Paging query = new Paging();
			query.setCount(200);
			ResponseList<Status> timeline = TwitterHandler.getUserTimeline(bird,id, query , index);
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
					bigAccounts.add(id);
				}
			}
		}
		DataBaseHandler.addBigAccounts(index, bigAccounts);
		DataBaseHandler.addBigAccountsWhiteList(index, bigAccounts);
	}

	public void harvestBigAccounts() throws UnknownHostException, InterruptedException, FuckinUpKPException{
		HashSet<Long> toFollowSet = new HashSet<Long>();
		Long lastTweet = DataBaseHandler.getBigAccountLatestTweet(index,bigAccountIndex);
		int maxNoRTTweets = 30;

		//TODO see if we can take more tweets
		//Only gets the 5 latest tweets of the bigAccount candidate. If the bigaccount was harvested 
		//before, it only takes the tweetsafter the latest tweet used.
		Paging querySettings = new Paging();
		querySettings.setCount(100);
		if(lastTweet != -1){
			querySettings.setSinceId(lastTweet);
		}

		ResponseList<Status> tweets = TwitterHandler.getUserTimeline(bird,DataBaseHandler.getBigAccount(index, bigAccountIndex), querySettings, index);
		ArrayList<Status> NoRTTweets = new ArrayList<Status>();

		//Makes sure the tweet is original to the bigAccount candidate
		for(Status tweet: tweets){
			if(!tweet.isRetweet()){
				NoRTTweets.add(tweet);
			}
			if(NoRTTweets.size()==maxNoRTTweets){
				break;
			}
		}

		//Reverse the order so that the latestTweet will be the last tweet used in the upcoming loop.
		Collections.reverse(NoRTTweets);

		//Gets ids of retweeters and puts it into toFollowSet and updates latestTweet for bigAccount
		//By using a HashSet, you get only unique retweeter ids.
		for(Status tweet :NoRTTweets){
			long[] toFollows = TwitterHandler.getRetweeterIds(bird,tweet.getId(), 100, -1, index);
			if(toFollows.length != 0){
				for(long id : toFollows){
					toFollowSet.add(id);
				}
			}
			DataBaseHandler.editBigAccountLatestTweet(index, bigAccountIndex, tweet.getId());
		}

		//If the retweeter is already in the whitelist, then remove that bitch
		for (Iterator<Long> i = toFollowSet.iterator(); i.hasNext();) {
			Long user_id = i.next();
			if(DataBaseHandler.isWhiteListed(index, user_id)){
				i.remove();
			}
		}

		if(toFollowSet.size()==0){
			if(DataBaseHandler.getBigAccountStrikes(index, bigAccountIndex)+1 >= GlobalStuff.BIG_ACCOUNT_STRIKES_FOR_OUT){
				if(DataBaseHandler.getBigAccountOuts(index, bigAccountIndex)+1 >= GlobalStuff.BIG_ACCOUNT_OUTS_FOR_REMOVAL){
					//if it gets however many outs, it's removed from bigAccounts
					DataBaseHandler.deleteBigAccount(index, bigAccountIndex);
				}
				else{
					//if it gets however many strikes, move it to the end of bigAccounts and reset strikes
					//and adds an out
					DataBaseHandler.editBigAccountStrikes(index, bigAccountIndex, 0);
					DataBaseHandler.editBigAccountOuts(index, bigAccountIndex, 
							DataBaseHandler.getBigAccountOuts(index, bigAccountIndex)+1);
					DataBaseHandler.moveBigAccountToEnd(index, bigAccountIndex);
				}
			}
			else{
				//if it gets a strike, add it to what it has now.
				DataBaseHandler.editBigAccountStrikes(index, bigAccountIndex, 
						DataBaseHandler.getBigAccountStrikes(index, bigAccountIndex) + 1);
			}
		}
		else{
			DataBaseHandler.addToFollow(index, new ArrayList<Long>(toFollowSet));
			DataBaseHandler.addWhitelist(index, new ArrayList<Long>(toFollowSet));
			int bigAccountHarvestIndex = DataBaseHandler.getBigAccountsSize(index) == bigAccountIndex ? 0 : bigAccountIndex + 1;
			DataBaseHandler.editBigAccountHarvestIndex(index, bigAccountHarvestIndex);
		}

		Maintenance.writeLog("done harvesting", index);
	}

	@Override
	public void run() {
		Maintenance.writeLog("run method called for bigAccRunnable");
		try {
			if(DataBaseHandler.getToFollowSize(index)>11900 || DataBaseHandler.getBigAccountsSize(index) < 30){
				findBigAccounts();
			}
			else{
				harvestBigAccounts();
			}
		} catch (UnknownHostException | InterruptedException
				| FuckinUpKPException | TwitterException e) {
			System.out.println(e.getStackTrace());
			Maintenance.writeLog("Something fucked up in bigAccRunnable", index);
		}
	}

	public static void main(String[] args) throws UnknownHostException{
		DataBaseHandler.findAndSetGlobalVars();
		new Thread(new bigAccRunnable()).start();
	}

}
