package eu.robojob.millassist.ui.admin.device;

import eu.robojob.millassist.ui.admin.MainMenuPresenter;
import eu.robojob.millassist.ui.admin.SubMenuAdminView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.SubContentPresenter;

public class DeviceAdminPresenter implements SubContentPresenter {

	private SubMenuAdminView view;
	private DeviceMenuPresenter deviceMenuPresenter;
	private MainMenuPresenter parent;
	
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

	public MainMenuPresenter getParent() {
		return this.parent;
	}
	
	@Override
	public void setParent(final MainMenuPresenter mainContentPresenter) {
		this.parent = (MainMenuPresenter) mainContentPresenter;
	}

	public void setTextFieldListener(final TextInputControlListener listener) {
		deviceMenuPresenter.setTextFieldListener(listener);
	}

	public void setContentView(final AbstractFormView<?> node) {
		getView().setContentView(node);
	}
}
