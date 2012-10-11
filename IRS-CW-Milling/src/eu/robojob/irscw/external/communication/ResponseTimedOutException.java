package eu.robojob.irscw.external.communication;

public class ResponseTimedOutException extends CommunicationException {

	private static final long serialVersionUID = 1L;

	private ExternalCommunication extComm;
	
	public ResponseTimedOutException(ExternalCommunication extComm) {
		this.extComm = extComm;
	}
	
	public ExternalCommunication getExternalCommunication() {
		return extComm;
	}
}
