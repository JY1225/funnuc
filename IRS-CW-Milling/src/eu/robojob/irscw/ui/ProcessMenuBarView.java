package eu.robojob.irscw.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ProcessMenuBarView extends HBox {
	
	private Button btnAlarms;
	private Button btnAdmin;
	
	private HBox hBoxProcessMenuItems;
	private Button btnConfigure;
	private Button btnTeach;
	private Button btnAutomate;
	
	private ProcessMenuBarPresenter presenter;
	
	private Button selectedBtn;
	
	private static final String BTN_SELECTED = "selected";
	
	public ProcessMenuBarView() {
		buildView();
	}
	
	public void setPresenter(ProcessMenuBarPresenter presenter) {
		this.presenter = presenter;
	}
	
	protected void buildView() {
		
		selectedBtn = null;
		
		btnAlarms = new Button("Alarms");
		btnAlarms.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showAlarmsView();
			}
		});
		
		btnAdmin = new Button("Admin");
		btnAdmin.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showAdminView();
			}
		});

		hBoxProcessMenuItems = new HBox();
		hBoxProcessMenuItems.setSpacing(0);
		
		btnConfigure = new Button("Configure");
		btnConfigure.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showConfigureView();
			}
		});
		btnTeach = new Button("Teach");
		btnTeach.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showTeachView();
			}
		});
		btnAutomate = new Button("Automate");
		btnAutomate.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presenter.showAutomateView();
			}
		});
		
		hBoxProcessMenuItems.getChildren().add(btnConfigure);
		hBoxProcessMenuItems.getChildren().add(btnTeach);
		hBoxProcessMenuItems.getChildren().add(btnAutomate);
		
		getChildren().add(btnAlarms);
		getChildren().add(hBoxProcessMenuItems);
		getChildren().add(btnAdmin);
		
		setHgrow(hBoxProcessMenuItems, Priority.ALWAYS);
		hBoxProcessMenuItems.setAlignment(Pos.CENTER);
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
