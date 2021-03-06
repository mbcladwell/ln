package ln;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

/**
 * Called from: ScatterPlot genHits Button
 */
public class DialogNewHitListFromFile extends JDialog {
  static JButton button;
  static JLabel label;
  static JLabel Description;
  static JTextField nameField;
  static JLabel ownerLabel;
  static JLabel numHitsLabel;
  static String owner;
  static JTextField descriptionField;
  static JTextField numberField;

  static JButton okButton;
  static JButton cancelButton;
  final Instant instant = Instant.now();
    final DialogMainFrame dmf;
    final DatabaseManager dbm;
    //  final Session session;
  final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  // final EntityManager em;
    private int  assay_run_id;
    //private double[][] selected_response;
    private int num_hits;
    private int[] hit_ids;
    private IFn require = Clojure.var("clojure.core", "require");
    
    public DialogNewHitListFromFile(DatabaseManager _dbm, int _assay_run_id,  int[] _hit_ids) {
	dbm = _dbm;
	//    this.session = _session;
    this.dmf = dbm.getDialogMainFrame();
    require.invoke(Clojure.read("ln.codax-manager"));
     IFn getUser = Clojure.var("ln.codax-manager", "get-user");
     owner = (String)getUser.invoke();
    assay_run_id = _assay_run_id;
    hit_ids = _hit_ids;
    num_hits = hit_ids.length;

    JPanel pane = new JPanel(new GridBagLayout());
    pane.setBorder(BorderFactory.createRaisedBevelBorder());

    GridBagConstraints c = new GridBagConstraints();
    // Image img = new
    // ImageIcon(DialogAddProject.class.getResource("../resources/mwplate.png")).getImage();
    // this.setIconImage(img);
    this.setTitle("New Hit List from file");
    // c.gridwidth = 2;

    label = new JLabel("Date:", SwingConstants.RIGHT);
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.LINE_END;

    c.insets = new Insets(5, 5, 2, 2);
    pane.add(label, c);

    label = new JLabel(df.format(Date.from(instant)));
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(label, c);

    label = new JLabel("Owner:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane.add(label, c);

        label = new JLabel("Number of hits:", SwingConstants.RIGHT);
    c.gridx = 2;
    c.gridy = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane.add(label, c);

    label = new JLabel("Hit List Name:", SwingConstants.RIGHT);
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 2;
    pane.add(label, c);

    label = new JLabel("Description:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 3;
    c.gridheight = 1;
    pane.add(label, c);


    ownerLabel = new JLabel(owner);
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(ownerLabel, c);

    numHitsLabel = new JLabel(String.valueOf(num_hits));
    c.gridx = 3;
    c.gridy = 1;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane.add(numHitsLabel, c);

    nameField = new JTextField(30);
    c.gridx = 1;
    c.gridy = 2;
    c.gridheight = 1;
    c.gridwidth = 3;
    pane.add(nameField, c);

    descriptionField = new JTextField(30);
    c.gridx = 1;
    c.gridy = 3;
    c.gridheight = 1;
    pane.add(descriptionField, c);



    
    okButton = new JButton("OK");
    okButton.setMnemonic(KeyEvent.VK_O);
    okButton.setActionCommand("ok");
    okButton.setEnabled(true);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 6;
    c.gridwidth = 2;
    c.gridheight = 1;
    okButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      dbm.getDatabaseInserter().insertHitListFromFile2(nameField.getText(), descriptionField.getText(), num_hits, assay_run_id, hit_ids);
	      /*
	      session.getDatabaseManager().getDatabaseInserter()
		  .insertHitList( nameField.getText(),
				  descriptionField.getText(),
				  num_hits,
				  assay_run_id,
				  selected_response);
	      */
            dispose();
          }
        }));

    pane.add(okButton, c);

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

    this.getContentPane().add(pane, BorderLayout.CENTER);
    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);
  }

}
