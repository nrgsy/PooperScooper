package src;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class RedditScraper {
	

	private ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();

	private ArrayList<String> captions = new ArrayList<String>();
	private ArrayList<String> imglinks = new ArrayList<String>();
	public ArrayList<ArrayList<String>> contentSnatch(){
		

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
				captions.add(title.text()+"haha");
				// TODO DAL stuff in here
				imglinks.add(title.attr("href"));
				// TODO DAL stuff in here
			}
			Elements link = document.select("a[rel=nofollow next]");
			url = link.attr("href");
		}
		
		content.add(captions);
		content.add(imglinks);
		return content;
	}

}
