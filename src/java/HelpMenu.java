package pm;

//import bllm.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;
//import javax.help.*;
import javax.swing.*;
import javax.swing.JComponent.*;
import java.awt.Toolkit;
import java.awt.Desktop;
import java.io.IOException;
import java.net.*;


public class HelpMenu extends JMenu {

  // DialogMainFrame dmf;
  // J/Table table;
    private Session session;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public HelpMenu( Session _s) {
      session = _s;

    this.setText("Help");
    this.setMnemonic(KeyEvent.VK_H);
    this.getAccessibleContext().setAccessibleDescription("Help items");

    JMenuItem menuItem = new JMenuItem("Launch Help", KeyEvent.VK_L);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      openWebpage(URI.create(session.getHelpURLPrefix() + "toc"));
	      //            new OpenHelpDialog();
          }
        });
    this.add(menuItem);
    /*
    menuItem = new JMenuItem("License", KeyEvent.VK_L);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            new bllm.DialogLicenseManager(
                "My Application", "./license.ser", "nszpx5U5Kt6d91JB3CW31n3SiNjSUzcZ");
          }
        });
    this.add(menuItem);
    */
    
    menuItem = new JMenuItem("About LIMS*Nucleus", KeyEvent.VK_A);
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));

    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            new DialogHelpAbout();
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
