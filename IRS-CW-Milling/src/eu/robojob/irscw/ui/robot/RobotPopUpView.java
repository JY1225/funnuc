package eu.robojob.irscw.ui.robot;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.robojob.irscw.ui.PopUpView;
import eu.robojob.irscw.util.UIConstants;

public class RobotPopUpView extends PopUpView<RobotPopUpPresenter> {

	private VBox vBoxMenuItems;
	
	private Button btnReset;
	private Button btnToHome;
	private Button btnToChange;
	
	private Button btn25;
	private Button btn50;
	private Button btn75;
	private Button btn100;
	
	private static final int width = UIConstants.BUTTON_HEIGHT*4;
	private static final int height = 7* UIConstants.BUTTON_HEIGHT;
	
	public RobotPopUpView() {
		super(80, 0, width, height);
	}
	
	@Override
	protected void build() {
		super.build();
		vBoxMenuItems = new VBox();
		this.getChildren().add(vBoxMenuItems);
		
		btnReset = new Button();
		btnReset.setGraphic(new Text(translator.getTranslation("reset")));
		btnReset.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		btnReset.getStyleClass().add("pop-up-btn");
		btnReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.resetClicked();
			}
		});
		vBoxMenuItems.getChildren().add(btnReset);
		
		btnToHome = new Button();
		btnToHome.setGraphic(new Text(translator.getTranslation("to-home")));
		btnToHome.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		btnToHome.getStyleClass().add("pop-up-btn");
		btnToHome.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.toHomeClicked();
			}
		});
		vBoxMenuItems.getChildren().add(btnToHome);
		
		btnToChange = new Button();
		btnToChange.setGraphic(new Text(translator.getTranslation("to-change")));
		btnToChange.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		btnToChange.getStyleClass().add("pop-up-btn");
		btnToChange.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.toChangePointClicked();
			}
		});
		vBoxMenuItems.getChildren().add(btnToChange);
		
		btn25 = new Button();
		btn25.setGraphic(new Text("25%"));
		btn25.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		btn25.getStyleClass().add("pop-up-btn");
		btn25.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.setSpeedClicked(25);
			}
		});
		vBoxMenuItems.getChildren().add(btn25);
		
		btn50 = new Button();
		btn50.setGraphic(new Text("50%"));
		btn50.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		btn50.getStyleClass().add("pop-up-btn");
		btn50.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.setSpeedClicked(50);
			}
		});
		vBoxMenuItems.getChildren().add(btn50);
		
		btn75 = new Button();
		btn75.setGraphic(new Text("75%"));
		btn75.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		btn75.getStyleClass().add("pop-up-btn");
		btn75.getStyleClass().add("pop-up-btn-pressed");
		btn75.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.setSpeedClicked(75);
			}
		});
		vBoxMenuItems.getChildren().add(btn75);
		
		btn100 = new Button();
		btn100.setGraphic(new Text("100%"));
		btn100.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		btn100.getStyleClass().add("pop-up-btn");
		btn100.getStyleClass().add("pop-up-btn-bottom");
		btn100.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.setSpeedClicked(100);
			}
		});
		vBoxMenuItems.getChildren().add(btn100);
	}
	
	public void refreshSpeed(int speed) {
		btn25.getStyleClass().remove("pop-up-btn-pressed");
		btn50.getStyleClass().remove("pop-up-btn-pressed");
		btn75.getStyleClass().remove("pop-up-btn-pressed");
		btn100.getStyleClass().remove("pop-up-btn-pressed");
		if (speed == 25) {
			btn25.getStyleClass().add("pop-up-btn-pressed");
		} else if (speed == 50) {
			btn50.getStyleClass().add("pop-up-btn-pressed");
		} else if (speed == 75) {
			btn75.getStyleClass().add("pop-up-btn-pressed");
		} else  if (speed == 100) {
			btn100.getStyleClass().add("pop-up-btn-pressed");
		}
	}

	public void setRobotConnected(boolean connected) {
		btn25.setDisable(!connected);
		btn50.setDisable(!connected);
		btn75.setDisable(!connected);
		btn100.setDisable(!connected);
		btnReset.setDisable(!connected);
		btnToChange.setDisable(!connected);
		btnToHome.setDisable(!connected);
		if (!connected) {
			refreshSpeed(0);
		}
	}
}
