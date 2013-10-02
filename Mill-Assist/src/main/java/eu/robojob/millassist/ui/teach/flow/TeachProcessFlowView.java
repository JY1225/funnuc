package eu.robojob.millassist.ui.teach.flow;

import eu.robojob.millassist.ui.general.flow.ProcessFlowView;

public class TeachProcessFlowView extends ProcessFlowView {

	private TeachProcessFlowPresenter presenter;
	
	public TeachProcessFlowView(final int progressBarAmount) {
		super(progressBarAmount);
	}

	public void setPresenter(final TeachProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void disableClickable() {
		presenter.buildFinished();
	}
}
