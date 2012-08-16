package eu.robojob.irscw.external.device;

import eu.robojob.irscw.external.communication.SocketConnection;

public class EmbossingDevice extends AbstractProcessingDevice {

	private SocketConnection socketConnection;
	
	public EmbossingDevice(String id, boolean isInvasive, SocketConnection socketConnection) {
		super(id, false);
		this.socketConnection = socketConnection;
	}

	@Override
	public void startCyclus(WorkArea workArea, AbstractProcessingDeviceStartCyclusSettings startCylusSettings) {
		// TODO implement method
	}

	@Override
	public void prepareForStartCyclus(WorkArea workArea, AbstractProcessingDeviceStartCyclusSettings startCylusSettings) {
		// TODO implement method
	}

	@Override
	public void prepareForPick(WorkArea workArea, AbstractDevicePickSettings pickSettings) {
		// TODO implement method
	}

	@Override
	public void prepareForPut(WorkArea workArea, AbstractDevicePutSettings putSettings) {
		// TODO implement method
	}

	@Override
	public void prepareForIntervention(WorkArea workArea, AbstractDeviceInterventionSettings interventionSettings) {
		// TODO implement method
	}

	@Override
	public void pickFinished(WorkArea workArea, AbstractDevicePickSettings pickSettings) {
		// TODO implement method
	}

	@Override
	public void putFinished(WorkArea workArea, AbstractDevicePutSettings putSettings) {
		// TODO implement method
	}

	@Override
	public void interventionFinished(WorkArea workArea, AbstractDeviceInterventionSettings interventionSettings) {
		// TODO implement method
	}

	@Override
	public void releasePiece(WorkArea workArea, AbstractDeviceClampingSettings clampingSettings) {
		// TODO implement method
	}

	@Override
	public void grabPiece(WorkArea workArea, AbstractDeviceClampingSettings clampingSettings) {
		// TODO implement method
	}

	@Override
	public String getStatus() {
		// TODO implement method		
		return null;
	}

}
