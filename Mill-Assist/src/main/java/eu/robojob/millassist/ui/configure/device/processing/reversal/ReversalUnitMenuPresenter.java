package eu.robojob.millassist.ui.configure.device.processing.reversal;

import eu.robojob.millassist.ui.configure.device.AbstractDeviceMenuPresenter;
import eu.robojob.millassist.ui.configure.device.DeviceMenuView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class ReversalUnitMenuPresenter extends AbstractDeviceMenuPresenter {

	private ReversalUnitConfigurePresenter deviceConfigurePresenter;
	private ReversalUnitPickPresenter reversalUnitPickPresenter;
	private ReversalUnitPutPresenter reversalUnitPutPresenter;
	
	static final String SAME_APPROACHTYPES = "ReversalUnitMenuPresenter.sameApproachTypes";
	
	public ReversalUnitMenuPresenter(final DeviceMenuView view, final DeviceInformation deviceInfo, 
			final ReversalUnitConfigurePresenter deviceConfigurePresenter, final ReversalUnitPutPresenter reversalUnitPutPresenter, 
			final ReversalUnitPickPresenter reversalUnitPickPresenter) {
		super(view, deviceInfo);
		this.deviceConfigurePresenter = deviceConfigurePresenter;
		deviceConfigurePresenter.setMenuPresenter(this);
		this.reversalUnitPickPresenter = reversalUnitPickPresenter;
		reversalUnitPickPresenter.setMenuPresenter(this);
		this.reversalUnitPutPresenter = reversalUnitPutPresenter;
		reversalUnitPutPresenter.setMenuPresenter(this);
	}

	@Override
	public void configurePick() {
		getView().setConfigurePickActive();
		getParent().setBottomRightView(reversalUnitPickPresenter.getView());
	}

	@Override
	public void configurePut() {
		getView().setConfigurePutActive();
		getParent().setBottomRightView(reversalUnitPutPresenter.getView());
	}

	@Override
	public void configureDevice() {
		getView().setProcessingActive();
		getParent().setBottomRightView(deviceConfigurePresenter.getView());
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {	
		reversalUnitPickPresenter.setTextFieldListener(listener);
		reversalUnitPutPresenter.setTextFieldListener(listener);
	}

	@Override
	public void setBlocked(final boolean blocked) {
	}
	
	boolean isSameApproachType() {
		return (reversalUnitPickPresenter.getPickStep().getRobotSettings().getApproachType().equals(reversalUnitPutPresenter.getPutStep().getRobotSettings().getApproachType()));
	}

	@Override
	public boolean isConfigured() {
		if (isSameApproachType()) {
			return false;
		}
		return (reversalUnitPickPresenter.isConfigured() && reversalUnitPutPresenter.isConfigured());
	}
	
	@Override
	public void openFirst() {
		configureDevice();
	}

	@Override
	public void unregisterListeners() { }
	

}
