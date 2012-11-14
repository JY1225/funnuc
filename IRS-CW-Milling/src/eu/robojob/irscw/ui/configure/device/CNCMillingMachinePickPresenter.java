package eu.robojob.irscw.ui.configure.device;

import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.event.DataChangedEvent;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class CNCMillingMachinePickPresenter extends AbstractFormPresenter<CNCMillingMachinePickView, CNCMillingMachineMenuPresenter> {

	private PickStep pickStep;
	private CNCMillingMachineSettings deviceSettings;
	//private Logger logger = Logger.getLogger(CNCMillingMachinePickPresenter.class);
	
	public CNCMillingMachinePickPresenter(CNCMillingMachinePickView view, PickStep pickStep, CNCMillingMachineSettings deviceSettings) {
		super(view);
		this.pickStep = pickStep;
		this.deviceSettings = deviceSettings;
		view.setPickStep(pickStep);
		view.setDeviceSettings(deviceSettings);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	public void changedSmoothX(float smoothX) {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			pickStep.getRobotSettings().getSmoothPoint().setX(smoothX);
		}  else {
			pickStep.getRobotSettings().setSmoothPoint(new Coordinates(smoothX, 0, 0, 0, 0, 0));
		}
		view.refresh();
	}
	
	public void changedSmoothY(float smoothY) {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			pickStep.getRobotSettings().getSmoothPoint().setY(smoothY);
		} else {
			pickStep.getRobotSettings().setSmoothPoint(new Coordinates(0, smoothY, 0, 0, 0, 0));
		}
		view.refresh();
	}
	
	public void changedSmoothZ(float smoothZ) {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			pickStep.getRobotSettings().getSmoothPoint().setZ(smoothZ);
		} else {
			pickStep.getRobotSettings().setSmoothPoint(new Coordinates(0, 0, smoothZ, 0, 0, 0));
		}
		view.refresh();
	}
	
	public void resetSmooth() {
		if (deviceSettings.getClamping(pickStep.getDeviceSettings().getWorkArea()) != null) {
			pickStep.getRobotSettings().setSmoothPoint(deviceSettings.getClamping(pickStep.getDeviceSettings().getWorkArea()).getSmoothFromPoint());
			view.refresh();
		}
	}
	
	public void changedHeight(float height) {
		if (pickStep.getRobotSettings().getWorkPiece().getDimensions() != null) {
			pickStep.getRobotSettings().getWorkPiece().getDimensions().setHeight(height);
		} else {
			WorkPieceDimensions dimensions = new WorkPieceDimensions();
			dimensions.setHeight(height);
			pickStep.getRobotSettings().getWorkPiece().setDimensions(dimensions);
		}
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}

	@Override
	public boolean isConfigured() {
		if ((pickStep.getRobotSettings().getSmoothPoint() != null) && (pickStep.getRobotSettings().getWorkPiece().getDimensions() != null)) {
			return true;
		} else {
			return false;
		}
	}
}
