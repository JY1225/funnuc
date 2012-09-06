package eu.robojob.irscw.ui.main.configure.process;

import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;

public class ProcessConfigurePresenter extends AbstractFormPresenter<ProcessConfigureView> {
	
	private boolean addDeviceActive;
	private boolean removeDeviceActive;
	
	public ProcessConfigurePresenter(ProcessConfigureView view) {
		super(view);
		addDeviceActive = false;
		removeDeviceActive = false;
	}
	
	public void addDeviceStep() {
		if (!addDeviceActive) {
			addDeviceActive = true;
			removeDeviceActive = false;
			parent.setAddDeviceMode();
		} else {
			addDeviceActive = false;
			removeDeviceActive = false;
			parent.setNormalMode();
		}
		updateActiveParts();
	}
	
	public void removeDeviceStep() {
		if (!removeDeviceActive) {
			addDeviceActive = false;
			removeDeviceActive = true;
			parent.setRemoveDeviceMode();
		} else {
			addDeviceActive = false;
			removeDeviceActive = false;
			parent.setNormalMode();
		}
		updateActiveParts();
	}
	
	private void updateActiveParts() {
		view.setAddDeviceStepActive(addDeviceActive);
		view.setRemoveDeviceStepActive(removeDeviceActive);
		view.setNameEnabled(!(addDeviceActive||removeDeviceActive));
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

}
