package eu.robojob.irscw.ui.main.configure.process;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;

public class ProcessConfigurePresenter extends AbstractFormPresenter<ProcessConfigureView, ProcessMenuPresenter> {
	
	private boolean addDeviceActive;
	private boolean removeDeviceActive;
	
	private ProcessFlow processFlow;
	
	public ProcessConfigurePresenter(ProcessConfigureView view, ProcessFlow processFlow) {
		super(view);
		this.processFlow = processFlow;
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
		view.build();
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
