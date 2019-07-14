package pm;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.sql.SQLException;

/**
 * Upon insert session gains a timestamp
 *
 * <p>Session provides user name and ID and project sys name and id
 */
public class Session {

    private int user_id;
    private String user;
    private String password;
    private int user_group_id;
    private String user_group; // admin, superuser, user
    private int project_id;
    private String project_sys_name;
    private String plate_set_sys_name;
    private int plate_set_id;
    private int plate_id;
    private Long session_id;
    private String working_dir;
    private String temp_dir;
    //DialogProperties not found has a help button that needs a help_url_prefix
    private String help_url_prefix = "http://labsolns.com/software/";  
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
    private boolean authenticated= false;
    private static final long serialVersionUID = 1L;

  public Session() {
    	String path = "./limsnucleus.properties";   
	try{
	    file = new FileInputStream(path);
	    loadProperties();
	if(user.equals("null")){
	    new DialogLogin(this, "", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
	    //postLoadProperties();
	}else{
	    postLoadProperties();

	}
	    
	}catch(FileNotFoundException fnfe){
	    new DialogPropertiesNotFound(this);	   
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
            host = prop.getProperty("host");
	    port = prop.getProperty("port");
	    sslmode = prop.getProperty("sslmode");
	    source = prop.getProperty("source");
            dbname = prop.getProperty("dbname");
            help_url_prefix = prop.getProperty("base.help.url");
            password = prop.getProperty("password");
	    user = prop.getProperty("user");	  
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
		password = "c5644d221fa636d8d8065d336014723f66df0c6b78e7a5390453c4a18c9b20b2";
		user = "dpstpnuvjslqch";
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
		password = "hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_";
		user = "klohymim";
	    	URL = new String("jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?user=" + user + "&password=" +password + "&SSL=true" );

		this.postLoadProperties();

	
    }

    
    /**
     * Continuation of login; at this point user has been determined either from properties or login dialog
     * Have to separate so that the proper username is used i.e. null might have been entered in properties file
     */
    public void postLoadProperties(){
	try{
	    
	    switch (source){
	    case "heroku":
		URL = new String("jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?sslmode=require&user=" + user + "&password=" +password );
		break;
	    case "local":
		URL = new String("jdbc:postgresql://" + host + "/" + dbname);
	    break;
	    case "elephantsql":
	
	    	URL = new String("jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?user=" + user + "&password=" +password + "&SSL=true" );
		break;
	}
	    //	 LOGGER.info("URL: " + URL);

	dbm = new DatabaseManager( this );
	if(authenticated){
	    dbr = dbm.getDatabaseRetriever();
	    dbi = dbm.getDatabaseInserter();
	    dmf = new DialogMainFrame(this);
	}else{
	    	JOptionPane.showMessageDialog(null,
			      "Invalid username or password.  Session terminated.",
				      "Authentication Failure!",
			      JOptionPane.ERROR_MESSAGE);
	}
	}
    catch(SQLException sqle){
	LOGGER.info("SQL exception creating DatabaseManager: " + sqle);
    }
	
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

  public String getPlateSetSysName() {
    return plate_set_sys_name;
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

    
  public Long getSessionID() {
    return session_id;
  }

  public void setSessionID(Long _l) {
    session_id = _l;
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

  public String getWorkingDir() {
    return working_dir;
  }
    public String getHelpURLPrefix(){
	return help_url_prefix;
    }
    public String getURL(){
	return URL;
    }
    public String getHost(){
	return host;
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
    
}
