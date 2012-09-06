package eu.robojob.irscw.ui.main.configure.process;

import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

public class ProcessMenuPresenter extends AbstractMenuPresenter<ProcessMenuView> {

	private ConfigurePresenter parent;
	
	private ProcessConfigurePresenter configurePresenter;
	private ProcessOpenPresenter openPresenter;
		
	public ProcessMenuPresenter(ProcessMenuView view, ProcessConfigurePresenter configurePresenter, ProcessOpenPresenter openPresenter) {
		super(view);
		this.configurePresenter = configurePresenter;
		this.openPresenter = openPresenter;
	}

	@Override
	protected void setPresenter() {
		view.setPresenter(this);
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
		configurePresenter.setTextFieldListener(parent);
		configurePresenter.setParent(parent);
		openPresenter.setParent(parent);
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


}
