package eu.robojob.irscw.ui.admin.device;

import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class BasicStackPlateConfigurePresenter extends AbstractFormPresenter<BasicStackPlateConfigureView, DeviceMenuPresenter> {

	public BasicStackPlateConfigurePresenter(final BasicStackPlateConfigureView view) {
		super(view);
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
