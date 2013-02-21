package eu.robojob.irscw.ui.admin.device;

import eu.robojob.irscw.ui.admin.AbstractSubMenuPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public class DeviceMenuPresenter extends AbstractSubMenuPresenter<DeviceMenuView, DeviceAdminPresenter> {

	private BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter;
	
	public DeviceMenuPresenter(final DeviceMenuView view, final BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter) {
		super(view);
		this.basicStackPlateConfigurePresenter = basicStackPlateConfigurePresenter;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		
	}

	@Override
	public void setBlocked(final boolean blocked) { }

	public void configureUserFrames() { 
		getView().setConfigureUserFramesActive();
	}
	
	public void configureBasicStackPlate() { 
		getView().setConfigureBasicStackPlateActive();
		getParent().setContentView(basicStackPlateConfigurePresenter.getView());
	}
	
	public void configureCNCMachine() { 
		getView().setConfigureCNCMachineActive();
	}
	
	public void configureCNCMachineClampings() {
		getView().setConfigureClampingsActive();
	}
	
	public void configurePrage() { 
		getView().setConfigurePrageActive();
	}
	
	@Override
	public boolean isConfigured() {
		return false;
	}

}
