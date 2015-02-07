package src;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class ImageManipulator {
	
	
	
	
	
	private BufferedImage convertCMYK2RGB(BufferedImage image) throws IOException{
	    //Create a new RGB image
	    BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(),
	    BufferedImage.TYPE_3BYTE_BGR);
	    // then do a funky color convert
	    ColorConvertOp op = new ColorConvertOp(null);
	    op.filter(image, rgbImage);
	    return rgbImage;
	}
	
	
	
	

	public ArrayList<ArrayList<String>> refactoredContent(){
		
		//TODO get last # in content pool 
		Image image = null;
		int inc = 0;
		
		ArrayList<ArrayList<String>> poopscoop = new RedditScraper().contentSnatch();
		for(String imgsrc : poopscoop.get(1)){
		try {
		    URL url = new URL(imgsrc);
		    //URL url = new URL("i.imgur.com/Z9f6L5n.jpg");

		    image = ImageIO.read(url);
		    BufferedImage bi = (BufferedImage) image;
		    
		    if(!imgsrc.endsWith(".jpg")||!imgsrc.endsWith(".jpeg")){
		    	bi = convertCMYK2RGB(bi);
		    }
		    
		    File f = new File("pics/"+inc+".jpg");
		    ImageIO.write(bi, "jpg", f);
		    imgsrc = "pics/"+inc+".jpg";
		    inc++;
		    
		}
		catch (IOException e) {
			System.out.println("fuckin up kp");
		}
		}
		return poopscoop;
		
		
	}
	
	
	public static void main(String[] args){
		for(String imgsrc : new ImageManipulator().refactoredContent().get(1)){
			System.out.println(imgsrc);
		}
	}
	
}
