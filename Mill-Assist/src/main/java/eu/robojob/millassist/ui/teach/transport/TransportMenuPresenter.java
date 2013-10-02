package eu.robojob.millassist.ui.teach.transport;

import javafx.application.Platform;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.teach.AbstractMenuPresenter;

public class TransportMenuPresenter extends AbstractMenuPresenter<TransportMenuView> {

	private TransportTeachedOffsetPresenter pickPresenter;
	private TransportTeachedOffsetPresenter putPresenter;
	
	public TransportMenuPresenter(final TransportMenuView view, final TransportTeachedOffsetPresenter pickPresenter, 
			final TransportTeachedOffsetPresenter putPresenter) {
		super(view);
		this.pickPresenter = pickPresenter;
		this.putPresenter = putPresenter;
		pickPresenter.setMenuPresenter(this);
		putPresenter.setMenuPresenter(this);
		view.build();
	}
	
	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void configurePick() {
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				getView().setConfigurePickActive();
				pickPresenter.refresh();
				getParent().setBottomRightView(pickPresenter.getView());
			}
		});
	}
	
	public void configurePut() {
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				getView().setConfigurePutActive();
				putPresenter.refresh();
				getParent().setBottomRightView(putPresenter.getView());
			}
		});
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		if (pickPresenter != null) {
			pickPresenter.setTextFieldListener(listener);
		}
		putPresenter.setTextFieldListener(listener);
	}

	@Override
	public void openFirst() {
		if (pickPresenter != null) {
			configurePick();
		} else {
			configurePut();
		}
	}

	@Override
	public void unregisterListeners() {
		// TODO Auto-generated method stub
		
	}
}
