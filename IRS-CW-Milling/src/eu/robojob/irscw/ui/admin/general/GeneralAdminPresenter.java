package eu.robojob.irscw.ui.admin.general;

import eu.robojob.irscw.ui.admin.MainMenuPresenter;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

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
