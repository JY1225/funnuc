package eu.robojob.irscw.ui.admin.robot;

import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.SubContentPresenter;
import eu.robojob.irscw.ui.admin.AdminPresenter;

public class RobotAdminPresenter implements SubContentPresenter {

	private RobotAdminView view;
	private RobotMenuPresenter robotMenuPresenter;
	private AdminPresenter adminPresenter;
	
	public RobotAdminPresenter(final RobotAdminView view, final RobotMenuPresenter robotMenuPresenter) {
		this.view = view;
		this.robotMenuPresenter = robotMenuPresenter;
		robotMenuPresenter.setParent(this);
	}
	
	public RobotAdminView getView() {
		return view;
	}

	@Override
	public void setActive(final boolean active) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParent(final MainContentPresenter mainContentPresenter) {
		this.adminPresenter = (AdminPresenter) mainContentPresenter;
	}
}
