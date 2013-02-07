package eu.robojob.irscw.ui.configure.device;

import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public abstract class AbstractDeviceMenuPresenter extends AbstractMenuPresenter<DeviceMenuView> {

	private DeviceInformation deviceInfo;
	
	public AbstractDeviceMenuPresenter(final DeviceMenuView view, final DeviceInformation deviceInfo) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	public abstract void configurePick();
		
	public abstract void configurePut();
	
	public abstract void configureDevice();
	
	@Override
	public void openFirst() {
		if (deviceInfo.getProcessingStep() != null) {
			configureDevice();
		} else if (deviceInfo.getPutStep() != null) {
			configurePut();
		} else if (deviceInfo.getPickStep() != null) {
			configurePick();
		}
	}
	
	public DeviceInformation getDeviceInformation() {
		return deviceInfo;
	}

}
