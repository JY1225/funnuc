package eu.robojob.millassist.ui.admin.device;

import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.ui.admin.device.gridplate.GeneralInfoGridPlateNode;
import eu.robojob.millassist.ui.admin.device.gridplate.GridHolePane;
import eu.robojob.millassist.ui.controls.OperationBox;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class GridPlateConfigureView extends AbstractFormView<GridPlateConfigurePresenter> {
	
	private Button btnEdit, btnNew;
	
	private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String EDIT_PATH = "M 15.71875,0 3.28125,12.53125 0,20 7.46875,16.71875 20,4.28125 C 20,4.28105 19.7362,2.486 18.625,1.375 17.5134,0.2634 15.71875,0 15.71875,0 z M 3.53125,12.78125 c 0,0 0.3421,-0.0195 1.0625,0.3125 C 4.85495,13.21295 5.1112,13.41 5.375,13.625 l 0.96875,0.96875 c 0.2258,0.2728 0.4471,0.5395 0.5625,0.8125 C 7.01625,15.66565 7.25,16.5 7.25,16.5 L 3,18.34375 C 2.5602,17.44355 2.55565,17.44 1.65625,17 l 1.875,-4.21875 z";

	private static final String EDIT = "GridPlateConfigureView.edit";
	private static final String NEW = "GridPlateConfigureView.new";
	static final String COPY = "GridPlateConfigureView.saveAs";
	static final String SAVE_AS_DIALOG = "GridPlateConfigureView.saveAsDialog";
	static final String NAME = "GridPlateConfigureView.name";
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	
	private GridHolePane gridHolePane; 
	private GeneralInfoGridPlateNode gridPlateGeneralInfo;
	private OperationBox operationBox;
	private ComboBox<GridPlate> cbbGridPlates;
		
	private BorderPane borderPane;
	
	@Override
	protected void build() {	
		gridPlateGeneralInfo = new GeneralInfoGridPlateNode();
		gridPlateGeneralInfo.setParent(this);
		gridHolePane = new GridHolePane();
		gridHolePane.setAlignment(Pos.CENTER);
		gridHolePane.setPadding(new Insets(0, 20, 0, 0));
		operationBox = new OperationBox();
		
		borderPane = new BorderPane();
		borderPane.setLeft(gridPlateGeneralInfo);
		borderPane.setRight(gridHolePane);
		
		borderPane.setBottom(operationBox);
		borderPane.setTop(getTitleBar());
		this.getChildren().add(borderPane);
		addActions();
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		gridHolePane.setTextFieldListener(listener);
		gridPlateGeneralInfo.setTextFieldListener(listener);
	}

	@Override
	public void refresh() {
		getPresenter().updateGridPlates();
		gridPlateGeneralInfo.refresh();
		operationBox.disableSaveButton(!gridPlateGeneralInfo.isConfigured());
		operationBox.disableSaveAsButton(!gridPlateGeneralInfo.isConfigured());
		operationBox.disableDeleteButton(true);
	}
	
	private HBox getTitleBar() {
		cbbGridPlates = new ComboBox<GridPlate>();
		cbbGridPlates.setPrefSize(UIConstants.COMBO_WIDTH-4, UIConstants.COMBO_HEIGHT);
		cbbGridPlates.setMinSize(UIConstants.COMBO_WIDTH-4, UIConstants.COMBO_HEIGHT);
		cbbGridPlates.setMaxSize(UIConstants.COMBO_WIDTH-4, UIConstants.COMBO_HEIGHT);
		HBox hboxButtons = new HBox();
		btnEdit = createButton(EDIT_PATH, null, Translator.getTranslation(EDIT), BTN_WIDTH, BTN_HEIGHT, null);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnEdit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				gridHolePane.reset();
				getPresenter().clickedEdit(cbbGridPlates.valueProperty().get());
				refresh();
				operationBox.disableDeleteButton(false);
				operationBox.disableSaveAsButton(false);
				disableEditButton();
			}
		});
		btnEdit.setDisable(true);
		btnNew = createButton(ADD_PATH, null, Translator.getTranslation(NEW), BTN_WIDTH, BTN_HEIGHT, null);
		btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		btnNew.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().clickedNew();
				cbbGridPlates.valueProperty().set(null);
				btnEdit.setDisable(true);
				operationBox.disableDeleteButton(true);
				operationBox.disableSaveButton(true);
				operationBox.disableSaveAsButton(true);
			}
		});
		hboxButtons.getChildren().addAll(btnEdit, btnNew);
		HBox hboxSelectGridPlate = new HBox();
		hboxSelectGridPlate.setSpacing(50);
		hboxSelectGridPlate.setAlignment(Pos.CENTER_LEFT);
		hboxSelectGridPlate.setPadding(new Insets(15,20,15,20));
		
		hboxSelectGridPlate.getChildren().addAll(cbbGridPlates, hboxButtons);
		return hboxSelectGridPlate;
	}
	
	private void addActions() {
		operationBox.addDeleteAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getPresenter().deleteGridPlate();
				getPresenter().updateGridPlates();
				reset();
			}
		});
		operationBox.addSaveAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getPresenter().saveData(gridPlateGeneralInfo.getName(), 
						gridPlateGeneralInfo.getPlateWidth(),
						gridPlateGeneralInfo.getPlateHeight(),
						gridPlateGeneralInfo.getPlateDepth(),
						gridPlateGeneralInfo.getOffsetX(), 
						gridPlateGeneralInfo.getOffsetY(),
						gridPlateGeneralInfo.getHoleLength(),
						gridPlateGeneralInfo.getHoleWidth(),
						gridHolePane.getGridHoles());
				refresh();
				getPresenter().disableEditMode();
			}
		});
		operationBox.addSaveAsAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getPresenter().saveAsData(gridPlateGeneralInfo.getPlateWidth(),
						gridPlateGeneralInfo.getPlateHeight(),
						gridPlateGeneralInfo.getPlateDepth(),
						gridPlateGeneralInfo.getOffsetX(), 
						gridPlateGeneralInfo.getOffsetY(),
						gridPlateGeneralInfo.getHoleLength(),
						gridPlateGeneralInfo.getHoleWidth(),
						gridHolePane.getGridHoles());
			}
		});
		cbbGridPlates.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GridPlate>() {
			@Override 
			public void changed(ObservableValue<? extends GridPlate> selected, GridPlate oldValue, GridPlate newValue) {
				btnEdit.setDisable(false);
			}
		});
	}
	
	public void setGridPlates(final Collection<GridPlate> gridPlates) {
		cbbGridPlates.getItems().clear();
		cbbGridPlates.getItems().addAll(gridPlates);
	}

	public void showGridPlate() {
		ThreadManager.submit(new Thread() {
			@Override
			public void run() {
				GridPlate gridPlate = new GridPlate(
						gridPlateGeneralInfo.getName(), 
						gridPlateGeneralInfo.getPlateWidth(),
						gridPlateGeneralInfo.getPlateHeight(), 
						gridHolePane.getGridHoles());
				gridPlate.setHoleLength(gridPlateGeneralInfo.getHoleLength());
				gridPlate.setHoleWidth(gridPlateGeneralInfo.getHoleWidth());
				getPresenter().showOverlayNode(gridPlate.createShape());
			}
		});
	}
	
	void disableEditButton() {
		btnEdit.setDisable(true);
	}
	
	void reset() {
		gridPlateGeneralInfo.reset();
		gridHolePane.reset();
		operationBox.disableDeleteButton(true);
		operationBox.disableSaveAsButton(true);
		operationBox.disableSaveButton(true);
	}

	public void gridPlateSelected(GridPlate selectedGridPlate) {
		gridPlateGeneralInfo.setGridPlate(selectedGridPlate);
		gridHolePane.setGridHoles(selectedGridPlate.getGridHoles());
	}
	
	public void validate(boolean isConfigured) {
		operationBox.disableSaveButton(!isConfigured);
	}
}
