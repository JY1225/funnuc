package eu.robojob.irscw.ui.admin.device;

import eu.robojob.irscw.ui.admin.AbstractSubMenuPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public class DeviceMenuPresenter extends AbstractSubMenuPresenter<DeviceMenuView, DeviceAdminPresenter> {

	public DeviceMenuPresenter(DeviceMenuView view) {
		super(view);
	}

	@Override
	public void setTextFieldListener(TextInputControlListener listener) {
		
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		
	}

	@Override
	public void setBlocked(boolean blocked) { }

	public void configureUserFrames() { }
	public void configureBasicStackPlate() { }
	public void configureCNCMachine() { }
	public void configureCNCMachineClampings() { }
	public void configurePrage() { }
	
	@Override
	public boolean isConfigured() {
		return false;
	}

}
