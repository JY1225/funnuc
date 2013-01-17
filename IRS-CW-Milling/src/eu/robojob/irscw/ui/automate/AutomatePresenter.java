package eu.robojob.irscw.ui.automate;

import javafx.scene.Node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowTimer;
import eu.robojob.irscw.process.execution.AutomateOptimizedThread;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.ui.general.status.StatusPresenter;

public class AutomatePresenter implements MainContentPresenter {
	
	private static final String PROCESS_FINISHED = "AutomatePresenter.processFinished";
	private static Logger logger = LogManager.getLogger(AutomatePresenter.class.getName());
	
	private AutomateView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	private ProcessFlow processFlow;
	private ProcessFlowTimer processFlowTimer;
	private TimingView timingView;
	private StatusPresenter statusPresenter;
	private MainPresenter parent;
	
	private AutomateOptimizedThread automateThread;
	
	public AutomatePresenter(final AutomateView view, final FixedProcessFlowPresenter processFlowPresenter, final ProcessFlow processFlow, 
			final ProcessFlowTimer processFlowTimer, final TimingView timingView, final StatusPresenter statusPresenter) {
		this.view = view;
		this.processFlowPresenter = processFlowPresenter;
		view.setProcessFlowView(processFlowPresenter.getView());
		this.processFlow = processFlow;
		this.processFlowTimer = processFlowTimer;
		this.timingView = timingView;
		view.setTimingView(timingView);
		this.statusPresenter = statusPresenter;
		statusPresenter.initializeView();
		view.setStatusView(statusPresenter.getView());
		view.build();
		view.setTotalAmount(processFlow.getTotalAmount());
		//view.setFinishedAmount(processFlow.getFinishedAmount());
		view.setFinishedAmount(3);
	}
	
	public void setParent(final MainPresenter parent) {
		this.parent = parent;
	}
	
	@Override
	public void setActive(final boolean active) {
		//FIXME implement
	}
	
	public void stopExecution() {
		//FIXME implement
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
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
}
