package eu.robojob.irscw.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import eu.robojob.irscw.util.Translator;

public class MenuBarView extends ToolBar {
	
	private Button btnAlarms;
	private Button btnAdmin;
	
	private SVGPath alarmsShape;
	private SVGPath adminShape;
	private String alarmsPath = "m 21.845957,-0.0414886 c -0.32119,0.0484 -0.624,0.26975 -0.75,0.59375 l -5.09375,13.2812496 0,-8.0624996 c 0,-0.071 -0.0143,-0.1185 -0.0312,-0.1875 -0.061,-0.301 -0.2535,-0.5695 -0.5625,-0.6875 -0.48,-0.184 -1.03575,0.0522 -1.21875,0.53125 l -2.125,5.0937496 -7.3437507,0.375 0,0.84375 c -0.24,0 -0.4375,-0.1945 -0.4375,-0.4375 0,-0.239 0.1965,-0.40625 0.4375,-0.40625 l -0.28125,0 c -0.241,0 -0.4375,0.1985 -0.4375,0.4375 0,0.243 0.1975,0.4375 0.4375,0.4375 l 8.1250007,0.5625 c 0.425,0.06 0.844,-0.1465 1,-0.5625 l 0.59375,-1.375 0,8.4375 c 0,0.515 0.39225,0.9375 0.90625,0.9375 0.0529,0 0.10571,-0.0225 0.15625,-0.0312 -0.0419,-0.0129 -0.0856,-0.0204 -0.125,-0.0312 l 0.3125,0 c 0.1025,-0.0395 0.20025,-0.0873 0.28125,-0.15625 0.016,-0.012 0.0192,-0.0495 0.0312,-0.0625 0.092,-0.095 0.198,-0.21375 0.25,-0.34375 l 5.09375,-13.3124996 0,8.0937496 c 0,0.492 0.395,0.9015 0.875,0.9375 0.418,0.058 0.842,-0.17875 1,-0.59375 l 2.125,-5.0937496 7.3125,0.21875 c 0.38742,0 0.729515,-0.21494 0.906255,-0.53125 l 0,-1 c -0.17774,-0.31248 -0.521515,-0.53057 -0.906255,-0.53125 l -7.875,0.0312 c -0.399,-0.03 -0.7855,0.20175 -0.9375,0.59375 l -0.625,1.46875 0,-8.53125 c 0,-0.459 -0.34525,-0.83125 -0.78125,-0.90625 -0.1105,-0.023 -0.20544,-0.0474 -0.3125,-0.0312 z";
	private String adminPath = "M 19.71875 -0.21875 C 16.9575 -0.21875 14.71875 2.02125 14.71875 4.78125 C 14.71875 5.55125 14.90625 6.284375 15.21875 6.9375 L 5.1875 16.9375 C 4.8875 17.240625 4.71875 17.66125 4.71875 18.125 C 4.71875 19.043125 5.451875 19.78125 6.375 19.78125 C 6.83625 19.78125 7.264375 19.615 7.5625 19.3125 L 17.5625 9.28125 C 18.211875 9.59125 18.95 9.78125 19.71875 9.78125 C 22.48 9.78125 24.71875 7.54125 24.71875 4.78125 C 24.71875 4.265 24.615625 3.78125 24.46875 3.3125 L 21.3125 6.46875 L 18.03125 6.46875 L 18.03125 3.125 L 21.15625 0.03125 C 20.699375 -0.106875 20.219375 -0.21875 19.71875 -0.21875 z";
	
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
				
		
		alarmsShape = new SVGPath();
		alarmsShape.setContent(alarmsPath);
		alarmsShape.getStyleClass().add("header-button-shape");
		
		btnAlarms = new Button();
		btnAlarms.setGraphic(alarmsShape);
		
		btnAlarms.setPrefSize(BTN_WIDTH_SMALL, BTN_HEIGHT);
		btnAlarms.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.clickedAlarms();
			}
		});
		btnAlarms.getStyleClass().add("header-button");
		btnAlarms.setId("btnAlarms");
		
		adminShape = new SVGPath();
		adminShape.setContent(adminPath);
		adminShape.getStyleClass().add("header-button-shape");
		
		btnAdmin = new Button();
		btnAdmin.setGraphic(adminShape);
		btnAdmin.setPrefSize(BTN_WIDTH_SMALL, BTN_HEIGHT);
		btnAdmin.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.clickedAdmin();
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
				presenter.clickedConfigure();
			}
		});
		btnTeach = new Button();
		Text btnTeachText = new Text(translator.getTranslation("Teach"));
		btnTeach.setGraphic(btnTeachText);
		btnTeach.getStyleClass().addAll("bar", "header-button");
		btnTeach.setPrefSize(BTN_WIDTH_LARGE, BTN_HEIGHT);
		btnTeach.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.clickedTeach();
			}
		});
		btnAutomate = new Button();
		Text btnAutomateText = new Text(translator.getTranslation("Automate"));
		btnAutomate.setGraphic(btnAutomateText);
		btnAutomate.setPrefSize(BTN_WIDTH_LARGE, BTN_HEIGHT);
		btnAutomate.getStyleClass().addAll("last", "header-button");
		btnAutomate.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.clickedAutomate();
			}
		});
				
		hBoxProcessMenuItems.getChildren().add(btnConfigure);
		hBoxProcessMenuItems.getChildren().add(btnTeach);
		hBoxProcessMenuItems.getChildren().add(btnAutomate);
		
		buttonBar = new HBox();
		
		buttonBar.getChildren().add(btnAlarms);
		buttonBar.getChildren().add(hBoxProcessMenuItems);
		buttonBar.getChildren().add(btnAdmin);
		
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

	public void setAlarmsActive() {
		setNoneActive();
		setActive(btnAlarms);
	}
	
	public void setAdminActive() {
		setNoneActive();
		setActive(btnAdmin);
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
	
	public void setEnabled(boolean enabled) {
		this.setDisable(!enabled);
	}
	
}


