package eu.robojob.irscw;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.db.GeneralMapper;
import eu.robojob.irscw.db.external.device.DeviceMapper;
import eu.robojob.irscw.db.external.robot.RobotMapper;
import eu.robojob.irscw.db.external.util.ConnectionMapper;
import eu.robojob.irscw.db.process.ProcessFlowMapper;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.process.ProcessFlowManager;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.RoboSoftAppFactory;

public class RoboSoft extends Application {

	private static Logger logger = LogManager.getLogger(RoboSoft.class.getName());
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		logger.info("Started application.");
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File("C:\\RoboJob\\settings.properties")));
		
		GeneralMapper generalMapper = new GeneralMapper();
		ConnectionMapper connectionMapper = new ConnectionMapper();
		DeviceMapper deviceMapper = new DeviceMapper(generalMapper, connectionMapper);
		DeviceManager deviceManager = new DeviceManager(deviceMapper);
		RobotMapper robotMapper = new RobotMapper(connectionMapper);
		RobotManager robotManager = new RobotManager(robotMapper);
		ProcessFlowMapper processFlowMapper = new ProcessFlowMapper(generalMapper, deviceManager, robotManager);
		ProcessFlowManager processFlowManager = new ProcessFlowManager(processFlowMapper, deviceManager, robotManager);
		
		RoboSoftAppFactory factory = new RoboSoftAppFactory(deviceManager, robotManager, processFlowManager);
		MainPresenter mainPresenter = factory.getMainPresenter();
		mainPresenter.showConfigure();
		Scene scene = new Scene(mainPresenter.getView(), WIDTH, HEIGHT);
		Locale.setDefault(new Locale("nl"));
		if (!Boolean.parseBoolean(properties.getProperty("mouse-visible"))) {
			scene.setCursor(Cursor.NONE);
		}
		scene.getStylesheets().add("css/general-style.css");
		scene.getStylesheets().add("css/header-style.css");
		scene.getStylesheets().add("css/keyboard-style.css");
		scene.getStylesheets().add("css/configure-style.css");
		scene.getStylesheets().add("css/processflow-style.css");
		scene.getStylesheets().add("css/teach-style.css");
		scene.getStylesheets().add("css/automate-style.css");
		stage.setScene(scene);
		stage.setTitle("RoboSoft");
		stage.centerOnScreen();
		stage.setResizable(false);
		//stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
	}
	
	@Override
	public void stop() {
		logger.info("Closing application.");
		ThreadManager.shutDown();
		logger.info("Closed application.");
	}
}
