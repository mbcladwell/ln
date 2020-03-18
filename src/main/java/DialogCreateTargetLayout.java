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
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;




public class DialogCreateTargetLayout extends JDialog
    implements java.awt.event.ActionListener, javax.swing.event.DocumentListener {
  static JButton button;
  static JLabel label;
  static JLabel q1label;
  static JLabel q2label;
  static JLabel q3label;
  static JLabel q4label;
    
  static JTextField nameField;
  static JTextField descriptionField;
  static JComboBox<ComboItem> quad1;
  static JComboBox<ComboItem> quad2;
  static JComboBox<ComboItem> quad3;
  static JComboBox<ComboItem> quad4;
    private ComboItem targets;
  
  static JButton okButton;

  static JButton select;
  static JButton cancelButton;
  static JButton helpButton;
    static ButtonGroup group;
    static JRadioButton singButton;
    static JRadioButton dupButton;
    static JRadioButton quadButton;
    
  private static final long serialVersionUID = 1L;
  private DialogMainFrame dmf;
  private DatabaseManager dbm;
  private DatabaseRetriever dbr;
  private DatabaseInserter dbi;
    private int replication = 1;
    private Session session;
    
        
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public DialogCreateTargetLayout( Session _s) {

      session = _s;
    //    expected_rows = dbr.getNumberOfSamplesForPlateSetID(_plate_set_id);
    // Create and set up the window.
        
	// IFn getProjectName = Clojure.var("ln.codax-manager", "get-project-name");
	    
    // JFrame frame = new JFrame("Add Project");
    this.dbm = session.getDatabaseManager();
    
    //  this.session = dmf.getSession();
    // this.dbm = session.getDatabaseManager();
    this.dbr = session.getDatabaseRetriever();
    this.dbi = dbm.getDatabaseInserter();
    

    JPanel pane = new JPanel(new GridBagLayout());
    pane.setBorder(BorderFactory.createRaisedBevelBorder());

    GridBagConstraints c = new GridBagConstraints();
     c.insets = new Insets(5, 5, 2, 2);

    this.setTitle("Create Target Layout For Project " + session.getProjectSysName() );

    label = new JLabel("Project:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.LINE_END;
    pane.add(label, c);

    label = new JLabel("Target Layout Name:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 1;
    pane.add(label, c);

    label = new JLabel("Description:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 2;
    pane.add(label, c);

    label = new JLabel("Replication:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 3;
    pane.add(label, c);

    q1label = new JLabel("Quad 1:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 4;
    pane.add(q1label, c);

    q2label = new JLabel("Quad 2:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 5;
    pane.add(q2label, c);

    q3label = new JLabel("Quad 3:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 6;
    pane.add(q3label, c);

    q4label = new JLabel("Quad 4:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 7;
    pane.add(q4label, c);

    label = new JLabel( session.getProjectSysName(), SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 0;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(label, c);

    nameField = new JTextField(30);
    c.gridwidth = 3;
    c.gridx = 1;
    c.gridy = 1;
    nameField.getDocument().addDocumentListener(this);
    pane.add(nameField, c);

    descriptionField = new JTextField(30);
    c.gridx = 1;
    c.gridy = 2;
    pane.add(descriptionField, c);

    singButton = new JRadioButton("Singlicates");
    singButton.setSelected(true);
    singButton.addActionListener(this);
    c.gridwidth = 1;
    c.gridx = 1;
    c.gridy = 3;
    pane.add(singButton, c);
    
    dupButton = new JRadioButton("Duplicates");
    dupButton.setSelected(false);
    dupButton.addActionListener(this);
    c.gridx = 2;
    c.gridy = 3;
    pane.add(dupButton, c);

    quadButton = new JRadioButton("Quadruplicates");
    quadButton.setSelected(false);
    quadButton.addActionListener(this);
    c.gridx = 3;
    c.gridy = 3;
    pane.add(quadButton, c);

    group = new ButtonGroup();
    group.add(singButton);
    group.add(dupButton);
    group.add(quadButton);
    
    //ComboItem[] targets = new ComboItem[]{ new ComboItem(4,">0% enhanced"), new ComboItem(3,"mean(background) + 3SD"), new ComboItem(2,"mean(background) + 2SD"), new ComboItem(1,"Top N")};

   ComboItem[] targets = session.getDatabaseRetriever().getTargetsForProject( session.getProjectID());
    quad1 = new JComboBox<ComboItem>(targets);
      c.gridx = 1;
    c.gridy = 4;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(quad1, c);
    quad1.setEnabled(true);
    quad1.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
	    // switch(((ComboItem)algorithmList.getSelectedItem()).getKey()){
	    // case 1:
	    // 	nLabel.setVisible(true);
	    // 	nField.setVisible(true);
	    // 	DialogAddPlateSetData.this.revalidate();
	    // 	DialogAddPlateSetData.this.repaint();
		
	    // 	break;
	    // case 2:
	    // 	nLabel.setVisible(false);
	    // 	nField.setVisible(false);
	    // 	DialogAddPlateSetData.this.revalidate();
	    // 	DialogAddPlateSetData.this.repaint();
	    // 	break;
	    // case 3:
	    // 	nLabel.setVisible(false);
	    // 	nField.setVisible(false);
	    // 	DialogAddPlateSetData.this.revalidate();
	    // 	DialogAddPlateSetData.this.repaint();
	    // 	break;		
	    // }
		   }
	});

    quad2 = new JComboBox<ComboItem>(targets);
    c.gridx = 1;
    c.gridy = 5;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(quad2, c);
    quad2.setEnabled(true);
    quad2.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		   }
	});
  
    quad3 = new JComboBox<ComboItem>(targets);
    c.gridx = 1;
    c.gridy = 6;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(quad3, c);
    quad3.setEnabled(true);
    quad3.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		   }
	});

        quad4 = new JComboBox<ComboItem>(targets);
    c.gridx = 1;
    c.gridy = 7;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(quad4, c);
    quad4.setEnabled(true);
    quad4.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		   }
	});

    okButton = new JButton("OK");
    okButton.setMnemonic(KeyEvent.VK_O);
    okButton.setActionCommand("ok");
    okButton.setEnabled(true);
    okButton.setForeground(Color.GREEN);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 8;
    c.gridwidth = 1;
    pane.add(okButton, c);
    okButton.setEnabled(false);
    okButton.addActionListener(this);

    cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand("cancel");
    cancelButton.setEnabled(true);
    cancelButton.setForeground(Color.RED);
    c.gridx = 2;
    c.gridy = 8;
    pane.add(cancelButton, c);
    cancelButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        }));

    helpButton = new JButton("Help");
    helpButton.setMnemonic(KeyEvent.VK_H);
    helpButton.setActionCommand("help");
    helpButton.setEnabled(true);
    c.gridx = 3;
    c.gridy = 8;
    pane.add(helpButton, c);
    helpButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	          

		  openWebpage(URI.create(session.getHelpURLPrefix() + "targets"));
            
          }
        }));

    this.getContentPane().add(pane, BorderLayout.CENTER);
    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);
  }

  /** Returns an ImageIcon, or null if the path was invalid. */
  protected static ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = DialogAddPlateSetData.class.getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL);
    } else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  public void actionPerformed(ActionEvent e) {
      
    if (e.getSource() == okButton) {
	switch(replication){
	case 1:
	dbm.getDatabaseInserter().addTargetLayoutName(session.getProjectID(), nameField.getText(),
						      descriptionField.getText(),
						      1,
						      ((ComboItem)quad1.getSelectedItem()).getKey(),
						      ((ComboItem)quad2.getSelectedItem()).getKey(),
						      ((ComboItem)quad3.getSelectedItem()).getKey(),
						      ((ComboItem)quad4.getSelectedItem()).getKey());	    
	    break;
	case 2:
	    dbm.getDatabaseInserter().addTargetLayoutName(session.getProjectID(), nameField.getText(),
							  descriptionField.getText(),
							  2,
							  ((ComboItem)quad1.getSelectedItem()).getKey(),
							  ((ComboItem)quad2.getSelectedItem()).getKey(),
							  ((ComboItem)quad1.getSelectedItem()).getKey(),
							  ((ComboItem)quad2.getSelectedItem()).getKey()
							  );

	    break;
	case 4:
	    	dbm.getDatabaseInserter().addTargetLayoutName(session.getProjectID(), nameField.getText(),
						      descriptionField.getText(),
							      4,
						      ((ComboItem)quad1.getSelectedItem()).getKey(),
						      ((ComboItem)quad1.getSelectedItem()).getKey(),
						      ((ComboItem)quad1.getSelectedItem()).getKey(),
							      ((ComboItem)quad1.getSelectedItem()).getKey());
						     
	    break;
	
	}
	dispose();
	return;	    	
    }

     if (e.getSource() == singButton) {
	 q1label.setText("Quad 1:");
	 q1label.setEnabled(true);
	 q2label.setText("Quad 2:");
	 q2label.setEnabled(true);
	 q3label.setText("Quad 3:");
	 q3label.setEnabled(true);
	 q4label.setText("Quad 4:");
	 q4label.setEnabled(true);

	 quad1.setEnabled(true);
	 quad2.setEnabled(true);
	 quad3.setEnabled(true);
	 quad4.setEnabled(true);
	 replication = 1;
     }
     if (e.getSource() == dupButton) {
	 q1label.setText("Quads 1,3:");
	 q1label.setEnabled(true);
	 q2label.setText("Quads 2,4:");
	 q2label.setEnabled(true);
	 q3label.setText("Quad 3:");
	 q3label.setEnabled(false);
	 q4label.setText("Quad 4:");
	 q4label.setEnabled(false);
	 quad1.setEnabled(true);
	 quad2.setEnabled(true);
	 quad3.setEnabled(false);
	 quad4.setEnabled(false);
	 replication = 2;

     }
     if (e.getSource() == quadButton) {
	 q1label.setText("Quads 1,2,3,4:");
	 q1label.setEnabled(true);
	 q2label.setText("Quad 2:");
	 q2label.setEnabled(false);
	 q3label.setText("Quad 3:");
	 q3label.setEnabled(false);
	 q4label.setText("Quad 4:");
	 q4label.setEnabled(false);
	 quad1.setEnabled(true);
	 quad2.setEnabled(false);
	 quad3.setEnabled(false);
	 quad4.setEnabled(false);
	 replication = 4;

     }
  }

  public void insertUpdate(DocumentEvent e) {

    if ( nameField.getText().length() > 0) {
      okButton.setEnabled(true);
    } else {
      okButton.setEnabled(false);
    }
  }

  public void removeUpdate(DocumentEvent e) {
    if ( nameField.getText().length() > 0) {
      okButton.setEnabled(true);
    } else {
      okButton.setEnabled(false);
    }
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
