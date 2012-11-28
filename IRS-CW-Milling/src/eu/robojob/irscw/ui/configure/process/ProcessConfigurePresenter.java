package eu.robojob.irscw.ui.configure.process;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;

public class ProcessConfigurePresenter extends AbstractFormPresenter<ProcessConfigureView, ProcessMenuPresenter> {
	
	private boolean addDeviceActive;
	private boolean removeDeviceActive;
		
	public ProcessConfigurePresenter(ProcessConfigureView view, ProcessFlow processFlow) {
		super(view);
		view.setProcessFlow(processFlow);
		view.build();
		addDeviceActive = false;
		removeDeviceActive = false;
	}
	
	public void addDeviceStep() {
		if (!addDeviceActive) {
			addDeviceActive = true;
			removeDeviceActive = false;
			menuPresenter.setAddDeviceMode();
		} else {
			setNormalMode();
			menuPresenter.setNormalMode();
		}
		updateActiveParts();
	}
	
	public void removeDeviceStep() {
		if (!removeDeviceActive) {
			addDeviceActive = false;
			removeDeviceActive = true;
			menuPresenter.setRemoveDeviceMode();
		} else {
			setNormalMode();
			menuPresenter.setNormalMode();
		}
		updateActiveParts();
	}
	
	public void setNormalMode() {
		addDeviceActive = false;
		removeDeviceActive = false;
		view.refresh();
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

	@Override
	public boolean isConfigured() {
		return false;
	}

}
