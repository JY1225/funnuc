package eu.robojob.millassist.ui.admin.general;

import eu.robojob.millassist.ui.admin.MainMenuPresenter;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class GeneralAdminPresenter extends AbstractFormPresenter<GeneralAdminView, MainMenuPresenter> {

	public GeneralAdminPresenter(final GeneralAdminView view) {
		super(view);
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

}
