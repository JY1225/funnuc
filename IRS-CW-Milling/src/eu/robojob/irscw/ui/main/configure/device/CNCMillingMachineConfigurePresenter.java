package eu.robojob.irscw.ui.main.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class CNCMillingMachineConfigurePresenter extends AbstractFormPresenter<CNCMillingMachineConfigureView, CNCMillingMachineMenuPresenter>{

	private DeviceInformation deviceInfo;
	private DeviceManager deviceManager;
	
	private static Logger logger = Logger.getLogger(CNCMillingMachineConfigurePresenter.class);
	
	public CNCMillingMachineConfigurePresenter(CNCMillingMachineConfigureView view, DeviceInformation deviceInfo, DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		this.deviceManager = deviceManager;
		view.setDeviceInfo(deviceInfo);
		view.setCNCMillingMachineIds(deviceManager.getCNCMachineIds());
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}
	
	public void changedDevice(String deviceId) {
		logger.debug("changed device to: " + deviceId);
		if (deviceId != null) {
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			if (deviceInfo.getDevice() != device) {
				setWorkArea(null);
				setClamping(null);
				view.refreshWorkAreas();
			}
		}
	}
	
	public void changedWorkArea(String workAreaId) {
		logger.debug("changed workarea to: " + workAreaId);
		WorkArea workArea = null;
		if (workAreaId != null) {
			workArea = deviceInfo.getDevice().getWorkAreaById(workAreaId);
			if (workArea == null) {
				throw new IllegalArgumentException("Unknown workarea id");
			} else {
				if (workArea != deviceInfo.getPutStep().getDeviceSettings().getWorkArea()) {
					setWorkArea(workArea);
					setClamping(null);
					view.refreshClampings();
				}
			}
		}
	}
	
	public void changedClamping(String clampingId) {
		logger.debug("changed clamping to: " + clampingId);
		Clamping clamping = null;
		if (clampingId != null) {
			clamping = deviceInfo.getPickStep().getDeviceSettings().getWorkArea().getClampingById(clampingId);
			if (clamping == null) {
				throw new IllegalArgumentException("Unknown clamping");
			} else {
				if (clamping != deviceInfo.getPutStep().getDeviceSettings().getClamping()) {
					setClamping(clamping);
				}
			}
		}
	}

	// these methods should only be called when a combo-box value is changed, into a real value
	private void setWorkArea(WorkArea workArea) {
		logger.debug("Changed workarea-settings to: " + workArea);
		deviceInfo.getPickStep().getDeviceSettings().setWorkArea(workArea);
		deviceInfo.getPutStep().getDeviceSettings().setWorkArea(workArea);
		deviceInfo.getProcessingStep().getStartCyclusSettings().setWorkArea(workArea);
	}
	
	// these methods should only be called when a combo-box value is changed, into a real value
	private void setClamping(Clamping clamping) {
		logger.debug("Changed clamping-settings to: " + clamping);
		deviceInfo.getPickStep().getDeviceSettings().setClamping(clamping);
		deviceInfo.getPutStep().getDeviceSettings().setClamping(clamping);
	}
	
	
}
