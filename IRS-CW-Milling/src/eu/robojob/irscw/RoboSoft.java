package eu.robojob.irscw;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
import eu.robojob.irscw.ui.controls.keyboard.FullKeyboardView.KeyboardType;
import eu.robojob.irscw.ui.preloader.RoboJobPreloader;

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
		final Properties properties = new Properties();
		properties.load(new FileInputStream(new File("C:\\RoboJob\\settings.properties")));
		//properties.load(new FileInputStream(new File("settings.properties")));
		final RoboJobPreloader preloader = new RoboJobPreloader();
		Scene scene2 = new Scene(preloader, WIDTH, HEIGHT);
		scene2.getStylesheets().add("css/preloader-style.css");
		stage.setScene(scene2);
		stage.setTitle("RoboSoft");
		stage.centerOnScreen();
		stage.setResizable(false);
		//stage.initStyle(StageStyle.UNDECORATED);
		stage.getIcons().add(new Image("img/icon.png"));
		stage.show();
		ThreadManager.submit(new Thread () {
			@Override
			public void run() {
				try {
					GeneralMapper generalMapper = new GeneralMapper();
					ConnectionMapper connectionMapper = new ConnectionMapper();
					DeviceMapper deviceMapper = new DeviceMapper(generalMapper, connectionMapper);
					DeviceManager deviceManager = new DeviceManager(deviceMapper);
					RobotMapper robotMapper = new RobotMapper(connectionMapper);
					RobotManager robotManager = new RobotManager(robotMapper);
					ProcessFlowMapper processFlowMapper = new ProcessFlowMapper(generalMapper, deviceManager, robotManager);
					ProcessFlowManager processFlowManager = new ProcessFlowManager(processFlowMapper, deviceManager, robotManager);
					String keyboardTypePropertyVal = properties.getProperty("keyboard-type");
					KeyboardType keyboardType = null;
					if (keyboardTypePropertyVal.equals("azerty")) {
						keyboardType = KeyboardType.AZERTY;
					} else if (keyboardTypePropertyVal.equals("querty")) {
						keyboardType = KeyboardType.QWERTY;
					} else if (keyboardTypePropertyVal.equals("quertz")) {
						keyboardType = KeyboardType.QWERTZ_DE;
					}
					RoboSoftAppFactory factory = new RoboSoftAppFactory(deviceManager, robotManager, processFlowManager, keyboardType);
					final MainPresenter mainPresenter = factory.getMainPresenter();
					mainPresenter.showConfigure();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							final Scene scene = new Scene(mainPresenter.getView(), WIDTH, HEIGHT);
							Locale.setDefault(new Locale(properties.getProperty("locale")));
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
							scene.getStylesheets().add("css/admin-style.css");
							stage.setScene(scene);						
						}
					});
				} catch(Exception e) {
					e.printStackTrace();
					Platform.exit();
				}
			}
		});
		
		
	}
	
	@Override
	public void stop() {
		logger.info("Closing application.");
		ThreadManager.shutDown();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
			}
		});
		logger.info("Closed application.");
	}
}
