<<<<<<< HEAD
=======
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
>>>>>>> 186ecbb597b1608fdbf70570b6b75b65e938ce1c

import com.mongodb.BasicDBList;

public class MongoFuckAround {

	public static void main(String[]args) throws Exception{
		
		BasicDBList bdbl1 = new BasicDBList();
		BasicDBList bdbl2 = new BasicDBList();
		BasicDBList bdbl3 = new BasicDBList();
		BasicDBList bdbl4 = new BasicDBList();
		BasicDBList bdbl5 = new BasicDBList();

		bdbl1.add("followerabc");
		bdbl1.add("followerdef");
		bdbl2.add("following1");
		bdbl2.add("following2");
		bdbl3.add("toFollow1");
		bdbl3.add("toFollow2");
		bdbl4.add("whitelist1");
		bdbl4.add("whitelist2");
		bdbl5.add("bigAccount1");
		bdbl5.add("bigAccount2");
		
		
<<<<<<< HEAD
//		MongoClient mongoClient = new MongoClient();
//		DB db = mongoClient.getDB("Schwergsy");
//		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");

=======
		
		
		new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				MongoClient mongoClient;
				try {
					mongoClient = new MongoClient();
				
				DB db = mongoClient.getDB("foo");
				DBCollection dbCollection = db.getCollection("foo");
				BasicDBObject query = new BasicDBObject("_id", 1);
				BasicDBObject ele = new BasicDBObject("$addToSet", new BasicDBObject("test_arr", new Date()));
				dbCollection.update(query, ele);
				//mongoClient.close();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}},0L, 1100);
		
		new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				MongoClient mongoClient;
				try {
					mongoClient = new MongoClient();
				
				DB db = mongoClient.getDB("foo");
				DBCollection dbCollection = db.getCollection("foo");
				BasicDBObject query = new BasicDBObject("_id", 1);
				BasicDBObject ele = new BasicDBObject("$addToSet", new BasicDBObject("test_arr", new Date().getTime()));
				dbCollection.update(query, ele);
				//mongoClient.close();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}},0L, 1000);
		
		new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				MongoClient mongoClient;
				try {
					mongoClient = new MongoClient();
				
				DB db = mongoClient.getDB("foo");
				DBCollection dbCollection = db.getCollection("foo");
				BasicDBObject query = new BasicDBObject("_id", 1);
				BasicDBObject ele = new BasicDBObject("$addToSet", new BasicDBObject("test_arr", new Date().getTime()));
				dbCollection.update(query, ele);
				//mongoClient.close();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}},0L, 900);
		
		new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				MongoClient mongoClient;
				try {
					mongoClient = new MongoClient();
				
				DB db = mongoClient.getDB("foo");
				DBCollection dbCollection = db.getCollection("foo");
				BasicDBObject query = new BasicDBObject("_id", 1);
				BasicDBObject ele = new BasicDBObject("$addToSet", new BasicDBObject("test_arr", new Date().getTime()));
				dbCollection.update(query, ele);
				//mongoClient.close();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}},0L, 800);
		
		new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				MongoClient mongoClient;
				try {
					mongoClient = new MongoClient();
				
				DB db = mongoClient.getDB("foo");
				DBCollection dbCollection = db.getCollection("foo");
				BasicDBObject query = new BasicDBObject("_id", 1);
				BasicDBObject ele = new BasicDBObject("$addToSet", new BasicDBObject("test_arr", new Date().getTime()));
				dbCollection.update(query, ele);
				//mongoClient.close();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}},0L, 700);
>>>>>>> 186ecbb597b1608fdbf70570b6b75b65e938ce1c
		
		//System.out.println(DataBaseHandler.getAuthorizationInfo(0));


		

	}

}
