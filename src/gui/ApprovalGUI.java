package gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import management.DataBaseHandler;
import management.GlobalStuff;

import org.bson.Document;

import twitter4j.TwitterException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

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
	private static MongoCursor<Document> cursor;
	private static MongoClient mongoClient;
	private static Document currentContent;
	private static int numRemaining;
	//the type of content we're dealing with (ass, workout, etc, but not Pending anything)
	private static String kind;
	//maps a link to its caption, link is key, caption is value
	private static HashMap<String, String> approvedContent;
	private static LinkedList<String> schwagLinks;

	//The text fields for the Schwergsy account adder
	private static JTextField nameField;
	private static JTextField cusSecField;
	private static JTextField cusKeyField;
	private static JTextField authSecField;
	private static JTextField authKeyField;
	private static JTextField incubatedField;
	private static JTextField suspendedField;

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

			picPanel.removeAll();
			picPanel.add(getPicLabel());
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
			System.out.println("Cannot load next. No content remaining");
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
			for (Entry<String, String> entry : approvedContent.entrySet()) {
				try {
					DataBaseHandler.removeContent("pending" + kind, entry.getKey());
					DataBaseHandler.newContent(entry.getValue(), entry.getKey(), kind);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			for (String link : schwagLinks) {
				try {
					DataBaseHandler.removeContent("pending" + kind, link);
					DataBaseHandler.newContent(null, link, "schwagass");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}		
	}

	private static class SchwergsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			frame.setVisible(false);
			frame.dispose();

			JPanel mainPanel = new JPanel(new GridLayout(8, 2));
			mainPanel.add(new JLabel("Name"));
			mainPanel.add(nameField);
			mainPanel.add(new JLabel("Customer Secret"));
			mainPanel.add(cusSecField);
			mainPanel.add(new JLabel("Customer Key"));
			mainPanel.add(cusKeyField);
			mainPanel.add(new JLabel("Authorization Secret"));
			mainPanel.add(authSecField);
			mainPanel.add(new JLabel("Authorization Key"));
			mainPanel.add(authKeyField);
			mainPanel.add(new JLabel("Is Incubated? (True/False)"));
			mainPanel.add(incubatedField);
			mainPanel.add(new JLabel("Is Suspended? (True/False)"));
			mainPanel.add(suspendedField);
			JButton addButton = new JButton("Add");
			addButton.addActionListener(new AddAccountListener());
			mainPanel.add(addButton);
			mainPanel.setBackground(Color.GRAY);

			JFrame frame = new JFrame("Enter Schwergsy Account Info");
			frame.setContentPane(mainPanel);

			frame.setSize(400, 500);
			frame.setLocationRelativeTo(null);	
			frame.setVisible(true);
		}		
	}

	//the listener for the add button in Schwergsy account adding interface
	private static class AddAccountListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			String name = nameField.getText();
			String customerSecret = cusSecField.getText();
			String customerKey = cusKeyField.getText();
			String authorizationSecret = authSecField.getText();
			String authorizationKey = authKeyField.getText();
			boolean isIncubated = Boolean.parseBoolean(incubatedField.getText());
			boolean isSuspended = Boolean.parseBoolean(suspendedField.getText());

			System.out.println("yo" + isIncubated + isSuspended);
			
			try {
				DataBaseHandler.insertSchwergsyAccount(name, customerSecret, customerKey,
						authorizationSecret, authorizationKey, isIncubated, isSuspended);
			} catch (UnknownHostException | TwitterException e1) {
				e1.printStackTrace();
			}
		}		
	}

	private static class ContentListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			frame.setVisible(false);
			frame.dispose();

			JComponent panel = new JPanel();
			String[] contentTypes = { "ass", "workout", "weed", "college", "canimals", "space"};
			JComboBox<Object> petList = new JComboBox<Object>(contentTypes);
			petList.setSelectedIndex(0);
			petList.addActionListener(new ListSelectListener());
			panel.add(petList, BorderLayout.PAGE_START);
			panel.setBackground(Color.GRAY);
			panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));    
			panel.setOpaque(true); //content panes must be opaque
			frame = new JFrame("Select Content Type");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setContentPane(panel);
			//Display the window.
			frame.setSize(300, 100);
			frame.setLocationRelativeTo(null);	
			frame.setVisible(true);

		}		
	}

	private static class ListSelectListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			@SuppressWarnings("unchecked")
			JComboBox<Object> cb = (JComboBox<Object>)e.getSource();

			kind = (String)cb.getSelectedItem();

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
				MongoDatabase db = mongoClient.getDatabase("Schwergsy");

				MongoCollection<Document> collection = DataBaseHandler.getCollection("pending" + kind, db);
				try {
					numRemaining = (int) DataBaseHandler.getCollectionSize(
							collection.getNamespace().getCollectionName()) - 1;
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				FindIterable<Document> findIter = collection.find();
				cursor = findIter.iterator();

				if (cursor.hasNext()) {

					currentContent = cursor.next();

					picPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					try {
						picPanel.add(getPicLabel());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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

					frame.setVisible(false);
					frame.dispose();
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

	/**
	 * Get the JLabel contain the image in currentContent
	 * 
	 * @return the JLabel
	 * @throws IOException
	 */
	public static JLabel getPicLabel() throws IOException {

		URL url = new URL(currentContent.get("imglink").toString());			
		BufferedImage bufferedImage = ImageIO.read(url);
		double newHeight = (double) GlobalStuff.MAX_IMAGE_DIMENSION;
		double ratio = newHeight/((double) bufferedImage.getHeight());
		double newWidth = ((double)bufferedImage.getWidth()) * ratio;

		//to guarantee really wide images will be fully contained by the window
		if (newWidth > (double)GlobalStuff.MAX_IMAGE_DIMENSION) {
			newWidth = (double)GlobalStuff.MAX_IMAGE_DIMENSION;
			ratio = newWidth/((double)bufferedImage.getWidth());
			newHeight = ((double)bufferedImage.getWidth()) * ratio;
		}

		Image scaledImage =
				bufferedImage.getScaledInstance(
						(int) newWidth, (int) newHeight, Image.SCALE_SMOOTH);
		ImageIcon image = new ImageIcon(scaledImage);		
		return new JLabel(image, SwingConstants.RIGHT);
	}

	public static void main(String[] args) throws IOException {

		DataBaseHandler.initGlobalVars();
		DataBaseHandler.findAndSetGlobalVars();

		//initialize these
		nameField = new JTextField();
		cusSecField  = new JTextField();
		cusKeyField = new JTextField();
		authSecField = new JTextField();
		authKeyField = new JTextField();
		incubatedField = new JTextField();
		suspendedField = new JTextField();

		//for opening the gui that adds or removes schwergsy accounts from the database
		JButton schwergsButton = new JButton("Add or Remove Schwergsy Accounts");
		schwergsButton.addActionListener(new SchwergsListener());
		//for opening the gui that edits images
		JButton contentButton = new JButton("Review Content");
		contentButton.addActionListener(new ContentListener());	

		JPanel panel = new JPanel(new GridLayout(1, 2));
		panel.add(schwergsButton);
		panel.add(contentButton);
		panel.setBackground(Color.GRAY);

		frame = new JFrame("Main Menu");
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 100);
		frame.setLocationRelativeTo(null);	
		frame.setVisible(true);
	}
}
