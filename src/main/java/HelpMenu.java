package ln;

//import bllm.*;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

//import javax.help.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;





public class HelpMenu extends JMenu {

  // DialogMainFrame dmf;
  // J/Table table;
       private Session session;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    

  public HelpMenu(Session _s ) {
          session = _s;
    

    this.setText("Help");
    this.setMnemonic(KeyEvent.VK_H);
    this.getAccessibleContext().setAccessibleDescription("Help items");

    JMenuItem menuItem = new JMenuItem("Launch Help", KeyEvent.VK_L);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	       
    System.out.println(session.getHelpURLPrefix() + "toc");
	  
	      openWebpage(URI.create(session.getHelpURLPrefix() + "toc"));
	      //            new OpenHelpDialog();
          }
        });
    this.add(menuItem);
    
    // menuItem = new JMenuItem("License", KeyEvent.VK_L);
    // menuItem.addActionListener(
    //     new ActionListener() {
    //       public void actionPerformed(ActionEvent e) {
    // 	          new DialogLicense();
    //       }
    //     });
    // this.add(menuItem);
    
    
    menuItem = new JMenuItem("About LIMS*Nucleus", KeyEvent.VK_A);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));

    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            new DialogHelpAbout(session);
          }
        });
    this.add(menuItem);
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