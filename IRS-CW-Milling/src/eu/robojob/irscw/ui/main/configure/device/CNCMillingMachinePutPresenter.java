package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;

public class CNCMillingMachinePutPresenter extends AbstractFormPresenter<CNCMillingMachinePutView, CNCMillingMachineMenuPresenter> {

	private PutStep putStep;
	
	public CNCMillingMachinePutPresenter(CNCMillingMachinePutView view, PutStep putStep) {
		super(view);
		this.putStep = putStep;
		view.setPutStep(putStep);
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
		if (putStep.getDeviceSettings().getClamping() != null) {
			putStep.getRobotSettings().setSmoothPoint(putStep.getDeviceSettings().getClamping().getSmoothFromPoint());
			view.refresh();
		}
	}
}
