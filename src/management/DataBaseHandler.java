package management;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author Bojangles and McChrpchrp
 *	The data access layer, a class for interfacing with the MongoDB database
 */
public class DataBaseHandler{

	/**
	 * @param type The type of content, types are "ass", "pendingass", "workout", "weed"
	 * @param index The index of the Schwergs account that wants the content
	 * @return a DBObject that is the random content, or null if none found
	 * @throws UnknownHostException
	 */
	public static synchronized DBObject getRandomContent(String type, long index) throws UnknownHostException {

		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection collection = getCollection(type, db);

		double size = collection.count();

		HashSet<DBObject> contentSample = new HashSet<>();

		//just add them all if we have less than 200 contents
		if (size < 200) {
			DBCursor cursor = collection.find();
			while (cursor.hasNext()) {
				DBObject content = cursor.next();
				BasicDBList list = (BasicDBList) content.get("accessInfo");
				boolean valid = true;
				for (Object o : list) {
					DBObject info = (DBObject) o;
					if ((long) info.get("index") == index) {
						long msSinceLastAccess = new Date().getTime() - (long) info.get("lastAccess");
						//don't consider content that's been posted sooner that a week ago (on this account)
						if (msSinceLastAccess < 604800000) {
							valid = false;
						}
						break;
					}
				}
				if (valid) {
					contentSample.add(content);
				}
			}
		}
		else {
			int numAttempts = 0;
			while (contentSample.size() < 100 && numAttempts < 1000) {
				numAttempts++;
				int randNum = (int) (Math.random() * size);
				DBObject content = collection.find().limit(-1).skip(randNum).next();
				BasicDBList list = (BasicDBList) content.get("accessInfo");
				boolean valid = true;
				for (Object o : list) {
					DBObject info = (DBObject) o;
					if ((long) info.get("index") == index) {
						long msSinceLastAccess = new Date().getTime() - (long) info.get("lastAccess");
						//don't consider content that's been posted sooner that a week ago (on this account)
						if (msSinceLastAccess < 604800000) {
							valid = false;
						}
						break;
					}
				}
				if (valid) {
					contentSample.add(content);
				}
			}
		}

		//determine which content in the sample was posted least recently
		Object[] contentArray = contentSample.toArray();
		DBObject bestContent = null;
		long minTimesAccessed = Long.MAX_VALUE;
		for (int i = 0; i < contentArray.length; i++) {
			DBObject candidateContent = (DBObject)contentArray[i];		
			BasicDBList list = (BasicDBList) candidateContent.get("accessInfo");
			boolean foundMatch = false;

			for (Object o : list) {
				DBObject info = (DBObject) o;
				if ((long) info.get("index") == index) {
					foundMatch = true;
					long timesAccessed = (long) info.get("timesAccessed");
					long lastAccess = (long) info.get("lastAccess");
					//restrict 
					if (timesAccessed < minTimesAccessed &&
							lastAccess < (new Date().getTime()) - GlobalStuff.WEEK_IN_MILLISECONDS) {
						minTimesAccessed = timesAccessed;
						bestContent = candidateContent;
					}
					break;
				}
			}
			//foundmatch will be false if the candidate content's access info didn't contain the index of
			//the schwergs account corresponding to index
			if (!foundMatch) {
				//because we know this content has never been used before
				bestContent = candidateContent;
				minTimesAccessed = 0;
				break;
			}
		}

		if (bestContent != null) {
			BasicDBList list = (BasicDBList) bestContent.get("accessInfo");
			DBObject info = null;
			for (Object o : list) {
				info = (DBObject) o;
				if ((long) info.get("index") == index) {
					long timesAccessed = (long) info.removeField("timesAccessed");
					info.removeField("lastAccess");
					info.put("timesAccessed", timesAccessed + 1);
					info.put("lastAccess", new Date().getTime());
					break;
				}
				info = null;
			}

			if (info == null) {
				info = new BasicDBObject()
				.append("index", index)
				.append("timesAccessed", 1L)
				.append("lastAccess", new Date().getTime());
				list.add(info);
			}

			bestContent.removeField("accessInfo");
			bestContent.put("accessInfo", list);

			long id = (long) bestContent.get("_id");
			BasicDBObject query = new BasicDBObject("_id", id);

			collection.findAndModify(query, bestContent);
		}

		mongoClient.close();
		return bestContent;
	}

	
	/**
	 * @param sourceType the type of the content e.g. "ass", "pendingass"
	 * @param sourceLink the link of the content to remove
	 * @throws UnknownHostException
	 */
	public static synchronized void removeContent(String sourceType, String sourceLink)
			throws UnknownHostException {		
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection sourceCollection = getCollection(sourceType, db);	
		BasicDBObject query = new BasicDBObject("imglink", sourceLink);
		sourceCollection.remove(query);
		mongoClient.close();
	}
	
	
	
	/**
	 * Pulls the globals from the GlobalVariables collection and uses them to initialize the globals in
	 * GlobalStuff
	 * 
	 * @throws UnknownHostException
	 */
	public static synchronized void findAndSetGlobalVars() throws UnknownHostException {
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection collection = db.getCollection("GlobalVariables");
		if (!db.collectionExists("GlobalVariables")) {
			System.err.println("ERROR: cannot pull global vars. Collection GlobalVariables does not exist");
		}
		else if (collection.getCount() == 1) {
			//Can use findOne() because the GlobalVariables collection will never have more than one entry
			BasicDBObject globalVars = (BasicDBObject) collection.findOne();
			setGlobalVars(globalVars);
		}
		else {
			System.err.println("ERROR: GlobalVariables had " + collection.getCount() + "entries. "
					+ "It should only ever have one entry, or not exist at all");
		}
		mongoClient.close();
	}
	
	/**
	 * 	//TODO uses the given dbobject to set (or initialize) the global variables in GlobalStuff
	 * 
	 * @param globalVars the BasicDBObject containing the global variables to initialize with 
	 * (typically pulled from the GlobalVariables collection of the database)
	 */
	public static synchronized void setGlobalVars(BasicDBObject globalVars) {
	
		GlobalStuff.FOLLOW_TIME_MIN = globalVars.getLong("FOLLOW_TIME_MIN");
		GlobalStuff.FOLLOW_TIME_MAX = globalVars.getLong("FOLLOW_TIME_MAX");
		GlobalStuff.POST_TIME_MIN = globalVars.getLong("POST_TIME_MIN");
		GlobalStuff.POST_TIME_MAX = globalVars.getLong("POST_TIME_MAX");
		GlobalStuff.FOLLOW_TIME_INCUBATED_MIN = globalVars.getLong("FOLLOW_TIME_INCUBATED_MIN");
		GlobalStuff.FOLLOW_TIME_INCUBATED_MAX = globalVars.getLong("FOLLOW_TIME_INCUBATED_MAX");	
		GlobalStuff.FOLLOWING_BASE_CAP = globalVars.getInt("FOLLOWING_BASE_CAP");
		GlobalStuff.ALPHA = globalVars.getDouble("ALPHA");
		GlobalStuff.MAX_NUMER_OF_POSTS = globalVars.getLong("MAX_NUMER_OF_POSTS");
		GlobalStuff.POST_TIME_CONSTANT = globalVars.getLong("POST_TIME_CONSTANT");
	}

	/**
	 * @param caption
	 * @param imglink
	 * @param type see getCollection below for content types
	 * @throws UnknownHostException
	 */
	
	
	//TODO, add specialization such that content that's created depends on the type
	//e.g. pendingass should only have link and caption, schwagass should only have link, and regular
	//ass should have all attributes as shown belows
	
	
	public static synchronized void newContent(String caption, String imglink, String type) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		BasicDBObject uniqueCheck = new BasicDBObject("imglink", imglink);
		DBCollection dbCollection = getCollection(type, db);

		if(dbCollection.find(uniqueCheck).limit(1).count() == 0) {
			long id_time = new Date().getTime();

			BasicDBObject newAss = new BasicDBObject("_id", id_time);
			newAss.append("caption", caption);
			newAss.append("imglink", imglink);
			//accessInfo is a list of BasicBObjects, [{index : ..., timesAccessed : ..., lastAccess : ...},{...}]
			newAss.append("accessInfo", new BasicDBList());

			dbCollection.insert(newAss);
			System.out.println("Successfully added new content of type " + type);
		}
		else{
			System.out.println("Image is not unique: "+ imglink);
		}
		mongoClient.close();
	}

	public static DBCollection getCollection(String type, DB db) {

		DBCollection dbCollection = null;
		switch (type.toLowerCase()) {
		case "ass" :
			dbCollection = db.getCollection("AssContent");
			break;
		case "pendingass" :
			dbCollection = db.getCollection("PendingAssContent");
			break;
		case "schwagass" :
			dbCollection = db.getCollection("SchwagAssContent");
			break;
		case "workout" :
			dbCollection = db.getCollection("Workout");
			break;
		case "pendingworkout" :
			dbCollection = db.getCollection("PendingWorkout");
			break;
		case "schwagworkout" :
			dbCollection = db.getCollection("SchwagWorkout");
			break;
		case "weed" :
			dbCollection = db.getCollection("Weed");
			break;
		case "pendingweed" :
			dbCollection = db.getCollection("PendingWeed");
			break;
		case "schwagweed" :
			dbCollection = db.getCollection("SchwagWeed");
			break;
		case "college" :
			dbCollection = db.getCollection("College");
			break;
		case "pendingcollege" :
			dbCollection = db.getCollection("PendingCollege");
			break;
		case "schwagcollege" :
			dbCollection = db.getCollection("SchwagCollege");
			break;
		case "canimals" :
			dbCollection = db.getCollection("Canimals");
			break;
		case "pendingcanimals" :
			dbCollection = db.getCollection("PendingCanimals");
			break;
		case "schwagcanimals" :
			dbCollection = db.getCollection("SchwagCanimals");
			break;
		case "space" :
			dbCollection = db.getCollection("Space");
			break;
		case "pendingspace" :
			dbCollection = db.getCollection("PendingSpace");
			break;
		case "schwagspace" :
			dbCollection = db.getCollection("SchwagSpace");
			break;
		default:
			System.err.println("Tears, " + type + " is schwag. Doesn't match an expected collection name");
		}

		return dbCollection;
	}

	//////Start region: add to array
	/**
	 * Adds the given an array of strings to the given list in the a particular schwergsy account
	 * @param index The index (database id) of the schwergsy account
	 * @param StringArr	The Strings to add to the list (should be twitter id's)
	 * @param column The list to add to (e.g. followers, toFollow, whiteList, etc)
	 * @throws UnknownHostException
	 */
	public static synchronized void addArrayToSchwergsArray(int index, Long[] StringArr, String column) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");

		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");

		BasicDBObject query = new BasicDBObject("_id", index);

		BasicDBObject arr = new BasicDBObject("$addToSet", new BasicDBObject(column, new BasicDBObject("$each", StringArr)));

		dbCollection.update(query, arr);
		System.out.println("successfully added an array of size "+StringArr.length+" to "+column);
		mongoClient.close();
	}

	/**
	 * replaces the schwergs array in column with the given String Array
	 * 
	 * @param index
	 * @param StringArr
	 * @param column
	 * @throws UnknownHostException
	 */
	public static synchronized void replaceSchwergsArray(int index, HashSet<Long> Set, String column) throws UnknownHostException{

		BasicDBList freshList = new BasicDBList();

		freshList.addAll(Set);

		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");		
		dbCollection.update(
				new BasicDBObject("_id", index),
				new BasicDBObject("$set", new BasicDBObject("followers", freshList)));
		mongoClient.close();

		System.out.println("successfully replaced array: " + column);
	}

	/**
	 * Adds the given object to the given list in the a particular schwergsy account
	 * @param index The index (databse id) of the schwergsy account
	 * @param element The object that should be a String (a twitter id) or a BasicDBObject (a statistic) 
	 * that will be added to the list 
	 * @param column The list to add to (e.g. followers, toFollow, whiteList, statistics, etc)
	 * @throws UnknownHostException
	 * @throws FuckinUpKPException 
	 */
	public static synchronized void addElementToSchwergsArray(int index, Object element, String column) throws UnknownHostException, FuckinUpKPException{

		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject query = new BasicDBObject("_id", index);

		BasicDBObject ele = null;

		if (element instanceof String) { 
			ele = new BasicDBObject("$addToSet", new BasicDBObject(column, (String) element));
		}
		else if (element instanceof BasicDBObject) { 
			ele = new BasicDBObject("$addToSet", new BasicDBObject(column, (BasicDBObject) element));
		}
		else {
			System.out.println();
			throw new FuckinUpKPException("method addElementToSchwergsArray is not a trash can.\nYou can't just be throwin' whatever you please in.");
		}

		dbCollection.update(query, ele);
		System.out.println("successfully added an element to "+ column);
		mongoClient.close();
	}

	/**
	 * @param index
	 * @param followersArr
	 * @throws UnknownHostException
	 */
	public static synchronized void addFollowers(int index, Long[] followersArr) throws UnknownHostException{
		addArrayToSchwergsArray(index,followersArr,"followers");
	}

	/**
	 * @param index
	 * @param followingArr
	 * @throws UnknownHostException
	 */
	public static synchronized void addFollowing(int index, Long[]followingArr) throws UnknownHostException{
		addArrayToSchwergsArray(index,followingArr,"following");
	}

	/**
	 * @param index
	 * @param toFollowArr
	 * @throws UnknownHostException
	 */
	public static synchronized void addToFollow(int index, Long[]toFollowArr) throws UnknownHostException{
		addArrayToSchwergsArray(index,toFollowArr,"toFollow");
	}

	/**
	 * @param index
	 * @param whitelistArr
	 * @throws UnknownHostException
	 */
	public static synchronized void addWhitelist(int index, Long[]whitelistArr) throws UnknownHostException{
		addArrayToSchwergsArray(index,whitelistArr,"whiteList");
	}

	/**
	 * @param index
	 * @param bigAccountElement
	 * @throws UnknownHostException
	 * @throws FuckinUpKPException 
	 */
	public static synchronized void addBigAccount(int index, long bigAccountID, long latestTweet) throws UnknownHostException, FuckinUpKPException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject query = new BasicDBObject("_id", index);

		BasicDBObject bigAccount = new BasicDBObject("user_id", bigAccountID);
		bigAccount.append("strikes", 0);
		bigAccount.append("latestTweet", latestTweet);
		BasicDBObject ele = new BasicDBObject("$push", new BasicDBObject("bigAccounts",bigAccount));

		dbCollection.update(query, ele);
		System.out.println("successfully added an element to bigAccounts");
		mongoClient.close();
	}


	/**
	 * @param index The index of the Schwergsy account we want to add the statistic to
	 * @param unFollows number of unFollows since the last Statistic was taken
	 * @param newFollows unFollows since the last Statistic was taken
	 * @param totalFollowers 
	 * @throws FuckinUpKPException 
	 * @throws UnknownHostException 
	 */
	public static synchronized void addNewStatistic(int index, int unfollows, int newFollows, int retainedFollowers, int totalFollowers) throws UnknownHostException, FuckinUpKPException {
		MongoClient mongoClient = null;


		long now = new Date().getTime();
		long oldStatCreationTime = now;

		try {
			mongoClient = new MongoClient();
			DB db = mongoClient.getDB("Schwergsy");
			DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
			BasicDBObject query = new BasicDBObject("_id", index);
			DBCursor cursor = dbCollection.find(query);
			BasicDBList statList = (BasicDBList) cursor.next().get("statistics");
			cursor.close();
			mongoClient.close();
			//get time of creation of the last statistic is statList, which should be the most recent stat
			if (statList.size() > 0) {
				oldStatCreationTime = ((BasicDBObject) statList.get(statList.size() -1)).getLong("creationDate");
			}
		} 		
		catch (UnknownHostException e) {
			System.out.println("Error getSchwergsyAccountArraySize");
			e.printStackTrace();
		}

		BasicDBObject stat = new BasicDBObject()
		.append("creationDate", now)
		.append("timeSinceLastStat", now - oldStatCreationTime)
		.append("unFollows", unfollows)
		.append("newFollows", newFollows)
		.append("retainedFollowers", retainedFollowers)
		.append("totalFollowers", totalFollowers);

		addElementToSchwergsArray(index, stat, "statistics");
	}

	//////End region: Add to array

	/**
	 * This should be called once a day for each Schwergsy account.
	 * @param index The id of the Schwergsy account
	 * @throws FuckinUpKPException 
	 * @throws UnknownHostException 
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static synchronized void updateFollowers(int index, HashSet<Long> freshFollowerSet)  throws UnknownHostException, FuckinUpKPException, FileNotFoundException, UnsupportedEncodingException {

		Date now = new Date();
		String fileName = now.getMonth() + "-" + now.getDate() + "-" + now.getHours()  + "-" + now.getMinutes()  + "-" + now.getSeconds();
		PrintWriter writer = new PrintWriter("FollowerLists/" + fileName + ".txt", "UTF-8");

		Long[] freshFollowerArray = freshFollowerSet.toArray(new Long[0]);

		int x = 0;				
		while (x < freshFollowerArray.length) {
			if (x%11 != 0) {
				writer.print(freshFollowerArray[x] + ", ");
				x++;
			}
			else {
				writer.print("\n" + freshFollowerArray[x] + ", ");
				x++;
			}
		}
		writer.close();

		int OGsize = freshFollowerSet.size();

		HashSet<Long> storedFollowerSet = new HashSet<>();

		BasicDBList tmpList = getSchwergsyAccountArray(index, "followers");
		ListIterator<Object> iter = tmpList.listIterator();

		while (iter.hasNext()) {
			storedFollowerSet.add((Long) iter.next());
		}

		//getting the number of cool new followers and unfollowing bastards.

		@SuppressWarnings("unchecked")
		HashSet<Long> retainedFollowerSet = (HashSet<Long>) freshFollowerSet.clone();
		@SuppressWarnings("unchecked")
		HashSet<Long> OGFreshFollowerSet = (HashSet<Long>) freshFollowerSet.clone();

		if (!retainedFollowerSet.retainAll(storedFollowerSet)) {
			System.out.println("FYI: freshFollowerSet doesn't have any elements that weren't already in storedFollowerSet");
		}
		int retainedFollowers = retainedFollowerSet.size();

		if (!freshFollowerSet.removeAll(retainedFollowerSet)) {
			System.out.println("FYI: freshFollowerSet doesn't have any common elements with retainedFollowerSet");
		}
		int newFollows = freshFollowerSet.size();

		if (!storedFollowerSet.removeAll(retainedFollowerSet)) {
			System.out.println("FYI: storedFollowerSet doesn't have any common elements with retainedFollowerSet");
		}
		int unfollows = storedFollowerSet.size();

		addNewStatistic(index, unfollows, newFollows, retainedFollowers, OGsize);
		replaceSchwergsArray(index, OGFreshFollowerSet, "followers");
	}

	/**
	 * @param index
	 * @param amount
	 * @return
	 * @throws UnknownHostException
	 */
	public static synchronized Long[] popMultipleFollowing(int index, int amount) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		Long[] toUnfollowArr = null;
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject query = new BasicDBObject("_id", index);
		BasicDBObject slice = new BasicDBObject("following", new BasicDBObject("$slice", amount));
		BasicDBObject pop = new BasicDBObject("$pop", new BasicDBObject("following", -1));
		DBCursor cursor = dbCollection.find(query, slice);
		BasicDBList toUnfollowList = (BasicDBList) cursor.next().get("following");
		cursor.close();
		toUnfollowArr = Arrays.copyOf(toUnfollowList.toArray(), toUnfollowList.toArray().length, Long[].class);
		for(int i = 0; i<amount; i++){
			dbCollection.update(query, pop);
		}
		mongoClient.close();
		return toUnfollowArr;
	}

	//Tested and given the Bojangles Seal of Approval
	private static synchronized Object getBigAccountStuff(int index, int bigAccountIndex, String property)throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject query = new BasicDBObject("_id",index);
		BasicDBObject find;
		if(bigAccountIndex != 0){		
			BasicDBList sliceParameters = new BasicDBList();
			sliceParameters.add(bigAccountIndex-1);
			sliceParameters.add(1);
			find = new BasicDBObject("bigAccounts", new BasicDBObject("$slice", sliceParameters));
		}
		else{
			int sliceParameters = 1;
			find = new BasicDBObject("bigAccounts", new BasicDBObject("$slice", sliceParameters));
		}
		
		DBCursor cursor = dbCollection.find(query, find);
		BasicDBList answerList = (BasicDBList)cursor.next().get("bigAccounts");
		BasicDBObject answer = (BasicDBObject)answerList.get(0);
		cursor.close();
		mongoClient.close();
		return answer.get(property);
	}

	public static synchronized long getBigAccount(int index, int bigAccountIndex)throws UnknownHostException{
		return  (Long)getBigAccountStuff(index, bigAccountIndex,"user_id");
	}

	public static synchronized int getBigAccountStrikes(int index, int bigAccountIndex) throws UnknownHostException{
		return (int)getBigAccountStuff(index, bigAccountIndex,"strikes");
	}

	public static synchronized Long getBigAccountLatestTweet(int index, int bigAccountIndex) throws UnknownHostException{
		return (Long)getBigAccountStuff(index, bigAccountIndex,"latestTweet");
	}

	//Tested and given the Bojangles Seal of Approval
	private static synchronized void editBigAccountStuff(int index, int bigAccountIndex, String property, Object change) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject query = new BasicDBObject("_id",index);
		BasicDBObject updater = new BasicDBObject("$set", new BasicDBObject("bigAccounts."+bigAccountIndex+"."+property,
				change));
		dbCollection.findAndModify(query, updater);
		mongoClient.close();
	}
	
	public static synchronized void editBigAccountStrikes(int index, int bigAccountIndex, int number) throws UnknownHostException{
		editBigAccountStuff(index,bigAccountIndex,"strikes",number);
	}

	public static synchronized void editBigAccountLatestTweet(int index, int bigAccountIndex, long tweetID) throws UnknownHostException{
		editBigAccountStuff(index,bigAccountIndex,"latestTweet",tweetID);
	}
	
	//Tested and given the Bojangles Seal of Approval
	public static void moveBigAccountToEnd(int index, int bigAccIndex) throws UnknownHostException, FuckinUpKPException {
		long user_id = getBigAccount(index, bigAccIndex);
		int strikes = getBigAccountStrikes(index,bigAccIndex);
		long latestTweet = -1;//getBigAccountLatestTweet(index,bigAccIndex);
		
		System.out.println(user_id+":"+strikes+":"+latestTweet);
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject match = new BasicDBObject("_id", index); //to match your direct app document
		BasicDBObject update = new BasicDBObject("user_id", user_id);
		dbCollection.update(match, new BasicDBObject("$pull", new BasicDBObject("bigAccounts", update)));
		mongoClient.close();
		
		addBigAccount(index, user_id, latestTweet);
		
		
	}
	
	//Tested and given the Bojangles Seal of Approval
	public static boolean isInBigAccounts(int index, long bigAccountID) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject query = new BasicDBObject("_id", index);
		query.append("bigAccounts",new BasicDBObject("$elemMatch", new BasicDBObject("user_id", bigAccountID)));
		BasicDBObject call = new BasicDBObject("bigAccounts.$",1);
		DBCursor cursor = dbCollection.find(query, call);
		if(cursor.hasNext()){
			return true;
		}
		return false;
	}

	//Tested and given the Bojangles Seal of Approval
	public static synchronized boolean isWhiteListed(int index, long user_id) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject query = new BasicDBObject("_id", index);
		query.append("whiteList", new BasicDBObject("$eq", user_id));
		BasicDBObject call = new BasicDBObject("whiteList.$", 1);
		DBCursor cursor = dbCollection.find(query, call);
		if(cursor.hasNext()){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 
	 * Gets one user_id from ToFollow to follow
	 * 
	 * 
	 * 
	 * 
	 * @param index
	 * @return
	 * @throws UnknownHostException
	 */
	public static synchronized Long getOneToFollow(int index) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		Long[] toFollowArr = null;
		BasicDBObject query = new BasicDBObject("_id", index);
		BasicDBObject pop = new BasicDBObject("$pop", new BasicDBObject("toFollow", -1));
		BasicDBObject slice = new BasicDBObject("toFollow", new BasicDBObject("$slice", 1));
		DBCursor cursor = dbCollection.find(query,slice);
		BasicDBList toFollowList = (BasicDBList) cursor.next().get("toFollow");
		cursor.close();
		//added so we don't have to call toArray() twice
		Object[] toFollowArray = toFollowList.toArray();
		toFollowArr = Arrays.copyOf(toFollowArray, toFollowArray.length, Long[].class);
		addWhitelist(index, toFollowArr);
		dbCollection.update(query, pop);
		mongoClient.close();
		return toFollowArr[0];
	}

	/**
	 * @param index the Schwergsy Account to get the list from
	 * @param column the specific list to return
	 * @return
	 * @throws UnknownHostException 
	 */
	public static synchronized BasicDBList getSchwergsyAccountArray(int index, String column) throws UnknownHostException {

		MongoClient mongoClient = new MongoClient();
			DB db = mongoClient.getDB("Schwergsy");
			DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
			BasicDBObject query = new BasicDBObject("_id", index);
			DBCursor cursor = dbCollection.find(query);
			BasicDBList SchwergsList = (BasicDBList) cursor.next().get(column);
			cursor.close();
			mongoClient.close();
			return SchwergsList;
			
	}

	////// Start region: get array size
	/**
	 * @param index
	 * @param column
	 * @return
	 * @throws UnknownHostException 
	 */
	public static synchronized int getSchwergsyAccountArraySize(int index, String column) throws UnknownHostException {

		return getSchwergsyAccountArray(index, column).size();
	}

	/**
	 * @param index
	 * @return
	 * @throws UnknownHostException
	 */
	public static int getFollowersSize(int index) throws UnknownHostException{
		return getSchwergsyAccountArraySize(index, "followers");
	}

	public static int getBigAccountsSize(int index) throws UnknownHostException{
		return getSchwergsyAccountArraySize(index, "bigAccounts");
	}

	/**
	 * @param index
	 * @return
	 * @throws UnknownHostException
	 */
	public static int getFollowingSize(int index) throws UnknownHostException{
		return getSchwergsyAccountArraySize(index, "following");
	}

	/**
	 * @param index
	 * @return
	 * @throws UnknownHostException
	 */
	public static int getToFollowSize(int index) throws UnknownHostException{
		return getSchwergsyAccountArraySize(index, "toFollow");
	}

	//////End region: Get array size

	/**
	 * Convenience method to insert a new account into the SchwergsyAccounts collection
	 * using empty lists for the missing parameters
	 * @param accountID
	 * @param name
	 * @param customerSecret
	 * @param customerKey
	 * @param authorizationSecret
	 * @param authorizationKey
	 * @param isIncubated
	 * @throws UnknownHostException 
	 */
	public static synchronized void insertSchwergsyAccount(
			String accountID,
			String name,
			String customerSecret,
			String customerKey,
			String authorizationSecret,
			String authorizationKey,
			boolean isIncubated) throws UnknownHostException {

		insertSchwergsyAccount(
				accountID,
				name,
				customerSecret,
				customerKey,
				authorizationSecret,
				authorizationKey,
				isIncubated,
				new BasicDBList(),
				new BasicDBList(),
				new BasicDBList(),
				new BasicDBList(),
				new BasicDBList(),
				new BasicDBList());	
	}


	/**
	 * Insert a new account into the the SchwergsyAccounts collection
	 * @param accountID
	 * @param name
	 * @param customerSecret
	 * @param customerKey
	 * @param authorizationSecret
	 * @param authorizationKey
	 * @param isIncubated
	 * @param followers
	 * @param following
	 * @param toFollow
	 * @param whiteList
	 * @param bigAccounts
	 * @param statistics
	 * @throws UnknownHostException
	 */
	public static synchronized void insertSchwergsyAccount(
			String accountID,
			String name,
			String customerSecret,
			String customerKey,
			String authorizationSecret,
			String authorizationKey,
			boolean isIncubated,
			BasicDBList followers,
			BasicDBList following,
			BasicDBList toFollow,
			BasicDBList whiteList,
			BasicDBList bigAccounts,
			BasicDBList statistics) throws UnknownHostException {

		System.out.println("inserting a new Schwergsy Account");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");		

		BasicDBObject basicBitch = new BasicDBObject("_id", (int) getCollectionSize("SchwergsyAccounts"))
		.append("accountID", accountID)
		.append("name", name)
		.append("customerSecret", customerSecret)
		.append("customerKey", customerKey)
		.append("authorizationSecret", authorizationSecret)
		.append("authorizationKey", authorizationKey)
		.append("isIncubated", isIncubated)
		.append("followers", followers)
		.append("following", following)
		.append("toFollow", toFollow)
		.append("whiteList", whiteList)
		.append("bigAccounts", bigAccounts)
		.append("statistics", statistics);

		dbCollection.insert(basicBitch);

		mongoClient.close();
	}

	/**
	 * @param index the id of the Schwergsy account
	 * @return a BasicDBObject containing the customerSecret, customerKey, authorizationSecret, authorizationKey,
	 * and isIncubated info from the Schwergsy account
	 * @throws Exception
	 */
	public static synchronized BasicDBObject getAuthorizationInfo(int index) throws Exception {

		System.out.println("scooping authInfo at index " + index);
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");

		BasicDBObject query = new BasicDBObject("_id", index);
		DBCursor cursor = dbCollection.find(query);

		DBObject schwergsyAccount = cursor.next();

		mongoClient.close();
		cursor.close();

		String customerSecret = (String) schwergsyAccount.get("customerSecret");
		String customerKey = (String) schwergsyAccount.get("customerKey");
		String authorizationSecret = (String) schwergsyAccount.get("authorizationSecret");
		String authorizationKey = (String) schwergsyAccount.get("authorizationKey");
		boolean isIncubated = (boolean) schwergsyAccount.get("isIncubated");

		BasicDBObject authInfo = new BasicDBObject()
		.append("customerSecret", customerSecret)
		.append("customerKey", customerKey)
		.append("authorizationSecret", authorizationSecret)
		.append("authorizationKey", authorizationKey)
		.append("isIncubated", isIncubated);

		return authInfo;	
	}
	
	
	public static synchronized void initUpdateFollowing(Twitter bird, int index) throws TwitterException, UnknownHostException{
		int ratecount = 0;
		IDs IDCollection;
		HashSet<Long> following = new HashSet<>();
		IDCollection = bird.getFriendsIDs(-1);
		for(long id : IDCollection.getIDs()){
			following.add(id);
		}
		ratecount++;
		while(IDCollection.getNextCursor()!=0 && ratecount<14){
			IDCollection = (bird.getFriendsIDs(IDCollection.getNextCursor()));
			for(long id : IDCollection.getIDs()){
				following.add(id);
			}
			ratecount++;
		}
		
		addFollowing(index, following.toArray(new Long[following.size()]));
		
	}

	/**
	 * @param collectionName
	 * @return The size of the given collection
	 * @throws UnknownHostException
	 */
	public static synchronized long getCollectionSize(String collectionName) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection(collectionName);
		long count = dbCollection.count();
		mongoClient.close();
		return count;
	}

	/**
	 * Print all the elements of Schwergsy account nicely to the console
	 * @param schwergsyAccountName
	 */
	public static synchronized void prettyPrintAccount(String schwergsyAccountName) {		
		MongoClient mongoClient = null;

		try {
			mongoClient = new MongoClient();
			DB db = mongoClient.getDB("Schwergsy");
			DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
			BasicDBObject query = new BasicDBObject("name", schwergsyAccountName);
			DBCursor cursor = dbCollection.find(query);
			BasicDBObject account = (BasicDBObject) cursor.next();
			cursor.close();
			Set<Entry<String, Object>> entrySet = account.entrySet();

			for (Entry<String, Object> e : entrySet) {
				System.out.println(e.getKey() + " = " + e.getValue() + "\n");
			}
		} 		
		catch (Exception e) {
			System.out.println("Error printing");
			e.printStackTrace();
		}
		finally{
			mongoClient.close();
		}		
	}

	/**
	 * print the statistics from the given account real nicely
	 * @param schwergsyAccountName
	 */
	public static synchronized void prettyPrintStatistics(String schwergsyAccountName) {		

		MongoClient mongoClient = null;

		try {
			mongoClient = new MongoClient();
			DB db = mongoClient.getDB("Schwergsy");
			DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
			BasicDBObject query = new BasicDBObject("name", schwergsyAccountName);
			DBCursor cursor = dbCollection.find(query);
			BasicDBObject account = (BasicDBObject) cursor.next();
			cursor.close();
			BasicDBList statList = (BasicDBList) account.get("statistics");

			if (statList.size() > 0) {

				System.out.println("Statistics for account " + account.get("name") + ":\n");
				Set<Entry<String, Object>> entrySet = ((BasicDBObject) statList.get(0)).entrySet();
				int columnWidth = 32;

				//print the stat names
				for (Entry<String, Object> e : entrySet) {

					String key = e.getKey();
					int textWidth = key.length();

					//the number of spaces to insert on each side
					int padding = (columnWidth - textWidth)/2;

					if (padding < 0) {
						System.out.println("String to large for column");
					}

					printNSpaces(padding);
					System.out.print(key);
					//need this if to fix off by one misprints
					if (textWidth % 2 == 0)
						printNSpaces(padding - 1);
					else
						printNSpaces(padding);
					System.out.print("|");

				}
				System.out.println("\n");

				//print the values for each stat
				for (Object obj : statList) {

					BasicDBObject stat = (BasicDBObject) obj;
					entrySet = stat.entrySet();
					boolean isDate = true;
					boolean isTime = true;

					for (Entry<String, Object> e : entrySet) {

						String value = null;

						//convert to millisecond date to something more readable
						if(isDate) {
							Long millisecondDate = (Long) e.getValue();
							Date date = new Date(millisecondDate);
							value = date.toString();
							isDate = false;
						}
						else if (isTime) {
							float millisecondTime = ((Long) e.getValue()).floatValue();
							int hours = (int) (millisecondTime / (60 * 60 * 1000));
							int mins = (int) (millisecondTime / (60 * 1000)) % 60;
							float secs = Math.round(((millisecondTime / 1000) % 60) * 1000)/1000f;
							value = hours + ":" + mins + ":" + secs;
							isTime = false;
						}
						else {
							value = e.getValue().toString();
						}

						int textWidth = value.length();

						//the number of spaces to insert on each side
						int padding = (columnWidth - textWidth)/2;

						if (padding < 0) {
							System.out.println("String to large for column");
						}

						printNSpaces(padding);
						System.out.print(value);

						//need this if to fix off by one misprints
						if (textWidth % 2 == 0)
							printNSpaces(padding - 1);
						else
							printNSpaces(padding);
						System.out.print("|");
					}
					System.out.println();
				}
			}
			else {
				System.out.println("No stats have been entered yet");
			}
		} 		
		catch (Exception e) {
			System.out.println("Error printing");
			e.printStackTrace();
		}

		finally{
			mongoClient.close();
		}		
	}

	public static synchronized void printNSpaces(int n) {	
		for (int i = 0; i < n; i++) {
			System.out.print(" ");
		}
	}
}




