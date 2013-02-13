package eu.robojob.irscw.ui.configure.process;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class ProcessOpenView extends AbstractFormView<ProcessOpenPresenter> {
	
	private Label lblName;
	private FullTextField fulltxtFilter;
	private TableView<ProcessFlow> table;
	private Button btnLoad;
	
	private static String openIconPath = "M 10 0 C 4.4775 0 0 4.4762499 0 10 C 0 15.52125 4.4775 20 10 20 C 15.5225 20 20 15.52125 20 10 C 20 4.4762499 15.5225 0 10 0 z M 10 5 L 14.96875 9.96875 L 11.25 9.96875 L 11.25 15 L 8.75 15 L 8.75 9.96875 L 5 9.96875 L 10 5 z ";
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
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
		build();
	}

	@Override
	protected void build() {
		
		setVgap(VGAP);
		setHgap(HGAP);
		
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
		    		 return new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd").format(date));
		    	 }
		     }
		  });
		nameColumn.setMinWidth(350);
		nameColumn.setMaxWidth(350);
		nameColumn.setPrefWidth(350);
		nameColumn.setResizable(false);
		lastOpenedColumn.setPrefWidth(150);
		lastOpenedColumn.setMinWidth(150);
		lastOpenedColumn.setMaxWidth(150);
		lastOpenedColumn.setResizable(false);
		table.getColumns().add(nameColumn);
		table.getColumns().add(lastOpenedColumn);
		table.setPrefSize(502, 230);
		table.setMinSize(502, 230);
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
		add(totalHBox, column++, row);
		row++;
		column = 0;		
		add(table, column++, row);
		
		setPadding(new Insets(15, 0, 0, 0));
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
	}
}
