package eu.robojob.irscw.ui.admin.device;

import javafx.scene.Node;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.SubContentPresenter;
import eu.robojob.irscw.ui.admin.AdminPresenter;
import eu.robojob.irscw.ui.admin.SubMenuAdminView;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

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
	public void setActive(boolean active) { 
	}

	public SubMenuAdminView getView() {
		return view;
	}

	public AdminPresenter getParent() {
		return this.parent;
	}
	
	@Override
	public void setParent(MainContentPresenter mainContentPresenter) {
		this.parent = (AdminPresenter) mainContentPresenter;
	}

	public void setTextFieldListener(final TextInputControlListener listener) {
		deviceMenuPresenter.setTextFieldListener(listener);
	}

	public void setContentView(final Node node) {
		getView().setContentView(node);
	}
}
