package eu.robojob.millassist.ui.automate.device.stacking.conveyor.eaton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.util.Translator;

public class ConveyorMenuView extends AbstractMenuView<ConveyorMenuPresenter> {

	private static final String LAYOUT_ICON = "m 14.78125,99.3125 -0.65625,0 0,2.46875 1.34375,0 0,-1.78125 1.75,0 0,-1.375 -2.4375,0 z M 19.9375,100 l 2.6875,0 0,-1.375 -2.6875,0 z m 5.375,0 2.71875,0 0,-1.375 -2.71875,0 z m 5.40625,0 1.3125,0 0,0.0312 1.34375,0 0,-0.71875 0,-0.6875 -0.6875,0 -1.96875,0 z m 1.3125,5.4375 1.34375,0 0,-2.6875 -1.34375,0 z m -17.90625,1.75 1.34375,0 0,-2.71875 -1.34375,0 z m 17.90625,1.65625 -0.65625,0 0,1.34375 1.3125,0 0.6875,0 0,-0.6875 0,-1.34375 -1.34375,0 z m -16.875,1.34375 2.71875,0 0,-1.34375 -2.71875,0 z m 5.40625,0 2.6875,0 0,-1.34375 -2.6875,0 z m 5.40625,0 2.6875,0 0,-1.34375 -2.6875,0 z";
	private static final String AMOUNTS_ICON = "M 4.59375 1.9375 L 0 7.28125 L 3.0625 7.28125 L 3.0625 11.09375 L 3.0625 14.15625 L 6.15625 14.15625 L 12.28125 14.15625 L 9.21875 11.09375 L 6.125 11.09375 L 6.125 7.28125 L 9.21875 7.28125 L 4.59375 1.9375 z M 8.4375 1.9375 L 11.5 5 L 14.5625 5 L 14.5625 8.8125 L 11.5 8.8125 L 16.09375 14.15625 L 20.71875 8.8125 L 17.625 8.8125 L 17.625 5 L 17.625 1.9375 L 14.5625 1.9375 L 8.4375 1.9375 z";
	private static final String VIEW_LAYOUT = "ConveyorMenuView.viewLayout";
	private static final String AMOUNTS = "ConveyorMenuView.amounts";

	@Override
	protected void build() {
		addMenuItem(0, LAYOUT_ICON, Translator.getTranslation(VIEW_LAYOUT), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().showLayout();
			}
		});
		addMenuItem(1, AMOUNTS_ICON, Translator.getTranslation(AMOUNTS), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().showAmounts();
			}
		});
	}

	public void setLayoutActive() {
		setMenuItemSelected(0);
	}
	
	public void setAmountsActive() {
		setMenuItemSelected(1);
	}
	
}
