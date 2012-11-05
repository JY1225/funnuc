package eu.robojob.irscw.ui.automate;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.main.flow.FixedProcessFlowPresenter;


public class AutomatePresenter implements MainContentPresenter {

	private AutomateView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	private ProcessFlow processFlow;
	private MainPresenter parent;
	
	public AutomatePresenter(AutomateView view, FixedProcessFlowPresenter processFlowPresenter, ProcessFlow processFlow) {
		this.view = view;
		view.setPresenter(this);
		this.processFlowPresenter = processFlowPresenter;
		view.setTop(processFlowPresenter.getView());
		this.processFlow = processFlow;
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
	
	@Override
	public AutomateView getView() {
		return view;
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
	}
	
	@Override
	public void setActive(boolean active) {
		
	}
	
	public void clickedStart() {
		
	}
	
	public void clickedPause() {
		
	}
}
