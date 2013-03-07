package eu.robojob.irscw.ui.admin.device;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.positioning.UserFrame;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class UserFramesConfigurePresenter extends AbstractFormPresenter<UserFramesConfigureView, DeviceMenuPresenter> {

	private DeviceManager deviceManager;
	private boolean editMode;
	private Set<UserFrame> userFrames;
	private UserFrame selectedUserFrame;
	
	public UserFramesConfigurePresenter(final UserFramesConfigureView view, final DeviceManager deviceManager) {
		super(view);
		this.userFrames = deviceManager.getAllUserFrames();
		Set<String> userFrameNames = new HashSet<String>();
		for (UserFrame frame : userFrames) {
			userFrameNames.add(frame.getName());
		}
		getView().setUserFrameNames(userFrameNames);
		getView().build();
		this.selectedUserFrame = null;
		this.editMode = false;
		this.deviceManager = deviceManager;
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	public UserFrame getUserFrameByName(final String name) {
		for (UserFrame userFrame : userFrames) {
			if (userFrame.getName().equals(name)) {
				return userFrame;
			}
		}
		return null;
	}
	
	public void clickedEdit(final String selectedUserFrameName) {
		if (editMode) {
			getView().reset();
			this.selectedUserFrame = null;
			editMode = false;
		} else {
			this.selectedUserFrame = getUserFrameByName(selectedUserFrameName);
			getView().userFrameSelected(selectedUserFrame);
			getView().showFormEdit();
			editMode = true;
		}
	}
	
	public void clickedNew() {
		getView().reset();
		this.selectedUserFrame = null;
		if (!editMode) {
			getView().showFormNew();
			editMode = true;
		} else {
			editMode = false;
		}
	}
	
	public void saveData(final String name, final int number, final float zSafeDistance, final float x, final float y, final float z,
			final float w, final float p, final float r) {
		if (selectedUserFrame != null) {
			deviceManager.updateUserFrame(selectedUserFrame, name, number, zSafeDistance, x, y, z, w, p, r);
		} else {
			deviceManager.addUserFrame(name, number, zSafeDistance, x, y, z, w, p, r);
		}
		Set<String> userFrameNames = new HashSet<String>();
		for (UserFrame frame : userFrames) {
			userFrameNames.add(frame.getName());
		}
		getView().setUserFrameNames(userFrameNames);
		selectedUserFrame = null;
		editMode = false;
		getView().refresh();
	}
}
