import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageHandler {
	
	
	
	
	
	public static void main(String[] args) throws IOException {
		

		
		
		System.out.println("yoou");
		
		Image image = null;
		try {
		    URL url = new URL("http://i.imgur.com/Z9f6L5n.jpg");
		    //URL url = new URL("i.imgur.com/Z9f6L5n.jpg");

		    image = ImageIO.read(url);
		    
		    BufferedImage bi = (BufferedImage) image;
		    File f = new File("output.png");
		    ImageIO.write(bi, "png", f);
		    
		    
		    
		    
		}
		catch (IOException e) {
			System.out.println("fuckin up kp");
		}
		
		
		
	}
	
	
}
