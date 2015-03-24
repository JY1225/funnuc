package eu.robojob.millassist.ui.configure.process;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlowManager;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.util.Translator;

public class ProcessOpenPresenter extends AbstractFormPresenter<ProcessOpenView, ProcessMenuPresenter> {
	
	private static Logger logger = LogManager.getLogger(ProcessOpenPresenter.class.getName());
	private List<ProcessFlow> allProcessFlows;
	private ObservableList<ProcessFlow> filteredProcessFlows;
	private ProcessFlowManager processFlowManager;	
	private ProcessFlow activeProcessFlow;
	
	private static final String OPEN_UNSAVED_TITLE = "ProcessOpenPresenter.openUnsavedTitle";
	private static final String OPEN_UNSAVED = "ProcessOpenPresenter.openUnsaved";
	private static final String DELETE_TITLE = "ProcessOpenPresenter.deleteTitle";
	private static final String DELETE = "ProcessOpenPresenter.delete";
	
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
	
	public void openProcess(final int processFlowId) {
		final ProcessFlow processFlow = processFlowManager.getProcessFlowForId(processFlowId);
		// Do this on seperate thread (not on UI thread)
		if(activeProcessFlow.hasChangesSinceLastSave()) {
			ThreadManager.submit(new Thread() {
				@Override
				public void run() {
					try {
						if (askConfirmation(Translator.getTranslation(OPEN_UNSAVED_TITLE), Translator.getTranslation(OPEN_UNSAVED))) {
							Platform.runLater(new Thread() {
								@Override
								public void run() {
									activeProcessFlow.loadFromOtherProcessFlow(processFlow);
									processFlowManager.updateLastOpened(activeProcessFlow);
									getMenuPresenter().refreshParent();
									getMenuPresenter().configureProcess();
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e);
					}
				}
			});
		} else {
			logger.info("loading process: " + processFlow.getId());
			//otherwise we have the same process - no need to load it
			if (activeProcessFlow.getId() != processFlow.getId()) {
				activeProcessFlow.loadFromOtherProcessFlow(processFlow);
				processFlowManager.updateLastOpened(activeProcessFlow);
				getMenuPresenter().refreshParent();
				getMenuPresenter().configureProcess();
			}
		}
				
	}
	
	public void deleteProcess(final int processId) {
		ThreadManager.submit(new Thread() {
			@Override
			public void run() {
				try {
					if (askConfirmation(Translator.getTranslation(DELETE_TITLE), Translator.getTranslation(DELETE))) {
						Platform.runLater(new Thread() {
							@Override
							public void run() {
								if (processId == activeProcessFlow.getId()) {
									activeProcessFlow.processProcessFlowEvent(new DataChangedEvent(activeProcessFlow, null, false));
								}
								processFlowManager.deleteProcessFlow(processId);
								getMenuPresenter().refreshParent();
								refreshProcessFlowList();
								filterChanged(getView().getFilter());
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		});
	}
}