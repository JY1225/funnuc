package eu.robojob.irscw.ui.main.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;

public class CNCMillingMachinePickPresenter extends AbstractFormPresenter<CNCMillingMachinePickView, CNCMillingMachineMenuPresenter> {

	private PickStep pickStep;
	private Logger logger = Logger.getLogger(CNCMillingMachinePickPresenter.class);
	
	public CNCMillingMachinePickPresenter(CNCMillingMachinePickView view, PickStep pickStep) {
		super(view);
		this.pickStep = pickStep;
		view.setPickStep(pickStep);
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
		if (pickStep.getDeviceSettings().getClamping() != null) {
			pickStep.getRobotSettings().setSmoothPoint(pickStep.getDeviceSettings().getClamping().getSmoothFromPoint());
		}
	}
}
