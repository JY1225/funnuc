package eu.robojob.simulators;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.simulators.ui.ConnectionPresenter;
import eu.robojob.simulators.ui.ConnectionView;
import eu.robojob.simulators.ui.MainPresenter;
import eu.robojob.simulators.ui.MainView;
import eu.robojob.simulators.ui.MessagingPresenter;
import eu.robojob.simulators.ui.MessagingView;


public class SocketConnectionDialog extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		MessagingView messagingView = new MessagingView();
		MessagingPresenter messagingPresenter = new MessagingPresenter(messagingView);
		ConnectionView connectionView = new ConnectionView();
		ConnectionPresenter connectionPresenter = new ConnectionPresenter(connectionView);
		MainView mainView = new MainView();
		MainPresenter mainPresenter = new MainPresenter(mainView, connectionPresenter, messagingPresenter);
		Scene scene = new Scene(mainPresenter.getView());
		scene.getStylesheets().addAll("style.css");
		stage.setScene(scene);
		stage.setTitle("Socket connection simulator");
		stage.centerOnScreen();
		stage.show();
	}

	@Override
	public void stop() {
		ThreadManager.getInstance().shutDown();
	}
}
