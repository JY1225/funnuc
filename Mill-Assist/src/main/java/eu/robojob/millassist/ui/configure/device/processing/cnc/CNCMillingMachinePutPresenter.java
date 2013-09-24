package eu.robojob.millassist.ui.configure.device.processing.cnc;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class CNCMillingMachinePutPresenter extends AbstractFormPresenter<CNCMillingMachinePutView, CNCMillingMachineMenuPresenter> {

	private PutStep putStep;
	private DeviceSettings deviceSettings;
	
	public CNCMillingMachinePutPresenter(final CNCMillingMachinePutView view, final PutStep putStep, final DeviceSettings deviceSettings) {
		super(view);
		this.putStep = putStep;
		this.deviceSettings = deviceSettings;
		view.setPutStep(putStep);
		view.setDeviceSettings(deviceSettings);
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	public void changedSmoothX(final float smoothX) {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			putStep.getRobotSettings().getSmoothPoint().setX(smoothX);
		}  else {
			putStep.getRobotSettings().setSmoothPoint(new Coordinates(smoothX, 0, 0, 0, 0, 0));
		}
		getView().refresh();
	}
	
	public void changedSmoothY(final float smoothY) {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			putStep.getRobotSettings().getSmoothPoint().setY(smoothY);
		} else {
			putStep.getRobotSettings().setSmoothPoint(new Coordinates(0, smoothY, 0, 0, 0, 0));
		}
		getView().refresh();
	}
	
	public void changedSmoothZ(final float smoothZ) {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			putStep.getRobotSettings().getSmoothPoint().setZ(smoothZ);
		} else {
			putStep.getRobotSettings().setSmoothPoint(new Coordinates(0, 0, smoothZ, 0, 0, 0));
		}
		getView().refresh();
	}
	
	public void resetSmooth() {
		if (deviceSettings.getClamping(putStep.getDeviceSettings().getWorkArea()) != null) {
			putStep.getRobotSettings().setSmoothPoint(deviceSettings.getClamping(putStep.getDeviceSettings().getWorkArea()).getSmoothFromPoint());
			getView().refresh();
		}
	}

	public void changedReleaseBefore(final boolean releaseBefore) {
		if (putStep.getRobotSettings().isReleaseBeforeMachine() != releaseBefore) {
			putStep.getRobotSettings().setReleaseBeforeMachine(releaseBefore);
			getView().refresh();
		}
	}
	
	public void changedAirblow(final boolean airblow) {
		putStep.getRobotSettings().setDoMachineAirblow(airblow);
	}
	
	@Override
	public boolean isConfigured() {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			return true;
		}
		return false;
	}
}
