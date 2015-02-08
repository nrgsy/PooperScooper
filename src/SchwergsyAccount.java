import com.mongodb.BasicDBList;





//TODO make sure setters actually update attribute in database
//db.collection.update(this)

//TODO implement the necessary add/remove functions for the account arrays


public class SchwergsyAccount {
	
	private String accountID;
	
	private String name;
	
	private AuthorizationInfo authorizationInfo;
	
	private BasicDBList followers;
	
	private BasicDBList following;
	
	private BasicDBList toFollow;

	private BasicDBList whiteList;
	
	private BasicDBList bigAccounts;
	
	public SchwergsyAccount(
			String accountID,
			String name,
			AuthorizationInfo authorizationInfo,
			BasicDBList followers,
			BasicDBList following,
			BasicDBList toFollow,
			BasicDBList whiteList,
			BasicDBList bigAccounts) {
		super();
		this.accountID = accountID;
		this.name = name;
		this.authorizationInfo = authorizationInfo;
		this.followers = followers;
		this.following = following;
		this.toFollow = toFollow;
		this.whiteList = whiteList;
		this.bigAccounts = bigAccounts;
	}
	
	public void setIsIncubated() {
//update in the database
	}

	public String getAccountID() {
		return accountID;
	}

	public String getName() {
		return name;
	}

	public AuthorizationInfo getAuthorizationInfo() {
		return authorizationInfo;
	}

	public BasicDBList getFollowers() {
		return followers;
	}

	public BasicDBList getFollowing() {
		return following;
	}

	public BasicDBList getToFollow() {
		return toFollow;
	}

	public BasicDBList getWhiteList() {
		return whiteList;
	}

	public BasicDBList getBigAccounts() {
		return bigAccounts;
	}


}
