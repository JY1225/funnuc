package eu.robojob.simulators.ui;

import javafx.stage.Stage;


public class MainPresenter {

	private MainView view;
	
	private ConnectionPresenter connectionPresenter;
	private MessagingPresenter messagingPresenter;
	private Stage stage;
	
	public MainPresenter(MainView view, ConnectionPresenter connectionPresenter, MessagingPresenter messagingPresenter, Stage stage) {
		this.view = view;
		view.setPresenter(this);
		this.connectionPresenter = connectionPresenter;
		connectionPresenter.setParent(this);
		this.messagingPresenter = messagingPresenter;
		messagingPresenter.setParent(this);
		this.stage = stage;
		disconnect();
	}
	
	public void disconnect() {
		view.setCenterView(connectionPresenter.getView());
	}
	
	public MainView getView() {
		return view;
	}
	
	public void connect(int portNumber, String type) {
		stage.setTitle(type + " verbinding - localhost:" + portNumber);
		view.setCenterView(messagingPresenter.getView());
		messagingPresenter.connect(portNumber, type);
	}
}
