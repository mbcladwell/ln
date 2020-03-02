



package ln;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;
import java.beans.*;
import javax.swing.*;
import clojure.java.api.Clojure;
import clojure.lang.IFn;

public class DialogAddPlateSet extends JDialog   {
    static JButton button;
    static JLabel label;
    static JLabel Description;
    static JLabel targetOptionalLabel;
    static JTextField nameField;
    static JLabel ownerLabel;
    static String owner;
    static JTextField descriptionField;
    static JTextField numberField;
    static JComboBox<Integer> formatList;
    static JComboBox<ComboItem> typeList;
    private ComboItem [] sampleLayoutNames;
    private ComboItem[] targetLayoutTypes;
    private JComboBox<ComboItem> sampleLayoutList;
    private JComboBox<ComboItem> targetLayoutList;
    private DefaultComboBoxModel<ComboItem> sample_layout_names_list_model;
    private DefaultComboBoxModel<ComboItem> target_layout_types_list_model;
    private ProgressBar progress_bar;
    private int target_layout_name_id;    
    private int project_id;    
    static JButton okButton;
    static JButton cancelButton;
    final Instant instant = Instant.now();
    final DialogMainFrame dmf;
    final DatabaseManager dbm;
    //   private Task task;
    //    final Session session;
    final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    // final EntityManager em;
    private IFn require = Clojure.var("clojure.core", "require");

  public DialogAddPlateSet(DatabaseManager _dbm) {
      dbm = _dbm;
      this.dmf = dbm.getDialogMainFrame();
    require.invoke(Clojure.read("ln.codax-manager"));
     IFn getUser = Clojure.var("ln.codax-manager", "get-user");
     IFn getProjectID = Clojure.var("ln.codax-manager", "get-project-id");
     project_id = ((Long)getProjectID.invoke()).intValue();
      //this.session = dmf.getSession();
     owner = (String)getUser.invoke();
    // Create and set up the window.
    // JFrame frame = new JFrame("Add Project");
    // this.em = em;
    JPanel pane = new JPanel(new GridBagLayout());
    pane.setBorder(BorderFactory.createRaisedBevelBorder());
    progress_bar = new ProgressBar();

    GridBagConstraints c = new GridBagConstraints();
    // Image img = new
    // ImageIcon(DialogAddProject.class.getResource("../resources/mwplate.png")).getImage();
    // this.setIconImage(img);
    this.setTitle("Add a Plate Set");
    // c.gridwidth = 2;

    label = new JLabel("Date:", SwingConstants.RIGHT);
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.LINE_END;

    c.insets = new Insets(5, 5, 2, 2);
    pane.add(label, c);

    label = new JLabel(df.format(Date.from(instant)));
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(label, c);

    label = new JLabel("Owner:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane.add(label, c);

    label = new JLabel("Plate Set Name:", SwingConstants.RIGHT);
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 2;
    pane.add(label, c);

    label = new JLabel("Description:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 3;
    c.gridheight = 1;
    pane.add(label, c);

    label = new JLabel("Number of plates:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 4;
    c.gridheight = 1;
    pane.add(label, c);

    ownerLabel = new JLabel(owner);
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 5;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(ownerLabel, c);

    nameField = new JTextField(30);
    c.gridx = 1;
    c.gridy = 2;
    c.gridheight = 1;
    pane.add(nameField, c);

    descriptionField = new JTextField(30);
    c.gridx = 1;
    c.gridy = 3;
    c.gridheight = 1;
    pane.add(descriptionField, c);

    numberField = new JTextField(4);
    c.gridx = 1;
    c.gridy = 4;
    c.gridheight = 1;
    c.gridwidth = 1;
    pane.add(numberField, c);

    label = new JLabel("Format:", SwingConstants.RIGHT);
    c.gridx = 2;
    c.gridy = 4;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane.add(label, c);

    Integer[] formats = {96, 384, 1536};

    formatList = new JComboBox<Integer>(formats);
    formatList.setSelectedIndex(0);
    c.gridx = 3;
    c.gridy = 4;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_START;
    formatList.addActionListener(
				 (new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
					     handleDropdownSelections();					     
					 }
					 
				     }));
    pane.add(formatList, c);
    // formatList.addActionListener(this);

    label = new JLabel("Type:", SwingConstants.RIGHT);
    c.gridx = 4;
    c.gridy = 4;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane.add(label, c);

    ComboItem[] plateTypes = dbm.getDatabaseRetriever().getPlateTypes();

    typeList = new JComboBox<ComboItem>(plateTypes);
    typeList.setSelectedIndex(2);
    c.gridx = 5;
    c.gridy = 4;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(typeList, c);
       typeList.addActionListener(
				 (new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
					     handleDropdownSelections();
					 }
				     }));
 

    label = new JLabel("Sample Layout:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 5;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane.add(label, c);

    ComboItem[] sampleLayoutTypes = dbm.getDatabaseRetriever().getPlateLayoutNames(96);
    //LOGGER.info("sampleLayoutTypes: " + sampleLayoutTypes[0].toString());
    sampleLayoutList = new JComboBox<ComboItem>(sampleLayoutTypes);
    sampleLayoutList.setSelectedIndex(0);
    c.gridx = 1;
    c.gridy = 5;
    c.gridheight = 1;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(sampleLayoutList, c);

    label = new JLabel("Target Layout:");
    c.gridx = 0;
    c.gridy = 6;
   c.gridwidth = 1;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane.add(label, c);

    targetLayoutTypes = dbm.getDatabaseRetriever().getTargetLayoutNamesForProject(project_id, 1);
    //LOGGER.info("sampleLayoutTypes: " + sampleLayoutTypes[0].toString());
    targetLayoutList = new JComboBox<ComboItem>(targetLayoutTypes);
    targetLayoutList.setSelectedIndex(0);
    targetLayoutList.setEnabled(false);    
    c.gridx = 1;
    c.gridy = 6;
    c.gridheight = 1;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(targetLayoutList, c);

    //optional (384, 1536 well) or invalid (96 well)
    targetOptionalLabel = new JLabel("(only for assay plates)", SwingConstants.RIGHT);
    c.gridx = 4;
    c.gridy = 6;
   c.gridwidth = 2;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(targetOptionalLabel, c);


    
    okButton = new JButton("OK");
    okButton.setMnemonic(KeyEvent.VK_O);
    okButton.setActionCommand("ok");
    okButton.setEnabled(true);
    okButton.setForeground(Color.GREEN);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 7;
    c.gridwidth = 2;
    c.gridheight = 1;
    okButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      Task task = new Task();
	      //  	     IFn getProjectID = Clojure.var("ln.codax-manager", "get-project-id");
	      //task.addPropertyChangeListener(this);
	    progress_bar.main( new String[] {"Creating Plate Set"} );
	      task.execute();
	      
          }
        }));

    pane.add(okButton, c);

    cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand("cancel");
    cancelButton.setEnabled(true);
    cancelButton.setForeground(Color.RED);
    c.gridx = 1;
    c.gridy = 7;
    c.gridwidth = 1;
    pane.add(cancelButton, c);
    cancelButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        }));

    this.getContentPane().add(pane, BorderLayout.CENTER);
    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);
  }

    private void handleDropdownSelections(){
	String selectedType = typeList.getSelectedItem().toString(); //assay, master, archive
	int selectedFormat = (int)formatList.getSelectedItem();  //96, 384, 1536
	 if(selectedType.equals("assay")){
	    switch(selectedFormat){
	    case 96:
		targetOptionalLabel.setText("(only singlicates)");
		   targetLayoutTypes = dbm.getDatabaseRetriever().getTargetLayoutNamesForProject(project_id, 1);
		   target_layout_types_list_model  = new DefaultComboBoxModel<ComboItem>( targetLayoutTypes );
		   targetLayoutList.setModel(target_layout_types_list_model);
		targetLayoutList.setEnabled(true);
		break;
	    default:
		sampleLayoutNames = dbm.getDatabaseRetriever().getSourcePlateLayoutNames(selectedFormat, 0);
		sample_layout_names_list_model = new DefaultComboBoxModel<ComboItem>( sampleLayoutNames );
		sampleLayoutList.setModel(sample_layout_names_list_model );
	    //sampleLayoutList.setSelectedIndex(-1);
		targetOptionalLabel.setText("(optional)");
		   targetLayoutTypes = dbm.getDatabaseRetriever().getTargetLayoutNamesForProject(project_id, 0);
		   target_layout_types_list_model  = new DefaultComboBoxModel<ComboItem>( targetLayoutTypes );
		   targetLayoutList.setModel(target_layout_types_list_model);
 
		targetLayoutList.setEnabled(true);
		break;
	    }
	    
	}else{
		targetOptionalLabel.setText("(only for assay plates)");
		targetLayoutList.setEnabled(false);
	    
	}
  				
				       	
    }    

         class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
	     IFn getProjectID = Clojure.var("ln.codax-manager", "get-project-id");

        @Override
        public Void doInBackground() {
	    switch((int)formatList.getSelectedItem()){
	    case 96:
		target_layout_name_id = 1;		
		break;
	    default:
		target_layout_name_id = ((ComboItem)targetLayoutList.getSelectedItem()).getKey();
		
		break;					    
	    }
	    //System.out.println("tlnid: "+ target_layout_name_id);
	    dbm.getDatabaseInserter()
		.insertPlateSet(nameField.getText(),
				descriptionField.getText(),
				Integer.valueOf(numberField.getText()),
				Integer.valueOf(formatList.getSelectedItem().toString()),
				((ComboItem)typeList.getSelectedItem()).getKey(),
				((Long)getProjectID.invoke()).intValue(),
				((ComboItem)sampleLayoutList.getSelectedItem()).getKey(),
				true,
				target_layout_name_id);		   
            return null;
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            setCursor(null); //turn off the wait cursor
	    progress_bar.setVisible(false);
	    IFn getProjectSysName = Clojure.var("ln.codax-manager", "get-project-sys-name");
	    dbm.getDialogMainFrame().showPlateSetTable((String)getProjectSysName.invoke());
            dispose();
	    //  System.out.println("complete done in swingworker in DialogAddPlateSet");
     }
    }
       
  
}

