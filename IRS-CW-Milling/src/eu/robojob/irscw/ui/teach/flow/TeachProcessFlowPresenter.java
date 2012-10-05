package eu.robojob.irscw.ui.teach.flow;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.main.flow.AbstractProcessFlowPresenter;
import eu.robojob.irscw.ui.main.flow.ProcessFlowView;

public class TeachProcessFlowPresenter extends AbstractProcessFlowPresenter {

	public TeachProcessFlowPresenter(ProcessFlowView view) {
		super(view);
		view.setPresenter(this);
	}

	@Override
	public void deviceClicked(int deviceIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transportClicked(int transportIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void backgroundClicked() {
		// TODO Auto-generated method stub
		
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		view.setProcessFlow(processFlow);
		view.showQuestionMarks();
		view.disableClickable();
	}

}
