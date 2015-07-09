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
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.crypto.Data;

import management.Director;
import management.DataBaseHandler;
import management.FuckinUpKPException;
import management.GlobalStuff;
import management.Maintenance;
import management.TimerFactory;
import management.Maintenance;

import org.bson.Document;

import twitter4j.TwitterException;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import content.ContentDirectory;
import content.ImageManipulator;

public class ApprovalGUI {

	private static Boolean lastWasApproved;
	private static boolean undoClicked;
	private static String lastApprovedLink;
	private static JFrame frame;
	private static JButton statsButton;
	private static JButton backToMainButton;
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
	private static String type;
	//maps a link to its caption, link is key, caption is value
	private static HashMap<String, String> approvedNormalContent;
	private static HashMap<String, String> approvedTimeLessContent;
	private static LinkedList<String> schwagLinks;

	//The text fields for the Schwergsy account adder
	private static JTextField nameField;
	private static JTextField cusSecField;
	private static JTextField cusKeyField;
	private static JTextField authSecField;
	private static JTextField authKeyField;
	private static JTextField accountTypeField;
	private static JTextField assRatioField;
	private static JTextField incubatedField;
	private static JTextField suspendedField;
	private static JTextField seedField;
	//the thing that has a run method which runs when clicking the x button in the gui
	private static WindowAdapter guiExitAdapter;

	public static void loadNext() throws IOException {

		if (cursor.hasNext()) {
			undoClicked = false;
			currentContent = cursor.next();

			captionTextField.setText(currentContent.get("caption").toString());
			numRemaining--;
			String labelText = "number of pending " + type +
					" images remaining: " + numRemaining;
			JLabel numRemainingLabel = new JLabel(labelText, SwingConstants.CENTER);
			labelPanel.removeAll();
			labelPanel.add(numRemainingLabel);
			labelPanel.setBackground(Color.GRAY);

			picPanel.removeAll();
			picPanel.add(getNextPicLabel());
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
		} else {
			Maintenance.writeLog("Cannot load next. No content remaining", "gui");
		}
	}

	private static class AddNormalContentListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			addContent(approvedNormalContent);
		}
	}

	private static class AddTimelessContentListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			addContent(approvedTimeLessContent);
		}
	}

	/**
	 * adds the current content to the given map
	 * 
	 * @param approvedTimeLessContent the hashmap to add it to
	 */
	private static void addContent(HashMap<String, String> map) {
		String link = currentContent.get("imglink").toString();
		map.put(link, captionTextField.getText());
		lastWasApproved = true;
		lastApprovedLink = link;
		try {
			ApprovalGUI.loadNext();
		} catch (IOException e1) {
			Maintenance.writeLog("addContent fucked up" + Maintenance.getStackTrace(e1),
					"gui", -1);
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
				Maintenance.writeLog("trash button fucked up" + Maintenance.getStackTrace(e1),
						"gui", -1);
			}
		}
	}

	private static class UndoListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (lastWasApproved != null && !undoClicked) {
				if (lastWasApproved) {
					//try to remove it from both because we're not sure which it was in
					approvedNormalContent.remove(lastApprovedLink);
					approvedTimeLessContent.remove(lastApprovedLink);
				} else {
					schwagLinks.removeLast();
				}
				undoClicked = true;
				Maintenance.writeLog("Undo completed", "gui");
			}
		}
	}

	private static class DoneListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			for (Entry<String, String> entry : approvedNormalContent.entrySet()) {
				DataBaseHandler.removeContent("pending" + type, entry.getKey());
				DataBaseHandler.newContent(entry.getValue(), entry.getKey(), type, false);	
			}
			for (Entry<String, String> entry : approvedTimeLessContent.entrySet()) {
				DataBaseHandler.removeContent("pending" + type, entry.getKey());
				DataBaseHandler.newContent(entry.getValue(), entry.getKey(), type, true);
			}
			for (String link : schwagLinks) {
				DataBaseHandler.removeContent("pending" + type, link);
				DataBaseHandler.newContent(null, link, "schwag" + type, null);
			}
			//reset the content reviewer section of the gui
			buildContentReviewer(type);
		}
	}

	/**
	 * Creates the interface for 
	 *
	 */
	private static class SchwergsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			frame.setVisible(false);
			frame.dispose();

			JPanel mainPanel = new JPanel(new GridLayout(13, 2));
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
			mainPanel.add(new JLabel("Account Type"));
			mainPanel.add(accountTypeField);
			mainPanel.add(new JLabel("Ass Ratio"));
			mainPanel.add(assRatioField);
			mainPanel.add(new JLabel("Is Incubated? (true/false)"));
			mainPanel.add(incubatedField);
			mainPanel.add(new JLabel("Is Suspended? (true/false)"));
			mainPanel.add(suspendedField);
			mainPanel.add(new JLabel("Seed (big account id)"));
			mainPanel.add(seedField);
			JButton addButton = new JButton("Add Account");
			addButton.addActionListener(new AddAccountListener());
			mainPanel.add(addButton);
			JButton removeButton = new JButton("Remove (Only fill in Name)");
			removeButton.addActionListener(new RemoveAccountListener());
			mainPanel.add(removeButton);
			JButton replaceButton = new JButton("Replace (Fill in name and info)");
			replaceButton.addActionListener(new ReplaceInfoListener());
			mainPanel.add(replaceButton);
			JButton addSeedButton = new JButton("Add Seed (Fill in name and seed)");
			addSeedButton.addActionListener(new AddSeedListener());
			mainPanel.add(addSeedButton);
			mainPanel.add(backToMainButton);
			mainPanel.setBackground(Color.GRAY);

			frame = new JFrame("Enter Schwergsy Account Info");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.addWindowListener(guiExitAdapter);
			frame.setContentPane(mainPanel);
			frame.setSize(600, 500);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	}

	//the listener for the add button in Schwergsy account interface
	private static class AddAccountListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			String name = nameField.getText();
			String customerSecret = cusSecField.getText();
			String customerKey = cusKeyField.getText();
			String authorizationSecret = authSecField.getText();
			String authorizationKey = authKeyField.getText();
			String accountType = accountTypeField.getText();
			double assRatio = Double.parseDouble(assRatioField.getText());
			boolean isIncubated;
			boolean isSuspended;
			try {
				isSuspended = getAccountBoolean("isSuspended");
				isIncubated = getAccountBoolean("isIncubated");
			} catch (FuckinUpKPException e2) {
				Maintenance.writeLog("add account button fucked up" + 
						Maintenance.getStackTrace(e2), "gui", -1);
				return;
			}

			try {
				//insertSchwergsyAccount returns a boolean indicating success. Exits if insertion
				//failed so the timers aren't created below
				//isFlaggedForDeletion is set to false by default
				if (DataBaseHandler.insertSchwergsyAccount(name, customerSecret, customerKey,
						authorizationSecret, authorizationKey, accountType, assRatio, isIncubated,
						isSuspended, false)
						== false) {
					return;
				}
			} catch (TwitterException e1) {
				Maintenance.writeLog("add account button fucked up" + 
						Maintenance.getStackTrace(e1), "gui", -1);
				return;
			}

			//SchwergsyAccount is already added at this point, so you must - 1 to get index
			TimerFactory.scheduleTimers(
					(int) DataBaseHandler.getCollectionSize("SchwergsyAccounts") - 1);

			//adds seed to Schwergsy account if one was entered
			addSeedToSchwergsyAccount();
		}
	}


	//the listener for the remove button in Schwergsy account interface
	private static class RemoveAccountListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String name = nameField.getText();
			DataBaseHandler.flagAccountForRemoval(DataBaseHandler.getSchwergsyAccountIndex(name));
			Maintenance.writeLog("Flagging account with name: " + name + " for removal", "gui");
		}
	}

	//the listener for the replace button in Schwergsy account interface
	private static class ReplaceInfoListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			String name = nameField.getText();
			int index = DataBaseHandler.getSchwergsyAccountIndex(name);
			String customerSecret = cusSecField.getText();
			String customerKey = cusKeyField.getText();
			String authorizationSecret = authSecField.getText();
			String authorizationKey = authKeyField.getText();
			String accountType = accountTypeField.getText();
			double assRatio = Double.parseDouble(assRatioField.getText());
			String incubatedString = incubatedField.getText();
			String suspendedString = suspendedField.getText();			

			try {

				if (!customerSecret.equals("")) {
					DataBaseHandler.replaceSchwergsyField(index, customerSecret, "customerSecret");
				}
				if (!customerKey.equals("")) {
					DataBaseHandler.replaceSchwergsyField(index, customerKey, "customerKey");
				}
				if (!authorizationSecret.equals("")) {
					DataBaseHandler.replaceSchwergsyField(index, authorizationSecret,
							"authorizationSecret");
				}
				if (!authorizationKey.equals("")) {
					DataBaseHandler.replaceSchwergsyField(index, authorizationKey, "authorizationKey");
				}
				if (!authorizationKey.equals("")) {
					DataBaseHandler.replaceSchwergsyField(index, accountType, "accountType");
				}
				if (!authorizationKey.equals("")) {
					DataBaseHandler.replaceSchwergsyField(index, assRatio, "assRatio");
				}
				if (!incubatedString.equals("")) {
					DataBaseHandler.replaceSchwergsyField(index, getAccountBoolean("isIncubated"),
							"isIncubated");
				}
				if (!suspendedString.equals("")) {
					DataBaseHandler.replaceSchwergsyField(index, getAccountBoolean("isSuspended"),
							"isSuspended");
				}
			} catch (FuckinUpKPException e2) {
				Maintenance.writeLog("replace button fucked up" + 
						Maintenance.getStackTrace(e2), "gui", -1);
				return;
			}
		}
	}

	/**
	 * attempts to get a boolean from the Schwergsy Account interface in the gui
	 * 
	 * @param fieldType the boolean to get, so "isIncubated or isSuspended"
	 * @return
	 * @throws FuckinUpKPException 
	 */
	public static boolean getAccountBoolean (String fieldType) throws FuckinUpKPException {

		JTextField textField;
		boolean parsedBoolean;

		if (fieldType.equals("isIncubated")) {
			textField = incubatedField;
		}
		else if (fieldType.equals("isSuspended")) {
			textField = suspendedField;
		}
		else {
			Maintenance.writeLog("bad field string passed in", "gui", -1);
			throw new FuckinUpKPException("");
		}

		if (textField.getText().toLowerCase().equals("true")) {
			parsedBoolean = true;
		} else if (textField.getText().toLowerCase().equals("false")) {
			parsedBoolean = false;
		} else {
			Maintenance.writeLog("Cannot parse boolean text field", "gui", -1);
			throw new FuckinUpKPException("");
		}	

		return parsedBoolean;
	}

	//The listener for the add seed button in the Schwergsy account section of the GUI
	private static class AddSeedListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			addSeedToSchwergsyAccount();
		}
	}

	/**
	 * This looks at the seed and name text fields and adds the seed if anything was entered.
	 * Does nothing if seed field is empty
	 * 
	 */
	private static void addSeedToSchwergsyAccount() {

		String name = nameField.getText();
		String seedText = seedField.getText();
		//add the seed if one was entered
		if (!seedText.equals("")) {
			long seed = Long.parseLong(seedText);

			try {
				DataBaseHandler.addBigAccount(DataBaseHandler.getSchwergsyAccountIndex(name),
						seed, 0, 0, -1);
			} catch (FuckinUpKPException e) {
				Maintenance.writeLog("Could not add big account" + Maintenance.getStackTrace(e), 
						"gui", -1);				
			}			
		}
	}

	//The listener for the perform maintenance button
	private static class MaintenanceListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				Maintenance.attemptMaintenance();
			} catch (Exception e1) {
				Maintenance.writeLog("Could not perform maintenance\n" + Maintenance.getStackTrace(e1),
						"maintenance", -1);

			}
		}
	}

	private static class StatsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {


			int i = 0;

			try {
				i = (int) DataBaseHandler.getCollectionSize("SchwergsyAccounts") - 1;
			} catch (Exception e2) {
				Maintenance.writeLog("stats button fucked up" + Maintenance.getStackTrace(e2),
						"gui", -1);
			}

			while (i >= 0) {
				Document account = DataBaseHandler.getSchwergsyAccount(i);
				DataBaseHandler.prettyPrintStatistics((String) account.get("name"));
				i--;
			}
		}
	}

	//the listener for going back in the add account screen
	private static class BackToMainListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			frame.setVisible(false);
			frame.dispose();

			drawMain();
		}
	}

	/**
	 * Listener for the Review content button on the main page
	 *
	 */
	private static class ContentListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			frame.setVisible(false);
			frame.dispose();

			JComponent panel = new JPanel();
			Object[] contentTypes = ContentDirectory.getContentTypes().toArray();
			JComboBox<Object> petList = new JComboBox<Object>(contentTypes);
			petList.setSelectedIndex(0);
			petList.addActionListener(new ListSelectListener());
			panel.add(petList, BorderLayout.PAGE_START);
			panel.add(backToMainButton);
			panel.setBackground(Color.GRAY);
			panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			panel.setOpaque(true);
			frame = new JFrame("Select Content Type");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.addWindowListener(guiExitAdapter);
			frame.setContentPane(panel);
			frame.setSize(300, 100);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	}

	private static class ListSelectListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<Object> cb = (JComboBox<Object>) e.getSource();
			type = (String) cb.getSelectedItem();
			buildContentReviewer(type);
		}
	}

	/**
	 * The method that actually draws the content reviewer section of the GUI from scratch
	 * 
	 * @param contentType The type of content to review
	 */
	public static void buildContentReviewer(String contentType) {

		lastWasApproved = null;
		undoClicked = false;
		approvedNormalContent = new HashMap<>();
		approvedTimeLessContent = new HashMap<>();
		schwagLinks = new LinkedList<>();

		MongoCollection<Document> collection =
				DataBaseHandler.getContentCollection("pending" + contentType);

		numRemaining = (int) DataBaseHandler.getCollectionSize(
				collection.getNamespace().getCollectionName()) - 1;

		FindIterable<Document> findIter = collection.find();
		cursor = findIter.iterator();

		if (cursor.hasNext()) {

			currentContent = cursor.next();

			picPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			try {
				picPanel.add(getNextPicLabel());
			} catch (IOException e) {
				Maintenance.writeLog("Failed to build content reviewer. Internet connection might"
						+ " be fuckin up. Here's the error if you're interested: " + e.toString(),
						"gui", 1);
				return;
			}
			picPanel.setBackground(Color.GRAY);

			String labelText = "number of pending " + contentType +
					" images remaining: " + numRemaining;
			JLabel numRemainingLabel = new JLabel(labelText, SwingConstants.CENTER);
			labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			labelPanel.add(numRemainingLabel);
			labelPanel.setBackground(Color.GRAY);

			Font font = new Font("SansSerif", Font.BOLD, 25);
			captionTextField = new JTextField();
			captionTextField.setFont(font);
			captionTextField.setText(currentContent.get("caption").toString());
			captionTextField.setPreferredSize(new Dimension(333, 30));

			JButton addNormalButton = new JButton("Add Normal");
			addNormalButton.addActionListener(new AddNormalContentListener());
			JButton addTimelessButton = new JButton("Add Timeless");
			addTimelessButton.addActionListener(new AddTimelessContentListener());
			JButton trashButton = new JButton("Trash");
			trashButton.addActionListener(new TrashListener());
			JButton undoButton = new JButton("Undo");
			undoButton.addActionListener(new UndoListener());
			JButton doneButton = new JButton("Done");
			doneButton.addActionListener(new DoneListener());

			buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			buttonPanel.add(addNormalButton);
			buttonPanel.add(addTimelessButton);
			buttonPanel.add(trashButton);
			buttonPanel.add(undoButton);
			buttonPanel.add(doneButton);
			buttonPanel.add(backToMainButton);
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
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.addWindowListener(guiExitAdapter);
			//So that these things close when we end the program
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					cursor.close();
					mongoClient.close();
				}
			});
			frame.getContentPane().add(scrPane);
			frame.pack();
			frame.setMinimumSize(new Dimension(300, 300));
			frame.setSize(800, 900);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
		else {
			Maintenance.writeLog("No content found in pending" + contentType, "gui");
		}
	}

	/**
	 * Get the JLabel contain the image in currentContent
	 *
	 * @return the JLabel
	 * @throws IOException
	 */
	public static JLabel getNextPicLabel() throws IOException {
		URL url = new URL(currentContent.get("imglink").toString());
		BufferedImage bufferedImage = ImageManipulator.getImageFromURL(url);
		double newHeight = (double) GlobalStuff.MAX_IMAGE_DIMENSION;
		double ratio = newHeight / ((double) bufferedImage.getHeight());
		double newWidth = ((double) bufferedImage.getWidth()) * ratio;

		//to guarantee really wide images will be fully contained by the window
		if (newWidth > (double) GlobalStuff.MAX_IMAGE_DIMENSION) {
			newWidth = (double) GlobalStuff.MAX_IMAGE_DIMENSION;
			ratio = newWidth / ((double) bufferedImage.getWidth());
			newHeight = ((double) bufferedImage.getHeight()) * ratio;
		}
		Image scaledImage =
				bufferedImage.getScaledInstance(
						(int) newWidth, (int) newHeight, Image.SCALE_SMOOTH);
		ImageIcon image = new ImageIcon(scaledImage);
		return new JLabel(image, SwingConstants.RIGHT);
	}

	private static void drawMain() {

		//for opening the gui that adds or removes schwergsy accounts from the database
		JButton schwergsButton = new JButton("Add or Remove Schwergsy Accounts");
		schwergsButton.addActionListener(new SchwergsListener());
		//for opening the gui that edits images
		JButton contentButton = new JButton("Review Content");
		contentButton.addActionListener(new ContentListener());
		//for performing maintenance on command
		JButton maintenanceButton = new JButton("Perform Maintenance");
		maintenanceButton.addActionListener(new MaintenanceListener());
		//for running stats
		if (statsButton == null) {
			statsButton = new JButton("Pretty Print Stats");
			statsButton.addActionListener(new StatsListener());
		}
		JPanel panel = new JPanel(new GridLayout(1, 4));
		panel.add(schwergsButton);
		panel.add(contentButton);
		panel.add(maintenanceButton);
		panel.add(statsButton);
		panel.setBackground(Color.GRAY);

		frame = new JFrame("Main Menu");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(guiExitAdapter);

		frame.add(panel);
		frame.setSize(1000, 100);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}


	public static void main(String[] args) throws Exception {

		Director.runDirector();

		//initialize these
		nameField = new JTextField();
		cusSecField = new JTextField();
		cusKeyField = new JTextField();
		authSecField = new JTextField();
		authKeyField = new JTextField();
		accountTypeField = new JTextField();
		assRatioField = new JTextField();
		incubatedField = new JTextField();
		suspendedField = new JTextField();
		seedField = new JTextField();
		backToMainButton = new JButton("Back");
		backToMainButton.addActionListener(new BackToMainListener());
		guiExitAdapter = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Maintenance.safeShutdownSystem();
			}
		};

		drawMain();
	}
}