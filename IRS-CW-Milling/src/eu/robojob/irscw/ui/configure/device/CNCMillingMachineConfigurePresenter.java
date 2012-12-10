package eu.robojob.irscw.ui.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.ClampingManner.Type;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.processing.cnc.CNCMillingMachineSettings;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPickSettings;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPutSettings;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.DataChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class CNCMillingMachineConfigurePresenter extends AbstractFormPresenter<CNCMillingMachineConfigureView, CNCMillingMachineMenuPresenter> implements ProcessFlowListener {

	private DeviceInformation deviceInfo;
	
	private static Logger logger = Logger.getLogger(CNCMillingMachineConfigurePresenter.class);
	private ProcessFlow processFlow;
	
	public CNCMillingMachineConfigurePresenter(CNCMillingMachineConfigureView view, DeviceInformation deviceInfo, DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		this.processFlow = deviceInfo.getPickStep().getProcessFlow();
		processFlow.addListener(this);
		view.setDeviceInfo(deviceInfo);
		view.setCNCMillingMachineIds(deviceManager.getCNCMachineIds());
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}
	
	public void changedDevice(String deviceId) {
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
	
	public void changedClampingTypeLength() {
		deviceInfo.getProcessingStep().getProcessFlow().getClampingType().setType(Type.LENGTH);
		view.refreshClampType();
		// TODO: change, let all Configure presenters listen for changes and update their own data!
		for (AbstractProcessStep step : deviceInfo.getPickStep().getProcessFlow().getProcessSteps()) {
			if (step instanceof AbstractTransportStep) {
				((AbstractTransportStep) step).setRelativeTeachedOffset(null);
			}
		}
		deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPickStep().getProcessFlow(), deviceInfo.getPickStep(), true));
	}
	
	public void changedClampingTypeWidth() {
		deviceInfo.getProcessingStep().getProcessFlow().getClampingType().setType(Type.WIDTH);
		view.refreshClampType();
		// TODO: change, let all Configure presenters listen for changes and update their own data!
		for (AbstractProcessStep step : deviceInfo.getPickStep().getProcessFlow().getProcessSteps()) {
			if (step instanceof AbstractTransportStep) {
				((AbstractTransportStep) step).setRelativeTeachedOffset(null);
			}
		}
		deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPickStep().getProcessFlow(), deviceInfo.getPickStep(), true));
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
		((CNCMillingMachineSettings) deviceInfo.getPickStep().getDevice().getDeviceSettings()).setClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea(), clamping);
		((CNCMillingMachineSettings) deviceInfo.getPutStep().getDevice().getDeviceSettings()).setClamping(deviceInfo.getPutStep().getDeviceSettings().getWorkArea(), clamping);
		deviceInfo.getPutStep().setRelativeTeachedOffset(null);
		deviceInfo.getPickStep().setRelativeTeachedOffset(null);
		deviceInfo.getPutStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPutStep().getProcessFlow(), deviceInfo.getPutStep(), true));
		deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPickStep().getProcessFlow(), deviceInfo.getPickStep(), true));
	}

	@Override
	public boolean isConfigured() {
		DevicePickSettings pickSettings = deviceInfo.getPickStep().getDeviceSettings();
		AbstractRobotPickSettings robotPickSettings = deviceInfo.getPickStep().getRobotSettings();
		DevicePutSettings putSettings = deviceInfo.getPutStep().getDeviceSettings();
		AbstractRobotPutSettings robotPutSettings = deviceInfo.getPutStep().getRobotSettings();
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

	@Override
	public void modeChanged(ModeChangedEvent e) {}
	@Override
	public void activeStepChanged(ActiveStepChangedEvent e) {}
	@Override
	public void exceptionOccured(ExceptionOccuredEvent e) {}
	@Override
	public void finishedAmountChanged(FinishedAmountChangedEvent e) {}
	@Override
	public void dataChanged(ProcessFlowEvent e) {
		DataChangedEvent de = (DataChangedEvent) e;
		if (de.getStep() instanceof PickStep) {
			int changedStepIndex = processFlow.getStepIndex(de.getStep());
			int currentStepIndex = processFlow.getStepIndex(deviceInfo.getPutStep());
			//TODO NOT ALWAYS CORRECT!
			if (changedStepIndex >= 0) {
				if (changedStepIndex < currentStepIndex) {
					boolean invasiveSteps = false;
					for (int i = changedStepIndex; i < currentStepIndex; i++) {
						AbstractProcessStep step = processFlow.getStep(i);
						if (step instanceof ProcessingStep){
							if (((ProcessingStep) step).getDevice().isInvasive()) {
								invasiveSteps = true;
							}
								
						}
					}
					if (!invasiveSteps) {
						PickStep prevPickStep = (PickStep) de.getStep();
						deviceInfo.getPickStep().getRobotSettings().getWorkPiece().getDimensions().setLength(prevPickStep.getRobotSettings().getWorkPiece().getDimensions().getLength());
						deviceInfo.getPickStep().getRobotSettings().getWorkPiece().getDimensions().setWidth(prevPickStep.getRobotSettings().getWorkPiece().getDimensions().getWidth());
						if (deviceInfo.getPickStep().getRobotSettings().getWorkPiece().getDimensions().getHeight() > prevPickStep.getRobotSettings().getWorkPiece().getDimensions().getHeight()) {
							deviceInfo.getPickStep().getRobotSettings().getWorkPiece().getDimensions().setHeight(prevPickStep.getRobotSettings().getWorkPiece().getDimensions().getHeight());
						}
					}
				}
			}
		}
	}
}
