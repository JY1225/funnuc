package eu.robojob.irscw.ui.automate;

import java.util.Set;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.ProcessFlowTimer;
import eu.robojob.irscw.process.execution.fixed.AutomateFixedControllingThread;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.automate.device.DeviceMenuFactory;
import eu.robojob.irscw.ui.automate.flow.AutomateProcessFlowPresenter;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.controls.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.general.ExecutionPresenter;
import eu.robojob.irscw.ui.general.MainContentView;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;
import eu.robojob.irscw.ui.general.status.DisconnectedDevicesView;

public class AutomatePresenter extends ExecutionPresenter implements TextInputControlListener {
	
	private MainContentView view;
	private ProcessFlowTimer processFlowTimer;
	private DisconnectedDevicesView disconnectedDevicesView;
	private AutomateStatusPresenter statusPresenter;
	private AutomateTimingThread automateTimingThread;
	private DeviceMenuFactory deviceMenuFactory;
	private AbstractMenuPresenter<?> activeMenu;
	private ProcessFlowAdapter processFlowAdapter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	private boolean numericKeyboardActive;
	private boolean running;
	
	private AutomateFixedControllingThread automateThread;
	
	public AutomatePresenter(final MainContentView view, final AutomateProcessFlowPresenter processFlowPresenter, final DisconnectedDevicesView disconnectedDevicesView,
			final ProcessFlow processFlow, final ProcessFlowTimer processFlowTimer, final AutomateStatusPresenter statusPresenter,
			final DeviceMenuFactory deviceMenuFactory, final NumericKeyboardPresenter numericKeyboardPresenter) {
		super(processFlowPresenter, processFlow, statusPresenter.getStatusPresenter());
		this.view = view;
		view.setTop(processFlowPresenter.getView());
		processFlowPresenter.setParent(this);
		this.disconnectedDevicesView = disconnectedDevicesView;
		this.statusPresenter = statusPresenter;
		statusPresenter.setParent(this);
		this.processFlowTimer = processFlowTimer;
		statusPresenter.setTotalAmount(processFlow.getTotalAmount());
		statusPresenter.setFinishedAmount(processFlow.getFinishedAmount());
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		this.running = false;
		automateThread = new AutomateFixedControllingThread(processFlow);
		this.deviceMenuFactory = deviceMenuFactory;
		this.numericKeyboardPresenter = numericKeyboardPresenter;
		numericKeyboardPresenter.setParent(this);
		this.numericKeyboardActive = false;
	}
	
	public int getMainProcessFlowId() {
		return automateThread.getMainProcessFlowId();
	}
	
	public void setTimers(final String cycleTime, final String timeInCycle, final String timeTillIntervention, final String timeTillFinished) {
		statusPresenter.setTimers(cycleTime, timeInCycle, timeTillIntervention, timeTillFinished);
	}

	@Override
	public Node getView() {
		return view;
	}

	@Override
	public void stopRunning() {
		running = false;
		if (automateThread.isRunning()) {
			automateThread.interrupt();
		} else {
			getProcessFlow().initialize();
			getProcessFlow().setMode(Mode.STOPPED);
		}
		//TODO reset devices
		automateThread.reset();
		statusPresenter.initializeView();
		statusPresenter.getView().activateStartButton();
	}
	
	public void startAutomate() {
		running = true;
		ThreadManager.submit(automateThread);
		statusPresenter.getView().activateStopButton();
	}
	
	public void continueAutomate() {
		running = true;
		ThreadManager.submit(automateThread);
		statusPresenter.getView().activateStopButton();
	}

	@Override
	public void allConnected() {
		if (!isRunning()) {
			view.setBottom(statusPresenter.getView());
			updateAlarms();
		}
	}

	@Override
	public void disconnectedDevices(final Set<String> disconnectedDevices) {
		if (!isRunning()) {
			disconnectedDevicesView.setDisconnectedDevices(disconnectedDevices);
			view.setBottom(disconnectedDevicesView);
		}
	}

	@Override
	public boolean isRunning() {
		if ((automateThread != null) && (automateThread.isRunning()) && running) {
			return true;
		}
		return false;
	}

	@Override
	public void startListening(final ProcessFlow processFlow) {
		statusPresenter.initializeView();
		automateTimingThread = new AutomateTimingThread(this, processFlowTimer);
		processFlow.addListener(statusPresenter);
		//TODO review: doesn't look so clean
		statusPresenter.setTotalAmount(processFlow.getTotalAmount());
		statusPresenter.setFinishedAmount(processFlow.getFinishedAmount());
		ThreadManager.submit(automateTimingThread);
	}

	@Override
	public void stopListening(final ProcessFlow processFlow) {
		processFlow.removeListener(statusPresenter);
		ThreadManager.stopRunning(automateTimingThread);
	}

	public boolean showDeviceMenu(final int deviceIndex) {
		activeMenu = deviceMenuFactory.getDeviceMenu(processFlowAdapter.getDeviceInformation(deviceIndex));
		if (activeMenu != null) {
			activeMenu.setParent(this);
			activeMenu.setTextFieldListener(this);
			activeMenu.openFirst();
			view.showBottomHBox();
			view.setBottomLeft(activeMenu.getView());
			return true;
		} else {
			return false;
		}
	}
	
	public void closeDeviceMenu() {
		view.hideBottomHBox();
	}
	
	public void setBottomRight(final Node node) {
		view.setBottomRight(node);
	}

	@Override
	public void closeKeyboard() {
		if (numericKeyboardActive) {
			view.removeNodeFromBottomLeft(numericKeyboardPresenter.getView());
			numericKeyboardActive = false;
			view.requestFocus();
		}
	}

	@Override
	public void textFieldFocussed(final TextInputControl textInputControl) {
		if (textInputControl instanceof IntegerTextField) {
			numericKeyboardPresenter.setTarget(textInputControl);
			if (!numericKeyboardActive) {
				view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
				numericKeyboardActive = true;
			}
		} else {
			throw new IllegalStateException("Expected to only contain integer textfields");
		}
	}

	@Override
	public void textFieldLostFocus(final TextInputControl textInputControl) {
		closeKeyboard();
	}
}
