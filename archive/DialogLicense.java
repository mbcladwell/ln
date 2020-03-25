package ln;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.beans.*;
import javax.swing.*;




public class DialogLicense extends JDialog {
  static JButton button;
  static JLabel label;
  static JLabel statusLabel;
  static JTextField customerIDField;
  static JTextField licenseKeyField;
  static JTextField emailField;
  static JButton okButton;
  static JButton cancelButton;
  final Instant instant = Instant.now();
  static DialogMainFrame dmf;
    //private static Session session;
    // private static DatabaseManager dbm;
  final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    

  public DialogLicense() {
     
   
    
   
    JPanel pane = new JPanel(new GridBagLayout());
    pane.setBorder(BorderFactory.createRaisedBevelBorder());

    GridBagConstraints c = new GridBagConstraints();
    // Image img = new
    // ImageIcon(DialogAddProject.class.getResource("../resources/mwplate.png")).getImage();
    // this.setIconImage(img);
    this.setTitle("Register License-key");
    // c.gridwidth = 2;

    label = new JLabel("Date:", SwingConstants.RIGHT);
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 2, 2);
    c.anchor = GridBagConstraints.LINE_END;
    pane.add(label, c);

    label = new JLabel("Status:", SwingConstants.RIGHT);
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 1;
    pane.add(label, c);

    label = new JLabel("Customer ID:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 2;
    pane.add(label, c);

    label = new JLabel("License-key:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 3;
    pane.add(label, c);

    label = new JLabel("email:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 4;
    pane.add(label, c);

    label = new JLabel(df.format(Date.from(instant)));
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(label, c);

    JButton helpButton = new JButton("Help");
    helpButton.setMnemonic(KeyEvent.VK_H);
    helpButton.setActionCommand("help");
    c.anchor = GridBagConstraints.LINE_END;
    c.gridx = 3;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    pane.add(helpButton, c);
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
	          

		  openWebpage(URI.create(session.getHelpURLPrefix() + "register"));
          }
        });

    
    statusLabel = new JLabel();
    c.gridx = 1;
    c.gridy = 1;
    c.gridheight = 1;
    pane.add(statusLabel, c);

    

    customerIDField = new JTextField(30);
    customerIDField.setText((String)getCustomerID.invoke());
    c.gridwidth = 3;
    c.gridx = 1;
    c.gridy = 2;
    pane.add(customerIDField, c);

    

    licenseKeyField = new JTextField(30);
    licenseKeyField.setText((String)getLicenseKey.invoke());
    c.gridx = 1;
    c.gridy = 3;
    c.gridheight = 1;
    pane.add(licenseKeyField, c);

    

    emailField = new JTextField(30);
    emailField.setText((String)getEmail.invoke());
    c.gridx = 1;
    c.gridy = 4;
    c.gridheight = 1;
    pane.add(emailField, c);

    cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand("cancel");
    cancelButton.setEnabled(true);
    cancelButton.setForeground(Color.RED);
    c.gridx = 1;
    c.gridy = 6;
    c.gridwidth = 1;
    pane.add(cancelButton, c);
    cancelButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        }));

    okButton = new JButton("OK");
    okButton.setMnemonic(KeyEvent.VK_O);
    okButton.setActionCommand("ok");
    okButton.setEnabled(true);
    okButton.setForeground(Color.GREEN);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 6;
    c.gridwidth = 2;
    c.gridheight = 1;
    okButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      
	      setLicenseCredentials.invoke(customerIDField.getText(), licenseKeyField.getText(), emailField.getText() );
	      if(!(validateLicenseKey())){JOptionPane.showMessageDialog(DialogLicense.this,
								      "Invalid license key.",
								      "Error",
								      JOptionPane.ERROR_MESSAGE);}
          }
        }));
    pane.add(okButton, c);




    validateLicenseKey();
    this.getContentPane().add(pane, BorderLayout.CENTER);
    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);
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

  private boolean validateLicenseKey() {
    
    boolean result = session.validateLicenseKeyCLJ();
    if(result){
	statusLabel.setText("Licensed");
	statusLabel.setForeground(Color.GREEN);
	customerIDField.setEnabled(false);
	licenseKeyField.setEnabled(false);
	emailField.setEnabled(false);
	okButton.setEnabled(false);
    }else{
	statusLabel.setText("Unlicensed");
	statusLabel.setForeground(Color.RED);
	customerIDField.setEnabled(true);
	licenseKeyField.setEnabled(true);
	emailField.setEnabled(true);
	okButton.setEnabled(true);
	
    }
    return result;   
  }
}
