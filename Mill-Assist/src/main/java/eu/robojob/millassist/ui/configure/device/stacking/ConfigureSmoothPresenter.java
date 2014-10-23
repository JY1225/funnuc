package eu.robojob.millassist.ui.configure.device.stacking;

import eu.robojob.millassist.external.robot.AbstractRobotActionSettings;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class ConfigureSmoothPresenter<T extends AbstractStackingDeviceMenuPresenter> extends AbstractFormPresenter<ConfigureSmoothView, T> {

	private AbstractRobotActionSettings<?> robotActionSettings;
	private Coordinates smoothPoint;
	
	public ConfigureSmoothPresenter(final ConfigureSmoothView view, final AbstractRobotActionSettings<?> robotActionSettings) {
		super(view);
		this.robotActionSettings = robotActionSettings;
		smoothPoint = robotActionSettings.getSmoothPoint();
		if (robotActionSettings.getSmoothPoint() == null) {
			if (robotActionSettings instanceof RobotPickSettings) {
				smoothPoint = new Coordinates(robotActionSettings.getWorkArea().getDefaultClamping().getSmoothFromPoint());
			} else if (robotActionSettings instanceof RobotPutSettings) {
				smoothPoint = new Coordinates(robotActionSettings.getWorkArea().getDefaultClamping().getSmoothToPoint());
			} else {
				throw new IllegalStateException("Unknown robot action settings type");
			}
			robotActionSettings.setSmoothPoint(smoothPoint);
		}
		view.setSmoothPoint(smoothPoint);
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return true;
	}
	
	public void changedSmoothX(final float x) {
		smoothPoint.setX(x);
		getView().refresh();
	}
	
	public void changedSmoothY(final float y) {
		smoothPoint.setY(y);
		getView().refresh();
	}
	
	public void changedSmoothZ(final float z) {
		smoothPoint.setZ(z);
		getView().refresh();
	}
	
	public void resetSmooth() {
		if (robotActionSettings instanceof RobotPickSettings) {		
			smoothPoint = new Coordinates(robotActionSettings.getWorkArea().getDefaultClamping().getSmoothFromPoint());
		} else if (robotActionSettings instanceof RobotPutSettings) {
			smoothPoint = new Coordinates(robotActionSettings.getWorkArea().getDefaultClamping().getSmoothToPoint());
		}
		getView().setSmoothPoint(smoothPoint);
		getView().refresh();
	}

}
