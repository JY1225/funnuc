package eu.robojob.millassist.ui.configure.device.stacking.stackplate;

import eu.robojob.millassist.ui.configure.device.stacking.AbstractStackingDeviceMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceMenuView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class BasicStackPlateMenuPresenter extends AbstractStackingDeviceMenuPresenter {
	
	private BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter;
	private AbstractFormPresenter<?, BasicStackPlateMenuPresenter> basicStackPlateWorkPiecePresenter;
	private BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter;
	
	public BasicStackPlateMenuPresenter(final StackingDeviceMenuView view, final DeviceInformation deviceInfo, final BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter,
			final AbstractFormPresenter<?, BasicStackPlateMenuPresenter> basicStackPlateWorkPiecePresenter, final BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter) {
		super(view, deviceInfo);
		this.basicStackPlateConfigurePresenter = basicStackPlateConfigurePresenter;
		basicStackPlateConfigurePresenter.setMenuPresenter(this);
		if (basicStackPlateWorkPiecePresenter != null) {
			this.basicStackPlateWorkPiecePresenter = basicStackPlateWorkPiecePresenter;
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
		// check if it's a pick (raw workpiece) or put (finished workpiece)	
		getParent().setBottomRightView(basicStackPlateWorkPiecePresenter.getView());
	}

	@Override
	public void showLayout() {
		getView().setViewLayoutActive();
		getParent().setBottomRightViewNoRefresh(basicStackPlateLayoutPresenter.getView());
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		basicStackPlateConfigurePresenter.setTextFieldListener(listener);
		if (basicStackPlateWorkPiecePresenter != null) {
			basicStackPlateWorkPiecePresenter.setTextFieldListener(listener);
		}
		basicStackPlateLayoutPresenter.setTextFieldListener(listener);
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

	@Override
	public void unregisterListeners() {
		basicStackPlateLayoutPresenter.unregister();
	}

}
