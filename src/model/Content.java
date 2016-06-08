package model;

import java.util.ArrayList;

@Id
private long id;
private String imglink;
private String caption;
private boolean isTimeless;
private ArrayList<accessInfo> accessInfo;
private String type;

public class Content{

	/**
	* Default empty Content constructor
	*/
	public Content() {
	}

	/**
	* Default Content constructor
	*/
	public Content(long id, String imglink, String caption, boolean isTimeless, ArrayList<accessInfo> accessInfo, String type) {
		this.id = id;
		this.imglink = imglink;
		this.caption = caption;
		this.isTimeless = isTimeless;
		this.accessInfo = accessInfo;
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
	* Returns value of isTimeless
	* @return
	*/
	public boolean isIsTimeless() {
		return isTimeless;
	}

	/**
	* Sets new value of isTimeless
	* @param
	*/
	public void setIsTimeless(boolean isTimeless) {
		this.isTimeless = isTimeless;
	}

	/**
	* Returns value of accessInfo
	* @return
	*/
	public ArrayList<accessInfo> getAccessInfo() {
		return accessInfo;
	}

	/**
	* Sets new value of accessInfo
	* @param
	*/
	public void setAccessInfo(ArrayList<accessInfo> accessInfo) {
		this.accessInfo = accessInfo;
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
