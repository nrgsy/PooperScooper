import java.net.UnknownHostException;


public class Director {

	public static void main(String[]args) throws UnknownHostException, Exception{
		

		while(true){
//			RedditScraper scoop = new RedditScraper();
//			scoop.contentSnatch();

			for(int id =0;id< DataBaseHandler.getCollectionSize(GlobalStuff.DATABASE_NAME, GlobalStuff.COLLECTION_NAME);id++){
				AuthorizationInfo info = DataBaseHandler.getAuthorizationInfo(GlobalStuff.DATABASE_NAME, GlobalStuff.COLLECTION_NAME, id);
				new Thread(new TwitterRunnable(info.getCustomerKey(),
						info.getCustomerSecret(),
						info.getAuthorizationKey(),
						info.getAuthorizationSecret(),
						info.isIncubated())).start();
			}
		}
	}
}
