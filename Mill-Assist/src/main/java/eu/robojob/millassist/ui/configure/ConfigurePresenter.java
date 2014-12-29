	package eu.robojob.millassist.ui.configure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.TextInputControl;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnitSettings;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.external.robot.RobotProcessingWhileWaitingSettings;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.external.robot.fanuc.FanucRobotPickSettings;
import eu.robojob.millassist.external.robot.fanuc.FanucRobotPutSettings;
import eu.robojob.millassist.process.PickAfterWaitStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessingStep;
import eu.robojob.millassist.process.ProcessingWhileWaitingStep;
import eu.robojob.millassist.process.PutAndWaitStep;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.ui.MainPresenter;
import eu.robojob.millassist.ui.configure.device.DeviceMenuFactory;
import eu.robojob.millassist.ui.configure.flow.ConfigureProcessFlowPresenter;
import eu.robojob.millassist.ui.configure.process.ProcessMenuPresenter;
import eu.robojob.millassist.ui.configure.transport.TransportMenuFactory;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.controls.keyboard.FullKeyboardPresenter;
import eu.robojob.millassist.ui.controls.keyboard.NumericKeyboardPresenter;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.MainContentPresenter;
import eu.robojob.millassist.ui.general.MainContentView;
import eu.robojob.millassist.ui.general.model.DeviceInformation;
import eu.robojob.millassist.ui.general.model.ProcessFlowAdapter;
import eu.robojob.millassist.workpiece.WorkPiece;

public class ConfigurePresenter implements TextInputControlListener, MainContentPresenter {

	public enum Mode {
		NORMAL, ADD_DEVICE, REMOVE_DEVICE
	}
		
	private MainContentView view;
	private FullKeyboardPresenter keyboardPresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	private ConfigureProcessFlowPresenter processFlowPresenter;
	private AbstractMenuPresenter<?> activeMenu;
	private DeviceMenuFactory deviceMenuFactory;
	private TransportMenuFactory transportMenuFactory;
	private boolean keyboardActive;
	private boolean numericKeyboardActive;
	private ProcessFlowAdapter processFlowAdapter;
	private MainPresenter parent;
	private ProcessMenuPresenter processMenuPresenter;
	private DeviceManager deviceManager;
	private Mode mode;
	
	private static Logger logger = LogManager.getLogger(ConfigurePresenter.class.getName());
	
	public ConfigurePresenter(final MainContentView view, final FullKeyboardPresenter keyboardPresenter, final NumericKeyboardPresenter numericKeyboardPresenter,
			final ConfigureProcessFlowPresenter processFlowPresenter, final ProcessMenuPresenter processMenuPresenter, final DeviceMenuFactory deviceMenuFactory, 
			final TransportMenuFactory transportMenuFactory, final DeviceManager deviceManager) {
		this.view = view;
		this.keyboardPresenter = keyboardPresenter;
		keyboardPresenter.setParent(this);
		this.numericKeyboardPresenter = numericKeyboardPresenter;
		numericKeyboardPresenter.setParent(this);
		this.processFlowPresenter = processFlowPresenter;
		processFlowPresenter.setParent(this);
		this.processMenuPresenter = processMenuPresenter;
		processMenuPresenter.setParent(this);
		this.deviceMenuFactory = deviceMenuFactory;
		this.transportMenuFactory = transportMenuFactory;
		keyboardActive = false;
		numericKeyboardActive = false;
		mode = Mode.NORMAL;
		view.setTop(processFlowPresenter.getView());
		this.deviceManager = deviceManager;
		configureProcess();
	}
	
	public ConfigureProcessFlowPresenter getProcessFlowPresenter() {
		return processFlowPresenter;
	}
	
	public void configureProcess() {
		view.setBottomLeft(processMenuPresenter.getView());
		if (keyboardActive) {
			view.addNodeToTop(keyboardPresenter.getView()); 
		}
		if (numericKeyboardActive) {
			view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
		}
		processMenuPresenter.setTextFieldListener(this);
		processMenuPresenter.openFirst();
		refreshProgressBar();
	}
	
	private void refreshProgressBar() {
		if (parent != null) {
			parent.refreshStatus();
		} else {
			isConfigured();
		}
	}
	
	public boolean isConfigured() {
		boolean configured = true;
		if (processFlowAdapter != null) {
			processFlowPresenter.refresh();
			for (int i = 0; i < processFlowAdapter.getDeviceStepCount(); i++) {
				AbstractMenuPresenter<?> menu = deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(i));
				if ((menu != null) && (menu.isConfigured())) {
					processFlowPresenter.setDeviceConfigured(i, true);
				} else {
					if (deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(i)) != null) {
						processFlowPresenter.setDeviceConfigured(i, false);
						configured = false;
					} else {
						processFlowPresenter.setDeviceConfigured(i, true);
					}
				}
			}
			for (int j = 0; j < processFlowAdapter.getTransportStepCount(); j++) {
				if ((transportMenuFactory.getTransportMenu(processFlowAdapter.getTransportInformation(j)) != null) && (transportMenuFactory.getTransportMenu(processFlowAdapter.getTransportInformation(j)).isConfigured())) {
					processFlowPresenter.setTransportConfigured(j, true);
				} else {
					processFlowPresenter.setTransportConfigured(j, false);
					configured = false;
				}
			}
			if (processFlowAdapter.getProcessFlow().getName() == null || processFlowAdapter.getProcessFlow().getName().equals("")) {
				configured = false;
			}
		} else {
			configured = false;
		}
		return configured;
	}
	
	@Override
	public void setParent(final MainPresenter parent) {
		this.parent = parent;
	}
	
	@Override
	public MainPresenter getParent() {
		return parent;
	}
	
	@Override
	public MainContentView getView() {
		return view;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	@Override
	public synchronized void closeKeyboard() {
		if (keyboardActive && numericKeyboardActive) {
			throw new IllegalStateException("Multiple keyboards are active!");
		}
		if (keyboardActive) {
			keyboardActive = false;
			// we assume the keyboard view is always on top
			getView().removeNodeFromTop(keyboardPresenter.getView());
			getView().requestFocus();
		} else if (numericKeyboardActive) {
			numericKeyboardActive = false;
			// we assume the keyboard view is always on top
			getView().removeNodeFromBottomLeft(numericKeyboardPresenter.getView());
			getView().requestFocus();
		}
	}
	
	public void setBottomRightView(final AbstractFormView<?> bottomRight) {
		bottomRight.refresh();
		view.setBottomRight(bottomRight);
	}
	
	public void setBottomRightViewNoRefresh(final AbstractFormView<?> bottomRight) {
		view.setBottomRight(bottomRight);
	}

	public void textFieldFocussed(final TextInputControl textInputControl) {
		if (textInputControl instanceof FullTextField) {
			this.textFieldFocussed((FullTextField) textInputControl);
		} else if (textInputControl instanceof NumericTextField) {
			this.textFieldFocussed((NumericTextField) textInputControl);
		} else if (textInputControl instanceof IntegerTextField) {
			this.textFieldFocussed((IntegerTextField) textInputControl);
		} else {
			throw new IllegalArgumentException("Unknown keyboard-type [" + textInputControl + "].");
		}
	}

	private void textFieldFocussed(final FullTextField textField) {
		keyboardPresenter.setTarget(textField);
		if (!keyboardActive) {
			view.addNodeToTop(keyboardPresenter.getView());
			keyboardActive = true;
		}
	}
	
	private void textFieldFocussed(final NumericTextField textField) {
		numericKeyboardPresenter.setTarget(textField);
		if (!numericKeyboardActive) {
			view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
			numericKeyboardActive = true;
		}
	}
	
	private void textFieldFocussed(final IntegerTextField textField) {
		numericKeyboardPresenter.setTarget(textField);
		if (!numericKeyboardActive) {
			view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
			numericKeyboardActive = true;
		}
	}

	@Override
	public void textFieldLostFocus(final TextInputControl textInputControl) {
		closeKeyboard();
	}
	
	public void configureDevice(final int index) {
		activeMenu = deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(index));
		activeMenu.setParent(this);
		activeMenu.setTextFieldListener(this);
		view.setBottomLeft(activeMenu.getView());
		activeMenu.openFirst();
	}
	
	public void configureTransport(final int index) {
		activeMenu = transportMenuFactory.getTransportMenu(processFlowAdapter.getTransportInformation(index));
		activeMenu.setParent(this);
		activeMenu.setTextFieldListener(this);
		view.setBottomLeft(activeMenu.getView());
		activeMenu.openFirst();
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		processFlowAdapter = new ProcessFlowAdapter(processFlow);
		processFlowPresenter.loadProcessFlow(processFlow);
		transportMenuFactory.clearBuffer();
		deviceMenuFactory.clearBuffer();
		refreshProgressBar();
	}
	
	public void updateProcessFlow() {
		processFlowPresenter.refresh();
		refreshProgressBar();
	}
	
	public void setAddDeviceMode() {
		view.setBottomLeftEnabled(false);
		parent.setChangeContentEnabled(false);
		boolean addPreProcessPossible = false;
		if (deviceManager.getPreProcessingDevices().size() > 0 && !processFlowAdapter.getProcessFlow().hasPrageDevice()) {
			addPreProcessPossible = true;
		}
		boolean addPostProcessPossible = false;
		if (deviceManager.getPostProcessingDevices().size() > 0) {
			addPostProcessPossible = true;
		}
		processFlowPresenter.setAddDeviceMode(addPreProcessPossible, addPostProcessPossible);
		mode = Mode.ADD_DEVICE;
	}
	
	public void setRemoveDeviceMode() {
		view.setBottomLeftEnabled(false);
		parent.setChangeContentEnabled(false);
		processFlowPresenter.setRemoveDeviceMode();
		mode = Mode.REMOVE_DEVICE;
	}
	
	public void setNormalMode() {
		view.setBottomLeftEnabled(true);
		parent.setChangeContentEnabled(true);
		processFlowPresenter.setNormalMode();
		mode = Mode.NORMAL;
	}
	
	/*
	 * Update the workpieces from the first CNC machine in a way that it generates HALF_FINISHED workpieces
	 */
	private static void updateCNCWorkPieces(DeviceInformation deviceInfo) {
		WorkPiece halfFinishedClone = deviceInfo.getPickStep().getRobotSettings().getWorkPiece().clone();
		halfFinishedClone.setType(WorkPiece.Type.HALF_FINISHED);
		deviceInfo.getPickStep().getRobotSettings().setWorkPiece(halfFinishedClone);
		deviceInfo.getPickStep().getDeviceSettings().setWorkPieceType(WorkPiece.Type.HALF_FINISHED);
		deviceInfo.getProcessingStep().getDeviceSettings().setWorkPieceType(WorkPiece.Type.HALF_FINISHED);
		deviceInfo.getPutStep().getDeviceSettings().setWorkPieceType(WorkPiece.Type.RAW);
	}
	
	// TODO - review (duplicate code)
	private void addCNCMachineCopy() throws IllegalArgumentException {
		AbstractCNCMachine cncMachine = deviceManager.getCNCMachines().iterator().next();
		int index = processFlowAdapter.getLastCNCMachineIndex();
		
		DeviceInformation deviceInfo = processFlowAdapter.getDeviceInformation(index);
		WorkArea workArea = null;
		Clamping clamping = null;
		int nbCNCMachine = processFlowAdapter.getNbCNCMachinesInFlow() + 1;
		if (cncMachine.getWorkAreas().size() >= 1) {
			for (WorkArea workA: cncMachine.getWorkAreas()) {
				if (!workA.inUse() && workA.getPrioIfCloned() == nbCNCMachine) {
					workArea = workA;
				}
			}
			if (workArea == null) {
				//Not enough workareas for steps in process
				throw new IllegalArgumentException("Device [" + cncMachine + "] does not contain workarea");
			}
			if (workArea.getClampings().size() >= 1) {
				clamping = workArea.getClampings().iterator().next();
				if (clamping == null) {
					throw new IllegalArgumentException("Device [" + cncMachine + "] with workarea [" + workArea + "] does not contain clamping");
				}
			}
		}
		
		//Clone nemen van robotPick and PutSettings
		DevicePickSettings devicePickSettings = cncMachine.getDefaultPickSettings(WorkPiece.Type.FINISHED);
		devicePickSettings.setWorkArea(workArea);
		
		ProcessingDeviceStartCyclusSettings deviceStartCyclusSettings = cncMachine.getDefaultStartCyclusSettings(WorkPiece.Type.FINISHED);
		deviceStartCyclusSettings.setWorkArea(workArea);

		//original raw workPiece
		DevicePutSettings devicePutSettings = cncMachine.getDefaultPutSettings(WorkPiece.Type.HALF_FINISHED);
		devicePutSettings.setWorkArea(workArea);
		
		DeviceSettings deviceSettings = cncMachine.getDeviceSettings();
		deviceSettings.setClamping(workArea, clamping);
		processFlowAdapter.getProcessFlow().setDeviceSettings(cncMachine, deviceSettings);
		cncMachine.loadDeviceSettings(deviceSettings);
		
		FanucRobotPutSettings robotPutSettings = new FanucRobotPutSettings();
		robotPutSettings.setRobot(deviceInfo.getPutStep().getRobotSettings().getRobot());
		robotPutSettings.setGripperHead(deviceInfo.getPutStep().getRobotSettings().getGripperHead());
		robotPutSettings.setSmoothPoint(null);
		robotPutSettings.setWorkArea(workArea);
		robotPutSettings.setDoMachineAirblow(false);
		
		RobotPickSettings robotPickSettings = new FanucRobotPickSettings();
		robotPickSettings.setRobot(deviceInfo.getPickStep().getRobotSettings().getRobot());
		robotPickSettings.setGripperHead(deviceInfo.getPickStep().getRobotSettings().getGripperHead());
		robotPickSettings.setSmoothPoint(null);
		robotPickSettings.setWorkArea(workArea);
		robotPickSettings.setWorkPiece(deviceInfo.getPickStep().getRobotSettings().getWorkPiece());
		
		DeviceInformation newDeviceInfo = new DeviceInformation((index + 1), processFlowAdapter);

		PutStep putStep = new PutStep(devicePutSettings, robotPutSettings);
		ProcessingStep processingStep = new ProcessingStep(deviceStartCyclusSettings);
		PickStep pickStep = new PickStep(devicePickSettings, robotPickSettings);
		
		newDeviceInfo.setPickStep(pickStep);
		newDeviceInfo.setPutStep(putStep);
		newDeviceInfo.setProcessingStep(processingStep);
		
		processFlowAdapter.addDeviceSteps(index, newDeviceInfo);
		
		updateCNCWorkPieces(deviceInfo);
	}
	
	//TODO - dit moet simpeler kunnen (spreiden over meerdere klassen?)
	public void addDevice(final int index) {
		
		AbstractProcessingDevice device = null;
		
		//TODO - get device (this only works if we have 1 pre-processing and 1 post-processing device)
		if (index < processFlowAdapter.getCNCMachineIndex()) {
			// pre-processing device
			if (deviceManager.getPreProcessingDevices().size() > 0) {
				device = deviceManager.getPreProcessingDevices().iterator().next();
			}
		} else {
			// post-processing device
			if (deviceManager.getPostProcessingDevices().size() > 0) {
				device = deviceManager.getPostProcessingDevices().iterator().next();
			}
		} 
		
		if (device == null) {
			//throw new IllegalArgumentException("Not possible: no device to use");
			return;
		}
		
		//TODO - dit mag ook niet standaard - parameter meegeven aan type device
		if (device.getType().equals(EDeviceGroup.POST_PROCESSING)) {
			try {
				addCNCMachineCopy();
			} catch (IllegalArgumentException e) {
				logger.error(e.getLocalizedMessage());
				return;
			}
		}
		
		//Get the information about the usage of the device currently at the given index.
		DeviceInformation deviceInfo = processFlowAdapter.getDeviceInformation(index);
		//Look for the workarea + clamping to be used by the new device
		WorkArea workArea = null;
		Clamping clamping = null;
		if (device.getWorkAreas().size() >= 1) {
			workArea = device.getWorkAreas().iterator().next();
			if (workArea == null) {
				throw new IllegalArgumentException("Device [" + device + "] does not contain workarea");
			}
			if (workArea.getClampings().size() >= 1) {
				clamping = workArea.getClampings().iterator().next();
				if (clamping == null) {
					throw new IllegalArgumentException("Device [" + device + "] with workarea [" + workArea + "] does not contain clamping");
				}
			}
		}
		
		//TODO - dit moet standaard een aparte methode worden
		// Create new devicePick/Put/StartCyclussettings and indicate that the workarea we just choose is the workarea to be used
		DevicePickSettings devicePickSettings = device.getDefaultPickSettings(WorkPiece.Type.RAW);
		devicePickSettings.setWorkArea(workArea);
		ProcessingDeviceStartCyclusSettings deviceStartCyclusSettings = device.getDefaultStartCyclusSettings(WorkPiece.Type.RAW);
		deviceStartCyclusSettings.setWorkArea(workArea);
		//TODO - can we give the priority of the workarea?
		DevicePutSettings devicePutSettings = device.getDefaultPutSettings(WorkPiece.Type.RAW);
		devicePutSettings.setWorkArea(workArea);
		
		// Create a new DeviceSettings object - unique for the new step
		DeviceSettings deviceSettings = device.getDeviceSettings();
		deviceSettings.setClamping(workArea, clamping);
		processFlowAdapter.getProcessFlow().setDeviceSettings(device, deviceSettings);
		//TODO - Wat doet dit exact?
		device.loadDeviceSettings(deviceSettings);
		
		//Set the current Pick settings as put settings 
		FanucRobotPutSettings robotPutSettings = new FanucRobotPutSettings();
		robotPutSettings.setRobot(deviceInfo.getPickStep().getRobotSettings().getRobot());
		robotPutSettings.setGripperHead(deviceInfo.getPickStep().getRobotSettings().getGripperHead());
		robotPutSettings.setSmoothPoint(null);
		robotPutSettings.setWorkArea(workArea);
		robotPutSettings.setDoMachineAirblow(false);
		
		RobotPickSettings robotPickSettings = new FanucRobotPickSettings();
		robotPickSettings.setRobot(deviceInfo.getPickStep().getRobotSettings().getRobot());
		robotPickSettings.setGripperHead(deviceInfo.getPickStep().getRobotSettings().getGripperHead());
		robotPickSettings.setSmoothPoint(null);
		robotPickSettings.setWorkArea(workArea);
		robotPickSettings.setWorkPiece(deviceInfo.getPickStep().getRobotSettings().getWorkPiece());
		
		//New deviceInformation for the new step
		DeviceInformation newDeviceInfo = new DeviceInformation((index + 1), processFlowAdapter);

		if (device instanceof PrageDevice) {
			devicePutSettings.setWorkPieceType(WorkPiece.Type.RAW);
			deviceStartCyclusSettings.setWorkPieceType(WorkPiece.Type.RAW);
			devicePickSettings.setWorkPieceType(WorkPiece.Type.RAW);
			
			RobotProcessingWhileWaitingSettings procSettings = new RobotProcessingWhileWaitingSettings(deviceInfo.getPickStep().getRobotSettings().getRobot(), workArea, deviceInfo.getPickStep().getRobotSettings().getGripperHead());		
			PutAndWaitStep putAndWait1 = new PutAndWaitStep(devicePutSettings, robotPutSettings);
			ProcessingWhileWaitingStep processing2 = new ProcessingWhileWaitingStep(deviceStartCyclusSettings, procSettings);
			PickAfterWaitStep pickAfterWait1 = new PickAfterWaitStep(devicePickSettings, robotPickSettings);			
			
			newDeviceInfo.setPickStep(pickAfterWait1);
			newDeviceInfo.setPutStep(putAndWait1);
			newDeviceInfo.setProcessingStep(processing2);	
			
		} else if (device instanceof ReversalUnit) {
			deviceSettings = (ReversalUnitSettings) deviceSettings;
			
//			robotPutSettings.setRobot(deviceInfo.getPutStep().getRobotSettings().getRobot());
			robotPickSettings.setGripperHead(deviceInfo.getPutStep().getRobotSettings().getGripperHead());
			PutStep putStep = new PutStep(devicePutSettings, robotPutSettings);
			ProcessingStep processingStep = new ProcessingStep(deviceStartCyclusSettings);
			PickStep pickStep = new PickStep(devicePickSettings, robotPickSettings);	
			
			pickStep.getRobotSettings().setApproachType(ApproachType.BOTTOM);

			devicePutSettings.setWorkPieceType(WorkPiece.Type.HALF_FINISHED);
			deviceStartCyclusSettings.setWorkPieceType(WorkPiece.Type.HALF_FINISHED);
			devicePickSettings.setWorkPieceType(WorkPiece.Type.HALF_FINISHED);

			newDeviceInfo.setPutStep(putStep);
			newDeviceInfo.setPickStep(pickStep);
			newDeviceInfo.setProcessingStep(processingStep);
		} else {
			PutStep putStep = new PutStep(devicePutSettings, robotPutSettings);
			ProcessingStep processingStep = new ProcessingStep(deviceStartCyclusSettings);
			PickStep pickStep = new PickStep(devicePickSettings, robotPickSettings);
			
			newDeviceInfo.setPickStep(pickStep);
			newDeviceInfo.setPutStep(putStep);
			newDeviceInfo.setProcessingStep(processingStep);
		}
		
		processFlowAdapter.addDeviceSteps(index, newDeviceInfo);

		deviceMenuFactory.clearBuffer();
		transportMenuFactory.clearBuffer();
		refresh();
	}
	
	public void removeDevice(final int index) {
		processFlowAdapter.removeDeviceSteps(index);
		//FIXME - potential problem if other post-device than reversal + cnc are possible
		if (index > processFlowAdapter.getCNCMachineIndex()) {
			// eerste CNC machine verwijderen - als we de laatste zouden verwijderen moeten we het werkstuk van de StackPlate ook aanpassen
			// nu zitten we wel met de tweede bewerkingsstap (stuk na ontladen bij de eerste CNC machine)
			//TODO - try to remove the last CNC machine
			updateCNCMachineWorkArea();
			processFlowAdapter.removeDeviceSteps(index-1);
			//TODO - do we still need the workPieceTypes now that we can check the priority of the workArea?
			processFlowAdapter.updateWorkPieceTypes();
		}		
		deviceMenuFactory.clearBuffer();
		transportMenuFactory.clearBuffer();
		refresh();
	}
	
	private void updateCNCMachineWorkArea() {
		processFlowAdapter.updateCNCMachineWorkArea();
	}
	
	public void refresh() {
		processMenuPresenter.setNormalMode();
		processFlowPresenter.refresh();
		refreshProgressBar();
	}
	
	public void refreshClearCache() {
		deviceMenuFactory.clearBuffer();
		transportMenuFactory.clearBuffer();
		refresh();
	}
	
	public void processOpened() {
		transportMenuFactory.clearBuffer();
		deviceMenuFactory.clearBuffer();
		processFlowPresenter.refresh();
		parent.refreshStatus();
	}

	@Override
	public void setActive(final boolean active) {
	}

}
