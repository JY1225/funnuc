package eu.robojob.millassist.ui.configure.device.stacking.conveyor;

import eu.robojob.millassist.ui.configure.device.stacking.AbstractStackingDeviceMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceMenuView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class ConveyorMenuPresenter extends AbstractStackingDeviceMenuPresenter {

	private ConveyorConfigurePresenter configurePresenter;
	private ConveyorRawWorkPiecePresenter rawWorkPiecePresenter;
	private ConveyorRawWorkPieceLayoutPresenter rawWorkPieceLayoutPresenter;
	
	public ConveyorMenuPresenter(final StackingDeviceMenuView view, final DeviceInformation deviceInfo,
			final ConveyorConfigurePresenter configurePresenter, final ConveyorRawWorkPiecePresenter rawWorkPiecePresenter, 
			final ConveyorRawWorkPieceLayoutPresenter rawWorkPieceLayoutPresenter) {
		super(view, deviceInfo);
		this.configurePresenter = configurePresenter;
		configurePresenter.setMenuPresenter(this);
		if (rawWorkPiecePresenter != null) {
			this.rawWorkPiecePresenter = rawWorkPiecePresenter;
			rawWorkPiecePresenter.setMenuPresenter(this);
		}
		this.rawWorkPieceLayoutPresenter = rawWorkPieceLayoutPresenter;
	}

	@Override
	public void configureDevice() {
		getView().setConfigureDeviceActive();
		getParent().setBottomRightView(configurePresenter.getView());
	}

	@Override
	public void configureWorkPiece() {
		getView().setConfigureWorkPieceActive();
		getParent().setBottomRightView(rawWorkPiecePresenter.getView());
	}

	@Override
	public void showLayout() {
		getView().setViewLayoutActive();
		getParent().setBottomRightView(rawWorkPieceLayoutPresenter.getView());
	}

	@Override
	public boolean isConfigured() {
		if (rawWorkPiecePresenter != null) {
			return configurePresenter.isConfigured() && rawWorkPiecePresenter.isConfigured() && rawWorkPieceLayoutPresenter.isConfigured();
		} else {
			return configurePresenter.isConfigured() && rawWorkPieceLayoutPresenter.isConfigured();
		}
	}

	@Override
	public void setBlocked(final boolean blocked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		configurePresenter.setTextFieldListener(listener);
		if (rawWorkPiecePresenter != null) {
			rawWorkPiecePresenter.setTextFieldListener(listener);
		}
	}

	@Override
	public void unregisterListeners() {
		// TODO Auto-generated method stub
		
	}

}
