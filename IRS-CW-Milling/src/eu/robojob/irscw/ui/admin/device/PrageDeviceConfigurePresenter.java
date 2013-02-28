package eu.robojob.irscw.ui.admin.device;

import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class PrageDeviceConfigurePresenter extends AbstractFormPresenter<PrageDeviceConfigureView, DeviceMenuPresenter> {

	public PrageDeviceConfigurePresenter(final PrageDeviceConfigureView view) {
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
