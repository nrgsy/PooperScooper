package content;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import management.Maintenance;

/**
 * This class gets an image from the given url.
 * This is needed because ImageIO.read(url) can hang indefinitely with a bad internet connection,
 * so the runnable can be killed after a certain amount of time passes.
 */
public class ImageGetterRunnable implements Runnable {

	private BufferedImage image;
	private URL url;

	/**
	 * @param url The url to get the image from
	 */
	public ImageGetterRunnable(URL url) {
		this.url = url;
	}

	@Override
	public void run() {
		try {
			image = ImageIO.read(url);			
		} catch (IIOException e) {
			Maintenance.writeLog("ImageGetterRunnable could not get image from URL, internet's"
					+ " probably fuckin up", "content", 1);
		} catch (Exception e) {
			Maintenance.writeLog("Something fucked up in the ImageGetterRunnable\n" + 
					Maintenance.getStackTrace(e), "content", -1);
		}
	}

	public BufferedImage getImage() {
		return image;
	}
}
