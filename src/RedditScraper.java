import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class RedditScraper implements Runnable{


	public void contentSnatch(boolean init) throws FuckinUpKPException{
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
				System.out.println("can't get to reddit.com");
				e.printStackTrace();
				throw new FuckinUpKPException();
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
					DataBaseHandler.insertImage(GlobalStuff.DATABASE_NAME, 
							GlobalStuff.COLLECTION_NAME, 
							new AssImage(imglinks.get(i),
									captions.get(i),
									0,
									new Date()));
				} catch (UnknownHostException e) {
					System.out.println("Could not insert content:\n"+captions.get(i)+"\n"+imglinks.get(i));
					e.printStackTrace();
				}
			}
		}
	}

	public void contentSnatch() throws FuckinUpKPException{
		contentSnatch(false);
	}

	@Override
	public void run() {
		try {
			new RedditScraper().contentSnatch();
		} catch (FuckinUpKPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



}
