package eu.robojob.irscw.ui.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class BasicStackPlateMenuPresenter extends AbstractStackingDeviceMenuPresenter {

	private Logger logger = Logger.getLogger(BasicStackPlateMenuPresenter.class);
	
	private BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter;
	private BasicStackPlateWorkPiecePresenter basicStackPlateWorkPiecePresenter;
	private BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter;
	
	public BasicStackPlateMenuPresenter(StackingDeviceMenuView view, DeviceInformation deviceInfo, BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter,
			BasicStackPlateWorkPiecePresenter basicStackPlateWorkPiecePresenter, BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter) {
		super(view, deviceInfo);
		this.basicStackPlateConfigurePresenter = basicStackPlateConfigurePresenter;
		basicStackPlateConfigurePresenter.setMenuPresenter(this);
		this.basicStackPlateWorkPiecePresenter = basicStackPlateWorkPiecePresenter;
		if (basicStackPlateWorkPiecePresenter != null) {
			basicStackPlateWorkPiecePresenter.setMenuPresenter(this);
		}
		this.basicStackPlateLayoutPresenter = basicStackPlateLayoutPresenter;
		basicStackPlateLayoutPresenter.setMenuPresenter(this);
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
		view.setViewLayoutActive();
		parent.setBottomRightView(basicStackPlateLayoutPresenter.getView());
	}

	@Override
	public void setTextFieldListener(ConfigurePresenter parent) {
		basicStackPlateConfigurePresenter.setTextFieldListener(parent);
		if (basicStackPlateWorkPiecePresenter != null) {
			basicStackPlateWorkPiecePresenter.setTextFieldListener(parent);
		}
		basicStackPlateLayoutPresenter.setTextFieldListener(parent);
	}

	@Override
	public void setBlocked(boolean blocked) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isConfigured() {
		if (basicStackPlateWorkPiecePresenter != null) {
			return basicStackPlateConfigurePresenter.isConfigured() && basicStackPlateLayoutPresenter.isConfigured() && basicStackPlateWorkPiecePresenter.isConfigured();
		} else {
			return basicStackPlateConfigurePresenter.isConfigured() && basicStackPlateLayoutPresenter.isConfigured();
		}
	}

}
