package eu.robojob.irscw.ui.configure.process;

import eu.robojob.irscw.process.DuplicateProcessFlowNameException;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowManager;
import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;

public class ProcessMenuPresenter extends AbstractMenuPresenter<ProcessMenuView> {
	
	private ProcessFlow processFlow;
	private ProcessFlowManager processFlowManager;
	private ProcessConfigurePresenter configurePresenter;
	private ProcessOpenPresenter openPresenter;
			
	public ProcessMenuPresenter(final ProcessMenuView view, final ProcessConfigurePresenter configurePresenter, final ProcessOpenPresenter openPresenter, 
			final ProcessFlow processFlow, final ProcessFlowManager processFlowManager) {
		super(view);
		this.configurePresenter = configurePresenter;
		configurePresenter.setMenuPresenter(this);
		this.openPresenter = openPresenter;
		openPresenter.setMenuPresenter(this);
		this.processFlow = processFlow;
		this.processFlowManager = processFlowManager;
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void saveData() {
		// check if another process exists with the same name (other id), if not: save data, else: show message: first delete data
		try {
			processFlowManager.saveProcessFlow(processFlow);
		} catch (DuplicateProcessFlowNameException e) {
			e.printStackTrace();
		}
	}
	
	public void configureProcess() {
		getView().setConfigureActive();
		getParent().setBottomRightView(configurePresenter.getView());
	}
	
	public void openProcess() {
		getView().setOpenActive();
		getParent().setBottomRightView(openPresenter.getView());
	}
	
	public void newProcess() {
	}
	
	public void processOpened() {
		getParent().processOpened();
	}

	@Override
	public void openFirst() {
		configureProcess();
	}

	public void setAddDeviceMode() {
		getParent().setAddDeviceMode();
	}
	
	public void setRemoveDeviceMode() {
		getParent().setRemoveDeviceMode();
	}
	
	public void setNormalMode() {
		getParent().setNormalMode();
		configurePresenter.setNormalMode();
	}

	@Override
	public void setBlocked(final boolean blocked) {
	}

	@Override
	public void setTextFieldListener(final ConfigurePresenter parent) {
		configurePresenter.setTextFieldListener(parent);
	}

	@Override
	public boolean isConfigured() {
		//TODO implement
		return false;
	}

}
