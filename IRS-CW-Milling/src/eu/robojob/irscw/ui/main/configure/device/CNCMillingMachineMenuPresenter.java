package eu.robojob.irscw.ui.main.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class CNCMillingMachineMenuPresenter extends AbstractDeviceMenuPresenter {

	private CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter;
	private CNCMillingMachinePickPresenter cncMillingMachinePickPresenter;
	private CNCMillingMachinePutPresenter cncMillingMachinePutPresenter;
	
	private Logger logger = Logger.getLogger(CNCMillingMachineMenuPresenter.class);
	
	public CNCMillingMachineMenuPresenter(DeviceMenuView view, DeviceInformation deviceInfo, CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter, 
			CNCMillingMachinePickPresenter cncMillingMachinePickPresenter, CNCMillingMachinePutPresenter cncMillingMachinePutPresenter) {
		super(view, deviceInfo);
		this.cncMillingMachineConfigurePresenter = cncMillingMachineConfigurePresenter;
		this.cncMillingMachinePickPresenter = cncMillingMachinePickPresenter;
		this.cncMillingMachinePutPresenter = cncMillingMachinePutPresenter;
	}

	@Override
	public void configurePick() {
		view.setConfigurePickActive();
		logger.debug("clicked configure pick");
		parent.setBottomRightView(cncMillingMachinePickPresenter.getView());
	}

	@Override
	public void configurePut() {
		view.setConfigurePutActive();
		logger.debug("clicked configure put");
		parent.setBottomRightView(cncMillingMachinePutPresenter.getView());
	}

	@Override
	public void configureDevice() {
		view.setProcessingActive();
		parent.setBottomRightView(cncMillingMachineConfigurePresenter.getView());
	}

	@Override
	public void setBlocked(boolean blocked) {
		
	}

	@Override
	public void setTextFieldListener(ConfigurePresenter parent) {
		cncMillingMachinePickPresenter.setTextFieldListener(parent);
		cncMillingMachinePutPresenter.setTextFieldListener(parent);
	}

}