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
		Maintenance.writeLog("New TwitterRunnable created", index);
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
	 * 
	 * @param attemptNumber the number of times it has been called recursively, exits when this exceeds
	 * the uploadPicAttemptLimit in global variables
	 */
	private void uploadPic(int attemptNumber) {
		
		if (attemptNumber > GlobalStuff.UPLOAD_PIC_ATTEMPT_LIMIT) {
			Maintenance.writeLog("Exceeded UPLOAD_PIC_ATTEMPT_LIMIT in TwitterRunnable", index, 1);
			return;
		}

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
						+ " This is not necessarily an error, some possible causes are that the db"
						+ " may have run out of content or the selection could have been unlucky"
						+ " and chosen only content that was posted recently."
						+ " Attempting uploadPic again...", index, 1);
				uploadPic(attemptNumber + 1);
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
					Maintenance.writeLog("Attempting uploadPic again", index, 1);
					uploadPic(attemptNumber + 1);
					return;
				}	
				//creates temp image and puts file location in "image"
				image = new File(location);

				//calls uploadPicTwitter to upload to twitter and deletes locally saved image
				uploadPicTwitter(image, caption);
			}
			else if(caption != null) {
				//TODO new method which posts only caption
			}
			else {
				//both caption and link were null for some reason
				uploadPic(attemptNumber + 1);
			}


		}
		catch (Exception e) {
			Maintenance.writeLog("Temp download of pic failed " + image + "\n" + 
					Maintenance.getStackTrace(e), index, 1);
		}
		finally{
			if (image != null) {
				image.delete();
			}
		}
	}
	
	//default number of attempts is 0
	private void uploadPic() {
		uploadPic(0);
	}	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Maintenance.writeLog("TwitterRunnable run for account " + index, index);
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
			Maintenance.writeLog("Something fucked up in TwitterRunnable\n" +
					Maintenance.getStackTrace(e), index, -1);
		}

		Maintenance.runStatus.put(index+"twitter", false);
	}
}