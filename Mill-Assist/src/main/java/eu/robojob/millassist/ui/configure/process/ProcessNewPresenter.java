package eu.robojob.millassist.ui.configure.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlowManager;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class ProcessNewPresenter extends AbstractFormPresenter<ProcessNewView, ProcessMenuPresenter> {
	
	private static Logger logger = LogManager.getLogger(ProcessNewPresenter.class.getName());
	private ProcessFlowManager processFlowManager;	
	private ProcessFlow activeProcessFlow;
	
	public ProcessNewPresenter(final ProcessNewView view, final ProcessFlow processFlow, final ProcessFlowManager processFlowManager) {
		super(view);
		getView().build();
		this.processFlowManager = processFlowManager;
		this.activeProcessFlow = processFlow;
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	//TODO add checks to see whether current process contains changes since last save
	public void newProcess() {
		logger.info("creating new process");
		activeProcessFlow.loadFromOtherProcessFlow(processFlowManager.createNewProcessFlow());
		getMenuPresenter().configureProcess();
	}
}
