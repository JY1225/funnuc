	package eu.robojob.irscw.ui.configure;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.pre.PrageDevice;
import eu.robojob.irscw.external.device.pre.PrageDevice.PrageDevicePickSettings;
import eu.robojob.irscw.external.device.pre.PrageDevice.PrageDevicePutSettings;
import eu.robojob.irscw.external.device.pre.PrageDevice.PrageDeviceStartCyclusSettings;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPickSettings;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPutSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutAndWaitStep;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.configure.device.DeviceMenuFactory;
import eu.robojob.irscw.ui.configure.flow.ConfigureProcessFlowPresenter;
import eu.robojob.irscw.ui.configure.process.ProcessMenuPresenter;
import eu.robojob.irscw.ui.configure.transport.TransportMenuFactory;
import eu.robojob.irscw.ui.controls.AbstractTextField;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class ConfigurePresenter implements TextFieldListener, MainContentPresenter {

	public enum Mode {
		NORMAL, ADD_DEVICE, REMOVE_DEVICE
	}

	private static Logger logger = Logger.getLogger(ConfigurePresenter.class);
		
	private ConfigureView view;
	
	private KeyboardPresenter keyboardPresenter;
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
	
	public ConfigurePresenter(ConfigureView view, KeyboardPresenter keyboardPresenter, NumericKeyboardPresenter numericKeyboardPresenter,
			ConfigureProcessFlowPresenter processFlowPresenter, ProcessMenuPresenter processMenuPresenter, DeviceMenuFactory deviceMenuFactory, TransportMenuFactory transportMenuFactory,
			 DeviceManager deviceManager) {
		this.view = view;
		this.keyboardPresenter = keyboardPresenter;
		keyboardPresenter.setParent(this);
		this.numericKeyboardPresenter = numericKeyboardPresenter;
		numericKeyboardPresenter.setParent(this);
		this.processFlowPresenter = processFlowPresenter;
		processFlowPresenter.setParent(this);
		this.deviceMenuFactory = deviceMenuFactory;
		this.transportMenuFactory = transportMenuFactory;
		this.processMenuPresenter = processMenuPresenter;
		processMenuPresenter.setParent(this);
		view.setPresenter(this);
		keyboardActive = false;
		numericKeyboardActive = false;
		mode = Mode.NORMAL;
		view.setTop(processFlowPresenter.getView());
		this.deviceManager = deviceManager;
		configureProcess();
	}
	
	public void setParent(MainPresenter parent) {
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
			logger.debug("Close keyboard");
			// we assume the keyboard view is always on top
			view.removeNodeFromTop(keyboardPresenter.getView());
			view.requestFocus();
		} else if (numericKeyboardActive) {
			numericKeyboardActive = false;
			logger.debug("Close numeric keyboard");
			view.removeNodeFromBottomLeft(numericKeyboardPresenter.getView());
			view.requestFocus();
		}
	}
	
	public void setBottomRightView(AbstractFormView<?> bottomRight) {
		bottomRight.refresh();
		view.setBottomRight(bottomRight);
	}

	public void textFieldFocussed(AbstractTextField<?> textField) {
		if (textField instanceof FullTextField) {
			this.textFieldFocussed((FullTextField) textField);
		} else if (textField instanceof NumericTextField) {
			this.textFieldFocussed((NumericTextField) textField);
		} else if (textField instanceof IntegerTextField) {
			this.textFieldFocussed((IntegerTextField) textField);
		} else {
			throw new IllegalArgumentException("Unknown keyboard-type");
		}
	}
	
	private void textFieldFocussed(FullTextField textField) {
		keyboardPresenter.setTarget(textField);
		if (!keyboardActive) {
			view.addNodeToTop(keyboardPresenter.getView());
			keyboardActive = true;
		}
	}
	
	private void textFieldFocussed(NumericTextField textField) {
		numericKeyboardPresenter.setTarget(textField);
		if (!numericKeyboardActive) {
			logger.debug("Opening numeric keyboard");
			view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
			numericKeyboardActive = true;
		}
	}
	
	private void textFieldFocussed(IntegerTextField textField) {
		numericKeyboardPresenter.setTarget(textField);
		if (!numericKeyboardActive) {
			logger.debug("Opening numeric keyboard");
			view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
			numericKeyboardActive = true;
		}
	}

	@Override
	public void textFieldLostFocus(AbstractTextField<?> textField) {
		closeKeyboard();
	}
	
	public void configureDevice(int index) {
		activeMenu = deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(index));
		if (activeMenu != null) {
			activeMenu.setParent(this);
			activeMenu.setTextFieldListener(this);
			view.setBottomLeft(activeMenu.getView());
			activeMenu.openFirst();
		} else {
			processFlowPresenter.removeFocus();
		}
	}
	
	public void configureTransport(int index) {
		activeMenu = transportMenuFactory.getTransportMenu(processFlowAdapter.getTransportInformation(index));
		activeMenu.setParent(this);
		activeMenu.setTextFieldListener(this);
		view.setBottomLeft(activeMenu.getView());
		activeMenu.openFirst();
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		processFlowAdapter = new ProcessFlowAdapter(processFlow);
		processFlowPresenter.loadProcessFlow(processFlow);
		refreshProgressBar();
	}
	
	public void updateProcessFlow() {
		processFlowPresenter.refresh();
		refreshProgressBar();
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
	
	public void addDevice(int index) {
		
		PrageDevice prageDevice = (PrageDevice) deviceManager.getPreProcessingDeviceById("Präge");
		DeviceInformation deviceInfo = processFlowAdapter.getDeviceInformation(index);
		
		PrageDevicePickSettings pragePickSettings = new PrageDevice.PrageDevicePickSettings(prageDevice.getWorkAreaById("Präge"));
		PrageDeviceStartCyclusSettings prageStartCyclusSettings = new PrageDevice.PrageDeviceStartCyclusSettings(prageDevice.getWorkAreaById("Präge"));
		PrageDevicePutSettings pragePutSettings = new PrageDevice.PrageDevicePutSettings(prageDevice.getWorkAreaById("Präge"));
		
		FanucRobotPutSettings robotPutSettings = new FanucRobot.FanucRobotPutSettings();
		robotPutSettings.setGripperHead(deviceInfo.getPickStep().getRobot().getGripperBody().getGripperHead("A"));
		robotPutSettings.setGripper(deviceInfo.getPickStep().getRobot().getGripperBody().getGripper("2P clamp grip"));
		robotPutSettings.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaById("Präge").getClampingById("Clamping 5").getSmoothToPoint()));
		robotPutSettings.setClamping(prageDevice.getWorkAreaById("Präge").getClampingById("Clamping 5"));
		robotPutSettings.setWorkArea(prageDevice.getWorkAreaById("Präge"));
		robotPutSettings.setDoMachineAirblow(false);	
		
		FanucRobotPickSettings robotPickSettings = new FanucRobot.FanucRobotPickSettings();
		robotPickSettings.setGripperHead(deviceInfo.getPickStep().getRobot().getGripperBody().getGripperHead("A"));
		robotPickSettings.setGripper(deviceInfo.getPickStep().getRobot().getGripperBody().getGripper("2P clamp grip"));
		robotPickSettings.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaById("Präge").getClampingById("Clamping 5").getSmoothFromPoint()));
		robotPickSettings.setWorkArea(prageDevice.getWorkAreaById("Präge"));
		robotPickSettings.setClamping(prageDevice.getWorkAreaById("Präge").getActiveClamping());
		WorkPieceDimensions dimensions1 = new WorkPieceDimensions(125.8f, 64.9f, 40);
		WorkPiece workPiece1 = new WorkPiece(WorkPiece.Type.RAW, dimensions1);
		robotPickSettings.setWorkPiece(workPiece1);
		
		PutAndWaitStep putAndWait1 = new PutAndWaitStep(deviceInfo.getPickStep().getRobot(), prageDevice, pragePutSettings, robotPutSettings);
		ProcessingStep processing2 = new ProcessingStep(prageDevice, prageStartCyclusSettings);
		PickAfterWaitStep pickAfterWait1 = new PickAfterWaitStep(deviceInfo.getPickStep().getRobot(), prageDevice, pragePickSettings, robotPickSettings);
		
		DeviceInformation newDeviceInfo = new DeviceInformation((index + 1), processFlowAdapter);
		newDeviceInfo.setPickStep(pickAfterWait1);
		newDeviceInfo.setPutStep(putAndWait1);
		newDeviceInfo.setProcessingStep(processing2);
		
		processFlowAdapter.addDeviceSteps(index, newDeviceInfo);
		refresh();
	}
	
	public void removeDevice(int index) {
		processFlowAdapter.removeDeviceSteps(index);
		refresh();
	}
	
	public void refresh() {
		processMenuPresenter.setNormalMode();
		processFlowPresenter.refresh();
		refreshProgressBar();
	}
	
	public boolean isConfigured() {
		boolean configured = true;
		
		if (processFlowAdapter != null) {
			for (int i = 0; i < processFlowAdapter.getDeviceStepCount(); i++) {
				if ((deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(i)) != null) && (deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(i)).isConfigured())){
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
	
	public void refreshProgressBar() {
		if (parent != null) {
			parent.refreshStatus();
		} else {
			isConfigured();
		}
	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub
		
	}

}
