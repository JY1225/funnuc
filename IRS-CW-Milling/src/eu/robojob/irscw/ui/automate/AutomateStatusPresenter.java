package eu.robojob.irscw.ui.automate;

import javafx.application.Platform;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.ui.general.status.StatusPresenter;

public class AutomateStatusPresenter implements ProcessFlowListener {

	private AutomateStatusView view;
	private AutomatePresenter parent;
	private TimingView timingView;
	private StatusPresenter statusPresenter;
	
	public AutomateStatusPresenter(final AutomateStatusView view, final StatusPresenter statusPresenter, final TimingView timingView) {
		this.view = view;
		this.statusPresenter = statusPresenter;
		this.timingView = timingView;
		view.setPresenter(this);
		view.setStatusView(statusPresenter.getView());
		view.setTimingView(timingView);
		view.build();
	}
	
	public void setTimers(final String cycleTime, final String timeInCycle, final String timeTillIntervention, final String timeTillFinished) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				timingView.setCycleTime(cycleTime);
				timingView.setTimeInCycle(timeInCycle);
				timingView.setTimeTillIntervention(timeTillIntervention);
				timingView.setTimeTillFinished(timeTillFinished);
			}
		});
	}
	
	public AutomateStatusView getView() {
		return view;
	}
	
	public void setParent(final AutomatePresenter parent) {
		this.parent = parent;
	}
	
	public void stopRunning() {
		parent.stopRunning();
	}
	
	public void startAutomate() {
		parent.startAutomate();
	}
	
	public StatusPresenter getStatusPresenter() {
		return statusPresenter;
	}
	
	public void initializeView() {
		statusPresenter.initializeView();
	}
	
	public void setTotalAmount(final int amount) {
		view.setTotalAmount(amount);
	}
	
	public void setFinishedAmount(final int amount) {
		view.setFinishedAmount(amount);
	}

	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setFinishedAmount(e.getFinishedAmount());
			}
		});
	}
	
	@Override public void dataChanged(final ProcessFlowEvent e) { 
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setTotalAmount(e.getSource().getTotalAmount());
			}
		});
	}

	@Override public void modeChanged(final ModeChangedEvent e) { }
	@Override public void statusChanged(final StatusChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }
}
