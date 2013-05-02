package eu.robojob.irscw.external.communication.socket;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.util.Translator;

public class SocketDisconnectedException extends AbstractCommunicationException {

	private static final long serialVersionUID = 1L;

	private static final String NO_CONNECTION_TO = "DisconnectedException.notConnectedTo";
	
	private SocketConnection connection;
	
	public SocketDisconnectedException(final SocketConnection socketConnection) {
		this.connection = socketConnection;
	}
	
	public SocketConnection getConnection() {
		return connection;
	}
	
	@Override
	public String getMessage() {
		return "Not connected to: " + connection.toString() + ".";
	}
	
	@Override
	public String getLocalizedMessage() {
		return Translator.getTranslation(NO_CONNECTION_TO) + " " + connection.toString() + ".";
	}
}
