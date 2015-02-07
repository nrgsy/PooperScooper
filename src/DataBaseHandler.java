import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


public class DataBaseHandler {

	public static void main(String[] args) throws UnknownHostException {

		boolean insert = true;

		if (insert) {
			System.out.println("inserting");
			MongoClient mongoClient = new MongoClient();
			DB db = mongoClient.getDB( "mydb" );
			DBCollection dbCollection = db.getCollection("Channel");
			BasicDBObject basicBitch = new BasicDBObject();
			basicBitch.put("name", "Phil Kangss");
			basicBitch.put("triflin yo", 1);
			dbCollection.insert(basicBitch);
		}
		else {
			System.out.println("querying");
			MongoClient mongoClient = new MongoClient();
			DB db = mongoClient.getDB( "mydb" );
			DBCollection dbCollection = db.getCollection("Channel");
			BasicDBObject basicBitch = new BasicDBObject();
			basicBitch.put("name", "TJ");
			DBCursor dbCursor = dbCollection.find(basicBitch);

			while(dbCursor.hasNext()) {

				System.out.println(dbCursor.next());

			}
		}



	}

}
