package eu.robojob.irscw.ui.admin;

import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public class MainMenuPresenter extends AbstractMenuPresenter<MainMenuView> {

	public MainMenuPresenter(final MainMenuView view) {
		super(view);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		getView().setGeneralMenuActive();
	}

	@Override
	public void setBlocked(final boolean blocked) {
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void generalMenuClicked() {
		getView().setGeneralMenuActive();
	}
	
	public void robotMenuClicked() {
		getView().setRobotMenuActive();
	}
	
	public void deviceMenuClicked() {
		getView().setDeviceMenuActive();
	}

}
