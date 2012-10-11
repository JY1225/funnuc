package eu.robojob.simulators.ui;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.simulators.threading.ListeningThread;
import eu.robojob.simulators.threading.ThreadManager;

public class MessagingPresenter {

	private MessagingView view;
	private SocketConnection connection;
	private MainPresenter parent;
	
	private StringBuffer buffer;
	
	private ListeningThread listeningRunnable;
	
	private static final Logger logger = Logger.getLogger(MessagingPresenter.class);
	
	public MessagingPresenter(MessagingView view) {
		this.view = view;
		view.setPresenter(this);
		buffer = new StringBuffer();
		setConnected(false);
	}
	
	public void setConnection(SocketConnection connection) {
		this.connection = connection;
	}
	
	public MessagingView getView() {
		view.clearLog();
		buffer = new StringBuffer();
		return view;
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
	
	public void sendMessage(String message) {
		logger.debug("about to send message: " + message);
		view.clearText();
		addToLog("OUT: " + message + "\n");
		view.setButtonEnabled(false);
		try {
			connection.sendString(message);
		} catch (DisconnectedException e) {
			e.printStackTrace();
			disconnect();
		}
		view.setButtonEnabled(true);
	}
	
	public void disconnect() {
		listeningRunnable.closeConnection();
	}
	
	public void connect(String ipAddress, int portNumber, String type) {
		listeningRunnable = new ListeningThread(ipAddress, portNumber, type, this);
		ThreadManager.getInstance().submit(listeningRunnable);
	}
	
	public void addToLog(String message) {
		buffer.append(message);
		view.setMessage(buffer.toString());
	}
	
	public void setConnected(boolean connected) {
		view.setConnected(connected);
		if (connected) {
			this.connection = listeningRunnable.getConnection();
		} else {
			if (parent != null) {
				parent.disconnect();
			}
		}
	}
}
