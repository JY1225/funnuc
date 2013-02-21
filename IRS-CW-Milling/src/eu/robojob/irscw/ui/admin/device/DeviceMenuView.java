package eu.robojob.irscw.ui.admin.device;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.irscw.ui.general.AbstractMenuView;
import eu.robojob.irscw.util.Translator;

public class DeviceMenuView extends AbstractMenuView<DeviceMenuPresenter> {

	private static final String USERFRAMES = "DeviceMenuView.userframes";
	private static final String BASICSTACKPLATE = "DeviceMenuView.basicStackPlate";
	private static final String PRAGE = "DeviceMenuView.prage";
	private static final String CNCMACHINE = "DeviceMenuView.cncMachine";
	private static final String CNCMACHINE_CLAMPINGS = "DeviceMenuView.cncMachineClampings";
	
	public DeviceMenuView() {
		build();
	}
	
	@Override
	protected void build() {
		setPrefWidth(170);
		setMinWidth(170);
		setMaxWidth(170);
		addTextMenuItem(0, Translator.getTranslation(USERFRAMES), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureUserFrames();
			}
		});
		addTextMenuItem(1, Translator.getTranslation(BASICSTACKPLATE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureBasicStackPlate();
			}
		});
		addTextMenuItem(2, Translator.getTranslation(CNCMACHINE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureCNCMachine();
			}
		});
		addTextMenuItem(3, Translator.getTranslation(CNCMACHINE_CLAMPINGS), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureCNCMachineClampings();
			}
		});
		addTextMenuItem(4, Translator.getTranslation(PRAGE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configurePrage();
			}
		});
	}

}
