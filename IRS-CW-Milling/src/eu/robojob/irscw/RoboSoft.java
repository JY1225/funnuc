package eu.robojob.irscw;

import java.util.Locale;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.log4j.Logger;

import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.RoboSoftAppFactory;

public class RoboSoft extends Application {

	static Logger logger = Logger.getLogger(RoboSoft.class.getName());
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		RoboSoftAppFactory factory = new RoboSoftAppFactory();
		MainPresenter mainPresenter = factory.getMainPresenter();
		mainPresenter.showConfigure();
		Scene scene = new Scene(mainPresenter.getView(), 800, 600);
		Locale.setDefault(new Locale("nl"));
		//scene.setCursor(Cursor.NONE);
		scene.getStylesheets().add("css/general-style.css");
		scene.getStylesheets().add("css/header-style.css");
		scene.getStylesheets().add("css/keyboard-style.css");
		scene.getStylesheets().add("css/configure-style.css");
		scene.getStylesheets().add("css/processflow-style.css");
		scene.getStylesheets().add("css/teach-style.css");
		//scene.getStylesheets().addAll("css/general-style.css", "css/header-style.css", "css/keyboard-style.css", "css/configure-style.css", "css/processflow-style.css", "css/teach-style.css");
		stage.setScene(scene);
		stage.setTitle("RoboSoft");
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);
		//stage.setFullScreen(true);
		stage.show();
	}
	
	@Override
	public void stop() {
		logger.info("should stop now!");
		ThreadManager.getInstance().shutDown();
	}
}
