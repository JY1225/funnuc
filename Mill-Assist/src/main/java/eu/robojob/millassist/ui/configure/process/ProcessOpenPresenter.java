package eu.robojob.millassist.ui.configure.process;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlowManager;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class ProcessOpenPresenter extends AbstractFormPresenter<ProcessOpenView, ProcessMenuPresenter> {
	
	private static Logger logger = LogManager.getLogger(ProcessOpenPresenter.class.getName());
	private List<ProcessFlow> allProcessFlows;
	private ObservableList<ProcessFlow> filteredProcessFlows;
	private ProcessFlowManager processFlowManager;	
	private ProcessFlow activeProcessFlow;
	private ProcessFlow tmpFlow;
	
	public ProcessOpenPresenter(final ProcessOpenView view, final ProcessFlow processFlow, final ProcessFlowManager processFlowManager) {
		super(view);
		getView().build();
		this.allProcessFlows = processFlowManager.getProcessFlows();
		this.processFlowManager = processFlowManager;
		this.activeProcessFlow = processFlow;
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
	
	public String getActiveProcessFlowName() {
		return activeProcessFlow.getName();
	}
	
	/**
	 * This action will be called when the user decides to save changes of his current process before opening the other process
	 */
	@Override
	public void doYesAction() {
		//FIXME - Keyboard is shown
		logger.info("User action: clicked on YES button to continue his action.");
		logger.info("loading process: " + tmpFlow.getId());
		activeProcessFlow.loadFromOtherProcessFlow(tmpFlow);
		processFlowManager.updateLastOpened(activeProcessFlow);
		super.doYesAction();
	}
	
	@Override
	public void doNoAction() {
		super.doNoAction();
		logger.info("User action: clicked on NO button.");
		//Maak "Bewaar" als actief scherm
	}
	
	public void openProcess(final ProcessFlow processFlow) {
		if(activeProcessFlow.hasChangesSinceLastSave()) {
			logger.info("active process: " + activeProcessFlow.getId() + " contains unsaved changes.");
			getView().showUnsavedNotificationMsg();
			//Save the processFlow instead of retrieving it back from the view (in case user decides to skip saving)
			tmpFlow = processFlow;
		} else {
			logger.info("loading process: " + processFlow.getId());
			//otherwise we have the same process - no need to load it
			if (activeProcessFlow.getId() != processFlow.getId()) {
				activeProcessFlow.loadFromOtherProcessFlow(processFlow);
				processFlowManager.updateLastOpened(activeProcessFlow);
			}
		}
	}
	
	public void deleteProcess(ProcessFlow process) {
		processFlowManager.deleteProcessFlow(process);
		//Check whether we are deleting the current process or not
		if(process.getId() != activeProcessFlow.getId()) {
			openProcess(processFlowManager.getLastProcessFlow());
		}
		getMenuPresenter().getParent().getProcessFlowPresenter().getView().refreshProcessFlowName();
		System.out.println("ACTIVE PROCESS = " + activeProcessFlow.getName());
		getView().refresh();
	}
}
