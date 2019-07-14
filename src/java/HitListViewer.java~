package pm;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import javax.swing.JScrollPane;
import javax.swing.table.*;



public class AssayRunViewer extends JDialog implements java.awt.event.ActionListener {
  static JButton exportAssayRun;
  static JButton viewAssayRun;
  static JButton exportHitList;
  static JButton viewHitList;
  static JButton closeButton;
    
  static JLabel label;
  static JComboBox<ComboItem> projectList;
  final DialogMainFrame dmf;
    final Session session;
    private int project_id;
    private String owner;
  private JTable table;
  private JTable table2;
  private JScrollPane scrollPane;
    private JScrollPane scrollPane2;
    private  JPanel parentPane;
    private  JPanel pane1;
    private  JPanel pane2;
    private JPanel arButtons;
    private JPanel hlButtons;
    
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  // final EntityManager em;
  private static final long serialVersionUID = 1L;
    
    
  public AssayRunViewer(DialogMainFrame _dmf) {
    this.setTitle("Assay Run Viewer");
    this.dmf = _dmf;
    this.session = dmf.getSession();
    project_id = session.getProjectID();
    owner = session.getUserName();

    parentPane = new JPanel(new BorderLayout());

    pane1 = new JPanel(new BorderLayout());
    pane1.setBorder(BorderFactory.createRaisedBevelBorder());
    javax.swing.border.TitledBorder pane1Border = BorderFactory.createTitledBorder("Assay Runs:");
    pane1Border.setTitlePosition(javax.swing.border.TitledBorder.TOP);
    pane1.setBorder(pane1Border);

    table = dmf.getDatabaseManager().getDatabaseRetriever().getAssayRuns(session.getProjectID());

    scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    pane1.add(scrollPane, BorderLayout.CENTER);

    GridLayout buttonLayout = new GridLayout(1,4,5,5);
    projectList = new JComboBox(dmf.getDatabaseManager().getDatabaseRetriever().getAllProjects());
    projectList.setSelectedIndex(9);
    projectList.addActionListener(this);
    exportAssayRun = new JButton("Export");
    exportAssayRun.addActionListener(this);
    viewAssayRun = new JButton("View");
    viewAssayRun.addActionListener(this);
    arButtons = new JPanel(buttonLayout);
    arButtons.add(projectList);
    arButtons.add(exportAssayRun);
    arButtons.add(viewAssayRun);
    pane1.add(arButtons, BorderLayout.SOUTH);
    
    
    
    
    

    pane2  = new JPanel(new BorderLayout());
    pane2.setBorder(BorderFactory.createRaisedBevelBorder());
    javax.swing.border.TitledBorder pane2Border = BorderFactory.createTitledBorder("Hit Lists:");
    pane2Border.setTitlePosition(javax.swing.border.TitledBorder.TOP);
    pane2.setBorder(pane2Border);

    table2 = dmf.getDatabaseManager().getDatabaseRetriever().getHitLists(session.getProjectID());

    scrollPane2 = new JScrollPane(table2);
    table2.setFillsViewportHeight(true);
    pane2.add(scrollPane2, BorderLayout.CENTER);

    hlButtons = new JPanel(buttonLayout);
    closeButton = new JButton("Close");
    closeButton.addActionListener(this);
   
    exportHitList = new JButton("Export");
    exportHitList.addActionListener(this);
   viewHitList = new JButton("View");
     viewHitList.addActionListener(this);
   hlButtons.add(closeButton);
    
    hlButtons.add(exportHitList);
    hlButtons.add(viewHitList);
    pane2.add(hlButtons, BorderLayout.SOUTH);


    this.getContentPane().add(parentPane, BorderLayout.CENTER);
    parentPane.add(pane1, BorderLayout.NORTH);
    parentPane.add(pane2, BorderLayout.SOUTH);
    
    GridBagConstraints c = new GridBagConstraints();
   
    
    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);
  }

   
    public void actionPerformed(ActionEvent e) {
	
    if (e.getSource() == exportAssayRun) {
    	
    }

        if (e.getSource() == viewAssayRun) {
    	    if(!table.getSelectionModel().isSelectionEmpty()){
		 
		 TableModel arModel = table.getModel();
		 int row = table.getSelectedRow();
		 String assay_run_sys_name =  table.getModel().getValueAt(row, 0).toString();
		 int  assay_run_id = Integer.parseInt(assay_run_sys_name.substring(3));
		 new ScatterPlot(dmf, assay_run_id);}
	    else{
	      JOptionPane.showMessageDialog(dmf, "Select an Assay Run!");	      
	    }
	
    }
    if (e.getSource() == exportHitList) {
    	
    }
    if (e.getSource() == viewHitList) {
    	
    }
        if (e.getSource() == closeButton) {
	    AssayRunViewer.this.dispose();
    }


    if (e.getSource() == projectList) {
	if(projectList.getSelectedIndex() > -1){
	    project_id  = ((ComboItem)projectList.getSelectedItem()).getKey();
	    this.refreshTables(); 
	}
    }  
  }

    public void refreshTables(){
       
	CustomTable arTable = dmf.getDatabaseManager().getDatabaseRetriever().getAssayRuns(project_id);
	TableModel arModel = arTable.getModel();
	table.setModel(arModel);	

		//LOGGER.info("project: " + project_id);
	CustomTable hlTable = dmf.getDatabaseManager().getDatabaseRetriever().getHitLists(project_id);
	TableModel hlModel = hlTable.getModel();
	table2.setModel(hlModel);
	
    }

 

 
}
