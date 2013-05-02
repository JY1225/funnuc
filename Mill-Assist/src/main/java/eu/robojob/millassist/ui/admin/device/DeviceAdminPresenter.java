package eu.robojob.millassist.ui.admin.device;

import eu.robojob.millassist.ui.admin.AdminPresenter;
import eu.robojob.millassist.ui.admin.SubMenuAdminView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.MainContentPresenter;
import eu.robojob.millassist.ui.general.SubContentPresenter;

public class DeviceAdminPresenter implements SubContentPresenter {

	private SubMenuAdminView view;
	private DeviceMenuPresenter deviceMenuPresenter;
	private AdminPresenter parent;
	
	public DeviceAdminPresenter(final SubMenuAdminView view, final DeviceMenuPresenter deviceMenuPresenter) {
		this.view = view;
		this.deviceMenuPresenter = deviceMenuPresenter;
		deviceMenuPresenter.setParent(this);
		view.setMenuView(deviceMenuPresenter.getView());
	}
	
	@Override
	public void setActive(final boolean active) { 
	}

	public SubMenuAdminView getView() {
		return view;
	}

	public AdminPresenter getParent() {
		return this.parent;
	}
	
	@Override
	public void setParent(final MainContentPresenter mainContentPresenter) {
		this.parent = (AdminPresenter) mainContentPresenter;
	}

	public void setTextFieldListener(final TextInputControlListener listener) {
		deviceMenuPresenter.setTextFieldListener(listener);
	}

	public void setContentView(final AbstractFormView<?> node) {
		getView().setContentView(node);
	}
}
