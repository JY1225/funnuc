package eu.robojob.millassist.ui.configure.device.processing.reversal;

import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnitSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class ReversalUnitPutPresenter extends AbstractFormPresenter<ReversalUnitPutView, ReversalUnitMenuPresenter> {

	private PutStep putStep;
	private ReversalUnitSettings deviceSettings;
	
	public ReversalUnitPutPresenter(final ReversalUnitPutView view, final PutStep putStep, final ReversalUnitSettings deviceSettings) {
		super(view);
		this.putStep = putStep;
		this.deviceSettings = deviceSettings;
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	PutStep getPutStep() {
		return this.putStep;
	}
	
	ReversalUnitSettings getDeviceSettings() {
		return this.deviceSettings;
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
		if (deviceSettings.getDefaultClamping(putStep.getDeviceSettings().getWorkArea()) != null) {
			putStep.getRobotSettings().setSmoothPoint(deviceSettings.getDefaultClamping(putStep.getDeviceSettings().getWorkArea()).getSmoothToPoint());
			putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
			getView().refresh();
		}
	}
	
	public void changedConfigWidth(final float configWidth) {
		if (deviceSettings.getConfigWidth() != configWidth) {
			deviceSettings.setConfigWidth(configWidth);		
			putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));

		}
	}
	
	@Override
	public boolean isConfigured() {
		if (putStep.getRobotSettings().getSmoothPoint() != null && deviceSettings.getConfigWidth() > 0) {
			return true;
		}
		return false;
	}
	
	public void changedShiftedOrigin(final boolean isShiftedOrigin) {
		if (deviceSettings.isShiftedOrigin() != isShiftedOrigin) {
			deviceSettings.setShiftedOrigin(isShiftedOrigin);
			putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, true));
		}
	}
	
	boolean hasShiftingPin() {
		return (((ReversalUnit) putStep.getDevice()).getAddedX() > 0);
	}
}
