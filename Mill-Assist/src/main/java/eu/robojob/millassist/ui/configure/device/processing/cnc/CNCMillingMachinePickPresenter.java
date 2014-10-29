package eu.robojob.millassist.ui.configure.device.processing.cnc;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

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
	
	
	public void changedAirblow(final boolean airblow) {
		pickStep.getRobotSettings().setDoMachineAirblow(airblow);
	}
	
	public void changedTIM(final boolean turnInMachine) {
		pickStep.getRobotSettings().setDoTIM(turnInMachine);
	}

	@Override
	public boolean isConfigured() {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			return true;
		}
		return false;
	}

}
