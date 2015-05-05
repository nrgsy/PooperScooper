import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.mongodb.BasicDBList;

import twitter4j.IDs;
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
	public FollowRunnable(String OAuthConsumerKey, String OAuthConsumerSecret, String OAuthAccessToken, String OAuthAccessTokenSecret, int index){
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
			bird.createFavorite(bird.createFriendship(DataBaseHandler.getOneToFollow(index)).getStatus().getId());
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
		int cap = 1000;
		int diff = (int)(cap+(Math.log(sizeFollowers)/Math.log(100))) - sizeFollowing;
		if(diff>0){
			Long[] unfollowArr = DataBaseHandler.popMultipleFollowing(index, diff);
			for(int i =0; i<unfollowArr.length; i++){
				bird.destroyFriendship(unfollowArr[i]);
			}
		}
	}
	
	/**
	 * @param init
	 * @throws TwitterException
	 * @throws UnknownHostException 
	 */
	//TODO update so that it passes cursor onto slave account for further processing
	public HashSet<Long> getFollowers() throws TwitterException, UnknownHostException{
		int ratecount = 0;
		IDs blah;
		blah = bird.getFollowersIDs(-1);
		ArrayList<Long> followers = new ArrayList<Long>();
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
		
		HashSet<Long> followersSet = new HashSet<>();				
		followersSet.addAll(followers);
		
		return followersSet;
	}

	/**
	 * @throws TwitterException
	 */
	//This method should only be for existing Twitter accounts. New ones will update their following as they go.
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

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//TODO figure out procedure for follow related methods
		try {
			DataBaseHandler.updateFollowers(index, getFollowers());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FuckinUpKPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		(new Thread(new FollowRunnable(0))).start();
	}
	
}

