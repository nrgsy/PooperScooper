import java.net.UnknownHostException;
import java.util.Date;

import org.bson.types.BasicBSONList;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class DataBaseHandler {

	public static void insertAuthorizationInfo(
			String dbName,
			String collectionName,
			AuthorizationInfo info) throws UnknownHostException {

		System.out.println("inserting Authorization info");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);

		BasicDBObject basicBitch = new BasicDBObject()
		.append("customerSecret", info.getCustomerSecret())
		.append("customerKey", info.getCustomerKey())
		.append("authorizationSecret", info.getAuthorizationSecret())
		.append("authorizationKey", info.getAuthorizationKey())
		.append("isIncubated", info.isIncubated());

		dbCollection.insert(basicBitch);
	}

	public static void insertImage(
			String dbName,
			String collectionName,
			AssImage image) throws UnknownHostException {

		System.out.println("inserting image");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);

		BasicDBList list = new BasicDBList();
		list.add(new BasicDBObject("link", image.getLink()));
		list.add(new BasicDBObject("caption", image.getCaption()));

		BasicDBList list2 = new BasicDBList();
		list2.add(new BasicDBObject("timesAccessed", image.getTimesAccessed()));
		list2.add(new BasicDBObject("lastAccessDate", image.getLastAccessDate()));

		BasicDBObject basicBitch = new BasicDBObject().append("contents", list).append("accessData", list2);

		dbCollection.insert(basicBitch);
	}

	public static AuthorizationInfo getAuthorizationInfo(String dbName, String collectionName, int index) throws Exception {

		System.out.println("scooping info at index " + index);
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

		DBObject info = dbCursor.next();	

		String customerSecret = (String) info.get("customerSecret");
		String customerKey = (String) info.get("customerKey");
		String authorizationSecret = (String) info.get("authorizationSecret");
		String authorizationKey = (String) info.get("authorizationKey");
		boolean isIncubated = (boolean) info.get("isIncubated");

		return new AuthorizationInfo(customerSecret, customerKey, authorizationSecret, authorizationKey, isIncubated);		
	}

	
	public static AssImage getRandomishAssImage(String dbName, String collectionName) throws UnknownHostException {

		
		
//TODO account for times Accessed and last AccessDate
		
		
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

		BasicBSONList contents = (BasicBSONList) ass.get("contents");
		BasicBSONList accessData = (BasicBSONList) ass.get("accessData");

		String link = (String) ((BasicDBObject) contents.get("0")).get("link");
		String caption = (String) ((BasicDBObject) contents.get("1")).get("caption");
		int timesAccessed = (int) ((BasicDBObject) accessData.get("0")).get("timesAccessed");
		Date lastAccessDate = (Date) ((BasicDBObject) accessData.get("1")).get("lastAccessDate");

		return new AssImage(link, caption, timesAccessed, lastAccessDate);		
	}

	public static void main(String[] args) throws Exception {

		AssImage i = getRandomishAssImage("test", "images");
		System.out.println(i.getCaption());
		System.out.println(i.getLink());
		System.out.println(i.getLastAccessDate());
		System.out.println(i.getTimesAccessed());
		
		System.out.println();
		
		AuthorizationInfo info = getAuthorizationInfo("test", "info", 0);
		System.out.println(info.getAuthorizationKey());
		System.out.println(info.getAuthorizationSecret());
		System.out.println(info.getCustomerKey());
		System.out.println(info.getCustomerSecret());
		System.out.println(info.isIncubated());








		//		MongoClient mongoClient = new MongoClient();
		//		mongoClient.dropDatabase("mydb");

		//		MongoClient mongoClient = new MongoClient();
		//		DB db = mongoClient.getDB( "test" );
		//		DBCollection coll = db.getCollection("Channel");
		//		coll.drop();
		//		System.out.println(db.getCollectionNames());
	}
}
