package eu.robojob.irscw.ui.admin.device;

import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class CNCMachineClampingsPresenter extends AbstractFormPresenter<CNCMachineClampingsView, DeviceMenuPresenter> {

	public CNCMachineClampingsPresenter(final CNCMachineClampingsView view) {
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
