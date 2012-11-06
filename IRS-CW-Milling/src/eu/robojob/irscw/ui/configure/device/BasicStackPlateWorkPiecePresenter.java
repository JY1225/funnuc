package eu.robojob.irscw.ui.configure.device;

import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.BasicStackPlateSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class BasicStackPlateWorkPiecePresenter extends AbstractFormPresenter<BasicStackPlateWorkPieceView, BasicStackPlateMenuPresenter> {

	private BasicStackPlateSettings deviceSettings;
	private PickStep pickStep;
	private WorkPieceDimensions dimensions;
	private WorkPieceOrientation orientation;
	
	public BasicStackPlateWorkPiecePresenter(BasicStackPlateWorkPieceView view, PickStep pickStep, BasicStackPlateSettings deviceSettings) {
		super(view);
		this.pickStep = pickStep;
		
		this.deviceSettings = deviceSettings;
		
		this.dimensions = pickStep.getRobotSettings().getWorkPiece().getDimensions();
		if (dimensions == null) {
			dimensions = new WorkPieceDimensions();
			pickStep.getRobotSettings().getWorkPiece().setDimensions(dimensions);
		}
		deviceSettings.setDimensions(dimensions);
					
		orientation = deviceSettings.getOrientation();
		if (orientation == null) {
			orientation = WorkPieceOrientation.HORIZONTAL;
			deviceSettings.setOrientation(orientation);
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
		pickStep.getRobotSettings().getWorkPiece().setDimensions(dimensions);
	}
	
	public void changedLength(float length) {
		dimensions.setLength(length);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		pickStep.getRobotSettings().getWorkPiece().setDimensions(dimensions);
	}
	
	public void changedHeight(float height) {
		dimensions.setHeight(height);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		pickStep.getRobotSettings().getWorkPiece().setDimensions(dimensions);
	}
	
	public void changedAmount(int amount) {
		deviceSettings.setAmount(amount);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		pickStep.getProcessFlow().setTotalAmount(amount);
	}
	
	public void changedOrientation(WorkPieceOrientation orientation) {
		deviceSettings.setOrientation(orientation);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		view.refresh();
	}

	@Override
	public boolean isConfigured() {
		BasicStackPlate plate = ((BasicStackPlate) pickStep.getDevice());
		if (
				(dimensions != null) && (orientation != null) &&
					(plate.getLayout().getStackingPositions() != null) && (plate.getLayout().getStackingPositions().size() > 0)
			) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setMaxAmount() {
		BasicStackPlate plate = ((BasicStackPlate) pickStep.getDevice());
		deviceSettings.setAmount(plate.getLayout().getStackingPositions().size());
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		view.refresh();
	}

}
