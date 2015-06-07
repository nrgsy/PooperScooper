package management;



import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MongoFuckAround {

	public static void main(String[]args) throws Exception{
		
//		DataBaseHandler.newContent("dude in skirt", "http://i.imgur.com/vx3glwO.jpg", "pendingass");
//		DataBaseHandler.newContent("girl in a bed", "http://i.imgur.com/x0ZSk6c.jpg", "pendingass");
//		DataBaseHandler.newContent("archer", "http://i.imgur.com/Rsed2ln.jpg", "pendingass");
//		DataBaseHandler.newContent("lawn", "http://i.imgur.com/gg0gBej.jpg", "pendingass");
		
		//System.out.println(DataBaseHandler.getOneToFollow(0));

		//DataBaseHandler.moveBigAccountToEnd(0, 0);
		//DataBaseHandler.insertSchwergsyAccount("WorkoutGetSwole", "uHQV3x8pHZD7jzteRwUIw", "OxfLKbnhfvPB8cpe5Rthex1yDR5l0I7ztHLaZXnXhmg", "2175141374-5Gg6WRBpW1NxRMNt5UsEUA95sPVaW3a566naNVI", "Jz2nLsKm59bbGwCxtg7sXDyfqIo7AqO6JsvWpGoEEux8t", false, false);
		//System.out.println(DataBaseHandler.getOneToFollow(0));
		Twitter bird;
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("uHQV3x8pHZD7jzteRwUIw")
		.setOAuthConsumerSecret("OxfLKbnhfvPB8cpe5Rthex1yDR5l0I7ztHLaZXnXhmg")
		.setOAuthAccessToken("2175141374-5Gg6WRBpW1NxRMNt5UsEUA95sPVaW3a566naNVI")
		.setOAuthAccessTokenSecret("Jz2nLsKm59bbGwCxtg7sXDyfqIo7AqO6JsvWpGoEEux8t");
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
		
		//System.out.println(bird.getUserTimeline("jerseyshorerose").get(0).getRetweetedStatus().getUser().getFollowersCount());
		System.out.println(TwitterHandler.isAtRateLimit(bird,"/statuses/retweeters/ids", 0));
		long oneToFollow = DataBaseHandler.getOneToFollow(0);
		int toFollowSize = DataBaseHandler.getToFollowSize(0);
		int whiteListSize = DataBaseHandler.getWhiteListSize(0);
		int bigAccountHarvestIndex = DataBaseHandler.getBigAccountHarvestIndex(0);
		int bigaccountstrikes = DataBaseHandler.getBigAccountStrikes(0, 68);
		int bigaccountouts = DataBaseHandler.getBigAccountOuts(0, 68);
		System.out.println("One to follow : "+oneToFollow);
		System.out.println("ToFollow size : " + toFollowSize);
		System.out.println("WhiteList size : " + whiteListSize);
		System.out.println("bigAccountHarvestIndex : " + bigAccountHarvestIndex);
		System.out.println("bigAccount 0 strikes : " + bigaccountstrikes);
		System.out.println("bigAccount 0 outs : " + bigaccountouts);
	}
}
