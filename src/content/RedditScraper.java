package content;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
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
		

		org.bson.Document reddits = GlobalStuff.redditScraping;

		int pages = 35;

		for(Entry<String, Object> entry : reddits.entrySet()){
			ArrayList<String> captions = new ArrayList<String>();
			ArrayList<String> imglinks = new ArrayList<String>(); 
			
			ArrayList<String> linkAndContentPool = (ArrayList<String>) entry.getValue();
			String contentPool = linkAndContentPool.get(1);
			String url = linkAndContentPool.get(0);
			//Loop through reddit and gathers title + image link
			for(int j = 0; j<pages; j++){
				Document document = null;

				try {
					document = Jsoup.connect(url).userAgent("Mozilla").get();
				} 
				catch (Exception e) {
					e.printStackTrace();
					throw new FuckinUpKPException("can't get to reddit.com");
				}

				//Must end with jpg or png
				Elements titles = document.select("a.title[href$=.jpg]");
				titles.addAll(document.select("a.title[href$=.png]"));

				//Populate ArrayList with gathered content
				for (Element title : titles){
					captions.add(title.text());
					imglinks.add(title.attr("href"));
				}

				//"Clicks" button to next page to start loop at next page
				Elements link = document.select("a[rel=nofollow next]");
				url = link.attr("href");
			}


			//Calls ImageManipulator to check if is within filesize limits (3MB)
			//If good, put into database
			ImageManipulator reviewer = new ImageManipulator();
			for(int i =0; i<imglinks.size();i++){
				if(!reviewer.isValid(imglinks.get(i))){
					imglinks.remove(i);
					captions.remove(i);
				}
				else{
					try {
						DataBaseHandler.newContent(captions.get(i),imglinks.get(i), contentPool);
					} catch (UnknownHostException e) {
						Maintenance.writeLog("Could not insert content:\n" + 
								captions.get(i)+"\n"+imglinks.get(i), "content");
						e.printStackTrace();
					}
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
			e.printStackTrace();
			Maintenance.writeLog("RedditScraper failed somewhere in snatching content");
		}
	}
}
