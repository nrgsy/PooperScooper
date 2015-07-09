package content;


import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import management.DataBaseHandler;
import management.FuckinUpKPException;
import management.GlobalStuff;
import management.Maintenance;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/**
 * @author Bojangles and McChrpchrp
 *
 */

public class RedditScraper implements Runnable{

	//when set to true, content snatch will return on the next iteration of its main loop
	public static boolean shutdownRequest;

	//indicator that the contentSnatch is running
	public static boolean isSnatching;

	public RedditScraper() {
		Maintenance.writeLog("New Reddit scraper created", "content");
	}

	/**
	 * @param init
	 * @throws FuckinUpKPException
	 * @throws InterruptedException 
	 * 
	 * ***NOTICE*** if you change this method, be sure isSnatching is set to false when the method exits
	 */
	@SuppressWarnings("unchecked")
	public void contentSnatch() throws FuckinUpKPException {

		isSnatching = true;		
		int pages = 35;
		ArrayList<String> redditLinks;

		for(Entry<String, Object> entry : ContentDirectory.contentDirectory.entrySet()) {

			if (shutdownRequest) {
				isSnatching = false;
				return;
			}

			org.bson.Document sites = (org.bson.Document) entry.getValue();
			redditLinks = (ArrayList<String>) sites.get("reddit");

			if (redditLinks == null) {
				Maintenance.writeLog("Tears, couldn't find reddit links in " + entry.getKey() + 
						"category", "content", -1);
				isSnatching = false;
				return;
			}

			for (String url : redditLinks) {

				HashMap<String, String> content = new HashMap<>();

				//Loop through reddit and gathers title + image link
				for(int j = 0; j<pages; j++) {

					Document document = null;

					try {
						document = Jsoup.connect(url).userAgent("Mozilla").get();
					} 
					catch(SocketTimeoutException e) {
						//give up on this reddit link
						Maintenance.writeLog("Failed to get url, skipping to the next reddit link. "
								+ "Got the following error:\n" + e.toString(), "content", 1);
						break;
					}
					catch(IllegalArgumentException e) {
						//skip to next iteration of the loop
						Maintenance.writeLog("Failed to get url, skipping to the next page. "
								+ "Got the following error:\n" + e.toString(), "content", 1);
						continue;	
					}
					catch (UnknownHostException | ConnectException e) {
						//skip to next iteration of the loop
						Maintenance.writeLog("The internet connection is probably fuckin up, "
								+ "can't snatch content right now", "content", 1);
						isSnatching = false;
						return;
					}
					catch (Exception e) {
						Maintenance.writeLog("Something fucked up in contentSnatch " + 
								Maintenance.getStackTrace(e), "content", -1);
						isSnatching = false;
						return;
					}

					if (document == null) {
						Maintenance.writeLog("Scraped document was null, skipping", "content");
						continue;
					}

					document.select("link[title=applied_subreddit_stylesheet]").first().remove();

					//Must end with jpg or png
					Elements titles = document.select("a.title[href$=.jpg]");
					titles.addAll(document.select("a.title[href$=.png]"));

					//Populate ArrayList with gathered content
					for (Element title : titles){
						content.put(title.attr("href"), title.text());
					}

					//"Clicks" button to next page to start loop at next page
					Elements link = document.select("a[rel=nofollow next]");
					url = link.attr("href");
				}

				//Calls ImageManipulator to check if is within filesize limits (3MB)
				//If good, put into database
				ImageManipulator reviewer = new ImageManipulator();
				content = reviewer.validateContent(content);
				for (Entry<String,String> contentEntry : content.entrySet()) {

					DataBaseHandler.newContent(contentEntry.getValue(),contentEntry.getKey(), 
							"pending" + entry.getKey(), null);
				}
			}
		}
		isSnatching = false;
		Maintenance.writeLog("Content snatch complete", "content");
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Maintenance.writeLog("run method called for RedditScraper", "content");
		try {
			contentSnatch();
		} catch (FuckinUpKPException e) {
			Maintenance.writeLog("Something fucked up in RedditScraper\n" + 
					Maintenance.getStackTrace(e), "content", -1);
		}
	}
}
