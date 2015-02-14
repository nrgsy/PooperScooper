import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DataBaseHandler{
	
	public static synchronized String[] getRandomAssContent() throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("AssContent");
		String[] AssContent = null;
		//Figure out randomness
		
		//[0] should be caption, [1] should be imglink. only returns a two element array.
		
		//add in update for last_accessed and times_accessed
		mongoClient.close();
		return AssContent;
	}
	
	public static synchronized void newAssContent(String caption, String imglink) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("AssContent");
		
		BasicDBObject uniqueCheck = new BasicDBObject("imglink", imglink);
		
		if(dbCollection.find(uniqueCheck).limit(1).count() == 0){
			int count = 0;
			long id_time = new Date().getTime();

			BasicDBObject newAss = new BasicDBObject("_id", id_time);
			newAss.append("caption", caption);
			newAss.append("imglink", imglink);
			newAss.append("times_accessed", count);
			newAss.append("last_accessed", id_time);

			dbCollection.insert(newAss);
			System.out.println("Successfully added new AssContent " + id_time);
		}
		else{
			System.out.println("Image is not unique: "+ imglink);
		}
		mongoClient.close();
	}
	
//////Start region: add to array
	public static synchronized void addArrayToSchwergsArray(int index, String[] StringArr, String column) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");

		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");

		BasicDBObject query = new BasicDBObject("_id", index);
		
		BasicDBObject arr = new BasicDBObject("$addToSet", new BasicDBObject(column, new BasicDBObject("$each", StringArr)));

		dbCollection.update(query, arr);
		System.out.println("successfully added an array of size "+StringArr.length+" to "+column);
		mongoClient.close();
	}

	public static synchronized void addElementToSchwergsArray(int index, String element, String column) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject query = new BasicDBObject("_id", index);
		BasicDBObject ele = new BasicDBObject("$addToSet", new BasicDBObject(column, element));

		dbCollection.update(query, ele);
		System.out.println("successfully added an element to "+ column);
		mongoClient.close();
	}
	
	public static synchronized void addFollowers(int index, String[] followersArr) throws UnknownHostException{
		addArrayToSchwergsArray(index,followersArr,"followers");
	}
	
	public static synchronized void addFollowing(int index, String[]followingArr) throws UnknownHostException{
		addArrayToSchwergsArray(index,followingArr,"following");
	}
	
	public static synchronized void addToFollow(int index, String[]toFollowArr) throws UnknownHostException{
		addArrayToSchwergsArray(index,toFollowArr,"to_follow");
	}
	
	public static synchronized void addWhitelist(int index, String[]whitelistArr) throws UnknownHostException{
		addArrayToSchwergsArray(index,whitelistArr,"whitelist");
	}
	
	public static synchronized void addBigAccount(int index, String bigAccountElement) throws UnknownHostException{
		addElementToSchwergsArray(index,bigAccountElement,"bigAccounts");
	}
//////End region: Add to array
	
	public static synchronized void updateYesterdayFollowers(int index){
		//TODO
	}
	
	public static synchronized String getToFollow(int index) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		String[] toFollowArr = null;
		BasicDBObject query = new BasicDBObject("_id", index);
		BasicDBObject pop = new BasicDBObject("$pop", new BasicDBObject("to_follow", -1));
		BasicDBObject slice = new BasicDBObject("to_follow", new BasicDBObject("$slice", 1));
		DBCursor cursor = dbCollection.find(query,slice);
		BasicDBList toFollowList = (BasicDBList) cursor.next().get("to_follow");
		cursor.close();		
		toFollowArr = Arrays.copyOf(toFollowList.toArray(), toFollowList.toArray().length, String[].class);
		addWhitelist(index, toFollowArr);
		dbCollection.update(query, pop);
		mongoClient.close();
		return toFollowArr[0];
	}
	
	
	public static synchronized String[] popMultipleFollowing(int index, int amount) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		String[] toUnfollowArr = null;
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		BasicDBObject query = new BasicDBObject("_id", index);
		BasicDBObject slice = new BasicDBObject("following", new BasicDBObject("$slice", amount));
		BasicDBObject pop = new BasicDBObject("$pop", new BasicDBObject("following", -1));
		DBCursor cursor = dbCollection.find(query, slice);
		BasicDBList toUnfollowList = (BasicDBList) cursor.next().get("following");
		cursor.close();
		toUnfollowArr = Arrays.copyOf(toUnfollowList.toArray(), toUnfollowList.toArray().length, String[].class);
		for(int i = 0; i<amount; i++){
			dbCollection.update(query, pop);
		}
		mongoClient.close();
		return toUnfollowArr;
	}
	
////// Start region: get array size
	public static synchronized int getSchwergsyAccountArraySize(int index, String column) {
		MongoClient mongoClient = null;
		int size = 0;
		
		try {
			mongoClient = new MongoClient();
			DB db = mongoClient.getDB("Schwergsy");
			DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
			BasicDBObject query = new BasicDBObject("_id", index);
			DBCursor cursor = dbCollection.find(query);
			BasicDBList SchwergsList = (BasicDBList)cursor.next().get(column);
			cursor.close();
			size =  SchwergsList.size();
		} 		
		catch (UnknownHostException e) {
			System.out.println("Error getSchwergsyAccountArraySize");
			e.printStackTrace();
		}
		
		finally{
			mongoClient.close();
			
		}
		
		return size;
	}
	
	public static int getFollowersSize(int index) throws UnknownHostException{
		return getSchwergsyAccountArraySize(index, "followers");
	}
	
	public static int getFollowingSize(int index) throws UnknownHostException{
		return getSchwergsyAccountArraySize(index, "following");
	}
	
	public static int getToFollowSize(int index) throws UnknownHostException{
		return getSchwergsyAccountArraySize(index, "to_follow");
	}

//////End region: Get array size
	
	
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
			BasicDBList bigAccounts) throws UnknownHostException {
		
		//make the id be an integer 0 to n, where n is the number of accounts we have. See what phil did for assContent as an example of how to make something the id
		
		System.out.println("inserting a new Schwergsy Account");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");		
		
		BasicDBObject basicBitch = new BasicDBObject("_id", getCollectionSize("SchwergsyAccounts"))
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
		.append("bigAccounts", bigAccounts);
		
		dbCollection.insert(basicBitch);
		
		mongoClient.close();
	}

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

		BasicDBObject authInfo = (BasicDBObject) schwergsyAccount.get("authorizationInfo");		

		return authInfo;	
	}

	public static synchronized long getCollectionSize(String collectionName) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection(collectionName);
		long count = dbCollection.count();
		mongoClient.close();
		return count;
	}
}
