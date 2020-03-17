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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;






public class DialogPropertiesNotFound extends JDialog
    implements java.awt.event.ActionListener, javax.swing.event.DocumentListener {
  static JButton button;
  static JButton selectDirButton;
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
    static JTextField dbnameField;
    
    static JComboBox<ComboItem> vendorBox;
    static JComboBox<ComboItem> sourceBox;
    static JButton updateLnProps;  
    static JRadioButton trueButton;
    static JRadioButton falseButton;
    static JRadioButton workingButton;
    static JRadioButton userButton;
    static JLabel selectedLabel;
    static JLabel selectedLabelResponse;
    static JLabel messageLabel;
    static DatabaseSetupPanel dbSetupPanel;
    //panel 3 components


    
  static JButton okButton;
  static JButton readlnpropsButton;
    static String sourceDescription; //for ln-props: local, elephantsql etc. a clue for populating other variables
  static JButton select;
  static JButton cancelButton;
  static JButton cancelButton2;
  private static final long serialVersionUID = 1L;
    // private Session session;
  private JFileChooser fileChooser;
    private     JTabbedPane tabbedPane;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private int startup_tab;
    private Map<String, String> allprops;
    
    public DialogPropertiesNotFound( Object _m ) {
	//Map<String, String> allprops = new java.util.HashMap<String, String>( _m);
	//	Map<String, String> m = new java.util.HashMap<String, String>(_m);
	allprops = (Map<String, String>)_m;
	
	//session = new Session();
	//dmf = session.getDialogMainFrame();
	
	
	//Map<String, String> allprops = (HashMap)getAllProps.invoke();
	//LOGGER.info("allprops: " + allprops);
    fileChooser = new JFileChooser();

    dbSetupPanel = new DatabaseSetupPanel();
    
    
    
    
    
    
    
    
    	

    
    tabbedPane = new JTabbedPane();
    tabbedPane.addChangeListener(  new ChangeListener() {
	    public void stateChanged(ChangeEvent changeEvent) {
		JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
		int index = sourceTabbedPane.getSelectedIndex();
		//System.out.println("Tab changed to: " + sourceTabbedPane.getTitleAt(index));
		String source = (String)getSource.invoke();
		String conn_url =  session.getConnURL(source);
		if(source.equals("test")){conn_url="Test cloud instance with example data.";}
		dbSetupPanel.updateURLLabel(conn_url);
		//	System.out.println(conn_url);
      }});

     
     ImageIcon icon = null;

/**
 * Panel1 allows for ln-props location 
 * OR connection to ElephantSQL
 */

/*
    JPanel panel1 = new JPanel(new GridBagLayout());
tabbedPane.addTab("Directory Selection", icon, panel1,
                  "");
tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
*/

JPanel panel2 = new JPanel(new GridBagLayout());
tabbedPane.addTab("View/Update ln-props", icon, panel2,
                  "Configure Database Connection");
//tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

JPanel panel3 = new JPanel(new GridBagLayout());
tabbedPane.addTab("Database setup", icon, panel3,
                  "Create table, functions, etc.");
//tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

    JPanel pane = new JPanel(new GridBagLayout());
    pane.setBorder(BorderFactory.createRaisedBevelBorder());

    GridBagConstraints c = new GridBagConstraints();
    // Image img = new
    // ImageIcon(DialogAddProject.class.getResource("../resources/mwplate.png")).getImage();
    // this.setIconImage(img);
    this.setTitle("LAS Properties Tool");
    

  
/**
 * Panel2 collect properties 
 * 
 */

    label = new JLabel("ln-props status:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_END;
    c.insets = new Insets(5, 5, 2, 2);
    panel2.add(label, c);


    messageLabel = new JLabel("", SwingConstants.RIGHT);
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 3;
    c.gridheight = 1;
	c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(5, 5, 2, 2);
    panel2.add(messageLabel, c);



    selectedLabel = new JLabel("Directory:", SwingConstants.RIGHT);
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

	    ComboItem[] sourceTypes = new ComboItem[]{ new ComboItem(0,"Viewing ln-props"), new ComboItem(1,"Test (Cloud)"), new ComboItem(2,"ElephantSQL (Cloud)"), new ComboItem(3,"Heroku (Cloud)"),  new ComboItem(4,"Internal Network (within company firewall)"), new ComboItem(5,"Local PostgreSQL (personal workstation / laptop)")};
    
	sourceBox = new JComboBox<ComboItem>(sourceTypes);
	sourceBox.setSelectedIndex(0);
	c.gridx = 1;
	c.gridy = 3;
	c.gridheight = 1;
	c.gridwidth = 5;
	c.anchor = GridBagConstraints.LINE_START;
	panel2.add(sourceBox, c);
	sourceBox.addActionListener(this);

    
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
    
    label = new JLabel("Database Name:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 6;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

     label = new JLabel("DB User Name:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 7;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

    label = new JLabel("DB Password:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 8;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

    hostField = new JTextField(50);
    c.gridx = 1;
    c.gridy = 4;
    c.gridwidth = 5;
    c.gridheight = 1;
    panel2.add(hostField, c);

    portField = new JTextField("5432");
    c.gridx = 1;
    c.gridy = 5;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(portField, c);

    trueButton   = new JRadioButton("True");
    falseButton    = new JRadioButton("False");
  
    ButtonGroup bgroup = new ButtonGroup();
    bgroup.add(trueButton);
    bgroup.add(falseButton);
    c.gridx = 4;
    c.gridy = 5;
    panel2.add(trueButton, c);
    c.gridx = 5;
    panel2.add(falseButton, c);

    dbnameField = new JTextField(50);
    c.gridx = 1;
    c.gridy = 6;
    c.gridwidth = 6;
    c.gridheight = 1;
    panel2.add(dbnameField, c);
    
    userField = new JTextField(50);
    c.gridx = 1;
    c.gridy = 7;
    c.gridwidth = 6;
    c.gridheight = 1;
    panel2.add(userField, c);

    passwordField = new JTextField(50);
    c.gridx = 1;
    c.gridy = 8;
    c.gridwidth = 5;
    c.gridheight = 1;
    panel2.add(passwordField, c);

    // readlnpropsButton = new JButton("Read ln-props");
    // c.fill = GridBagConstraints.HORIZONTAL;
    // c.gridx = 1;
    // c.gridy = 11;
    // c.gridwidth = 1;
    // c.gridheight = 1;
    // readlnpropsButton.setEnabled(true);
    // readlnpropsButton.addActionListener(this);
    // panel2.add(readlnpropsButton, c);

    updateLnProps =
        new JButton(
            "Update ln-props", createImageIcon("/toolbarButtonGraphics/general/New16.gif"));
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 11;
    c.gridwidth = 1;
    c.gridheight = 1;
    updateLnProps.setEnabled(false);
    updateLnProps.addActionListener(this);
    panel2.add(updateLnProps, c);

 
    cancelButton2 = new JButton("Cancel");
    cancelButton2.setMnemonic(KeyEvent.VK_C);
    cancelButton2.setActionCommand("cancel");
    cancelButton2.setEnabled(true);
    cancelButton2.setForeground(Color.RED);
    c.gridx = 4;
    c.gridy = 11;
    panel2.add(cancelButton2, c);
    cancelButton2.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        }));

    JButton helpButton2 = new JButton("Help");
    helpButton2.setMnemonic(KeyEvent.VK_H);
    helpButton2.setActionCommand("help");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 5;
    c.gridy = 11;
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
    helpButton2.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      //	      openWebpage(URI.create(session.getHelpURLPrefix() + "login"));
          }
        });
    helpButton2.setSize(10, 10);

   
    //panel 3

    panel3.add(dbSetupPanel);
    java.io.File nodeFile = new java.io.File("/ln-props/nodes");
    //IFn recentlyModified  = Clojure.var("ln.codax-manager", "recently-modified?");
    Long elapsed = System.currentTimeMillis() - nodeFile.lastModified();
    if(elapsed < 10000){
	    messageLabel.setText("newly created");
	}else{
	    messageLabel.setText("pre-existing");}
	hostField.setText(allprops.get(":host"));
	portField.setText(String.valueOf(allprops.get(":port")));
	userField.setText(allprops.get(":user"));
	passwordField.setText(allprops.get(":password"));
	selectedLabelResponse.setText(System.getProperty("user.dir") + "/ln-props");
	if(String.valueOf(allprops.get(":sslmode")).equals("true")){
	    trueButton.setSelected(true);
	}else{falseButton.setSelected(true);
	}
	switch (allprops.get(":source")){
	case "internal":  sourceBox.setSelectedIndex(4);
	    break;
	case "local":  sourceBox.setSelectedIndex(5);
	    break;
	case "heroku":  sourceBox.setSelectedIndex(3);
	    break;
	case "elephantsql":  sourceBox.setSelectedIndex(2);
	    break;
	case "test":  sourceBox.setSelectedIndex(1);
	    break;
	}
	    
	/*
	hostField.setEnabled(false);
	portField.setEnabled(false);
	trueButton.setEnabled(false);
	falseButton.setEnabled(false);
	userField.setEnabled(false);
	passwordField.setEnabled(false);
	updateLnProps.setEnabled(false);
	*/
    
    this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    tabbedPane.setSelectedIndex(startup_tab);
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
    
    
    
    
    
    
    
    

      if (e.getSource() == sourceBox) {  
   switch(((ComboItem)sourceBox.getSelectedItem()).getKey()){
	    case 0:
		hostField.setText(session.getHost());
		falseButton.setSelected(session.getSSLmode());
		dbnameField.setText((String)getDBname.invoke());
		userField.setText(session.getDBuser());
		passwordField.setText(session.getDBpassword());
		updateLnProps.setEnabled(false);
		break;
	    case 1:
	    	//hostField.setText("");
	    	hostField.setText("");
	    	sourceDescription = "test";
	    	falseButton.setSelected(false);
	    	dbnameField.setText("");
	    	userField.setText("");
	    	passwordField.setText("");
	    	updateLnProps.setEnabled(true);
	    	//updateAllVariables();
	    	break;
		
	    case 2:
	    	//hostField.setText("");
	    	sourceDescription = "heroku";
	    	hostField.setText("");
	    	falseButton.setSelected(false);
	    	dbnameField.setText("");
	    	userField.setText("");
	    	passwordField.setText("");
	    	updateLnProps.setEnabled(true);
	    	//updateAllVariables();
	    	break;
		
	    case 3:
	    	//hostField.setText("");
	    	sourceDescription = "elephantsql";
	    	hostField.setText("");
	    	falseButton.setSelected(false);
	    	dbnameField.setText("");
	    	userField.setText("");
	    	passwordField.setText("");
	    	updateLnProps.setEnabled(true);
	    	//updateAllVariables();
	    	break;
	    case 4:
		//hostField.setText("");
		hostField.setText("192.168.1.11");
		sourceDescription = "internal";
		falseButton.setSelected(false);
		dbnameField.setText("lndb");
		userField.setText("ln_admin");
		passwordField.setText("welcome");
		updateLnProps.setEnabled(true);
		//updateAllVariables();
		break;
	    case 5:
		hostField.setText("127.0.0.1");
		falseButton.setSelected(false);
		userField.setText("ln_admin");
		dbnameField.setText("lndb");
		passwordField.setText("welcome");
		sourceDescription = "local";
		updateLnProps.setEnabled(true);
		//updateAllVariables();
		break;
	    }


	 

      }
	
  // if (e.getSource() == elephantsql) {
  //     // session.setupElephantSQL();
  //     this.dispose();
  // }
    
    if (e.getSource() == updateLnProps) {
	
		  
	updateLnPropsMethod.invoke( hostField.getText(),
				      portField.getText(),
				    dbnameField.getText(),
				    sourceDescription,
				      Boolean.toString(trueButton.isSelected()),
				      userField.getText(),
				    passwordField.getText(),
				    "www.labsolns.com/software",
				    System.getProperty("user.dir").toString() + "/ln-props");
	JOptionPane.showMessageDialog(this,
				      new String(System.getProperty("user.dir").toString() + "/ln-props updated."));	
	messageLabel.setText("updated");
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
