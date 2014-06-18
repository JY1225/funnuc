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

	public void newProcess() {
		if(processFlowManager.getActiveProcessFlow().hasChangesSinceLastSave()) {
			getView().showUnsavedNotificationMsg();
		} else {
			createNewProcess();
		}
	}
	
	public ProcessFlowManager getProcessFlowManager() {
		return this.processFlowManager;
	}
	
	private void createNewProcess() {
		logger.info("creating new process");
		activeProcessFlow.loadFromOtherProcessFlow(processFlowManager.createNewProcessFlow());
		getMenuPresenter().configureProcess();
	}
	
	@Override
	protected void doNoAction() {
		logger.info("User action: clicked on NO button.");
		super.doNoAction();
	}
	
	@Override
	protected void doYesAction() {
		logger.info("User action: clicked on YES button to continue his action.");
		createNewProcess();
		super.doYesAction();
	}
}
