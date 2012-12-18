package eu.robojob.irscw.ui.configure.device;

import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public abstract class AbstractStackingDeviceMenuPresenter extends AbstractMenuPresenter<StackingDeviceMenuView> {

	DeviceInformation deviceInfo;
	
	public AbstractStackingDeviceMenuPresenter(StackingDeviceMenuView view, DeviceInformation deviceInfo) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
	}
	
	public abstract void configureDevice();
	
	public abstract void configureWorkPiece();
	
	public abstract void showLayout();

	@Override
	protected void setPresenter() {
		view.setPresenter(this);
	}

	@Override
	public void openFirst() {
		configureDevice();
	}

	public DeviceInformation getDeviceInfo() {
		return deviceInfo;
	}

}
