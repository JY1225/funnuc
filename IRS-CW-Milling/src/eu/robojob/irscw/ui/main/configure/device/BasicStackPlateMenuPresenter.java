package eu.robojob.irscw.ui.main.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class BasicStackPlateMenuPresenter extends AbstractStackingDeviceMenuPresenter {

	private Logger logger = Logger.getLogger(BasicStackPlateMenuPresenter.class);
	
	public BasicStackPlateMenuPresenter(StackingDeviceMenuView view,
			DeviceInformation deviceInfo) {
		super(view, deviceInfo);
	}

	@Override
	public void configureDevice() {
		logger.debug("configure device");
	}

	@Override
	public void configureWorkPiece() {
		logger.debug("configure work piece");
	}

	@Override
	public void showLayout() {
		logger.debug("show layout");
	}

	@Override
	public void setTextFieldListener(ConfigurePresenter parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlocked(boolean blocked) {
		// TODO Auto-generated method stub
		
	}

}
