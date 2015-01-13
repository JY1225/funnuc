package eu.robojob.millassist.ui.configure.device.processing.cnc;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.WorkAreaManager;
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
		if (deviceSettings.getDefaultClamping(pickStep.getDeviceSettings().getWorkArea()) != null) {
			pickStep.getRobotSettings().setSmoothPoint(deviceSettings.getDefaultClamping(pickStep.getDeviceSettings().getWorkArea()).getSmoothFromPoint());
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			getView().refresh();
		}
	}

	public void resetAirblow(String clampingName) {
		Clamping clamping = pickStep.getRobotSettings().getWorkArea().getWorkAreaManager().getClampingByName(clampingName);
		if (clamping != null) {
			AirblowSquare defaultAirblow = clamping.getDefaultAirblowPoints();
			if (pickStep.getRobotSettings().getAirblowSquare(clamping.getId()) != null) {
				AirblowSquare clampAirblow = pickStep.getRobotSettings().getAirblowSquare(clamping.getId());
				clampAirblow.getBottomCoord().setCoordinateValues(defaultAirblow.getBottomCoord().getCoordValues());
				clampAirblow.getTopCoord().setCoordinateValues(defaultAirblow.getTopCoord().getCoordValues());
			} else {
				AirblowSquare newClampAirblow = new AirblowSquare(new Coordinates(defaultAirblow.getBottomCoord()), new Coordinates(defaultAirblow.getTopCoord()));
				pickStep.getRobotSettings().addRobotAirblowSettings(clamping.getId(), newClampAirblow);
			}
			getView().setBottomCoord(pickStep.getRobotSettings().getAirblowSquare(clamping.getId()).getBottomCoord());
			getView().setTopCoord(pickStep.getRobotSettings().getAirblowSquare(clamping.getId()).getTopCoord());
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			getView().refreshCoordboxes();
			getView().hideNotification();
			isConfigured();
		}
	}
	
	public void changedAirblow(final boolean airblow) {
		pickStep.getRobotSettings().setDoMachineAirblow(airblow);
		if (!airblow) {
			pickStep.getRobotSettings().clearAirblowSettings();
		}
		changedCoordinate();
	}
	
	void changedClamping(final String clampingName) {
		if (clampingName != null) {
			int clampingId = pickStep.getRobotSettings().getWorkArea().getWorkAreaManager().getClampingByName(clampingName).getId();
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
		for (Clamping clamping : pickStep.getRobotSettings().getWorkArea().getAllActiveClampings()) {
			if (!pickStep.getRobotSettings().getRobotAirblowSettings().containsKey(clamping.getId())) {
				return false;
			}
		}
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
		WorkAreaManager workarea = pickStep.getRobotSettings().getWorkArea().getWorkAreaManager();
		if (workarea.getBoundaries() != null) {
			AirblowSquare square = workarea.getBoundaries().getBoundary();
			for (int clampingId: pickStep.getRobotSettings().getRobotAirblowSettings().keySet()) {
				Clamping clamping = pickStep.getRobotSettings().getWorkArea().getWorkAreaManager().getClampingById(clampingId);
				AirblowSquare clampingAir = pickStep.getRobotSettings().getAirblowSquare(clampingId);
				Coordinates lowerLeftCorner = Coordinates.add(clampingAir.getBottomCoord(), clamping.getRelativePosition());
				Coordinates upperRightCorner = Coordinates.add(clampingAir.getTopCoord(), clamping.getRelativePosition());
				upperRightCorner.setZ(lowerLeftCorner.getZ());
				if (!lowerLeftCorner.isInsideSquare(square) || !upperRightCorner.isInsideSquare(square)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public void changedMachineAirblow(final boolean newValue) {
		pickStep.getDeviceSettings().setIsMachineAirblow(newValue);
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
	}
}
