package eu.robojob.irscw.ui.automate.device.stacking;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.irscw.ui.general.AbstractMenuView;
import eu.robojob.irscw.util.Translator;

public class BasicStackPlateMenuView extends AbstractMenuView<BasicStackPlateMenuPresenter> {
	
	private static final String LAYOUT_ICON = "m 4.3125,0.12499999 c -1.9028305,0 -3.5,1.56110911 -3.5,3.50000001 0,1.938891 1.5971695,3.5 3.5,3.5 1.9028305,0 3.46875,-1.561109 3.46875,-3.5 0,-1.9388909 -1.5659195,-3.50000001 -3.46875,-3.50000001 z m 12.125,0 c -1.90283,0 -3.46875,1.56110911 -3.46875,3.50000001 0,1.938891 1.56592,3.5 3.46875,3.5 1.902831,0 3.5,-1.561109 3.5,-3.5 0,-1.9388909 -1.597169,-3.50000001 -3.5,-3.50000001 z M 4.3125,1.3125 c 1.309224,0 2.3125,1.0393364 2.3125,2.3125 0,1.273164 -1.003276,2.3125 -2.3125,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.2731636 1.034526,-2.3125 2.34375,-2.3125 z m 12.125,0 c 1.309224,0 2.34375,1.0393364 2.34375,2.3125 0,1.273164 -1.034526,2.3125 -2.34375,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.2731636 1.034526,-2.3125 2.34375,-2.3125 z m -12.125,9.03125 c -2.4090408,0 -4.375,1.965959 -4.375,4.375 0,2.409041 1.9659592,4.34375 4.375,4.34375 2.4090408,0 4.34375,-1.934709 4.34375,-4.34375 0,-2.409041 -1.9347092,-4.375 -4.34375,-4.375 z m 12.125,0.875 c -1.90283,0 -3.46875,1.561109 -3.46875,3.5 0,1.938891 1.56592,3.46875 3.46875,3.46875 1.902831,0 3.5,-1.529859 3.5,-3.46875 0,-1.938891 -1.597169,-3.5 -3.5,-3.5 z m 0,1.15625 c 1.309224,0 2.34375,1.070586 2.34375,2.34375 0,1.273164 -1.034526,2.3125 -2.34375,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.273164 1.034526,-2.34375 2.34375,-2.34375 z";
	private static final String REFILL_ICON = "M 4.59375 1.9375 L 0 7.28125 L 3.0625 7.28125 L 3.0625 11.09375 L 3.0625 14.15625 L 6.15625 14.15625 L 12.28125 14.15625 L 9.21875 11.09375 L 6.125 11.09375 L 6.125 7.28125 L 9.21875 7.28125 L 4.59375 1.9375 z M 8.4375 1.9375 L 11.5 5 L 14.5625 5 L 14.5625 8.8125 L 11.5 8.8125 L 16.09375 14.15625 L 20.71875 8.8125 L 17.625 8.8125 L 17.625 5 L 17.625 1.9375 L 14.5625 1.9375 L 8.4375 1.9375 z";
	private static final String VIEW_LAYOUT = "StackingDeviceMenuView.viewLayout";
	private static final String REFILL = "StackingDeviceMenuView.refill";

	@Override
	protected void build() {
		addMenuItem(0, LAYOUT_ICON, Translator.getTranslation(VIEW_LAYOUT), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().showLayout();
			}
		});
		addMenuItem(1, REFILL_ICON, Translator.getTranslation(REFILL), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().showRefill();
			}
		});
	}
	
	public void setLayoutActive() {
		setMenuItemSelected(0);
	}
	
	public void setRefillActive() {
		setMenuItemSelected(1);
	}
}
