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

import clojure.java.api.Clojure;
import clojure.lang.IFn;



public class MenuBarForProject extends JMenuBar {

  DialogMainFrame dmf;
    DatabaseManager dbm;
  CustomTable project_table;
    //    Session session;

  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public MenuBarForProject(DatabaseManager _dbm, CustomTable _project_table) {

      dbm = _dbm;
      dmf = dbm.getDialogMainFrame();
    project_table = _project_table;
    //session = dbm.getDialogMainFrame().getSession();
     IFn require = Clojure.var("clojure.core", "require");
    require.invoke(Clojure.read("ln.codax-manager"));

    JMenu menu = new JMenu("Project");
    menu.setMnemonic(KeyEvent.VK_P);
    menu.getAccessibleContext().setAccessibleDescription("Project");
    this.add(menu);

    JMenuItem    menuItem = new JMenuItem("Export", KeyEvent.VK_E);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Export as .csv.");
    menuItem.putClientProperty("mf", dbm);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            String[][] results = project_table.getSelectedRowsAndHeaderAsStringArray();
            POIUtilities poi = new POIUtilities(dbm);
            poi.writeJTableToSpreadsheet("Projects", results);
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
      ImageIcon down =
          new ImageIcon(
              this.getClass().getResource("/toolbarButtonGraphics/navigation/Down16.gif"));
      downbutton.setIcon(down);
    } catch (Exception ex) {
      LOGGER.severe(ex + " down image not found");
    }
    downbutton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            try {
              int i = project_table.convertRowIndexToModel(project_table.getSelectedRow());
              String project_sys_name = (String) project_table.getValueAt(i, 0);
	      IFn setProjectSysName = Clojure.var("ln.codax-manager", "set-project-sys-name");
	      setProjectSysName.invoke(project_sys_name);
	      
	      int project_id = Integer.parseInt(project_sys_name.substring(4));
		  
	      IFn setProjectID = Clojure.var("ln.codax-manager", "set-project-id");
	      setProjectID.invoke(project_id);


	      //  String results[][] = project_table.getSelectedRowsAndHeaderAsStringArray();
              // LOGGER.info("down button results: " + results);
              // LOGGER.info("down button results: " + results[1][0]);
	      //session.getDatabaseManager().updateSessionWithProject(results[1][0]);
	      //dbm.getDialogMainFrame().setMainFrameTitle(results[1][0]);
              //dbm.getDialogMainFrame().showPlateSetTable(results[1][0]);
	     
              //dbm.updateSessionWithProject(project_sys_name);
	      dbm.getDialogMainFrame().setMainFrameTitle(project_sys_name);
              dbm.getDialogMainFrame().showPlateSetTable(project_sys_name);
            } catch (ArrayIndexOutOfBoundsException s) {
		JOptionPane.showMessageDialog( dbm.getDialogMainFrame(),
					      "Select a row!","Error",JOptionPane.ERROR_MESSAGE);
            } catch (IndexOutOfBoundsException s) {
		JOptionPane.showMessageDialog(dbm.getDialogMainFrame(),
					      "Select a row!","Error",JOptionPane.ERROR_MESSAGE);
            }
          }
        });
    this.add(downbutton);

    menu = new ViewerMenu(dbm);
    this.add(menu);

    IFn getUserGroupID = Clojure.var("ln.codax-manager", "get-user-group-id");
    // System.out.println( "getGroup: " + getUserGroupID.invoke());
    //System.out.println( "getGroup: " + getUserGroupID.invoke().getClass());
    //  System.out.println( "getGroup: " + (((Long)getUserGroupID.invoke()).intValue()).getClass());
    
    if((int)getUserGroupID.invoke() == 1){
    menu = new AdminMenu(dbm, project_table);
     this.add(menu);
    }
    
     this.add(Box.createHorizontalGlue());

    menu = new HelpMenu();

    this.add(menu);
  }
}
