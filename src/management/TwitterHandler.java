package management;

import java.net.UnknownHostException;
import java.util.HashSet;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.mongodb.BasicDBObject;

public class TwitterHandler {

	/**
	 * @param info the authorization info from the account in the schwergsyAccounts collection
	 * @return
	 */
	public static Twitter getTwitter(BasicDBObject info) {	
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
	public static HashSet<Long> getFollowers(Twitter bird) throws TwitterException, UnknownHostException{
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
	
	

}
