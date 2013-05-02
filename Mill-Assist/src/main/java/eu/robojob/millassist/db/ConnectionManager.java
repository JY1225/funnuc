package eu.robojob.millassist.db;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConnectionManager {

	private static Connection conn;
	private static Logger logger = LogManager.getLogger(ConnectionManager.class.getName());
	
	private ConnectionManager() {
	}
	
	private static void connect() {
		Properties props = new Properties();
		try {
			URL url = ClassLoader.getSystemResource("jdbc.properties");
			props.load(url.openStream());
			System.setProperty("derby.system.home", props.getProperty("derby.system.home"));
			Class.forName(props.getProperty("jdbc.driver"));
			String connectionString = props.getProperty("jdbc.protocol") + ":" + props.getProperty("jdbc.database") + ";create=true" + ";user=" 
					+ props.getProperty("jdbc.user") + ";password=" + props.getProperty("jdbc.password");
			logger.info("About to create database connection.");
			conn = DriverManager.getConnection(connectionString);
			logger.info("Successfully created database connection.");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		} 
	}
	
	public static Connection getConnection() {
		if (conn == null) {
			connect();
		}
		return conn;
	}
	
}
