package ln;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

public class DatabaseSetupPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  private JPanel panel1;    
  private JPanel panel2;
  private JPanel panel3;

        static JButton createTablesButton;  
    static JButton loadEgDataButton;
    static JButton deleteTablesButton;
    static JButton deleteEgDataButton; 
    static JLabel urlLabel;

    public DatabaseSetupPanel() {
	BorderLayout b = new BorderLayout();
	b.setHgap(20);
	b.setVgap(20);
	
	this.setLayout(b);

    //setup desired Clojure methods
    IFn require = Clojure.var("clojure.core", "require");
    require.invoke(Clojure.read("ln.db-init"));
    IFn dropAllTables = Clojure.var("ln.db-init", "drop-all-tables");
    IFn initLimsNucleus = Clojure.var("ln.db-init", "initialize-limsnucleus");
    IFn addExampleData = Clojure.var("ln.db-init", "add-example-data");
    IFn deleteExampleData = Clojure.var("ln.db-init", "delete-example-data");
    
    GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 2, 2);

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    //Panel 1
    panel1 = new JPanel(new GridBagLayout());
    Icon warnIcon = UIManager.getIcon("OptionPane.warningIcon");
    JLabel warningLabel = new JLabel(warnIcon);
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.EAST;
    c.weightx=1;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel1.add(warningLabel, c);

    JLabel  label = new JLabel("Buttons on this panel will delete your data. Use with caution!", SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 0;
    c.anchor = GridBagConstraints.WEST;
    c.gridwidth = 2;
    c.gridheight = 1;
    panel1.add(label, c);

    
    JButton helpButton = new JButton("Help");
    helpButton.setMnemonic(KeyEvent.VK_H);
    helpButton.setActionCommand("help");
    //c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.WEST;
    c.gridx = 0;
    c.gridy = 1;
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

	  }
        });
    //helpButton.setSize(10, 10);


    label = new JLabel("Read the help before proceeding.", SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 2;
    c.gridheight = 1;
    panel1.add(label, c);


    urlLabel = new JLabel("");
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 3;
    c.gridheight = 1;
    panel1.add(urlLabel, c);
        ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    //Panel 2
    panel2 = new JPanel(new GridBagLayout());
    javax.swing.border.TitledBorder panel2_border = BorderFactory.createTitledBorder("Base Tables");
    panel2_border.setTitlePosition(javax.swing.border.TitledBorder.TOP);
    panel2.setBorder(panel2_border);


    createTablesButton = new JButton("Create");
    createTablesButton.setMnemonic(KeyEvent.VK_T);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(createTablesButton, c);
    createTablesButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      initLimsNucleus.invoke();
          }
        });
    createTablesButton.setSize(10, 10);


        deleteTablesButton = new JButton("Delete");
    deleteTablesButton.setMnemonic(KeyEvent.VK_D);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(deleteTablesButton, c);
    deleteTablesButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      dropAllTables.invoke();
          }
        });
    deleteTablesButton.setSize(10, 10);

    label = new JLabel("Create tables, indices and required data e.g. plate layouts and assay types.", SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

    
    label = new JLabel("Delete tables, indices and all data leaving an empty database.", SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel2.add(label, c);

        ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    //Panel 3
    panel3 = new JPanel(new GridBagLayout());
    javax.swing.border.TitledBorder panel3_border = BorderFactory.createTitledBorder("Example Data");
    panel3_border.setTitlePosition(javax.swing.border.TitledBorder.TOP);
    panel3.setBorder(panel3_border);
    
        loadEgDataButton = new JButton("Load");
    loadEgDataButton.setMnemonic(KeyEvent.VK_L);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel3.add(loadEgDataButton, c);
    loadEgDataButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      addExampleData.invoke();
          }
        });
    loadEgDataButton.setSize(10, 10);


     deleteEgDataButton = new JButton("Delete");
    deleteEgDataButton.setMnemonic(KeyEvent.VK_T);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel3.add(deleteEgDataButton, c);
    deleteEgDataButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      deleteExampleData.invoke();
	  }
        });
    deleteEgDataButton.setSize(10, 10);

        label = new JLabel("Load optional example data that will allow you to excercise LIMS*Nucleus.", SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel3.add(label, c);

        label = new JLabel("Delete optional example data only, preserving tables, functions and required data.", SwingConstants.LEFT);
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    panel3.add(label, c);

    
    this.add(panel1, BorderLayout.NORTH);
    this.add(panel2, BorderLayout.CENTER);
    this.add(panel3, BorderLayout.SOUTH);
    // this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);


  }

    public void updateURLLabel (String s){
	urlLabel.setText("Connection URL: " + s);
    }
}

