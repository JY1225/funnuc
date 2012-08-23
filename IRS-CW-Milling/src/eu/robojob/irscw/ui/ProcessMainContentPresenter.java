package eu.robojob.irscw.ui;

public class ProcessMainContentPresenter {

	private ProcessMainContentView processMainContentView;
	
	public ProcessMainContentPresenter(ProcessMainContentView processMainContentView) {
		this.processMainContentView = processMainContentView;
		processMainContentView.setPresenter(this);
	}
	
	public ProcessMainContentView getView() {
		return processMainContentView;
	}
	
	public void showAlarmsView() {
		
	}
	
	public void showConfigureView() {
		
	}
	
	public void showTeachView() {
		
	}
	
	public void showAutomateView() {
		
	}
	
}
