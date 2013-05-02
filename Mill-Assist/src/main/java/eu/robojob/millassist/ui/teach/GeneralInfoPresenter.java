package eu.robojob.irscw.ui.teach;

public class GeneralInfoPresenter {

	private TeachPresenter parent;
	private GeneralInfoView view;
	
	public GeneralInfoPresenter(final GeneralInfoView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(final TeachPresenter parent) {
		this.parent = parent;
	}
	
	public GeneralInfoView getView() {
		view.refresh();
		return view;
	}
	
	public void startTeachingOptimal() {
		parent.startTeachOptimal();
	}
	
	public void startTeachingAll() {
		parent.startTeachAll();
	}
	
}
