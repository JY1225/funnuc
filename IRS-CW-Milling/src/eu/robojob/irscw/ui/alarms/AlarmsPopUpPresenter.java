package eu.robojob.irscw.ui.alarms;

import eu.robojob.irscw.ui.general.AbstractPopUpPresenter;

public class AlarmsPopUpPresenter extends AbstractPopUpPresenter<AlarmsPopUpView> {

	public AlarmsPopUpPresenter(final AlarmsPopUpView view) {
		super(view);
	}

	@Override
	protected void setViewPresenter() {
		getView().setPresenter(this);
	}

}
