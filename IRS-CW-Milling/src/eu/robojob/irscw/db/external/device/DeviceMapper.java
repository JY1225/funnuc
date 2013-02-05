package eu.robojob.irscw.db.external.device;

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

public final class DeviceMapper {

	private static Logger logger = LogManager.getLogger(DeviceMapper.class.getName());
	private static DeviceMapper instance;
	
	private Connection conn = null;
	
	private DeviceMapper() {
		connect();
	}
	
	private void connect() {
		Properties props = new Properties();
		try {
			logger.info("About to create database connection.");
			URL url = ClassLoader.getSystemResource("jdbc.properties");
			props.load(url.openStream());
			System.setProperty("derby.system.home", props.getProperty("derby.system.home"));
			logger.info("System property derby.system.home [" + System.getProperty("derby.system.home") + "].");
			Class.forName(props.getProperty("jdbc.driver"));
			String connectionString = props.getProperty("jdbc.protocol") + ":" + props.getProperty("jdbc.database") + ";create=true" + ";user=" 
					+ props.getProperty("jdbc.user") + ";password=" + props.getProperty("jdbc.password");
			logger.info("Connection-string: [" + connectionString + "].");
			conn = DriverManager.getConnection(connectionString);
			logger.info("Successfully created database connection.");
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
	
	public static DeviceMapper getInstance() {
		if (instance == null) {
			instance = new DeviceMapper();
		}
		return instance;
	}
	
	public void getAllDevices() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT * FROM DEVICES");
	}
}
