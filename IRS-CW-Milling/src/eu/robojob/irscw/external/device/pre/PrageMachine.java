package eu.robojob.irscw.external.device.pre;

import java.util.List;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.robot.FanucRobotCommunication;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class PrageMachine extends AbstractProcessingDevice {

	private FanucRobotCommunication fanucRobotCommunication;
	
	public PrageMachine(String id, FanucRobotCommunication fanucRobotCommunication) {
		super(id, false);
		this.fanucRobotCommunication = fanucRobotCommunication;
	}
	
	public PrageMachine (String id, List<Zone> zones, FanucRobotCommunication fanucRobotCommunication) {
		super(id, zones, false);
		this.fanucRobotCommunication = fanucRobotCommunication;
	}

	@Override
	public void startCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// TODO commando sturen om klem te sluiten (en weer te openen)
	}

	@Override
	public void prepareForStartCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws CommunicationException, DeviceActionException {
	}

	@Override
	public boolean validateStartCyclusSettings(AbstractProcessingDeviceStartCyclusSettings startCyclusSettings) {
		for (Zone zone : zones) {
			if (zone.getWorkAreas().contains(startCyclusSettings.getWorkArea())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canPick(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException {
		return true;
	}
	@Override
	public boolean canPut(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		return true;
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException, InterruptedException {
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// TODO klem openen
	}

	@Override
	public void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) throws CommunicationException, DeviceActionException {}
	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException {}
	@Override
	public void putFinished(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException {}
	@Override
	public void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) throws CommunicationException, DeviceActionException {}

	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// TODO commando sturen om klem te openen
	}
	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// TODO commando sturen om klem te sluiten
	}

	@Override
	public void loadDeviceSettings(AbstractDeviceSettings deviceSettings) {
	}

	@Override
	public AbstractDeviceSettings getDeviceSettings() {
		return null;
	}

	@Override
	public boolean validatePickSettings(AbstractDevicePickSettings pickSettings) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validatePutSettings(AbstractDevicePutSettings putSettings) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validateInterventionSettings(
			AbstractDeviceInterventionSettings interventionSettings) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AbstractDeviceInterventionSettings getInterventionSettings(
			AbstractDevicePickSettings pickSettings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractDeviceInterventionSettings getInterventionSettings(
			AbstractDevicePutSettings putSettings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinates getPickLocation(WorkArea workArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinates getPutLocation(WorkArea workArea,
			WorkPieceDimensions workPieceDimensions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopCurrentAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DeviceType getType() {
		// TODO Auto-generated method stub
		return null;
	}
 
}
