package eu.robojob.millassist.ui.automate.flow;

import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.DimensionsChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.general.flow.ProcessFlowView;
import eu.robojob.millassist.ui.general.model.ProcessFlowAdapter;

public class AutomateProcessFlowView extends ProcessFlowView implements ProcessFlowListener {

	private AutomateProcessFlowPresenter presenter;
	private int nbOriginalProgressBars;
	
	public AutomateProcessFlowView(final int progressBarAmount) {
		super(progressBarAmount);
		nbOriginalProgressBars = progressBarAmount;
	}

	public void setPresenter(final AutomateProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void loadProcessFlow(final ProcessFlow processFlow) {
		processFlow.removeListener(this);
		processFlow.addListener(this);
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		this.progressBarAmount = nbOriginalProgressBars;
		if (processFlow.isConcurrentExecutionPossible()) {
			this.progressBarAmount++;
		} 
		refresh();
	}

	@Override
	public void disableClickable() {
		presenter.buildFinished();
	}

	@Override
	public void modeChanged(ModeChangedEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void statusChanged(StatusChangedEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dataChanged(DataChangedEvent e) {
		loadProcessFlow(e.getSource());
	}

	@Override
	public void finishedAmountChanged(FinishedAmountChangedEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exceptionOccured(ExceptionOccuredEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregister() {
		// TODO Auto-generated method stub
		
	}
	
	@Override public void dimensionChanged(DimensionsChangedEvent e) {	}
}
