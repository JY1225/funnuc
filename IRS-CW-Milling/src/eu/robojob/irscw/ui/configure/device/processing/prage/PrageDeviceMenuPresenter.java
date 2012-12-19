package eu.robojob.irscw.ui.configure.device.processing.prage;

import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.configure.device.AbstractDeviceMenuPresenter;
import eu.robojob.irscw.ui.configure.device.DeviceMenuView;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public class PrageDeviceMenuPresenter extends AbstractDeviceMenuPresenter {

	private PrageDeviceConfigurePresenter deviceConfigurePresenter;
	
	public PrageDeviceMenuPresenter(DeviceMenuView view, DeviceInformation deviceInfo, PrageDeviceConfigurePresenter deviceConfigurePresenter) {
		super(view, deviceInfo);
		this.deviceConfigurePresenter = deviceConfigurePresenter;
	}

	@Override
	public void configurePick() {
		throw new IllegalStateException("Shouldn't be possible with this device");
	}

	@Override
	public void configurePut() {
		throw new IllegalStateException("Shouldn't be possible with this device");
	}

	@Override
	public void configureDevice() {
		view.setProcessingActive();
		parent.setBottomRightView(deviceConfigurePresenter.getView());
	}

	@Override
	public void setTextFieldListener(ConfigurePresenter parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlocked(boolean blocked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConfigured() {
		return true;
	}

}
