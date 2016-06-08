package model;

@Id
private long id;
private long BIG_ACCOUNT_TIME;
private String PICS_DIR;
private long MAX_MAINTENANCE_RUN_TIME;
private int FOLLOWING_BASE_CAP;
private int BIG_ACCOUNT_OUTS_FOR_REMOVAL;
private long MIN_POST_TIME_INTERVAL;
private long TWITTER_RUNNABLE_INTERVAL;
private long CONTENT_SAMPLE_SIZE;
private long FOLLOW_TIME_MIN;
private long FOLLOW_TIME_INCUBATED_MIN;
private long TIMER_TASK_FIRE_RATE;
private long FOLLOW_TIME_MAX;
private double ALPHA;
private String LOG_DIRECTORY;
private long RATE_LIMIT_UPDATE_TIME;
private long MIN_NUMBER_OF_BIG_ACCOUNTS;
private long FOLLOW_TIME_INCUBATED_MAX;
private long MAX_IMAGE_FETCH_TIME;
private long UPLOAD_PIC_ATTEMPT_LIMIT;
private long MIN_TIME_BETWEEN_ACCESSES;
private int BIG_ACCOUNT_STRIKES_FOR_OUT;
private long MAINTENANCE_SNOOZE_TIME;
private long MAX_IMAGE_DIMENSION;
private long MAX_NUMBER_HARVEST_ATTEMPTS;

public class GlobalVariables{



	/**
	* Default empty GlobalVariables constructor
	*/
	public GlobalVariables() {
	}

	/**
	* Default GlobalVariables constructor
	*/
	public GlobalVariables(long id, long BIG_ACCOUNT_TIME, String PICS_DIR, long MAX_MAINTENANCE_RUN_TIME,
  int FOLLOWING_BASE_CAP, int BIG_ACCOUNT_OUTS_FOR_REMOVAL, long MIN_POST_TIME_INTERVAL,
  long TWITTER_RUNNABLE_INTERVAL, long CONTENT_SAMPLE_SIZE, long FOLLOW_TIME_MIN,
  long FOLLOW_TIME_INCUBATED_MIN, long TIMER_TASK_FIRE_RATE, long FOLLOW_TIME_MAX,
  double ALPHA, String LOG_DIRECTORY, long RATE_LIMIT_UPDATE_TIME, long MIN_NUMBER_OF_BIG_ACCOUNTS,
  long FOLLOW_TIME_INCUBATED_MAX, long MAX_IMAGE_FETCH_TIME, long UPLOAD_PIC_ATTEMPT_LIMIT,
  long MIN_TIME_BETWEEN_ACCESSES, int BIG_ACCOUNT_STRIKES_FOR_OUT, long MAINTENANCE_SNOOZE_TIME,
  long MAX_IMAGE_DIMENSION, long MAX_NUMBER_HARVEST_ATTEMPTS) {
		this.id = id;
		this.BIG_ACCOUNT_TIME = BIG_ACCOUNT_TIME;
		this.PICS_DIR = PICS_DIR;
		this.MAX_MAINTENANCE_RUN_TIME = MAX_MAINTENANCE_RUN_TIME;
		this.FOLLOWING_BASE_CAP = FOLLOWING_BASE_CAP;
		this.BIG_ACCOUNT_OUTS_FOR_REMOVAL = BIG_ACCOUNT_OUTS_FOR_REMOVAL;
		this.MIN_POST_TIME_INTERVAL = MIN_POST_TIME_INTERVAL;
		this.TWITTER_RUNNABLE_INTERVAL = TWITTER_RUNNABLE_INTERVAL;
		this.CONTENT_SAMPLE_SIZE = CONTENT_SAMPLE_SIZE;
		this.FOLLOW_TIME_MIN = FOLLOW_TIME_MIN;
		this.FOLLOW_TIME_INCUBATED_MIN = FOLLOW_TIME_INCUBATED_MIN;
		this.TIMER_TASK_FIRE_RATE = TIMER_TASK_FIRE_RATE;
		this.FOLLOW_TIME_MAX = FOLLOW_TIME_MAX;
		this.ALPHA = ALPHA;
		this.LOG_DIRECTORY = LOG_DIRECTORY;
		this.RATE_LIMIT_UPDATE_TIME = RATE_LIMIT_UPDATE_TIME;
		this.MIN_NUMBER_OF_BIG_ACCOUNTS = MIN_NUMBER_OF_BIG_ACCOUNTS;
		this.FOLLOW_TIME_INCUBATED_MAX = FOLLOW_TIME_INCUBATED_MAX;
		this.MAX_IMAGE_FETCH_TIME = MAX_IMAGE_FETCH_TIME;
		this.UPLOAD_PIC_ATTEMPT_LIMIT = UPLOAD_PIC_ATTEMPT_LIMIT;
		this.MIN_TIME_BETWEEN_ACCESSES = MIN_TIME_BETWEEN_ACCESSES;
		this.BIG_ACCOUNT_STRIKES_FOR_OUT = BIG_ACCOUNT_STRIKES_FOR_OUT;
		this.MAINTENANCE_SNOOZE_TIME = MAINTENANCE_SNOOZE_TIME;
		this.MAX_IMAGE_DIMENSION = MAX_IMAGE_DIMENSION;
		this.MAX_NUMBER_HARVEST_ATTEMPTS = MAX_NUMBER_HARVEST_ATTEMPTS;
	}

	/**
	* Returns value of id
	* @return
	*/
	public long getId() {
		return id;
	}

	/**
	* Sets new value of id
	* @param
	*/
	public void setId(long id) {
		this.id = id;
	}

	/**
	* Returns value of BIG_ACCOUNT_TIME
	* @return
	*/
	public long getBIG_ACCOUNT_TIME() {
		return BIG_ACCOUNT_TIME;
	}

	/**
	* Sets new value of BIG_ACCOUNT_TIME
	* @param
	*/
	public void setBIG_ACCOUNT_TIME(long BIG_ACCOUNT_TIME) {
		this.BIG_ACCOUNT_TIME = BIG_ACCOUNT_TIME;
	}

	/**
	* Returns value of PICS_DIR
	* @return
	*/
	public String getPICS_DIR() {
		return PICS_DIR;
	}

	/**
	* Sets new value of PICS_DIR
	* @param
	*/
	public void setPICS_DIR(String PICS_DIR) {
		this.PICS_DIR = PICS_DIR;
	}

	/**
	* Returns value of MAX_MAINTENANCE_RUN_TIME
	* @return
	*/
	public long getMAX_MAINTENANCE_RUN_TIME() {
		return MAX_MAINTENANCE_RUN_TIME;
	}

	/**
	* Sets new value of MAX_MAINTENANCE_RUN_TIME
	* @param
	*/
	public void setMAX_MAINTENANCE_RUN_TIME(long MAX_MAINTENANCE_RUN_TIME) {
		this.MAX_MAINTENANCE_RUN_TIME = MAX_MAINTENANCE_RUN_TIME;
	}

	/**
	* Returns value of FOLLOWING_BASE_CAP
	* @return
	*/
	public int getFOLLOWING_BASE_CAP() {
		return FOLLOWING_BASE_CAP;
	}

	/**
	* Sets new value of FOLLOWING_BASE_CAP
	* @param
	*/
	public void setFOLLOWING_BASE_CAP(int FOLLOWING_BASE_CAP) {
		this.FOLLOWING_BASE_CAP = FOLLOWING_BASE_CAP;
	}

	/**
	* Returns value of BIG_ACCOUNT_OUTS_FOR_REMOVAL
	* @return
	*/
	public int getBIG_ACCOUNT_OUTS_FOR_REMOVAL() {
		return BIG_ACCOUNT_OUTS_FOR_REMOVAL;
	}

	/**
	* Sets new value of BIG_ACCOUNT_OUTS_FOR_REMOVAL
	* @param
	*/
	public void setBIG_ACCOUNT_OUTS_FOR_REMOVAL(int BIG_ACCOUNT_OUTS_FOR_REMOVAL) {
		this.BIG_ACCOUNT_OUTS_FOR_REMOVAL = BIG_ACCOUNT_OUTS_FOR_REMOVAL;
	}

	/**
	* Returns value of MIN_POST_TIME_INTERVAL
	* @return
	*/
	public long getMIN_POST_TIME_INTERVAL() {
		return MIN_POST_TIME_INTERVAL;
	}

	/**
	* Sets new value of MIN_POST_TIME_INTERVAL
	* @param
	*/
	public void setMIN_POST_TIME_INTERVAL(long MIN_POST_TIME_INTERVAL) {
		this.MIN_POST_TIME_INTERVAL = MIN_POST_TIME_INTERVAL;
	}

	/**
	* Returns value of TWITTER_RUNNABLE_INTERVAL
	* @return
	*/
	public long getTWITTER_RUNNABLE_INTERVAL() {
		return TWITTER_RUNNABLE_INTERVAL;
	}

	/**
	* Sets new value of TWITTER_RUNNABLE_INTERVAL
	* @param
	*/
	public void setTWITTER_RUNNABLE_INTERVAL(long TWITTER_RUNNABLE_INTERVAL) {
		this.TWITTER_RUNNABLE_INTERVAL = TWITTER_RUNNABLE_INTERVAL;
	}

	/**
	* Returns value of CONTENT_SAMPLE_SIZE
	* @return
	*/
	public long getCONTENT_SAMPLE_SIZE() {
		return CONTENT_SAMPLE_SIZE;
	}

	/**
	* Sets new value of CONTENT_SAMPLE_SIZE
	* @param
	*/
	public void setCONTENT_SAMPLE_SIZE(long CONTENT_SAMPLE_SIZE) {
		this.CONTENT_SAMPLE_SIZE = CONTENT_SAMPLE_SIZE;
	}

	/**
	* Returns value of FOLLOW_TIME_MIN
	* @return
	*/
	public long getFOLLOW_TIME_MIN() {
		return FOLLOW_TIME_MIN;
	}

	/**
	* Sets new value of FOLLOW_TIME_MIN
	* @param
	*/
	public void setFOLLOW_TIME_MIN(long FOLLOW_TIME_MIN) {
		this.FOLLOW_TIME_MIN = FOLLOW_TIME_MIN;
	}

	/**
	* Returns value of FOLLOW_TIME_INCUBATED_MIN
	* @return
	*/
	public long getFOLLOW_TIME_INCUBATED_MIN() {
		return FOLLOW_TIME_INCUBATED_MIN;
	}

	/**
	* Sets new value of FOLLOW_TIME_INCUBATED_MIN
	* @param
	*/
	public void setFOLLOW_TIME_INCUBATED_MIN(long FOLLOW_TIME_INCUBATED_MIN) {
		this.FOLLOW_TIME_INCUBATED_MIN = FOLLOW_TIME_INCUBATED_MIN;
	}

	/**
	* Returns value of TIMER_TASK_FIRE_RATE
	* @return
	*/
	public long getTIMER_TASK_FIRE_RATE() {
		return TIMER_TASK_FIRE_RATE;
	}

	/**
	* Sets new value of TIMER_TASK_FIRE_RATE
	* @param
	*/
	public void setTIMER_TASK_FIRE_RATE(long TIMER_TASK_FIRE_RATE) {
		this.TIMER_TASK_FIRE_RATE = TIMER_TASK_FIRE_RATE;
	}

	/**
	* Returns value of FOLLOW_TIME_MAX
	* @return
	*/
	public long getFOLLOW_TIME_MAX() {
		return FOLLOW_TIME_MAX;
	}

	/**
	* Sets new value of FOLLOW_TIME_MAX
	* @param
	*/
	public void setFOLLOW_TIME_MAX(long FOLLOW_TIME_MAX) {
		this.FOLLOW_TIME_MAX = FOLLOW_TIME_MAX;
	}

	/**
	* Returns value of ALPHA
	* @return
	*/
	public double getALPHA() {
		return ALPHA;
	}

	/**
	* Sets new value of ALPHA
	* @param
	*/
	public void setALPHA(double ALPHA) {
		this.ALPHA = ALPHA;
	}

	/**
	* Returns value of LOG_DIRECTORY
	* @return
	*/
	public String getLOG_DIRECTORY() {
		return LOG_DIRECTORY;
	}

	/**
	* Sets new value of LOG_DIRECTORY
	* @param
	*/
	public void setLOG_DIRECTORY(String LOG_DIRECTORY) {
		this.LOG_DIRECTORY = LOG_DIRECTORY;
	}

	/**
	* Returns value of RATE_LIMIT_UPDATE_TIME
	* @return
	*/
	public long getRATE_LIMIT_UPDATE_TIME() {
		return RATE_LIMIT_UPDATE_TIME;
	}

	/**
	* Sets new value of RATE_LIMIT_UPDATE_TIME
	* @param
	*/
	public void setRATE_LIMIT_UPDATE_TIME(long RATE_LIMIT_UPDATE_TIME) {
		this.RATE_LIMIT_UPDATE_TIME = RATE_LIMIT_UPDATE_TIME;
	}

	/**
	* Returns value of MIN_NUMBER_OF_BIG_ACCOUNTS
	* @return
	*/
	public long getMIN_NUMBER_OF_BIG_ACCOUNTS() {
		return MIN_NUMBER_OF_BIG_ACCOUNTS;
	}

	/**
	* Sets new value of MIN_NUMBER_OF_BIG_ACCOUNTS
	* @param
	*/
	public void setMIN_NUMBER_OF_BIG_ACCOUNTS(long MIN_NUMBER_OF_BIG_ACCOUNTS) {
		this.MIN_NUMBER_OF_BIG_ACCOUNTS = MIN_NUMBER_OF_BIG_ACCOUNTS;
	}

	/**
	* Returns value of FOLLOW_TIME_INCUBATED_MAX
	* @return
	*/
	public long getFOLLOW_TIME_INCUBATED_MAX() {
		return FOLLOW_TIME_INCUBATED_MAX;
	}

	/**
	* Sets new value of FOLLOW_TIME_INCUBATED_MAX
	* @param
	*/
	public void setFOLLOW_TIME_INCUBATED_MAX(long FOLLOW_TIME_INCUBATED_MAX) {
		this.FOLLOW_TIME_INCUBATED_MAX = FOLLOW_TIME_INCUBATED_MAX;
	}

	/**
	* Returns value of MAX_IMAGE_FETCH_TIME
	* @return
	*/
	public long getMAX_IMAGE_FETCH_TIME() {
		return MAX_IMAGE_FETCH_TIME;
	}

	/**
	* Sets new value of MAX_IMAGE_FETCH_TIME
	* @param
	*/
	public void setMAX_IMAGE_FETCH_TIME(long MAX_IMAGE_FETCH_TIME) {
		this.MAX_IMAGE_FETCH_TIME = MAX_IMAGE_FETCH_TIME;
	}

	/**
	* Returns value of UPLOAD_PIC_ATTEMPT_LIMIT
	* @return
	*/
	public long getUPLOAD_PIC_ATTEMPT_LIMIT() {
		return UPLOAD_PIC_ATTEMPT_LIMIT;
	}

	/**
	* Sets new value of UPLOAD_PIC_ATTEMPT_LIMIT
	* @param
	*/
	public void setUPLOAD_PIC_ATTEMPT_LIMIT(long UPLOAD_PIC_ATTEMPT_LIMIT) {
		this.UPLOAD_PIC_ATTEMPT_LIMIT = UPLOAD_PIC_ATTEMPT_LIMIT;
	}

	/**
	* Returns value of MIN_TIME_BETWEEN_ACCESSES
	* @return
	*/
	public long getMIN_TIME_BETWEEN_ACCESSES() {
		return MIN_TIME_BETWEEN_ACCESSES;
	}

	/**
	* Sets new value of MIN_TIME_BETWEEN_ACCESSES
	* @param
	*/
	public void setMIN_TIME_BETWEEN_ACCESSES(long MIN_TIME_BETWEEN_ACCESSES) {
		this.MIN_TIME_BETWEEN_ACCESSES = MIN_TIME_BETWEEN_ACCESSES;
	}

	/**
	* Returns value of BIG_ACCOUNT_STRIKES_FOR_OUT
	* @return
	*/
	public int getBIG_ACCOUNT_STRIKES_FOR_OUT() {
		return BIG_ACCOUNT_STRIKES_FOR_OUT;
	}

	/**
	* Sets new value of BIG_ACCOUNT_STRIKES_FOR_OUT
	* @param
	*/
	public void setBIG_ACCOUNT_STRIKES_FOR_OUT(int BIG_ACCOUNT_STRIKES_FOR_OUT) {
		this.BIG_ACCOUNT_STRIKES_FOR_OUT = BIG_ACCOUNT_STRIKES_FOR_OUT;
	}

	/**
	* Returns value of MAINTENANCE_SNOOZE_TIME
	* @return
	*/
	public long getMAINTENANCE_SNOOZE_TIME() {
		return MAINTENANCE_SNOOZE_TIME;
	}

	/**
	* Sets new value of MAINTENANCE_SNOOZE_TIME
	* @param
	*/
	public void setMAINTENANCE_SNOOZE_TIME(long MAINTENANCE_SNOOZE_TIME) {
		this.MAINTENANCE_SNOOZE_TIME = MAINTENANCE_SNOOZE_TIME;
	}

	/**
	* Returns value of MAX_IMAGE_DIMENSION
	* @return
	*/
	public long getMAX_IMAGE_DIMENSION() {
		return MAX_IMAGE_DIMENSION;
	}

	/**
	* Sets new value of MAX_IMAGE_DIMENSION
	* @param
	*/
	public void setMAX_IMAGE_DIMENSION(long MAX_IMAGE_DIMENSION) {
		this.MAX_IMAGE_DIMENSION = MAX_IMAGE_DIMENSION;
	}

	/**
	* Returns value of MAX_NUMBER_HARVEST_ATTEMPTS
	* @return
	*/
	public long getMAX_NUMBER_HARVEST_ATTEMPTS() {
		return MAX_NUMBER_HARVEST_ATTEMPTS;
	}

	/**
	* Sets new value of MAX_NUMBER_HARVEST_ATTEMPTS
	* @param
	*/
	public void setMAX_NUMBER_HARVEST_ATTEMPTS(long MAX_NUMBER_HARVEST_ATTEMPTS) {
		this.MAX_NUMBER_HARVEST_ATTEMPTS = MAX_NUMBER_HARVEST_ATTEMPTS;
	}
}
