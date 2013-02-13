package eu.robojob.irscw.ui.configure.process;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowManager;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;

public class ProcessOpenPresenter extends AbstractFormPresenter<ProcessOpenView, ProcessMenuPresenter> {
	
	private static Logger logger = LogManager.getLogger(ProcessOpenPresenter.class.getName());
	private List<ProcessFlow> allProcessFlows;
	private ObservableList<ProcessFlow> filteredProcessFlows;
	private ProcessFlowManager processFlowManager;	
	
	public ProcessOpenPresenter(final ProcessOpenView view, final ProcessFlowManager processFlowManager) {
		super(view);
		getView().build();
		this.allProcessFlows = processFlowManager.getProcessFlows();
		this.processFlowManager = processFlowManager;
		this.filteredProcessFlows = FXCollections.observableArrayList();
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
	
	public void refreshProcessFlowList() {
		allProcessFlows = processFlowManager.getProcessFlows();
	}
	
	public void filterChanged(final String filter) {
		filteredProcessFlows.clear();
		for (ProcessFlow processFlow : allProcessFlows) {
			if (processFlow.getName().contains(filter)) {
				filteredProcessFlows.add(processFlow);
			}
		}
	}
	
	public void openProcess(final ProcessFlow processFlow) {
		logger.info("loading process: " + processFlow.getId());
	}
}
