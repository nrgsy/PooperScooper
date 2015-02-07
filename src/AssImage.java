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

	public void setLink(String link) {
		this.link = link;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int getTimesAccessed() {
		return timesAccessed;
	}

	public void setTimesAccessed(int timesAccessed) {
		this.timesAccessed = timesAccessed;
	}

	public String getLastAccessDate() {
		return lastAccessDate;
	}

	public void setLastAccessDate(String lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
	
	
	
}
