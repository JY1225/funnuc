package eu.robojob.irscw.ui.configure.device.processing.cnc;

import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.event.DataChangedEvent;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class CNCMillingMachinePickPresenter extends AbstractFormPresenter<CNCMillingMachinePickView, CNCMillingMachineMenuPresenter> {

	private PickStep pickStep;
	private DeviceSettings deviceSettings;
	
	public CNCMillingMachinePickPresenter(final CNCMillingMachinePickView view, final PickStep pickStep, final DeviceSettings deviceSettings) {
		super(view);
		this.pickStep = pickStep;
		this.deviceSettings = deviceSettings;
		view.setPickStep(pickStep);
		view.setDeviceSettings(deviceSettings);
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	public void changedSmoothX(final float smoothX) {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			pickStep.getRobotSettings().getSmoothPoint().setX(smoothX);
		}  else {
			pickStep.getRobotSettings().setSmoothPoint(new Coordinates(smoothX, 0, 0, 0, 0, 0));
		}
		getView().refresh();
	}
	
	public void changedSmoothY(final float smoothY) {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			pickStep.getRobotSettings().getSmoothPoint().setY(smoothY);
		} else {
			pickStep.getRobotSettings().setSmoothPoint(new Coordinates(0, smoothY, 0, 0, 0, 0));
		}
		getView().refresh();
	}
	
	public void changedSmoothZ(final float smoothZ) {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			pickStep.getRobotSettings().getSmoothPoint().setZ(smoothZ);
		} else {
			pickStep.getRobotSettings().setSmoothPoint(new Coordinates(0, 0, smoothZ, 0, 0, 0));
		}
		getView().refresh();
	}
	
	public void resetSmooth() {
		if (deviceSettings.getClamping(pickStep.getDeviceSettings().getWorkArea()) != null) {
			pickStep.getRobotSettings().setSmoothPoint(deviceSettings.getClamping(pickStep.getDeviceSettings().getWorkArea()).getSmoothFromPoint());
			getView().refresh();
		}
	}
	
	public void changedWidth(final float width) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setWidth(width);
		pickStep.setRelativeTeachedOffset(null);
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void resetWidth() {
		WorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		changedWidth(prevPickDimensions.getWidth());
		getView().refresh();
	}
	
	public void changedLength(final float length) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setLength(length);
		pickStep.setRelativeTeachedOffset(null);
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void resetLength() {
		WorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		changedLength(prevPickDimensions.getLength());
		getView().refresh();
	}
	
	public void changedHeight(final float height) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setHeight(height);
		pickStep.setRelativeTeachedOffset(null);
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void resetHeight() {
		WorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		changedHeight(prevPickDimensions.getHeight());
		getView().refresh();
	}
	
	private WorkPieceDimensions getPreviousPickDimensions() {
		int pickIndex = pickStep.getProcessFlow().getStepIndex(pickStep);
		for (int i = pickIndex - 1; i >= 0; i--) {
			AbstractProcessStep step = pickStep.getProcessFlow().getStep(i);
			if ((step instanceof PickStep) && !(step instanceof PickAfterWaitStep)) {
				return ((PickStep) step).getRobotSettings().getWorkPiece().getDimensions();
			}
		}
		throw new IllegalArgumentException("Couldn't find previous pick step");
	}

	@Override
	public boolean isConfigured() {
		if ((pickStep.getRobotSettings().getSmoothPoint() != null) && (pickStep.getRobotSettings().getWorkPiece().getDimensions() != null)) {
			WorkPieceDimensions myDimensions = pickStep.getRobotSettings().getWorkPiece().getDimensions();
			WorkPieceDimensions prevDimensions = getPreviousPickDimensions();
			if ((myDimensions.getWidth() > 0) && (myDimensions.getLength() > 0) && (myDimensions.getHeight() > 0) && (myDimensions.getWidth() <= prevDimensions.getWidth()) && (myDimensions.getLength() <= prevDimensions.getLength()) 
					&& (myDimensions.getHeight() <= prevDimensions.getHeight())) {
				return true;
			}
		}
		return false;
	}
}
