package content;


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

	public RedditScraper() {
		Maintenance.writeLog("New Reddit scraper created");
	}

	/**
	 * @param init
	 * @throws FuckinUpKPException
	 * @throws InterruptedException 
	 */
	public void contentSnatch() throws FuckinUpKPException, InterruptedException {

		int pages = 35;

		ArrayList<String> redditLinks;

		for(Entry<String, Object> entry : ContentDirectory.contentDirectory.entrySet()) {
			org.bson.Document sites = (org.bson.Document) entry.getValue();
			redditLinks = (ArrayList<String>) sites.get("reddit");

			if (redditLinks == null) {
				Maintenance.writeLog("***ERROR*** Tears, couldn't find reddit links ***ERROR***", "KP");
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
					catch(IllegalArgumentException e) {
						//do nothing
						Maintenance.writeLog("Found invalid url, skipping.", "content");
						continue;	
					}
					catch (SocketTimeoutException e) {
						//do nothing
						continue;
					}
					catch (Exception e) {
						e.printStackTrace();
						throw new FuckinUpKPException("can't get to reddit.com");
					}

					if (document == null) {
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
	}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			Maintenance.writeLog("run method called for RedditScraper");
			try {
				contentSnatch();
			} catch (FuckinUpKPException | InterruptedException e) {
				Maintenance.writeLog("***ERROR*** Something fucked up in RedditScraper ***ERRROR*** \n"+Maintenance.writeStackTrace(e), "KP");
			}
		}
	}
