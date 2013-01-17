package eu.robojob.irscw.ui.automate;

import java.util.Set;

import javafx.scene.Node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowTimer;
import eu.robojob.irscw.process.execution.AutomateOptimizedThread;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.general.ExecutionPresenter;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.ui.general.status.DisconnectedDevicesView;
import eu.robojob.irscw.ui.general.status.StatusPresenter;

public class AutomatePresenter extends ExecutionPresenter {
	
	private static final String PROCESS_FINISHED = "AutomatePresenter.processFinished";
	private static Logger logger = LogManager.getLogger(AutomatePresenter.class.getName());
	
	private AutomateView view;
	private ProcessFlowTimer processFlowTimer;
	private TimingView timingView;
	private MainPresenter parent;
	
	private AutomateOptimizedThread automateThread;
	
	public AutomatePresenter(final AutomateView view, final FixedProcessFlowPresenter processFlowPresenter, final ProcessFlow processFlow, 
			final ProcessFlowTimer processFlowTimer, final TimingView timingView, 
			final StatusPresenter statusPresenter) {
		super(processFlowPresenter, processFlow, statusPresenter);
		this.view = view;
		view.setProcessFlowView(processFlowPresenter.getView());
		this.processFlowTimer = processFlowTimer;
		this.timingView = timingView;
		view.setTimingView(timingView);
		statusPresenter.initializeView();
		view.setStatusView(statusPresenter.getView());
		view.build();
		view.setTotalAmount(processFlow.getTotalAmount());
		view.setFinishedAmount(processFlow.getFinishedAmount());
		this.automateThread = new AutomateOptimizedThread(processFlow);
	}
	
	public void setParent(final MainPresenter parent) {
		this.parent = parent;
	}
	
	public int getMainProcessFlowId() {
		return automateThread.getMainProcessFlowId();
	}
	
	public void setTimers(final String cycleTime, final String timeInCycle, final String timeTillIntervention, final String timeTillFinished) {
		timingView.setCycleTime(cycleTime);
		timingView.setTimeInCycle(timeInCycle);
		timingView.setTimeTillIntervention(timeTillIntervention);
		timingView.setTimeTillFinished(timeTillFinished);
	}

	@Override
	public Node getView() {
		return view;
	}

	@Override
	public void stopRunning() {
		automateThread.interrupt();
		view.activeStopButton();
	}
	
	public void startAutomate() {
		ThreadManager.submit(automateThread);
		view.activeStopButton();
	}

	@Override
	public void allConnected() {
		if (!isRunning()) {
		}
	}

	@Override
	public void disconnectedDevices(final Set<String> disconnectedDevices) {
		//TODO show disconnected devices view!
	}

	@Override
	public boolean isRunning() {
		if ((automateThread != null) && (automateThread.isRunning())) {
			return true;
		}
		return false;
	}
}
