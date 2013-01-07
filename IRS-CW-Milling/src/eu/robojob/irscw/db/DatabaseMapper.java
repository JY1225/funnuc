package eu.robojob.irscw.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DatabaseMapper {
	
	private static Logger logger = LogManager.getLogger(DatabaseMapper.class.getName());
	
	private static final int ID_INDEX = 1;
	private static final int NAME_INDEX = 2;
	private static final int CITY_INDEX = 3;
	
	private Connection conn = null;
	private Statement stmt = null;
	
	private static DatabaseMapper instance = null;
	
	private DatabaseMapper() {
		createConnection();
	}
	
	public static DatabaseMapper getInstance() {
		if (instance == null) {
			instance = new DatabaseMapper();
		}
		return instance;
	}
	
	private void createConnection() {
		Properties props = new Properties();
		try {
			logger.info("About to create database connection");
			URL url = ClassLoader.getSystemResource("jdbc.properties");
			props.load(url.openStream());
			System.setProperty("derby.system.home", props.getProperty("derby.system.home"));
			logger.info("System property derby.system.home: " + System.getProperty("derby.system.home"));
			Class.forName(props.getProperty("jdbc.driver"));
			//new ClientDriver();
			String connectionString = props.getProperty("jdbc.protocol") + ":" + props.getProperty("jdbc.database") + ";create=true" + ";user=" 
					+ props.getProperty("jdbc.user") + ";password=" + props.getProperty("jdbc.password");
			logger.info("Connection-string: [" + connectionString + "].");
			conn = DriverManager.getConnection(connectionString);
			logger.info("Created database connection");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error(e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	public void logTestContent() {
		try {
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery("select * from TEST");
			while (results.next()) {
				int id = results.getInt(ID_INDEX);
				String name = results.getString(NAME_INDEX);
				String city = results.getString(CITY_INDEX);
				logger.info("Person: " + name + " from " + city + " with id: " + id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
}
