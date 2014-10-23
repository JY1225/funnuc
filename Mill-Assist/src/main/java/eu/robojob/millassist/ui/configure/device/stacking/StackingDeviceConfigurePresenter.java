package eu.robojob.millassist.ui.configure.device.stacking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.event.ProcessChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class StackingDeviceConfigurePresenter extends AbstractFormPresenter<StackingDeviceConfigureView, AbstractStackingDeviceMenuPresenter> {

	private DeviceInformation deviceInfo;
	private DeviceManager deviceManager;
	
	private static Logger logger = LogManager.getLogger(StackingDeviceConfigurePresenter.class.getName());
	
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
		if(deviceInfo.getDevice() instanceof BasicStackPlate) {
			((BasicStackPlate) deviceInfo.getDevice()).setGridPlate(deviceManager.getGridPlateByName(getGridPlateName()));
		}
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void changedDevice(final String deviceName, final String gridPlateName) {
		AbstractDevice device = getDeviceByName(deviceName);
		AbstractDevice prevDevice = deviceInfo.getDevice();
		if ((prevDevice instanceof AbstractStackingDevice) && (device instanceof AbstractStackingDevice)) {
			if (((AbstractStackingDevice) prevDevice).getRawWorkPiece() != null) {
				((AbstractStackingDevice) device).setRawWorkPiece(((AbstractStackingDevice) prevDevice).getRawWorkPiece());
			}
			if (((AbstractStackingDevice) prevDevice).getFinishedWorkPiece() != null) {
				((AbstractStackingDevice) device).setFinishedWorkPiece(((AbstractStackingDevice) prevDevice).getFinishedWorkPiece());
			}
			if((prevDevice instanceof BasicStackPlate) && (device instanceof BasicStackPlate)) {
				logger.debug("Gridplate " + gridPlateName + " added.");
				((BasicStackPlate) device).setGridPlate(deviceManager.getGridPlateByName(gridPlateName));
			}
		}
		if (deviceInfo.hasPickStep()) {
			// TODO remove device settings currently present, only if this was only step with this device!
			// change device for pick
			deviceInfo.getPickStep().getProcessFlow().setDeviceSettings(device, device.getDeviceSettings());
			device.loadDeviceSettings(deviceInfo.getPickStep().getProcessFlow().getDeviceSettings(device));
			deviceInfo.getPickStep().setDeviceSettings(device.getDefaultPickSettings(((AbstractStackingDevice) device).getRawWorkPiece().getType()));
			deviceInfo.getPickStep().getRobotSettings().setWorkArea(deviceInfo.getPickStep().getDeviceSettings().getWorkArea());
			deviceInfo.getPickStep().getRobotSettings().setSmoothPoint(new Coordinates(device.getDefaultPickSettings(((AbstractStackingDevice) device).getRawWorkPiece().getType()).getWorkArea().getDefaultClamping().getSmoothFromPoint()));
			//deviceInfo.getPickStep().setRelativeTeachedOffset(null);
			deviceInfo.getPickStep().getProcessFlow().initialize();
			deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new ProcessChangedEvent(deviceInfo.getPickStep().getProcessFlow()));
		} else if (deviceInfo.hasPutStep()) {
			// change device for put
			deviceInfo.getPutStep().getProcessFlow().setDeviceSettings(device, device.getDeviceSettings());
			device.loadDeviceSettings(deviceInfo.getPutStep().getProcessFlow().getDeviceSettings(device));
			deviceInfo.getPutStep().setDeviceSettings(device.getDefaultPutSettings(((AbstractStackingDevice) device).getFinishedWorkPiece().getType()));
			deviceInfo.getPutStep().getRobotSettings().setWorkArea(deviceInfo.getPutStep().getDeviceSettings().getWorkArea());
			deviceInfo.getPutStep().getRobotSettings().setSmoothPoint(new Coordinates(device.getDefaultPutSettings(((AbstractStackingDevice) device).getFinishedWorkPiece().getType()).getWorkArea().getDefaultClamping().getSmoothToPoint()));
			//deviceInfo.getPutStep().setRelativeTeachedOffset(null);
			deviceInfo.getPutStep().getProcessFlow().initialize();
			deviceInfo.getPutStep().getProcessFlow().processProcessFlowEvent(new ProcessChangedEvent(deviceInfo.getPutStep().getProcessFlow()));
		} else {
			throw new IllegalStateException("No pick or put step.");
		}
		deviceInfo.setDevice(device);
		getMenuPresenter().refreshClearCache();
		getMenuPresenter().getParent().configureDevice(deviceInfo.getIndex());
	}

	public AbstractDevice getDeviceByName(String name) {
		return deviceManager.getDeviceByName(name);
	}
	
	public String getGridPlateName() {
		if(getGridPlateLayout() == null)
			return null;
		else
			return getGridPlateLayout().getName();
	}
	
	private GridPlateLayout getGridPlateLayout() {
		try {
			AbstractStackPlateDeviceSettings devSettings = (AbstractStackPlateDeviceSettings) deviceInfo.getDeviceSettings();
			return deviceManager.getGridPlateByID(devSettings.getGridId());
		} catch(ClassCastException e) {
			return null;
		}
	}

	@Override
	public boolean isConfigured() {
		if (deviceInfo.getDevice() != null) {
			return true;
		}
		return false;
	}
	
	public void updateGridPlates() {
		getView().setGridPlates(deviceManager.getAllGridPlateNames());
	}
	
}
