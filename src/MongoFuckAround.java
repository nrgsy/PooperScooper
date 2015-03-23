

import java.io.PrintWriter;
import java.util.Date;

import com.mongodb.BasicDBList;

public class MongoFuckAround {

	public static void main(String[]args) throws Exception{

//		BasicDBList bdbl1 = new BasicDBList();
//		BasicDBList bdbl2 = new BasicDBList();
//		BasicDBList bdbl3 = new BasicDBList();
//		BasicDBList bdbl4 = new BasicDBList();
//		BasicDBList bdbl5 = new BasicDBList();
//
//		//		bdbl1.add(1L);
//		//		bdbl1.add(2L);
//		//		bdbl1.add(3L);
//
//		bdbl2.add("following1");
//		bdbl2.add("following2");
//		bdbl3.add("toFollow1");
//		bdbl3.add("toFollow2");
//		bdbl4.add("whitelist1");
//		bdbl4.add("whitelist2");
//		bdbl5.add("bigAccount1");
//		bdbl5.add("bigAccount2");
//
//		DataBaseHandler.insertSchwergsyAccount("thisIsTheID", "Thisisthename", "cussssh", "cuskey", "authshhh", "authkey", true, bdbl1, bdbl2, bdbl3, bdbl4, bdbl5, new BasicDBList());



		//		Long[] followers = {-1L, 0L, 1L, 2L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 13L, 14L};
		//
		//		DataBaseHandler.updateFollowers(0,followers);

		//DataBaseHandler.prettyPrintAccount("Thisisthename");
		DataBaseHandler.prettyPrintStatistics("Thisisthename");

		//System.out.println(DataBaseHandler.getFollowersSize(0));

		//DataBaseHandler.newContent("dummy caption", "ww.yoo.xom", "ass");


	}

}
