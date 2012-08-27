package eu.robojob.irscw.ui.process;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import eu.robojob.irscw.util.Translator;

public class MenuBarView extends ToolBar {
	
	private Button btnAlarms;
	private Button btnAdmin;
	
	private HBox buttonBar;
	
	private HBox hBoxProcessMenuItems;
	private Button btnConfigure;
	private Button btnTeach;
	private Button btnAutomate;
	
	private MenuBarPresenter presenter;
	private Translator translator;
	
	private Button selectedBtn;
	
	private static final String BTN_SELECTED = "selected";
	private static final int BTN_HEIGHT = 45;
	private static final int BTN_WIDTH_SMALL = 60;
	private static final int BTN_WIDTH_LARGE = 130;
	
	public MenuBarView() {
		translator = Translator.getInstance();
		buildView();
	}
	
	public void setPresenter(MenuBarPresenter presenter) {
		this.presenter = presenter;
	}
	
	protected void buildView() {
		
		selectedBtn = null;
				
		btnAlarms = new Button();
		btnAlarms.setPrefSize(BTN_WIDTH_SMALL, BTN_HEIGHT);
		btnAlarms.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showAlarmsView();
			}
		});
		btnAlarms.getStyleClass().add("header-button");
		btnAlarms.setId("btnAlarms");
		
		btnAdmin = new Button();
		btnAdmin.setPrefSize(BTN_WIDTH_SMALL, BTN_HEIGHT);
		btnAdmin.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showAdminView();
			}
		});
		btnAdmin.getStyleClass().add("header-button");
		btnAdmin.setId("btnAdmin");

		hBoxProcessMenuItems = new HBox();
		hBoxProcessMenuItems.setSpacing(0);
		
		btnConfigure = new Button();
		Text btnConfigureText = new Text(translator.getTranslation("Configure"));
		btnConfigure.setGraphic(btnConfigureText);
		btnConfigure.getStyleClass().addAll("first", "header-button");
		btnConfigure.setPrefSize(BTN_WIDTH_LARGE, BTN_HEIGHT);
		btnConfigure.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showConfigureView();
			}
		});
		btnTeach = new Button();
		Text btnTeachText = new Text(translator.getTranslation("Teach"));
		btnTeach.setGraphic(btnTeachText);
		btnTeach.getStyleClass().addAll("bar", "header-button");
		btnTeach.setPrefSize(BTN_WIDTH_LARGE, BTN_HEIGHT);
		btnTeach.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showTeachView();
			}
		});
		btnAutomate = new Button();
		Text btnAutomateText = new Text(translator.getTranslation("Automate"));
		btnAutomate.setGraphic(btnAutomateText);
		btnAutomate.setPrefSize(BTN_WIDTH_LARGE, BTN_HEIGHT);
		btnAutomate.getStyleClass().addAll("last", "header-button");
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
		this.selectedBtn = button;
		selectedBtn.getStyleClass().add(BTN_SELECTED);
	}
	
}


