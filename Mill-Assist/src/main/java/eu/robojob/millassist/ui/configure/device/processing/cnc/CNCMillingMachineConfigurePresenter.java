package eu.robojob.millassist.ui.configure.device.processing.cnc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner.Type;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;
import eu.robojob.millassist.util.Translator;

public class CNCMillingMachineConfigurePresenter extends AbstractFormPresenter<CNCMillingMachineConfigureView, CNCMillingMachineMenuPresenter> {

	private DeviceInformation deviceInfo;
	private DeviceManager deviceManager;
	
	private static final String SAME_TYPE = "CNCMillingMachineConfigurePresenter.sameTypeClamp";
	private static final String DIFFERENT_FAMILY = "CNCMillingMachineConfigurePresenter.differentFamilyClamp";

	private static Logger logger = LogManager.getLogger(CNCMillingMachineConfigurePresenter.class.getName());
	
	public CNCMillingMachineConfigurePresenter(final CNCMillingMachineConfigureView view, final DeviceInformation deviceInfo, final DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		this.deviceManager = deviceManager;
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
	
	public void refreshMachineNames() {
		getView().setCNCMillingMachineIds(deviceManager.getCNCMachineNames());
	}
	
	public void changedWorkArea(final String workAreaName) {
		logger.debug("Changed workarea [" + workAreaName + "].");
		SimpleWorkArea workArea = null;
		if (workAreaName != null) {
			workArea = deviceInfo.getDevice().getWorkAreaByName(workAreaName).getWorkAreaWithSequence(deviceInfo.getCNCNbInFlow());
			if (workArea == null) {
				throw new IllegalArgumentException("Unknown workarea-id [" + workAreaName + "].");
			} else {
				if ((workArea != deviceInfo.getPutStep().getDeviceSettings().getWorkArea()) || (workArea != deviceInfo.getPickStep().getDeviceSettings().getWorkArea())) {
					deviceInfo.getPutStep().getDeviceSettings().getWorkArea().setInUse(false);
					deviceInfo.getPickStep().getDeviceSettings().getWorkArea().setInUse(false);
					setWorkArea(workArea);
					workArea.setInUse(true);
					setClamping(workArea.getDefaultClamping());
					addProcessFlowEvent();
					getView().refreshClampings();
				}
			}
		}
	}
	
	/**
	 * Add or remove a given clamping from the list of active clampings. Whether the clamping should be removed or added
	 * is solely dependent of the boolean value isSelected.
	 * 
	 * @param clamping to be removed/added
	 * @param isSelected flag 
	 * @see #addClamping(Clamping)
	 * @see #removeClamping(Clamping)
	 */
	public void changedClamping(final Clamping clamping, final boolean isSelected) {
		if (clamping == null) {
			throw new IllegalArgumentException("Clamping is null.");
		}
		getView().hideNotification();
		if(isSelected) {
			addClamping(clamping);
			addProcessFlowEvent();
		} else {
			removeClamping(clamping);
			addProcessFlowEvent();
			getView().setDefaultClampingText(clamping.getName(), false);
		}
	}
	
	/**
	 * Add the given clamping to the list of active clampings for the current workArea. In case a clamping is already provided for this
	 * workArea, the clamping will be added to the relatedClampings of the activeClamping.
	 * 
	 * @param clamping to add as one of the active clampings to be used for put and pick operations in the machine
	 */
	private void addClamping(final Clamping clamping) {
		DeviceSettings settings = deviceInfo.getDeviceSettings();
		Clamping activeClamping = settings.getDefaultClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea());
		if(activeClamping != null) {
			//Add related clamping to activeClamping
			if ((clamping != deviceInfo.getDeviceSettings().getDefaultClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea()))
					|| (clamping != deviceInfo.getDeviceSettings().getDefaultClamping(deviceInfo.getPutStep().getDeviceSettings().getWorkArea()))) {
				activeClamping.addRelatedClamping(clamping);
				getView().setDefaultClampingText(clamping.getName(), false);
				logger.debug("Related clamping " + clamping.getName() +" added.");
			}
		} else {
			logger.debug("Active clamping changed to " + clamping.getName());
			setClamping(clamping);
			getView().setDefaultClampingText(clamping.getName(), true);
		}
	}
	
	/**
	 * Removes the given clamping from the list of active clampings for the current workArea. In case the clamping to remove is the 
	 * active clamping, a new active clamping will be taken from the list of related clampings. If this list is empty, the active 
	 * clamping will not be changed, because we need at least 1 clamping to be able to process workpieces
	 * 
	 * @param clamping to be removed from the active clampings to be used for put and pick operations in the machine
	 */
	private void removeClamping(final Clamping clamping) {
		DeviceSettings settings = deviceInfo.getDeviceSettings();
		Clamping activeClamping = settings.getDefaultClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea());
		if(activeClamping.equals(clamping)) {
			//Remove the active clamping and set the active clamping to one of the related clampings if provided - otherwise set to null
			if(activeClamping.getRelatedClampings().size() > 0) {
				//Als de size 1 is moeten we ook niet veel doen - removeRelated & putToActiveClamping
				Set<Clamping> newRelatedClampingSet = new HashSet<Clamping>();
				Clamping toBeActiveClamping = null;
				for(Clamping relClamping: activeClamping.getRelatedClampings()) {
					if(toBeActiveClamping == null) {
						toBeActiveClamping = relClamping;
					} else {
						newRelatedClampingSet.add(relClamping);
					}
				}
				toBeActiveClamping.setRelatedClampings(newRelatedClampingSet);
				//reset the related clampings of the previous active clamping
				activeClamping.setRelatedClampings(new HashSet<Clamping>());
				logger.debug("Active clamping " + activeClamping.getName() + " changed to " + toBeActiveClamping.getName());
				setClamping(toBeActiveClamping);
				getView().setDefaultClampingText(toBeActiveClamping.getName(), true);
			} else {
				//Should not occur, because the request to remove the activeClamping without there being a replacement is stopped before calling this function
				throw new IllegalArgumentException("Tried to remove the active clamping without there being a replacement clamping.");
			}
		} else {
			logger.debug("Related clamping " + clamping.getName() + " removed.");
			activeClamping.removeRelatedClamping(clamping);
		}	
	}
	
	/**
	 * Check that, in case of a clamping that needs to be removed, the active clamping is selected. If so, 
	 * we will return false in case there are no related clampings that can act as active after the removal 
	 * 
	 * @param clamping
	 * @param isToBeRemoved
	 * @return
	 */
	public boolean canClampingBeModified(Clamping clamping, boolean isToBeRemoved) {
		//In case we want to add the clamping, there is no issue
		if(isToBeRemoved) {
			DeviceSettings settings = deviceInfo.getDeviceSettings();
			Clamping activeClamping = settings.getDefaultClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea());
			if(activeClamping.equals(clamping)) {
				//There is no other clamp that can take the role of defaultClamping
				if(activeClamping.getRelatedClampings().size() == 0) {
					return false;
				}
			}
			return true;
		} 
		return true;
	}
	
	private void setClamping(final Clamping clamping) {
		DeviceSettings settings = deviceInfo.getDeviceSettings();		
		settings.setDefaultClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea(), clamping);		
		//sets the active clamping
		deviceInfo.getDevice().loadDeviceSettings(settings);
		(deviceInfo.getPickStep().getDevice().getDeviceSettings()).setDefaultClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea(), clamping);
		(deviceInfo.getPutStep().getDevice().getDeviceSettings()).setDefaultClamping(deviceInfo.getPutStep().getDeviceSettings().getWorkArea(), clamping);
		deviceInfo.getPutStep().setRelativeTeachedOffset(null);
		deviceInfo.getPickStep().setRelativeTeachedOffset(null);
	}
	
	private void addProcessFlowEvent() {
		deviceInfo.getPutStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPutStep().getProcessFlow(), deviceInfo.getPutStep(), true));
		deviceInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(deviceInfo.getPickStep().getProcessFlow(), deviceInfo.getPickStep(), true));
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

	private void setWorkArea(final SimpleWorkArea workArea) {
		deviceInfo.getPickStep().getDeviceSettings().setWorkArea(workArea);
		deviceInfo.getPickStep().getRobotSettings().setWorkArea(workArea);
		deviceInfo.getPutStep().getDeviceSettings().setWorkArea(workArea);
		deviceInfo.getPutStep().getRobotSettings().setWorkArea(workArea);
		deviceInfo.getPickStep().getRobotSettings().clearAirblowSettings();
		deviceInfo.getPutStep().getRobotSettings().clearAirblowSettings();
		deviceInfo.getProcessingStep().getDeviceSettings().setWorkArea(workArea);
		if (deviceInfo.hasInterventionStepAfterPut()) {
			deviceInfo.getInterventionStepAfterPut().getDeviceSettings().setWorkArea(workArea);
		}
		if (deviceInfo.hasInterventionStepBeforePick()) {
			deviceInfo.getInterventionStepBeforePick().getDeviceSettings().setWorkArea(workArea);
		}
	}
	
	private boolean correctNbOfActiveClampingsChoosen() {
		DevicePickSettings pickSettings = deviceInfo.getPickStep().getDeviceSettings();
		// All chosen fixture types must be of the same family. It is thus not possible to have an active fixture 1 + 2 together with
		// a fixture 3.
		int fixtureTypeAmount = pickSettings.getWorkArea().getDefaultClamping().getFixtureType().nbFixtures();
		for(Clamping clamping: pickSettings.getWorkArea().getDefaultClamping().getRelatedClampings()) {
			if(fixtureTypeAmount != clamping.getFixtureType().nbFixtures()) {
				getView().showNotification(Translator.getTranslation(DIFFERENT_FAMILY), eu.robojob.millassist.ui.general.NotificationBox.Type.WARNING);
				return false;
			}
		}
		// All chosen fixture types must be different from each other. It is thus not possible to have two active fixtures both of type fixture 1.
		//TODO - in case of same type: deselect one and select other
		for(Clamping clamping1: pickSettings.getWorkArea().getAllActiveClampings()) {
			for(Clamping clamping2: pickSettings.getWorkArea().getAllActiveClampings()) {
				if(!clamping1.equals(clamping2) && clamping1.getFixtureType().equals(clamping2.getFixtureType())) {
					getView().showNotification(Translator.getTranslation(SAME_TYPE), eu.robojob.millassist.ui.general.NotificationBox.Type.WARNING);
					return false;
				}
			}
		}
		return true;
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
				&& (pickSettings.getWorkArea().getDefaultClamping() != null)
				&& (deviceSettings.getDefaultClamping(pickSettings.getWorkArea()) != null)
				&& (deviceSettings.getDefaultClamping(pickSettings.getWorkArea()).equals(pickSettings.getWorkArea().getDefaultClamping()))
				&& (startCyclusSettings.getWorkArea() != null)
				&& (startCyclusSettings.getWorkArea().getDefaultClamping() != null)
				&& (putSettings.getWorkArea() != null)
				&& (robotPutSettings.getWorkArea() != null)
				&& (putSettings.getWorkArea().equals(robotPutSettings.getWorkArea()))
				&& (putSettings.getWorkArea().getDefaultClamping() != null)
				&& (deviceSettings.getDefaultClamping(putSettings.getWorkArea()).equals(putSettings.getWorkArea().getDefaultClamping()))
				&& (correctNbOfActiveClampingsChoosen())		
			)  {
			return true;
		}
		return false;
	}
	
	List<String> getListOfWorkAreas() {
		List<String> waList = new ArrayList<String>();
		for (SimpleWorkArea workArea: deviceInfo.getDevice().getWorkAreas()) {
			if (workArea.getSequenceNb() == deviceInfo.getCNCNbInFlow()) {
				waList.add(workArea.getWorkAreaManager().getName());
			}
		}
		return waList;
	}
}
