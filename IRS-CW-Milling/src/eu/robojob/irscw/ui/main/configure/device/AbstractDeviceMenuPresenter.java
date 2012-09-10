package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public abstract class AbstractDeviceMenuPresenter extends AbstractMenuPresenter<DeviceMenuView> {

	private DeviceInformation deviceInfo;
	
	public AbstractDeviceMenuPresenter(DeviceMenuView view, DeviceInformation deviceInfo) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
	}

	@Override
	protected void setPresenter() {
		view.setPresenter(this);
	}

	public abstract void configurePick();
		
	public abstract void configurePut();
	
	public abstract void configureDevice();
	
	@Override
	public void openFirst() {
		if (deviceInfo.getProcessingStep() != null) {
			configureDevice();
		} else if (deviceInfo.getPickStep() != null) {
			configurePick();
		} else if (deviceInfo.getPutStep() != null) {
			configurePut();
		}
	}
	
	public DeviceInformation getDeviceInformation() {
		return deviceInfo;
	}

}
