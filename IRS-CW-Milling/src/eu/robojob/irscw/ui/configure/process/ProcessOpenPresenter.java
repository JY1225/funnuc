package eu.robojob.irscw.ui.configure.process;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.configure.AbstractFormPresenter;

public class ProcessOpenPresenter extends AbstractFormPresenter<ProcessOpenView, ProcessMenuPresenter> {
	
	private static final Logger logger = Logger.getLogger(ProcessOpenPresenter.class);
	
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
	
	public void openProcess(String processId) {
		logger.info("loading process: " + processId);
	}
}
