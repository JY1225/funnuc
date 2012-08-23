package eu.robojob.irscw.ui;

public class MainPresenter {

	private MainView view;
	
	private ProcessMenuBarPresenter processMenuBarPresenter;
	private ProcessMainContentPresenter processMainContentPresenter;
		
	public MainPresenter(MainView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setProcessMenuBarPresenter(ProcessMenuBarPresenter processMenuBarPresenter) {
		this.processMenuBarPresenter = processMenuBarPresenter;
	}
	
	public void setProcessMainContentPresenter(ProcessMainContentPresenter processMainContentPresenter) {
		this.processMainContentPresenter = processMainContentPresenter;
	}
	
	public void showProcessView() {
		view.setHeader(processMenuBarPresenter.getView());
		view.setContent(processMainContentPresenter.getView());
	}
	
	public void showAdminView() {
		
	}
	
	public MainView getView() {
		return view;
	}
	
}
