
public class Director {

	public static void main(String[]args){

		while(true){
			RedditScraper scoop = new RedditScraper();
			scoop.contentSnatch();

			for(int id : /*MongoDB authinfo total collections*/){
				new Thread(new TwitterRunnable(id.OAuthConsumerKey,
						id.OAuthConsumerSecret,
						id.OAuthAccessToken,
						id.OAuthAccessTokenSecret,
						id.isIncubated)).start();
			}
		}
	}
}
