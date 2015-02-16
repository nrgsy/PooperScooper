

import com.mongodb.BasicDBList;

public class MongoFuckAround {

	public static void main(String[]args) throws Exception{
		
//		BasicDBList bdbl1 = new BasicDBList();
//		BasicDBList bdbl2 = new BasicDBList();
//		BasicDBList bdbl3 = new BasicDBList();
//		BasicDBList bdbl4 = new BasicDBList();
//		BasicDBList bdbl5 = new BasicDBList();
//
//		bdbl1.add("followerabc");
//		bdbl1.add("followerdef");
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
		
		//DataBaseHandler.addNewStatistic(0, 0, 1);
		//System.out.println(DataBaseHandler.getSchwergsyAccountArraySize(0, "statistics"));
		
		DataBaseHandler.prettyPrintAccount("Thisisthename");

		DataBaseHandler.prettyPrintStatistics("Thisisthename");

		

	}

}
