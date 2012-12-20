package eu.robojob.irscw.ui.configure.device.stacking;

import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public class BasicStackPlateMenuPresenter extends AbstractStackingDeviceMenuPresenter {
	
	private BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter;
	private BasicStackPlateWorkPiecePresenter basicStackPlateWorkPiecePresenter;
	private BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter;
	
	public BasicStackPlateMenuPresenter(final StackingDeviceMenuView view, final DeviceInformation deviceInfo, final BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter,
			final BasicStackPlateWorkPiecePresenter basicStackPlateWorkPiecePresenter, final BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter) {
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
		getView().setConfigureDeviceActive();
		getParent().setBottomRightView(basicStackPlateConfigurePresenter.getView());
	}

	@Override
	public void configureWorkPiece() {
		getView().setConfigureWorkPieceActive();
		getParent().setBottomRightView(basicStackPlateWorkPiecePresenter.getView());
	}

	@Override
	public void showLayout() {
		getView().setViewLayoutActive();
		getParent().setBottomRightView(basicStackPlateLayoutPresenter.getView());
	}

	@Override
	public void setTextFieldListener(final ConfigurePresenter parent) {
		basicStackPlateConfigurePresenter.setTextFieldListener(parent);
		if (basicStackPlateWorkPiecePresenter != null) {
			basicStackPlateWorkPiecePresenter.setTextFieldListener(parent);
		}
		basicStackPlateLayoutPresenter.setTextFieldListener(parent);
	}

	@Override
	public void setBlocked(final boolean blocked) {
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
