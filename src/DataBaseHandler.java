import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;

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
	 * @param type The type of content, e.g. ass, pendingass, etc
	 * @param index The index of the Schwergs account that want the content
	 * @return an array of Strings containing some pseudo-random content 
	 * @throws UnknownHostException
	 */
	public static synchronized String[] getRandomContent(String type, int index) throws UnknownHostException{

		//TODO keep the ass content sorted by date last used so that you only scoop a random ass-content from the section
		//that hasn't been used in the last x days.

		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = getCollection(type, db);
		String[] AssContent = null;
		//Figure out randomness

		//[0] should be caption, [1] should be imglink. only returns a two element array.

		//add in update for last_accessed and times_accessed
		mongoClient.close();
		return AssContent;
	}


	/**
	 * inserts a new caption and link to the image into the database
	 * 
	 * @param caption
	 * @param imglink
	 * @throws UnknownHostException
	 */
	public static synchronized void newContent(String caption, String imglink, String type) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");

		BasicDBObject uniqueCheck = new BasicDBObject("imglink", imglink);

		DBCollection dbCollection = getCollection(type, db);
		
		if(dbCollection.find(uniqueCheck).limit(1).count() == 0){
			int count = 0;
			long id_time = new Date().getTime();

			BasicDBObject newAss = new BasicDBObject("_id", id_time);
			newAss.append("caption", caption);
			newAss.append("imglink", imglink);
			newAss.append("times_accessed", count);
			newAss.append("last_accessed", id_time);
			//accessInfo is a list of key value pairs, keys being the account that accessed it, and the values being the number of times it did
			newAss.append("accessInfo", new BasicDBObject());
			
			dbCollection.insert(newAss);
			System.out.println("Successfully added new AssContent " + id_time);
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
		case "workout" :
			dbCollection = db.getCollection("Workout");
			break;
		case "pendingworkout" :
			dbCollection = db.getCollection("PendingWorkout");
			break;
		case "weed" :
			dbCollection = db.getCollection("Weed");
			break;
		case "pendingweed" :
			dbCollection = db.getCollection("PendingWeed");
			break;
		case "college" :
			dbCollection = db.getCollection("College");
			break;
		case "pendingcollege" :
			dbCollection = db.getCollection("PendingCollege");
			break;
		case "canimals" :
			dbCollection = db.getCollection("Canimals");
			break;
		case "pendingcanimals" :
			dbCollection = db.getCollection("PendingCanimals");
			break;
		default:
			System.out.println("Tears, " + type + " is schwag");
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
			System.out.println("This method is not a trash can. You can't just be throwin' whatever you please in.");
			throw new FuckinUpKPException();
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
		addArrayToSchwergsArray(index,toFollowArr,"to_follow");
	}

	/**
	 * @param index
	 * @param whitelistArr
	 * @throws UnknownHostException
	 */
	public static synchronized void addWhitelist(int index, Long[]whitelistArr) throws UnknownHostException{
		addArrayToSchwergsArray(index,whitelistArr,"whitelist");
	}

	/**
	 * @param index
	 * @param bigAccountElement
	 * @throws UnknownHostException
	 * @throws FuckinUpKPException 
	 */
	public static synchronized void addBigAccount(int index, String bigAccountElement) throws UnknownHostException, FuckinUpKPException{
		addElementToSchwergsArray(index,bigAccountElement,"bigAccounts");
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

		//******Temporary, write the new FollowerList to a file so we have it recorded	
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
		//********

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

	public static synchronized Long getBigAccount(int index)throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		Long bigAccount = null;
		DBCollection dbCollection = db.getCollection("SchwergsyAccount");
		BasicDBObject query = new BasicDBObject("_id",index);
		BasicDBObject slice = new BasicDBObject("bigAccounts", new BasicDBObject("$slice",1));
		DBCursor cursor = dbCollection.find(query,slice);
		BasicDBList bigAccountList = (BasicDBList) cursor.next().get("bigAccounts");
		cursor.close();
		bigAccount = Arrays.copyOf(bigAccountList.toArray(), bigAccountList.toArray().length, Long[].class)[0];
		mongoClient.close();
		return bigAccount;
	}

	public static synchronized boolean isWhiteListed(int index, long user_id) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccount");
		BasicDBObject clause1 = new BasicDBObject("$eq", new BasicDBObject("_id", index));
		BasicDBObject clause2 = new BasicDBObject("_whiteList", new BasicDBObject("$in", user_id));
		BasicDBList and = new BasicDBList();
		and.add(clause1);
		and.add(clause2);
		BasicDBObject query = new BasicDBObject("$and", and);
		DBCursor cursor = dbCollection.find(query);
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
		BasicDBObject pop = new BasicDBObject("$pop", new BasicDBObject("to_follow", -1));
		BasicDBObject slice = new BasicDBObject("to_follow", new BasicDBObject("$slice", 1));
		DBCursor cursor = dbCollection.find(query,slice);
		BasicDBList toFollowList = (BasicDBList) cursor.next().get("to_follow");
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
	 */
	public static synchronized BasicDBList getSchwergsyAccountArray(int index, String column) {

		MongoClient mongoClient = null;

		try {
			mongoClient = new MongoClient();
			DB db = mongoClient.getDB("Schwergsy");
			DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
			BasicDBObject query = new BasicDBObject("_id", index);
			DBCursor cursor = dbCollection.find(query);
			BasicDBList SchwergsList = (BasicDBList) cursor.next().get(column);
			cursor.close();
			return SchwergsList;
		} 		
		catch (Exception e) {
			System.out.println("Error in getting " + column);
			e.printStackTrace();
		}

		finally{
			mongoClient.close();
		}
		return null;
	}

	////// Start region: get array size
	/**
	 * @param index
	 * @param column
	 * @return
	 */
	public static synchronized int getSchwergsyAccountArraySize(int index, String column) {

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
		return getSchwergsyAccountArraySize(index, "to_follow");
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



