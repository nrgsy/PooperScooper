package twitterRunnables;


import java.io.File;
import java.util.Date;

import org.bson.Document;

import management.DataBaseHandler;
import management.GlobalStuff;
import management.Maintenance;
import management.TwitterHandler;

import content.ImageManipulator;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * @author Bojangles and McChrpchrp
 *
 */
public class TwitterRunnable implements Runnable {
	private Twitter bird = null;
	private int index;

	/**
	 * @param OAuthConsumerKey
	 * @param OAuthConsumerSecret
	 * @param OAuthAccessToken
	 * @param OAuthAccessTokenSecret
	 */
	public TwitterRunnable (Twitter twitter, int index){
		super();
		Maintenance.writeLog("New TwitterRunnable created");
		this.index = index;
		bird = twitter;
		Maintenance.runStatus.put(index+"twitter", true);
	}

	/**
	 * handles actual uploading to twitter
	 * 
	 * @param file
	 * @param message
	 * @param twitter
	 * @throws TwitterException 
	 * @throws Exception
	 */
	public void uploadPicTwitter(File file, String message) throws TwitterException{
		StatusUpdate status = new StatusUpdate(message);
		status.setMedia(file);
		TwitterHandler.updateStatus(bird, status, index);
	}

	/**
	 * 	handles downloading image, updating db, and deleting image after upload
	 */
	public void uploadPic(){

		Maintenance.writeLog("uploading pic for account with index: " + index, index);

		ImageManipulator imgman = new ImageManipulator();
		File image = null;
		try {
			//TODO content structure may have been changed since writing this method.
			String contentType;
			if(Math.random()<=DataBaseHandler.getAssRatio(index)){
				contentType = "ass";
			}
			else{
				contentType = DataBaseHandler.getAccountType(index);
			}
			Document content = DataBaseHandler.getRandomContent(contentType, index);
			if(content == null) {
				Maintenance.writeLog("Tried to post, but could not pull content from db."
						+ "This is not necessarily an error", index);
				return;
			}	

			String caption = content.getString("caption");
			String link = content.getString("imglink");
			
			if(link!=null){
				if(caption==null){
					caption = "";
				}
				String location = imgman.getImageFile(link);
				
				if (location == null) {
					Maintenance.writeLog("Tried to upload but link was bad, calling "
							+ "uploadPic again", index);
					uploadPic();
					return;
				}	
				//creates temp image and puts file location in "image"
				image = new File(location);

				//calls uploadPicTwitter to upload to twitter and deletes locally saved image
				uploadPicTwitter(image, caption);
			}
			else if(caption!=null){
				//TODO new method which posts only caption
			}
			else{
				//both caption and link were null for some reason
				uploadPic();
			}

			
		}
		catch (Exception e) {
			Maintenance.writeLog("Temp download of pic failed " + image + "\n" +Maintenance.writeStackTrace(e), index);
			e.printStackTrace();
		}
		finally{
			if (image != null) {
				image.delete();
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Maintenance.writeLog("run method called for TwitterRunnable");
		try{
			long now = new Date().getTime();
			Long lastPostTime = GlobalStuff.lastPostTimeMap.get(index);
			boolean canPost = true;

			if (lastPostTime != null && now - lastPostTime < GlobalStuff.MIN_POST_TIME_INTERVAL) {
				canPost = false;
			} 

			//post if the random number is less than the alpha constant and we're allowed to post
			if (canPost == true && Math.random() < GlobalStuff.ALPHA) {
				uploadPic();
				GlobalStuff.lastPostTimeMap.put(index, now);
			}
		}
		catch(Exception e){
			Maintenance.writeLog("Something fucked up in TwitterRunnable\n"+Maintenance.writeStackTrace(e), index);
			Maintenance.writeLog("Something fucked up in TwitterRunnable\n"+Maintenance.writeStackTrace(e), "KP");
		}

		Maintenance.runStatus.put(index+"twitter", false);
	}
}