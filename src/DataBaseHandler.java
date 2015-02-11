import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DataBaseHandler {

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

	public static synchronized void insertImage(
			String dbName,
			String collectionName,
			AssImage image) throws UnknownHostException {

		//check whether the image is already in the database (compare by link)

		System.out.println("inserting image");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);

		BasicDBList contents = new BasicDBList();
		contents.add(new BasicDBObject("link", image.getLink()));
		contents.add(new BasicDBObject("caption", image.getCaption()));

		BasicDBList accessData = new BasicDBList();
		accessData.add(new BasicDBObject("timesAccessed", image.getTimesAccessed()));
		accessData.add(new BasicDBObject("lastAccessDate", image.getLastAccessDate()));

		BasicDBObject basicBitch = new BasicDBObject().append("contents", contents).append("accessData", accessData);

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

	public static synchronized long getListSize(String dbName, String collectionName, int index, String listName) throws UnknownHostException, FuckinUpKPException {
		return getList(dbName, collectionName, index, listName).size();
	}

	public static synchronized AssImage getRandomishAssImage(String dbName, String collectionName) throws UnknownHostException {

		System.out.println("scooping ass image");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);

		int randIndex = (int) (Math.random() * dbCollection.getCount());

		DBCursor dbCursor = dbCollection.find();

		for (int i = 0; i < randIndex; i++) {
			dbCursor.next();
		}

		DBObject ass = dbCursor.next();

		//TODO check that this shouldn't actaully be a BasicBSONList instead
		BasicDBList contents = (BasicDBList) ass.get("contents");
		BasicDBList accessData = (BasicDBList) ass.get("accessData");

		String link = (String) ((BasicDBObject) contents.get("0")).get("link");
		String caption = (String) ((BasicDBObject) contents.get("1")).get("caption");
		int timesAccessed = (int) ((BasicDBObject) accessData.get("0")).get("timesAccessed");
		Date lastAccessDate = (Date) ((BasicDBObject) accessData.get("1")).get("lastAccessDate");

		return new AssImage(link, caption, timesAccessed, lastAccessDate);		
	}


	public static synchronized BasicDBList getList(String dbName, String collectionName, int index, String listName) throws UnknownHostException, FuckinUpKPException {

		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);

		DBCursor dbCursor = dbCollection.find();

		for (int i = 0; i < index; i++) {			
			if (dbCursor.hasNext())
				dbCursor.next();
			else {			
				System.out.println("that ass passed in an invalid index");
				throw new FuckinUpKPException();
			}
		}

		DBObject schwergsyAccount = dbCursor.next();	

		BasicDBList list = (BasicDBList) schwergsyAccount.get(listName);

		return list;
	}

	public static synchronized void addToList(String dbName, String collectionName, int index, String listName, String userID) throws UnknownHostException, FuckinUpKPException {

		BasicDBList list = getList(dbName, collectionName, index, listName);	
		list.add(userID);

		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);

		DBCursor dbCursor = dbCollection.find();

		for (int i = 0; i < index; i++) {			
			if (dbCursor.hasNext())
				dbCursor.next();
			else {			
				System.out.println("that ass passed in an invalid index");
				throw new FuckinUpKPException();
			}
		}

		DBObject schwergsyAccount = dbCursor.next();
		//schwergsyAccount.get("_id")
		
		BasicDBObject newDocument = (BasicDBObject) schwergsyAccount;
		
		//newDocument.
		
		//System.out.println(newDocument);
		
//		newDocument.put(listName, list);
//
//		BasicDBObject searchQuery = new BasicDBObject().append("_id", schwergsyAccount.get("_id"));
//
//		dbCollection.update(searchQuery, newDocument);



		//dbCollection.update(q, o)
		//
		//
		//
		//		BasicDBObject query = new BasicDBObject("_id", id);
		//		query.append(new BasicDBObject("stats.employee", "rob"));
		//
		//		BasicDBObject update = new BasicDBObject("$set",
		//				new BasicDBObject("stats.$.stat2", value));
		//
		//		dbCollection.update(query,update);


	}




	public static void main(String[] args) throws Exception {		
		
//		MongoClient mongoClient = new MongoClient();
//		DB db = mongoClient.getDB("test");
//		DBCollection dbCollection = db.getCollection("collection1");
	
		
//		BasicDBObject basicBitch = new BasicDBObject()
//		.append("a", 1)
//		.append("b", new BasicDBObject("c", 3).append("d", 4));
//
//		
//		
//		System.out.println(basicBitch);
//		
//		System.out.println(new BasicDBObject("$gt", 10));
//
//		System.out.println(new BasicDBObject("address.city", "London"));
		
		
		
		

//				BasicDBList bdbl1 = new BasicDBList();
//				BasicDBList bdbl2 = new BasicDBList();
//				BasicDBList bdbl3 = new BasicDBList();
//				BasicDBList bdbl4 = new BasicDBList();
//				BasicDBList bdbl5 = new BasicDBList();
//		
//				bdbl1.add("followerabc");
//				bdbl1.add("followerdef");
//				bdbl2.add("following1");
//				bdbl2.add("following2");
//				bdbl3.add("toFollow1");
//				bdbl3.add("toFollow2");
//				bdbl4.add("whitelist1");
//				bdbl4.add("whitelist2");
//				bdbl5.add("bigAccount1");
//				bdbl5.add("bigAccount2");
//		
//		
//				insertSchwergsyAccount("test",
//						"schwergsAccounts",
//						new SchwergsyAccount(
//								"abc",
//								"this is an account name",
//								new AuthorizationInfo("customerSHHH",
//										"cuskey",
//										"authSHH",
//										"authkey",
//										true),
//										bdbl1,
//										bdbl2,
//										bdbl3,
//										bdbl4,
//										bdbl5)
//						);





		//addToList("test", "schwergsAccounts", 0, "followers", "newFollower");




		//insertImage("test", "assImages", new AssImage("www.assWebsite.com", "this is a caption", 0, new Date(0)));


		//		AssImage i = getRandomishAssImage("test", "images");
		//		System.out.println(i.getCaption());
		//		System.out.println(i.getLink());
		//		System.out.println(i.getLastAccessDate());
		//		System.out.println(i.getTimesAccessed());
		//		
		//		System.out.println();
		//		









		//		MongoClient mongoClient = new MongoClient();
		//		mongoClient.dropDatabase("mydb");

		//		MongoClient mongoClient = new MongoClient();
		//		DB db = mongoClient.getDB( "test" );
		//		DBCollection coll = db.getCollection("Channel");
		//		coll.drop();
		//		System.out.println(db.getCollectionNames());
	}
}
