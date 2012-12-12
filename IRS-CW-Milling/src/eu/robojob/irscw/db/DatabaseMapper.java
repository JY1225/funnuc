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

import org.apache.derby.jdbc.ClientDriver;
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
			Class.forName(props.getProperty("jdbc.driver")).newInstance();
			new ClientDriver();
			conn = DriverManager.getConnection(props.getProperty("jdbc.protocol") + "://" + props.getProperty("jdbc.host") + ":" + props.getProperty("jdbc.port") + "/" + props.getProperty("jdbc.database") + ";create=true" + ";user=" 
					+ props.getProperty("jdbc.user") + ";password=" + props.getProperty("jdbc.password"));
			logger.info("Created database connection");
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		} catch (InstantiationException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
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
			logger.error(e);
		}
	}
}
