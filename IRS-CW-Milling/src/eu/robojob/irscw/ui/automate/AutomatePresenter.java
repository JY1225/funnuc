package eu.robojob.irscw.ui.automate;

import java.util.Set;

import javafx.scene.Node;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowTimer;
import eu.robojob.irscw.process.execution.AutomateOptimizedThread;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainContentView;
import eu.robojob.irscw.ui.general.ExecutionPresenter;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.ui.general.status.DisconnectedDevicesView;

public class AutomatePresenter extends ExecutionPresenter {
	
	private MainContentView view;
	private ProcessFlowTimer processFlowTimer;
	private DisconnectedDevicesView disconnectedDevicesView;
	private AutomateStatusPresenter statusPresenter;
	private AutomateTimingThread automateTimingThread;
	
	private boolean running;
	
	private AutomateOptimizedThread automateThread;
	
	public AutomatePresenter(final MainContentView view, final FixedProcessFlowPresenter processFlowPresenter, final DisconnectedDevicesView disconnectedDevicesView,
			final ProcessFlow processFlow, final ProcessFlowTimer processFlowTimer, final AutomateStatusPresenter statusPresenter) {
		super(processFlowPresenter, processFlow, statusPresenter.getStatusPresenter());
		this.view = view;
		view.setTop(processFlowPresenter.getView());
		this.disconnectedDevicesView = disconnectedDevicesView;
		this.statusPresenter = statusPresenter;
		statusPresenter.setParent(this);
		this.processFlowTimer = processFlowTimer;
		statusPresenter.setTotalAmount(processFlow.getTotalAmount());
		statusPresenter.setFinishedAmount(processFlow.getFinishedAmount());
		this.running = false;
		//view.setBottom(statusPresenter.getView());
		automateThread = new AutomateOptimizedThread(processFlow);
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
		automateThread.interrupt();
		statusPresenter.initializeView();
		statusPresenter.getView().activateStartButton();
	}
	
	public void startAutomate() {
		running = true;
		automateThread = new AutomateOptimizedThread(getProcessFlow());
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
}
