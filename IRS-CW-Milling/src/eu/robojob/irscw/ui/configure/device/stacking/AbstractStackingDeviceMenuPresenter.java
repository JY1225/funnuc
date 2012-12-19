package eu.robojob.irscw.ui.configure.device.stacking;

import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public abstract class AbstractStackingDeviceMenuPresenter extends AbstractMenuPresenter<StackingDeviceMenuView> {

	private DeviceInformation deviceInfo;
	
	public AbstractStackingDeviceMenuPresenter(final StackingDeviceMenuView view, final DeviceInformation deviceInfo) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
	}
	
	public abstract void configureDevice();
	
	public abstract void configureWorkPiece();
	
	public abstract void showLayout();

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		configureDevice();
	}

	public DeviceInformation getDeviceInfo() {
		return deviceInfo;
	}

}
