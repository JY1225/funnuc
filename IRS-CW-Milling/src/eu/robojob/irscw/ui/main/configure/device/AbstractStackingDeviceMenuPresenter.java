package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

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
