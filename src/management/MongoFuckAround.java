package management;



import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class MongoFuckAround {

	public static void main(String[]args) throws Exception{

/*		BasicDBList bdbl1 = new BasicDBList();
		BasicDBList bdbl2 = new BasicDBList();
		BasicDBList bdbl3 = new BasicDBList();
		BasicDBList bdbl4 = new BasicDBList();
		BasicDBList bdbl5 = new BasicDBList();

		bdbl2.add("following1");
		bdbl2.add("following2");
		bdbl3.add("toFollow1");
		bdbl3.add("toFollow2");
		bdbl4.add("whitelist1");
		bdbl4.add("whitelist2");

		DataBaseHandler.insertSchwergsyAccount("thisIsTheID", "Thisisthename", "cussssh", "cuskey", "authshhh", "authkey", true, bdbl1, bdbl2, bdbl3, bdbl4, bdbl5, new BasicDBList());

*/
		
		

		DataBaseHandler.newContent("dude in skirt", "http://i.imgur.com/vx3glwO.jpg", "pendingass");
		DataBaseHandler.newContent("girl in a bed", "http://i.imgur.com/x0ZSk6c.jpg", "pendingass");
		DataBaseHandler.newContent("archer", "http://i.imgur.com/Rsed2ln.jpg", "pendingass");
		DataBaseHandler.newContent("lawn", "http://i.imgur.com/gg0gBej.jpg", "pendingass");

		
	}
}
