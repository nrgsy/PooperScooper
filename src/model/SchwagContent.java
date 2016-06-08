package model;

@Id
private long id;
private String imglink;
private String type;

public class SchwagContent{


	/**
	* Default empty SchwagContent constructor
	*/
	public SchwagContent() {
	}

	/**
	* Default SchwagContent constructor
	*/
	public SchwagContent(long id, String imglink, String type) {
		this.id = id;
		this.imglink = imglink;
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
