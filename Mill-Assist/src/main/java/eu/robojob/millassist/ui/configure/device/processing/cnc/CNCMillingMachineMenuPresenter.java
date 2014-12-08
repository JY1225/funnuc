package eu.robojob.millassist.ui.configure.device.processing.cnc;

import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.ui.configure.device.AbstractDeviceMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.ui.general.model.DeviceInformation;
import eu.robojob.millassist.util.Translator;

public class CNCMillingMachineMenuPresenter extends AbstractDeviceMenuPresenter {

	private CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter;
	private CNCMillingMachinePickPresenter cncMillingMachinePickPresenter;
	private CNCMillingMachinePutPresenter cncMillingMachinePutPresenter;
	private CNCMillingMachineWorkPiecePresenter cncMillingMachineWorkPiecePresenter;
	
	private static final String DIFFERENT_AMOUNT = "CNCMillingMachineMenuPresenter.differentAmount";
		
	public CNCMillingMachineMenuPresenter(final CNCMillingMachineMenuView view, final DeviceInformation deviceInfo, final CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter, 
			final CNCMillingMachinePickPresenter cncMillingMachinePickPresenter, final CNCMillingMachinePutPresenter cncMillingMachinePutPresenter,
			final CNCMillingMachineWorkPiecePresenter cncMillingMachineWorkPiecePresenter) {
		super(view, deviceInfo);
		this.cncMillingMachineConfigurePresenter = cncMillingMachineConfigurePresenter;
		cncMillingMachineConfigurePresenter.setMenuPresenter(this);
		this.cncMillingMachinePickPresenter = cncMillingMachinePickPresenter;
		cncMillingMachinePickPresenter.setMenuPresenter(this);
		this.cncMillingMachinePutPresenter = cncMillingMachinePutPresenter;
		cncMillingMachinePutPresenter.setMenuPresenter(this);
		this.cncMillingMachineWorkPiecePresenter = cncMillingMachineWorkPiecePresenter;
		cncMillingMachineWorkPiecePresenter.setMenuPresenter(this);
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
		boolean isConfigured = true;
		for (Zone zone: getDeviceInformation().getDevice().getZones()) {
			if (!zone.clampingSelectionCorrect()) {
				cncMillingMachineConfigurePresenter.getView().showNotification(Translator.getTranslation(DIFFERENT_AMOUNT), Type.WARNING);
				return false;
			}
		}
		return cncMillingMachineConfigurePresenter.isConfigured() 
			&& cncMillingMachinePickPresenter.isConfigured() 
			&& cncMillingMachinePutPresenter.isConfigured() 
			&& cncMillingMachineWorkPiecePresenter.isConfigured() 
			&& isConfigured;
	}

	@Override
	public void unregisterListeners() {
		cncMillingMachineWorkPiecePresenter.unregister();
	}
	
	public void changedTIM(final boolean turnInMachine) {
		cncMillingMachinePickPresenter.changedTIM(turnInMachine);
		cncMillingMachinePutPresenter.changedTIM(turnInMachine);
	}
}
