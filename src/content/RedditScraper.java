package content;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import management.DataBaseHandler;
import management.FuckinUpKPException;
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
	
	boolean init;

	public RedditScraper(boolean init) {
		Maintenance.writeLog("New Reddit scraper created");
		Maintenance.runStatus.put("reddit", true);
		this.init = init;
	}

	/**
	 * @param init
	 * @throws FuckinUpKPException
	 * @throws InterruptedException 
	 */
	public void contentSnatch() throws FuckinUpKPException, InterruptedException {
		ArrayList<String> captions = new ArrayList<String>();
		ArrayList<String> imglinks = new ArrayList<String>(); 

		String url = "http://www.reddit.com/r/blackpeopletwitter";

		int pages = 1;

		if(init){
			pages = 35;
		}

		//Loop through reddit and gathers title + image link
		for(int j = 0; j<pages; j++){
			Document document = null;

			try {
				document = Jsoup.connect(url).userAgent("Mozilla").get();
			} 
			catch (IOException e) {
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
					DataBaseHandler.newContent(captions.get(i),imglinks.get(i), "pendingass");
				} catch (UnknownHostException e) {
					Maintenance.writeLog("Could not insert content:\n" + 
							captions.get(i)+"\n"+imglinks.get(i), "content");
					e.printStackTrace();
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
		}
		finally{
			Maintenance.runStatus.put("reddit", false);
		}
	}
}
