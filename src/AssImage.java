import java.util.Date;







//TODO make sure setters actually update attribute in database
//call mondb's update function



public class AssImage {

	private String link;
	private String caption;
	private int timesAccessed;
	private Date lastAccessDate;
	
	public AssImage(String link, String caption, int timesAccessed, Date lastAccessDate) {
		this.link = link;
		this.caption = caption;
		this.timesAccessed = timesAccessed;
		this.lastAccessDate = lastAccessDate;
	}

	public String getLink() {
		return link;
	}

	public synchronized void setLink(String link) {
		this.link = link;
	}

	public String getCaption() {
		return caption;
	}

	public synchronized void setCaption(String caption) {
		this.caption = caption;
	}

	public int getTimesAccessed() {
		return timesAccessed;
	}

	public synchronized void setTimesAccessed(int timesAccessed) {
		this.timesAccessed = timesAccessed;
	}

	public Date getLastAccessDate() {
		return lastAccessDate;
	}

<<<<<<< HEAD
	public void setLastAccessDate(Date lastAccessDate) {
=======
	public synchronized void setLastAccessDate(String lastAccessDate) {
>>>>>>> 715f8b43eeec04f9317eb7e3eb9cf66e7b43d24f
		this.lastAccessDate = lastAccessDate;
		
		
		//db.collection.update(this)
	
	
	
	
	}
	
	
	
}
