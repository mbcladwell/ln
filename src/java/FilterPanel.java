package ln;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.JComponent.*;
import javax.swing.RowFilter;
import javax.swing.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class FilterPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  private JTable table;
  private JScrollPane scrollPane;
  private DialogMainFrame parent;
  private JPanel textPanel;
  private JButton clearButton;
  private JButton refreshButton;
    private int entity_type;
    private int id;
    private JTextField textField;
    private Session session;    

  /**
   * @param id the project/plateset/plate etc id for fetching the main table
 */
    public FilterPanel(DialogMainFrame _parent, JTable _table, int _id, int _entity_type) {
    this.setLayout(new GridBagLayout());
    parent = _parent;
    session = parent.getSession();
    table = _table;
    id = _id;
    entity_type = _entity_type;

    TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
    table.setRowSorter(rowSorter);

    GridBagConstraints c = new GridBagConstraints();
    /*
    JLabel label = new JLabel("Filter:", SwingConstants.RIGHT);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0.1;
    c.anchor = GridBagConstraints.LINE_END;
    c.insets = new Insets(5, 5, 2, 2);
    this.add(label, c);
    */
    refreshButton = new JButton("Refresh");
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0.1;
    c.anchor = GridBagConstraints.LINE_END;
    c.insets = new Insets(5, 5, 2, 2);
    refreshButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	      JTable table2 =  session.getDatabaseManager().getDatabaseRetriever().getDMFTableData(id, entity_type );
	       TableModel model = table2.getModel();
	       table.setModel(model);
          }
        });
    this.add(refreshButton, c);
 
    clearButton = new JButton("Filter:");
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 0.1;
    c.anchor = GridBagConstraints.LINE_END;
    c.insets = new Insets(5, 5, 2, 2);
    clearButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            textField.setText("");
            clearButton.setText("Filter:");
          }
        });
    this.add(clearButton, c);
    
    textField = new JTextField(60);
    c.gridx = 2;
    c.gridy = 0;
    c.weightx = 0.9;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(5, 5, 2, 2);
    this.add(textField, c);

    textField
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {

              public void changedUpdate(DocumentEvent e) {
                String text = textField.getText();
                clearButton.setText("Clear:");

                if (text.trim().length() == 0) {
                  rowSorter.setRowFilter(null);
                } else {
                  rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
              }

              public void removeUpdate(DocumentEvent e) {
                String text = textField.getText();
                clearButton.setText("Clear:");

                if (text.trim().length() == 0) {
                  rowSorter.setRowFilter(null);
                } else {
                  rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
              }

              public void insertUpdate(DocumentEvent e) {
                String text = textField.getText();
                clearButton.setText("Clear:");

                if (text.trim().length() == 0) {
                  rowSorter.setRowFilter(null);
                } else {
                  rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
              }
            });

  }

  /*
  public JTable getTable() {
    return table;
  }

  public void updatePanel() {
    JTable table = parent.getDatabaseManager().getProjectTableData();
    TableModel model = table.getModel();
    this.table.setModel(model);
  }

  public void tableChanged(TableModelEvent e) {
    int row = e.getFirstRow();
    int column = e.getColumn();
    TableModel model = (TableModel) e.getSource();
    String columnName = model.getColumnName(column);
    Object data = model.getValueAt(row, column);

    model.setValueAt(false, row, 0);

    // Do something with the data...
  }
  */
  /*
  class SharedListSelectionHandler implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
      ListSelectionModel lsm = (ListSelectionModel) e.getSource();

      int firstIndex = e.getFirstIndex();
      int lastIndex = e.getLastIndex();
      boolean isAdjusting = e.getValueIsAdjusting();
        LOGGER.info(
      //   "Event for indexes "
              + firstIndex
              + " - "
              + lastIndex
              + "; isAdjusting is "
              + isAdjusting
              + "; selected indexes:");

      if (lsm.isSelectionEmpty()) {
        LOGGER.info(" <none>");
      } else {
        if (!isAdjusting) {
          // Find out which indexes are selected.
          int minIndex = lsm.getMinSelectionIndex();
          int maxIndex = lsm.getMaxSelectionIndex();
          for (int i = minIndex; i <= maxIndex; i++) {
            if (lsm.isSelectedIndex(i)) {
              LOGGER.info(" " + i);
              javax.swing.table.TableModel tm = ProjectPanel.this.getTable().getModel();
              if ((boolean) tm.getValueAt(i, 0)) tm.setValueAt(false, i, 0);
              else tm.setValueAt(true, i, 0);
              ProjectPanel.this.table.setModel(tm);
            }
          }
        }
      }
    }
  }

  */
}
