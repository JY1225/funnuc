package eu.robojob.irscw.ui.automate;


public class AutomatePresenter {

	private AutomateView view;
	
	public AutomatePresenter(AutomateView view) {
		this.view = view;
	}
	
	public AutomateView getView() {
		return view;
	}
}
