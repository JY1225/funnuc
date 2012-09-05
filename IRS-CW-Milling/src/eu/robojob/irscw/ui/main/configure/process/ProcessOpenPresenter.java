package eu.robojob.irscw.ui.main.configure.process;

public class ProcessOpenPresenter {

	private ProcessOpenView view;
	
	public ProcessOpenPresenter(ProcessOpenView view) {
		this.view = view;
		view.setPresenter(this);
	}
}
