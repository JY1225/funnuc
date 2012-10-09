package eu.robojob.simulators.ui;


public class MainPresenter {

	private MainView view;
	
	private ConnectionPresenter connectionPresenter;
	private MessagingPresenter messagingPresenter;
	
	public MainPresenter(MainView view, ConnectionPresenter connectionPresenter, MessagingPresenter messagingPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.connectionPresenter = connectionPresenter;
		connectionPresenter.setParent(this);
		this.messagingPresenter = messagingPresenter;
		messagingPresenter.setParent(this);
		disconnect();
	}
	
	public void disconnect() {
		view.setCenterView(connectionPresenter.getView());
	}
	
	public MainView getView() {
		return view;
	}
	
	public void connect(int portNumber, String type) {
		view.setCenterView(messagingPresenter.getView());
		messagingPresenter.connect(portNumber, type);
	}
}
