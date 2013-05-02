package eu.robojob.irscw.ui.admin.robot;

import eu.robojob.irscw.external.robot.Gripper;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class RobotGripperPresenter extends AbstractFormPresenter<RobotGripperView, RobotMenuPresenter> {
	
	private Gripper selectedGripper;
	private boolean editMode;
	private RobotManager robotManager;
	
	public RobotGripperPresenter(final RobotGripperView view, final RobotManager robotManager) {
		super(view);
		this.robotManager = robotManager;
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
	
	public void saveData(final String name, final String imgUrl, final float height, final boolean fixedHeight, final boolean headA, final boolean headB,
			final boolean headC, final boolean headD) {
		if (selectedGripper != null) {
			robotManager.updateGripper(selectedGripper, name, imgUrl, height, fixedHeight, headA, headB, headC, headD);
		} else {
			robotManager.addGripper(name, imgUrl, height, fixedHeight, headA, headB, headC, headD);
		}
		selectedGripper = null;
		editMode = false;
		getView().refresh();
	}
	
	public void deleteGripper() {
		robotManager.deleteGripper(selectedGripper);
		selectedGripper = null;
		editMode = false;
		getView().refresh();
	}
	
}
