package ln;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

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

import clojure.java.api.Clojure;
import clojure.lang.IFn;



public class MenuBarForGlobalQuery extends JMenuBar {

  DialogMainFrame dmf;
    DatabaseManager dbm;
    CustomTable search_table;
      Connection conn;

    // Session session;
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public MenuBarForGlobalQuery(DatabaseManager _dbm, CustomTable _search_table){
      dbm = _dbm;
      this.conn = dbm.getConnection();
  
      dmf = dbm.getDialogMainFrame();
    search_table = _search_table;
    
    IFn require = Clojure.var("clojure.core", "require");
    require.invoke(Clojure.read("ln.codax-manager"));

    JMenu menu = new JMenu("Global Query");
    this.add(menu);

  
    JMenu utilitiesMenu = new JMenu("Utilities");
    utilitiesMenu.setMnemonic(KeyEvent.VK_U);
    this.add(utilitiesMenu);

         JMenuItem    selectAllMenuItem = new JMenuItem("Select All", KeyEvent.VK_S);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    selectAllMenuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
          search_table.selectAll();
          }
        });
    utilitiesMenu.add(selectAllMenuItem);

   
     JMenuItem    menuItem = new JMenuItem("Export", KeyEvent.VK_E);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Export as .csv.");
    menuItem.putClientProperty("mf", dbm);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      try{
            String[][] results = search_table.getSelectedRowsAndHeaderAsStringArray();
            POIUtilities poi = new POIUtilities(dbm);
            poi.writeJTableToSpreadsheet("Global search results", results);
            
              Desktop d = Desktop.getDesktop();
              d.open(new File("./Writesheet.xlsx"));
            } catch (IOException ioe) {
            } catch(ArrayIndexOutOfBoundsException aioob) {
              JOptionPane.showMessageDialog(
                  dbm.getDialogMainFrame(), "Please select a plate set!", "Error", JOptionPane.ERROR_MESSAGE);
            }catch(IndexOutOfBoundsException ioob) {
              JOptionPane.showMessageDialog(
                  dbm.getDialogMainFrame(), "Please select a plate set!", "Error", JOptionPane.ERROR_MESSAGE);
            }

          }
        });
    utilitiesMenu.add(menuItem);

   
   
    
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
	      //  System.out.println("in action listener");

            try {
		//int i = plate_set_table.getSelectedRow();
		//String plate_set_sys_name = (String) plate_set_table.getValueAt(i, 0);
	      String results[][] = search_table.getSelectedRowsAndHeaderAsStringArray();
	      String project_sys_name = results[1][0];
	      int project_id = Integer.valueOf(project_sys_name.substring(4));
	      String plate_set_sys_name = new String();    //   =results[1][1]; does not exist for project
	      int plate_set_id = 0;            //= Integer.valueOf(plate_set_sys_name.substring(3));
	      String entity_name = results[1][2];
	      String entity_sys_name = results[1][3];
	      String sql_statement = new String();
		  
	     

	      switch (entity_name){
	      case "Sample":
		  plate_set_sys_name  = results[1][1];
		  plate_set_id = Integer.valueOf(plate_set_sys_name.substring(3));
		  int sample_id = Integer.valueOf(entity_sys_name.substring(4));
	 	  		     	     
		  try {
		      PreparedStatement pstmt = conn.prepareStatement("SELECT plate.plate_sys_name  FROM plate_set, plate, plate_plate_set, well, well_sample, sample WHERE plate_plate_set.plate_set_id = ?  AND plate_plate_set.plate_id = plate.id AND plate_plate_set.plate_set_id = plate_set.id and plate.id=well.plate_id and well_sample.well_id=well.id and well_sample.sample_id=sample.id and sample.id=?;");

		      pstmt.setInt(1, plate_set_id);
		      pstmt.setInt(2, sample_id);     
		      
		      ResultSet rs = pstmt.executeQuery();
		      rs.next();
		      String plate_sys_name = rs.getString("plate_sys_name");
		      // LOGGER.info("resuklt plate layout name: " + result);
		      rs.close();
		      pstmt.close();
		    dmf.showWellTable(plate_sys_name);
		      
		  } catch (SQLException sqle) {
		      LOGGER.severe("SQL exception getting assay_type_id: " + sqle);
		  }
	 
		  break;
	      case "Plate":
		  plate_set_sys_name = results[1][1];
		  dmf.showPlateTable(plate_set_sys_name);
		  break;
	      case "PlateSet":
		  plate_set_sys_name = results[1][1];
		    dmf.showPlateTable(plate_set_sys_name);		  
		  break;
	      case "Project":
		    dmf.showPlateSetTable(project_sys_name);		  
		  break;
	      case "AssayRun":
		  //must set the project so the AssayRunViewer opens with correct project
		  
		  	IFn setProjectSysName = Clojure.var("ln.codax-manager", "set-project-sys-name");
			setProjectSysName.invoke(project_sys_name);
		  	IFn setProjectID = Clojure.var("ln.codax-manager", "set-project-id");
			setProjectID.invoke(project_id);

			new AssayRunViewer(dbm);
			dmf.showPlateSetTable(project_sys_name);		  
		  
		  break;
	      case "HitList":
		  int hit_list_id = Integer.valueOf(entity_sys_name.substring(3));		  
		  new HitListViewer(dbm, hit_list_id);
		  break;
	      }
	      //need this here to go to different tables
              //dbm.getDialogMainFrame().showPlateTable(plate_set_sys_name);
            } catch (ArrayIndexOutOfBoundsException s) {
			JOptionPane.showMessageDialog(dbm.getDialogMainFrame(),
					      "Select a row!","Error",JOptionPane.ERROR_MESSAGE);
          
            } catch (IndexOutOfBoundsException s) {
		JOptionPane.showMessageDialog(dbm.getDialogMainFrame(),
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
	IFn getProjectSysName = Clojure.var("ln.codax-manager", "get-project-sys-name");

	String project_sys_name = (String)getProjectSysName.invoke();
            dbm.getDialogMainFrame().showPlateSetTable(project_sys_name);
	    	    	      dbm.getDialogMainFrame().setMainFrameTitle("");

          }
        });
    this.add(upbutton);
   
    this.add(Box.createHorizontalGlue());

    menu = new HelpMenu();
    this.add(menu);
  }
}
