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
		
		
		System.out.println(DataBaseHandler.getOneToFollow(0));

	}
}
