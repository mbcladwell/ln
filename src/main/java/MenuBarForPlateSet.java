package ln;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


public class MenuBarForPlateSet extends JMenuBar {

  DialogMainFrame dmf;
    DatabaseManager dbm;
    CustomTable plate_set_table;
    Session session;
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public MenuBarForPlateSet(Session _s, CustomTable _plate_set_table){
	session = _s;
      dbm = session.getDatabaseManager();
      dmf = session.getDialogMainFrame();
    plate_set_table = _plate_set_table;
    //session = dmf.getSession();
       
    

    JMenu menu = new JMenu("Plate Set");
    menu.setMnemonic(KeyEvent.VK_P);
    menu.getAccessibleContext().setAccessibleDescription("Menu items related to plate sets");
    this.add(menu);

    // a group of JMenuItems
    JMenuItem menuItem = new JMenuItem("Add plate set", KeyEvent.VK_A);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Launch the Add Plate Set dialog.");
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            new DialogAddPlateSet(session);
          }
        });
    menu.add(menuItem);

    menuItem = new JMenuItem("Edit plate set ");
    menuItem.setMnemonic(KeyEvent.VK_E);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
               try{
		  int rowIndex = plate_set_table.getSelectedRow();
		  String plate_set_sys_name = plate_set_table.getValueAt(rowIndex, 0).toString();
		  int plate_set_id = Integer.valueOf(plate_set_sys_name.substring(3));
		  String layout = session.getDatabaseRetriever().getLayoutForPlateSet(plate_set_id);
		  String name = plate_set_table.getValueAt(rowIndex, 1).toString();
		  int plate_set_owner_id = session.getDatabaseRetriever().getPlateSetOwnerID(plate_set_id);
		  String description = plate_set_table.getValueAt(rowIndex, 6).toString();
		  
   
		  if ( plate_set_owner_id == session.getUserID()) {
		      new DialogEditPlateSet(session, plate_set_sys_name, name, description, layout);
	      } else {
                JOptionPane.showMessageDialog(
                    session.getDialogMainFrame(),
                    "Only the owner can modify a plate set.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
		    }   
		  
            } catch(ArrayIndexOutOfBoundsException aioob) {
              JOptionPane.showMessageDialog(
                  session.getDialogMainFrame(), "Please select a plate set!", "Error", JOptionPane.ERROR_MESSAGE);
            }catch(IndexOutOfBoundsException ioob) {
              JOptionPane.showMessageDialog(
                  session.getDialogMainFrame(), "Please select a plate set!", "Error", JOptionPane.ERROR_MESSAGE);
            }
	 	
          }
        });
    menu.add(menuItem);

    JMenu utilitiesMenu = new JMenu("Utilities");
    menu.setMnemonic(KeyEvent.VK_U);
    this.add(utilitiesMenu);

    menuItem = new JMenuItem("Group");
    menuItem.setMnemonic(KeyEvent.VK_G);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dbm.groupPlateSets(plate_set_table);
          }
        });
    utilitiesMenu.add(menuItem);

        menuItem = new JMenuItem("Reformat");
    menuItem.setMnemonic(KeyEvent.VK_R);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	    if(!plate_set_table.getSelectionModel().isSelectionEmpty()){
		dbm.reformatPlateSet(plate_set_table);
	    }else{
		JOptionPane.showMessageDialog(session.getDialogMainFrame(), "Select a Plate Set to reformat!");
	    }
	  }});
    utilitiesMenu.add(menuItem);

    menuItem = new JMenuItem("Import assay data");
    menuItem.setMnemonic(KeyEvent.VK_I);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	    if(!plate_set_table.getSelectionModel().isSelectionEmpty()){
		Object[][] results = plate_set_table.getSelectedRowsAndHeaderAsStringArray();	   
		String plate_set_sys_name = (String) results[1][0];
		int  plate_set_id = Integer.parseInt(plate_set_sys_name.substring(3));
		int format_id = Integer.parseInt((String)results[1][2]);
		int plate_num = Integer.parseInt((String)results[1][3]);
		
		new DialogAddPlateSetData(session, plate_set_sys_name, plate_set_id, format_id, plate_num);
	    }
	    else{
	      JOptionPane.showMessageDialog(session.getDialogMainFrame(), "Select a Plate Set to populate with data!");	      
	    }
          }
        });
    utilitiesMenu.add(menuItem);

      menuItem = new JMenuItem("Import Accessions");
    menuItem.setMnemonic(KeyEvent.VK_I);
    menuItem.addActionListener(
        new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if(!plate_set_table.getSelectionModel().isSelectionEmpty()){
		    Object[][] results = plate_set_table.getSelectedRowsAndHeaderAsStringArray();	   
		    String plate_set_sys_name = (String) results[1][0];
		    int  plate_set_id = Integer.parseInt(plate_set_sys_name.substring(3));
		    session.getDatabaseInserter().importAccessionsByPlateSet(plate_set_id);
		}else{
		    JOptionPane.showMessageDialog(session.getDialogMainFrame(), "Select a Plate Set for which to populate with accession IDs!");	      
		} 	   
	    }
        });
    utilitiesMenu.add(menuItem);


      menuItem = new JMenuItem("Import Barcodes");
    menuItem.setMnemonic(KeyEvent.VK_B);
    menuItem.addActionListener(
        new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if(!plate_set_table.getSelectionModel().isSelectionEmpty()){
		    Object[][] results = plate_set_table.getSelectedRowsAndHeaderAsStringArray();	   
		    String plate_set_sys_name = (String) results[1][0];
		    int  plate_set_id = Integer.parseInt(plate_set_sys_name.substring(3));
		    session.getDatabaseInserter().importBarcodesByPlateSet(plate_set_id);
		}else{
		    JOptionPane.showMessageDialog(session.getDialogMainFrame(), "Select a Plate Set for which to populate with accession IDs!");	      
		} 	   
	    }
        });
    utilitiesMenu.add(menuItem);


    
    
    menuItem = new JMenuItem("Worklist");
    menuItem.setMnemonic(KeyEvent.VK_W);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      if(!plate_set_table.getSelectionModel().isSelectionEmpty()){
		  Object[][] results = plate_set_table.getSelectedRowsAndHeaderAsStringArray();
  
		try{
	       	int worklist_id = Integer.parseInt((String)results[1][7]);
		Object[][] worklist = session.getDatabaseRetriever().getWorklist(worklist_id);
		POIUtilities poi = new POIUtilities(session);
		poi.writeJTableToSpreadsheet("Plate Sets", worklist);
		try{
		Desktop d = Desktop.getDesktop();
		d.open(new File("./Writesheet.xlsx"));
		}
		catch (IOException ioe) {
		}
		}catch(NumberFormatException nfe){
		    JOptionPane.showMessageDialog(session.getDialogMainFrame(), "Plate Set must have an associated worklist!");   
		}
	     
	    }
	    else{
	      JOptionPane.showMessageDialog(session.getDialogMainFrame(), "Select a Plate Set with an associated worklist!");	      
	    }
          }
        });
    utilitiesMenu.add(menuItem);

     menu = new JMenu("Targets");
     utilitiesMenu.add(menu);    

    menuItem = new JMenuItem("Add New Target", KeyEvent.VK_A);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      new DialogAddTarget(session);
          }
        });
    menu.add(menuItem);

        menuItem = new JMenuItem("Create Target Layout", KeyEvent.VK_C);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      new DialogCreateTargetLayout(session);
	  }     
        });
    menu.add(menuItem);

    
     menu = new JMenu("Export");
     utilitiesMenu.add(menu);    
    
    menuItem = new JMenuItem("Selected rows this table", KeyEvent.VK_S);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Export as .csv.");
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
		    Object[][] results = session.getDialogMainFrame().getUtilities().getSelectedRowsAndHeaderAsStringArray(plate_set_table);
		    if(results.length>1){
			//   LOGGER.info("hit list table: " + results);
			POIUtilities poi = new POIUtilities(session);
			poi.writeJTableToSpreadsheet("Plate Sets", results);
			try {
			    Desktop d = Desktop.getDesktop();
			    d.open(new File("./Writesheet.xlsx"));
			} catch (IOException ioe) {
			}	 
		    }else{
			JOptionPane.showMessageDialog(session.getDialogMainFrame(), "Select one or more rows!");	
		    }   

          }
        });
    menu.add(menuItem);

    
    menuItem = new JMenuItem("Underlying data", KeyEvent.VK_U);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Export as .csv.");   
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
		if(!plate_set_table.getSelectionModel().isSelectionEmpty()){

       		    
		    Object[][] results = session.getDialogMainFrame().getUtilities().getSelectedRowsAndHeaderAsStringArray(plate_set_table);
		    //  LOGGER.info("results length: " + results.length );
		    
		    if(results.length>1){
			String[] plate_set_ids = new String[results.length];
			try{
			    
			    for(int i=1; i < results.length; i++){
				plate_set_ids[i] = ((String)results[i][0]).substring(3);
			 	
				//plate_set_ids[i] =  plate_set_table.getModel().getValueAt(i, 0).toString().substring(3);
			  LOGGER.info("i: "+ i + "  psid: " + plate_set_ids[i] );
			  }

			    Object[][] plate_set_data = session.getDatabaseRetriever().getPlateSetData(plate_set_ids);
			    POIUtilities poi = new POIUtilities(session);
			    
			    poi.writeJTableToSpreadsheet("Plate Set Information", plate_set_data);
			    //poi.writeJTableToSpreadsheet("Assay Run Data for " + assay_runs_sys_name, assay_run_data);
		
			    Desktop d = Desktop.getDesktop();
			    d.open(new File("./Writesheet.xlsx"));
			}catch(IOException ioe){
			    JOptionPane.showMessageDialog(session.getDialogMainFrame(), "IOException!: " + ioe);   
			}    
		    }else{
			JOptionPane.showMessageDialog(session.getDialogMainFrame(), "Select one or more  Plate Sets!");	
		    }
		}
	  
          }
        });
    menu.add(menuItem);


    
    JButton downbutton = new JButton();
    try {
      ImageIcon down =
          new ImageIcon(
              this.getClass().getResource("/toolbarButtonGraphics/navigation/Down16.gif"));
      downbutton.setIcon(down);
    } catch (Exception ex) {
      System.out.println(ex + " ddown.PNG image not found");
    }
    downbutton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            try {
		//int i = plate_set_table.getSelectedRow();
		//String plate_set_sys_name = (String) plate_set_table.getValueAt(i, 0);
	      String results[][] = plate_set_table.getSelectedRowsAndHeaderAsStringArray();
	      String plate_set_sys_name = results[1][0];
              session.setPlateSetSysName(plate_set_sys_name);
	      session.setPlateSetID(Integer.parseInt(plate_set_sys_name.substring(3)));
	      // System.out.println("plate_set_sys_name: " + plate_set_sys_name);
	      //System.out.println("plate_set_id: " + Integer.parseInt(plate_set_sys_name.substring(3)));
	      
              session.getDialogMainFrame().showPlateTable(plate_set_sys_name);
            } catch (ArrayIndexOutOfBoundsException s) {
			JOptionPane.showMessageDialog(session.getDialogMainFrame(),
					      "Select a row!","Error",JOptionPane.ERROR_MESSAGE);
          
            } catch (IndexOutOfBoundsException s) {
		JOptionPane.showMessageDialog(session.getDialogMainFrame(),
					      "Select a row!","Error",JOptionPane.ERROR_MESSAGE);
            }
          }
        });
    this.add(downbutton);

    JButton upbutton = new JButton();

    try {
      ImageIcon up =
          new ImageIcon(this.getClass().getResource("/toolbarButtonGraphics/navigation/Up16.gif"));
      upbutton.setIcon(up);
    } catch (Exception ex) {
	System.out.println(ex);
    }
    upbutton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      //project table is always initiated - no need to check or provide alternative
            session.getDialogMainFrame().flipToProjectTable();
	    	    	      session.getDialogMainFrame().setMainFrameTitle("");

          }
        });
    this.add(upbutton);
    menu = new ViewerMenu(session);
    this.add(menu);
   
    this.add(Box.createHorizontalGlue());

    menu = new HelpMenu(session);
    this.add(menu);
  }
}
