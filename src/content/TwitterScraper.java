package content;

import java.util.ArrayList;

import management.Maintenance;
import management.TwitterHandler;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

// UNDER CONSTRUCTION!!

public class TwitterScraper implements Runnable{
	
	//when set to true, content snatch will return on the next iteration of its main loop
	public static boolean shutdownRequest;

	//indicator that the contentSnatch is running
	public static boolean isSnatching;
	
	private static Twitter bird;
	
	// The id of the Twitter user we'll be scraping from
	private long scrapee;
	
	// The id of the last Tweet we scraped from the scrapee
	private long lastScrape;
	
	// Where the content will go
	private String content;
	
	
	
	public TwitterScraper(long scrapee, long lastScrape, String content){
		Maintenance.writeLog("New Twitter scraper created", "content");
		
		// Eliza is instantiated to scrape Twitter
		// We should rename Eliza to Sweet Dee since
		// Eliza is being assigned to variable bird
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("rMkaQoP1qlW9CFk7PF4f5tHfi")
		.setOAuthConsumerSecret("1iouHUA0Ky3D38vX8tUoiRZ1BrMQZq5VDiTVN3ybJtMS6VCJQ6")
		.setOAuthAccessToken("4601850373-ToOARDLHS3a5bEcPpzzx6IMbpEbsczptnCWCRRT")
		.setOAuthAccessTokenSecret("EVSiMRyk5USW3gDFPJjIGfdZJgaKk7Zh1L2IAvupdQS4r");
		TwitterFactory tf = new TwitterFactory(cb.build());
		bird = tf.getInstance();
		this.scrapee = scrapee;
		this.lastScrape = lastScrape;
		this.content = content;
	}
	
	public void contentSnatch() throws TwitterException{
		
		Paging paging = new Paging();
		
		if(lastScrape != 0){
		paging.setSinceId(lastScrape);
		}
		paging.setCount(200);
		
		ArrayList<ResponseList<Status>> timeline = new ArrayList<ResponseList<Status>>();
		timeline.add(bird.getUserTimeline(scrapee, paging));
//		TODO this is the correct implementation to be done later.
//		ArrayList<ResponseList<Status>> timeline = TwitterHandler.getUserTimeline(bird, scrapee,paging, 
//				0);
		
		for(Status status : timeline.get(0)){
			// TODO Change this once text content is implemented. This allows only pics to be scraped with captions
			if(status.getMediaEntities().length !=0){
				// TODO this currently gets the text of the tweet, with the image link through twitter's bullshit pic viwer
				// Change this so that it removes the link. maybe with regex?
				System.out.println(status.getText());
				System.out.println(status.getMediaEntities()[0].getMediaURLHttps());
			}
		}
		
	}
	
	

	@Override
	public void run() {
		try {
			contentSnatch();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		TwitterScraper shit = new TwitterScraper(868817972L, 1234L, "thing");
		shit.run();
	}
	

}
