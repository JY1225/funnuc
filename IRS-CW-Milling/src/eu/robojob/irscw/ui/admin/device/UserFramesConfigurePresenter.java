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
	
	public UserFramesConfigurePresenter(final UserFramesConfigureView view, final DeviceManager deviceManager) {
		super(view);
		this.userFrames = deviceManager.getAllUserFrames();
		Set<String> userFrameNames = new HashSet<String>();
		for (UserFrame frame : userFrames) {
			userFrameNames.add(frame.getName());
		}
		getView().setUserFrameNames(userFrameNames);
		getView().build();
		this.editMode = false;
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
			editMode = false;
		} else {
			getView().userFrameSelected(getUserFrameByName(selectedUserFrameName));
			getView().showFormEdit();
			editMode = true;
		}
	}
	
	public void clickedNew() {
		getView().reset();
		if (!editMode) {
			getView().showFormNew();
			editMode = true;
		} else {
			editMode = false;
		}
	}
}
