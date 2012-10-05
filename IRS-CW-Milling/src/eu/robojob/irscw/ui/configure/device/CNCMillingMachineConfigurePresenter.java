package eu.robojob.irscw.ui.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.CNCMillingMachine.CNCMillingMachinePickSettings;
import eu.robojob.irscw.external.device.CNCMillingMachine.CNCMillingMachinePutSettings;
import eu.robojob.irscw.external.device.CNCMillingMachine.CNCMillingMachineSettings;
import eu.robojob.irscw.external.device.CNCMillingMachine.CNCMillingMachineStartCylusSettings;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPickSettings;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPutSettings;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
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
		// TODO: changed device!
	}
	
	public void changedWorkArea(String workAreaId) {
		logger.debug("changed workarea to: " + workAreaId);
		WorkArea workArea = null;
		if (workAreaId != null) {
			workArea = deviceInfo.getDevice().getWorkAreaById(workAreaId);
			if (workArea == null) {
				throw new IllegalArgumentException("Unknown workarea id");
			} else {
				if ((workArea != deviceInfo.getPutStep().getDeviceSettings().getWorkArea()) || (workArea != deviceInfo.getPickStep().getDeviceSettings().getWorkArea()) ) {
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
				if ( (clamping != ((CNCMillingMachineSettings) deviceInfo.getDeviceSettings()).getClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea())) ||
						(clamping != ((CNCMillingMachineSettings) deviceInfo.getDeviceSettings()).getClamping(deviceInfo.getPutStep().getDeviceSettings().getWorkArea())) ){
					setClamping(clamping);
				}
			}
		}
	}

	// these methods should only be called when a combo-box value is changed, into a real value
	private void setWorkArea(WorkArea workArea) {
		logger.debug("Changed workarea-settings to: " + workArea);
		deviceInfo.getPickStep().getDeviceSettings().setWorkArea(workArea);
		deviceInfo.getPickStep().getRobotSettings().setWorkArea(workArea);
		deviceInfo.getPutStep().getDeviceSettings().setWorkArea(workArea);
		deviceInfo.getPutStep().getRobotSettings().setWorkArea(workArea);
		deviceInfo.getProcessingStep().getStartCyclusSettings().setWorkArea(workArea);
		if (deviceInfo.hasInterventionStepAfterPut()) {
			deviceInfo.getInterventionStepAfterPut().getInterventionSettings().setWorkArea(workArea);
		}
		if (deviceInfo.hasInterventionStepBeforePick()) {
			deviceInfo.getInterventionStepBeforePick().getInterventionSettings().setWorkArea(workArea);
		}
	}
	
	// these methods should only be called when a combo-box value is changed, into a real value
	private void setClamping(Clamping clamping) {
		logger.debug("Changed clamping-settings to: " + clamping);
		CNCMillingMachineSettings settings = (CNCMillingMachineSettings) deviceInfo.getDeviceSettings();
		settings.setClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea(), clamping);
		deviceInfo.getDevice().loadDeviceSettings(settings);
	}

	@Override
	public boolean isConfigured() {
		CNCMillingMachinePickSettings pickSettings = (CNCMillingMachinePickSettings) deviceInfo.getPickStep().getDeviceSettings();
		AbstractRobotPickSettings robotPickSettings = deviceInfo.getPickStep().getRobotSettings();
		CNCMillingMachinePutSettings putSettings = (CNCMillingMachinePutSettings) deviceInfo.getPutStep().getDeviceSettings();
		AbstractRobotPutSettings robotPutSettings = deviceInfo.getPutStep().getRobotSettings();
		CNCMillingMachineStartCylusSettings startCyclusSettings = (CNCMillingMachineStartCylusSettings) deviceInfo.getProcessingStep().getStartCyclusSettings();
		CNCMillingMachineSettings deviceSettings = (CNCMillingMachineSettings) deviceInfo.getDeviceSettings();
		// TODO take into account start cyclus settings
		if (    
				(pickSettings.getWorkArea() != null) && 
				(robotPickSettings.getWorkArea() != null) &&
				(pickSettings.getWorkArea().equals(robotPickSettings.getWorkArea())) &&
				(pickSettings.getWorkArea().getActiveClamping() != null) && 
				(deviceSettings.getClamping(pickSettings.getWorkArea()).equals(pickSettings.getWorkArea().getActiveClamping())) && 
				(putSettings.getWorkArea() != null) && 
				(robotPutSettings.getWorkArea() != null) &&
				(putSettings.getWorkArea().equals(robotPutSettings.getWorkArea())) &&
				(putSettings.getWorkArea().getActiveClamping() != null) && 
				(deviceSettings.getClamping(putSettings.getWorkArea()).equals(putSettings.getWorkArea().getActiveClamping())) 
			)  {
			return true;
		} else {
			return false;
		}
	}
	
	
}
