package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.positioning.Coordinates;

public class Conveyor extends AbstractStackingDevice {

	public Conveyor(String id, List<Zone> zones) {
		super(id, zones);
	}
	
	public Conveyor(String id) {
		this(id, new ArrayList<Zone>());
	}

	@Override
	public boolean canPickWorkpiece() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPutWorkpiece() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Coordinates getPickLocation(WorkArea workArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinates getPutLocation(WorkArea workArea) {
		// TODO Auto-generated method stub
		return null;
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
	public String getStatus() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeviceType getType() {
		return DeviceType.STACKING;
	}
	
	public static class ConveyorPickSettings extends AbstractStackingDevicePickSettings {
		public ConveyorPickSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
		}

		@Override
		public boolean isTeachingNeeded() {
			return step.getProcessFlow().needsTeaching();
		}
	}
	
	public static class ConveyorPutSettings extends AbstractStackingDevicePutSettings {
		public ConveyorPutSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
		}

		@Override
		public boolean isTeachingNeeded() {
			return step.getProcessFlow().needsTeaching();
		}
	}
	
	public static class ConveyorInterventionSettings extends AbstractStackingDeviceInterventionSettings {
		public ConveyorInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}

}
