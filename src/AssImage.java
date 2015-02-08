import com.mongodb.BasicDBObject;


public class AssImage {

	private String link;
	private String caption;
	private int timesAccessed;
	private String lastAccessDate;
	
	public AssImage(String link, String caption, int timesAccessed, String lastAccessDate) {
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

	public String getLastAccessDate() {
		return lastAccessDate;
	}

	public synchronized void setLastAccessDate(String lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
	
	
	
}
