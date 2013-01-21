package eu.robojob.irscw.ui.configure.device.processing.prage;

import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.configure.device.AbstractDeviceMenuPresenter;
import eu.robojob.irscw.ui.configure.device.DeviceMenuView;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

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
	public void setTextFieldListener(final ConfigurePresenter parent) {		
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
