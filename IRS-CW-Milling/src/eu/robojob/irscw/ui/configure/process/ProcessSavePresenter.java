package eu.robojob.irscw.ui.configure.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.DuplicateProcessFlowNameException;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowManager;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;

public class ProcessSavePresenter extends AbstractFormPresenter<ProcessSaveView, ProcessMenuPresenter> {

	private ProcessFlowManager processFlowManager;
	private ProcessFlow processFlow;
	
	private static Logger logger = LogManager.getLogger(ProcessSavePresenter.class.getName());
	
	public ProcessSavePresenter(final ProcessSaveView view, final ProcessFlowManager processFlowManager, final ProcessFlow processFlow) {
		super(view);
		view.setProcessFlow(processFlow);
		view.build();
		this.processFlowManager = processFlowManager;
		this.processFlow = processFlow;
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return true;
	}
	
	public void nameChanged(final String name) {
		processFlow.setName(name);
	}
	
	public void overwrite() {
		try {
			processFlowManager.updateProcessFlow(processFlow);
		} catch (DuplicateProcessFlowNameException e) {
			//FIXME handle this exception
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void saveAsNew() {
		try {
			processFlowManager.saveProcessFlow(processFlow);
		} catch (DuplicateProcessFlowNameException e) {
			//FIXME handle this exception
			logger.error(e);
			e.printStackTrace();
		}
	}
}
