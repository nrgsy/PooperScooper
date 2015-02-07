import java.net.UnknownHostException;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


public class DataBaseHandler {


	public static void insertImage(
			String dbName,
			String collectionName,
			String link,
			String caption,
			String timesAccessed,
			String lastAccessDate
			) throws UnknownHostException {

		System.out.println("inserting image");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);

		BasicDBList list = new BasicDBList();
		list.add(new BasicDBObject("link", link));
		list.add(new BasicDBObject("caption", caption));

		BasicDBList list2 = new BasicDBList();
		list2.add(new BasicDBObject("timeAccessed", timesAccessed));
		list2.add(new BasicDBObject("lastAccess", lastAccessDate));

		BasicDBObject basicBitch = new BasicDBObject().append("contents", list).append("accessData", list2);

		dbCollection.insert(basicBitch);
	}
	
	//public static String get

	public static void main(String[] args) throws UnknownHostException {

		insertImage("test", "collection1", "www.yyyy.com", "yo mama so fat", "0", "02/07/15");

		System.out.println("querying");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB( "test" );
		DBCollection dbCollection = db.getCollection("collection1");
		DBCursor dbCursor = dbCollection.find();

		while(dbCursor.hasNext()) {

			System.out.println(dbCursor.next());


		}



		//		MongoClient mongoClient = new MongoClient();
		//		mongoClient.dropDatabase("mydb");

		//		MongoClient mongoClient = new MongoClient();
		//		DB db = mongoClient.getDB( "test" );
		//		DBCollection coll = db.getCollection("Channel");
		//		coll.drop();
		//		System.out.println(db.getCollectionNames());



	}

}
