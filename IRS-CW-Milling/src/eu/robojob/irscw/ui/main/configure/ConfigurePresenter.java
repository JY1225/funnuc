	package eu.robojob.irscw.ui.main.configure;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.MenuBarPresenter;
import eu.robojob.irscw.ui.controls.AbstractTextField;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.main.configure.device.DeviceMenuFactory;
import eu.robojob.irscw.ui.main.configure.process.ProcessMenuPresenter;
import eu.robojob.irscw.ui.main.configure.transport.TransportMenuFactory;
import eu.robojob.irscw.ui.main.flow.ProcessFlowPresenter;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;

public class ConfigurePresenter implements TextFieldListener {

	public enum Mode {
		NORMAL, ADD_DEVICE, REMOVE_DEVICE
	}

	private static Logger logger = Logger.getLogger(ConfigurePresenter.class);
		
	private ConfigureView view;
	
	private KeyboardPresenter keyboardPresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	
	private ProcessFlowPresenter processFlowPresenter;

	private AbstractMenuPresenter activeMenu;
	private DeviceMenuFactory deviceMenuFactory;
	private TransportMenuFactory transportMenuFactory;
	
	private boolean keyboardActive;
	private boolean numericKeyboardActive;
	
	private ProcessFlow processFlow;
	private ProcessFlowAdapter processFlowAdapter;
	
	private MainPresenter parent;
	
	private ProcessMenuPresenter processMenuPresenter;
	
	private Mode mode;
	
	public ConfigurePresenter(ConfigureView view, KeyboardPresenter keyboardPresenter, NumericKeyboardPresenter numericKeyboardPresenter,
			ProcessFlowPresenter processFlowPresenter, ProcessMenuPresenter processMenuPresenter, DeviceMenuFactory deviceMenuFactory, TransportMenuFactory transportMenuFactory) {
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
		processFlow = null;
		mode = Mode.NORMAL;
		view.setTop(processFlowPresenter.getView());
		configureProcess();
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
	
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
	
	public void setBottomRightView(AbstractFormView bottomRight) {
		bottomRight.refresh();
		view.setBottomRight(bottomRight);
	}

	public void textFieldFocussed(AbstractTextField textField) {
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
	public void textFieldLostFocus(AbstractTextField textField) {
		closeKeyboard();
	}
	
	public void configureDevice(int index) {
		activeMenu = deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(index));
		activeMenu.setParent(this);
		activeMenu.setTextFieldListener(this);
		view.setBottomLeft(activeMenu.getView());
		activeMenu.openFirst();
	}
	
	public void configureTransport(int index) {
		activeMenu = transportMenuFactory.getTransportMenu(processFlowAdapter.getTransportInformation(index));
		activeMenu.setParent(this);
		activeMenu.setTextFieldListener(this);
		view.setBottomLeft(activeMenu.getView());
		activeMenu.openFirst();
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		this.processFlow = processFlow;
		processFlowAdapter = new ProcessFlowAdapter(processFlow);
		processFlowPresenter.loadProcessFlow(processFlow);
		refreshProgressBar();
	}
	
	public void updateProcessFlow() {
		processFlowPresenter.refresh();
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
		parent.setMenuBarEnabled(false);
		processFlowPresenter.setAddDeviceMode();
		mode = Mode.ADD_DEVICE;
	}
	
	public void setRemoveDeviceMode() {
		view.setBottomLeftEnabled(false);
		parent.setMenuBarEnabled(false);
		processFlowPresenter.setRemoveDeviceMode();
		mode = Mode.REMOVE_DEVICE;
	}
	
	public void setNormalMode() {
		view.setBottomLeftEnabled(true);
		parent.setMenuBarEnabled(true);
		processFlowPresenter.setNormalMode();
		mode = Mode.NORMAL;
	}
	
	public void addDevice(int index) {
		processFlowAdapter.addDeviceSteps(index);
		setNormalMode();
		processFlowPresenter.refresh();
		processMenuPresenter.setNormalMode();
	}
	
	public void removeDevice(int index) {
		processFlowAdapter.removeDeviceSteps(index);
		processFlowPresenter.refresh();
		setNormalMode();
		processMenuPresenter.setNormalMode();
	}
	
	public void refreshProgressBar() {
		if (processFlowAdapter != null) {
			for (int i = 0; i < processFlowAdapter.getDeviceStepCount(); i++) {
				refreshProgressBarDevice(i);
			}
			for (int j = 0; j < processFlowAdapter.getTransportStepCount(); j++) {
				refreshProgressBarTransport(j);
			}
		}
	}
	
	public void refreshProgressBarDevice(int deviceIndex) {
		if ((deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(deviceIndex)) != null) && (deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(deviceIndex)).isConfigured())) {
			processFlowPresenter.setDeviceProgressGreen(deviceIndex);
		} else {
			processFlowPresenter.setDeviceProgressNone(deviceIndex);
		}
	}
	
	public void refreshProgressBarTransport(int transportIndex) {
		if ((transportMenuFactory.getTransportMenu(processFlowAdapter.getTransportInformation(transportIndex)) != null) && (transportMenuFactory.getTransportMenu(processFlowAdapter.getTransportInformation(transportIndex)).isConfigured())) {
			processFlowPresenter.setTransportProgressGreen(transportIndex);
		} else {
			processFlowPresenter.setTransportProgressNone(transportIndex);
		}
	}

}
