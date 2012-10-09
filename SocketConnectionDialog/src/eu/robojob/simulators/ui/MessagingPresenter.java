package eu.robojob.simulators.ui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.simulators.threading.ListeningThread;

public class MessagingPresenter {

	private MessagingView view;
	private SocketConnection connection;
	private MainPresenter parent;
	
	private StringBuffer buffer;
	
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
		return view;
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
	
	public void sendMessage(String message) {
		view.setButtonEnabled(false);
		connection.sendString(message);
		view.setButtonEnabled(true);
	}
	
	public void disconnect() {
		parent.disconnect();
	}
	
	public void connect(int portNumber, String type) {
		ListeningThread listeningRunnable = new ListeningThread(portNumber, type, this);
		ThreadManager.getInstance().submit(listeningRunnable);
	}
	
	public void addToLog(String message) {
		buffer.append(message);
		view.setMessage(buffer.toString());
	}
	
	public void setConnected(boolean connected) {
		view.setConnected(connected);
	}
}
