package pm;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import javax.swing.*;
import javax.swing.JComponent.*;
import java.util.logging.*;

public class DialogLogin extends JDialog {
  static JButton button;
  static JLabel label;
  static JLabel password;
  static JTextField userIDField;
  static JTextField passwordField;
  static JButton okButton;
  static JButton cancelButton;
  final Instant instant = Instant.now();
  final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Session s;
  
    public DialogLogin  ( Session _s, String _string, ModalityType _m) {
	s = _s;

       JPanel parent_pane = new JPanel(new BorderLayout());
    
	
	
	//Pane 1 contains the controls
       JPanel pane1 = new JPanel(new GridBagLayout());
    pane1.setBorder(BorderFactory.createRaisedBevelBorder());

    GridBagConstraints c = new GridBagConstraints();
    // Image img = new
    // ImageIcon(DialogAddProject.class.getResource("../resources/mwplate.png")).getImage();
    // this.setIconImage(img);
    this.setTitle("Login to LIMS*Nucleus");
    // c.gridwidth = 2;

    label = new JLabel("LIMS*Nucleus v0.1-2019");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    c.anchor = GridBagConstraints.LINE_START;

    c.insets = new Insets(5, 5, 2, 2);
    pane1.add(label, c);

    label = new JLabel("Date:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;

    c.insets = new Insets(5, 5, 2, 2);
    pane1.add(label, c);

    label = new JLabel(df.format(Date.from(instant)));
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(label, c);

    label = new JLabel("Name:", SwingConstants.RIGHT);
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    label = new JLabel("Password:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 3;
    pane1.add(label, c);

   
    userIDField = new JTextField(30);
    c.gridx = 1;
    c.gridy = 2;
    c.gridwidth = 2;
    c.gridheight = 1;
    pane1.add(userIDField, c);

    passwordField = new JTextField(30);
    c.gridx = 1;
    c.gridy = 3;
    pane1.add(passwordField, c);

  

    okButton = new JButton("OK");
    okButton.setMnemonic(KeyEvent.VK_O);
    okButton.setActionCommand("ok");
    okButton.setEnabled(true);
    okButton.setForeground(Color.GREEN);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 5;
    c.gridwidth = 1;
    c.gridheight = 1;
    okButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      s.setUserName(userIDField.getText());
	      s.setPassword(passwordField.getText());
	      s.postLoadProperties();
            dispose();
          }
        }));

    pane1.add(okButton, c);

    cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand("cancel");
    cancelButton.setEnabled(true);
    cancelButton.setForeground(Color.RED);
    c.gridx = 1;
    c.gridy = 5;
    pane1.add(cancelButton, c);
    cancelButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
           dispose();
          }
        }));

    //Pane2 has the logo
     JPanel pane2 = new JPanel(new GridBagLayout());
    pane2.setBorder(BorderFactory.createRaisedBevelBorder());
    try {
      ImageIcon logo =
          new ImageIcon(
              this.getClass().getResource("images/las.png"));
      JLabel logolabel = new JLabel(logo, JLabel.CENTER);

      c.gridx=0;
      c.gridwidth=3;
      c.gridy=0;
      
      pane2.add(logolabel, c);
    } catch (Exception ex) {
      LOGGER.severe(ex + " las image not found");
    }
    /*
    label = new JLabel("LIMS*Nucleus v0.1-2019");
     c.gridy=1;
    c.gridx=2;
      c.gridwidth=1;
    pane2.add(label, c);
    */
    this.getContentPane().add(parent_pane, BorderLayout.CENTER);
    parent_pane.add(pane1, BorderLayout.SOUTH);
    parent_pane.add(pane2, BorderLayout.NORTH);
    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);
  }

  
}
