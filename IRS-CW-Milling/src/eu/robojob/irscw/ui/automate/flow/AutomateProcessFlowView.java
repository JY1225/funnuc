package eu.robojob.irscw.ui.automate.flow;

import eu.robojob.irscw.ui.general.flow.ProcessFlowView;

public class AutomateProcessFlowView extends ProcessFlowView {

	private AutomateProcessFlowPresenter presenter;
	
	public AutomateProcessFlowView(final int progressBarAmount) {
		super(progressBarAmount);
	}

	public void setPresenter(final AutomateProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void disableClickable() {
		presenter.buildFinished();
	}
}
