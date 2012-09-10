package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class CNCMillingMachineConfigurePresenter extends AbstractFormPresenter<CNCMillingMachineConfigureView, CNCMillingMachineMenuPresenter>{

	private DeviceInformation deviceInfo;
	
	public CNCMillingMachineConfigurePresenter(CNCMillingMachineConfigureView view, DeviceInformation deviceInfo) {
		super(view);
		this.deviceInfo = deviceInfo;
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}
	
}
