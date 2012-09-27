package eu.robojob.irscw.ui.main.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class BasicStackPlateMenuPresenter extends AbstractStackingDeviceMenuPresenter {

	private Logger logger = Logger.getLogger(BasicStackPlateMenuPresenter.class);
	
	private BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter;
	private BasicStackPlateWorkPiecePresenter basicStackPlateWorkPiecePresenter;
	
	public BasicStackPlateMenuPresenter(StackingDeviceMenuView view, DeviceInformation deviceInfo, BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter,
			BasicStackPlateWorkPiecePresenter basicStackPlateWorkPiecePresenter) {
		super(view, deviceInfo);
		this.basicStackPlateConfigurePresenter = basicStackPlateConfigurePresenter;
		this.basicStackPlateWorkPiecePresenter = basicStackPlateWorkPiecePresenter;
	}

	@Override
	public void configureDevice() {
		logger.debug("configure device");
		view.setConfigureDeviceActive();
		parent.setBottomRightView(basicStackPlateConfigurePresenter.getView());
	}

	@Override
	public void configureWorkPiece() {
		logger.debug("configure work piece");
		view.setConfigureWorkPieceActive();
		parent.setBottomRightView(basicStackPlateWorkPiecePresenter.getView());
	}

	@Override
	public void showLayout() {
		logger.debug("show layout");
	}

	@Override
	public void setTextFieldListener(ConfigurePresenter parent) {
		basicStackPlateConfigurePresenter.setTextFieldListener(parent);
		if (basicStackPlateWorkPiecePresenter != null) {
			basicStackPlateWorkPiecePresenter.setTextFieldListener(parent);
		}
	}

	@Override
	public void setBlocked(boolean blocked) {
		// TODO Auto-generated method stub
		
	}

}
