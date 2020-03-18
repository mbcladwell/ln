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





public class MenuBarForPlate extends JMenuBar {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private DialogMainFrame dmf;
  private   DatabaseManager dbm;
  CustomTable plate_table;
    final Session session;
   


  public MenuBarForPlate(Session _s, CustomTable _table) {
      session = _s;
      dbm = session.getDatabaseManager();
    plate_table = _table;
    dmf = session.getDialogMainFrame();
    // Create the menu bar.
    // JMenuBar menuBar = new JMenuBar();
    //    this.em = em;
    // Build the first menu.
     
    
    JMenu menu = new JMenu("Plate");
    menu.setMnemonic(KeyEvent.VK_P);
    menu.getAccessibleContext()
        .setAccessibleDescription("The only menu in this program that has menu items");
    this.add(menu);

    //a group of JMenuItems
    JMenuItem menuItem = new JMenuItem("All plates", KeyEvent.VK_A);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Show all plates for this project.");
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      try {
		  
		  String project_sys_name = session.getProjectSysName();
	 
		  session.getDialogMainFrame().showAllPlatesTable(project_sys_name);
		      
          
            } catch (IndexOutOfBoundsException s) {
		JOptionPane.showMessageDialog(session.getDialogMainFrame(),
					      "Select a row!","Error",JOptionPane.ERROR_MESSAGE);
            }
          }
        });
    menu.add(menuItem);

    menu = new JMenu("Utilities");
    menu.setMnemonic(KeyEvent.VK_U);
    menu.getAccessibleContext().setAccessibleDescription("Plate utilities");
    this.add(menu);
    
    menuItem = new JMenuItem("Group");
    menuItem.setMnemonic(KeyEvent.VK_G);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      //System.out.println("in MenuBarForPlates launching dbm.groupPlates");
            dbm.groupPlates(plate_table);
    	    

    	    session.getDialogMainFrame().showPlateTable(session.getPlateSetSysName());
          }
        });
    menu.add(menuItem);

    
    menuItem = new JMenuItem("Export", KeyEvent.VK_E);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Export as .csv.");
    menuItem.putClientProperty("mf", session.getDialogMainFrame());
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            String[][] results = plate_table.getSelectedRowsAndHeaderAsStringArray();
	    // LOGGER.info("results(plate): " + results);
            POIUtilities poi = new POIUtilities(session);
            poi.writeJTableToSpreadsheet("Plates", results);
            try {
              Desktop d = Desktop.getDesktop();
              d.open(new File("./Writesheet.xlsx"));
            } catch (IOException ioe) {
            }
            // JWSFileChooserDemo jwsfcd = new JWSFileChooserDemo();
            // jwsfcd.createAndShowGUI();

          }
        });

    menu.add(menuItem);
    JButton downbutton = new JButton();
    try {
      ImageIcon img =
          new ImageIcon(
              this.getClass().getResource("/toolbarButtonGraphics/navigation/Down16.gif"));
      downbutton.setIcon(img);
    } catch (Exception ex) {
      System.out.println(ex);
    }
    downbutton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
		
		//int i = plate_table.getSelectedRow();
		//String plate_sys_name = (String) plate_table.getValueAt(i, 0);
	      String results[][] = plate_table.getSelectedRowsAndHeaderAsStringArray();
	      String plate_sys_name = results[1][1];
	  
	      session.setPlateSysName(plate_sys_name);
	      
	      //session.setPlateSysName(plate_sys_name);
	      
	      
	      session.setPlateID(Integer.parseInt(plate_sys_name.substring(4)));
	      
              session.getDialogMainFrame().showWellTable(plate_sys_name);
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
      ImageIcon img =
          new ImageIcon(this.getClass().getResource("/toolbarButtonGraphics/navigation/Up16.gif"));
      upbutton.setIcon(img);
    } catch (Exception ex) {
      System.out.println(ex);
    }
    upbutton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            session.getDialogMainFrame().flipToPlateSet();
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
