package eu.robojob.millassist.ui.admin.robot;

import eu.robojob.millassist.ui.admin.MainMenuPresenter;
import eu.robojob.millassist.ui.admin.SubMenuAdminView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.SubContentPresenter;

public class RobotAdminPresenter implements SubContentPresenter {

	private SubMenuAdminView view;
	private RobotMenuPresenter robotMenuPresenter;
	private MainMenuPresenter parent;
	
	public RobotAdminPresenter(final SubMenuAdminView view, final RobotMenuPresenter robotMenuPresenter) {
		this.view = view;
		this.robotMenuPresenter = robotMenuPresenter;
		robotMenuPresenter.setParent(this);
		getView().setMenuView(robotMenuPresenter.getView());
	}
	
	public SubMenuAdminView getView() {
		return view;
	}

	@Override
	public void setActive(final boolean active) {
	}

	@Override
	public void setParent(final MainMenuPresenter mainContentPresenter) {
		this.parent = (MainMenuPresenter) mainContentPresenter;
	}
	
	public MainMenuPresenter getParent() {
		return this.parent;
	}
	
	public void setContentView(final AbstractFormView<?> node) {
		getView().setContentView(node);
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		robotMenuPresenter.setTextFieldListener(listener);
	}
}
