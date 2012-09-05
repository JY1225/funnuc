package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;

public class DeviceMenuPresenter extends AbstractMenuPresenter<DeviceMenuView> {

	public DeviceMenuPresenter(DeviceMenuView view) {
		super(view);
	}

	@Override
	protected void setPresenter() {
		view.setPresenter(this);
	}

	public void configurePick() {
		
	}
	
	public void configurePut() {
		
	}
	
	public void configureDevice() {
		
	}
}
