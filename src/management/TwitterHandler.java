package management;

import twitter4j.Twitter;
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
	
	
	

}
