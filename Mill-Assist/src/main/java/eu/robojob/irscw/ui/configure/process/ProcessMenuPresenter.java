package eu.robojob.irscw.ui.configure.process;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowManager;
import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public class ProcessMenuPresenter extends AbstractMenuPresenter<ProcessMenuView> {
	
	private ProcessConfigurePresenter configurePresenter;
	private ProcessSavePresenter savePresenter;
	private ProcessOpenPresenter openPresenter;
	private ProcessFlowManager processFlowManager;	
	private ProcessFlow activeProcessFlow;
	
	public ProcessMenuPresenter(final ProcessMenuView view, final ProcessConfigurePresenter configurePresenter, final ProcessSavePresenter savePresenter,
			final ProcessOpenPresenter openPresenter, final ProcessFlow activeProcessFlow, final ProcessFlowManager processFlowManager) {
		super(view);
		this.configurePresenter = configurePresenter;
		configurePresenter.setMenuPresenter(this);
		this.savePresenter = savePresenter;
		savePresenter.setMenuPresenter(this);
		this.openPresenter = openPresenter;
		openPresenter.setMenuPresenter(this);
		this.activeProcessFlow = activeProcessFlow;
		this.processFlowManager = processFlowManager;
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void saveData() {
		getView().setSaveActive();
		getParent().setBottomRightView(savePresenter.getView());
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
		activeProcessFlow.loadFromOtherProcessFlow(processFlowManager.createNewProcessFlow());
		configureProcess();
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
	public void setTextFieldListener(final TextInputControlListener listener) {
		configurePresenter.setTextFieldListener(listener);
		openPresenter.setTextFieldListener(listener);
		savePresenter.setTextFieldListener(listener);
	}

	@Override
	public boolean isConfigured() {
		//TODO implement
		return false;
	}

}
