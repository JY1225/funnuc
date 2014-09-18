package eu.robojob.millassist.ui.general.dialog;

public class NotificationDialogPresenter extends AbstractDialogPresenter<NotificationDialogView, Boolean> {

	public NotificationDialogPresenter(NotificationDialogView view) {
		super(view);
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

}
