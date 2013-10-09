package eu.robojob.millassist.ui.automate;

import javafx.application.Platform;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.ProcessFlow.Type;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.general.status.StatusPresenter;

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
	
	public void setTimers(final String totalTime, final String finishedInterval, final String remainingCurrent, final String timeTillFinished) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				timingView.setTotalTime(totalTime);
				timingView.setFinishedInterval(finishedInterval);
				timingView.setRemainingCurrent(remainingCurrent);
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
	
	public void continueAutomate() {
		parent.continueAutomate();
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
				view.setTotalAmount(e.getTotalAmount());
				view.setFinishedAmount(e.getFinishedAmount());
			}
		});
	}
	
	@Override public void dataChanged(final DataChangedEvent e) { 
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setTotalAmount(e.getSource().getTotalAmount());
			}
		});
	}

	@Override public void modeChanged(final ModeChangedEvent e) { 
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (e.getMode() == Mode.PAUSED) {
					view.activateContinueButton();
				}
				if ((e.getMode() == Mode.AUTO) && (e.getSource().getType() == Type.CONTINUOUS)) {
					view.enableContinuousAnimation(true);
				} else {
					view.enableContinuousAnimation(false);
				}
			}
		});
	}
	
	@Override public void statusChanged(final StatusChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }

	@Override
	public void unregister() {
		//TODO implement!
	}
}
