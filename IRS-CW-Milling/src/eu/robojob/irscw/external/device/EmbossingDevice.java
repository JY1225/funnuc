package eu.robojob.irscw.external.device;

import eu.robojob.irscw.external.communication.SocketConnection;

public class EmbossingDevice extends AbstractProcessingDevice {

	private SocketConnection socketConnection;
	
	public EmbossingDevice(String id, boolean isInvasive, SocketConnection socketConnection) {
		super(id, false);
		this.socketConnection = socketConnection;
	}

	@Override
	public void startCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) {
		// TODO implement method
	}

	@Override
	public void prepareForStartCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) {
		// TODO implement method
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings) {
		// TODO implement method
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings) {
		// TODO implement method
	}

	@Override
	public void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) {
		// TODO implement method
	}

	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings) {
		// TODO implement method
	}

	@Override
	public void putFinished(AbstractDevicePutSettings putSettings) {
		// TODO implement method
	}

	@Override
	public void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) {
		// TODO implement method
	}

	@Override
	public void releasePiece(AbstractDeviceClampingSettings clampingSettings) {
		// TODO implement method
	}

	@Override
	public void grabPiece(AbstractDeviceClampingSettings clampingSettings) {
		// TODO implement method
	}

	@Override
	public String getStatus() {
		// TODO implement method		
		return null;
	}

}
