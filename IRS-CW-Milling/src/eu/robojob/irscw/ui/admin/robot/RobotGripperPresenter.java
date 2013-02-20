package eu.robojob.irscw.ui.admin.robot;

import eu.robojob.irscw.external.robot.Gripper;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class RobotGripperPresenter extends AbstractFormPresenter<RobotGripperView, RobotMenuPresenter> {
	
	private Gripper selectedGripper;
	private boolean editMode;
	
	public RobotGripperPresenter(final RobotGripperView view, final RobotManager robotManager) {
		super(view);
		getView().setRobot(robotManager.getRobots().iterator().next());
		getView().refresh();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	public void selectedGripper(final Gripper gripper) {
		if (!editMode) {
			selectedGripper = gripper;
			getView().gripperSelected(gripper);
		}
	}
	
	public void clickedEdit() {
		if (editMode) {
			getView().reset();
			editMode = false;
		} else {
			getView().showFormEdit();
			editMode = true;
		}
	}
	
	public void clickedNew() {
		getView().reset();
		if (!editMode) {
			selectedGripper = null;
			getView().showFormNew();
			editMode = true;
		} else {
			editMode = false;
		}
	}
}
