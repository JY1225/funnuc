package eu.robojob.millassist.ui.configure.process;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class ProcessOpenView extends AbstractFormView<ProcessOpenPresenter> {
	
	private Label lblName;
	private FullTextField fulltxtFilter;
	private TableView<ProcessFlow> table;
	private Button btnLoad;
	
	private static String openIconPath = "M 10 0 C 4.4775 0 0 4.4762499 0 10 C 0 15.52125 4.4775 20 10 20 C 15.5225 20 20 15.52125 20 10 C 20 4.4762499 15.5225 0 10 0 z M 10 5 L 14.96875 9.96875 L 11.25 9.96875 L 11.25 15 L 8.75 15 L 8.75 9.96875 L 5 9.96875 L 10 5 z ";
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	private static final int TOP_MARGIN = 10;
	
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3.5;
	private static final int MAX_FILTER_LENGTH = 25;
	private static final int NAME_TEXTFIELD_WIDTH = 250;

	private static final String CSS_CLASS_BUTTON_LOAD = "btn-load";
	private static final String CSS_CLASS_TABLE = "table";
	
	private static final String LOAD = "ProcessOpenView.load";
	private static final String NAME = "ProcessOpenView.name";
	private static final String LAST_OPENED_DATE = "ProcessOpenView.lastOpenedDate";
	private static final String NO_CONTENT = "ProcessOpenView.noContent";
		
	public ProcessOpenView() {
		super();
	}

	@Override
	protected void build() {
		
		getContents().setVgap(VGAP);
		getContents().setHgap(HGAP);
		
		fulltxtFilter = new FullTextField(MAX_FILTER_LENGTH);
		fulltxtFilter.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		fulltxtFilter.setPrefWidth(NAME_TEXTFIELD_WIDTH);
		fulltxtFilter.setNotifyEveryChange(true);
		fulltxtFilter.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String oldValue, final String newValue) {
				getPresenter().filterChanged(newValue);
			}
		});
		
		table = new TableView<ProcessFlow>();
		table.setEditable(false);
		TableColumn<ProcessFlow, String> nameColumn = new TableColumn<ProcessFlow, String>(Translator.getTranslation(NAME));
		nameColumn.setCellValueFactory(new Callback<CellDataFeatures<ProcessFlow, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(final CellDataFeatures<ProcessFlow, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getName());
		     }
		  });
		TableColumn<ProcessFlow, String> lastOpenedColumn = new TableColumn<ProcessFlow, String>(Translator.getTranslation(LAST_OPENED_DATE));
		lastOpenedColumn.setCellValueFactory(new Callback<CellDataFeatures<ProcessFlow, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(final CellDataFeatures<ProcessFlow, String> p) {
		    	 if (p.getValue().getLastOpened() == null) {
		    		 return null;
		    	 } else {
		    		 Date date = new Date(p.getValue().getLastOpened().getTime());
		    		 return new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd, HH:mm").format(date));
		    	 }
		     }
		  });
		TableColumn<ProcessFlow, ProcessFlow> deleteProcessColumn = new TableColumn<ProcessFlow, ProcessFlow>();
		deleteProcessColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProcessFlow,ProcessFlow>, ObservableValue<ProcessFlow>>() {

			@Override
			public ObservableValue<ProcessFlow> call(CellDataFeatures<ProcessFlow, ProcessFlow> p) {
				return new ReadOnlyObjectWrapper<ProcessFlow>(p.getValue());
			}
			
	    });
		deleteProcessColumn.setCellFactory(new Callback<TableColumn<ProcessFlow,ProcessFlow>, TableCell<ProcessFlow,ProcessFlow>>() {

			@Override
			public TableCell<ProcessFlow, ProcessFlow> call(TableColumn<ProcessFlow, ProcessFlow> arg0) {
				return new DeleteButton();
			}
				
		});
		nameColumn.setMinWidth(240);
		nameColumn.setMaxWidth(240);
		nameColumn.setPrefWidth(240);
		nameColumn.setResizable(false);
		lastOpenedColumn.setPrefWidth(170);
		lastOpenedColumn.setMinWidth(170);
		lastOpenedColumn.setMaxWidth(170);
		lastOpenedColumn.setResizable(false);
		deleteProcessColumn.setMinWidth(90);
		deleteProcessColumn.setMaxWidth(90);
		deleteProcessColumn.setPrefWidth(90);
		deleteProcessColumn.setResizable(false);
		table.getColumns().add(nameColumn);
		table.getColumns().add(lastOpenedColumn);
		table.getColumns().add(deleteProcessColumn);
		table.setPrefSize(502, 230);
		table.setMinSize(502, 160);
		table.setMaxSize(502, 230);
		table.setTableMenuButtonVisible(false);	
		table.getStyleClass().add(CSS_CLASS_TABLE);
		table.setPlaceholder(new Label(Translator.getTranslation(NO_CONTENT)));
		table.setTableMenuButtonVisible(false);
		btnLoad = createButton(openIconPath, CSS_CLASS_BUTTON_LOAD, Translator.getTranslation(LOAD), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().openProcess(table.getSelectionModel().selectedItemProperty().getValue());
			}
		});
		table.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(final ObservableValue<? extends Number> arg0, final Number oldValue, final Number newValue) {
				if ((newValue != null) && (newValue.intValue() >= 0)) {
					btnLoad.setDisable(false);
				} else {
					btnLoad.setDisable(true);
				}
			}
			
		});
		btnLoad.setDisable(true);
		table.getSelectionModel().setCellSelectionEnabled(false);
		HBox hbox = new HBox();
		lblName = new Label(Translator.getTranslation(NAME));
		lblName.getStyleClass().addAll(CSS_CLASS_FORM_LABEL);
		hbox.getChildren().add(lblName);
		hbox.getChildren().add(fulltxtFilter);
		hbox.setPrefWidth(350);
		HBox hbox2 = new HBox();
		hbox2.getChildren().add(btnLoad);
		hbox.setSpacing(VGAP);
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox2.setAlignment(Pos.CENTER_RIGHT);
		hbox2.setPrefWidth(150);
		HBox totalHBox = new HBox();
		totalHBox.getChildren().add(hbox);
		totalHBox.getChildren().add(hbox2);
		int row = 0;
		int column = 0;
		getContents().add(totalHBox, column++, row);
		row++;
		column = 0;		
		getContents().add(table, column++, row);
		HBox.setMargin(hbox, new Insets(TOP_MARGIN, 0, 0, 0));
		HBox.setMargin(hbox2, new Insets(TOP_MARGIN, 0, 0, 0));
	}
	
	public String getFilter() {
		return fulltxtFilter.getText();
	}
	
	public void setProcessFlows(final ObservableList<ProcessFlow> processflows) {
		table.setItems(processflows);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtFilter.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		getPresenter().refreshProcessFlowList();
		fulltxtFilter.setText("");
		getPresenter().filterChanged("");
		hideNotification();
	}
	
	private class DeleteButton extends TableCell<ProcessFlow, ProcessFlow> {
		
		static final String CSS_CLASS_DELETE_OPEN_BTN = "delete-open";
		final Button deleteButton;
		final String deleteIconPath = "M 3.6875 1.5 C 3.6321551 1.5180584 3.5743859 1.5512948 3.53125 1.59375 L 1.59375 3.53125 C 1.423759 3.7005604 1.4751389 4.0379792 1.6875 4.25 L 7.46875 10 L 1.6875 15.75 C 1.4751389 15.962191 1.423759 16.297908 1.59375 16.46875 L 3.53125 18.40625 C 3.7020918 18.577772 4.0386598 18.55526 4.25 18.34375 L 10 12.5625 L 15.75 18.34375 C 15.962021 18.55526 16.297908 18.577772 16.46875 18.40625 L 18.40625 16.46875 C 18.57556 16.297908 18.524691 15.962191 18.3125 15.75 L 12.53125 10 L 18.3125 4.25 C 18.524691 4.0379792 18.57556 3.7005604 18.40625 3.53125 L 16.46875 1.59375 C 16.297908 1.4244396 15.962021 1.4759897 15.75 1.6875 L 10 7.46875 L 4.25 1.6875 C 4.1439896 1.5809791 4.0004088 1.5136129 3.875 1.5 C 3.8124658 1.4931936 3.7428449 1.4819416 3.6875 1.5 z ";
		ProcessFlow p;
		
		DeleteButton() {
			deleteButton = createButton(deleteIconPath, CSS_CLASS_DELETE_OPEN_BTN, "", 32, 0, new EventHandler<ActionEvent>() {
				 @Override public void handle(ActionEvent actionEvent) {
					 getPresenter().deleteProcess(p);
				 }
			});
		}
		
		@Override
		protected void updateItem(ProcessFlow item, boolean empty) {
			super.updateItem(item, empty);
			if(!empty) {
				setGraphic(deleteButton);
				this.p = item;
			}
				
		}
	}
}
