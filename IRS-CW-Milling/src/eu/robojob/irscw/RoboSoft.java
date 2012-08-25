package eu.robojob.irscw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
		mainPresenter.showProcessView();
		Scene scene = new Scene(mainPresenter.getView(), 800, 600);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("RoboSoft");
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.setIconified(false);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
	}
}
