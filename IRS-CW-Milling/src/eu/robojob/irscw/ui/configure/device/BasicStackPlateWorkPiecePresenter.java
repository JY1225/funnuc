package eu.robojob.irscw.ui.configure.device;

import eu.robojob.irscw.external.device.BasicStackPlate;
import eu.robojob.irscw.external.device.BasicStackPlate.BasicStackPlateSettings;
import eu.robojob.irscw.external.device.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class BasicStackPlateWorkPiecePresenter extends AbstractFormPresenter<BasicStackPlateWorkPieceView, BasicStackPlateMenuPresenter> {

	private BasicStackPlateSettings deviceSettings;
	private PickStep pickStep;
	private WorkPieceDimensions dimensions;
	
	public BasicStackPlateWorkPiecePresenter(BasicStackPlateWorkPieceView view, PickStep pickStep, BasicStackPlateSettings deviceSettings) {
		super(view);
		this.pickStep = pickStep;
		
		this.deviceSettings = deviceSettings;
		
		this.dimensions = deviceSettings.getDimensions();
		if (dimensions == null) {
			dimensions = new WorkPieceDimensions();
		}
			
		view.setPickStep(pickStep);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}
	
	public void changedWidth(float width) {
		dimensions.setWidth(width);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
	}
	
	public void changedLength(float length) {
		dimensions.setLength(length);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
	}
	
	public void changedHeight(float height) {
		dimensions.setHeight(height);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
	}
	
	public void changedAmount(int amount) {
		deviceSettings.setAmount(amount);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
	}
	
	public void changedOrientation(WorkPieceOrientation orientation) {
		deviceSettings.setOrientation(orientation);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		view.refresh();
	}

	@Override
	public boolean isConfigured() {
		BasicStackPlate plate = ((BasicStackPlate) pickStep.getDevice());
		if ((plate.getRawStackingPositions() != null) && (plate.getRawStackingPositions().size() > 0)) {
			return true;
		} else {
			return false;
		}
	}

}
