package eu.robojob.millassist.ui.configure.device.processing.cnc;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.robot.RobotAirblowSettings;
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
			putStep.getRobotSettings().setSmoothPoint(deviceSettings.getClamping(putStep.getDeviceSettings().getWorkArea()).getSmoothToPoint());
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
		if (!airblow) {
			putStep.getRobotSettings().clearAirblowSettings();
		}
	}
	
	void changedClamping(String clampingName) {
		if (clampingName != null) {
			int clampingId = putStep.getRobotSettings().getWorkArea().getClampingByName(clampingName).getId();
			RobotAirblowSettings airblowSettings;
			if (putStep.getRobotSettings().getRobotAirblowSettings(clampingId) == null) {
				airblowSettings = new RobotAirblowSettings();
				putStep.getRobotSettings().addRobotAirblowSettings(clampingId, airblowSettings);
			} else {
				airblowSettings = putStep.getRobotSettings().getRobotAirblowSettings(clampingId);
			}
			getView().setTopCoord(airblowSettings.getTopCoord());
			getView().setBottomCoord(airblowSettings.getBottomCoord());
			getView().refreshCoordboxes();
		}
	}
	
	@Override
	public boolean isConfigured() {
		if (!isAirblowConfigured() && putStep.getRobotSettings().isDoMachineAirblow()) {
			return false;
		}
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			return true;
		} 
		return false;
	}
	
	private boolean isAirblowConfigured() {
		for (RobotAirblowSettings airblowSettings: putStep.getRobotSettings().getRobotAirblowSettings().values()) {
			if (!(airblowSettings.getBottomCoord().getX() < airblowSettings.getTopCoord().getX() &&
				airblowSettings.getBottomCoord().getY() < airblowSettings.getTopCoord().getY()))
				return false;
		}
		return true;
	}
	
	public void changedTIM(final boolean newValue) {
		putStep.getRobotSettings().setTurnInMachine(newValue);
	}
	
	Set<String> getSelectedClampings() {
		Set<String> clNames = new HashSet<String>();
		for (Clamping clamping: putStep.getRobotSettings().getWorkArea().getAllActiveClampings()) {
			clNames.add(clamping.getName());
		}
		return clNames;
	}
}
