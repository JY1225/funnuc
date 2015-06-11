package eu.robojob.millassist.ui.configure.device.stacking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPalletDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.event.ProcessChangedEvent;
import eu.robojob.millassist.ui.configure.device.stacking.pallet.PalletDeviceSettings;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.ui.general.model.DeviceInformation;
import eu.robojob.millassist.util.Translator;

public class StackingDeviceConfigurePresenter extends AbstractFormPresenter<StackingDeviceConfigureView, AbstractStackingDeviceMenuPresenter> {

	private DeviceInformation deviceInfo;
	private DeviceManager deviceManager;
	
    private static final String NO_GRID_SELECTED = "StackingDeviceConfigurePresenter.noGridSelected";
	
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
	
	public void changedDevice(final String deviceName, final String gridPlateName, final String palletLayoutName) {
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
			if(device instanceof Pallet) {
			    logger.debug("Gridplate " + gridPlateName + " added.");
                ((Pallet) device).setPalletLayout(deviceManager.getPalletLayoutByName(palletLayoutName));
                GridPlate grid = deviceManager.getGridPlateByName(gridPlateName);
                if (grid == null) {
                    getView().showNotification(Translator.getTranslation(NO_GRID_SELECTED), Type.WARNING);
                    return;
                } else {
                    ((Pallet) device).setGridPlate(deviceManager.getGridPlateByName(gridPlateName));
                }
			}
			if(prevDevice instanceof UnloadPallet) {
			    ((UnloadPallet) prevDevice).setPalletLayout(deviceManager.getPalletLayoutByName(palletLayoutName));
			    
			    if(deviceInfo.getPutStep().getProcessFlow().getProcessSteps().get(0) instanceof InterventionStep){
			        deviceInfo.getPutStep().getProcessFlow().removeStep(deviceInfo.getPutStep().getProcessFlow().getProcessSteps().get(0));
			    }
			}
			if(device instanceof UnloadPallet) {
			    ((UnloadPallet) device).setPalletLayout(deviceManager.getPalletLayoutByName(palletLayoutName));
			    if(prevDevice instanceof BasicStackPlate) {
			        if(deviceInfo.hasPutStep()) {
			            ((BasicStackPlate) prevDevice).setFinishedWorkPiece(null);
			            ((AbstractStackPlateDeviceSettings)deviceInfo.getPutStep().getProcessFlow().getDeviceSettings(prevDevice)).setFinishedWorkPiece(null);
			        }
			    }
			}
		}
		if (deviceInfo.hasPickStep()) {
			// TODO remove device settings currently present, only if this was only step with this device!
			// change device for pick
			deviceInfo.getPickStep().getProcessFlow().setDeviceSettings(device, device.getDeviceSettings());
			device.loadDeviceSettings(deviceInfo.getPickStep().getProcessFlow().getDeviceSettings(device));
			deviceInfo.getPickStep().setDeviceSettings(device.getDefaultPickSettings(1));
			deviceInfo.getPickStep().getRobotSettings().setWorkArea(deviceInfo.getPickStep().getDeviceSettings().getWorkArea());
			deviceInfo.getPickStep().getRobotSettings().setSmoothPoint(new Coordinates(device.getDefaultPickSettings(1).getWorkArea().getDefaultClamping().getSmoothFromPoint()));
			//deviceInfo.getPickStep().setRelativeTeachedOffset(null);
			deviceInfo.getPickStep().getProcessFlow().initialize();
			deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new ProcessChangedEvent(deviceInfo.getPickStep().getProcessFlow()));
		} else if (deviceInfo.hasPutStep()) {
			// change device for put
			deviceInfo.getPutStep().getProcessFlow().setDeviceSettings(device, device.getDeviceSettings());
			device.loadDeviceSettings(deviceInfo.getPutStep().getProcessFlow().getDeviceSettings(device));
			deviceInfo.getPutStep().setDeviceSettings(device.getDefaultPutSettings(1));
			deviceInfo.getPutStep().getRobotSettings().setWorkArea(deviceInfo.getPutStep().getDeviceSettings().getWorkArea());
			deviceInfo.getPutStep().getRobotSettings().setSmoothPoint(new Coordinates(device.getDefaultPutSettings(1).getWorkArea().getDefaultClamping().getSmoothToPoint()));
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
		try {
			AbstractStackPlateDeviceSettings devSettings = (AbstractStackPlateDeviceSettings) deviceInfo.getDeviceSettings();
			if (devSettings.getGridId() > 0) {
				return deviceManager.getGridPlateByID(devSettings.getGridId()).getName();
			} else {
				return null;
			}
		} catch (ClassCastException e) {
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
	
	public void updatePalletLayouts() {
	    getView().setPalletLayouts(deviceManager.getAllPalletLayoutNames());
	}
	
	public String getPalletLayoutName() {
	    if(deviceInfo.getDeviceSettings() instanceof UnloadPalletDeviceSettings) {
	        UnloadPalletDeviceSettings devSettings = (UnloadPalletDeviceSettings) deviceInfo.getDeviceSettings();
            return devSettings.getLayout().getName();
	    } else if(deviceInfo.getDeviceSettings() instanceof PalletDeviceSettings) {
	        PalletDeviceSettings devSettings = (PalletDeviceSettings) deviceInfo.getDeviceSettings();
            return devSettings.getPalletLayout().getName();
	    } else {
	        return null;
	    }

	}
	
}
