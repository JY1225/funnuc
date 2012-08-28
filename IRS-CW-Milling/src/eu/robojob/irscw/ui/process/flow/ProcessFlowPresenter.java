package eu.robojob.irscw.ui.process.flow;

public class ProcessFlowPresenter {

	private ProcessFlowView view;
	
	public ProcessFlowPresenter(ProcessFlowView view) {
		this.view = view;
		view.setPresenter(this);
	}
}
