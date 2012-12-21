	package eu.robojob.irscw.ui.configure;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.external.device.processing.prage.PrageDevice;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotPickSettings;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotPutSettings;
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
import eu.robojob.irscw.ui.controls.keyboard.FullKeyboardPresenter;
import eu.robojob.irscw.ui.controls.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.general.model.DeviceInformation;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;

public class ConfigurePresenter implements TextFieldListener, MainContentPresenter {

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
			view.removeNodeFromTop(keyboardPresenter.getView());
			view.requestFocus();
		} else if (numericKeyboardActive) {
			numericKeyboardActive = false;
			// we assume the keyboard view is always on top
			view.removeNodeFromBottomLeft(numericKeyboardPresenter.getView());
			view.requestFocus();
		}
	}
	
	public void setBottomRightView(final AbstractFormView<?> bottomRight) {
		bottomRight.refresh();
		view.setBottomRight(bottomRight);
	}

	public void textFieldFocussed(final AbstractTextField<?> textField) {
		if (textField instanceof FullTextField) {
			this.textFieldFocussed((FullTextField) textField);
		} else if (textField instanceof NumericTextField) {
			this.textFieldFocussed((NumericTextField) textField);
		} else if (textField instanceof IntegerTextField) {
			this.textFieldFocussed((IntegerTextField) textField);
		} else {
			throw new IllegalArgumentException("Unknown keyboard-type [" + textField + "].");
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
	public void textFieldLostFocus(final AbstractTextField<?> textField) {
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
	
	//TODO review, for now this is hard coded
	public void addDevice(final int index) {
		
		PrageDevice prageDevice = (PrageDevice) deviceManager.getPreProcessingDeviceById("Präge");
		DeviceInformation deviceInfo = processFlowAdapter.getDeviceInformation(index);
		
		DevicePickSettings pragePickSettings = new DevicePickSettings(prageDevice.getWorkAreaById("Präge"));
		ProcessingDeviceStartCyclusSettings prageStartCyclusSettings = new ProcessingDeviceStartCyclusSettings(prageDevice.getWorkAreaById("Präge"));
		DevicePutSettings pragePutSettings = new DevicePutSettings(prageDevice.getWorkAreaById("Präge"));
		
		FanucRobotPutSettings robotPutSettings = new FanucRobotPutSettings();
		robotPutSettings.setGripperHead(deviceInfo.getPickStep().getRobotSettings().getGripperHead());
		robotPutSettings.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaById("Präge").getActiveClamping().getSmoothToPoint()));
		robotPutSettings.setWorkArea(prageDevice.getWorkAreaById("Präge"));
		robotPutSettings.setDoMachineAirblow(false);	
		
		FanucRobotPickSettings robotPickSettings = new FanucRobotPickSettings();
		robotPickSettings.setGripperHead(deviceInfo.getPickStep().getRobotSettings().getGripperHead());
		robotPickSettings.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaById("Präge").getActiveClamping().getSmoothFromPoint()));
		robotPickSettings.setWorkArea(prageDevice.getWorkAreaById("Präge"));
		robotPickSettings.setWorkPiece(deviceInfo.getPickStep().getRobotSettings().getWorkPiece());
		
		PutAndWaitStep putAndWait1 = new PutAndWaitStep(deviceInfo.getPickStep().getRobot(), prageDevice, pragePutSettings, robotPutSettings);
		ProcessingStep processing2 = new ProcessingStep(prageDevice, prageStartCyclusSettings);
		PickAfterWaitStep pickAfterWait1 = new PickAfterWaitStep(deviceInfo.getPickStep().getRobot(), prageDevice, pragePickSettings, robotPickSettings);
		
		DeviceInformation newDeviceInfo = new DeviceInformation((index + 1), processFlowAdapter);
		newDeviceInfo.setPickStep(pickAfterWait1);
		newDeviceInfo.setPutStep(putAndWait1);
		newDeviceInfo.setProcessingStep(processing2);
		
		processFlowAdapter.addDeviceSteps(index, newDeviceInfo);
		
		deviceMenuFactory.reset();
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
