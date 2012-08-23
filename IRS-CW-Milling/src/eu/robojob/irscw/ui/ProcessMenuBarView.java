package eu.robojob.irscw.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import eu.robojob.irscw.util.Translator;

public class ProcessMenuBarView extends ToolBar {
	
	private Button btnAlarms;
	private Button btnAdmin;
	
	private HBox buttonBar;
	
	private HBox hBoxProcessMenuItems;
	private Button btnConfigure;
	private Button btnTeach;
	private Button btnAutomate;
	
	private ProcessMenuBarPresenter presenter;
	private Translator translator;
	
	private Button selectedBtn;
	
	private static final String BTN_SELECTED = "selected";
	private static final int BTN_HEIGHT = 40;
	private static final int BTN_WIDTH_SMALL = 60;
	private static final int BTN_WIDTH_LARGE = 120;
	
	public ProcessMenuBarView() {
		translator = Translator.getInstance();
		buildView();
	}
	
	public void setPresenter(ProcessMenuBarPresenter presenter) {
		this.presenter = presenter;
	}
	
	protected void buildView() {
		
		selectedBtn = null;
		
		btnAlarms = new Button(translator.getTranslation("Alarms"));
		btnAlarms.setPrefSize(BTN_WIDTH_SMALL, BTN_HEIGHT);
		btnAlarms.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showAlarmsView();
			}
		});
		
		btnAdmin = new Button(translator.getTranslation("Admin"));
		btnAdmin.setPrefSize(BTN_WIDTH_SMALL, BTN_HEIGHT);
		btnAdmin.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showAdminView();
			}
		});

		hBoxProcessMenuItems = new HBox();
		hBoxProcessMenuItems.setSpacing(0);
		
		btnConfigure = new Button(translator.getTranslation("Configure"));
		btnConfigure.getStyleClass().add("first");
		btnConfigure.setPrefSize(BTN_WIDTH_LARGE, BTN_HEIGHT);
		btnConfigure.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showConfigureView();
			}
		});
		btnTeach = new Button(translator.getTranslation("Teach"));
		btnTeach.getStyleClass().add("bar");
		btnTeach.setPrefSize(BTN_WIDTH_LARGE, BTN_HEIGHT);
		btnTeach.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showTeachView();
			}
		});
		btnAutomate = new Button(translator.getTranslation("Automate"));
		btnAutomate.setPrefSize(BTN_WIDTH_LARGE, BTN_HEIGHT);
		btnAutomate.getStyleClass().add("last");
		btnAutomate.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showAutomateView();
			}
		});
				
		hBoxProcessMenuItems.getChildren().add(btnConfigure);
		hBoxProcessMenuItems.getChildren().add(btnTeach);
		hBoxProcessMenuItems.getChildren().add(btnAutomate);
		
		buttonBar = new HBox();
		
		buttonBar.getChildren().add(btnAlarms);
		buttonBar.getChildren().add(hBoxProcessMenuItems);
		buttonBar.getChildren().add(btnAdmin);
		
		btnAlarms.setAlignment(Pos.CENTER_LEFT);
		btnAdmin.setAlignment(Pos.CENTER_RIGHT);
		hBoxProcessMenuItems.setAlignment(Pos.CENTER);
		buttonBar.setAlignment(Pos.CENTER);
		
		HBox.setHgrow(buttonBar, Priority.ALWAYS);
		HBox.setHgrow(hBoxProcessMenuItems, Priority.ALWAYS);
		
		this.getItems().add(buttonBar);
		this.setPrefHeight(55);
	}
	
	public void setConfigureActive() {
		setNoneActive();
		setActive(btnConfigure);
	}
	
	public void setTeachActive() {
		setNoneActive();
		setActive(btnTeach);
	}
	
	public void setAutomateActive() {
		setNoneActive();
		setActive(btnAutomate);
	}
	
	public void setNoneActive() {
		if (selectedBtn != null) {
			selectedBtn.getStyleClass().remove(BTN_SELECTED);
		}
	}
	
	private void setActive(Button button) {
		btnConfigure.getStyleClass().add(BTN_SELECTED);
	}
	
}


