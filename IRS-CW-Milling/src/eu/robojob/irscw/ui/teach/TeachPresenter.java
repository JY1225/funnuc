package eu.robojob.irscw.ui.teach;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.teach.flow.TeachProcessFlowPresenter;

public class TeachPresenter {

	private TeachView view;
	private TeachProcessFlowPresenter processFlowPresenter;
	private MainPresenter parent;
	
	public TeachPresenter(TeachView view, TeachProcessFlowPresenter processFlowPresenter) {
		this.view = view;
		this.processFlowPresenter = processFlowPresenter;
		view.setTop(processFlowPresenter.getView());
	}

	public TeachView getView() {
		return view;
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
	}
}
