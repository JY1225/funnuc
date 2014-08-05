package eu.robojob.millassist.ui.configure.process;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.process.ProcessFlowMapper;
import eu.robojob.millassist.process.DuplicateProcessFlowNameException;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlowManager;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;

public class ProcessSavePresenter extends AbstractFormPresenter<ProcessSaveView, ProcessMenuPresenter> {

	private ProcessFlowManager processFlowManager;
	private ProcessFlow processFlow;
	
	private static final String DUPLICATE_NAME = "ProcessSavePresenter.duplicateName";
	private static final String SAVE_SUCCESSFULL = "ProcessSavePresenter.saveOK";
	private static final String UPDATE_SUCCESSFULL = "ProcessSavePresenter.updateOK";
	
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
	
	public ProcessFlowManager getProcessFlowManager() {
		return this.processFlowManager;
	}
	
	public void nameChanged(final String name) {
		processFlow.setName(name);
		processFlow.processProcessFlowEvent(new DataChangedEvent(processFlow, null, false));
		getMenuPresenter().refreshParent();
	}
	
	//FIXME: make sure process can only be saved if configured correctly
	public void save(String saveProcessName) {
		try {
			//in this case a new name is given, so we need to check whether we can create a copy or not
			//when a process already exists with this name, we will create a warning and will not save
			int saveProcessID = (ProcessFlowMapper.getProcessFlowIdForName(saveProcessName));
			if(saveProcessID != 0) {
				saveAsExisting();
			} else {
				saveAsNew();
			}
			getMenuPresenter().refreshParent();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	private void saveAsExisting() {
		try {
			processFlowManager.updateProcessFlow(processFlow);
			//reset the flag to indicate the latest changes are saved
			processFlow.setChangesSinceLastSave(false);
			getView().showNotification(Translator.getTranslation(UPDATE_SUCCESSFULL), Type.OK);
			getMenuPresenter().refreshParent();
		} catch (DuplicateProcessFlowNameException e) {
			//We will come here when a user tries to save an existing process which is not the activeProcess
			getView().showNotification(Translator.getTranslation(DUPLICATE_NAME), Type.WARNING);
		}
	}
	
	private void saveAsNew() {
		try {
			processFlowManager.saveProcessFlow(processFlow);
			//reset the flag to indicate the latest changes are saved
			processFlow.setChangesSinceLastSave(false);
			//Show the new processName in the flow region
			getMenuPresenter().getParent().getProcessFlowPresenter().getView().refreshProcessFlowName();
			getView().showNotification(Translator.getTranslation(SAVE_SUCCESSFULL), Type.OK);
			getMenuPresenter().refreshParent();
		} catch (DuplicateProcessFlowNameException e) {
			//this should never happen
			getView().showNotification(Translator.getTranslation(DUPLICATE_NAME), Type.WARNING);
		}
	}
	
}
