package eu.robojob.millassist.ui.configure.device.processing.cnc;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;

public class CNCMillingMachinePutPresenter extends AbstractFormPresenter<CNCMillingMachinePutView, CNCMillingMachineMenuPresenter> {

	private PutStep putStep;
	private DeviceSettings deviceSettings;
	
	private static final String AIRBLOW_NOT_CORRECT = "CNCMillingMachinePutPresenter.airblowNotCorrect";
	private static final String AIRBLOW_OUTSIDE_BOUND = "CNCMillingMachinePutPresenter.airblowOutOfBound";
	
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
		putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
		getView().refresh();
	}
	
	public void changedSmoothY(final float smoothY) {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			putStep.getRobotSettings().getSmoothPoint().setY(smoothY);
		} else {
			putStep.getRobotSettings().setSmoothPoint(new Coordinates(0, smoothY, 0, 0, 0, 0));
		}
		putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
		getView().refresh();
	}
	
	public void changedSmoothZ(final float smoothZ) {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			putStep.getRobotSettings().getSmoothPoint().setZ(smoothZ);
		} else {
			putStep.getRobotSettings().setSmoothPoint(new Coordinates(0, 0, smoothZ, 0, 0, 0));
		}
		putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
		getView().refresh();
	}
	
	public void resetSmooth() {
		if (deviceSettings.getClamping(putStep.getDeviceSettings().getWorkArea()) != null) {
			putStep.getRobotSettings().setSmoothPoint(deviceSettings.getClamping(putStep.getDeviceSettings().getWorkArea()).getSmoothToPoint());
			getView().refresh();
		}
	}
	
	public void resetAirblow(String clampingName) {
		Clamping clamping = putStep.getRobotSettings().getWorkArea().getClampingByName(clampingName);
		if (clamping != null) {
			AirblowSquare defaultAirblow = clamping.getDefaultAirblowPoints();
			if (putStep.getRobotSettings().getAirblowSquare(clamping.getId()) != null) {
				AirblowSquare clampAirblow = putStep.getRobotSettings().getAirblowSquare(clamping.getId());
				clampAirblow.getBottomCoord().setCoordinateValues(defaultAirblow.getBottomCoord().getCoordValues());
				clampAirblow.getTopCoord().setCoordinateValues(defaultAirblow.getTopCoord().getCoordValues());
			} else {
				AirblowSquare newClampAirblow = new AirblowSquare(new Coordinates(defaultAirblow.getBottomCoord()), new Coordinates(defaultAirblow.getTopCoord()));
				putStep.getRobotSettings().addRobotAirblowSettings(clamping.getId(), newClampAirblow);
			}
			getView().setBottomCoord(putStep.getRobotSettings().getAirblowSquare(clamping.getId()).getBottomCoord());
			getView().setTopCoord(putStep.getRobotSettings().getAirblowSquare(clamping.getId()).getTopCoord());
			putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
			getView().refreshCoordboxes();
		}
	}

	public void changedReleaseBefore(final boolean releaseBefore) {
		if (putStep.getRobotSettings().isReleaseBeforeMachine() != releaseBefore) {
			putStep.getRobotSettings().setReleaseBeforeMachine(releaseBefore);
			putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
			getView().refresh();
		}
	}
	
	public void changedAirblow(final boolean airblow) {
		putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
		putStep.getRobotSettings().setDoMachineAirblow(airblow);
		if (!airblow) {
			putStep.getRobotSettings().clearAirblowSettings();
		}
		changedCoordinate();
	}
	
	void changedClamping(String clampingName) {
		if (clampingName != null) {
			int clampingId = putStep.getRobotSettings().getWorkArea().getClampingByName(clampingName).getId();
			AirblowSquare airblowSettings;
			if (putStep.getRobotSettings().getAirblowSquare(clampingId) == null) {
				airblowSettings = new AirblowSquare();
				putStep.getRobotSettings().addRobotAirblowSettings(clampingId, airblowSettings);
			} else {
				airblowSettings = putStep.getRobotSettings().getAirblowSquare(clampingId);
			}
			getView().setTopCoord(airblowSettings.getTopCoord());
			getView().setBottomCoord(airblowSettings.getBottomCoord());
			getView().refreshCoordboxes();
		}
	}
	
	@Override
	public boolean isConfigured() {
		if (putStep.getRobotSettings().isDoMachineAirblow() && putStep.getRobotSettings().getRobotAirblowSettings().isEmpty()) {
			getView().showNotification(Translator.getTranslation(AIRBLOW_NOT_CORRECT), Type.WARNING);
			return false;
		}
		if (!isAirblowConfigured() && putStep.getRobotSettings().isDoMachineAirblow()) {
			getView().showNotification(Translator.getTranslation(AIRBLOW_NOT_CORRECT), Type.WARNING);
			return false;
		}
		if (putStep.getRobotSettings().isDoMachineAirblow() && !isInsideMachineBoundaries()) {
			getView().showNotification(Translator.getTranslation(AIRBLOW_OUTSIDE_BOUND), Type.WARNING);
			return false;
		}
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			return true;
		} 
		return false;
	}
	
	private boolean isAirblowConfigured() {
		for (AirblowSquare airblowSettings: putStep.getRobotSettings().getRobotAirblowSettings().values()) {
			if (!(airblowSettings.getBottomCoord().getX() < airblowSettings.getTopCoord().getX() &&
				airblowSettings.getBottomCoord().getY() < airblowSettings.getTopCoord().getY()))
				return false;
		}
		return true;
	}
	
	public void changedTIM(final boolean newValue) {
		putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
		putStep.getRobotSettings().setTurnInMachine(newValue);
	}
	
	Set<String> getSelectedClampings() {
		Set<String> clNames = new HashSet<String>();
		for (Clamping clamping: putStep.getRobotSettings().getWorkArea().getAllActiveClampings()) {
			clNames.add(clamping.getName());
		}
		return clNames;
	}

	public void changedCoordinate() {
		getView().hideNotification();
		putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
		isConfigured();
	}
	
	private boolean isInsideMachineBoundaries() {
		Zone zone = putStep.getRobotSettings().getWorkArea().getZone();
		if (zone.getBoundaries() != null) {
			AirblowSquare square = zone.getBoundaries();
			for (int clampingId: putStep.getRobotSettings().getRobotAirblowSettings().keySet()) {
				Clamping clamping = putStep.getRobotSettings().getWorkArea().getClampingById(clampingId);
				AirblowSquare clampingAir = putStep.getRobotSettings().getAirblowSquare(clampingId);
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
