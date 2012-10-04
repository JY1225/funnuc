package eu.robojob.irscw.ui.configure.process;

import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;

public class ProcessMenuPresenter extends AbstractMenuPresenter<ProcessMenuView> {

	private ConfigurePresenter parent;
	
	private ProcessConfigurePresenter configurePresenter;
	private ProcessOpenPresenter openPresenter;
			
	public ProcessMenuPresenter(ProcessMenuView view, ProcessConfigurePresenter configurePresenter, ProcessOpenPresenter openPresenter) {
		super(view);
		this.configurePresenter = configurePresenter;
		configurePresenter.setMenuPresenter(this);
		configurePresenter.setTextFieldListener(parent);
		this.openPresenter = openPresenter;
		openPresenter.setMenuPresenter(this);
	}

	@Override
	protected void setPresenter() {
		view.setPresenter(this);
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}

	public ProcessMenuView getView() {
		return view;
	}
	
	public void saveData() {
	}
	
	public void configureProcess() {
		view.setConfigureActive();
		parent.setBottomRightView(configurePresenter.getView());
	}
	
	public void openProcess() {
		view.setOpenActive();
		parent.setBottomRightView(openPresenter.getView());
	}
	
	public void newProcess() {
		
	}

	@Override
	public void openFirst() {
		configureProcess();
	}

	public void setAddDeviceMode() {
		parent.setAddDeviceMode();
	}
	
	public void setRemoveDeviceMode() {
		parent.setRemoveDeviceMode();
	}
	
	public void setNormalMode() {
		parent.setNormalMode();
		configurePresenter.setNormalMode();
	}

	@Override
	public void setBlocked(boolean blocked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextFieldListener(ConfigurePresenter parent) {
		configurePresenter.setTextFieldListener(parent);
	}

	@Override
	public boolean isConfigured() {
		// TODO Auto-generated method stub
		return false;
	}

}
