package server;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


import com.bulenkov.darcula.DarculaLaf;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Swing GUI for a multi-threaded dictionary server.
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
public class ServerView implements Runnable {

	private final String DEFAULT_TOAST = "Pocket Dictionary Server";
	private final Color TOAST_BACKGROUND = new Color(45, 49, 50);
	private Server server;

	private JFrame frame;
	
	private JButton powerButton;
	private JTextField portField;
	private JLabel portLabel;
	private JLabel dictPathLabel;
	private JTextField dictPathField;
	
	private JPanel toast;
	private JLabel toastLabel;
	

	/**
	 * Create the application.
	 */
	public ServerView(Server server) {
		
		this.server = server;
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
		frame.setBounds(100, 100, 431, 271);
		GridBagLayout gridBagLayout = new GridBagLayout();
     	gridBagLayout.columnWidths = new int[]{497, 0};
     	gridBagLayout.rowHeights = new int[]{310, 25, 0};
     	gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
     	gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
     	frame.getContentPane().setLayout(gridBagLayout);
     	
     	// Main panel
     	JPanel panel = new JPanel();
     	GridBagConstraints gbc_panel = new GridBagConstraints();
     	gbc_panel.insets = new Insets(0, 0, 5, 0);
     	gbc_panel.fill = GridBagConstraints.BOTH;
     	gbc_panel.gridx = 0;
     	gbc_panel.gridy = 0;
     	frame.getContentPane().add(panel, gbc_panel);
     	GridBagLayout gbl_panel = new GridBagLayout();
     	gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0};
     	gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
     	gbl_panel.columnWeights = new double[]{2.0, 4.0, 1.0, 2.0, Double.MIN_VALUE};
     	gbl_panel.rowWeights = new double[]{4.0, 0.0, 0.0, 1.0, 0.0, 4.0, Double.MIN_VALUE};
     	panel.setLayout(gbl_panel);
     	
     	
     	// Power Button
     	powerButton = new JButton("Start Server");
     	GridBagConstraints gbc_powerButton = new GridBagConstraints();
     	gbc_powerButton.gridheight = 4;
     	gbc_powerButton.insets = new Insets(0, 0, 5, 5);
     	gbc_powerButton.fill = GridBagConstraints.BOTH;
     	gbc_powerButton.gridx = 2;
     	gbc_powerButton.gridy = 1;
     	panel.add(powerButton, gbc_powerButton);
     	powerButton.addActionListener(new ActionListener() {
     		public void actionPerformed(ActionEvent arg0) {
     			
     			if (server.isRunning()) {
     				// turn off the server
     				server.stopServer();
     				portField.setEditable(true);
     				dictPathField.setEditable(true);
     				powerButton.setText("Start Server");
     			} else {
     				// turn on the server
     				server.setPort(portField.getText());
         			server.setDictLocation(dictPathField.getText());
         			server.startServer();
     			}
     		}
     	});
     	
     	// Port Label
     	portLabel = new JLabel("Port");
     	GridBagConstraints gbc_portLabel = new GridBagConstraints();
     	gbc_portLabel.anchor = GridBagConstraints.SOUTHWEST;
     	gbc_portLabel.insets = new Insets(0, 0, 5, 5);
     	gbc_portLabel.gridx = 1;
     	gbc_portLabel.gridy = 1;
     	panel.add(portLabel, gbc_portLabel);
     	

     	// Port Field
     	portField = new JTextField();
     	portField.setText("3784");
     	GridBagConstraints gbc_portField = new GridBagConstraints();
     	gbc_portField.insets = new Insets(0, 0, 5, 5);
     	gbc_portField.fill = GridBagConstraints.HORIZONTAL;
     	gbc_portField.gridx = 1;
     	gbc_portField.gridy = 2;
     	panel.add(portField, gbc_portField);
     	portField.setColumns(10);
     	
     	// Dictionary Path Label
     	dictPathLabel = new JLabel("Dictionary Path");
     	GridBagConstraints gbc_dictPathLabel = new GridBagConstraints();
     	gbc_dictPathLabel.anchor = GridBagConstraints.SOUTHWEST;
     	gbc_dictPathLabel.insets = new Insets(0, 0, 5, 5);
     	gbc_dictPathLabel.gridx = 1;
     	gbc_dictPathLabel.gridy = 3;
     	panel.add(dictPathLabel, gbc_dictPathLabel);
     	
     	// Dictionary Path Field
     	dictPathField = new JTextField();
     	dictPathField.setText("C:\\Users\\rlewi\\Documents\\MultiThreadedServerClient\\src\\server\\data.json");
     	GridBagConstraints gbc_dictPathField = new GridBagConstraints();
     	gbc_dictPathField.anchor = GridBagConstraints.SOUTH;
     	gbc_dictPathField.insets = new Insets(0, 0, 5, 5);
     	gbc_dictPathField.fill = GridBagConstraints.HORIZONTAL;
     	gbc_dictPathField.gridx = 1;
     	gbc_dictPathField.gridy = 4;
     	panel.add(dictPathField, gbc_dictPathField);
     	dictPathField.setColumns(10);
     	
     	
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
        
        // Make sure close properly
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
            	frame.dispose();
            	server.stopServer();
            	System.exit(0);
            }
        });
	}
	
	/**************************************************************************************************
     * 
     * 									     Helper Methods
     * 
     *************************************************************************************************/

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
	 * Shows a success message in the GUI's toast area
	 */
	public void showMessage(String message) {
		toast.setBackground(TOAST_BACKGROUND);
        toastLabel.setText(message);
	}
	
	/**
	 * Shows that the server is running
	 */
	public void showRunning() {
		powerButton.setText("Stop Server");
		portField.setEditable(false);
		dictPathField.setEditable(false);
		showSuccess("Server Running!");
	}
}
