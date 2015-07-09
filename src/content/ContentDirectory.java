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

	//contentDirectory is a document with many documents appended
	//each document is a content type (ass, weed, workout, etc) mapped a document with one or more
	//documents appended to it. Each of those documents is a scraper site (reddit, imgur, etc)
	//mapped to a list of links. So the link groups are specific to a scraper site and a content type
	public static Document contentDirectory;

	/**
	 * Initialize the contentDirectory variable with these hard coded links.
	 */
	public static void init() {

		contentDirectory = new Document();

		//For ass
		//////////////////////////////////////////////////////////////////////////////////////////
		//list of links to places (on reddit for example) to scrape from
		ArrayList<String> redditAssLinks = new ArrayList<>();
		redditAssLinks.add("http://www.reddit.com/r/blackpeopletwitter"); //add sites here
		redditAssLinks.add("http://www.reddit.com/r/memes");
		Document redditAssDoc = new Document();
		redditAssDoc.append("reddit", redditAssLinks);

		//could also append this when imgur implementation is ready: 
		//contentDirectory.append("ass", imgurAssDoc);
		contentDirectory.append("ass", redditAssDoc);
		//////////////////////////////////////////////////////////////////////////////////////////

		//for workout
		//////////////////////////////////////////////////////////////////////////////////////////
		ArrayList<String> redditWorkoutLinks = new ArrayList<>();
		redditWorkoutLinks.add("http://www.reddit.com/r/gymfails");
		redditWorkoutLinks.add("http://www.reddit.com/r/gymmemes");
		Document redditWorkoutDoc = new Document();
		redditWorkoutDoc.append("reddit", redditWorkoutLinks);

		contentDirectory.append("workout", redditWorkoutDoc);
		//////////////////////////////////////////////////////////////////////////////////////////

		//for minecraft
		//////////////////////////////////////////////////////////////////////////////////////////
		ArrayList<String> redditMinecraftLinks = new ArrayList<>();
		redditMinecraftLinks.add("http://www.reddit.com/r/Minecraft/");
		redditMinecraftLinks.add("http://www.reddit.com/r/MinecraftSuggestions");
		Document redditMinecraftDoc = new Document();
		redditMinecraftDoc.append("reddit", redditMinecraftLinks);

		contentDirectory.append("minecraft", redditMinecraftDoc);
		//////////////////////////////////////////////////////////////////////////////////////////

		//for minecraft
		//////////////////////////////////////////////////////////////////////////////////////////
		ArrayList<String> redditKSPLinks = new ArrayList<>();
		redditKSPLinks.add("https://www.reddit.com/r/KerbalSpaceProgram/");
		redditKSPLinks.add("https://www.reddit.com/r/KSPMemes");
		redditKSPLinks.add("http://www.reddit.com/r/KerbalAcademy");
		redditKSPLinks.add("https://www.reddit.com/r/RealSolarSystem/");
		Document redditKSPDoc = new Document();
		redditKSPDoc.append("reddit", redditKSPLinks);

		contentDirectory.append("KSP", redditKSPDoc);
		//////////////////////////////////////////////////////////////////////////////////////////

	}

	public static ArrayList<String> getContentTypes() {	
		ArrayList<String> contentTypes = new ArrayList<>();	
		for (String type: contentDirectory.keySet()) {		
			//adds the content String to the list, e.g. "ass" or "workout"
			contentTypes.add(type);
		}
		return contentTypes;
	}
}
