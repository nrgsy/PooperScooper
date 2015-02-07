package src;

import java.net.UnknownHostException;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


public class DataBaseHandler {
	
	
	public void insert() throws UnknownHostException {
		
		System.out.println("inserting");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB( "test" );
		DBCollection dbCollection = db.getCollection("collection1");
		
		
		BasicDBList list = new BasicDBList();
		list.add(new BasicDBObject("link", "www.yyyy.com"));
		list.add(new BasicDBObject("caption", "yo mama so fat"));
		
		BasicDBList list2 = new BasicDBList();
		list2.add(new BasicDBObject("timeAccessed", 0));
		list2.add(new BasicDBObject("lastAccess", "02/07/15"));
		
		BasicDBObject basicBitch = new BasicDBObject().append("contents", list).append("accessData", list2);
		
		dbCollection.insert(basicBitch);
		
		
	}

	public static void main(String[] args) throws UnknownHostException {

		boolean insert = false;

		if (insert) {
			
			
		}
		else {
			System.out.println("querying");
			MongoClient mongoClient = new MongoClient();
			DB db = mongoClient.getDB( "test" );
			DBCollection dbCollection = db.getCollection("collection1");
			DBCursor dbCursor = dbCollection.find();

			while(dbCursor.hasNext()) {

				System.out.println(dbCursor.next());

			}
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
