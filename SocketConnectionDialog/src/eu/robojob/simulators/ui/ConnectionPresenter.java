package eu.robojob.simulators.ui;


public class ConnectionPresenter {

	private ConnectionView view;
	private MainPresenter parent;
	
	public ConnectionPresenter(ConnectionView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void connect() {
		if (view.getPortNumber() != -1) {
			parent.connect(view.getIpAddress(), view.getPortNumber(), view.getType());
		} else {
			throw new IllegalArgumentException("Illegal port number!");
		}
	}
	
	public ConnectionView getView() {
		return view;
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
}
