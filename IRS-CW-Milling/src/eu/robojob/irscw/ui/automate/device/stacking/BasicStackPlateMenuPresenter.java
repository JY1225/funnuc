package eu.robojob.irscw.ui.automate.device.stacking;

import eu.robojob.irscw.ui.automate.AbstractMenuPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

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
		layoutPresenter.getView().refresh();
		getParent().setBottomRight(layoutPresenter.getView());
		getView().setLayoutActive();
	}
	
	public void showRefill() {
		refillPresenter.getView().refresh();
		getParent().setBottomRight(refillPresenter.getView());
		getView().setRefillActive();
	}

}
