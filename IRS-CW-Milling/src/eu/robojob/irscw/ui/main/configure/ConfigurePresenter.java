package eu.robojob.irscw.ui.main.configure;

import javafx.scene.Node;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.controls.AbstractTextField;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.main.configure.device.DeviceMenuPresenter;
import eu.robojob.irscw.ui.main.configure.device.DeviceMenuView;
import eu.robojob.irscw.ui.main.configure.process.ProcessMenuPresenter;
import eu.robojob.irscw.ui.main.configure.transport.TransportMenuPresenter;
import eu.robojob.irscw.ui.main.configure.transport.TransportMenuView;
import eu.robojob.irscw.ui.main.flow.ProcessFlowPresenter;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;

public class ConfigurePresenter implements TextFieldListener {

	private static Logger logger = Logger.getLogger(ConfigurePresenter.class);
		
	private ConfigureView view;
	
	private KeyboardPresenter keyboardPresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	
	private ProcessFlowPresenter processFlowPresenter;
	private ProcessMenuPresenter processConfigurationMenuPresenter;
	
	private boolean keyboardActive;
	private boolean numericKeyboardActive;
	
	private ProcessFlow processFlow;
	private ProcessFlowAdapter processFlowAdapter;
	
	public ConfigurePresenter(ConfigureView view, KeyboardPresenter keyboardPresenter, NumericKeyboardPresenter numericKeyboardPresenter,
			ProcessFlowPresenter processFlowPresenter, ProcessMenuPresenter processConfigurationMenuPresenter) {
		this.view = view;
		this.keyboardPresenter = keyboardPresenter;
		keyboardPresenter.setParent(this);
		this.numericKeyboardPresenter = numericKeyboardPresenter;
		numericKeyboardPresenter.setParent(this);
		this.processFlowPresenter = processFlowPresenter;
		processFlowPresenter.setParent(this);
		this.processConfigurationMenuPresenter = processConfigurationMenuPresenter;
		processConfigurationMenuPresenter.setParent(this);
		view.setPresenter(this);
		showConfigureView();
		keyboardActive = false;
		numericKeyboardActive = false;
		processFlow = null;
	}
	
	public ConfigureView getView() {
		return view;
	}
	
	public void showAlarmsView() {
		
	}
	
	public void showConfigureView() {
		view.setTop(processFlowPresenter.getView());
		configureProcess();
	}
	
	public void showTeachView() {
		
	}
	
	public void showAutomateView() {
		
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
	
	public void setBottomRightView(Node bottomRight) {
		view.setBottomRight(bottomRight);
	}

	public void textFieldFocussed(AbstractTextField textField) {
		if (textField instanceof FullTextField) {
			this.textFieldFocussed((FullTextField) textField);
		} else if (textField instanceof NumericTextField) {
			this.textFieldFocussed((NumericTextField) textField);
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

	@Override
	public void textFieldLostFocus(AbstractTextField textField) {
		closeKeyboard();
	}
	
	public void configureDevice(int index) {
		DeviceMenuView deviceMenuView = new DeviceMenuView();
		DeviceMenuPresenter deviceMenuPresenter = new DeviceMenuPresenter(deviceMenuView, processFlowAdapter.getDeviceInformation(index));
		deviceMenuPresenter.setParent(this);
		view.setBottomLeft(deviceMenuPresenter.getView());
		deviceMenuPresenter.openFirst();
	}
	
	public void configureTransport(int index) {
		TransportMenuView transportMenuView = new TransportMenuView(processFlowAdapter.getTransportInformation(index));
		TransportMenuPresenter transportMenuPresenter = new TransportMenuPresenter(transportMenuView);
		transportMenuPresenter.setParent(this);
		view.setBottomLeft(transportMenuPresenter.getView());
		transportMenuPresenter.openFirst();
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		this.processFlow = processFlow;
		processFlowAdapter = new ProcessFlowAdapter(processFlow);
		processFlowPresenter.loadProcessFlow(processFlow);
	}
	
	public void configureProcess() {
		view.setBottomLeft(processConfigurationMenuPresenter.getView());
		if (keyboardActive) {
			view.addNodeToTop(keyboardPresenter.getView()); 
		}
		if (numericKeyboardActive) {
			view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
		}
		processConfigurationMenuPresenter.openFirst();
	}

}
