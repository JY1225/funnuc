package eu.robojob.millassist.ui.configure.device.processing.cnc;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;

public class CNCMillingMachinePickPresenter extends AbstractFormPresenter<CNCMillingMachinePickView, CNCMillingMachineMenuPresenter> {

	private PickStep pickStep;
	private DeviceSettings deviceSettings;
	
	private static final String AIRBLOW_NOT_CORRECT = "CNCMillingMachinePickPresenter.airblowNotCorrect";
	private static final String AIRBLOW_OUTSIDE_BOUND = "CNCMillingMachinePickPresenter.airblowOutOfBound";

	
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
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		getView().refresh();
	}
	
	public void changedSmoothY(final float smoothY) {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			pickStep.getRobotSettings().getSmoothPoint().setY(smoothY);
		} else {
			pickStep.getRobotSettings().setSmoothPoint(new Coordinates(0, smoothY, 0, 0, 0, 0));
		}
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		getView().refresh();
	}
	
	public void changedSmoothZ(final float smoothZ) {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			pickStep.getRobotSettings().getSmoothPoint().setZ(smoothZ);
		} else {
			pickStep.getRobotSettings().setSmoothPoint(new Coordinates(0, 0, smoothZ, 0, 0, 0));
		}
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		getView().refresh();
	}
	
	public void resetSmooth() {
		if (deviceSettings.getClamping(pickStep.getDeviceSettings().getWorkArea()) != null) {
			pickStep.getRobotSettings().setSmoothPoint(deviceSettings.getClamping(pickStep.getDeviceSettings().getWorkArea()).getSmoothFromPoint());
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			getView().refresh();
		}
	}
	
	public void changedAirblow(final boolean airblow) {
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		pickStep.getRobotSettings().setDoMachineAirblow(airblow);
		if (!airblow) {
			pickStep.getRobotSettings().clearAirblowSettings();
		}
		changedCoordinate();
	}
	
	void changedClamping(final String clampingName) {
		if (clampingName != null) {
			int clampingId = pickStep.getRobotSettings().getWorkArea().getClampingByName(clampingName).getId();
			AirblowSquare airblowSettings;
			if (pickStep.getRobotSettings().getAirblowSquare(clampingId) == null) {
				airblowSettings = new AirblowSquare();
				pickStep.getRobotSettings().addRobotAirblowSettings(clampingId, airblowSettings);
			} else {
				airblowSettings = pickStep.getRobotSettings().getAirblowSquare(clampingId);
			}
			getView().setTopCoord(airblowSettings.getTopCoord());
			getView().setBottomCoord(airblowSettings.getBottomCoord());
			getView().refreshCoordboxes();
		}
	}

	@Override
	public boolean isConfigured() {
		if (pickStep.getRobotSettings().isDoMachineAirblow() && pickStep.getRobotSettings().getRobotAirblowSettings().isEmpty()) {
			getView().showNotification(Translator.getTranslation(AIRBLOW_NOT_CORRECT), Type.WARNING);
			return false;
		}
		if (!isAirblowConfigured() && pickStep.getRobotSettings().isDoMachineAirblow()) {
			getView().showNotification(Translator.getTranslation(AIRBLOW_NOT_CORRECT), Type.WARNING);
			return false;
		}
		if (pickStep.getRobotSettings().isDoMachineAirblow() && !isInsideMachineBoundaries()) {
			getView().showNotification(Translator.getTranslation(AIRBLOW_OUTSIDE_BOUND), Type.WARNING);
			return false;
		}
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			return true;
		}
		return false;
	}
	
	private boolean isAirblowConfigured() {
		for (AirblowSquare airblowSettings: pickStep.getRobotSettings().getRobotAirblowSettings().values()) {
			if (!(airblowSettings.getBottomCoord().getX() < airblowSettings.getTopCoord().getX() &&
				airblowSettings.getBottomCoord().getY() < airblowSettings.getTopCoord().getY()))
				return false;
		}
		return true;
	}

	public void changedTIM(final boolean newValue) {
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		pickStep.getRobotSettings().setTurnInMachine(newValue);
	}

	Set<String> getSelectedClampings() {
		Set<String> clNames = new HashSet<String>();
		for (Clamping clamping: pickStep.getRobotSettings().getWorkArea().getAllActiveClampings()) {
			clNames.add(clamping.getName());
		}
		return clNames;
	}
	
	public void changedCoordinate() {
		getView().hideNotification();
		//FIXME - niet altijd sturen!!
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		isConfigured();
	}
	
	private boolean isInsideMachineBoundaries() {
		Zone zone = pickStep.getRobotSettings().getWorkArea().getZone();
		if (zone.getBoundaries() != null) {
			AirblowSquare square = zone.getBoundaries();
			for (int clampingId: pickStep.getRobotSettings().getRobotAirblowSettings().keySet()) {
				Clamping clamping = pickStep.getRobotSettings().getWorkArea().getClampingById(clampingId);
				AirblowSquare clampingAir = pickStep.getRobotSettings().getAirblowSquare(clampingId);
				Coordinates lowerLeftCorner = Coordinates.add(clampingAir.getBottomCoord(), clamping.getRelativePosition());
				Coordinates upperRightCorner = Coordinates.add(clampingAir.getTopCoord(), clamping.getRelativePosition());
				if (!lowerLeftCorner.isInsideSquare(square) || !upperRightCorner.isInsideSquare(square)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
