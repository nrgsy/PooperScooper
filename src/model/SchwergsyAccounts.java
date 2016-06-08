package model;

import java.util.ArrayList;

@Id
private int id;
private String name;
private String customerSecret;
private String customerKey;
private String authorizationSecret;
private String authorizationKey;
private String accountType;
private double assRatio;
private boolean isIncubated;
private boolean isSuspended;
private boolean isFlaggedForDeletion;
private ArrayList<Long> followers;
private ArrayList<Long> following;
private ArrayList<Long> toFollow;
private ArrayList<Long> whiteList;
private ArrayList<Long> bigAccounts;
private ArrayList<statistic> statistics;
private int bigAccountHarvestIndex;
private ArrayList<Long> bigAccountWhiteList;

public class SchwergsyAccount{

	/**
	* Default empty SchwergsyAccount constructor
	*/
	public SchwergsyAccount() {
	}

	/**
	* Default SchwergsyAccount constructor
	*/
	public SchwergsyAccount(int id, String name, String customerSecret, String customerKey, String authorizationSecret,
  String authorizationKey, String accountType, double assRatio, boolean isIncubated, boolean isSuspended,
  boolean isFlaggedForDeletion, ArrayList<Long> followers, ArrayList<Long> following, ArrayList<Long> toFollow,
  ArrayList<Long> whiteList, ArrayList<Long> bigAccounts, ArrayList<statistic> statistics, int bigAccountHarvestIndex,
  ArrayList<Long> bigAccountWhiteList) {
		this.id = id;
		this.name = name;
		this.customerSecret = customerSecret;
		this.customerKey = customerKey;
		this.authorizationSecret = authorizationSecret;
		this.authorizationKey = authorizationKey;
		this.accountType = accountType;
		this.assRatio = assRatio;
		this.isIncubated = isIncubated;
		this.isSuspended = isSuspended;
		this.isFlaggedForDeletion = isFlaggedForDeletion;
		this.followers = followers;
		this.following = following;
		this.toFollow = toFollow;
		this.whiteList = whiteList;
		this.bigAccounts = bigAccounts;
		this.statistics = statistics;
		this.bigAccountHarvestIndex = bigAccountHarvestIndex;
		this.bigAccountWhiteList = bigAccountWhiteList;
	}

	/**
	* Returns value of id
	* @return
	*/
	public int getId() {
		return id;
	}

	/**
	* Sets new value of id
	* @param
	*/
	public void setId(int id) {
		this.id = id;
	}

	/**
	* Returns value of name
	* @return
	*/
	public String getName() {
		return name;
	}

	/**
	* Sets new value of name
	* @param
	*/
	public void setName(String name) {
		this.name = name;
	}

	/**
	* Returns value of customerSecret
	* @return
	*/
	public String getCustomerSecret() {
		return customerSecret;
	}

	/**
	* Sets new value of customerSecret
	* @param
	*/
	public void setCustomerSecret(String customerSecret) {
		this.customerSecret = customerSecret;
	}

	/**
	* Returns value of customerKey
	* @return
	*/
	public String getCustomerKey() {
		return customerKey;
	}

	/**
	* Sets new value of customerKey
	* @param
	*/
	public void setCustomerKey(String customerKey) {
		this.customerKey = customerKey;
	}

	/**
	* Returns value of authorizationSecret
	* @return
	*/
	public String getAuthorizationSecret() {
		return authorizationSecret;
	}

	/**
	* Sets new value of authorizationSecret
	* @param
	*/
	public void setAuthorizationSecret(String authorizationSecret) {
		this.authorizationSecret = authorizationSecret;
	}

	/**
	* Returns value of authorizationKey
	* @return
	*/
	public String getAuthorizationKey() {
		return authorizationKey;
	}

	/**
	* Sets new value of authorizationKey
	* @param
	*/
	public void setAuthorizationKey(String authorizationKey) {
		this.authorizationKey = authorizationKey;
	}

	/**
	* Returns value of accountType
	* @return
	*/
	public String getAccountType() {
		return accountType;
	}

	/**
	* Sets new value of accountType
	* @param
	*/
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	* Returns value of assRatio
	* @return
	*/
	public double getAssRatio() {
		return assRatio;
	}

	/**
	* Sets new value of assRatio
	* @param
	*/
	public void setAssRatio(double assRatio) {
		this.assRatio = assRatio;
	}

	/**
	* Returns value of isIncubated
	* @return
	*/
	public boolean isIsIncubated() {
		return isIncubated;
	}

	/**
	* Sets new value of isIncubated
	* @param
	*/
	public void setIsIncubated(boolean isIncubated) {
		this.isIncubated = isIncubated;
	}

	/**
	* Returns value of isSuspended
	* @return
	*/
	public boolean isIsSuspended() {
		return isSuspended;
	}

	/**
	* Sets new value of isSuspended
	* @param
	*/
	public void setIsSuspended(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}

	/**
	* Returns value of isFlaggedForDeletion
	* @return
	*/
	public boolean isIsFlaggedForDeletion() {
		return isFlaggedForDeletion;
	}

	/**
	* Sets new value of isFlaggedForDeletion
	* @param
	*/
	public void setIsFlaggedForDeletion(boolean isFlaggedForDeletion) {
		this.isFlaggedForDeletion = isFlaggedForDeletion;
	}

	/**
	* Returns value of followers
	* @return
	*/
	public ArrayList<Long> getFollowers() {
		return followers;
	}

	/**
	* Sets new value of followers
	* @param
	*/
	public void setFollowers(ArrayList<Long> followers) {
		this.followers = followers;
	}

	/**
	* Returns value of following
	* @return
	*/
	public ArrayList<Long> getFollowing() {
		return following;
	}

	/**
	* Sets new value of following
	* @param
	*/
	public void setFollowing(ArrayList<Long> following) {
		this.following = following;
	}

	/**
	* Returns value of toFollow
	* @return
	*/
	public ArrayList<Long> getToFollow() {
		return toFollow;
	}

	/**
	* Sets new value of toFollow
	* @param
	*/
	public void setToFollow(ArrayList<Long> toFollow) {
		this.toFollow = toFollow;
	}

	/**
	* Returns value of whiteList
	* @return
	*/
	public ArrayList<Long> getWhiteList() {
		return whiteList;
	}

	/**
	* Sets new value of whiteList
	* @param
	*/
	public void setWhiteList(ArrayList<Long> whiteList) {
		this.whiteList = whiteList;
	}

	/**
	* Returns value of bigAccounts
	* @return
	*/
	public ArrayList<Long> getBigAccounts() {
		return bigAccounts;
	}

	/**
	* Sets new value of bigAccounts
	* @param
	*/
	public void setBigAccounts(ArrayList<Long> bigAccounts) {
		this.bigAccounts = bigAccounts;
	}

	/**
	* Returns value of statistics
	* @return
	*/
	public ArrayList<statistic> getStatistics() {
		return statistics;
	}

	/**
	* Sets new value of statistics
	* @param
	*/
	public void setStatistics(ArrayList<statistic> statistics) {
		this.statistics = statistics;
	}

	/**
	* Returns value of bigAccountHarvestIndex
	* @return
	*/
	public int getBigAccountHarvestIndex() {
		return bigAccountHarvestIndex;
	}

	/**
	* Sets new value of bigAccountHarvestIndex
	* @param
	*/
	public void setBigAccountHarvestIndex(int bigAccountHarvestIndex) {
		this.bigAccountHarvestIndex = bigAccountHarvestIndex;
	}

	/**
	* Returns value of bigAccountWhiteList
	* @return
	*/
	public ArrayList<Long> getBigAccountWhiteList() {
		return bigAccountWhiteList;
	}

	/**
	* Sets new value of bigAccountWhiteList
	* @param
	*/
	public void setBigAccountWhiteList(ArrayList<Long> bigAccountWhiteList) {
		this.bigAccountWhiteList = bigAccountWhiteList;
	}
}
