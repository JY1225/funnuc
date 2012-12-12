package eu.robojob.irscw;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		RoboSoftAppFactory factory = new RoboSoftAppFactory(properties);
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
		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
	}
	
	@Override
	public void stop() {
		logger.info("Closing application.");
		ThreadManager.getInstance().shutDown();
		logger.info("Closed application.");
	}
}
