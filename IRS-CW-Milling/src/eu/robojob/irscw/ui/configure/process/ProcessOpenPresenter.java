package eu.robojob.irscw.ui.configure.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowManager;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;

public class ProcessOpenPresenter extends AbstractFormPresenter<ProcessOpenView, ProcessMenuPresenter> {
	
	private static Logger logger = LogManager.getLogger(ProcessOpenPresenter.class.getName());
	private ProcessFlowManager processFlowManager;
	private List<ProcessFlow> allProcessFlows;
	private List<ProcessFlow> filteredProcessFlows;
		
	public ProcessOpenPresenter(final ProcessOpenView view, final ProcessFlowManager processFlowManager) {
		super(view);
		getView().build();
		this.processFlowManager = processFlowManager;
		this.allProcessFlows = processFlowManager.getProcessFlows();
		this.filteredProcessFlows = new ArrayList<ProcessFlow>();
		getView().setProcessFlows(filteredProcessFlows);
		filterChanged("");
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void filterChanged(final String filter) {
		filteredProcessFlows.clear();
		for (int i = 0; i < 4; i++) {
			for (ProcessFlow processFlow : allProcessFlows) {
				if (processFlow.getName().contains(filter)) {
					filteredProcessFlows.add(processFlow);
				}
			}
		}
	}
	
	public void openProcess(final String processId) {
		logger.info("loading process: " + processId);
		
	}
}
