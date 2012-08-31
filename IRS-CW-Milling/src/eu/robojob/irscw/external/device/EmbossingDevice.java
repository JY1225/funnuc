package eu.robojob.irscw.external.device;

import java.io.IOException;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePickSettings;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePutSettings;

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

}
