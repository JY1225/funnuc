package eu.robojob.irscw.external.device;

import java.io.IOException;

import eu.robojob.irscw.external.communication.SocketConnection;

public class EmbossingDevice extends AbstractProcessingDevice {

	private SocketConnection socketConnection;
	
	public EmbossingDevice(String id, SocketConnection socketConnection) {
		super(id, false);
		this.socketConnection = socketConnection;
	}

	@Override
	public void startCyclus(
			AbstractProcessingDeviceStartCyclusSettings startCylusSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareForStartCyclus(
			AbstractProcessingDeviceStartCyclusSettings startCylusSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareForIntervention(
			AbstractDeviceInterventionSettings interventionSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putFinished(AbstractDevicePutSettings putSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interventionFinished(
			AbstractDeviceInterventionSettings interventionSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeviceType getType() {
		return DeviceType.PRE_PROCESSING;
	}
	
	public static class EmbossingDevicePickSettings extends AbstractProcessingDevicePickSettings {
		public EmbossingDevicePickSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
		}

		@Override
		public boolean isTeachingNeeded() {
			return step.getProcessFlow().needsTeaching();
		}
	}
	
	public static class EmbossingDevicePutSettings extends AbstractProcessingDevicePutSettings {
		public EmbossingDevicePutSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
		}

		@Override
		public boolean isTeachingNeeded() {
			return false;
		}
	}
	
	public static class EmbossingDeviceInterventionSettings extends AbstractProcessingDeviceInterventionSettings {
		public EmbossingDeviceInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	public static class EmbossingDeviceStartCyclusSettings extends AbstractProcessingDeviceStartCyclusSettings {
		public EmbossingDeviceStartCyclusSettings(WorkArea workArea) {
			super(workArea);
		}
	}

}
