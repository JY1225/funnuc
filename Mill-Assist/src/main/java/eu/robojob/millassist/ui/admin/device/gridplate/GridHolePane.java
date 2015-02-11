package eu.robojob.millassist.ui.admin.device.gridplate;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridHole;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * GridPlate Point view exists of a table containing all the holes of the gridplate. Beneath the table there are 3 
 * textboxes which containing the X,Y,R values for a given hole. 
 *
 */
public class GridHolePane extends GridPane {

	private TableView<GridHole> table;
	private NumericTextField numtxtX, numtxtY, numtxtR;
	private Button addButton;
	private final ObservableList<GridHole> data = FXCollections.observableArrayList();
	private static final String GRIDTABLE_PLACEHOLDER = "GridHolePane.gridTablePlaceHolder";
	
	private static final String CSS_CLASS_TABLE = "table";
		
	public GridHolePane() {
		initComponents();
		build();
	}
	
	private void build() {
		getTable();
		add(table, 0,0);
		add(getButtonBox(), 0,1);
	}
	
	private void initComponents() {	
		numtxtX = new NumericTextField(5);
		numtxtY = new NumericTextField(5);
		numtxtR = new NumericTextField(5);
		table = new TableView<GridHole>();
	}
	
	private HBox getButtonBox() {
		HBox buttonBox = new HBox();
		numtxtX.setPrefHeight(35);
		numtxtY.setPrefHeight(35);
		numtxtR.setPrefHeight(35);
		addButton = AbstractFormView.createButton("OK", 70, 35, new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if (numtxtR.getValue() >= 0 && numtxtR.getValue() <= 180) {
					GridHole newGridHole = new GridHole(numtxtX.getValue(), numtxtY.getValue(), numtxtR.getValue());
					data.add(newGridHole);
				} 
			}
		});
		buttonBox.setSpacing(10);
		buttonBox.getChildren().addAll(numtxtX, numtxtY, numtxtR, addButton);
		buttonBox.setPadding(new Insets(10, 0, 10, 0));
		return buttonBox;
	}
	
	private void getTable() {
		table.setItems(data);
		table.setEditable(false);
		TableColumn<GridHole, Float> xColumn = new TableColumn<GridHole, Float>("X");
		xColumn.setResizable(false);
		xColumn.setCellValueFactory(new Callback<CellDataFeatures<GridHole, Float>, ObservableValue<Float>>() {
		     public ObservableValue<Float> call(final CellDataFeatures<GridHole, Float> p) {
		         return new ReadOnlyObjectWrapper<Float>(p.getValue().getX());
		     }
		  });
		TableColumn<GridHole, Float> yColumn = new TableColumn<GridHole, Float>("Y");
		yColumn.setResizable(false);
		yColumn.setCellValueFactory(new Callback<CellDataFeatures<GridHole, Float>, ObservableValue<Float>>() {
		     public ObservableValue<Float> call(final CellDataFeatures<GridHole, Float> p) {
		         return new ReadOnlyObjectWrapper<Float>(p.getValue().getY());
		     }
		  });
		TableColumn<GridHole, Float> rColumn = new TableColumn<GridHole, Float>("R (°)");
		rColumn.setResizable(false);
		rColumn.setCellValueFactory(new Callback<CellDataFeatures<GridHole, Float>, ObservableValue<Float>>() {
		     public ObservableValue<Float> call(final CellDataFeatures<GridHole, Float> p) {
		         return new ReadOnlyObjectWrapper<Float>(p.getValue().getAngle());
		     }
		  });
		TableColumn<GridHole, GridHole> deleteHole = new TableColumn<GridHole, GridHole>();
		deleteHole.setResizable(false);
		deleteHole.setCellValueFactory(new Callback<CellDataFeatures<GridHole, GridHole>, ObservableValue<GridHole>>() {

			@Override
			public ObservableValue<GridHole> call(CellDataFeatures<GridHole, GridHole> p) {
				return new ReadOnlyObjectWrapper<GridHole>(p.getValue());
			}
			
	    });
		deleteHole.setCellFactory(new Callback<TableColumn<GridHole,GridHole>, TableCell<GridHole,GridHole>>() {

			@Override
			public TableCell<GridHole, GridHole> call(TableColumn<GridHole, GridHole> arg0) {
				return new DeleteButton();
			}
				
		});	
		deleteHole.setMinWidth(100);
		deleteHole.setMaxWidth(100);
		deleteHole.setPrefWidth(100);
		rColumn.setMinWidth(70);
		rColumn.setMaxWidth(70);
		rColumn.setPrefWidth(70);
		xColumn.setMinWidth(70);
		xColumn.setMaxWidth(70);
		xColumn.setPrefWidth(70);
		yColumn.setMinWidth(70);
		yColumn.setMaxWidth(70);
		yColumn.setPrefWidth(70);
				
		table.getColumns().add(xColumn);
		table.getColumns().add(yColumn);
		table.getColumns().add(rColumn);
		table.getColumns().add(deleteHole);
		table.getStyleClass().add(CSS_CLASS_TABLE);
		table.setTableMenuButtonVisible(false);	
		table.getSelectionModel().setCellSelectionEnabled(false);
		table.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				if ((newValue != null) && (newValue.intValue() >= 0)) {
					GridHole hole = table.getSelectionModel().getSelectedItem();
					numtxtX.setText("" +hole.getX());
					numtxtY.setText("" +hole.getY());
					numtxtR.setText("" +hole.getAngle());
				}
			}
		});
		Label placeHolder = new Label(Translator.getTranslation(GRIDTABLE_PLACEHOLDER));
		placeHolder.setWrapText(true);
		table.setPlaceholder(placeHolder);
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		numtxtX.setFocusListener(listener);
		numtxtY.setFocusListener(listener);
		numtxtR.setFocusListener(listener);
	}
	
	public SortedSet<GridHole> getGridHoles() {
		return new TreeSet<GridHole>(data);
	}
	
	public void reset() {
		table.getItems().clear();
		numtxtX.setText("");
		numtxtY.setText("");
		numtxtR.setText("");
	}
	
	public void setGridHoles(Set<GridHole> gridHoles) {
		table.getItems().addAll(gridHoles);
	}
	
	private class DeleteButton extends TableCell<GridHole, GridHole> {
		
		static final String CSS_CLASS_DELETE_OPEN_BTN = "delete-open";
		final Button deleteButton;
		final String deleteIconPath = "M 3.6875 1.5 C 3.6321551 1.5180584 3.5743859 1.5512948 3.53125 1.59375 L 1.59375 3.53125 C 1.423759 3.7005604 1.4751389 4.0379792 1.6875 4.25 L 7.46875 10 L 1.6875 15.75 C 1.4751389 15.962191 1.423759 16.297908 1.59375 16.46875 L 3.53125 18.40625 C 3.7020918 18.577772 4.0386598 18.55526 4.25 18.34375 L 10 12.5625 L 15.75 18.34375 C 15.962021 18.55526 16.297908 18.577772 16.46875 18.40625 L 18.40625 16.46875 C 18.57556 16.297908 18.524691 15.962191 18.3125 15.75 L 12.53125 10 L 18.3125 4.25 C 18.524691 4.0379792 18.57556 3.7005604 18.40625 3.53125 L 16.46875 1.59375 C 16.297908 1.4244396 15.962021 1.4759897 15.75 1.6875 L 10 7.46875 L 4.25 1.6875 C 4.1439896 1.5809791 4.0004088 1.5136129 3.875 1.5 C 3.8124658 1.4931936 3.7428449 1.4819416 3.6875 1.5 z ";
		GridHole p;
		
		DeleteButton() {
			deleteButton = AbstractFormView.createButton(deleteIconPath, CSS_CLASS_DELETE_OPEN_BTN, "", 32, 0, new EventHandler<ActionEvent>() {
				 @Override 
				 public void handle(ActionEvent actionEvent) {
					 table.getItems().remove(p);
				 }
			});
		}
		
		@Override
		protected void updateItem(GridHole item, boolean empty) {
			super.updateItem(item, empty);
			if(!empty) {
				setGraphic(deleteButton);
				this.p = item;
			}
				
		}
	}
	
}
