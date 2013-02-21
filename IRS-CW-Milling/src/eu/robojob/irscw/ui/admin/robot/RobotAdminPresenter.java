package eu.robojob.irscw.ui.admin.robot;

import javafx.scene.Node;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.SubContentPresenter;
import eu.robojob.irscw.ui.admin.AdminPresenter;
import eu.robojob.irscw.ui.admin.SubMenuAdminView;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public class RobotAdminPresenter implements SubContentPresenter {

	private SubMenuAdminView view;
	private RobotMenuPresenter robotMenuPresenter;
	private AdminPresenter parent;
	
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
	public void setParent(final MainContentPresenter mainContentPresenter) {
		this.parent = (AdminPresenter) mainContentPresenter;
	}
	
	public AdminPresenter getParent() {
		return this.parent;
	}
	
	public void setContentView(final Node node) {
		getView().setContentView(node);
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		robotMenuPresenter.setTextFieldListener(listener);
	}
}
