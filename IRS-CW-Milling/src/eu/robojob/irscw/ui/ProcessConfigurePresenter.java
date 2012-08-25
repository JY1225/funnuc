package eu.robojob.irscw.ui;

public class ProcessConfigurePresenter {

	private ProcessConfigureView view;
	
	private KeyboardPresenter keyboardPresenter;
	
	public ProcessConfigurePresenter(ProcessConfigureView view, KeyboardPresenter keyboardPresenter) {
		this.view = view;
		this.keyboardPresenter = keyboardPresenter;
		view.setPresenter(this);
		activateKeyoard();
	}
	
	public ProcessConfigureView getView() {
		return view;
	}
	
	public void showAlarmsView() {
		
	}
	
	public void showConfigureView() {
		
	}
	
	public void showTeachView() {
		
	}
	
	public void showAutomateView() {
		
	}
	
	private void activateKeyoard() {
		view.setTop(keyboardPresenter.getView());
	}
	
}
