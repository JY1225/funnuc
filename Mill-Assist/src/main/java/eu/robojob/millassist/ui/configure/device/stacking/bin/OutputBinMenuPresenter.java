package eu.robojob.millassist.ui.configure.device.stacking.bin;

import eu.robojob.millassist.ui.configure.device.stacking.AbstractStackingDeviceMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.ConfigureSmoothPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceConfigurePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceMenuView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class OutputBinMenuPresenter extends AbstractStackingDeviceMenuPresenter {

	private StackingDeviceConfigurePresenter configurePresenter;
	private ConfigureSmoothPresenter<OutputBinMenuPresenter> smoothToPresenter;
	
	public OutputBinMenuPresenter(final StackingDeviceMenuView view, final DeviceInformation deviceInfo, 
			final StackingDeviceConfigurePresenter configurePresenter, final ConfigureSmoothPresenter<OutputBinMenuPresenter> smoothToPresenter) {
		super(view, deviceInfo);
		this.configurePresenter = configurePresenter;
		configurePresenter.setMenuPresenter(this);
		this.smoothToPresenter = smoothToPresenter;
		smoothToPresenter.setMenuPresenter(this);
	}

	@Override
	public void configureDevice() {
		getView().setConfigureDeviceActive();
		getParent().setBottomRightView(configurePresenter.getView());
	}

	@Override
	public void configureWorkPiece() { }

	@Override
	public void configurePick() { }

	@Override
	public void configurePut() {
		getView().setConfigurePutActive();
		getParent().setBottomRightView(smoothToPresenter.getView());
	}

	@Override
	public void showLayout() { }

	@Override
	public boolean isConfigured() {
		return (configurePresenter.isConfigured() && smoothToPresenter.isConfigured());
	}

	@Override
	public void setBlocked(final boolean blocked) { }

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		configurePresenter.setTextFieldListener(listener);
		smoothToPresenter.setTextFieldListener(listener);
	}

	@Override
	public void unregisterListeners() { }

}
