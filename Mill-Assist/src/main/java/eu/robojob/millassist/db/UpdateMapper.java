package eu.robojob.millassist.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.util.Version;

public class UpdateMapper {
	
	//template for the directoryName. All directories with this format will be investigated.
	private static final String TEMPLATE_DBSCRIPTS_NAME = "+.+.+";
	private static Logger logger = LogManager.getLogger(UpdateMapper.class.getName());
	
	public UpdateMapper() {
		//Automatically call the main function of this class
		this.makeDBUpToDate();
	}
	
	/**
	 * This function will compare the current program ID, given in the settings.properties file, with the version ID found in the database.
	 * In case the program has a higher version than the largest database value, all db updates found will be executed.
	 */
	private void makeDBUpToDate() {
		try {
			Version versionToBe = getProgramVersion();
			Version versionAsIs = getLastUpdatedDBVersion();
			if(versionToBe.compareTo(versionAsIs) > 0) {
				logger.info("Current version of DB " + versionAsIs + " will be updated to " + versionToBe);
				updateDB(versionToBe, versionAsIs);
			}
		} catch(SQLException e) {
			logger.error(e);
		}
	}
	
	
	/**
	 * This functions gives the program version ID that has been given in the settings.properties file
	 * 
	 * @return Version x.y.z
	 */
	private Version getProgramVersion() {
		try {
			final Properties properties = new Properties();
			properties.load(new FileInputStream(new File("settings.properties")));
			if (properties.containsKey("version")) {
				String versionID = properties.getProperty("version").toString();
				return new Version(versionID);
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Version(0,0,0);
	}
	
	/**
	 * This function checks the database for the latest version ID that was used to update the database
	 * 
	 * @return Version x.y.z
	 * @throws SQLException
	 */
	private Version getLastUpdatedDBVersion() throws SQLException {
		try {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM VERSION ORDER BY MAJOR_RELEASE_ID DESC, MINOR_RELEASE_ID DESC, PATCH_RELEASE_ID DESC");
			stmt.setMaxRows(1);
			ResultSet results = stmt.executeQuery();
			int majorVersionId = 0, minorVersionId = 0, patchVersionId = 0;
			if(results.next()) {
				majorVersionId = results.getInt("MAJOR_RELEASE_ID");
				minorVersionId = results.getInt("MINOR_RELEASE_ID");
				patchVersionId = results.getInt("PATCH_RELEASE_ID");
			}
			stmt.close();
			return new Version(majorVersionId, minorVersionId, patchVersionId);	
		} catch(SQLException e) {
			return new Version(0,0,0);
		}
	}
	
	/**
	 * This function checks all files and directories in the dbscripts\ folder. In case a folder is found with a version number
	 * lower than the versionToBe, the scripts in this folder will be executed. If a folder is found with a version number higher 
	 * than the versionToBe, the updates will be skipped. The same is true for updates that are having a version number lower than
	 * the As-Is version.
	 * 
	 * After running this function, the database will be updated with the versionToBe version number indicating that all updates for
	 * this version were completed.
	 * 
	 * @param versionToBe
	 * 			- Version x.y.z
	 * @param versionAsIs
	 * 			- Version x.y.z
	 * @throws SQLException
	 */
	private void updateDB(Version versionToBe, Version versionAsIs) throws SQLException {

		//Run all SQL scripts in the directory of this version - 
	    File f = new File("dbscripts\\"); // current directory/dbscripts
	    File[] files = f.listFiles();
	    for (File file : files) {
	    	if(file.isDirectory() && isDBVersionDir(file.getName())) {
	    		Version dbScriptVersion = new Version(file.getName());
	    		//Only execute the updates with a version higher as the current version and lower than or equal to the toBe version
	    		if(dbScriptVersion.compareTo(versionAsIs) > 0 && dbScriptVersion.compareTo(versionToBe) <= 0) {
	    			for (File dbScript: file.listFiles()) {
	    				logger.info("Running SQL scripts from directory " + file.getName());
	    				runScript(dbScript);
	    			}
	    		}
	    	}
	    }
		updateDBVersion(versionToBe);
	}
	
	private static boolean isDBVersionDir(String fileName) {
		String regex = TEMPLATE_DBSCRIPTS_NAME.replace("+", ".+?");
		if(fileName.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void updateDBVersion(Version versionId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO VERSION (MAJOR_RELEASE_ID, MINOR_RELEASE_ID, PATCH_RELEASE_ID) VALUES (?,?,?)");
		stmt.setInt(1, versionId.getMajorReleaseId());
		stmt.setInt(2, versionId.getMinorReleaseId());
		stmt.setInt(3, versionId.getPatchReleaseId());
		stmt.executeUpdate();
		stmt.close();
	}
 
	/**
	 * Read the input file line by line and create a SQL command to execute.
	 * 
	 * @param dbScript
	 * 			- database script containing queries to execute
	 */
	private static void runScript(File dbScript) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(dbScript));
			String line;
			StringBuffer command = new StringBuffer();
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(line.startsWith("--") || line.startsWith("//") || line.length() < 1) {
					//Do nothing - comment
				} 
				//End of query
				else if(line.contains(";")) {
					command.append(line.substring(0, line.indexOf(";")));
					executeCommand(command.toString());
					command = new StringBuffer();
				} else {
					command.append(" " + line);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void executeCommand(String command) {
		try{
			Statement stmt = ConnectionManager.getConnection().createStatement();
			logger.info("Trying to update the database " + command);
			stmt.executeUpdate(command);
			ConnectionManager.getConnection().commit();
		} catch(SQLException e) {
			logger.error(e);
		}
	}  
}
