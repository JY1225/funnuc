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
	
	public String getMessage() {
		return "Het duurde te lang voordat een antwoord kwam van " + extComm;
	}
}
