package eu.robojob.millassist.ui.configure.device.processing.cnc;

import java.util.Set;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.ui.general.model.DeviceInformation;
import eu.robojob.millassist.util.Translator;

@Deprecated
public class CNCMillingMachineAirblowPresenter extends AbstractFormPresenter<CNCMillingMachineAirblowView, CNCMillingMachineMenuPresenter> {

	private DeviceInformation deviceInfo;
	
	private static final String AIRBLOW_PICK_NOT_CORRECT = "CNCMillingMachineAirblowPresenter.pickNotCorrect";
	private static final String AIRBLOW_PUT_NOT_CORRECT = "CNCMillingMachineAirblowPresenter.putNotCorrect";

	
	public CNCMillingMachineAirblowPresenter(CNCMillingMachineAirblowView view, DeviceInformation deviceInfo) {
		super(view);
		this.deviceInfo = deviceInfo;
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	Set<Clamping> getActiveClampings() {
		return getWorkArea().getAllActiveClampings();
	}
	
	private void changedClamping(int clampingId, boolean isPut) {
		AirblowSquare airblowSettings;
		if (isPut) {			
			airblowSettings = getAirblowSettings(deviceInfo.getPutStep().getRobotSettings(), clampingId);
		} else {
			airblowSettings = getAirblowSettings(deviceInfo.getPickStep().getRobotSettings(), clampingId);
		}
		getView().setTopCoord(airblowSettings.getTopCoord(), isPut);
		getView().setBottomCoord(airblowSettings.getBottomCoord(), isPut);
		getView().refreshCoordBoxes();
	}
	
	void changedClamping(String clampingName, boolean isPut) {
		if (clampingName != null) {
			int clampingId = getWorkArea().getClampingByName(clampingName).getId();
			changedClamping(clampingId, isPut);
		}
	}

	private AirblowSquare getAirblowSettings(AbstractRobotActionSettings<?> robotSettings, int clampingId) {
		AirblowSquare airblowSettings;
		if (robotSettings.getAirblowSquare(clampingId) == null) {
			airblowSettings = new AirblowSquare();
			robotSettings.addRobotAirblowSettings(clampingId, airblowSettings);
		} else {
			airblowSettings = robotSettings.getAirblowSquare(clampingId);
		}
		return airblowSettings;
	}
	
	void setMachineAirblow() {
		if (deviceInfo.getPutStep().getRobotSettings().isDoMachineAirblow()) {
			getView().setPutAirblowSelected(true);
		} else {
			getView().setPutAirblowSelected(false);
		}
		if (deviceInfo.getPickStep().getRobotSettings().isDoMachineAirblow()) {
			getView().setPickAirblowSelected(true);
		} else {
			getView().setPickAirblowSelected(false);
		}
	}
	
	void setDoMachineAirblow(boolean doMachineAirblow, boolean isPut) {
		if (isPut) {
			deviceInfo.getPutStep().getRobotSettings().setDoMachineAirblow(doMachineAirblow);
			if (!doMachineAirblow) {
				deviceInfo.getPutStep().getRobotSettings().clearAirblowSettings();
			}
		} else {
			deviceInfo.getPickStep().getRobotSettings().setDoMachineAirblow(doMachineAirblow);
			if (!doMachineAirblow) {
				deviceInfo.getPickStep().getRobotSettings().clearAirblowSettings();
			}
		}
		addProcessFlowEvent(isPut);
	}
	
	private WorkArea getWorkArea() {
		return deviceInfo.getPutStep().getDeviceSettings().getWorkArea();
	}
	
	private void addProcessFlowEvent(boolean isPut) {
		if (isPut) {
			deviceInfo.getPutStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPutStep().getProcessFlow(), deviceInfo.getPutStep(), true));
		} else {
			deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPickStep().getProcessFlow(), deviceInfo.getPickStep(), true));
		}
	}
	
	@Override
	public boolean isConfigured() {
		if (deviceInfo.getPutStep().getRobotSettings().isDoMachineAirblow()) {
			if (!isAirblowConfigured(deviceInfo.getPutStep().getRobotSettings())) {
				getView().showNotification(Translator.getTranslation(AIRBLOW_PUT_NOT_CORRECT), Type.WARNING);
				return false;
			}
		}
		if (deviceInfo.getPickStep().getRobotSettings().isDoMachineAirblow()) {
			if (!isAirblowConfigured(deviceInfo.getPickStep().getRobotSettings())) {
				getView().showNotification(Translator.getTranslation(AIRBLOW_PICK_NOT_CORRECT), Type.WARNING);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check whether all the robotAirblowSettings are having coordinates that can form a square
	 * @param robotSettings
	 * @return
	 */
	private boolean isAirblowConfigured(AbstractRobotActionSettings<?> robotSettings) {
		for (AirblowSquare airblowSettings: robotSettings.getRobotAirblowSettings().values()) {
			if (!(airblowSettings.getBottomCoord().getX() < airblowSettings.getTopCoord().getX() &&
				airblowSettings.getBottomCoord().getY() < airblowSettings.getTopCoord().getY()))
				return false;
		}
		return true;
	}

	public void changedCoordinate(boolean isPut) {
		getView().hideNotification();
		addProcessFlowEvent(isPut);
		isConfigured();
	}
}
