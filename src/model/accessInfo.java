package model;

private long index;
private long timesAccessed;
@Id
private long lastAccess;

public class accessInfo{


	/**
	* Default empty accessInfo constructor
	*/
	public accessInfo() {
	}

	/**
	* Default accessInfo constructor
	*/
	public accessInfo(long index, long timesAccessed, long lastAccess) {
		this.index = index;
		this.timesAccessed = timesAccessed;
		this.lastAccess = lastAccess;
	}

	/**
	* Returns value of index
	* @return
	*/
	public long getIndex() {
		return index;
	}

	/**
	* Sets new value of index
	* @param
	*/
	public void setIndex(long index) {
		this.index = index;
	}

	/**
	* Returns value of timesAccessed
	* @return
	*/
	public long getTimesAccessed() {
		return timesAccessed;
	}

	/**
	* Sets new value of timesAccessed
	* @param
	*/
	public void setTimesAccessed(long timesAccessed) {
		this.timesAccessed = timesAccessed;
	}

	/**
	* Returns value of lastAccess
	* @return
	*/
	public long getLastAccess() {
		return lastAccess;
	}

	/**
	* Sets new value of lastAccess
	* @param
	*/
	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}
}
