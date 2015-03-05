package eu.robojob.millassist;

import java.io.File;
import java.util.Locale;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import eu.robojob.millassist.db.ConnectionManager;
import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.db.external.device.DeviceMapper;
import eu.robojob.millassist.db.external.robot.RobotMapper;
import eu.robojob.millassist.db.external.util.ConnectionMapper;
import eu.robojob.millassist.db.process.ProcessFlowMapper;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.robot.RobotManager;
import eu.robojob.millassist.process.ProcessFlowManager;
import eu.robojob.millassist.threading.MemoryUsageMonitoringThread;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.ui.MainPresenter;
import eu.robojob.millassist.ui.RoboSoftAppFactory;
import eu.robojob.millassist.ui.controls.keyboard.FullKeyboardView.KeyboardType;
import eu.robojob.millassist.ui.preloader.RoboJobPreloader;
import eu.robojob.millassist.util.PropertyManager;
import eu.robojob.millassist.util.StdErrLog;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.PropertyManager.Setting;

public class RoboSoft extends Application {

	private static Logger logger = LogManager.getLogger(RoboSoft.class.getName());
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		File loggerConfig = new File("log4j2.xml");
		Configurator.initialize("logger", null, loggerConfig.toURI());
		logger.info("Started application.");
		StdErrLog.tieSystemOutAndErrToLog();
		PropertyManager.readPropertyFile();
		Font.loadFont(this.getClass().getResourceAsStream("/fonts/Open Sans/OpenSans-Light.ttf"), 12).getName();
		Font.loadFont(this.getClass().getResourceAsStream("/fonts/Open Sans/OpenSans-Regular.ttf"), 12).getName();
		Font.loadFont(this.getClass().getResourceAsStream("/fonts/Open Sans/OpenSans-Semibold.ttf"), 12).getName();
		Font.loadFont(this.getClass().getResourceAsStream("/fonts/Open Sans/OpenSans-Bold.ttf"), 12).getName();
		if (PropertyManager.hasSettingValue(Setting.MEMORY, "true")) {
			ThreadManager.submit(new MemoryUsageMonitoringThread());
		}
		final RoboJobPreloader preloader = new RoboJobPreloader();
		Scene scene2 = new Scene(preloader, WIDTH, HEIGHT);
		scene2.getStylesheets().add("styles/preloader-style.css");
		stage.setScene(scene2);
		stage.setTitle("RoboSoft");
		stage.centerOnScreen();
		stage.setResizable(false);
		if (!PropertyManager.hasSettingValue(Setting.TITLEBAR, "true")) {
			stage.initStyle(StageStyle.UNDECORATED);
		}
		stage.getIcons().add(new Image("images/icon.png"));
		stage.show();
		ThreadManager.submit(new Thread () {
			@Override
			public void run() {
				try {
//					File file = new File ("languages\\");
//					URL[] urls = {file.toURI().toURL()};
//					ClassLoader loader = new URLClassLoader(urls);
//					Translator.setLanguage(properties.getProperty("locale"), loader);	
					Locale.setDefault(new Locale(PropertyManager.getValue(Setting.LANGUAGE)));
					if (PropertyManager.hasSettingValue(Setting.LANGUAGE, "en")) {
						Translator.setLanguageEN();
					} else if (PropertyManager.hasSettingValue(Setting.LANGUAGE, "de")) {
						Translator.setLanguageDE();
					} else if (PropertyManager.hasSettingValue(Setting.LANGUAGE, "se")) {
						Translator.setLanguageSE();
					} else {
						Translator.setLanguageNL();
					}
					GeneralMapper generalMapper = new GeneralMapper();
					//new UpdateMapper();
					ConnectionMapper connectionMapper = new ConnectionMapper();
					DeviceMapper deviceMapper = new DeviceMapper(generalMapper, connectionMapper);
					DeviceManager deviceManager = new DeviceManager(deviceMapper);
					RobotMapper robotMapper = new RobotMapper(connectionMapper);
					RobotManager robotManager = new RobotManager(robotMapper);
					ProcessFlowMapper processFlowMapper = new ProcessFlowMapper(generalMapper, deviceManager, robotManager);
					ProcessFlowManager processFlowManager = new ProcessFlowManager(processFlowMapper, deviceManager, robotManager);
					String keyboardTypePropertyVal = PropertyManager.getValue(Setting.KEYBOARD);
					KeyboardType keyboardType = null;
					if (keyboardTypePropertyVal.equals("azerty")) {
						keyboardType = KeyboardType.AZERTY;
					} else if (keyboardTypePropertyVal.equals("querty")) {
						keyboardType = KeyboardType.QWERTY;
					} else if (keyboardTypePropertyVal.equals("quertz")) {
						keyboardType = KeyboardType.QWERTZ_DE;
					}
					RoboSoftAppFactory.intialize(deviceManager, robotManager, processFlowManager, keyboardType);
					final MainPresenter mainPresenter = RoboSoftAppFactory.getMainPresenter();
					mainPresenter.showConfigure();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							final Scene scene = new Scene(mainPresenter.getView(), WIDTH, HEIGHT);
							
							if (!PropertyManager.hasSettingValue(Setting.MOUSE_VISIBLE, "true")) {
								scene.setCursor(Cursor.NONE);
							}
							
							scene.getStylesheets().add("styles/keyboard-style.css");
							scene.getStylesheets().add("styles/configure-style.css");
							scene.getStylesheets().add("styles/teach-style.css");
							scene.getStylesheets().add("styles/automate-style.css");
							scene.getStylesheets().add("styles/admin-style.css");
							scene.getStylesheets().add("styles/general-style.css");
							scene.getStylesheets().add("styles/header-style.css");
							scene.getStylesheets().add("styles/processflow-style.css");
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
		ConnectionManager.shutDown();
		ThreadManager.shutDown();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
			}
		});
		logger.info("Closed application.");
	}
}
