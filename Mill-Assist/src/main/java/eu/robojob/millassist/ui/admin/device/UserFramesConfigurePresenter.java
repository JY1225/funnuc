package eu.robojob.millassist.ui.admin.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class UserFramesConfigurePresenter extends AbstractFormPresenter<UserFramesConfigureView, DeviceMenuPresenter> {

	private DeviceManager deviceManager;
	private boolean editMode;
	private UserFrame selectedUserFrame;
	
	public UserFramesConfigurePresenter(final UserFramesConfigureView view, final DeviceManager deviceManager) {
		super(view);
		this.selectedUserFrame = null;
		this.editMode = false;
		this.deviceManager = deviceManager;
		getView().build();
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
	
	public void updateUserFrames() {
		List<String> userFrameNames = new ArrayList<String>();
		for (UserFrame uf : deviceManager.getAllUserFrames()) {
			userFrameNames.add(uf.getName());
		}
		getView().setUserFrames(userFrameNames);
	}

	public UserFrame getUserFrameByName(final String name) {
		for (UserFrame userFrame : deviceManager.getAllUserFrames()) {
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
		selectedUserFrame = null;
		editMode = false;
		getView().refresh();
	}
}
