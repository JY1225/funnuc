package eu.robojob.millassist.ui.automate.device.stacking.stackplate;

import eu.robojob.millassist.ui.automate.AbstractMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class BasicStackPlateMenuPresenter extends AbstractMenuPresenter<BasicStackPlateMenuView> {

	private BasicStackPlateLayoutPresenter layoutPresenter;
	private BasicStackPlateRefillPresenter refillPresenter;
	
	public BasicStackPlateMenuPresenter(final BasicStackPlateMenuView view, final BasicStackPlateLayoutPresenter layoutPresenter,
			final BasicStackPlateRefillPresenter refillPresenter) {
		super(view);
		view.build();
		this.layoutPresenter = layoutPresenter;
		this.refillPresenter = refillPresenter;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		refillPresenter.setTextFieldListener(listener);
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		showLayout();
	}
	
	public void showLayout() {
		getView().setLayoutActive();
		getParent().setBottomRight(layoutPresenter.getView());
	}
	
	public void showRefill() {
		getView().setRefillActive();
		refillPresenter.getView().refresh();
		getParent().setBottomRight(refillPresenter.getView());
	}

	@Override
	public void unregisterListeners() {
		layoutPresenter.unregister();
	}

}
