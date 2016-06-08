package model;

@Id
private long creationDate;
private long timeSinceLastState;
private int unFollows;
private int newFollows;
private int retainedFollowers;
private int totalFollowers;

public class statistic{


	/**
	* Default empty statistic constructor
	*/
	public statistic() {
	}

	/**
	* Default statistic constructor
	*/
	public statistic(long creationDate, long timeSinceLastState, int unFollows, int newFollows,
  int retainedFollowers, int totalFollowers) {
		this.creationDate = creationDate;
		this.timeSinceLastState = timeSinceLastState;
		this.unFollows = unFollows;
		this.newFollows = newFollows;
		this.retainedFollowers = retainedFollowers;
		this.totalFollowers = totalFollowers;
	}

	/**
	* Returns value of creationDate
	* @return
	*/
	public long getCreationDate() {
		return creationDate;
	}

	/**
	* Sets new value of creationDate
	* @param
	*/
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	/**
	* Returns value of timeSinceLastState
	* @return
	*/
	public long getTimeSinceLastState() {
		return timeSinceLastState;
	}

	/**
	* Sets new value of timeSinceLastState
	* @param
	*/
	public void setTimeSinceLastState(long timeSinceLastState) {
		this.timeSinceLastState = timeSinceLastState;
	}

	/**
	* Returns value of unFollows
	* @return
	*/
	public int getUnFollows() {
		return unFollows;
	}

	/**
	* Sets new value of unFollows
	* @param
	*/
	public void setUnFollows(int unFollows) {
		this.unFollows = unFollows;
	}

	/**
	* Returns value of newFollows
	* @return
	*/
	public int getNewFollows() {
		return newFollows;
	}

	/**
	* Sets new value of newFollows
	* @param
	*/
	public void setNewFollows(int newFollows) {
		this.newFollows = newFollows;
	}

	/**
	* Returns value of retainedFollowers
	* @return
	*/
	public int getRetainedFollowers() {
		return retainedFollowers;
	}

	/**
	* Sets new value of retainedFollowers
	* @param
	*/
	public void setRetainedFollowers(int retainedFollowers) {
		this.retainedFollowers = retainedFollowers;
	}

	/**
	* Returns value of totalFollowers
	* @return
	*/
	public int getTotalFollowers() {
		return totalFollowers;
	}

	/**
	* Sets new value of totalFollowers
	* @param
	*/
	public void setTotalFollowers(int totalFollowers) {
		this.totalFollowers = totalFollowers;
	}
}
