package eu.robojob.millassist.ui.configure.device.processing.reversal;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class ReversalUnitConfigurePresenter extends AbstractFormPresenter<ReversalUnitConfigureView, ReversalUnitMenuPresenter> {
	
	public ReversalUnitConfigurePresenter(final ReversalUnitConfigureView view, final DeviceInformation deviceInfo, final DeviceManager deviceManager) {
		super(view);
		view.setPostProcessingDeviceIds(deviceManager.getPostProcessingDeviceNames());
		view.build();
	}
	
	public void changedDevice(final String deviceId) {
		// TODO: change device!
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return true;
	}

}
