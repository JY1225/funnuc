package eu.robojob.millassist.ui.automate.device.stacking.stackplate;

import eu.robojob.millassist.ui.automate.AbstractMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class BasicStackPlateMenuPresenter extends AbstractMenuPresenter<BasicStackPlateMenuView> {

	private BasicStackPlateLayoutPresenter layoutPresenter;
	private BasicStackPlateReplacePresenter refillPresenter;
	private BasicStackPlateAddPresenter addPresenter;
	
	public BasicStackPlateMenuPresenter(final BasicStackPlateMenuView view, final BasicStackPlateLayoutPresenter layoutPresenter,
			final BasicStackPlateReplacePresenter refillPresenter, final BasicStackPlateAddPresenter addPresenter) {
		super(view);
		view.build();
		this.layoutPresenter = layoutPresenter;
		this.refillPresenter = refillPresenter;
		this.addPresenter = addPresenter;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		refillPresenter.setTextFieldListener(listener);
		addPresenter.setTextFieldListener(listener);
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
	
	public void showAdd() {
		getView().setAddActive();
		addPresenter.getView().refresh();
		getParent().setBottomRight(addPresenter.getView());
	}

	@Override
	public void unregisterListeners() {
		layoutPresenter.unregister();
	}

}
