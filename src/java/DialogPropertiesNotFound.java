package ln;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;

import clojure.java.api.Clojure;
import clojure.lang.IFn;



public class DialogPropertiesNotFound extends JDialog
    implements java.awt.event.ActionListener, javax.swing.event.DocumentListener {
  static JButton button;
  static JLabel label;
  static JLabel nLabel;
    
  static JTextField nameField;
  static JTextField descrField;
  static JTextField layoutField;
    //static JTextField nField;

    //panel 2 components
    static JTextField userField;
    static JTextField passwordField;
    static JTextField hostField;
    static JTextField portField;
    static JComboBox<ComboItem> vendorBox;
    static JComboBox<ComboItem> sourceBox;
    static JButton createLnProps;  
    static JRadioButton trueButton;
    static JRadioButton falseButton;
    static JLabel selectedLabel;
    static JLabel selectedLabelResponse;
    
    //panel 3 components


    
  static JButton okButton;
  static JButton elephantsql;
    static String sourceDescription; //for ln-props: local, elephantsql etc. a clue for populating other variables
  static JButton select;
  static JButton cancelButton;
  private static final long serialVersionUID = 1L;
    // private Session session;
  private JFileChooser fileChooser;
    
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public DialogPropertiesNotFound( ) {

	//session = new Session();
	//dmf = session.getDialogMainFrame();
	IFn require = Clojure.var("clojure.core", "require");
	require.invoke(Clojure.read("ln.session"));

    fileChooser = new JFileChooser();

    JTabbedPane tabbedPane = new JTabbedPane();
ImageIcon icon = null;

/**
 * Panel1 allows for ln-props location 
 * OR connection to ElephantSQL
 */

    JPanel panel1 = new JPanel(new GridBagLayout());
tabbedPane.addTab("Evaluate with E-SQL", icon, panel1,
                  "Use ElephantSQL example data");
tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

JPanel panel2 = new JPanel(new GridBagLayout());
tabbedPane.addTab("Find/Create ln-props", icon, panel2,
                  "Configure Database Connection");
tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

JPanel panel3 = new JPanel(new GridBagLayout());
tabbedPane.addTab("Database setup", icon, panel3,
                  "Create table, functions, etc.");
tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

    JPanel pane = new JPanel(new GridBagLayout());
    pane.setBorder(BorderFactory.createRaisedBevelBorder());

    GridBagConstraints c = new GridBagConstraints();
    // Image img = new
    // ImageIcon(DialogAddProject.class.getResource("../resources/mwplate.png")).getImage();
    // this.setIconImage(img);
    this.setTitle("Administrator Activities");
    // c.gridwidth = 2;

  try {
      ImageIcon logo =
          new ImageIcon( 
			this.getClass().getResource("images/las.png"));
      JLabel logolabel = new JLabel(logo, JLabel.CENTER);
      c.gridx=0;
      c.gridwidth=3;
      c.gridy=0;
      
      panel1.add(logolabel, c);
    } catch (Exception ex) {
      LOGGER.severe(ex + " las image not found");
      LOGGER.severe((new java.io.File(DialogPropertiesNotFound.class.getProtectionDomain().getCodeSource().getLocation().getPath())).toString());
    
    }
 

    
    elephantsql =  new JButton( "Connect to ElephantSQL");
    elephantsql.setMnemonic(KeyEvent.VK_E);    
    elephantsql.setEnabled(true);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets = new Insets(5, 5, 2, 2);
    elephantsql.addActionListener(this);
    panel1.add(elephantsql, c);

    label = new JLabel("for evaluation purposes only - no personal data.");
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 3;
    c.gridheight = 1;
    c.insets = new Insets(5, 5, 2, 2);
  panel1.add(label, c);
 

  label = new JLabel("ElephantSQL example data set refreshed daily at midnight.");
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 2;
    c.gridheight = 1;
    panel1.add(label, c);

    JButton helpButton = new JButton("Help");
    helpButton.setMnemonic(KeyEvent.VK_H);
    helpButton.setActionCommand("help");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 5;
    c.gridy = 8;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel1.add(helpButton, c);
      try {
      ImageIcon help =
          new ImageIcon(this.getClass().getResource("/toolbarButtonGraphics/general/Help16.gif"));
      helpButton.setIcon(help);
    } catch (Exception ex) {
      System.out.println("Can't find help icon: " + ex);
    }
    helpButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      //	      openWebpage(URI.create(session.getHelpURLPrefix() + "login"));
          }
        });
    helpButton.setSize(10, 10);
    //; helpButton.setPreferredSize(new Dimension(5, 20));
    // helpButton.setBounds(new Rectangle(
    //             getLocation(), getPreferredSize()));
    //helpButton.setMargin(new Insets(1, -40, 1, -100)); //(top, left, bottom, right)

    
    cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand("cancel");
    cancelButton.setEnabled(true);
    cancelButton.setForeground(Color.RED);
    c.gridx = 2;
    c.gridy = 8;
    panel1.add(cancelButton, c);
    cancelButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        }));

/**
 * Panel2 collect properties 
 * 
 */

        select =
        new JButton(
            "Find ln-props...", createImageIcon("/toolbarButtonGraphics/general/Open16.gif"));
    select.setMnemonic(KeyEvent.VK_O);
    select.setActionCommand("select");
    select.setEnabled(true);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    select.addActionListener(this);
    panel2.add(select, c);

    JButton helpButton2 = new JButton("Help");
    helpButton2.setMnemonic(KeyEvent.VK_H);
    helpButton2.setActionCommand("help");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 5;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(helpButton2, c);
      try {
      ImageIcon help =
          new ImageIcon(this.getClass().getResource("/toolbarButtonGraphics/general/Help16.gif"));
      helpButton2.setIcon(help);
    } catch (Exception ex) {
      System.out.println("Can't find help icon: " + ex);
    }
    helpButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      //	      openWebpage(URI.create(session.getHelpURLPrefix() + "login"));
          }
        });
    helpButton2.setSize(10, 10);


    selectedLabel = new JLabel("Selected:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
	c.anchor = GridBagConstraints.LINE_END;
    c.insets = new Insets(5, 5, 2, 2);
    panel2.add(selectedLabel, c);

    selectedLabelResponse = new JLabel("", SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 4;
    c.gridheight = 1;
 	c.anchor = GridBagConstraints.LINE_START;
   c.insets = new Insets(5, 5, 2, 2);
    panel2.add(selectedLabelResponse, c);


    
    
    ComboItem[] vendorTypes = new ComboItem[]{ new ComboItem(1,"PostgreSQL"), new ComboItem(2,"MySQL"), new ComboItem(3,"SQLite") };
    
	vendorBox = new JComboBox<ComboItem>(vendorTypes);
	vendorBox.setSelectedIndex(0);
	vendorBox.setEnabled(false);
	c.gridx = 1;
	c.gridy = 2;
	c.gridheight = 1;
	c.gridwidth = 5;
    c.fill = GridBagConstraints.HORIZONTAL;
	c.anchor = GridBagConstraints.LINE_START;
	panel2.add(vendorBox, c);
	vendorBox.addActionListener(new ActionListener() { 
		public void actionPerformed(ActionEvent evt) {
		    //   LOGGER.info("Algorithm event fired");
	    switch(((ComboItem)vendorBox.getSelectedItem()).getKey()){
	    case 3:
		//updateAllVariables();
		break;
	    case 2:
		//updateAllVariables();
		break;
	    case 1:
		//updateAllVariables();
		break;
	    }
        }
    });

	    ComboItem[] sourceTypes = new ComboItem[]{ new ComboItem(3,"ElephantSQL (Cloud)"), new ComboItem(4,"Heroku (Cloud)"),  new ComboItem(2,"Internal Network (within company firewall)"), new ComboItem(1,"Local PostgreSQL (personal workstation / laptop)")};
    
	sourceBox = new JComboBox<ComboItem>(sourceTypes);
	vendorBox.setSelectedIndex(0);
	c.gridx = 1;
	c.gridy = 3;
	c.gridheight = 1;
	c.gridwidth = 5;
	c.anchor = GridBagConstraints.LINE_START;
	panel2.add(sourceBox, c);
	sourceBox.addActionListener(new ActionListener() { 
		public void actionPerformed(ActionEvent evt) {
		    //   LOGGER.info("Algorithm event fired");
	    switch(((ComboItem)sourceBox.getSelectedItem()).getKey()){
	    case 4:
		hostField.setText("");
		sourceDescription = "heroku";
		//updateAllVariables();
		break;
		
	    case 3:
		hostField.setText("");
		sourceDescription = "elephantsql";
		//updateAllVariables();
		break;
	    case 2:
		hostField.setText("");
		sourceDescription = "internal";
		//updateAllVariables();
		break;
	    case 1:
		hostField.setText("127.0.0.1");
		falseButton.setSelected(true);
		sourceDescription = "local";
		//updateAllVariables();
		break;
	    }
        }
    });

    
    label = new JLabel("Database Vendor:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets = new Insets(5, 5, 2, 2);
    panel2.add(label, c);

      label = new JLabel("Source:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

      label = new JLabel("Host:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 4;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

     label = new JLabel("Port:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);
    
    label = new JLabel("SSL mode:", SwingConstants.RIGHT);
    c.gridx = 3;
    c.gridy = 5;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);
    
     label = new JLabel("User Name:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 6;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

    label = new JLabel("Password:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 7;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

label = new JLabel("User Directory:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 8;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

    label = new JLabel("Home Directory:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 9;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

    label = new JLabel("Temp Directory:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 10;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

    hostField = new JTextField(50);
    c.gridx = 1;
    c.gridy = 4;
    c.gridwidth = 5;
    c.gridheight = 1;
    panel2.add(hostField, c);

    portField = new JTextField(5);
    portField.setText("5432");	    
    c.gridx = 1;
    c.gridy = 5;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(portField, c);

    trueButton   = new JRadioButton("True");
    falseButton    = new JRadioButton("False", true);
  
    ButtonGroup bgroup = new ButtonGroup();
    bgroup.add(trueButton);
    bgroup.add(falseButton);
    c.gridx = 4;
    c.gridy = 5;
    panel2.add(trueButton, c);
    c.gridx = 5;
    panel2.add(falseButton, c);

    
    userField = new JTextField(50);
    userField.setText("ln_admin");
    c.gridx = 1;
    c.gridy = 6;
    c.gridwidth = 5;
    c.gridheight = 1;
    panel2.add(userField, c);

    passwordField = new JTextField(50);
    passwordField.setText("welcome");
    c.gridx = 1;
    c.gridy = 7;
    c.gridwidth = 5;
    c.gridheight = 1;
    panel2.add(passwordField, c);

       label = new JLabel(System.getProperty("user.dir"), SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 8;
    c.gridwidth = 5;
    c.gridheight = 1;
    panel2.add(label, c);

    label = new JLabel(System.getProperty("user.home"), SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 9;
    c.gridwidth = 5;
    c.gridheight = 1;
    panel2.add(label, c);

    label = new JLabel(System.getProperty("java.io.tmpdir"), SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 10;
    c.gridwidth = 5;
    c.gridheight = 1;
    panel2.add(label, c);

    createLnProps =
        new JButton(
            "Create ln-props", createImageIcon("/toolbarButtonGraphics/general/New16.gif"));
    select.setMnemonic(KeyEvent.VK_C);
    select.setEnabled(true);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 11;
    c.gridwidth = 1;
    c.gridheight = 1;
    createLnProps.addActionListener(this);
    panel2.add(createLnProps, c);

    /*
  okButton = new JButton("Connect (ln-props database)" );
    okButton.setMnemonic(KeyEvent.VK_P);
    okButton.setActionCommand("ok");
    okButton.setEnabled(true);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 11;
    c.gridwidth = 3;
    c.gridheight = 1;
    panel2.add(okButton, c);
    okButton.setEnabled(false);
    okButton.addActionListener(this);
    */
    c.gridx = 5;
    c.gridy = 11;
    panel2.add(cancelButton, c);

    //panel 3

    panel3.add(new DatabaseSetupPanel());

    
    this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);
  }

    
  /** Returns an ImageIcon, or null if the path was invalid. */
  protected static ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = DialogPropertiesNotFound.class.getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL);
    } else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  public void actionPerformed(ActionEvent e) {
      int top_n_number = 0;
      /*   
      if (e.getSource() == okButton) {  
	 

      }
      */
	
  if (e.getSource() == elephantsql) {
      // session.setupElephantSQL();
      this.dispose();
  }
    
    if (e.getSource() == createLnProps) {
	
	IFn createLnPropsMethod  = Clojure.var("ln.session", "create-ln-props");	  
	createLnPropsMethod.invoke( hostField.getText(),
				      portField.getText(),
				    "lndb",
				    sourceDescription,
				      Boolean.toString(trueButton.isSelected()),
				      userField.getText(),
				      passwordField.getText() );
	JOptionPane.showMessageDialog(this,
				      new String(System.getProperty("user.dir").toString() + "/ln-props created."));
	
	
	select.setEnabled(false);
	createLnProps.setEnabled(false);
	//okButton.setEnabled(true);
	selectedLabel.setText("Created:");
	selectedLabel.setForeground(Color.GREEN);
	String newDirLocation = new String(System.getProperty("user.dir").toString() + "/ln-props");
	selectedLabelResponse.setText(newDirLocation);
	
   }

    
    if (e.getSource() == select) { //find the ln-props directory and populate text fields
	fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
      int returnVal = fileChooser.showOpenDialog(DialogPropertiesNotFound.this);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        java.io.File file = fileChooser.getSelectedFile();
	selectedLabelResponse.setText(file.toString());
	IFn setLnProps  = Clojure.var("ln.session", "set-ln-props");
	setLnProps.invoke(file.toString());
	IFn getAllProps  = Clojure.var("ln.session", "get-all-props");
	
	Map<String, String> results = new HashMap<>();
	results = (Map<String, String>)getAllProps.invoke();
	LOGGER.info("results: " + results);
	LOGGER.info("results.get(:port): " + results.get(":port"));
	LOGGER.info("results.get(:sslmode): " + results.get(":sslmode"));
	

		vendorBox.setEnabled(false);
		sourceBox.setEnabled(false);
		hostField.setText(results.get(":host"));
		portField.setText(results.get(":port"));
		if(Boolean.valueOf(results.get(":sslmode"))){trueButton.setSelected(true);}else{falseButton.setSelected(true);};
		userField.setText(results.get(":user"));
		passwordField.setText(results.get(":password"));
	
      } else {
        LOGGER.info("Open command cancelled by user.\n");
      }
    }
  }

  public void insertUpdate(DocumentEvent e) {

  }

  public void removeUpdate(DocumentEvent e) {

  }

  public void changedUpdate(DocumentEvent e) {
    // Plain text components don't fire these events.
  }
  

public static boolean openWebpage(URI uri) {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(uri);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return false;
}

    public static boolean openWebpage(URL url) {
    try {
        return openWebpage(url.toURI());
    } catch (URISyntaxException e) {
        e.printStackTrace();
    }
    return false;
}

}
