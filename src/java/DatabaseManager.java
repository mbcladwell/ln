package ln;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import clojure.java.api.Clojure;
import clojure.lang.IFn;


/** */
public class DatabaseManager {
    Connection conn;
  // CustomTable table;
    DatabaseInserter dbInserter;
    DatabaseRetriever dbRetriever;
    DialogMainFrame dmf;
    String source; // The connection source e.g. local, heroku
    // Session session;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
private IFn require = Clojure.var("clojure.core", "require");

  // psql -U ln_admin -h 192.168.1.11 -d lndb
  // psql postgres://klohymim:hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_@raja.db.elephantsql.com:5432/klohymim

  /**
   * Use 'lndb' as the database name. Regular users will connect as ln_user and will have restricted
   * access (no delete etc.). Connect as ln_admin to get administrative privileges.
   */
  public DatabaseManager() {
      //LOGGER.info("in session: " + _s);
    require.invoke(Clojure.read("ln.codax-manager"));
    IFn getDBuser = Clojure.var("ln.codax-manager", "get-db-user");
    IFn getDBpassword = Clojure.var("ln.codax-manager", "get-db-password");
    
    // IFn setAuthenticated = Clojure.var("ln.codax-manager", "set-authenticated");
    IFn getDbType = Clojure.var("ln.codax-manager", "get-dbtype");
    String dbtype = (String)getDbType.invoke();
    Properties props = new Properties();
      Long insertKey = 0L;
      try {
	  if(dbtype.equals("postgres")){
	  Class.forName("org.postgresql.Driver");
	  }else{
	  Class.forName("com.mysql.jdbc.Driver");		
	  }
	    
	  props.setProperty("user", ((String)getDBuser.invoke()));
	  props.setProperty("password", ((String)getDBpassword.invoke()));
	  IFn getSource = Clojure.var("ln.codax-manager", "get-source");
	  IFn getURL = Clojure.var("ln.codax-manager", "get-connection-string");
   
	  String target = (String)getSource.invoke();
 	  String url = (String)getURL.invoke(target);
	  System.out.println(url);
	  conn = DriverManager.getConnection(url, props);	
      

      //This is the first initialization of  DatabaseRetriever, DatabaseInserter
      dbRetriever = new DatabaseRetriever(this);
      dbInserter = new DatabaseInserter(this);
       dmf = new DialogMainFrame(this);
    } catch (ClassNotFoundException e) {
      LOGGER.severe("Class not found: " + e);
    } catch (SQLException sqle) {
      LOGGER.severe("SQL exception: " + sqle);
    }
  }



  public DefaultTableModel buildTableModel(ResultSet _rs) {

    try {
      ResultSet rs = _rs;
      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();

      Vector<Vector<Object>> data = new Vector<Vector<Object>>();
      Vector<String> columnNames = new Vector<String>();
      /*
      String[] columnNames = new String[columnCount];
      for (int column = 0; column < columnCount; column++) {
        columnNames[column] = metaData.getColumnName(column + 1);
      }
      */
      for (int column = 0; column < columnCount; column++) {
        columnNames.addElement(metaData.getColumnName(column + 1));
      }

      // data of the table
      while (rs.next()) {
        Vector<Object> vector = new Vector<Object>();

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
          vector.add(rs.getObject(columnIndex + 1));
        }
        data.add(vector);
      }
      // LOGGER.info("data: " + data);
      return new DefaultTableModel(data, columnNames);

      //          data.stream().map(List::toArray).toArray(Object[][]::new), columnNames);

    } catch (SQLException sqle) {
      LOGGER.severe("SQLException in buildTableModel: " + sqle);
    }

    return null;
  }

  /**
   * ******************************************************************
   *
   * <p>Generic DB activities
   *
   * <p>****************************************************************
   */
  // public void insertPreparedStatement(PreparedStatement _preparedStatement) {
  //   PreparedStatement preparedStatement = _preparedStatement;
  //   //  LOGGER.info(preparedStatement.toString());

  //   try {
  //     preparedStatement.executeUpdate();

  //   } catch (SQLException sqle) {
  //     LOGGER.warning("Failed to execute prepared statement: " + preparedStatement.toString());
  //     LOGGER.warning("Exception: " + sqle);
  //   }
  // }

  /** TableModel Columns: PSID Name Descr Format  called from the PlateSet menu item "group" */
  public void groupPlateSets(JTable _table) {
    // 4 columns in the plate set table
    JTable plate_set_table = _table;
    TableModel tableModel = plate_set_table.getModel();
    int[] selection = plate_set_table.getSelectedRows();
    String[][] results = new String[selection.length][4];

    //  LOGGER.info("selection: " + selection.toString());
    ArrayList<String> plateSet = new ArrayList<String>();
    Set<String> plateFormatSet = new HashSet<String>();

    for (int i = 0; i < selection.length; i++) {
      for (int j = 0; j < 4; j++) {
        results[i][j] = tableModel.getValueAt(selection[i], j).toString();
        // LOGGER.info("i: " + i + " j: " + j + " results[i][j]: " + results[i][j]);
      }
    }
    for (int k = 0; k < selection.length; k++) {
      plateSet.add(results[k][0]);
      // LOGGER.info("prjID: " + results[k][0]);

      plateFormatSet.add(results[k][2]);
      // LOGGER.info("pltformat: " + results[k][2]);
    }
    LOGGER.info("Size of plateFormatSet: " + plateFormatSet.size());
    if (plateFormatSet.size() == 1) {
      HashMap<String, String> numberOfPlatesInPlateSets =
          dbRetriever.getNumberOfPlatesInPlateSets(plateSet);
      String format = new String();
      for (Iterator<String> it = plateFormatSet.iterator(); it.hasNext(); ) {
        format = it.next();
      }
      new DialogGroupPlateSet(this, numberOfPlatesInPlateSets, format, plateSet);
    } else {
      JOptionPane.showMessageDialog(
          dmf,
          "Plate sets to be grouped must be of the same formats\n and of the same layout!",
          "Error!",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Launched by the Plate menu item "group". Since by definition a plate set can only have one format of
   * plate, no need to check that there is only one format
   */
  public void groupPlates(CustomTable _table) {
    // 4 columns in the plate  table
    // plate_sys_name Order type format
    CustomTable plate_table = _table;
    TableModel tableModel = plate_table.getModel();
    int[] selection = plate_table.getSelectedRows();
    String[][] results = new String[selection.length][4];
    String numberOfPlates = Integer.valueOf(selection.length).toString();

    //  LOGGER.info("selection: " + selection.toString());
    Set<String> plateSet = new HashSet<String>();

    for (int i = 0; i < selection.length; i++) {
      for (int j = 0; j < 4; j++) {
        results[i][j] = tableModel.getValueAt(selection[i], j).toString();
        // LOGGER.info("i: " + i + " j: " + j + " results[i][j]: " + results[i][j]);
      }
    }
    for (int k = 0; k < selection.length; k++) {
      plateSet.add(results[k][0]);
      // LOGGER.info("prjID: " + results[k][0]);

    }
    String format = new String();
    try{
    format = results[1][3];
    new DialogGroupPlates(this, plateSet, format);
    }catch(ArrayIndexOutOfBoundsException aiob){
	  JOptionPane.showMessageDialog(
          dmf,
          "Select multiple plates!",
          "Error!",
          JOptionPane.ERROR_MESSAGE);
	  
    }
    
  }

     /**
      * Collapse multiple plates by quadrant. 
      *
      *<p> Plate set table:  id|plate_set_name|descr| plate_set_sys_name | num_plates|
      *plate_format_id|plate_type_id|project_id |updated     
      *
      * Passing around dbm makes this difficult to convert to clojure       
      */
    public void reformatPlateSet(CustomTable _table){
    CustomTable plate_set_table = _table;
    TableModel tableModel = plate_set_table.getModel();
    int[] selection = plate_set_table.getSelectedRows();
    if (selection.length > 1){
       JOptionPane.showMessageDialog(dmf,
    "Select one plate set.",
    "Error",
    JOptionPane.ERROR_MESSAGE);
    }else{
       
	String format = (String)tableModel.getValueAt(selection[0], 2).toString();
	    String[] plate_set_sys_name = new String[1];
	    plate_set_sys_name[0] = tableModel.getValueAt(selection[0], 0).toString();
	    String descr = (String)tableModel.getValueAt(selection[0], 6);
	    int num_plates = (int)tableModel.getValueAt(selection[0], 3);
	    String plate_type = (String)tableModel.getValueAt(selection[0], 4);

	    require.invoke(Clojure.read("ln.db-inserter"));
	    //IFn prepForDialogReformatPlateSet  = Clojure.var("ln.db-inserter", "prep-for-dialog-reformat-plate-set");
	    //prepForDialogReformatPlateSet.invoke(  plate_set_sys_name[0], descr, num_plates, plate_type, format);

	    IFn getIDsForSysNames = Clojure.var("ln.db-inserter", "get-ids-for-sys-names");
	    
	    // Integer[] plate_set_id = getIDsForSysNames.invoke(plate_set_sys_name, "plate_set", "plate_set_sys_name");
	    //Integer[] plate_set_id = dbRetriever.getIDsForSysNames(plate_set_sys_name, "plate_set", "plate_set_sys_name");
	    //int[] array = list.stream().mapToInt(i->i).toArray();
	    clojure.lang.PersistentVector  pv = (clojure.lang.PersistentVector)getIDsForSysNames.invoke(plate_set_sys_name, "plate_set", "plate_set_sys_name");
	    //	    int[] plate_set_id = pv.stream().mapToInt((clojure.lang.PersistentVector i)->i).toArray();
	    Object[] plate_set_id_pre = pv.toArray();
	    int plate_set_id = ((java.math.BigInteger)plate_set_id_pre[0]).intValue();
	    
	    require.invoke(Clojure.read("ln.db-retriever"));
	    IFn getNumSamplesForPlateSetID  = Clojure.var("ln.db-inserter", "get-num-samples-for-plate-set-id");
	    int num_samples = (int)getNumSamplesForPlateSetID.invoke(plate_set_id);
	    //int num_samples = this.getDatabaseRetriever().getNumberOfSamplesForPlateSetID(plate_set_id[0]);
     	    int plate_layout_name_id = this.getDatabaseRetriever().getPlateLayoutNameIDForPlateSetID(plate_set_id);
    // 	    LOGGER.info("plate_set_id[0]: " + plate_set_id[0]);
    	    switch(format){
    	    case "96":
    			DialogReformatPlateSet drps = new DialogReformatPlateSet( this, plate_set_id, plate_set_sys_name[0], descr, num_plates, num_samples, plate_type, format, plate_layout_name_id);		
    		    break;
    	    case "384":	 drps = new DialogReformatPlateSet( this, plate_set_id, plate_set_sys_name[0], descr, num_plates, num_samples, plate_type, format, plate_layout_name_id);
		
    		    break;
    	    case "1536":  JOptionPane.showMessageDialog(dmf,
    "1536 well plates can not be reformatted.",
    "Error", JOptionPane.ERROR_MESSAGE);
    		    break;}		    
    }}

    /*    
  public DialogMainFrame getDmf() {
    return this.dmf;
  }
    */

    /**
     * In DatabaseManager (instead of DatabaseRetriever) because this is an early query
     * prior to instantiation of DatabaseRetriever.
     */
  // public int getUserIDForUserName(String _user_name) {
  //   String user_name = _user_name;
  //   // int plate_set_id;

  //   try {
  //     PreparedStatement pstmt =
  //         conn.prepareStatement(
  //             "SELECT lnuser.id FROM lnuser WHERE lnuser_name = ?;");

  //     pstmt.setString(1, user_name);
  //     ResultSet rs = pstmt.executeQuery();
  //     rs.next();
  //     int lnuser_id = Integer.valueOf(rs.getString("id"));

  //     // LOGGER.info("result: " + plate_set_id);
  //     rs.close();
  //     pstmt.close();
  //     return lnuser_id;

  //   } catch (SQLException sqle) {
  //     LOGGER.severe("SQL exception getting plateset_id: " + sqle);
  //   }
  //   int dummy = -1;
  //   return dummy;
  // }

        /**
     * In DatabaseManager (instead of DatabaseRetriever) because this is an early query
     * prior to instantiation of DatabaseRetriever.
     */
  // public String getUserGroupForUserName(String _user_name) {
  //   String user_name = _user_name;
  //   // int plate_set_id;

  //   try {
  //     PreparedStatement pstmt =
  //         conn.prepareStatement(
  //             "SELECT lnuser_groups.usergroup FROM lnuser, lnuser_groups WHERE lnuser.lnuser_name = ? AND lnuser.usergroup=lnuser_groups.id;");

  //     pstmt.setString(1, user_name);
  //     ResultSet rs = pstmt.executeQuery();
  //     rs.next();
  //     String usergroup = rs.getString("usergroup");

  //     // LOGGER.info("result: " + plate_set_id);
  //     rs.close();
  //     pstmt.close();
  //     return usergroup;

  //   } catch (SQLException sqle) {
  //     LOGGER.severe("SQL exception getting plateset_id: " + sqle);
  //   }
  //   String dummy = "error";
  //   return dummy;
  // }

    public DialogMainFrame getDialogMainFrame(){
	return this.dmf;
    }
    
  public DatabaseInserter getDatabaseInserter() {
    return this.dbInserter;
  }

  public DatabaseRetriever getDatabaseRetriever() {
    return this.dbRetriever;
  }

  public Connection getConnection() {
    return this.conn;
  }

 
}
