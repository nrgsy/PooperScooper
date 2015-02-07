import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class RedditScraper {

private ArrayList<String> captions;
private ArrayList<String> imglinks;

	public ArrayList<String> contentSnatch(){

		String url = "http://www.reddit.com/r/blackpeopletwitter";

		for(int j = 0; j<10; j++){

			Document document = null;
			try {
				document = Jsoup.connect(url).userAgent("Mozilla").get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Elements titles = document.select("a.title[href$=.jpg]");
			titles.addAll(document.select("a.title[href$=.png]"));

			for (Element title : titles){
				captions.add(title.text());
				// TODO DAL stuff in here
				imglinks.add(title.attr("href"));
				// TODO DAL stuff in here
			}
			Elements link = document.select("a[rel=nofollow next]");
			url = link.attr("href");
		}
		
		return captions;
	}
}
