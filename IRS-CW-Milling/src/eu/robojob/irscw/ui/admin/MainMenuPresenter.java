package eu.robojob.irscw.ui.admin;

public class MainMenuPresenter extends AbstractMenuPresenter<MainMenuView> {

	public MainMenuPresenter(final MainMenuView view) {
		super(view);
	}

	@Override
	public void setTextFieldListener(final AdminPresenter parent) {
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
