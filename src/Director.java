import java.net.UnknownHostException;


public class Director {
	
	private final static String DATABASE_NAME = "";
	private final static String COLLECTION_NAME = "";

	public static void main(String[]args) throws UnknownHostException, Exception{
		

		while(true){
//			RedditScraper scoop = new RedditScraper();
//			scoop.contentSnatch();

			for(int id =0;id< DataBaseHandler.getCollectionSize(DATABASE_NAME, COLLECTION_NAME);id++){
				AuthorizationInfo info = DataBaseHandler.getAuthorizationInfo(DATABASE_NAME, COLLECTION_NAME, id);
				new Thread(new TwitterRunnable(info.getCustomerKey(),
						info.getCustomerSecret(),
						info.getAuthorizationKey(),
						info.getAuthorizationSecret(),
						info.isIncubated())).start();
			}
		}
	}
}
