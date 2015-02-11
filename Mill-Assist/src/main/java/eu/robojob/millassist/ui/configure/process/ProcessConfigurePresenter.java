package eu.robojob.millassist.ui.configure.process;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class ProcessConfigurePresenter extends AbstractFormPresenter<ProcessConfigureView, ProcessMenuPresenter> {
	
	private ProcessFlow processFlow;
	private boolean addDeviceActive;
	private boolean removeDeviceActive;
		
	public ProcessConfigurePresenter(final ProcessConfigureView view, final ProcessFlow processFlow, final DeviceManager deviceManager) {
		super(view);
		this.processFlow = processFlow;
		view.setProcessFlow(processFlow);
		view.build();
		if ((deviceManager.getPostProcessingDevices().size() == 0) && (deviceManager.getPreProcessingDevices().size() == 0)) {
			view.disableAddRemove();
		}
		addDeviceActive = false;
		removeDeviceActive = false;
	}
	
	public void addDeviceStep() {
		if (!addDeviceActive) {
			addDeviceActive = true;
			removeDeviceActive = false;
			getMenuPresenter().setAddDeviceMode();
		} else {
			setNormalMode();
			getMenuPresenter().setNormalMode();
		}
		updateActiveParts();
	}
	
	public void nameChanged(final String name) {
		processFlow.setName(name);
		processFlow.processProcessFlowEvent(new DataChangedEvent(processFlow, null, false));
		getMenuPresenter().refreshParent();
	}
	
	public void removeDeviceStep() {
		if (!removeDeviceActive) {
			addDeviceActive = false;
			removeDeviceActive = true;
			getMenuPresenter().setRemoveDeviceMode();
		} else {
			setNormalMode();
			getMenuPresenter().setNormalMode();
		}
		updateActiveParts();
	}
	
	public void setNormalMode() {
		addDeviceActive = false;
		removeDeviceActive = false;
		getView().refresh();
		updateActiveParts();
	}
	
	private void updateActiveParts() {
		getView().setAddDeviceStepActive(addDeviceActive);
		getView().setRemoveDeviceStepActive(removeDeviceActive);
		getView().setNameEnabled(!(addDeviceActive || removeDeviceActive));
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void setSingleCycle(boolean isSingleCycle) {
		if (processFlow.isSingleCycle() != isSingleCycle) {
			this.processFlow.setSingleCycle(isSingleCycle);
			processFlow.processProcessFlowEvent(new DataChangedEvent(processFlow, null, false));
		}
	}

}
