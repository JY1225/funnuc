package eu.robojob.millassist.ui.configure.device.stacking;

import eu.robojob.millassist.ui.configure.AbstractMenuPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public abstract class AbstractStackingDeviceMenuPresenter extends AbstractMenuPresenter<StackingDeviceMenuView> {

	private DeviceInformation deviceInfo;
	
	public AbstractStackingDeviceMenuPresenter(final StackingDeviceMenuView view, final DeviceInformation deviceInfo) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
	}
	
	public abstract void configureDevice();
	
	public abstract void configureWorkPiece();
	
	public abstract void configureOffsets();
	
	public abstract void showLayout();

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		if (deviceInfo.hasPickStep()) {
			configureWorkPiece();
		} else {
			showLayout();
		}
	}

	public DeviceInformation getDeviceInfo() {
		return deviceInfo;
	}

}
