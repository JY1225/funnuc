package eu.robojob.millassist.ui.configure.process;

import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlowManager;
import eu.robojob.millassist.ui.configure.AbstractMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class ProcessMenuPresenter extends AbstractMenuPresenter<ProcessMenuView> {
	
	private ProcessConfigurePresenter configurePresenter;
	private ProcessSavePresenter savePresenter;
	private ProcessOpenPresenter openPresenter;
	private ProcessNewPresenter newPresenter;	
	private ProcessFlow activeProcessFlow;
	
	public ProcessMenuPresenter(final ProcessMenuView view, final ProcessConfigurePresenter configurePresenter, final ProcessSavePresenter savePresenter,
			final ProcessOpenPresenter openPresenter, final ProcessNewPresenter newPresenter, final ProcessFlow activeProcessFlow, final ProcessFlowManager processFlowManager) {
		super(view);
		this.configurePresenter = configurePresenter;
		configurePresenter.setMenuPresenter(this);
		this.savePresenter = savePresenter;
		savePresenter.setMenuPresenter(this);
		this.openPresenter = openPresenter;
		openPresenter.setMenuPresenter(this);
		this.newPresenter = newPresenter;
		newPresenter.setMenuPresenter(this);
		this.activeProcessFlow = activeProcessFlow;
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
	
	public ProcessFlow getActiveProcessFlow() {
		return this.activeProcessFlow;
	}
	
	public void newProcess() {		
		getView().setNewActive();
		getParent().setBottomRightView(newPresenter.getView());
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
	
	public void refreshParent() {
		getParent().refresh();
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

	@Override
	public void unregisterListeners() { }

}
