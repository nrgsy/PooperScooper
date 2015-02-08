import java.net.UnknownHostException;
import java.util.List;

import org.bson.types.BasicBSONList;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class DataBaseHandler {


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
	
	public static AssImage getRandomAssImage(String dbName, String collectionName) throws UnknownHostException {
		
		System.out.println("scooping ass image");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection("collection1");

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
		String lastAccessDate = (String) ((BasicDBObject) accessData.get("1")).get("lastAccessDate");

		return new AssImage(link, caption, timesAccessed, lastAccessDate);		
	}

	public static void main(String[] args) throws UnknownHostException {

		
		//insertImage("test", "collection1", new AssImage("smokeWeed.jpg", "everyday", 0, "02/07/15"));
		
		AssImage i = getRandomAssImage("test", "collection1");
		System.out.println(i.getCaption());
		System.out.println(i.getLink());
		System.out.println(i.getLastAccessDate());
		System.out.println(i.getTimesAccessed());
		
		
		

		//		MongoClient mongoClient = new MongoClient();
		//		mongoClient.dropDatabase("mydb");

		//		MongoClient mongoClient = new MongoClient();
		//		DB db = mongoClient.getDB( "test" );
		//		DBCollection coll = db.getCollection("Channel");
		//		coll.drop();
		//		System.out.println(db.getCollectionNames());
	}
}
