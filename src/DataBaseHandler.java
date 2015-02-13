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
		String increment = column+".count";
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
	
	public static synchronized String[] getToFollow(int index, int amount) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("Schwergsy");
		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");
		String[] toFollowArr = null;
		BasicDBObject query = new BasicDBObject("_id", index);
		BasicDBObject slice = new BasicDBObject("to_follow", new BasicDBObject("$slice", amount));
		DBCursor cursor = dbCollection.find(query,slice);
		BasicDBList toFollowList = (BasicDBList) cursor.next().get("to_follow");
		cursor.close();
		
		
//Be sure to add them to whitelist and remove from toFollow
		
		
		toFollowArr = Arrays.copyOf(toFollowList.toArray(), toFollowList.toArray().length, String[].class);
		mongoClient.close();
		return toFollowArr;
	}
	
	
	
////// Start region: get array size
	public static synchronized int getSchwergsyAccountArraySize(int index, String column){
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
			size =  SchwergsList.toArray().length;
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
			String dbName,
			String collectionName,
			SchwergsyAccount account) throws UnknownHostException {

		System.out.println("inserting a new Schwergsy Account");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);

		AuthorizationInfo authInfo = account.getAuthorizationInfo();

		BasicDBList authInfoList = new BasicDBList();
		authInfoList.add(new BasicDBObject("customerSecret", authInfo.getCustomerSecret()));
		authInfoList.add(new BasicDBObject("customerKey", authInfo.getCustomerKey()));
		authInfoList.add(new BasicDBObject("authorizationSecret", authInfo.getAuthorizationSecret()));
		authInfoList.add(new BasicDBObject("authorizationKey", authInfo.getAuthorizationKey()));
		authInfoList.add(new BasicDBObject("isIncubated", authInfo.isIncubated()));

		BasicDBObject basicBitch = new BasicDBObject()
		.append("accountID", account.getAccountID())
		.append("name", account.getName())
		.append("followers", account.getFollowers())
		.append("following", account.getFollowing())
		.append("toFollow", account.getToFollow())
		.append("whiteList", account.getWhiteList())
		.append("bigAccounts", account.getBigAccounts())
		.append("authorizationInfo", authInfoList);

		dbCollection.insert(basicBitch);
	}

	public static synchronized AuthorizationInfo getAuthorizationInfo(String dbName, String collectionName, int index) throws Exception {

		System.out.println("scooping authInfo at index " + index);
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);

		DBCursor dbCursor = dbCollection.find();

		for (int i = 0; i < index; i++) {			
			if (dbCursor.hasNext())
				dbCursor.next();
			else {			
				System.out.println("that ass passed in an invalid index for this authorization info");
				throw new FuckinUpKPException();
			}
		}

		DBObject schwergsAccount = dbCursor.next();	

		BasicDBList authInfoList = (BasicDBList) schwergsAccount.get("authorizationInfo");		

		String customerSecret = (String) ((BasicDBObject) authInfoList.get("0")).get("customerSecret");
		String customerKey = (String) ((BasicDBObject) authInfoList.get("1")).get("customerKey");
		String authorizationSecret = (String) ((BasicDBObject) authInfoList.get("2")).get("authorizationSecret");
		String authorizationKey = (String) ((BasicDBObject) authInfoList.get("3")).get("authorizationKey");
		boolean isIncubated = (boolean) ((BasicDBObject) authInfoList.get("4")).get("isIncubated");

		return new AuthorizationInfo(customerSecret, customerKey, authorizationSecret, authorizationKey, isIncubated);		
	}

	public static synchronized long getCollectionSize(String dbName, String collectionName) throws UnknownHostException {

		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);
		return dbCollection.count();

	}
	
	public static void main(String[] args) {
		System.out.println("test");
	}
}
