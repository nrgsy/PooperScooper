
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
		
		
//		MongoClient mongoClient = new MongoClient();
//		DB db = mongoClient.getDB("Schwergsy");
//		DBCollection dbCollection = db.getCollection("SchwergsyAccounts");

		
		//System.out.println(DataBaseHandler.getAuthorizationInfo(0));


		

	}

}
