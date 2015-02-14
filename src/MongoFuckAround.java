import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
public class MongoFuckAround {
	
	public static void main(String[]args) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("foo");
		DBCollection dbCollection = db.getCollection("foo");
//		BasicDBObject test_insert = new BasicDBObject("_id", new Date().getTime());
//		test_insert.append("caption", "ayy lmao");
//		test_insert.append("imglink", "https://scontent-iad.xx.fbcdn.net/hphotos-xpf1/v/t1.0-9/11001780_"
//				+ "10205739676432251_7394124516083810628_n.jpg?oh=9000ef02d11bdb416802c9cc0e86f7bf&oe=554DE18C");
//		int count = 0;
//		test_insert.append("times_accessed",count);
//		test_insert.append("last_accessed", new Date().getTime());
//		
//		dbCollection.insert(test_insert);
		
		String[] stringarr = new String[]{"Happy", "Birthday", "To","You","Donkeybrains","Happy","Dicknuts"};
		List<Object> StringList = new ArrayList<Object>() ;
		BasicDBObject query = new BasicDBObject("_id",1);
		BasicDBList list = new BasicDBList();
		StringList.add("Happy");
		StringList.add("You");
		StringList.add("Dicknuts");
		
		BasicDBObject slice = new BasicDBObject("$pull",
				new BasicDBObject("test_Arr", StringList));
		
		System.out.println(dbCollection.update(query, slice));
		
	}

}
