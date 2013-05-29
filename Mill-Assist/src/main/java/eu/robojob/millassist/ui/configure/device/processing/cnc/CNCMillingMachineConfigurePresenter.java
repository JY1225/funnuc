package eu.robojob.millassist.ui.configure.device.processing.cnc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner.Type;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class CNCMillingMachineConfigurePresenter extends AbstractFormPresenter<CNCMillingMachineConfigureView, CNCMillingMachineMenuPresenter> {

	private DeviceInformation deviceInfo;
	
	private static Logger logger = LogManager.getLogger(CNCMillingMachineConfigurePresenter.class.getName());
	
	public CNCMillingMachineConfigurePresenter(final CNCMillingMachineConfigureView view, final DeviceInformation deviceInfo, final DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
		view.setCNCMillingMachineIds(deviceManager.getCNCMachineNames());
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void changedDevice(final String deviceId) {
		// TODO: change device!
	}
	
	public void changedWorkArea(final String workAreaId) {
		logger.debug("Changed workarea [" + workAreaId + "].");
		WorkArea workArea = null;
		if (workAreaId != null) {
			workArea = deviceInfo.getDevice().getWorkAreaByName(workAreaId);
			if (workArea == null) {
				throw new IllegalArgumentException("Unknown workarea-id [" + workAreaId + "].");
			} else {
				if ((workArea != deviceInfo.getPutStep().getDeviceSettings().getWorkArea()) || (workArea != deviceInfo.getPickStep().getDeviceSettings().getWorkArea())) {
					setWorkArea(workArea);
					setClamping(null);
					getView().refreshClampings();
				}
			}
		}
	}
	
	public void changedClamping(final String clampingId) {
		logger.debug("Changed clamping [" + clampingId + "].");
		Clamping clamping = null;
		if (clampingId != null) {
			clamping = deviceInfo.getPickStep().getDeviceSettings().getWorkArea().getClampingByName(clampingId);
			if (clamping == null) {
				throw new IllegalArgumentException("Unknown clamping-id [" + clampingId + "].");
			} else {
				if ((clamping != deviceInfo.getDeviceSettings().getClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea()))
						|| (clamping != deviceInfo.getDeviceSettings().getClamping(deviceInfo.getPutStep().getDeviceSettings().getWorkArea()))) {
					setClamping(clamping);
				}
			}
		}
	}
	
	public void changedClampingTypeLength() {
		logger.debug("Changed clamping type to length.");
		deviceInfo.getProcessingStep().getProcessFlow().getClampingType().setType(Type.LENGTH);
		getView().refreshClampType();
		for (AbstractProcessStep step : deviceInfo.getPickStep().getProcessFlow().getProcessSteps()) {
			if (step instanceof AbstractTransportStep) {
				((AbstractTransportStep) step).setRelativeTeachedOffset(null);
			}
		}
		deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPickStep().getProcessFlow(), deviceInfo.getPickStep(), true));
	}
	
	public void changedClampingTypeWidth() {
		logger.debug("Changed clamping type to width.");
		deviceInfo.getProcessingStep().getProcessFlow().getClampingType().setType(Type.WIDTH);
		getView().refreshClampType();
		for (AbstractProcessStep step : deviceInfo.getPickStep().getProcessFlow().getProcessSteps()) {
			if (step instanceof AbstractTransportStep) {
				((AbstractTransportStep) step).setRelativeTeachedOffset(null);
			}
		}
		deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPickStep().getProcessFlow(), deviceInfo.getPickStep(), true));
	}

	private void setWorkArea(final WorkArea workArea) {
		deviceInfo.getPickStep().getDeviceSettings().setWorkArea(workArea);
		deviceInfo.getPickStep().getRobotSettings().setWorkArea(workArea);
		deviceInfo.getPutStep().getDeviceSettings().setWorkArea(workArea);
		deviceInfo.getPutStep().getRobotSettings().setWorkArea(workArea);
		deviceInfo.getProcessingStep().getDeviceSettings().setWorkArea(workArea);
		if (deviceInfo.hasInterventionStepAfterPut()) {
			deviceInfo.getInterventionStepAfterPut().getDeviceSettings().setWorkArea(workArea);
		}
		if (deviceInfo.hasInterventionStepBeforePick()) {
			deviceInfo.getInterventionStepBeforePick().getDeviceSettings().setWorkArea(workArea);
		}
	}
	
	private void setClamping(final Clamping clamping) {
		DeviceSettings settings = deviceInfo.getDeviceSettings();
		settings.setClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea(), clamping);
		deviceInfo.getDevice().loadDeviceSettings(settings);
		(deviceInfo.getPickStep().getDevice().getDeviceSettings()).setClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea(), clamping);
		(deviceInfo.getPutStep().getDevice().getDeviceSettings()).setClamping(deviceInfo.getPutStep().getDeviceSettings().getWorkArea(), clamping);
		deviceInfo.getPutStep().setRelativeTeachedOffset(null);
		deviceInfo.getPickStep().setRelativeTeachedOffset(null);
		deviceInfo.getPutStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPutStep().getProcessFlow(), deviceInfo.getPutStep(), true));
		deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPickStep().getProcessFlow(), deviceInfo.getPickStep(), true));
	}

	@Override
	public boolean isConfigured() {
		DevicePickSettings pickSettings = deviceInfo.getPickStep().getDeviceSettings();
		RobotPickSettings robotPickSettings = deviceInfo.getPickStep().getRobotSettings();
		DevicePutSettings putSettings = deviceInfo.getPutStep().getDeviceSettings();
		RobotPutSettings robotPutSettings = deviceInfo.getPutStep().getRobotSettings();
		DeviceSettings deviceSettings = (DeviceSettings) deviceInfo.getDeviceSettings();
		ProcessingDeviceStartCyclusSettings startCyclusSettings = deviceInfo.getProcessingStep().getDeviceSettings();
		// TODO take into account start cycle settings
		if ((pickSettings.getWorkArea() != null)
				&& (robotPickSettings.getWorkArea() != null)
				&& (pickSettings.getWorkArea().equals(robotPickSettings.getWorkArea()))
				&& (pickSettings.getWorkArea().getActiveClamping() != null)
				&& (deviceSettings.getClamping(pickSettings.getWorkArea()) != null)
				&& (deviceSettings.getClamping(pickSettings.getWorkArea()).equals(pickSettings.getWorkArea().getActiveClamping()))
				&& (startCyclusSettings.getWorkArea() != null)
				&& (startCyclusSettings.getWorkArea().getActiveClamping() != null)
				&& (putSettings.getWorkArea() != null)
				&& (robotPutSettings.getWorkArea() != null)
				&& (putSettings.getWorkArea().equals(robotPutSettings.getWorkArea()))
				&& (putSettings.getWorkArea().getActiveClamping() != null)
				&& (deviceSettings.getClamping(putSettings.getWorkArea()).equals(putSettings.getWorkArea().getActiveClamping())) 
			)  {
			return true;
		}
		return false;
	}
}
