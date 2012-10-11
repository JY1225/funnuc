package eu.robojob.irscw.external.communication;

public class DisconnectedException extends CommunicationException {

	private SocketConnection connection;
	
	public DisconnectedException(SocketConnection socketConnection) {
		this.connection = socketConnection;
	}
	
	private static final long serialVersionUID = 1L;

	public SocketConnection getConnection() {
		return connection;
	}
}
