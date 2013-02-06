package eu.robojob.irscw.ui.configure.device.processing.prage;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public class PrageDeviceConfigurePresenter extends AbstractFormPresenter<PrageDeviceConfigureView, PrageDeviceMenuPresenter> {
	
	public PrageDeviceConfigurePresenter(final PrageDeviceConfigureView view, final DeviceInformation deviceInfo, final DeviceManager deviceManager) {
		super(view);
		view.setPreProcessingDeviceIds(deviceManager.getPreProcessingDeviceNames());
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
