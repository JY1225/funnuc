package eu.robojob.millassist.ui.automate.device.stacking.conveyor;

import eu.robojob.millassist.ui.automate.AbstractMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.AbstractWorkPieceLayoutPresenter;

public class ConveyorMenuPresenter extends AbstractMenuPresenter<ConveyorMenuView> {

	private AbstractWorkPieceLayoutPresenter<?, ConveyorMenuPresenter> layoutPresenter;
	private ConveyorAmountsPresenter amountsPresenter;
	
	public ConveyorMenuPresenter(final ConveyorMenuView view, final AbstractWorkPieceLayoutPresenter<?, ConveyorMenuPresenter> layoutPresenter, 
			final ConveyorAmountsPresenter amountsPresenter) {
		super(view);
		view.build();
		this.layoutPresenter = layoutPresenter;
		this.amountsPresenter = amountsPresenter;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		layoutPresenter.setTextFieldListener(listener);
		amountsPresenter.setTextFieldListener(listener);
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
		getParent().setBottomRight(layoutPresenter.getView());
		getView().setLayoutActive();
	}
	
	public void showAmounts() {
		amountsPresenter.getView().refresh();
		getParent().setBottomRight(amountsPresenter.getView());
		getView().setAmountsActive();
	}

	@Override
	public void unregisterListeners() {
		layoutPresenter.unregister();
	}

}
