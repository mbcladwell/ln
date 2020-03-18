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






public class MenuBarForProject extends JMenuBar {

  DialogMainFrame dmf;
    DatabaseManager dbm;
  CustomTable project_table;
     Session session;

  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public MenuBarForProject(Session _s, CustomTable _project_table) {
      session = _s;
      dbm = session.getDatabaseManager();
      dmf = session.getDialogMainFrame();
    project_table = _project_table;
    //session = session.getDialogMainFrame().getSession();
     
    

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
	      try{
            String[][] results = project_table.getSelectedRowsAndHeaderAsStringArray();
            POIUtilities poi = new POIUtilities(session);
            poi.writeJTableToSpreadsheet("Projects", results);
            
              Desktop d = Desktop.getDesktop();
              d.open(new File("./Writesheet.xlsx"));
            } catch (IOException ioe) {
            }  catch(ArrayIndexOutOfBoundsException aioob) {
              JOptionPane.showMessageDialog(
                  session.getDialogMainFrame(), "Please select some rows!", "Error", JOptionPane.ERROR_MESSAGE);
            }catch(IndexOutOfBoundsException ioob) {
              JOptionPane.showMessageDialog(
                  session.getDialogMainFrame(), "Please select some rows!", "Error", JOptionPane.ERROR_MESSAGE);
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
      LOGGER.severe(ex + " down image not found");
    }
    downbutton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            try {
		//int i = project_table.convertRowIndexToModel(project_table.getSelectedRow());
		//String project_sys_name = (String) project_table.getValueAt(i, 0);
	         String results[][] = project_table.getSelectedRowsAndHeaderAsStringArray();
		 //LOGGER.info("view row: " + project_table.getSelectedRow());
		 //LOGGER.info("model row: " + i);
	      
		 //LOGGER.info("down button results: " + results);
		 //LOGGER.info("down button row results: " + results[1][0]);
	      dbm.updateSessionWithProject(results[1][0]);
	      session.getDialogMainFrame().setMainFrameTitle(results[1][0]);
              session.getDialogMainFrame().showPlateSetTable(results[1][0]);
	     
              //dbm.updateSessionWithProject(project_sys_name);
	      //session.getDialogMainFrame().setMainFrameTitle(project_sys_name);
              //session.getDialogMainFrame().showPlateSetTable(project_sys_name);
            } catch (ArrayIndexOutOfBoundsException s) {
		JOptionPane.showMessageDialog( session.getDialogMainFrame(),
					      "Select a row!","Error",JOptionPane.ERROR_MESSAGE);
            } catch (IndexOutOfBoundsException s) {
		JOptionPane.showMessageDialog(session.getDialogMainFrame(),
					      "Select a row!","Error",JOptionPane.ERROR_MESSAGE);
            }
          }
        });
    this.add(downbutton);

    menu = new ViewerMenu(session);
    this.add(menu);

   
    if(session.getUserGroupID() == 1){
    menu = new AdminMenu(session, project_table);
     this.add(menu);
    }
    
     this.add(Box.createHorizontalGlue());

    menu = new HelpMenu(session);

    this.add(menu);
  }
}
