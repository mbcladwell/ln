package ln;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

/** 
 * executeUpdate expects no returns!!!
*/
public class DatabaseInserter {
  DatabaseManager dbm;
  DialogMainFrame dmf;
  // private DatabaseRetriever dbr;
  Connection conn;
  JTable table;
  Utilities utils;
    int session_id; 
    //  Session session;
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private IFn require = Clojure.var("clojure.core", "require");

  /** */
  public DatabaseInserter(DatabaseManager _dbm) {
    this.dbm = _dbm;
    this.conn = dbm.getConnection();
    // this.dbr = dbm.getDatabaseRetriever();
    //session = dbm.getSession();
    this.dmf = dbm.getDialogMainFrame();
    require.invoke(Clojure.read("ln.db-inserter"));
    require.invoke(Clojure.read("ln.codax-manager"));
    IFn getSessionID = Clojure.var("ln.codax-manager", "get-session-id");
    session_id = ((Long)getSessionID.invoke()).intValue();
    // this.utils = dmf.getUtilities();
    //this.session = dmf.getSession();
  }

  public void insertProject(String _name, String _description, int _lnuser_id) {

    String insertSql = "SELECT new_project(?, ?, ?);";
    PreparedStatement insertPs;

    try {
      insertPs = conn.prepareStatement(insertSql);
      insertPs.setString(1, _description);
      insertPs.setString(2, _name);
      insertPs.setInt(3, _lnuser_id);
      int i = insertPs.executeUpdate();
   
      //      insertPreparedStatement(insertPs);
    } catch (SQLException sqle) {
      LOGGER.warning("Failed to properly prepare  prepared statement: " + sqle);
    }
  }

  public void updateProject(String _name, String _description, String _project_sys_name) {

    String sqlstring = "UPDATE project SET project_name = ?, descr = ? WHERE project_sys_name = ?;";
    PreparedStatement preparedStatement;
    try {
      preparedStatement = conn.prepareStatement(sqlstring, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, _name);
      preparedStatement.setString(2, _description);
      preparedStatement.setString(3, _project_sys_name);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      LOGGER.warning("Failed to properly prepare  prepared statement: " + sqle);
    }
  }

  public void updatePlateSet(String _name, String _description, String _plate_set_sys_name) {

    String sqlstring = "UPDATE plate_set SET plate_set_name = ?, descr = ? WHERE plate_set_sys_name = ?;";
    PreparedStatement preparedStatement;
    try {
      preparedStatement = conn.prepareStatement(sqlstring, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, _name);
      preparedStatement.setString(2, _description);
      preparedStatement.setString(3, _plate_set_sys_name);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      LOGGER.warning("Failed to properly prepare  prepared statement: " + sqle);
    }
  }

    
  public void insertPreparedStatement(PreparedStatement _preparedStatement) {
    PreparedStatement preparedStatement = _preparedStatement;
    LOGGER.info(preparedStatement.toString());

    try {
      preparedStatement.executeUpdate();

    } catch (SQLException sqle) {
      LOGGER.warning("Failed to execute prepared statement: " + preparedStatement.toString());
      LOGGER.warning("Exception: " + sqle);
    }
  }

  public static DefaultTableModel buildTableModel(ResultSet _rs) throws SQLException {

    ResultSet rs = _rs;
    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();
    Vector<String> columnNames = new Vector<String>();
    for (int column = 1; column <= columnCount; column++) {
      columnNames.add(metaData.getColumnName(column));
    }

    // data of the table
    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    while (rs.next()) {
      Vector<Object> vector = new Vector<Object>();
      for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
        vector.add(rs.getObject(columnIndex));
      }
      data.add(vector);
    }

    return new DefaultTableModel(data, columnNames);
  }

    
  /**
   * called from DialogGroupPlateSet; Performs the creation of a new plate set from existing plate
   * sets. The HashMap contains the pair plateset_sys_name:number of plates. A dedicated Postgres
   * function "new_plate_set_from_group" will create the plateset and return the id without making
   * any plates, nor will it associate the new plate_set.id with plates.
   *
   *<p>Note that the method groupPlatesIntoPlateSet is for grouping individual plates
   */
  public void groupPlateSetsIntoNewPlateSet(
      String _description,
      String _name,
      HashMap<String, String> _plate_set_num_plates,
      String _plate_format,
      String _plate_type,
      int _project_id,
      int _plate_layout_name_id,
      ArrayList<String> _plate_sys_names) {

    String description = _description;
    String name = _name;
    HashMap<String, String> plate_set_num_plates = _plate_set_num_plates;
    String plate_format = _plate_format;
    String plate_type = _plate_type;
    int project_id = _project_id;
    int format_id = 0;
    int new_plate_set_id = 0;
    int plate_layout_name_id = _plate_layout_name_id;
    ArrayList<String> plate_sys_names = _plate_sys_names;
  
    // determine total number of plates in new plate set
    int total_num_plates = 0;

    Iterator<Map.Entry<String,String>> it = plate_set_num_plates.entrySet().iterator();
    while (it.hasNext()) {
	HashMap.Entry<String, String> pair = (HashMap.Entry<String, String>) it.next();
      total_num_plates = total_num_plates + Integer.parseInt((String) pair.getValue());
      // it.remove(); // avoids a ConcurrentModificationException
    }
    // LOGGER.info("total: " + total_num_plates);

    // determine format id
    LOGGER.info("format: " + plate_format);

    format_id = Integer.parseInt(plate_format);
    //    format_id = dbr.getPlateFormatID(plate_format);

    // determine type id
    int plateTypeID = dbm.getDatabaseRetriever().getIDForPlateType(plate_type);

 
    //LOGGER.info(
    //  "set: "
    //      + session.getDialogMainFrame().getUtilities().getStringArrayForStringSet(new HashSet<String>(plate_sys_names)));

    // Integer[] plate_ids =
    //     dbm.getDatabaseRetriever()
    //         .getIDsForSysNames(
    // 			       dbm.getDialogMainFrame().getUtilities().getStringArrayForStringSet(new HashSet<String>(plate_sys_names)),
    //             "plate",
    //             "plate_sys_name");
    
    IFn getIDsForSysNames = Clojure.var("ln.db-inserter", "get-ids-for-sys-names");	    
    Integer[] plate_ids =(Integer[]) getIDsForSysNames.invoke(dbm.getDialogMainFrame().getUtilities().getStringArrayForStringSet(new HashSet<String>(plate_sys_names)), "plate", "plate_sys_name");
	   
    String sqlstring = "SELECT new_plate_set_from_group (?, ?, ?, ?, ?, ?, ?);";

    try {
      PreparedStatement preparedStatement =
          conn.prepareStatement(sqlstring, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, description);
      preparedStatement.setString(2, name);
      preparedStatement.setInt(3, total_num_plates);
      preparedStatement.setInt(4, format_id);
      preparedStatement.setInt(5, plateTypeID);
      preparedStatement.setInt(6, project_id);
       preparedStatement.setInt(7, plate_layout_name_id);
       preparedStatement.execute(); // executeUpdate expects no returns!!!

      ResultSet resultSet = preparedStatement.getResultSet();
      resultSet.next();
      new_plate_set_id = resultSet.getInt("new_plate_set_from_group");
      // LOGGER.info("resultset: " + result);

    } catch (SQLException sqle) {
      LOGGER.warning("SQLE at inserting plate set from group: " + sqle);
    }

    // associate old (existing) plates with new plate set id
    //

    Set<Integer> all_plate_ids = new HashSet<Integer>();
    Iterator<Map.Entry<String,String>> it2 = plate_set_num_plates.entrySet().iterator();
    while (it2.hasNext()) {
	HashMap.Entry<String,String> pair = (HashMap.Entry<String,String>) it2.next();
      int plate_set_id =
          dbm.getDatabaseRetriever().getPlateSetIDForPlateSetSysName((String) pair.getKey());
      all_plate_ids.addAll(dbm.getDatabaseRetriever().getAllPlateIDsForPlateSetID(plate_set_id));
      it2.remove(); // avoids a ConcurrentModificationException
    }

    // LOGGER.info("keys: " + all_plate_ids);
    //LOGGER.info("new_plate_set_id: " + new_plate_set_id);

     IFn assocPlateIDsWithPlateSetID = Clojure.var("ln.db-inserter", "assoc-plate-ids-with-plate-set-id");
    assocPlateIDsWithPlateSetID.invoke(all_plate_ids, new_plate_set_id);

    
    //this.associatePlateIDsWithPlateSetID(all_plate_ids, new_plate_set_id);
     IFn getProjectSysName = Clojure.var("ln.codax-manager", "get-project-sys-name");
   
     dbm.getDialogMainFrame().showPlateSetTable((String)getProjectSysName.invoke());
  }

  /** Called from DialogGroupPlates from the plate panel/menubar */
  public void groupPlatesIntoPlateSet(
      String _description,
      String _name,
      Set<String> _plates,
      String _format,
      String _type,
      int _projectID,
      int _plate_layout_name_id) {
    String description = _description;
    String name = _name;
    Set<String> plates = _plates;
    String format = _format;
    String type = _type;
    int projectID = _projectID;
    int new_plate_set_id = 0;
    int plate_layout_name_id = _plate_layout_name_id;
    // ResultSet resultSet;
    // PreparedStatement preparedStatement;

    int format_id = Integer.parseInt(format);
        

    int plateTypeID = dbm.getDatabaseRetriever().getIDForPlateType(type);
    int num_plates = plates.size();

    String sqlstring = "SELECT new_plate_set_from_group (?, ?, ?, ?, ?, ?, ?);";

    try {
      PreparedStatement preparedStatement =
          conn.prepareStatement(sqlstring, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, description);
      preparedStatement.setString(2, name);
      preparedStatement.setInt(3, num_plates);
      preparedStatement.setInt(4, format_id);
      preparedStatement.setInt(5, plateTypeID);
      preparedStatement.setInt(6, projectID);
      preparedStatement.setInt(7, plate_layout_name_id);
      

      preparedStatement.execute(); // executeUpdate expects no returns!!!

      ResultSet resultSet = preparedStatement.getResultSet();
      resultSet.next();
      new_plate_set_id = resultSet.getInt("new_plate_set_from_group");
      LOGGER.info(" new_plate_set_id: " + new_plate_set_id);

    } catch (SQLException sqle) {
      LOGGER.warning("SQLE at inserting plate set from group: " + sqle);
    }

    // associate old plates with new plate set id
    Set<Integer> plate_ids = new HashSet<Integer>();
    for (String temp : plates) {
      plate_ids.add(dbm.getDatabaseRetriever().getIDForSysName(temp, "plate"));
    }

    LOGGER.info("keys: " + plate_ids);
    LOGGER.info("new_plate_set_id: " + new_plate_set_id);
    

     IFn assocPlateIDsWithPlateSetID = Clojure.var("ln.db-inserter", "assoc-plate-ids-with-plate-set-id");
    assocPlateIDsWithPlateSetID.invoke(plate_ids, new_plate_set_id);

    //this.associatePlateIDsWithPlateSetID(plate_ids, new_plate_set_id);
     IFn getProjectSysName = Clojure.var("ln.codax-manager", "get-project-sys-name");
   
    dbm.getDialogMainFrame().showPlateSetTable((String)getProjectSysName.invoke());
  }


    /* Method signature in DialogAddPlateSetData
      dbi.associateDataWithPlateSet(
          nameField.getText(),
          descrField.getText(),
          plate_set_sys_name,
          (ComboItem) assayTypes.getSelectedItem().getKey(),
          (ComboItem) plateLayouts.getSelectedItem().getKey(),
          session.getDialogMainFrame().getUtilities().loadDataFile(fileField.getText()),
          checkBox.isSelected()
          (ComboItem) algoritmList.getSelectedItem().getKey());
  */

  /** Called from DialogAddPlateSetData */
  public void associateDataWithPlateSet(
      String _assayName,
      String _descr,
      String _plate_set_sys_name,
      int _format_id,
      int _assay_type_id,
      int _plate_layout_name_id,
      // ArrayList<String[]> _table,
      String _file_name,
      boolean _auto_select_hits,
      int _hit_selection_algorithm,
      int _top_n_number) {

    String assayName = _assayName;
    String descr = _descr;
    int format_id = _format_id;
    String[] plate_set_sys_name = new String[1];
    plate_set_sys_name[0] = _plate_set_sys_name;

    int assay_type_id = _assay_type_id;
    int plate_layout_name_id = _plate_layout_name_id;
    //ArrayList<String[]> table = _table;
    String file_name = _file_name;
    boolean auto_select_hits = _auto_select_hits;
    int hit_selection_algorithm = _hit_selection_algorithm;
    int top_n_number = _top_n_number;
    int assay_run_id =0;
    IFn associateDataWithPlateSet = Clojure.var("ln.db-inserter", "associate-data-with-plate-set");
    // assay_run_id = (int)associateDataWithPlateSet.invoke(assayName, descr, plate_set_sys_name, format_id, assay_type_id,plate_layout_name_id, file_name, auto_select_hits, hit_selection_algorithm, top_n_number);

    //System.out.println("stuff: "  +  assayName + ", " +  descr + ", " +  plate_set_sys_name + ", " + format_id + ", " + assay_type_id + ", " + plate_layout_name_id + ", " + file_name + ", " + auto_select_hits + ", " + hit_selection_algorithm + ", "+ top_n_number);
    
    assay_run_id = (int)associateDataWithPlateSet.invoke(assayName, descr, plate_set_sys_name, format_id, assay_type_id,plate_layout_name_id, file_name, auto_select_hits, hit_selection_algorithm, top_n_number);
    

    
    //Now I need to select hits if requested by user.  I have the assay_run_id, and the algorithm for hit selection.
    // stored procedure: new_hit_list(_name VARCHAR, _descr VARCHAR, _num_hits INTEGER, _assay_run_id INTEGER, hit_list integer[])
    // DialogNewHitList(DialogMainFrame _session.getDialogMainFrame(), int  _assay_run_id, double[][] _selected_response, int _num_hits)
    // table = session.getDatabaseRetriever().getDataForScatterPlot(assay_run_id);
    // 	norm_response = new ResponseWrangler(table, ResponseWrangler.NORM);
   //    double[][]  sortedResponse [response] [well] [type_id] [sample_id];
    // selected_response.getHitsAboveThreshold(threshold))
 
    if(assay_run_id != 0){ 
	if(auto_select_hits){
	    ResponseWrangler rw = new ResponseWrangler(dbm.getDatabaseRetriever().getDataForScatterPlot(assay_run_id),ResponseWrangler.NORM);
	    double[][] sorted_response = rw.getSortedResponse();
	    int number_of_hits = 0;
	
	    switch(hit_selection_algorithm){
	    case 1: //Top N
		number_of_hits = top_n_number;
		break;
	    case 2: // mean(background) + 2SD
    
		number_of_hits =  rw.getHitsAboveThreshold(rw.getMean_neg_2_sd() );
		break;
	    case 3:  // mean(background) + 3SD
		number_of_hits =  rw.getHitsAboveThreshold(rw.getMean_neg_3_sd() );
		break;
	    case 4:  // >0% enhanced
		number_of_hits =  rw.getHitsAboveThreshold(rw.getMean_pos() );
		break;
	    
	    }
	    DialogNewHitList dnhl = new DialogNewHitList(dbm, assay_run_id, sorted_response, number_of_hits);	
	}
    }
    
  }

    
    /**
     * Called from AdminMenu
     */
    public void deleteProject(int _prj_id){
	int prj_id = _prj_id;
    String sqlstring = "delete from project  WHERE project.id = ?;";
    PreparedStatement preparedStatement;
    try {
      preparedStatement = conn.prepareStatement(sqlstring);
      preparedStatement.setInt(1, prj_id);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      LOGGER.warning("Failed to properly prepare  prepared statement: " + sqle);
    }
    dbm.getDialogMainFrame().showProjectTable();
  }


     
    //not used?
    // public void insertPlateLayout(String _name, String _descr,  String _file_name){
    // 	String name = _name;
    // 	String descr = _descr;
    // 	int format = 0;
    // 	ArrayList<String[]> data = dbm.getDialogMainFrame().getUtilities().loadDataFile(_file_name);

    // 	Object[][]  dataObject = dbm.getDialogMainFrame().getUtilities().getObjectArrayForArrayList(data); 

    // 	switch(data.size()-1){
    // 	case 96:
    // 	    format = 96;
    // 	    // ImportLayoutViewer ilv = new ImportLayoutViewer(dmf, dataObject);   
    // 	    break;
    // 	case 384:
    // 	    format = 384;
	    
    // 	    break;
    // 	case 1536:
    // 	    format = 1536;
	    
    // 	    break;
    // 	default:
    // 	    JOptionPane.showMessageDialog( dbm.getDialogMainFrame(), "Expecting 96, 384, or 1536 lines of data. Found " + (data.size()-1) +  "!", "Error", JOptionPane.ERROR_MESSAGE);	    
    // 	}
	      
    // 	    String sqlString = "SELECT new_plate_layout(?,?, ?, ?)";
    // // LOGGER.info("insertSql: " + insertSql);
    // try {
    //   PreparedStatement preparedStatement = conn.prepareStatement(sqlString);
    //   preparedStatement.setString(1, name);
    //   preparedStatement.setString(2, descr);
    //   preparedStatement.setInt(3, format);
    //   preparedStatement.setArray(4, conn.createArrayOf("VARCHAR", (data.toArray())));
    //   preparedStatement.execute(); // executeUpdate expects no returns!!!

    // } catch (SQLException sqle) {
    //   LOGGER.warning("Failed to properly prepare  prepared statement: " + sqle);
    // }
    // }


    /**
     * This method preps variable for displaying the dialog box
     */
    public void importAccessionsByPlateSet(int _plate_set_id){
	int plate_set_id = _plate_set_id;
	String plate_set_sys_name = new String("PS-" + String.valueOf(plate_set_id));
	int plate_num = dbm.getDatabaseRetriever().getNumberOfPlatesForPlateSetID(plate_set_id);
	int format_id = dbm.getDatabaseRetriever().getFormatForPlateSetID(plate_set_id);
	   
		new DialogImportPlateSetAccessionIDs(dbm, plate_set_sys_name, plate_set_id, format_id, plate_num);
	
	
    }

    /**
     * Loads table and make the association
     *
     * accessions looks like:
     * plate	well	accs.id
     * 1	1	AMRVK5473H
     * 1	2	KMNCX9294W
     * 1	3	EHRXZ2102Z
     * 1	4	COZHR7852Q
     * 1	5	FJVNR6433Q
     */
    public void associateAccessionsWithPlateSet(int  _plate_set_id, int _format, ArrayList<String[]> _accessions){

	int plate_set_id = _plate_set_id;
	int format = _format;
	ArrayList<String[]> accessions = _accessions;
	
	    // read in data file an populate assay_result with data;
    // only continue if successful
    String sql_statement = new String("INSERT INTO temp_accs_id (plate_order, by_col, accs_id) VALUES ");
    //LOGGER.info(accessions.get(0)[0] + " " + accessions.get(0)[1] + " " +accessions.get(0)[2] );	
    if (accessions.get(0)[0].equals("plate") & accessions.get(0)[1].equals("well") & accessions.get(0)[2].equals("accs.id")) {

    accessions.remove(0); // get rid of the header
    for (String[] row : accessions) {
      sql_statement =
          sql_statement
	  + "("
	  + Integer.parseInt(row[0])
	  + ", "
	  + Integer.parseInt(row[1])
	  + ", '"
	  + row[2]
	  + "'), ";
    }
    }else{
    JOptionPane.showMessageDialog(
				  dbm.getDialogMainFrame(), "Expecting the headers \"plate\", \"well\", and \"accs.id\", but found\n" + accessions.get(0)[0] + ", " +  accessions.get(0)[1] +  ", and " + accessions.get(0)[2] + "." , "Error", JOptionPane.ERROR_MESSAGE);
    return;
  	
    }

    String insertSql = "SELECT process_access_ids(?,?);";
    LOGGER.info("sqlstatement: " + insertSql);
    PreparedStatement insertPs;
    try {
      insertPs = conn.prepareStatement(insertSql);
          insertPs.setInt(1, plate_set_id);
      insertPs.setString(2, sql_statement.substring(0, sql_statement.length() - 2));
  
      insertPreparedStatement(insertPs);
    } catch (SQLException sqle) {
      LOGGER.warning("Failed to properly prepare  prepared statement: " + sqle);
      JOptionPane.showMessageDialog(
          dmf, "Problems parsing accesion ids file!.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    

    }



    /**
     * This method preps variable for displaying the dialog box
     */
    public void importBarcodesByPlateSet(int _plate_set_id){
	int plate_set_id = _plate_set_id;
	String plate_set_sys_name = new String("PS-" + String.valueOf(plate_set_id));
	int plate_num = dbm.getDatabaseRetriever().getNumberOfPlatesForPlateSetID(plate_set_id);
	//	int format_id = dbm.getDatabaseRetriever().getFormatForPlateSetID(plate_set_id);
	   
		new DialogImportPlateSetBarcodeIDs(dbm, plate_set_sys_name, plate_set_id,  plate_num);	
    }


    /**
     * @param _data
     * batch sql https://www.mkyong.com/jdbc/jdbc-preparedstatement-example-batch-update/
     */
    public void importPlateLayout(Object[][] _data, String _name, String _descr, String _control_location, int _n_controls, int _n_unknowns, int _format, int _n_edge){
	Object[][] data = _data;
	String name = _name;
	String descr = _descr;
	String control_location = _control_location;
	int n_controls = _n_controls;
	int n_unknowns = _n_unknowns;
	int format = _format;
	int n_edge = _n_edge;

	// LOGGER.info("data : " + data);
	// LOGGER.info("name : " + name);
	// LOGGER.info("descr : " + descr);
	// LOGGER.info("control_location : " + control_location);
	// LOGGER.info("n_controls : " + n_controls);
	// LOGGER.info("n_unknowns : " + n_unknowns);
	// LOGGER.info(" format: " + format);
	// LOGGER.info("n_edge : " + n_edge);
	
	String sql_statement1="TRUNCATE TABLE import_plate_layout;";
	
	String sql_statement2_pre = "INSERT INTO import_plate_layout (well_by_col, well_type_id, replicates, target) VALUES ";
	for (int i = 1; i < data.length; i++) {
      sql_statement2_pre =
          sql_statement2_pre
	  + "("
	  + Integer.parseInt((String)data[i][0])
	  + ", "
	  + Integer.parseInt((String)data[i][1])
	  + ", 1, 1), ";
    }
	//################Clojure
	//I will modify so that sql_statement2 update the import temp table proceeds, statement 3
	//is perfomed in clojure
	String sql_statement2 = sql_statement2_pre.substring(0, sql_statement2_pre.length() - 2) + ";";
	LOGGER.info("ss2: " + sql_statement2);
	//	String sql_statement3 = "SELECT create_layout_records(?,?,?,?,?,?,?);";
    //LOGGER.info(insertSql);
    PreparedStatement insertPs;
    try {
	conn.setAutoCommit(false);
      insertPs = conn.prepareStatement(sql_statement1);
      insertPreparedStatement(insertPs);
      //insertPs.addBatch();
	
      insertPs = conn.prepareStatement(sql_statement2);
      insertPreparedStatement(insertPs);
      // insertPs = conn.prepareStatement(sql_statement3);
      // insertPs.setString(1, name);
      // insertPs.setString(2, descr);
      // insertPs.setString(3, control_location);
      // insertPs.setInt(4, n_controls);
      // insertPs.setInt(5, n_unknowns);
      // insertPs.setInt(6, format);
      // insertPs.setInt(7, n_edge);
      // insertPreparedStatement(insertPs);
      //insertPs.addBatch();
      //insertPs.executeBatch();
      //insertPreparedStatement(insertPs);
      conn.commit();
      LOGGER.info("here after commit");
      IFn newPlateLayout = Clojure.var("ln.db-inserter", "new-plate-layout");
        newPlateLayout.invoke(data,  name,  descr, control_location, n_controls, n_unknowns, format,  n_edge );
     LOGGER.info("here after clojure");
 
    } catch (SQLException sqle) {
      LOGGER.warning("Failed to properly prepare  prepared statement: " + sqle);
      JOptionPane.showMessageDialog(
          dmf, "Problems parsing data file!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

	
    }

    /**
     * Insert a hit list read from a file.  The file text and contains one sample id per line
     * Sample id can be SPL-100 or 100.  Method will determine which and behave appropriately.
     * Expecting a column header, i.e. the first line of the input file is ignored.
     */
    public void insertHitListFromFile(int _assay_run_id, Vector<String> _s_ids_pre){
	int assay_run_id = _assay_run_id;
	Vector<String> s_ids_pre = _s_ids_pre;
	//convert to an int[] array all hits
	int[] s_ids = new int[s_ids_pre.size()];
	Iterator value = s_ids_pre.iterator();
	int counter = 0;
	if(s_ids_pre.get(0).substring(0,3).equals("SPL")){
	    while (value.hasNext()) {
		s_ids[counter] = (int)Integer.parseInt(((String)value.next()).substring(4));
		counter = counter +1;
        } 
	    
	}else{
	    while (value.hasNext()) {
		s_ids[counter] = (int)Integer.parseInt((String)value.next());
		counter = counter +1;
             
        }
	    
	}
	new DialogNewHitListFromFile(dbm, assay_run_id, s_ids);
    }

    /**
     * Follow up to insertHitListFromFile responding to DialogNewHitListFromFile
     */
    public void insertHitListFromFile2(String _name, String _description, int _num_hits, int _assay_run_id, int[] _hit_list){

	Integer[] hit_list = Arrays.stream( _hit_list ).boxed().toArray( Integer[]::new );
	//Object[] hit_list = (Integer[])_hit_list;
	      try {
      String insertSql = "SELECT new_hit_list ( ?, ?, ?, ?, ?);";
      PreparedStatement insertPs =
          conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
      insertPs.setString(1, _name);
      insertPs.setString(2, _description);
      insertPs.setInt(3, _num_hits);
      insertPs.setInt(4, _assay_run_id);
      insertPs.setArray(5, conn.createArrayOf("INTEGER", hit_list));
   
      LOGGER.info(insertPs.toString());
      insertPs.executeUpdate();
      //ResultSet resultSet = insertPs.getResultSet();
      //resultSet.next();
      //new_plate_set_id = resultSet.getInt("new_plate_set");
     
    } catch (SQLException sqle) {
	LOGGER.warning("SQLE at inserting new plate set: " + sqle);
    }


      }

    
}
