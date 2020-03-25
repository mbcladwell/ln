package ln;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import java.awt.Desktop;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Upon insert session gains a timestamp
 *
 * <p>Session provides user name and ID and project sys name and id
 */
public class Session {
    private boolean init;
    private int user_id;
    private String user;
    private String password;
    private String connuser;
    private String connpassword;
    private int user_group_id;
    private String user_group; // admin, superuser, user
    private int project_id;
    private String project_sys_name;
    private String plate_set_sys_name;
    private String plate_sys_name;
    private int plate_set_id;
    private int plate_id;
    private int session_id;
    private String working_dir;
    private String temp_dir;
    //DialogProperties not found has a help button that needs a help_url_prefix
    private String help_url_prefix;  
    private String URL;
    private String dbname;
    private DatabaseManager dbm;
    private DialogMainFrame dmf;
    private String host;
    private String port;
    private String sslmode;
    private String source;
    private DatabaseInserter dbi;
    private DatabaseRetriever dbr;
    private FileInputStream file;
    private  Properties prop = new Properties();
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean authenticated = false;
    private boolean admin_account = false;
    private static final long serialVersionUID = 1L;

  public Session() {
      String path = "./limsnucleus.properties";   
      try{
	    file = new FileInputStream(path);
	    loadProperties();
	    if(init){
		new DialogPropertiesNotFound( this);
	    }else{    
		//	if(user.equals("null") || password.equals("null")){
		//    new DialogLogin(this, "", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		//	}
		postLoadProperties();
	
	    }
      }catch(FileNotFoundException fnfe){
	  JOptionPane.showMessageDialog(null,
					"Connecting to ElephantSQL test instance.",
					"No limsnucleus.properties file!",
					JOptionPane.ERROR_MESSAGE);
	  setupElephantSQL();
	  setUserName("ESQL_test");
	  setUserGroup("user");
	  postLoadProperties();
      }   
  }

  
    /**
     * Define class variables with contents of properties file
     * File has been determined at this point.
     */
    public void loadProperties(){

	try{
	    prop.load(file);
            // get the property value and print it out
	    init = Boolean.parseBoolean(prop.getProperty("init"));
            host = prop.getProperty("host");
	    port = prop.getProperty("port");
	    sslmode = prop.getProperty("sslmode");
	    source = prop.getProperty("source");
            dbname = prop.getProperty("dbname");
            help_url_prefix = prop.getProperty("help-url-prefix");
            password = prop.getProperty("password");
	    user = prop.getProperty("user");	  
            connpassword = prop.getProperty("connpassword");
	    connuser = prop.getProperty("connuser");	  
	    temp_dir = new File(System.getProperty("java.io.tmpdir")).toString();
	    working_dir = new File(System.getProperty("user.dir")).toString();
	    file.close();
	}catch(IOException ioe){
		
	}    
	    
    }

    public void setupHeroku(){
		host = "ec2-50-19-114-27.compute-1.amazonaws.com";
		port = "5432";
		sslmode = "require";	   
		dbname = "d6dmueahka0hch";
		help_url_prefix = "http://labsolns.com/software/";
		connpassword = "c5644d221fa636d8d8065d336014723f66df0c6b78e7a5390453c4a18c9b20b2";
		connuser = "dpstpnuvjslqch";
		//URL = new String("jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?sslmode=require&user=" + user + "&password=" +password );
	
	this.postLoadProperties();
	
    }
    public void setupElephantSQL(){
		host = "raja.db.elephantsql.com";
		port = "5432";
		sslmode = "require";	      
		dbname = "klohymim";
		source = "elephantsql";
		help_url_prefix = "http://labsolns.com/software/";
		connpassword = "hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_";
		connuser = "klohymim";
		user="ln_user";
		password="welcome";
	    	URL = new String("jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?user=" + connuser + "&password=" + connpassword + "&SSL=true" );

		this.postLoadProperties();

	
    }

    
    /**
     * Continuation of login; at this point user has been determined either from properties or login dialog
     * Have to separate so that the proper username is used i.e. null might have been entered in properties file
     */
    public void postLoadProperties(){
	try{
	    URL = getConnURL(source);
	    //LOGGER.info("URL: " + URL);

	    dbm = new DatabaseManager(this );
	    if(authenticated){
		createNewSession(); //session id needed for dbi
		dbr = new DatabaseRetriever(this);
		dbi = new DatabaseInserter(this);
		dmf = new DialogMainFrame(this);
	    }else{
	    	JOptionPane.showMessageDialog(null,
					      "Invalid username or password.  Session terminated.",
					      "Authentication Failure!",
					      JOptionPane.ERROR_MESSAGE);
	    }
	}catch(SQLException sqle){
	    LOGGER.info("SQL exception creating DatabaseManager: " + sqle);
	}
	
    }


    public String getConnURL(String _source){
	switch (_source){
	case "internal":
	    URL = new String("jdbc:postgresql://" + host + ":" + port + "/" + dbname  );
	    break;
	case "heroku":
	    URL = new String("jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?sslmode=require&user=" + connuser + "&password=" + connpassword );
	    break;
	case "local":
	    URL = new String("jdbc:postgresql://" + host + "/" + dbname);
	    break;
	case "elephantsql":
	    
	    URL = new String("jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?user=" + connuser + "&password=" + connpassword + "&SSL=true" );
	    break;
	}
	return URL;	
    }


  public int createNewSession() {
      int i=0;
    String insertSql = "INSERT INTO lnsession( lnuser_id) VALUES (? ) RETURNING id;";
    PreparedStatement insertPs;

    try {
       Connection conn = dbm.getConnection();

       insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS  );
      insertPs.setInt(1, getUserID());
       insertPs.executeUpdate(); // executeUpdate expects no returns!!!
       ResultSet keys = insertPs.getGeneratedKeys();
       keys.next();
       i = keys.getInt(1); 
       //LOGGER.info("session id: " + i);
      setSessionID(i);
      //      insertPreparedStatement(insertPs);
    } catch (SQLException sqle) {
      LOGGER.warning("Failed to properly prepare  prepared statement: " + sqle);
    }
    return i;
  }

    
  public void setUserID(int _id) {
    user_id = _id;
  }

  public int getUserID() {
    return user_id;
  }

  public void setUserName(String _n) {
    user = _n;
  }

  public String getUserName() {
    return user;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String _p) {
    password= _p;
  }

      public String getDBuser() {
    return connuser;
  }
      public String getDBname() {
    return dbname;
  }

    public String getDBpassword() {
    return connpassword;
  }

  public void setUserGroup(String _s) {
    user_group = _s;
  }

  public String getUserGroup() {
    return user_group;
  }

  public int getUserGroupID() {
    return user_group_id;
  }

  public void setUserGroupID(int _i) {
    user_group_id = _i;
  }

  public void setProjectID(int _id) {
    project_id = _id;
  }

  public int getProjectID() {
    return project_id;
  }

  public void setProjectSysName(String _s) {
    project_sys_name = _s;
  }

  public String getProjectSysName() {
    return project_sys_name;
  }

  public void setPlateSetSysName(String _s) {
    plate_set_sys_name = _s;
  }
 public void setPlateSysName(String _s) {
    plate_sys_name = _s;
  }

  public String getPlateSetSysName() {
    return plate_set_sys_name;
  }
  public String getPlateSysName() {
    return plate_sys_name;
  }

      public void setPlateSetID(int _id) {
    plate_set_id = _id;
  }

  public int getPlateSetID() {
    return plate_set_id;
  }

      public void setPlateID(int _id) {
    plate_id = _id;
  }

  public int getPlateID() {
    return plate_id;
  }

    public boolean getSSLmode(){
	switch(sslmode){
	case "true":
	    return true;
	    
	case "false":
	    return false;	   
	}
	return false;
    }  
  public int getSessionID() {
    return session_id;
  }

  public void setSessionID(int _i) {
    session_id = _i;
  }

  public void setTempDir(String _s) {
    temp_dir = _s;
  }

  public String getTempDir() {
    return temp_dir;
  }

  public void setWorkingDir(String _s) {
    working_dir = _s;
  }

    public void openHelpPage(String _s){
	//defn open-help-page [s]
	//(browse/browse-url (str (cm/get-help-url-prefix) s)))
	openWebpage(URI.create(this.getHelpURLPrefix() + _s));

    }
    
  public String getWorkingDir() {
    return working_dir;
  }
    public String getHelpURLPrefix(){
	return "http://labsolns.com/software/";
    }
    public String getURL(){
	return URL;
    }
    public String getHost(){
	return host;
    }
    public String getSource(){
	return source;
    }
    public String getPort(){
	return port;
    }

    
    public DatabaseManager getDatabaseManager(){
	return dbm;
    }
    public DatabaseRetriever getDatabaseRetriever(){
	return dbr;
    }
    public DatabaseInserter getDatabaseInserter(){
	return dbi;
    }
    public DialogMainFrame getDialogMainFrame(){
	return dmf;
    }
    public void setPropertiesFile(FileInputStream _f){
	file=_f;
    }
    public FileInputStream getPropertiesFile(){
	return file;
    }
    public void setAuthenticated(boolean _b){
	authenticated = _b;
    }
    public boolean getAuthenticated(){
	return authenticated;
    }
    public void setAdminAccount(boolean _b){
	admin_account = _b;
    }
    public boolean getAdminAccount(){
	return admin_account;
    }

    
    public String getAllProps(){
	return null;
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


    public static void main(String[] args){
	new Session();
	
    }
}
