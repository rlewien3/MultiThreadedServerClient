package client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.bulenkov.darcula.DarculaLaf;

import common.Result;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Swing GUI for a dictionary client
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
public class ClientView implements Runnable {

	private final String DEFAULT_TOAST = "Pocket Dictionary";
	private final Color STD_BACKGROUND = new Color(60, 63, 65);
	private final Color LIGHT_BACKGROUND = new Color(69, 73, 74);
	private final Color DARK_BACKGROUND = new Color(54, 57, 58);
	private final Color TOAST_BACKGROUND = new Color(45, 49, 50);
	
	private JFrame frame;
	
	private JPanel searchPanel;
	private JTextField queryField;
	private JTextPane textPane;
	private JScrollPane scrollPane;
	
	private JTextField addField;
	private JTextArea descriptionField;
	private JTextField removeField;
	
	private JPanel toast;
	private JLabel toastLabel;
	private boolean toastIsClickable;
	
	private Client client;
	private JTextField portField;
	private JTextField ipField;

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
		frame.setBounds(100, 100, 500, 553);
		GridBagLayout gridBagLayout = new GridBagLayout();
     	gridBagLayout.columnWidths = new int[]{497, 0};
     	gridBagLayout.rowHeights = new int[]{310, 25, 0};
     	gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
     	gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
     	frame.getContentPane().setLayout(gridBagLayout);
     	
     	// Close the client window properly
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
            	frame.dispose();
            	client.stopClient();
            	System.exit(0);
            }
        });
		
     	
		/** Search Tab **/
        searchPanel = new JPanel();
        searchPanel.setBackground(LIGHT_BACKGROUND);
        GridBagLayout gbl_searchPanel = new GridBagLayout();
        gbl_searchPanel.columnWidths = new int[]{20, 230, 80, 15, 0};
        gbl_searchPanel.rowHeights = new int[]{70, 10, 150, 30, 10};
        gbl_searchPanel.columnWeights = new double[]{1.0, 10.0, 0.0, 1.0, Double.MIN_VALUE};
        gbl_searchPanel.rowWeights = new double[]{1.0, 0.0, 20.0, 0.0, 1.0};
        searchPanel.setLayout(gbl_searchPanel);
        
        // Upper panel containing the search bar
        JPanel panel = new JPanel();
        panel.setBackground(STD_BACKGROUND);
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.gridwidth = 4;
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        searchPanel.add(panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{20, 230, 80, 15, 0};
        gbl_panel.rowHeights = new int[]{30, 0, 0, 10, 0};
        gbl_panel.columnWeights = new double[]{1.0, 10.0, 0.0, 1.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);
        
        // Search title
        JLabel searchTitle = new JLabel("What're you lookin for?");
        GridBagConstraints gbc_searchTitle = new GridBagConstraints();
        gbc_searchTitle.gridwidth = 2;
        gbc_searchTitle.anchor = GridBagConstraints.WEST;
        gbc_searchTitle.insets = new Insets(0, 0, 5, 5);
        gbc_searchTitle.gridx = 1;
        gbc_searchTitle.gridy = 1;
        panel.add(searchTitle, gbc_searchTitle);
        searchTitle.setFont(new Font("Tahoma", Font.BOLD, 19));
        
        // Search query field
        queryField = new JTextField();
        GridBagConstraints gbc_queryField = new GridBagConstraints();
        gbc_queryField.insets = new Insets(0, 0, 5, 5);
        gbc_queryField.fill = GridBagConstraints.HORIZONTAL;
        gbc_queryField.gridx = 1;
        gbc_queryField.gridy = 2;
        panel.add(queryField, gbc_queryField);
        
        // Search query submit button
        JButton queryButton = new JButton("Search");
        GridBagConstraints gbc_queryButton = new GridBagConstraints();
        gbc_queryButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_queryButton.insets = new Insets(0, 0, 5, 5);
        gbc_queryButton.gridx = 2;
        gbc_queryButton.gridy = 2;
        panel.add(queryButton, gbc_queryButton);
        queryButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		client.sendQuery(queryField.getText());
        		queryField.setText("");
        	}
        });
        
        // Search results text pane
        textPane = new JTextPane();
        textPane.setBackground(LIGHT_BACKGROUND);
        textPane.setEditable(false);
        scrollPane = new JScrollPane();
        scrollPane.setBorder(new EmptyBorder(0,0,0,0));
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 2;
        scrollPane.setViewportView(textPane);
        searchPanel.add(scrollPane, gbc_scrollPane);
        
        // Feeling lucky randomiser button at bottom
        JButton randomButton = new JButton("Feeling Lucky?");
     	randomButton.setBackground(LIGHT_BACKGROUND);
     	GridBagConstraints gbc_randomButton = new GridBagConstraints();
     	gbc_randomButton.fill = GridBagConstraints.HORIZONTAL;
     	gbc_randomButton.gridwidth = 2;
     	gbc_randomButton.insets = new Insets(0, 0, 5, 5);
     	gbc_randomButton.gridx = 1;
     	gbc_randomButton.gridy = 3;
     	searchPanel.add(randomButton, gbc_randomButton);
     	randomButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		client.getRandom();
        	}
        });

        

        /** Edit Tab **/
        JPanel editPanel = new JPanel();
        GridBagLayout gbl_editPanel = new GridBagLayout();
        gbl_editPanel.columnWidths = new int[]{30, 179, 0, 25, 0};
        gbl_editPanel.rowHeights = new int[]{20, 0, 0, 22, 0, 50, 15, 0, 22, 20, 45, 0};
        gbl_editPanel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_editPanel.rowWeights = new double[]{2.0, 0.0, 0.0, 0.0, 0.0, 4.0, 1.0, 0.0, 0.0, 2.0, 0.0, Double.MIN_VALUE};
        editPanel.setLayout(gbl_editPanel);
        
        // 'Add' title
        JLabel addTitle = new JLabel("Add a New Term");
        addTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        GridBagConstraints gbc_addTitle = new GridBagConstraints();
        gbc_addTitle.gridwidth = 2;
        gbc_addTitle.anchor = GridBagConstraints.WEST;
        gbc_addTitle.insets = new Insets(0, 0, 5, 5);
        gbc_addTitle.gridx = 1;
        gbc_addTitle.gridy = 1;
        editPanel.add(addTitle, gbc_addTitle);
        
        // Add word field label
        JLabel addTermLabel = new JLabel("Term");
        addTermLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
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
        addDefinitionLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        GridBagConstraints gbc_addDefinitionLabel = new GridBagConstraints();
        gbc_addDefinitionLabel.insets = new Insets(0, 0, 5, 5);
        gbc_addDefinitionLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_addDefinitionLabel.gridx = 1;
        gbc_addDefinitionLabel.gridy = 4;
        editPanel.add(addDefinitionLabel, gbc_addDefinitionLabel);
        
        // Add definition field
        descriptionField = new JTextArea();
        descriptionField.setLineWrap(true);
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
        		client.addWord(addField.getText(), descriptionField.getText());
        		addField.setText("");
        		descriptionField.setText("");
        	}
        });
        
        // 'Remove' title
        JLabel removeTitle = new JLabel("Remove a Term");
        removeTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
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
        		client.removeWord(removeField.getText());
        		removeField.setText("");
        	}
        });
     	
     	// Advanced Features Panel
     	JPanel advFeaturesPanel = new JPanel();
     	advFeaturesPanel.setBackground(DARK_BACKGROUND);
     	GridBagConstraints gbc_advFeaturesPanel = new GridBagConstraints();
     	gbc_advFeaturesPanel.gridwidth = 4;
     	gbc_advFeaturesPanel.fill = GridBagConstraints.BOTH;
     	gbc_advFeaturesPanel.gridx = 0;
     	gbc_advFeaturesPanel.gridy = 10;
     	editPanel.add(advFeaturesPanel, gbc_advFeaturesPanel);
     	GridBagLayout gbl_advFeaturesPanel = new GridBagLayout();
     	gbl_advFeaturesPanel.columnWidths = new int[]{30, 0, 0, 0, 25, 0};
     	gbl_advFeaturesPanel.rowHeights = new int[]{20, 0, 0, 0, 15, 0};
     	gbl_advFeaturesPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
     	gbl_advFeaturesPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
     	advFeaturesPanel.setLayout(gbl_advFeaturesPanel);
     	
     	// Advanced Features Title
     	JLabel advFeaturesTitle = new JLabel("Advanced Features");
     	advFeaturesTitle.setFont(new Font("Tahoma", Font.BOLD, 12));
     	GridBagConstraints gbc_advFeaturesTitle = new GridBagConstraints();
     	gbc_advFeaturesTitle.anchor = GridBagConstraints.WEST;
     	gbc_advFeaturesTitle.gridwidth = 3;
     	gbc_advFeaturesTitle.insets = new Insets(0, 0, 5, 5);
     	gbc_advFeaturesTitle.gridx = 1;
     	gbc_advFeaturesTitle.gridy = 1;
     	advFeaturesPanel.add(advFeaturesTitle, gbc_advFeaturesTitle);
     	
     	// IP Address Label
     	JLabel ipLabel = new JLabel("IP Address");
     	GridBagConstraints gbc_ipLabel = new GridBagConstraints();
     	gbc_ipLabel.anchor = GridBagConstraints.EAST;
     	gbc_ipLabel.insets = new Insets(0, 0, 5, 5);
     	gbc_ipLabel.gridx = 1;
     	gbc_ipLabel.gridy = 2;
     	advFeaturesPanel.add(ipLabel, gbc_ipLabel);
     	
     	// IP Address Field
     	ipField = new JTextField();
     	ipField.setText(client.getIPAddress()); // show current IP address
     	GridBagConstraints gbc_ipField = new GridBagConstraints();
     	gbc_ipField.insets = new Insets(0, 0, 5, 5);
     	gbc_ipField.fill = GridBagConstraints.HORIZONTAL;
     	gbc_ipField.gridx = 2;
     	gbc_ipField.gridy = 2;
     	advFeaturesPanel.add(ipField, gbc_ipField);
     	ipField.setColumns(10);
     	
     	// Port Label
     	JLabel portLabel = new JLabel("Port");
     	GridBagConstraints gbc_portLabel = new GridBagConstraints();
     	gbc_portLabel.anchor = GridBagConstraints.EAST;
     	gbc_portLabel.insets = new Insets(0, 0, 5, 5);
     	gbc_portLabel.gridx = 1;
     	gbc_portLabel.gridy = 3;
     	advFeaturesPanel.add(portLabel, gbc_portLabel);
     	
     	// Port Field
     	portField = new JTextField();
     	portField.setText(((Integer) client.getPort()).toString()); // show the current port
     	GridBagConstraints gbc_portField = new GridBagConstraints();
     	gbc_portField.insets = new Insets(0, 0, 5, 5);
     	gbc_portField.fill = GridBagConstraints.HORIZONTAL;
     	gbc_portField.gridx = 2;
     	gbc_portField.gridy = 3;
     	advFeaturesPanel.add(portField, gbc_portField);
     	portField.setColumns(10);
     	GridBagConstraints gbc_tp = new GridBagConstraints();
     	gbc_tp.fill = GridBagConstraints.BOTH;
     	gbc_tp.gridx = 0;
     	gbc_tp.gridy = 0;
     	
     	// Advanced Features Submit Button
     	JButton portSubmitButton = new JButton("Update");
     	portSubmitButton.setBackground(DARK_BACKGROUND);
     	GridBagConstraints gbc_portSubmitButton = new GridBagConstraints();
     	gbc_portSubmitButton.fill = GridBagConstraints.VERTICAL;
     	gbc_portSubmitButton.gridheight = 2;
     	gbc_portSubmitButton.insets = new Insets(0, 0, 5, 5);
     	gbc_portSubmitButton.gridx = 3;
     	gbc_portSubmitButton.gridy = 2;
     	advFeaturesPanel.add(portSubmitButton, gbc_portSubmitButton);
     	portSubmitButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		showError("Connecting...");
        		client.updateConnection(ipField.getText(), portField.getText());
        	}
        });
     	
     	// Toast panel at bottom
     	toast = new JPanel();
     	GridBagConstraints gbc_toast = new GridBagConstraints();
     	gbc_toast.fill = GridBagConstraints.BOTH;
     	gbc_toast.gridx = 0;
     	gbc_toast.gridy = 1;
     	toast.setBackground(new Color(41, 43, 45));
     	frame.getContentPane().add(toast, gbc_toast);
     	toast.addMouseListener(new MouseAdapter() {
     		// Reconnect client at the same address if no longer connected to server
     		@Override
     		public void mouseClicked(MouseEvent arg0) {
     			if (toastIsClickable) {
     				client.updateConnection(client.getIPAddress(), ((Integer) client.getPort()).toString());
     			}
     		}
     	});
     	
     	// Toast label
        toastLabel = new JLabel(DEFAULT_TOAST);
        GridBagConstraints gbc_toastLabel = new GridBagConstraints();
        gbc_toastLabel.insets = new Insets(0, 0, 5, 5);
        gbc_toastLabel.gridx = 0;
        gbc_toastLabel.gridy = 0;
        toast.add(toastLabel, gbc_toastLabel);
     	
     	
     	// Put panels together on the frame
     	JTabbedPane tp = new JTabbedPane();   
     	tp.add("Search", searchPanel);
     	tp.add("Edit", editPanel);
     	frame.getContentPane().add(tp, gbc_tp);
	}

	/**************************************************************************************************
     * 
     * 									  	Public Methods
     * 
     *************************************************************************************************/
	
	/**
	 * Shows an error in the GUI's toast area
	 */
	public void showError(String error) {
		toast.setBackground(new Color(148, 41, 41));
        toastLabel.setText(error);
        toastIsClickable = false;
	}
	
	/**
	 * Shows a clickable fatal error in the GUI's toast area
	 */
	public void showFatalConnectionError(String ipAddress, int port) {
		toast.setBackground(new Color(148, 41, 41));
        toastLabel.setText("Connection error! Click here to refresh, or change connection in Advanced Features.");
        toastIsClickable = true;
	}
	
	
	/**
	 * Shows a success message in the GUI's toast area
	 */
	public void showSuccess(String success) {
		toast.setBackground(new Color(51, 135, 96));
        toastLabel.setText(success);
        toastIsClickable = false;
        refreshAdvFeatFields();
	}
	
	/**
	 * Resets the toaster background and text to default
	 */
	public void resetToaster() {
		toast.setBackground(TOAST_BACKGROUND);
        toastLabel.setText(DEFAULT_TOAST);
        toastIsClickable = false;
        refreshAdvFeatFields();
	}
	
	/**
	 * Shows a response in the GUI's text pane
	 */
	public void showResults(String word, List<Result> results, boolean isRandom) {
        
		ArrayList<String> s = new ArrayList<String>();
		ArrayList<String> styles = new ArrayList<String>();
		
		// Random word title at top if required
		if (isRandom) {
			s.add("Lucky Term of the Day\n");
			styles.add("large title");
			
			// Separator
	        s.add("\n");
	        styles.add("small");
		}
		
		// The word itself
		s.add(capitalise(word) + "\n");
		styles.add("title");

		// Collect results together
		int n = 1;
		for (Result result : results) {

			// Separator between results
	        if (n != 1) {
	        	s.add("\n");
		        styles.add("regular");
	        }
			
			// Subtitle if multiple results
	        if (results.size() > 1) {
	        	// Separator
		        s.add("\n");
		        styles.add("small");
		        
	        	s.add("Definition " + n + "\n");
		        styles.add("subtitle");
		        n++;
	        }
	        
	        // Part of the speech
	        if (result.getPartOfSpeech().length() > 0) {
	        	s.add(result.getPartOfSpeech() +"\n");
	        	styles.add("italic");
	        }
	        
	        // Definition
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
	        	// Separator
		        s.add("\n");
		        styles.add("small");
		        
	        	s.add("Examples: \n");
		        styles.add("bold");
		        s.add(collectStrings(result.getExamples()));
		        styles.add("italic");
	        }
		}

		updateTextPane(s, styles);
    }
	
	/**************************************************************************************************
     * 
     * 									  	Helper Methods
     * 
     *************************************************************************************************/
	
	
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
	 * Updates the IP Address and Port fields in the Advanced Features to match the client's
	 */
	private void refreshAdvFeatFields() {
		ipField.setText(client.getIPAddress());
		portField.setText(((Integer) client.getPort()).toString());
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
	 * Capitalises the first letter of a string
	 */
	private String capitalise(String str) {
	    if(str == null || str.isEmpty()) {
	        return str;
	    }
	    return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * Add some standard styles to the new text document
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
 
        s = doc.addStyle("subtitle", regular);
        StyleConstants.setFontSize(s, 13);
        StyleConstants.setBold(s, true);
        
        s = doc.addStyle("title", regular);
        StyleConstants.setFontSize(s, 16);
        StyleConstants.setBold(s, true);
        
        s = doc.addStyle("large title", regular);
        StyleConstants.setFontSize(s, 18);
        StyleConstants.setBold(s, true);
	}
}
