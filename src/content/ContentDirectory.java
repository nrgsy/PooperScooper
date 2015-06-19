/**
 * 
 */
package content;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import management.Maintenance;

import org.bson.Document;

/**
 * A class to represent the Content Director
 *
 */
public class ContentDirectory {

	//the list of documents
	//each document is a content type (ass, weed, workout, etc) mapped a list of documents
	//each of those documents is a scraper site (reddit, imgur, etc) mapped to a list of links
	//So the link groups are specific a scraper site and a content type
	public static ArrayList<Document> contentDirectory;

	/**
	 * Initialize the contentDirectory variable with these hard coded links.
	 */
	public static void init() {
		
		contentDirectory = new ArrayList<>();
		
		//For ass
		//////////////////////////////////////////////////////////////////////////////////////////
		//list of links to places (on reddit for example) to scrape from
		ArrayList<String> redditAssLinks = new ArrayList<>();
		redditAssLinks.add("http://www.reddit.com/r/blackpeopletwitter"); //add sites here
		redditAssLinks.add("http://www.reddit.com/r/memes");
		Document redditAssDoc = new Document();
		redditAssDoc.append("reddit", redditAssLinks);
		
		ArrayList<Document> assScraperSiteList = new ArrayList<>();
		assScraperSiteList.add(redditAssDoc); //could also add something like imgurAssDoc in the future
		
		Document assDoc = new Document();
		assDoc.append("ass", assScraperSiteList);
		contentDirectory.add(assDoc);
		//////////////////////////////////////////////////////////////////////////////////////////

		//for workout
		//////////////////////////////////////////////////////////////////////////////////////////
		ArrayList<String> redditWorkoutLinks = new ArrayList<>();
		redditWorkoutLinks.add("http://www.reddit.com/r/gymfails");
		redditWorkoutLinks.add("http://www.reddit.com/r/gymmemes");
		Document redditWorkoutDoc = new Document();
		redditWorkoutDoc.append("reddit", redditWorkoutLinks);
		
		ArrayList<Document> workoutScraperSiteList = new ArrayList<>();
		workoutScraperSiteList.add(redditWorkoutDoc);
		
		Document workoutDoc = new Document();
		workoutDoc.append("workout", workoutScraperSiteList);
		contentDirectory.add(workoutDoc);
		//////////////////////////////////////////////////////////////////////////////////////////
	}
	
	public static ArrayList<String> getContentTypes() {	
		ArrayList<String> contentTypes = new ArrayList<>();
		
		for (Document doc : contentDirectory) {
			
			HashSet<Entry<String, Object>> entrySet = (HashSet<Entry<String, Object>>) doc.entrySet();		
			if (entrySet.size() != 1) {
				Maintenance.writeLog("***ERROR*** Tears, invalid element in"
						+ "Content Directory ***ERROR***","KP");
				return null;
			}		
			//adds the content String to the list, e.g. "ass" or "workout"
			contentTypes.add(entrySet.iterator().next().getKey());
		}
		return contentTypes;
	}
}
