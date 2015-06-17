package content;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import management.FuckinUpKPException;
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
	public HashMap<String,String> isValid(HashMap<String,String> content) throws FuckinUpKPException{
		Image image = null;
		int count = 0;
		String dir = "pics/";
		File img = null;
		HashMap<String,String> retVal = new HashMap<String,String>();

		try{
			for(Entry<String,String> entry : content.entrySet()){
				String URI = entry.getKey();
				URL url = new URL(URI);
				image = ImageIO.read(url);
				BufferedImage bi = (BufferedImage) image;

				if (!new File(dir).exists()) {
					new File(dir).mkdirs();
				}

				img = new File(dir + count + ".jpg");
				count++;
				ImageIO.write(bi, "jpg", img);
				//Checks to see img size is less than ~3MB
				if(img.length()<3000000){
					Maintenance.writeLog("Image size: "+img.length()+" | Image Link: "+URI+" scooped.", "content");
					retVal.put(entry.getKey(), entry.getValue());
				}
				else{
					Maintenance.writeLog("Image size for " + URI + " is larger than 3MB", "content");
				}
			}
		}
		catch (Exception e) {
			Maintenance.writeLog("***ERROR*** Something fucked up in ImageMainpulator ***ERRROR*** \n"+Maintenance.writeStackTrace(e), "KP");
		}
		finally{
			while(count>=0){
				img = new File(dir + count + ".jpg");
				img.delete();
				--count;
			}
		}
		
		return retVal;
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
	 * Gets image link, saves image, returns image location
	 * 
	 * @param imgsrc
	 * @return
	 * @throws FuckinUpKPException
	 */
	public String getImageFile(String imgsrc) throws FuckinUpKPException{

		Image image = null;

		try {
			URL url = new URL(imgsrc);
			image = ImageIO.read(url);
			BufferedImage bi = (BufferedImage) image;

			//If not jpg, then colorconvert to avoid red tint
			if(!imgsrc.endsWith(".jpg")||!imgsrc.endsWith(".jpeg")){
				bi = convertCMYK2RGB(bi);
			}
			long unique = new Date().getTime();
			//makes file name and saves it, returns file location
			File f = new File("pics/"+unique+".jpg");

			ImageIO.write(bi, "jpg", f);
			imgsrc = "pics/"+unique+".jpg";
			return imgsrc;
			//The receiver must delete the file after posting to Twitter
		}
		catch (IOException e) {
			Maintenance.writeLog("***ERROR*** Something fucked up in ImageMainpulator ***ERRROR*** \n"+Maintenance.writeStackTrace(e), "KP");
			throw new FuckinUpKPException("ERROR: Did not get image file with string"+imgsrc);
		}
	}

}
