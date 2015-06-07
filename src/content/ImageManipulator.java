package content;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;
import javax.imageio.ImageIO;
import management.FuckinUpKPException;
import management.Maintenance;


/**
 * @author Bojangles and McChrpchrp
 *
 */
public class ImageManipulator {

	File img = null;
	long inc = 0;


	/**
	 * @param URI
	 * @return
	 * @throws FuckinUpKPException
	 */
	public boolean isValid(String URI) throws FuckinUpKPException{
		Image image = null;
		
		try{
			URL url = new URL(URI);
			image = ImageIO.read(url);
			BufferedImage bi = (BufferedImage) image;
			img = new File("pics/"+inc+".jpg");
			inc++;
			ImageIO.write(bi, "jpg", img);
			//Checks to see img size is less than ~3MB
			if(img.length()<3000000){
				Maintenance.writeLog("image is less than 3MB #" + inc, "content");
				return true;
			}
			
		}
		catch (SocketTimeoutException e){
			return false;
		}
		catch(IOException e) {
			return false;
		}
		finally{
			img.delete();
		}
		Maintenance.writeLog("Image size of " + URI + " is larger than 3MB", "content");
		return false;
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
				throw new FuckinUpKPException("ERROR: Did not get image file with string"+imgsrc);
			}
	}

}
