package eu.robojob.irscw.ui.configure.device;

import eu.robojob.irscw.external.device.processing.cnc.CNCMillingMachineSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;

public class CNCMillingMachinePutPresenter extends AbstractFormPresenter<CNCMillingMachinePutView, CNCMillingMachineMenuPresenter> {

	private PutStep putStep;
	private CNCMillingMachineSettings deviceSettings;
	
	public CNCMillingMachinePutPresenter(CNCMillingMachinePutView view, PutStep putStep, CNCMillingMachineSettings deviceSettings) {
		super(view);
		this.putStep = putStep;
		this.deviceSettings = deviceSettings;
		view.setPutStep(putStep);
		view.setDeviceSettings(deviceSettings);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	public void changedSmoothX(float smoothX) {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			putStep.getRobotSettings().getSmoothPoint().setX(smoothX);
		}  else {
			putStep.getRobotSettings().setSmoothPoint(new Coordinates(smoothX, 0, 0, 0, 0, 0));
		}
		view.refresh();
	}
	
	public void changedSmoothY(float smoothY) {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			putStep.getRobotSettings().getSmoothPoint().setY(smoothY);
		} else {
			putStep.getRobotSettings().setSmoothPoint(new Coordinates(0, smoothY, 0, 0, 0, 0));
		}
		view.refresh();
	}
	
	public void changedSmoothZ(float smoothZ) {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			putStep.getRobotSettings().getSmoothPoint().setZ(smoothZ);
		} else {
			putStep.getRobotSettings().setSmoothPoint(new Coordinates(0, 0, smoothZ, 0, 0, 0));
		}
		view.refresh();
	}
	
	public void resetSmooth() {
		if (deviceSettings.getClamping(putStep.getDeviceSettings().getWorkArea()) != null) {
			putStep.getRobotSettings().setSmoothPoint(deviceSettings.getClamping(putStep.getDeviceSettings().getWorkArea()).getSmoothFromPoint());
			view.refresh();
		}
	}

	@Override
	public boolean isConfigured() {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			return true;
		} else {
			return false;
		}
	}
}
