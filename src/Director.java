import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;

import com.mongodb.BasicDBObject;


/**
 * @author Bojangles and McChrpchrp
 *
 */
public class Director {

	/**
	 * @param args
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	public static void main(String[]args) throws UnknownHostException, Exception{
		long scrapetime = 86400000;

		for(int id =0; id < DataBaseHandler.getCollectionSize("SchwergsyAccounts"); id++){
			final BasicDBObject info = DataBaseHandler.getAuthorizationInfo(id);

			long followtime_min = 86400L;
			long followtime_max = 123430L;
			long posttime_min = 900000L;
			long posttime_max = 1500000L;
			Random r = new Random();
			long followtime = followtime_min+((long)(r.nextDouble()*(followtime_max-followtime_min)));
			long posttime = posttime_min+((long)(r.nextDouble()*(posttime_max-posttime_min)));

			//If in incubation, follows at a rate of 425 per day
			if((boolean) info.get("isIncubated")){
				followtime = 203250;
			}

			new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
				@Override
				public void run() {
					new TwitterRunnable((String) info.get("customerKey"),
							(String) info.get("customerSecret"),
							(String) info.get("authorizationKey"),
							(String) info.get("authorizationSecret"),
							0);
				}},0L, posttime);


			new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
				@Override
				public void run() {
					new FollowRunnable((String) info.get("customerKey"),
							(String) info.get("customerSecret"),
							(String) info.get("authorizationKey"),
							(String) info.get("authorizationSecret"),
							0);
				}}, 0L, followtime);
		}

		new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				new Thread(new RedditScraper()).start();
			}},0L, scrapetime);
	}
}
