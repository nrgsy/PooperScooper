package model;

@Id
private long id;
private String imglink;
private String caption;
private String type;

public class PendingContent{


	/**
	* Default empty PendingContent constructor
	*/
	public PendingContent() {
	}

	/**
	* Default PendingContent constructor
	*/
	public PendingContent(long id, String imglink, String caption, String type) {
		this.id = id;
		this.imglink = imglink;
		this.caption = caption;
		this.type = type;
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
	* Returns value of imglink
	* @return
	*/
	public String getImglink() {
		return imglink;
	}

	/**
	* Sets new value of imglink
	* @param
	*/
	public void setImglink(String imglink) {
		this.imglink = imglink;
	}

	/**
	* Returns value of caption
	* @return
	*/
	public String getCaption() {
		return caption;
	}

	/**
	* Sets new value of caption
	* @param
	*/
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	* Returns value of type
	* @return
	*/
	public String getType() {
		return type;
	}

	/**
	* Sets new value of type
	* @param
	*/
	public void setType(String type) {
		this.type = type;
	}
}
