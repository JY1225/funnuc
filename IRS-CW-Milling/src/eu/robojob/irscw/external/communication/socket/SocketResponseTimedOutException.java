package eu.robojob.irscw.external.communication.socket;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.util.Translator;

public class SocketResponseTimedOutException extends AbstractCommunicationException {

	private static final long serialVersionUID = 1L;
	
	private static final String RESPONSE_TIMED_OUT_FROM = "ResponseTimedOutException.responseTimedOutFrom";
	
	private SocketConnection socketConnection;
	
	public SocketResponseTimedOutException(final SocketConnection socketConnection) {
		this.socketConnection = socketConnection;
	}
	
	public SocketConnection getConnection() {
		return socketConnection;
	}
	
	@Override
	public String getMessage() {
		return "Waiting for a response from " + socketConnection + " timed out.";
	}
	
	@Override
	public String getLocalizedMessage() {
		return Translator.getTranslation(RESPONSE_TIMED_OUT_FROM) + " " + socketConnection;
	}
}
