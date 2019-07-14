package pm;

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
import org.postgresql.Driver;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/** */
public class DatabaseManager {
  Connection conn;
  // CustomTable table;
     DatabaseInserter dbInserter;
    DatabaseRetriever dbRetriever;
  DialogMainFrame dmf;
    Session session;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  // psql -U ln_admin -h 192.168.1.11 -d lndb


  /**
   * Use 'lndb' as the database name. Regular users will connect as ln_user and will have restricted
   * access (no delete etc.). Connect as ln_admin to get administrative privileges.
   */
  public DatabaseManager(Session _s) {
      //LOGGER.info("in session: " + _s);
      session=_s;
      Long insertKey = 0L;
      try {
	  Class.forName("org.postgresql.Driver");

	  // String url = "jdbc:postgresql://localhost/postgres";
	  String url = session.getURL();
	  Properties props = new Properties();
	  props.setProperty("user", "ln_admin");
	  props.setProperty("password", "welcome");

	  conn = DriverManager.getConnection(url, props);
	  //LOGGER.info("conn: " + conn);
	  PreparedStatement pstmt = conn.prepareStatement(
              //  "SELECT password = crypt( ?,password) FROM pmuser WHERE pmuser_name = ?;");
              "SELECT password = ?, password FROM lnuser WHERE lnuser_name = ?;");
	  pstmt.setString(1, session.getPassword());
	  pstmt.setString(2, session.getUserName());
	  //LOGGER.info("pstmnt: " + pstmt);
	  ResultSet rs = pstmt.executeQuery();
	  rs.next();
	  boolean pass = rs.getBoolean(1);

	  rs.close();
	  pstmt.close();

      if (pass) {
	  
	  session.setUserID( getUserIDForUserName(session.getUserName()));
	  session.setUserGroup( getUserGroupForUserName(session.getUserName()));
	  session.setAuthenticated( true);
        
        String insertSql =
	    "INSERT INTO lnsession (lnuser_id) values (?);";
       
	// "INSERT INTO lnsession (lnuser_id) SELECT id FROM lnuser WHERE lnuser_name = ?;";
        PreparedStatement insertPs =
            conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
        insertPs.setInt(1, session.getUserID());
        insertPs.executeUpdate();
        ResultSet rsKey = insertPs.getGeneratedKeys();

        if (rsKey.next()) {
          insertKey = rsKey.getLong(1);
	  session.setSessionID(insertKey.intValue());

          // LOGGER.info("rsKey: " + insertKey);
        }

      } else {
        LOGGER.info("Authentication failed, no session generated.");

	
      }

      //This is the first initialization of  DatabaseRetriever, DatabaseInserter
      dbRetriever = new DatabaseRetriever(this);
      dbInserter = new DatabaseInserter(this);
    } catch (ClassNotFoundException e) {
      LOGGER.severe("Class not found: " + e);
    } catch (SQLException sqle) {
      LOGGER.severe("SQL exception: " + sqle);
    }
  }


  public void updateSessionWithProject(String _project_sys_name) {
    int results = 0;
    String project_sys_name = _project_sys_name;
    session.setProjectSysName(project_sys_name);

    try {
      String query =
          new String("SELECT id FROM project WHERE project_sys_name = '" + project_sys_name + "';");
      Statement st = conn.createStatement();
      ResultSet rs = st.executeQuery(query);
      rs.next();
      results = rs.getInt("id");
      // LOGGER.info("projectID: " + results);
      this.getSession().setProjectID(results);

      rs.close();
      st.close();
    } catch (SQLException sqle) {
      LOGGER.warning("Failed to properly prepare  prepared statement: " + sqle);
    }
  }
    /*
  public CustomTable getPlateTableData(String _plate_set_sys_name) {
    try {
      PreparedStatement pstmt =
          conn.prepareStatement(
              "SELECT plate.plate_sys_name AS \"PlateID\", plate_plate_set.plate_order AS \"Order\",  plate_type.plate_type_name As \"Type\", plate_format.format AS \"Format\" FROM plate_set, plate, plate_type, plate_format, plate_plate_set WHERE plate_plate_set.plate_set_id = (select id from plate_set where plate_set_sys_name like ?) AND plate.plate_type_id = plate_type.id AND plate_plate_set.plate_id = plate.id AND plate_plate_set.plate_set_id = plate_set.id  AND plate_format.id = plate.plate_format_id ORDER BY plate_plate_set.plate_order DESC;");
      pstmt.setString(1, _plate_set_sys_name);
      LOGGER.info("statement: " + pstmt.toString());
      ResultSet rs = pstmt.executeQuery();

      CustomTable table = new CustomTable(dmf, buildTableModel(rs));
      LOGGER.info("Got plate table " + table.getSelectedRowsAndHeaderAsStringArray().toString());
      rs.close();
      pstmt.close();
      return table;

    } catch (SQLException sqle) {
	LOGGER.info("Exception in dbm.getPlateTableData: " + sqle);
    }
    return null;
  }

  public CustomTable getWellTableData(String _plate_sys_name) {
    try {
      PreparedStatement pstmt =
          conn.prepareStatement(
   
      "SELECT plate.plate_sys_name AS \"PlateID\", well_numbers.well_name AS \"Well\", well.by_col AS \"Well_NUM\", sample.sample_sys_name AS \"Sample\", sample.accs_id as \"Accession\" FROM  plate, sample, well_sample, well JOIN well_numbers ON ( well.by_col= well_numbers.by_col)  WHERE plate.id = well.plate_id AND well_sample.well_id=well.id AND well_sample.sample_id=sample.id AND well.plate_id = (SELECT plate.id FROM plate WHERE plate.plate_sys_name = ?) AND  well_numbers.plate_format = (SELECT plate_format_id  FROM plate_set WHERE plate_set.ID =  (SELECT plate_set_id FROM plate_plate_set WHERE plate_id = plate.ID LIMIT 1) ) ORDER BY well.by_col DESC;");


      
      pstmt.setString(1, _plate_sys_name);
      ResultSet rs = pstmt.executeQuery();

      CustomTable table = new CustomTable(dmf, buildTableModel(rs));
      rs.close();
      pstmt.close();
      return table;
    } catch (SQLException sqle) {
      LOGGER.severe("Failed to retrieve well data: " + sqle);
    }
    return null;
  }
*/

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
  public void insertPreparedStatement(PreparedStatement _preparedStatement) {
    PreparedStatement preparedStatement = _preparedStatement;
    //  LOGGER.info(preparedStatement.toString());

    try {
      preparedStatement.executeUpdate();

    } catch (SQLException sqle) {
      LOGGER.warning("Failed to execute prepared statement: " + preparedStatement.toString());
      LOGGER.warning("Exception: " + sqle);
    }
  }

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
      new DialogGroupPlateSet(session, numberOfPlatesInPlateSets, format, plateSet);
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
    format = results[1][3];
    new DialogGroupPlates(dmf, plateSet, format);
  }

     /**
      * Collapse multiple plates by quadrant. 
      *
      *<p> Plate set table:  id|plate_set_name|descr| plate_set_sys_name | num_plates|
      *plate_format_id|plate_type_id|project_id |updated            
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
	    Integer[] plate_set_id = this.getDatabaseRetriever().getIDsForSysNames(plate_set_sys_name, "plate_set", "plate_set_sys_name");
	    String descr = (String)tableModel.getValueAt(selection[0], 4);
	    int num_plates = (int)tableModel.getValueAt(selection[0], 3);
	    String plate_type = (String)tableModel.getValueAt(selection[0], 4);
	    int num_samples = this.getDatabaseRetriever().getNumberOfSamplesForPlateSetID(plate_set_id[0]);
	    int plate_layout_name_id = this.getDatabaseRetriever().getPlateLayoutNameIDForPlateSetID((int)plate_set_id[0]);
	    LOGGER.info("plate_set_id[0]: " + plate_set_id[0]);
	    switch(format){
	    case "96":
			DialogReformatPlateSet drps = new DialogReformatPlateSet( session, (int)plate_set_id[0], plate_set_sys_name[0], descr, num_plates, num_samples, plate_type, format, plate_layout_name_id);
		
		    break;
	    case "384":	 drps = new DialogReformatPlateSet( session, (int)plate_set_id[0], plate_set_sys_name[0], descr, num_plates, num_samples, plate_type, format, plate_layout_name_id);
		
		    break;
	    case "1536":  JOptionPane.showMessageDialog(dmf,
    "1536 well plates can not be reformatted.",
    "Error", JOptionPane.ERROR_MESSAGE);
		    break;
		    
		    }	
    }
    }

    /*    
  public DialogMainFrame getDmf() {
    return this.dmf;
  }
    */

    /**
     * In DatabaseManager (instead of DatabaseRetriever) because this is an early query
     * prior to instantiation of DatabaseRetriever.
     */
  public int getUserIDForUserName(String _user_name) {
    String user_name = _user_name;
    // int plate_set_id;

    try {
      PreparedStatement pstmt =
          conn.prepareStatement(
              "SELECT lnuser.id FROM lnuser WHERE lnuser_name = ?;");

      pstmt.setString(1, user_name);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      int lnuser_id = Integer.valueOf(rs.getString("id"));

      // LOGGER.info("result: " + plate_set_id);
      rs.close();
      pstmt.close();
      return lnuser_id;

    } catch (SQLException sqle) {
      LOGGER.severe("SQL exception getting plateset_id: " + sqle);
    }
    int dummy = -1;
    return dummy;
  }

        /**
     * In DatabaseManager (instead of DatabaseRetriever) because this is an early query
     * prior to instantiation of DatabaseRetriever.
     */
  public String getUserGroupForUserName(String _user_name) {
    String user_name = _user_name;
    // int plate_set_id;

    try {
      PreparedStatement pstmt =
          conn.prepareStatement(
              "SELECT lnuser_groups.usergroup FROM lnuser, lnuser_groups WHERE lnuser.lnuser_name = ? AND lnuser.usergroup=lnuser_groups.id;");

      pstmt.setString(1, user_name);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      String usergroup = rs.getString("usergroup");

      // LOGGER.info("result: " + plate_set_id);
      rs.close();
      pstmt.close();
      return usergroup;

    } catch (SQLException sqle) {
      LOGGER.severe("SQL exception getting plateset_id: " + sqle);
    }
    String dummy = "error";
    return dummy;
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

  public Session getSession() {
    return session;
  }
}
