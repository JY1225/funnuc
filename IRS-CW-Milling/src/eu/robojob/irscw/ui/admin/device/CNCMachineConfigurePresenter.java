package eu.robojob.irscw.ui.admin.device;

import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class CNCMachineConfigurePresenter extends AbstractFormPresenter<CNCMachineConfigureView, DeviceMenuPresenter> {

	public CNCMachineConfigurePresenter(final CNCMachineConfigureView view) {
		super(view);
		getView().build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

}
