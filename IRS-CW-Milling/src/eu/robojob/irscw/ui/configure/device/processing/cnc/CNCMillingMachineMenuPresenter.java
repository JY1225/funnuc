package eu.robojob.irscw.ui.configure.device.processing.cnc;

import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.configure.device.AbstractDeviceMenuPresenter;
import eu.robojob.irscw.ui.configure.device.DeviceMenuView;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public class CNCMillingMachineMenuPresenter extends AbstractDeviceMenuPresenter {

	private CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter;
	private CNCMillingMachinePickPresenter cncMillingMachinePickPresenter;
	private CNCMillingMachinePutPresenter cncMillingMachinePutPresenter;
		
	public CNCMillingMachineMenuPresenter(final DeviceMenuView view, final DeviceInformation deviceInfo, final CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter, 
			final CNCMillingMachinePickPresenter cncMillingMachinePickPresenter, final CNCMillingMachinePutPresenter cncMillingMachinePutPresenter) {
		super(view, deviceInfo);
		this.cncMillingMachineConfigurePresenter = cncMillingMachineConfigurePresenter;
		this.cncMillingMachinePickPresenter = cncMillingMachinePickPresenter;
		this.cncMillingMachinePutPresenter = cncMillingMachinePutPresenter;
	}

	@Override
	public void configurePick() {
		getView().setConfigurePickActive();
		getParent().setBottomRightView(cncMillingMachinePickPresenter.getView());
	}

	@Override
	public void configurePut() {
		getView().setConfigurePutActive();
		getParent().setBottomRightView(cncMillingMachinePutPresenter.getView());
	}

	@Override
	public void configureDevice() {
		getView().setProcessingActive();
		getParent().setBottomRightView(cncMillingMachineConfigurePresenter.getView());
	}

	@Override
	public void setBlocked(final boolean blocked) {
	}

	@Override
	public void setTextFieldListener(final ConfigurePresenter parent) {
		cncMillingMachinePickPresenter.setTextFieldListener(parent);
		cncMillingMachinePutPresenter.setTextFieldListener(parent);
	}

	@Override
	public boolean isConfigured() {
		return cncMillingMachineConfigurePresenter.isConfigured() && cncMillingMachinePickPresenter.isConfigured() && cncMillingMachinePutPresenter.isConfigured();
	}

}
