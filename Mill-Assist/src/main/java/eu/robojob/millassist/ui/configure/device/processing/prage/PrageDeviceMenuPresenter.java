package eu.robojob.millassist.ui.configure.device.processing.prage;

import eu.robojob.millassist.ui.configure.device.AbstractDeviceMenuPresenter;
import eu.robojob.millassist.ui.configure.device.DeviceMenuView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class PrageDeviceMenuPresenter extends AbstractDeviceMenuPresenter {

	private PrageDeviceConfigurePresenter deviceConfigurePresenter;
	
	public PrageDeviceMenuPresenter(final DeviceMenuView view, final DeviceInformation deviceInfo, final PrageDeviceConfigurePresenter deviceConfigurePresenter) {
		super(view, deviceInfo);
		this.deviceConfigurePresenter = deviceConfigurePresenter;
	}

	@Override
	public void configurePick() {
		throw new IllegalStateException("Not possible with this device [" + getDeviceInformation().getDevice() + "].");
	}

	@Override
	public void configurePut() {
		throw new IllegalStateException("Not possible with this device [" + getDeviceInformation().getDevice() + "].");
	}

	@Override
	public void configureDevice() {
		getView().setProcessingActive();
		getParent().setBottomRightView(deviceConfigurePresenter.getView());
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {		
	}

	@Override
	public void setBlocked(final boolean blocked) {
	}

	@Override
	public boolean isConfigured() {
		return true;
	}
	
	@Override
	public void openFirst() {
		configureDevice();
	}

}
