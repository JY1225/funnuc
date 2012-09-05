package eu.robojob.irscw.ui.main.flow;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

public class ProcessFlowPresenter {

	private ProcessFlowView view;
	private ConfigurePresenter parent;
	
	private static Logger logger = Logger.getLogger(ProcessFlowPresenter.class);
	
	public ProcessFlowPresenter(ProcessFlowView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}
	
	public ProcessFlowView getView() {
		return view;
	}
	
	public void deviceClicked(int index) {
		logger.debug("Clicked device with index: " + index);
		view.focusDevice(index);
		parent.configureDevice(index);
	}
	
	public void transportClicked(int index) {
		logger.debug("Clicked transport with index: " + index);
		view.focusTransport(index);
	}
	
	public void backgroundClicked() {
		logger.debug("Clicked process-flow background");
		view.focusAll();
		parent.configureProcess();
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		view.setProcessFlow(processFlow);
	}
}
