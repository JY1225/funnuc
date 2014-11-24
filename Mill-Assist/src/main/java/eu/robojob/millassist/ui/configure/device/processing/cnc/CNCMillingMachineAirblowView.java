package eu.robojob.millassist.ui.configure.device.processing.cnc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.controls.CoordinateBox;
import eu.robojob.millassist.ui.controls.IconFlowSelector;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;

@Deprecated
public class CNCMillingMachineAirblowView extends AbstractFormView<CNCMillingMachineAirblowPresenter> {

	private CheckBox cbAirblowPut, cbAirblowPick;
	private Label lblCbAirblowPut, lblCbAirblowPick;
	private CoordinateBox coordBoxPutBottom, coordBoxPutTop, coordBoxPickBottom, coordBoxPickTop;
	private IconFlowSelector ifsClamping;
	
	private static final int HGAP = 15;
	private static final int VGAP = 10;
	private static final int MAX_INTEGER_LENGTH = 6;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	
	private static final String AIRBLOW_PICK = "CNCMillingMachineAirblowView.configurePick";
	private static final String AIRBLOW_PUT = "CNCMillingMachineAirblowView.configurePut";
	
	public CNCMillingMachineAirblowView() {
		build();
	}
	
	@Override
	protected void build() {
		initComponents();
		getContents().setVgap(VGAP);
		getContents().setHgap(HGAP);
		addActions();
				
		int column = 0;
		int row = 0;
		getContents().add(ifsClamping, column, row, 4, 1);
		
		column = 0;
		row++;
		getContents().add(cbAirblowPut, column++, row,1,2);
		getContents().add(lblCbAirblowPut, column++, row,1,2);
		getContents().add(coordBoxPutBottom, column, row++);
		getContents().add(coordBoxPutTop, column, row);
		
		column = 0;
		row++;
		getContents().add(cbAirblowPick, column++, row,1,2);
		getContents().add(lblCbAirblowPick, column++, row,1,2);
		getContents().add(coordBoxPickBottom, column, row++);
		getContents().add(coordBoxPickTop, column, row);	
	}
	
	private void initComponents() {
		cbAirblowPut = new CheckBox();
		lblCbAirblowPut = new Label(Translator.getTranslation(AIRBLOW_PUT));
		cbAirblowPick = new CheckBox();
		lblCbAirblowPick = new Label(Translator.getTranslation(AIRBLOW_PICK));
		
		coordBoxPutBottom  = new CoordinateBox(MAX_INTEGER_LENGTH, "X", "Y", "Z");
		coordBoxPutTop  = new CoordinateBox(MAX_INTEGER_LENGTH, "X", "Y", "Z");
		coordBoxPickBottom  = new CoordinateBox(MAX_INTEGER_LENGTH, "X", "Y", "Z");
		coordBoxPickTop  = new CoordinateBox(MAX_INTEGER_LENGTH, "X", "Y", "Z");
		
		ifsClamping = new IconFlowSelector(true);
		ifsClamping.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
	}
	
	private void addActions() {
		cbAirblowPut.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				showPutCoords(newValue);
				getPresenter().setDoMachineAirblow(newValue, true);
			}
		});
		cbAirblowPick.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				showPickCoords(newValue);
				getPresenter().setDoMachineAirblow(newValue, false);
			}
		});
		coordBoxPutTop.addChangeListeners(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observableValue, final Float oldValue, final Float newValue) {
				coordBoxPutTop.updateCoordinate();
				getPresenter().changedCoordinate(true);
			}
		});
		coordBoxPutBottom.addChangeListeners(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observableValue, final Float oldValue, final Float newValue) {
				coordBoxPutBottom.updateCoordinate();
				getPresenter().changedCoordinate(true);
			}
		});
		coordBoxPickTop.addChangeListeners(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observableValue, final Float oldValue, final Float newValue) {
				coordBoxPickTop.updateCoordinate();
				getPresenter().changedCoordinate(false);
			}
		});
		coordBoxPickBottom.addChangeListeners(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observableValue, final Float oldValue, final Float newValue) {
				coordBoxPickBottom.updateCoordinate();
				getPresenter().changedCoordinate(false);
			}
		});
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		coordBoxPutTop.setTextFieldListener(listener);
		coordBoxPutBottom.setTextFieldListener(listener);
		coordBoxPickTop.setTextFieldListener(listener);
		coordBoxPickBottom.setTextFieldListener(listener);
	}

	@Override
	public void refresh() {
		hideNotification();
		refreshClampings();
		getPresenter().setMachineAirblow();
		refreshCoordBoxes();
		getPresenter().isConfigured();
	}
	
	private void refreshClampings() {
		ifsClamping.clearItems();
		int itemIndex = 0;
		for (final Clamping clamping: getPresenter().getActiveClampings()) {
			ifsClamping.addItem(itemIndex, clamping.getName(), clamping.getImageUrl(), clamping.getFixtureType().toShortString(), new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent arg0) {
					selectClamping(clamping.getName());
				}
			});
			itemIndex++;
		}
	}
	
	private void selectClamping(String clampingName) {
		ifsClamping.setSelected(clampingName);
		if (cbAirblowPut.isSelected()) {
			getPresenter().changedClamping(clampingName, true);
		}
		if (cbAirblowPick.isSelected()) {
			getPresenter().changedClamping(clampingName, false);
		}
	}
	
	private void showClampingSelector() {
		if (cbAirblowPut.isSelected() || cbAirblowPick.isSelected()) {
			ifsClamping.setDisable(false);
			selectClamping(ifsClamping.first());
		} else {
			ifsClamping.setDisable(true);
			ifsClamping.deselectAll();
		}
	}
	
	private void showPutCoords(boolean putRobotAirblow) {
		coordBoxPutBottom.setVisible(putRobotAirblow);
		coordBoxPutTop.setVisible(putRobotAirblow);
		coordBoxPutBottom.setManaged(putRobotAirblow);
		coordBoxPutTop.setManaged(putRobotAirblow);
		showClampingSelector();
	}
	
	private void showPickCoords(boolean pickRobotAirblow) {
		coordBoxPickBottom.setVisible(pickRobotAirblow);
		coordBoxPickTop.setVisible(pickRobotAirblow);
		coordBoxPickBottom.setManaged(pickRobotAirblow);
		coordBoxPickTop.setManaged(pickRobotAirblow);
		showClampingSelector();
	}
	
	void setBottomCoord(Coordinates coord, boolean isPut) {
		if (isPut) {
			coordBoxPutBottom.setCoordinate(coord);
		} else {
			coordBoxPickBottom.setCoordinate(coord);
		}
	}
	
	void setTopCoord(Coordinates coord, boolean isPut) {
		if (isPut) {
			coordBoxPutTop.setCoordinate(coord);
		} else {
			coordBoxPickTop.setCoordinate(coord);
		}
	}

	void refreshCoordBoxes() {
		coordBoxPickBottom.reset();
		coordBoxPickTop.reset();
		coordBoxPutBottom.reset();
		coordBoxPutTop.reset();
	}

	void setPutAirblowSelected(boolean isActive) {
		cbAirblowPut.setSelected(isActive);
		showPutCoords(isActive);
	}

	void setPickAirblowSelected(boolean isActive) {
		cbAirblowPick.setSelected(isActive);
		showPickCoords(isActive);
	}
	
}
