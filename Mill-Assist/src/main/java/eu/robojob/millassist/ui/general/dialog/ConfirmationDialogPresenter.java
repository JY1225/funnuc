package eu.robojob.millassist.ui.general.dialog;

public class ConfirmationDialogPresenter extends AbstractDialogPresenter<ConfirmationDialogView, Boolean> {

	public ConfirmationDialogPresenter(ConfirmationDialogView view) {
		super(view);
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

}
