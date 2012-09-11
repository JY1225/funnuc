package eu.robojob.irscw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.log4j.Logger;

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
		mainPresenter.showProcessConfigureView();
		Scene scene = new Scene(mainPresenter.getView(), 800, 600);
		scene.getStylesheets().addAll("css/general-style.css", "css/header-style.css", "css/keyboard-style.css", "css/configure-style.css", "css/processflow-style.css");
		stage.setScene(scene);
		stage.setTitle("RoboSoft");
		stage.centerOnScreen();
		stage.setResizable(false);
		//stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
	}
}
