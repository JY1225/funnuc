package eu.robojob.millassist.ui.teach;

import eu.robojob.millassist.ui.general.status.StatusPresenter;

public class TeachStatusPresenter {

	private TeachStatusView view;
	private TeachPresenter parent;
	private StatusPresenter statusPresenter;
	
	public TeachStatusPresenter(final TeachStatusView view, final StatusPresenter statusPresenter) {
		this.view = view;
		this.statusPresenter = statusPresenter;
		view.setPresenter(this);
		view.setStatusView(statusPresenter.getView());
		view.build();
	}
	
	public TeachStatusView getView() {
		return view;
	}
	
	public void setParent(final TeachPresenter parent) {
		this.parent = parent;
	}
	
	public void stopTeaching() {
		parent.stopTeaching();
	}
	
	public void saveProcess() {
		parent.saveProcess();
	}
	
	public void showSaveButton(final boolean show) {
		view.showSaveButton(show);
	}
	
	public StatusPresenter getStatusPresenter() {
		return statusPresenter;
	}
	
	public void initializeView() {
		statusPresenter.initializeView();
	}
}
