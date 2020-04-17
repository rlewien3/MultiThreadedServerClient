package client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.bulenkov.darcula.DarculaLaf;

import server.Result;

public class ClientView implements Runnable {

	private JFrame frame;
	private JTextField queryField;
	private JTextField addField;
	private JTextField descriptionField;
	JTextArea textArea;
	private Client client;

	/**
	 * Create the application.
	 */
	public ClientView(Client client) {
		
		this.client = client;
		initialize();
	}
	
	@Override
	public void run() {
		try {
			this.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		// Set dark theme
		try {
			UIManager.setLookAndFeel(new DarculaLaf());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		/** Queries **/
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{320, 100, 0};
		gridBagLayout.rowHeights = new int[]{50, 23, 20, 150, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		queryField = new JTextField();
		GridBagConstraints gbc_queryField = new GridBagConstraints();
		gbc_queryField.fill = GridBagConstraints.HORIZONTAL;
		gbc_queryField.insets = new Insets(0, 0, 5, 5);
		gbc_queryField.gridx = 0;
		gbc_queryField.gridy = 0;
		frame.getContentPane().add(queryField, gbc_queryField);
		queryField.setColumns(10);
		queryField.setColumns(10);
		queryField.setColumns(10);
		
		JButton queryButton = new JButton("Search");
		GridBagConstraints gbc_queryButton = new GridBagConstraints();
		gbc_queryButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_queryButton.insets = new Insets(0, 0, 5, 0);
		gbc_queryButton.gridx = 1;
		gbc_queryButton.gridy = 0;
		frame.getContentPane().add(queryButton, gbc_queryButton);
		queryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Query button clicked!");
				textArea.setText("");
				client.sendQuery(queryField.getText());
				queryField.setText("");
			}
		});
		addField = new JTextField();
		GridBagConstraints gbc_addField = new GridBagConstraints();
		gbc_addField.anchor = GridBagConstraints.NORTH;
		gbc_addField.fill = GridBagConstraints.HORIZONTAL;
		gbc_addField.insets = new Insets(0, 0, 5, 5);
		gbc_addField.gridx = 0;
		gbc_addField.gridy = 1;
		frame.getContentPane().add(addField, gbc_addField);
		
		JButton addButton = new JButton("Add to Dictionary");
		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.fill = GridBagConstraints.VERTICAL;
		gbc_addButton.anchor = GridBagConstraints.WEST;
		gbc_addButton.insets = new Insets(0, 0, 5, 0);
		gbc_addButton.gridx = 1;
		gbc_addButton.gridy = 1;
		gbc_addButton.gridheight = 2;
		frame.getContentPane().add(addButton, gbc_addButton);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Add button clicked!");
				textArea.setText("");
				client.addWord(addField.getText(), descriptionField.getText()); // change to two boxes!
				addField.setText("");
			}
		});
		
		descriptionField = new JTextField();
		GridBagConstraints gbc_descriptionField = new GridBagConstraints();
		gbc_descriptionField.anchor = GridBagConstraints.NORTH;
		gbc_descriptionField.fill = GridBagConstraints.HORIZONTAL;
		gbc_descriptionField.insets = new Insets(0, 0, 5, 5);
		gbc_descriptionField.gridx = 0;
		gbc_descriptionField.gridy = 2;
		frame.getContentPane().add(descriptionField, gbc_descriptionField);
		
		textArea = new JTextArea();
		textArea.setRows(10);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.insets = new Insets(0, 0, 0, 5);
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 3;
		frame.getContentPane().add(textArea, gbc_textArea);
		
		/* Query submit button */
		
		/** Adding **/
		
		/* Addition submit button */
		
	}

	/**
	 * Shows an error in the GUI's text area
	 */
	public void showError (String error) {
		textArea.append(error + "\n");
	}
	
	/**
	 * Shows an error in the GUI's text area
	 */
	public void showSuccess (String success) {
		textArea.append(success + "\n");
	}
	
	/**
	 * Shows a response in the GUI's text area DELETE IN FINAL
	 */
	public void showResults (List<Result> results) {
		
		int i = 1;
		for (Result result : results) {
			textArea.append("Definition " + i + ": " + result.getDefinition() + "\n");
			
			if (result.getPartOfSpeech().length() > 0) {
				textArea.append("Part of Speech: " + result.getPartOfSpeech());
			}
			
			// Synonyms
			if (result.getSynonyms().size() > 0) {
				textArea.append("\nSynonyms: ");
				for (String synonym : result.getSynonyms()) {
					textArea.append(synonym + ", ");
				}
			}
			
			// Type of
			if (result.getTypeOf().size() > 0) {
				textArea.append("\nType of: ");
				for (String typeOf : result.getTypeOf()) {
					textArea.append(typeOf + ", ");
				}
			}
			
			// Has types
			if (result.getHasTypes().size() > 0) {
				textArea.append("\nHas Types: ");
				for (String hasTypes : result.getHasTypes()) {
					textArea.append(hasTypes + ", ");
				}
			}
			
			// Derivations
			if (result.getDerivation().size() > 0) {
				textArea.append("\nDerivations: ");
				for (String derivation : result.getDerivation()) {
					textArea.append(derivation + ", ");
				}
			}
			
			// Examples
			if (result.getExamples().size() > 0) {
				textArea.append("\nExamples: ");
				for (String example : result.getExamples()) {
					textArea.append(example + ", ");
				}
			}
			
			textArea.append("\n\n");
			i++;
		}
	}
	
	/**
	 * Shows a response in the GUI's text area DELETE IN FINAL
	 */
	public void showResponse (String response) {
		textArea.append(response + "\n");
	}
}
