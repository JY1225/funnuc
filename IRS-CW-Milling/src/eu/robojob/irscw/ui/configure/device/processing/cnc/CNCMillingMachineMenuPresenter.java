package eu.robojob.irscw.ui.configure.device.processing.cnc;

import eu.robojob.irscw.ui.configure.device.AbstractDeviceMenuPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public class CNCMillingMachineMenuPresenter extends AbstractDeviceMenuPresenter {

	private CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter;
	private CNCMillingMachinePickPresenter cncMillingMachinePickPresenter;
	private CNCMillingMachinePutPresenter cncMillingMachinePutPresenter;
	private CNCMillingMachineWorkPiecePresenter cncMillingMachineWorkPiecePresenter;
		
	public CNCMillingMachineMenuPresenter(final CNCMillingMachineMenuView view, final DeviceInformation deviceInfo, final CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter, 
			final CNCMillingMachinePickPresenter cncMillingMachinePickPresenter, final CNCMillingMachinePutPresenter cncMillingMachinePutPresenter,
			final CNCMillingMachineWorkPiecePresenter cncMillingMachineWorkPiecePresenter) {
		super(view, deviceInfo);
		this.cncMillingMachineConfigurePresenter = cncMillingMachineConfigurePresenter;
		this.cncMillingMachinePickPresenter = cncMillingMachinePickPresenter;
		this.cncMillingMachinePutPresenter = cncMillingMachinePutPresenter;
		this.cncMillingMachineWorkPiecePresenter = cncMillingMachineWorkPiecePresenter;
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

	public void configureWorkPiece() {
		((CNCMillingMachineMenuView) getView()).setWorkPieceActive();
		getParent().setBottomRightView(cncMillingMachineWorkPiecePresenter.getView());
	}
	
	@Override
	public void setBlocked(final boolean blocked) {
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		cncMillingMachinePickPresenter.setTextFieldListener(listener);
		cncMillingMachinePutPresenter.setTextFieldListener(listener);
		cncMillingMachineWorkPiecePresenter.setTextFieldListener(listener);
	}

	@Override
	public boolean isConfigured() {
		return cncMillingMachineConfigurePresenter.isConfigured() && cncMillingMachinePickPresenter.isConfigured() && cncMillingMachinePutPresenter.isConfigured() && cncMillingMachineWorkPiecePresenter.isConfigured();
	}

}
