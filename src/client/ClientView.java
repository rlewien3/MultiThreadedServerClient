package client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.bulenkov.darcula.DarculaLaf;

import server.Result;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

public class ClientView implements Runnable {

	private final String DEFAULT_TOAST = "Ryan's Dictionary";
	
	private JFrame frame;
	private JTextField queryField;
	private JTextArea textArea;
	
	private JTextField addField;
	private JTextField descriptionField;
	private JTextField removeField;
	
	private JPanel toast;
	private JLabel toastLabel;
	
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
		frame.setBounds(100, 100, 513, 349);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		/** Search Panel **/
		
		
		/** Edit Panel **/
     	GridBagLayout gridBagLayout = new GridBagLayout();
     	gridBagLayout.columnWidths = new int[]{497, 0};
     	gridBagLayout.rowHeights = new int[]{310, 25, 0};
     	gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
     	gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
     	frame.getContentPane().setLayout(gridBagLayout);
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(null);
        GridBagLayout gbl_searchPanel = new GridBagLayout();
        gbl_searchPanel.columnWidths = new int[]{20, 203, 6, 20, 0};
        gbl_searchPanel.rowHeights = new int[]{20, 0, 22, 184, 20, 0};
        gbl_searchPanel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_searchPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        searchPanel.setLayout(gbl_searchPanel);
        
        // Search title
        JLabel lblNewLabel_3 = new JLabel("Search the Dictionary");
        lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 14));
        GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
        gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_3.gridx = 1;
        gbc_lblNewLabel_3.gridy = 1;
        searchPanel.add(lblNewLabel_3, gbc_lblNewLabel_3);
        
        // Search query field
        queryField = new JTextField();
        GridBagConstraints gbc_queryField = new GridBagConstraints();
        gbc_queryField.fill = GridBagConstraints.BOTH;
        gbc_queryField.insets = new Insets(0, 0, 5, 5);
        gbc_queryField.gridx = 1;
        gbc_queryField.gridy = 2;
        searchPanel.add(queryField, gbc_queryField);
        
        // Search query submit button
        JButton queryButton = new JButton("Search");
        GridBagConstraints gbc_queryButton = new GridBagConstraints();
        gbc_queryButton.fill = GridBagConstraints.BOTH;
        gbc_queryButton.insets = new Insets(0, 0, 5, 5);
        gbc_queryButton.gridx = 2;
        gbc_queryButton.gridy = 2;
        searchPanel.add(queryButton, gbc_queryButton);
        queryButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		System.out.println("Query button clicked!");
        		textArea.setText("");
        		client.sendQuery(queryField.getText());
        		queryField.setText("");
        	}
        });
        
        // Search result area
        textArea = new JTextArea();
        textArea.setRows(10);
        GridBagConstraints gbc_textArea = new GridBagConstraints();
        gbc_textArea.fill = GridBagConstraints.BOTH;
        gbc_textArea.insets = new Insets(0, 0, 5, 5);
        gbc_textArea.gridx = 1;
        gbc_textArea.gridy = 3;
        searchPanel.add(textArea, gbc_textArea);
        JPanel editPanel = new JPanel();
        GridBagLayout gbl_editPanel = new GridBagLayout();
        gbl_editPanel.columnWidths = new int[]{20, 20, 0, 0, 20, 0};
        gbl_editPanel.rowHeights = new int[]{20, 0, 22, 50, 15, 0, 22, 20, 0, 0};
        gbl_editPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_editPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        editPanel.setLayout(gbl_editPanel);
        
        // 'Add' title
        JLabel lblNewLabel_2 = new JLabel("Add a New Term");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 14));
        GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
        gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_2.gridwidth = 2;
        gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_2.gridx = 1;
        gbc_lblNewLabel_2.gridy = 1;
        editPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
        
        // Add word field label
        JLabel lblNewLabel = new JLabel("Word");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.gridx = 1;
        gbc_lblNewLabel.gridy = 2;
        editPanel.add(lblNewLabel, gbc_lblNewLabel);
        
        // Add word field
        addField = new JTextField();
        GridBagConstraints gbc_addField = new GridBagConstraints();
        gbc_addField.fill = GridBagConstraints.BOTH;
        gbc_addField.insets = new Insets(0, 0, 5, 5);
        gbc_addField.gridx = 2;
        gbc_addField.gridy = 2;
        editPanel.add(addField, gbc_addField);
        
        // Add definition field label
        JLabel lblNewLabel_1 = new JLabel("Definition");
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTHEAST;
        gbc_lblNewLabel_1.gridx = 1;
        gbc_lblNewLabel_1.gridy = 3;
        editPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
        
        // Add definition field
        descriptionField = new JTextField();
        GridBagConstraints gbc_descriptionField = new GridBagConstraints();
        gbc_descriptionField.fill = GridBagConstraints.BOTH;
        gbc_descriptionField.insets = new Insets(0, 0, 5, 5);
        gbc_descriptionField.gridx = 2;
        gbc_descriptionField.gridy = 3;
        editPanel.add(descriptionField, gbc_descriptionField);
        
        // Add word submit button
        JButton addButton = new JButton("Add to Dictionary");
        GridBagConstraints gbc_addButton = new GridBagConstraints();
        gbc_addButton.fill = GridBagConstraints.BOTH;
        gbc_addButton.gridheight = 2;
        gbc_addButton.insets = new Insets(0, 0, 5, 5);
        gbc_addButton.gridx = 3;
        gbc_addButton.gridy = 2;
        editPanel.add(addButton, gbc_addButton);
        addButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		System.out.println("Add button clicked!");
        		textArea.setText("");
        		client.addWord(addField.getText(), descriptionField.getText());
        		addField.setText("");
        	}
        });
        
        // 'Remove' title
        JLabel lblNewLabel_4 = new JLabel("Remove a Term");
        lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 14));
        GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
        gbc_lblNewLabel_4.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_4.gridwidth = 2;
        gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_4.gridx = 1;
        gbc_lblNewLabel_4.gridy = 5;
        editPanel.add(lblNewLabel_4, gbc_lblNewLabel_4);
        
        // Remove word field
        removeField = new JTextField();
        GridBagConstraints gbc_removeField = new GridBagConstraints();
        gbc_removeField.gridwidth = 2;
        gbc_removeField.insets = new Insets(0, 0, 5, 5);
        gbc_removeField.fill = GridBagConstraints.BOTH;
        gbc_removeField.gridx = 1;
        gbc_removeField.gridy = 6;
        editPanel.add(removeField, gbc_removeField);
        removeField.setColumns(10);
        
        // Remove word submit button
        JButton removeButton = new JButton("Remove");
        GridBagConstraints gbc_removeButton = new GridBagConstraints();
        gbc_removeButton.fill = GridBagConstraints.BOTH;
        gbc_removeButton.insets = new Insets(0, 0, 5, 5);
        gbc_removeButton.gridx = 3;
        gbc_removeButton.gridy = 6;
        editPanel.add(removeButton, gbc_removeButton);
        removeButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		System.out.println("Remove button clicked!");
        		textArea.setText("");
        		client.removeWord(removeField.getText());
        		addField.setText("");
        	}
        });
                
                
        // Put panels together on the frame
     	JTabbedPane tp = new JTabbedPane();   
     	tp.add("Search", searchPanel);
     	tp.add("Edit", editPanel);
     	
     	
     	
     	GridBagConstraints gbc_tp = new GridBagConstraints();
     	gbc_tp.fill = GridBagConstraints.BOTH;
     	gbc_tp.gridx = 0;
     	gbc_tp.gridy = 0;
     	frame.getContentPane().add(tp, gbc_tp);
     	
     	// Toast panel at bottom
     	toast = new JPanel();
     	GridBagConstraints gbc_toast = new GridBagConstraints();
     	gbc_toast.fill = GridBagConstraints.BOTH;
     	gbc_toast.gridx = 0;
     	gbc_toast.gridy = 1;
     	toast.setBackground(new Color(41, 43, 45));
     	frame.getContentPane().add(toast, gbc_toast);
     	
     	// Toast label
        toastLabel = new JLabel(DEFAULT_TOAST);
        GridBagConstraints gbc_toastLabel = new GridBagConstraints();
        gbc_toastLabel.insets = new Insets(0, 0, 5, 5);
        gbc_toastLabel.gridx = 0;
        gbc_toastLabel.gridy = 0;
        toast.add(toastLabel, gbc_toastLabel);
     	
	}

	/**
	 * Shows an error in the GUI's toast area
	 */
	public void showError(String error) {
		toast.setBackground(new Color(148, 41, 41));
        toastLabel.setText(error);
	}
	
	/**
	 * Shows a success message in the GUI's toast area
	 */
	public void showSuccess(String success) {
		toast.setBackground(new Color(51, 135, 96));
        toastLabel.setText(success);
	}
	
	public void showInitialResult(String word) {
		textArea.append(word);
	}
	
	/**
	 * Shows a response in the GUI's text area DELETE IN FINAL
	 */
	public void showResults(List<Result> results) {
		
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
	 * Shows a response in the GUI's toast area DELETE IN FINAL
	 */
	public void showResponse(String response) {
		toast.setBackground(new Color(41, 43, 45));
        toastLabel.setText(response);
	}
	
	public void resetToaster() {
		toast.setBackground(new Color(41, 43, 45));
        toastLabel.setText(DEFAULT_TOAST);
	}
}
