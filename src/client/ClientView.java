package client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.bulenkov.darcula.DarculaLaf;

import server.Result;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

public class ClientView implements Runnable {

	private final String DEFAULT_TOAST = "Pocket Dictionary";
	
	private JFrame frame;
	
	private JPanel searchPanel;
	private JTextField queryField;
	private JTextPane textPane;
	private JScrollPane scrollPane;
	
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
		
		// Total frame of program
		frame = new JFrame();
		frame.setTitle(DEFAULT_TOAST);
		frame.setBounds(100, 100, 365, 380);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
     	gridBagLayout.columnWidths = new int[]{497, 0};
     	gridBagLayout.rowHeights = new int[]{310, 25, 0};
     	gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
     	gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
     	frame.getContentPane().setLayout(gridBagLayout);
		
     	
		/** Search Tab **/
        searchPanel = new JPanel();
        GridBagLayout gbl_searchPanel = new GridBagLayout();
        gbl_searchPanel.columnWidths = new int[]{20, 203, 0, 15, 0};
        gbl_searchPanel.rowHeights = new int[]{25, 17, 0, 10, 150, 30, 20};
        gbl_searchPanel.columnWeights = new double[]{1.0, 5.0, 0.0, 1.0, Double.MIN_VALUE};
        gbl_searchPanel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 20.0, 0.0, 1.0};
        searchPanel.setLayout(gbl_searchPanel);
        
        // Search title
        JLabel searchTitle = new JLabel("What're you lookin for?");
        searchTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        GridBagConstraints gbc_searchTitle = new GridBagConstraints();
        gbc_searchTitle.anchor = GridBagConstraints.WEST;
        gbc_searchTitle.gridwidth = 2;
        gbc_searchTitle.gridheight = 1;
        gbc_searchTitle.insets = new Insets(0, 0, 5, 5);
        gbc_searchTitle.gridx = 1;
        gbc_searchTitle.gridy = 1;
        searchPanel.add(searchTitle, gbc_searchTitle);
        
        // Search query field
        queryField = new JTextField();
        GridBagConstraints gbc_queryField = new GridBagConstraints();
        gbc_queryField.fill = GridBagConstraints.HORIZONTAL;
        gbc_queryField.gridheight = 1;
        gbc_queryField.insets = new Insets(0, 0, 5, 5);
        gbc_queryField.gridx = 1;
        gbc_queryField.gridy = 2;
        searchPanel.add(queryField, gbc_queryField);
        
        // Search query submit button
        JButton queryButton = new JButton("Search");
        GridBagConstraints gbc_queryButton = new GridBagConstraints();
        gbc_queryButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_queryButton.gridheight = 1;
        gbc_queryButton.insets = new Insets(0, 0, 5, 5);
        gbc_queryButton.gridx = 2;
        gbc_queryButton.gridy = 2;
        searchPanel.add(queryButton, gbc_queryButton);
        queryButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		System.out.println("Query button clicked!");
        		client.sendQuery(queryField.getText());
        		queryField.setText("");
        	}
        });
        
        // Search results text pane
        textPane = new JTextPane();
        textPane.setEditable(false);
        
        scrollPane = new JScrollPane();
        scrollPane.setBorder(new EmptyBorder(0,0,0,0));
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 4;
        scrollPane.setViewportView(textPane);
        searchPanel.add(scrollPane, gbc_scrollPane);

        
        
        
        /** Edit Tab **/
        JPanel editPanel = new JPanel();
        GridBagLayout gbl_editPanel = new GridBagLayout();
        gbl_editPanel.columnWidths = new int[]{20, 180, 0, 15, 0};
        gbl_editPanel.rowHeights = new int[]{20, 0, 0, 22, 0, 50, 15, 0, 22, 20, 0};
        gbl_editPanel.columnWeights = new double[]{2.0, 4.0, 0.0, 2.0, Double.MIN_VALUE};
        gbl_editPanel.rowWeights = new double[]{2.0, 0.0, 0.0, 0.0, 0.0, 4.0, 1.0, 0.0, 0.0, 2.0, Double.MIN_VALUE};
        editPanel.setLayout(gbl_editPanel);
        
        // 'Add' title
        JLabel addTitle = new JLabel("Add a New Term");
        addTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        GridBagConstraints gbc_addTitle = new GridBagConstraints();
        gbc_addTitle.gridwidth = 2;
        gbc_addTitle.anchor = GridBagConstraints.WEST;
        gbc_addTitle.insets = new Insets(0, 0, 5, 5);
        gbc_addTitle.gridx = 1;
        gbc_addTitle.gridy = 1;
        editPanel.add(addTitle, gbc_addTitle);
        
        // Add word field label
        JLabel addTermLabel = new JLabel("Term");
        addTermLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
        GridBagConstraints gbc_addTermLabel = new GridBagConstraints();
        gbc_addTermLabel.gridwidth = 2;
        gbc_addTermLabel.insets = new Insets(0, 0, 5, 5);
        gbc_addTermLabel.anchor = GridBagConstraints.WEST;
        gbc_addTermLabel.gridx = 1;
        gbc_addTermLabel.gridy = 2;
        editPanel.add(addTermLabel, gbc_addTermLabel);
        
        // Add word field
        addField = new JTextField();
        GridBagConstraints gbc_addField = new GridBagConstraints();
        gbc_addField.fill = GridBagConstraints.BOTH;
        gbc_addField.insets = new Insets(0, 0, 5, 5);
        gbc_addField.gridx = 1;
        gbc_addField.gridy = 3;
        editPanel.add(addField, gbc_addField);
        
        // Add definition field label
        JLabel addDefinitionLabel = new JLabel("Definition");
        addDefinitionLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
        GridBagConstraints gbc_addDefinitionLabel = new GridBagConstraints();
        gbc_addDefinitionLabel.insets = new Insets(0, 0, 5, 5);
        gbc_addDefinitionLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_addDefinitionLabel.gridx = 1;
        gbc_addDefinitionLabel.gridy = 4;
        editPanel.add(addDefinitionLabel, gbc_addDefinitionLabel);
        
        // Add definition field
        descriptionField = new JTextField();
        GridBagConstraints gbc_descriptionField = new GridBagConstraints();
        gbc_descriptionField.fill = GridBagConstraints.BOTH;
        gbc_descriptionField.insets = new Insets(0, 0, 5, 5);
        gbc_descriptionField.gridx = 1;
        gbc_descriptionField.gridy = 5;
        editPanel.add(descriptionField, gbc_descriptionField);
        
        // Add word submit button
        JButton addButton = new JButton("Add to Dictionary");
        GridBagConstraints gbc_addButton = new GridBagConstraints();
        gbc_addButton.fill = GridBagConstraints.BOTH;
        gbc_addButton.gridheight = 3;
        gbc_addButton.insets = new Insets(0, 0, 5, 5);
        gbc_addButton.gridx = 2;
        gbc_addButton.gridy = 3;
        editPanel.add(addButton, gbc_addButton);
        addButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		System.out.println("Add button clicked!");
        		client.addWord(addField.getText(), descriptionField.getText());
        		addField.setText("");
        	}
        });
        
        // 'Remove' title
        JLabel removeTitle = new JLabel("Remove a Term");
        removeTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        GridBagConstraints gbc_removeTitle = new GridBagConstraints();
        gbc_removeTitle.gridwidth = 2;
        gbc_removeTitle.anchor = GridBagConstraints.WEST;
        gbc_removeTitle.insets = new Insets(0, 0, 5, 5);
        gbc_removeTitle.gridx = 1;
        gbc_removeTitle.gridy = 7;
        editPanel.add(removeTitle, gbc_removeTitle);
        
        // Remove word field
        removeField = new JTextField();
        GridBagConstraints gbc_removeField = new GridBagConstraints();
        gbc_removeField.insets = new Insets(0, 0, 5, 5);
        gbc_removeField.fill = GridBagConstraints.BOTH;
        gbc_removeField.gridx = 1;
        gbc_removeField.gridy = 8;
        editPanel.add(removeField, gbc_removeField);
        removeField.setColumns(10);
        
        // Remove word submit button
        JButton removeButton = new JButton("Remove");
        GridBagConstraints gbc_removeButton = new GridBagConstraints();
        gbc_removeButton.fill = GridBagConstraints.BOTH;
        gbc_removeButton.insets = new Insets(0, 0, 5, 5);
        gbc_removeButton.gridx = 2;
        gbc_removeButton.gridy = 8;
        editPanel.add(removeButton, gbc_removeButton);
        removeButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		System.out.println("Remove button clicked!");
        		client.removeWord(removeField.getText());
        		removeField.setText("");
        	}
        });
                
                
        // Put panels together on the frame
     	JTabbedPane tp = new JTabbedPane();   
     	tp.add("Search", searchPanel);
     	
     	JButton randomButton = new JButton("Feeling lucky?");
     	GridBagConstraints gbc_randomButton = new GridBagConstraints();
     	gbc_randomButton.fill = GridBagConstraints.HORIZONTAL;
     	gbc_randomButton.gridwidth = 2;
     	gbc_randomButton.insets = new Insets(0, 0, 5, 5);
     	gbc_randomButton.gridx = 1;
     	gbc_randomButton.gridy = 5;
     	searchPanel.add(randomButton, gbc_randomButton);
     	tp.add("Edit", editPanel);
     	randomButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		System.out.println("Requested new random word!");
        		client.getRandom();
        	}
        });
     	
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
	
	/**
	 * Initial showing in text area
	 */
	//private initialResults() {
		
	//}
	
	/**
	 * Shows a response in the GUI's text pane
	 */
	public void showResults(List<Result> results) {
        
		ArrayList<String> s = new ArrayList<String>();
		ArrayList<String> styles = new ArrayList<String>();
		
		// Collect results together
		int n = 1;
		for (Result result : results) {
			
			// Separator between results
	        if (n != 1) {
	        	s.add("\n\n");
		        styles.add("regular");
	        }
			
			// title
	        s.add("Definition " + n + "\n");
	        styles.add("bold large");
	        n++;
	        
	        if (result.getPartOfSpeech().length() > 0) {
	        	s.add(result.getPartOfSpeech() +"\n");
	        	styles.add("italic");
	        }
	        
	        // definition
	        if (result.getDefinition().length() > 0) {
	        	s.add(capitalise(result.getDefinition()) + ".\n");
	        	styles.add("regular");
	        }
	        
	        // Separator
	        s.add("\n");
	        styles.add("small");
	        
	        // Synonyms
	        if (result.getSynonyms().size() > 0) {
		        s.add("Synonyms: ");
		        styles.add("bold");
		        s.add(collectStrings(result.getSynonyms()));
		        styles.add("regular");
	        }
		    
	        // Type of
	        if (result.getTypeOf().size() > 0) {
		        s.add("A type of: ");
		        styles.add("bold");
		        s.add(collectStrings(result.getTypeOf()));
		        styles.add("regular");
	        }
	        
	        // Has Types
	        if (result.getHasTypes().size() > 0) {
		        s.add("Has the types: ");
		        styles.add("bold");
		        s.add(collectStrings(result.getHasTypes()));
		        styles.add("regular");
	        }
		    
	        // Examples
	        if (result.getExamples().size() > 0) {
		        s.add("\nExamples: \n");
		        styles.add("bold");
		        s.add(collectStrings(result.getExamples()));
		        styles.add("italic");
	        }
		}

		updateTextPane(s, styles);
    }
	
	/**
	 * Updates the search result text pane
	 * Takes a list of strings to output, and each string's associated style
	 */
	private void updateTextPane(List<String> s, List<String> styles) {
		textPane.setText("");
        MutableAttributeSet mas = textPane.getInputAttributes();
        mas.removeAttributes(mas);
        
        StyledDocument doc = textPane.getStyledDocument();
        addStylesToDocument(doc);
        
        try {
            for (int i=0; i < s.size(); i++) {
                doc.insertString(doc.getLength(), s.get(i),
                                 doc.getStyle(styles.get(i)));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
	}
	
	/**
	 * Concatenates an array of strings together into one string
	 */
	private String collectStrings(List<String> strings) {
		
		String collector = "";
        for (String s : strings) {
        	collector += s + ", ";
        }
        
        // remove last comma
        collector = collector.substring(0, collector.length() - 2);
        
        return collector + "\n";
	}
	
	/**
	 * Capitalises a string
	 */
	private String capitalise(String str) {
	    if(str == null || str.isEmpty()) {
	        return str;
	    }
	    return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * Add some standard styles to the document
	 */
	private void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);
 
        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
 
        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);
 
        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
 
        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 6);
 
        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 15);
 
        s = doc.addStyle("bold large", regular);
        StyleConstants.setFontSize(s, 15);
        StyleConstants.setBold(s, true);
	}
}
