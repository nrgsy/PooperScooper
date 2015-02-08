import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;


public class ImageManipulator {

	File img = null;
	int inc = 0;


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
				System.out.println("image is less than 3MB #"+inc);
				return true;
			}
			
		}
		catch(IOException e) {
			System.out.println("fuckin up kp");
			throw new FuckinUpKPException();
		}
		finally{
			img.delete();
		}
		System.out.println("Image size of "+URI+"is larger than 3MB");
		return false;
	}


	private BufferedImage convertCMYK2RGB(BufferedImage image) throws IOException{
		//Create a new RGB image
		BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_3BYTE_BGR);
		// then do a funky color convert
		ColorConvertOp op = new ColorConvertOp(null);
		op.filter(image, rgbImage);
		return rgbImage;
	}


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
				Random rand = new Random();
			    int randomNum = rand.nextInt(5001);
				File f = new File("pics/"+randomNum+".jpg");
				ImageIO.write(bi, "jpg", f);
				imgsrc = "pics/"+randomNum+".jpg";
				return imgsrc;
				//The receiver must delete the file after posting to Twitter
			}
			catch (IOException e) {
				System.out.println("Error: Did not get image file with string"+imgsrc);
				throw new FuckinUpKPException();
			}
	}

}
