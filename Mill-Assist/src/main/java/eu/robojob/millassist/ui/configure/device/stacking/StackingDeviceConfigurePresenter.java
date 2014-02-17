package eu.robojob.millassist.ui.configure.device.stacking;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.event.ProcessChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class StackingDeviceConfigurePresenter extends AbstractFormPresenter<StackingDeviceConfigureView, AbstractStackingDeviceMenuPresenter> {

	private DeviceInformation deviceInfo;
	private DeviceManager deviceManager;	
	
	public StackingDeviceConfigurePresenter(final StackingDeviceConfigureView view, final DeviceInformation deviceInfo, final DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
		this.deviceManager = deviceManager;
		if (deviceInfo.hasPickStep()) {
			view.setStackingDeviceIds(deviceManager.getStackingFromDeviceNames());
		} else if (deviceInfo.hasPutStep()) {
			view.setStackingDeviceIds(deviceManager.getStackingToDeviceNames());
		} else {
			throw new IllegalStateException("No pick or put step.");
		}
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void changedDevice(final String deviceName) {
		AbstractDevice device = deviceManager.getDeviceByName(deviceName);
		AbstractDevice prevDevice = deviceInfo.getDevice();
		if ((prevDevice instanceof AbstractStackingDevice) && (device instanceof AbstractStackingDevice)) {
			((AbstractStackingDevice) device).setRawWorkPiece(((AbstractStackingDevice) prevDevice).getRawWorkPiece());
			((AbstractStackingDevice) device).setFinishedWorkPiece(((AbstractStackingDevice) prevDevice).getFinishedWorkPiece());
		}
		if (deviceInfo.hasPickStep()) {
			// TODO remove device settings currently present, only if this was only step with this device!
			// change device for pick
			deviceInfo.getPickStep().getProcessFlow().setDeviceSettings(device, device.getDeviceSettings());
			device.loadDeviceSettings(deviceInfo.getPickStep().getProcessFlow().getDeviceSettings(device));
			deviceInfo.getPickStep().setDeviceSettings(device.getDefaultPickSettings());
			deviceInfo.getPickStep().getRobotSettings().setWorkArea(deviceInfo.getPickStep().getDeviceSettings().getWorkArea());
			deviceInfo.getPickStep().getRobotSettings().setSmoothPoint(new Coordinates(device.getDefaultPickSettings().getWorkArea().getActiveClamping().getSmoothFromPoint()));
			//deviceInfo.getPickStep().setRelativeTeachedOffset(null);
			deviceInfo.getPickStep().getProcessFlow().initialize();
			deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new ProcessChangedEvent(deviceInfo.getPickStep().getProcessFlow()));
		} else if (deviceInfo.hasPutStep()) {
			// change device for put
			deviceInfo.getPutStep().getProcessFlow().setDeviceSettings(device, device.getDeviceSettings());
			device.loadDeviceSettings(deviceInfo.getPutStep().getProcessFlow().getDeviceSettings(device));
			deviceInfo.getPutStep().setDeviceSettings(device.getDefaultPutSettings());
			deviceInfo.getPutStep().getRobotSettings().setWorkArea(deviceInfo.getPutStep().getDeviceSettings().getWorkArea());
			deviceInfo.getPutStep().getRobotSettings().setSmoothPoint(new Coordinates(device.getDefaultPutSettings().getWorkArea().getActiveClamping().getSmoothToPoint()));
			//deviceInfo.getPutStep().setRelativeTeachedOffset(null);
			deviceInfo.getPutStep().getProcessFlow().initialize();
			deviceInfo.getPutStep().getProcessFlow().processProcessFlowEvent(new ProcessChangedEvent(deviceInfo.getPutStep().getProcessFlow()));
		} else {
			throw new IllegalStateException("No pick or put step.");
		}
		getMenuPresenter().refreshClearCache();
		getMenuPresenter().getParent().configureDevice(deviceInfo.getIndex());
	}

	@Override
	public boolean isConfigured() {
		if (deviceInfo.getDevice() != null) {
			return true;
		}
		return false;
	}

}
