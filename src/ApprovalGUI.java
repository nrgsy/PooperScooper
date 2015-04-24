import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

//NOTICE: for this to work, you must pass in the content type (ass, workout, weed, etc)
public class ApprovalGUI {

	private static Boolean lastWasApproved;
	private static boolean undoClicked;
	private static String lastApprovedLink;
	private static JFrame frame;
	private static JPanel topPanel;
	private static JPanel labelPanel;
	private static JPanel buttonPanel;
	private static JTextField captionTextField;
	private static JPanel picPanel;
	private static DBCursor cursor;
	private static MongoClient mongoClient;
	private static DBObject currentContent;
	private static int numRemaining;
	//the type of content we're dealing with (ass, workout, etc, but not Pending anything)
	private static String kind;
	//maps a link to its caption, link is key, caption is value
	private static HashMap<String, String> approvedContent;
	private static LinkedList<String> schwagLinks;

	public static void loadNext() throws IOException {

		if (cursor.hasNext()) {
			undoClicked = false;
			currentContent = cursor.next();

			captionTextField.setText(currentContent.get("caption").toString());
			numRemaining--;
			String labelText = "number of pending " + kind +
					" images remaining: " + numRemaining;
			JLabel numRemainingLabel = new JLabel(labelText, SwingConstants.CENTER);
			labelPanel.removeAll();
			labelPanel.add(numRemainingLabel);
			labelPanel.setBackground(Color.GRAY);

			URL url = new URL(currentContent.get("imglink").toString());			
			BufferedImage bufferedImage = ImageIO.read(url);
			double newHeight = 700;
			double ratio = newHeight/bufferedImage.getHeight();
			double newWidth = bufferedImage.getWidth() * ratio;				
			Image scaledImage =
					bufferedImage.getScaledInstance(
							(int) newWidth, (int) newHeight, Image.SCALE_SMOOTH);
			ImageIcon image = new ImageIcon(scaledImage);		
			JLabel picLabel = new JLabel(image, SwingConstants.RIGHT);
			picPanel.removeAll();
			picPanel.add(picLabel);
			picPanel.setBackground(Color.GRAY);

			topPanel.removeAll();
			topPanel.add(labelPanel);
			topPanel.add(buttonPanel);
			topPanel.add(captionTextField);
			topPanel.add(picPanel);
			topPanel.setPreferredSize(new Dimension(333, 780));
			topPanel.setBackground(Color.GRAY);

			topPanel.revalidate();
			topPanel.repaint();
			frame.repaint();		
		}
		else {
			System.out.println("No content remaining");
		}
	}

	private static class AddListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String link = currentContent.get("imglink").toString();
			approvedContent.put(link, captionTextField.getText());
			lastWasApproved = true;
			lastApprovedLink = link;
			try {
				ApprovalGUI.loadNext();
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}		
	}

	private static class TrashListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {		
			schwagLinks.add(currentContent.get("imglink").toString());
			lastWasApproved = false;
			try {
				ApprovalGUI.loadNext();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}		
	}

	private static class UndoListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (lastWasApproved != null && !undoClicked) {
				if (lastWasApproved) {
					approvedContent.remove(lastApprovedLink);
				}
				else {
					schwagLinks.removeLast();
				}
				undoClicked = true;
				System.out.println("Undo completed");
			}
		}
	}

	private static class DoneListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			approvedContent.entrySet();
			for (Entry<String, String> entry : approvedContent.entrySet()) {
				try {
					DataBaseHandler.newContent(entry.getValue(), entry.getKey(), kind);
					DataBaseHandler.removeContent("pending" + kind, entry.getKey());
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
			}
			for (String link : schwagLinks) {
				try {
					DataBaseHandler.newContent(null, link, "schwagass");
					DataBaseHandler.removeContent("pending" + kind, link);
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
			}
		}		
	}

	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			System.err.println("must pass in the type of images as an argument");
		}

		kind = args[0];

		if (!kind.equals("ass") &&
				!kind.equals("workout") &&
				!kind.equals("weed") &&
				!kind.equals("college") &&
				!kind.equals("canimals") &&
				!kind.equals("space")) {
			System.err.println("invalid argument, must be ass, workout, etc");
		}
		else {
			lastWasApproved = null;
			undoClicked = false;
			approvedContent = new HashMap<>();
			schwagLinks = new LinkedList<>();

			mongoClient = new MongoClient();
			DB db = mongoClient.getDB("Schwergsy");

			DBCollection collection = DataBaseHandler.getCollection("pending" + kind, db);
			numRemaining = (int) DataBaseHandler.getCollectionSize(collection.getName()) - 1;

			cursor = collection.find();

			if (cursor.hasNext()) {

				currentContent = cursor.next();

				URL url = new URL(currentContent.get("imglink").toString());			
				BufferedImage bufferedImage = ImageIO.read(url);
				double newHeight = 700;
				double ratio = newHeight/bufferedImage.getHeight();
				double newWidth = bufferedImage.getWidth() * ratio;				
				Image scaledImage =
						bufferedImage.getScaledInstance(
								(int) newWidth, (int) newHeight, Image.SCALE_SMOOTH);
				ImageIcon image = new ImageIcon(scaledImage);		
				JLabel picLabel = new JLabel(image, SwingConstants.RIGHT);
				picPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				picPanel.add(picLabel);
				picPanel.setBackground(Color.GRAY);

				String labelText = "number of pending " + kind +
						" images remaining: " + numRemaining;			
				JLabel numRemainingLabel = new JLabel(labelText, SwingConstants.CENTER);
				labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				labelPanel.add(numRemainingLabel);
				labelPanel.setBackground(Color.GRAY);

				Font font = new Font("SansSerif", Font.BOLD, 25);
				captionTextField = new JTextField();
				captionTextField.setFont(font);
				captionTextField.setText(currentContent.get("caption").toString());
				captionTextField.setPreferredSize(new Dimension(333,30));

				JButton addButton = new JButton("Add");
				addButton.addActionListener(new AddListener());	
				JButton trashButton = new JButton("Trash");
				trashButton.addActionListener(new TrashListener());	
				JButton undoButton = new JButton("Undo");
				undoButton.addActionListener(new UndoListener());	
				JButton doneButton = new JButton("Done");
				doneButton.addActionListener(new DoneListener());		

				buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				buttonPanel.add(addButton);
				buttonPanel.add(trashButton);
				buttonPanel.add(undoButton);
				buttonPanel.add(doneButton);
				buttonPanel.setBackground(Color.GRAY);

				topPanel = new JPanel();
				topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
				topPanel.setAlignmentX(Container.LEFT_ALIGNMENT);
				topPanel.add(labelPanel);
				topPanel.add(buttonPanel);
				topPanel.add(captionTextField);
				topPanel.add(picPanel);
				topPanel.setPreferredSize(new Dimension(333, 780));
				topPanel.setBackground(Color.GRAY);

				JPanel bottomPanel = new JPanel();
				bottomPanel.setBackground(Color.GRAY);

				JPanel containerPanel = new JPanel();
				containerPanel.setLayout(new BorderLayout());	
				containerPanel.add(topPanel, BorderLayout.NORTH);
				containerPanel.add(bottomPanel, BorderLayout.CENTER);

				JScrollPane scrPane = new JScrollPane(containerPanel);	

				frame = new JFrame("Content Reviewer");			
				//So that these things close when we end the program
				frame.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						cursor.close();
						mongoClient.close();			        
					}
				});			
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(scrPane);
				frame.pack();
				frame.setMinimumSize(new Dimension(300, 300));
				frame.setSize(800, 900);
				frame.setLocationRelativeTo(null);	
				frame.setVisible(true);	
			}
			else {
				System.out.println("No content found in pending" + kind);
			}
		}
	}
}
