package eu.robojob.millassist.ui.admin.device;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.util.Translator;

public class DeviceMenuView extends AbstractMenuView<DeviceMenuPresenter> {

	private static final String USERFRAMES = "DeviceMenuView.userframes";
	private static final String BASICSTACKPLATE = "DeviceMenuView.basicStackPlate";
	private static final String PRAGE = "DeviceMenuView.prage";
	private static final String CNCMACHINE = "DeviceMenuView.cncMachine";
	private static final String CNCMACHINE_CLAMPINGS = "DeviceMenuView.cncMachineClampings";
	private static final String OUTPUT_BIN = "DeviceMenuView.outputBin";
	
	public DeviceMenuView() {
		build();
	}
	
	@Override
	protected void build() {
		this.getStyleClass().add("admin-menu");
		setPrefWidth(150);
		setMinWidth(150);
		setMaxWidth(150);
		int index = 0;
		addTextMenuItem(index++, Translator.getTranslation(USERFRAMES), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureUserFrames();
			}
		});
		addTextMenuItem(index++, Translator.getTranslation(BASICSTACKPLATE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureBasicStackPlate();
			}
		});
		addTextMenuItem(index++, Translator.getTranslation(CNCMACHINE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureCNCMachine();
			}
		});
		addTextMenuItem(index++, Translator.getTranslation(CNCMACHINE_CLAMPINGS), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureCNCMachineClampings();
			}
		});
		addTextMenuItem(index++, Translator.getTranslation(PRAGE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configurePrage();
			}
		});
		addTextMenuItem(index++, Translator.getTranslation(OUTPUT_BIN), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureOutputBin();
			}
		});
	}
	
	public void disablePrageMenuItem() {
		getMenuItem(4).setVisible(false);
		getMenuItem(4).setManaged(false);
	}
	
	public void disableBasicStackPlateMenuItem() {
		getMenuItem(1).setVisible(false);
		getMenuItem(1).setManaged(false);
	}
	
	public void disableBinMenuItem() {
		getMenuItem(5).setVisible(false);
		getMenuItem(5).setManaged(false);
	}
	
	public void setConfigureUserFramesActive() {
		setMenuItemSelected(0);
	}
	
	public void setConfigureBasicStackPlateActive() {
		setMenuItemSelected(1);
	}
	
	public void setConfigureCNCMachineActive() {
		setMenuItemSelected(2);
	}
	
	public void setConfigureClampingsActive() {
		setMenuItemSelected(3);
	}
	
	public void setConfigurePrageActive() {
		setMenuItemSelected(4);
	}
	
	public void setConfigureOutputBinActive() {
		setMenuItemSelected(5);
	}

}
