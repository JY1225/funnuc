package eu.robojob.irscw.ui.configure.process;

import eu.robojob.irscw.ui.configure.AbstractFormPresenter;

public class ProcessOpenPresenter extends AbstractFormPresenter<ProcessOpenView, ProcessMenuPresenter> {
	
	public ProcessOpenPresenter(ProcessOpenView view) {
		super(view);
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
}
