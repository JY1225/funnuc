	package eu.robojob.irscw.ui.configure;

import javafx.scene.control.TextInputControl;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.external.device.processing.prage.PrageDevice;
import eu.robojob.irscw.external.robot.RobotPickSettings;
import eu.robojob.irscw.external.robot.RobotProcessingWhileWaitingSettings;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotPickSettings;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotPutSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.ProcessingWhileWaitingStep;
import eu.robojob.irscw.process.PutAndWaitStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.configure.device.DeviceMenuFactory;
import eu.robojob.irscw.ui.configure.flow.ConfigureProcessFlowPresenter;
import eu.robojob.irscw.ui.configure.process.ProcessMenuPresenter;
import eu.robojob.irscw.ui.configure.transport.TransportMenuFactory;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.controls.keyboard.FullKeyboardPresenter;
import eu.robojob.irscw.ui.controls.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.ui.general.MainContentPresenter;
import eu.robojob.irscw.ui.general.model.DeviceInformation;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;

public class ConfigurePresenter implements TextInputControlListener, MainContentPresenter {

	public enum Mode {
		NORMAL, ADD_DEVICE, REMOVE_DEVICE
	}
		
	private ConfigureView view;
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
	
	public ConfigurePresenter(final ConfigureView view, final FullKeyboardPresenter keyboardPresenter, final NumericKeyboardPresenter numericKeyboardPresenter,
			final ConfigureProcessFlowPresenter processFlowPresenter, final ProcessMenuPresenter processMenuPresenter, final DeviceMenuFactory deviceMenuFactory, 
			final TransportMenuFactory transportMenuFactory, final DeviceManager deviceManager) {
		this.view = view;
		view.setPresenter(this);
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
		} else {
			configured = false;
		}
		return configured;
	}
	
	public void setParent(final MainPresenter parent) {
		this.parent = parent;
	}
	
	@Override
	public ConfigureView getView() {
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
		if (deviceManager.getPreProcessingDevices().size() > 0) {
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
	
	//TODO review
	public void addDevice(final int index) {
		
		AbstractProcessingDevice device = null;
		
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
			throw new IllegalArgumentException("Not possible: no device to use");
		}
		
		DeviceInformation deviceInfo = processFlowAdapter.getDeviceInformation(index);
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
					throw new IllegalArgumentException("Device [" + device + "] with workarea [" + workArea + "] does not contain workarea");
				}
			}
		}
		
		DevicePickSettings devicePickSettings = device.getDefaultPickSettings();
		devicePickSettings.setWorkArea(workArea);
		ProcessingDeviceStartCyclusSettings deviceStartCyclusSettings = device.getDefaultStartCyclusSettings();
		deviceStartCyclusSettings.setWorkArea(workArea);
		DevicePutSettings devicePutSettings = device.getDefaultPutSettings();
		devicePutSettings.setWorkArea(workArea);
		DeviceSettings deviceSettings = device.getDeviceSettings();
		deviceSettings.setClamping(workArea, clamping);
		processFlowAdapter.getProcessFlow().setDeviceSettings(device, deviceSettings);
		device.loadDeviceSettings(deviceSettings);
		
		FanucRobotPutSettings robotPutSettings = new FanucRobotPutSettings();
		robotPutSettings.setRobot(deviceInfo.getPickStep().getRobotSettings().getRobot());
		robotPutSettings.setGripperHead(deviceInfo.getPickStep().getRobotSettings().getGripperHead());
		robotPutSettings.setSmoothPoint(new Coordinates(clamping.getSmoothToPoint()));
		robotPutSettings.setWorkArea(workArea);
		robotPutSettings.setDoMachineAirblow(false);	
		
		RobotPickSettings robotPickSettings = new FanucRobotPickSettings();
		robotPickSettings.setRobot(deviceInfo.getPickStep().getRobotSettings().getRobot());
		robotPickSettings.setGripperHead(deviceInfo.getPickStep().getRobotSettings().getGripperHead());
		robotPickSettings.setSmoothPoint(new Coordinates(clamping.getSmoothFromPoint()));
		robotPickSettings.setWorkArea(workArea);
		robotPickSettings.setWorkPiece(deviceInfo.getPickStep().getRobotSettings().getWorkPiece());
		
		DeviceInformation newDeviceInfo = new DeviceInformation((index + 1), processFlowAdapter);

		if (device instanceof PrageDevice) {
			RobotProcessingWhileWaitingSettings procSettings = new RobotProcessingWhileWaitingSettings(deviceInfo.getPickStep().getRobotSettings().getRobot(), workArea, deviceInfo.getPickStep().getRobotSettings().getGripperHead());		
			PutAndWaitStep putAndWait1 = new PutAndWaitStep(devicePutSettings, robotPutSettings);
			ProcessingWhileWaitingStep processing2 = new ProcessingWhileWaitingStep(deviceStartCyclusSettings, procSettings);
			PickAfterWaitStep pickAfterWait1 = new PickAfterWaitStep(devicePickSettings, robotPickSettings);
			
			newDeviceInfo.setPickStep(pickAfterWait1);
			newDeviceInfo.setPutStep(putAndWait1);
			newDeviceInfo.setProcessingStep(processing2);
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
		deviceMenuFactory.reset();
		refresh();
	}
	
	public void refresh() {
		processMenuPresenter.setNormalMode();
		processFlowPresenter.refresh();
		refreshProgressBar();
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
