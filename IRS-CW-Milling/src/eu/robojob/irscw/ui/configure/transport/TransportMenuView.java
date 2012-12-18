package eu.robojob.irscw.ui.configure.transport;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.irscw.ui.configure.AbstractMenuView;
import eu.robojob.irscw.ui.general.model.TransportInformation;

public class TransportMenuView extends AbstractMenuView<TransportMenuPresenter> {

	protected TransportInformation transportInfo;
	
	private static final String iconGrip = "M 10.6875 -0.34375 L -0.125 -0.15625 L 11.21875 6.6875 L 8.875 9.9375 L 9.03125 11.5625 L 10.84375 12.09375 L 13.1875 12.28125 L 13.375 11.1875 L 12.3125 10.46875 L 13.375 7.59375 L 16.78125 6.15625 L 17.53125 8.6875 L 18.25 8.125 L 18.59375 5.0625 L 16.78125 3.46875 L 13.125 4.125 L 10.6875 -0.34375 z M 17.84375 11.1875 C 16.72967 11.1875 15.84375 12.07342 15.84375 13.1875 C 15.84375 14.301579 16.72967 15.21875 17.84375 15.21875 C 18.957829 15.21875 19.875 14.301579 19.875 13.1875 C 19.875 12.07342 18.957829 11.1875 17.84375 11.1875 z";
	private static final String iconIntervention = "M 10 0 C 4.4775 0 0 4.4762499 0 10 C 0 15.52125 4.4775 20 10 20 C 15.5225 20 20 15.52125 20 10 C 20 4.4762499 15.5225 -5.7824116e-019 10 0 z M 6.625 5.5 L 8.875 5.5 L 8.875 14.5 L 6.625 14.5 L 6.625 5.5 z M 11.125 5.5 L 13.375 5.5 L 13.375 14.5 L 11.125 14.5 L 11.125 5.5 z";
	public TransportMenuView(TransportInformation transportInfo) {
		this.transportInfo = transportInfo;
		build();
	}
	
	@Override
	protected void build() {
		addMenuItem(0, iconGrip, translator.getTranslation("ConfigGripers"), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.configureGriper();
			}
		});
		addMenuItem(1, iconIntervention, translator.getTranslation("ConfigInterventions"), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.configureInterventions();
			}
		});
	}
	
	public void setConfigureGripperActive() {
		setMenuItemSelected(0);
	}
	
	public void setConfigureInterventionsActive() {
		setMenuItemSelected(1);
	}

}
