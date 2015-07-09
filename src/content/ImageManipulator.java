package content;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import management.FuckinUpKPException;
import management.GlobalStuff;
import management.Maintenance;


/**
 * @author Bojangles and McChrpchrp
 *
 */
public class ImageManipulator {

	/**
	 * @param URI
	 * @return
	 * @throws FuckinUpKPException
	 */
	public HashMap<String,String> validateContent(HashMap<String,String> content) {
		Image image = null;
		int count = 0;
		String dir = GlobalStuff.PICS_DIR;
		File img = null;
		HashMap<String,String> retVal = new HashMap<String,String>();

		try {
			for(Entry<String,String> entry : content.entrySet()) {

				String URI = entry.getKey();
				URL url = new URL(URI);
				try {
					
					image = getImageFromURL(url);

					BufferedImage bi = (BufferedImage) image;

					if (!new File(dir).exists()) {
						new File(dir).mkdirs();
					}

					img = new File(dir + count + ".jpg");
					ImageIO.write(bi, "jpg", img);
					count++;
					//Checks to see img size is less than ~3MB
					if(img.length() < 3000000) {
						Maintenance.writeLog("Image size: "+img.length()+" | Image Link: "+URI+" scooped.", "content");
						retVal.put(entry.getKey(), entry.getValue());
					}
					else {
						Maintenance.writeLog("Image size for " + URI + " is larger than 3MB", "content");
					}

				}
				catch (Exception e) {
					Maintenance.writeLog("Skipped a url when validating content", "content");
					continue;
				}
			}
		}
		catch (Exception e) {
			Maintenance.writeLog("Something fucked up in ImageMainpulator\n" + 
					Maintenance.getStackTrace(e), "content", -1);
		}
		finally{
			Maintenance.deleteResidualPics();
		}
		return retVal;
	}

	/**
	 * call this instead of ImageIO.read(url); because ImageIO.read can hang when internet
	 * fucks up, so we need it to run on a separate thread and give up (return null)
	 * if it's taking too long
	 * 
	 * @param url
	 * @return
	 */
	public static BufferedImage getImageFromURL(final URL url) {

		//create a runnable that attempts to get the image from the url
		ImageGetterRunnable imageGetterRunnable = new ImageGetterRunnable(url);

		Thread imageGetterThread = new Thread(imageGetterRunnable);
		long endTimeMillis = System.currentTimeMillis() + GlobalStuff.MAX_IMAGE_FETCH_TIME;
		imageGetterThread.start();

		//monitor the theread and shut it down if it takes too long trying to get the image from the url
		while (imageGetterThread.isAlive()) {
			if (System.currentTimeMillis() > endTimeMillis) {
				Maintenance.writeLog("Could not get Image for from URL. imageGetterThread was alive "
						+ "for too long. Interrupting imageGetterThread.", "content", 1);
				imageGetterThread.interrupt();
				return null;
			}
			//so that we're not checking constantly, only ten times a second
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Maintenance.writeLog("Sleep interrupted for some reason", "maintenance", -1);
			}
		}
		return imageGetterRunnable.getImage();
	}

	//Converts weird-ass png to plain-ass jpg
	private BufferedImage convertCMYK2RGB(BufferedImage image) throws IOException{
		//Create a new RGB image
		BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_3BYTE_BGR);
		// then do a funky color convert
		ColorConvertOp op = new ColorConvertOp(null);
		op.filter(image, rgbImage);
		return rgbImage;
	}

	/**
	 * Gets image link, saves image, returns image location OR NULL IF THE URL IS BAD/UNABLE TO DOWNLOAD
	 * 
	 * @param imgsrc
	 * @return
	 * @throws FuckinUpKPException
	 */
	public String getImageFile(String imgsrc) throws FuckinUpKPException{

		Image image = null;

		try {
			URL url = new URL(imgsrc);
			image = getImageFromURL(url);
			BufferedImage bi = (BufferedImage) image;

			//If not jpg, then colorconvert to avoid red tint
			if(!imgsrc.endsWith(".jpg")||!imgsrc.endsWith(".jpeg")){
				bi = convertCMYK2RGB(bi);
			}
			long unique = new Date().getTime();
			//makes file name and saves it, returns file location
			File f = new File(GlobalStuff.PICS_DIR + unique + ".jpg");

			ImageIO.write(bi, "jpg", f);
			imgsrc = GlobalStuff.PICS_DIR + unique + ".jpg";
			return imgsrc;
			//The receiver must delete the file after posting to Twitter
		}
		catch (IIOException e) {
			Maintenance.writeLog("There was a problem with the interwebs or URL was bad when "
					+ "trying to fetch an image.\nIt's probably whatever, "
					+ "but here's the error anyway" + e.toString(), "content", 1);
			return null;
		}
		catch (IOException e) {
			Maintenance.writeLog("Something fucked up in ImageMainpulator\n" + 
					Maintenance.getStackTrace(e), "content", -1);
			throw new FuckinUpKPException("Did not get image file with string" + imgsrc);
		}
	}

}
