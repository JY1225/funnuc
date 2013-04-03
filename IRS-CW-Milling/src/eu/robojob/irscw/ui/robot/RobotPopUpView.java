package eu.robojob.irscw.ui.robot;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.robojob.irscw.ui.general.PopUpView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class RobotPopUpView extends PopUpView<RobotPopUpPresenter> {

	private VBox vBoxMenuItems;
	
	private Button btnReset;
	private Button btnRestart;
	private Button btnToHome;
	private Button btnToChange;
	
	private Button btn10;
	private Button btn25;
	private Button btn50;
	private Button btn100;
	
	private static final int BUTTON_HEIGHT = UIConstants.BUTTON_HEIGHT + 5;
	private static final int WIDTH = BUTTON_HEIGHT * 4;
	private static final int AMOUNT_OF_ITEMS = 7;
	private static final int HEIGHT = AMOUNT_OF_ITEMS * BUTTON_HEIGHT;
	
	private static final String RESET = "RobotPopUpView.reset";
	private static final String RESTART = "RobotPopUpView.restart";
	private static final String TO_HOME = "RobotPopUpView.toHome";
	private static final String TO_CHANGE = "RobotPopUpView.toChange";
	
	private static final String CSS_CLASS_POPUP_BUTTON = "pop-up-btn";
	private static final String CSS_CLASS_POPUP_BUTTON_BOTTOM = "pop-up-btn-bottom";
	private static final String CSS_CLASS_POPUP_BUTTON_PRESSED = "pop-up-btn-pressed";
	
	private static final int TOP_LEFT_X = 65;
	private static final int TOP_LEFT_Y = 0;
	
	private int speed;
	
	public RobotPopUpView() {
		super(TOP_LEFT_X, TOP_LEFT_Y, WIDTH, HEIGHT);
	}
	
	@Override
	protected void build() {
		super.build();
		vBoxMenuItems = new VBox();
		this.getChildren().add(vBoxMenuItems);
		
		btnReset = new Button();
		btnReset.setGraphic(new Text(Translator.getTranslation(RESET)));
		btnReset.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnReset.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().resetClicked();
			}
		});
		vBoxMenuItems.getChildren().add(btnReset);
		
		btnRestart = new Button();
		btnRestart.setGraphic(new Text(Translator.getTranslation(RESTART)));
		btnRestart.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnRestart.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnRestart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().restartClicked();
			}
		});
		vBoxMenuItems.getChildren().add(btnRestart);
		
		btnToHome = new Button();
		btnToHome.setGraphic(new Text(Translator.getTranslation(TO_HOME)));
		btnToHome.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnToHome.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnToHome.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().toHomeClicked();
			}
		});
		vBoxMenuItems.getChildren().add(btnToHome);
		
		btnToChange = new Button();
		btnToChange.setGraphic(new Text(Translator.getTranslation(TO_CHANGE)));
		btnToChange.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnToChange.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnToChange.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().toChangePointClicked();
			}
		});
		vBoxMenuItems.getChildren().add(btnToChange);
		
		btn10 = new Button();
		btn10.setGraphic(new Text("10%"));
		btn10.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btn10.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btn10.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().setSpeedClicked(10);
			}
		});
		vBoxMenuItems.getChildren().add(btn10);
		
		btn25 = new Button();
		btn25.setGraphic(new Text("25%"));
		btn25.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btn25.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btn25.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().setSpeedClicked(25);
			}
		});
		vBoxMenuItems.getChildren().add(btn25);
		
		btn50 = new Button();
		btn50.setGraphic(new Text("50%"));
		btn50.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btn50.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btn50.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().setSpeedClicked(50);
			}
		});
		vBoxMenuItems.getChildren().add(btn50);
		
		btn100 = new Button();
		btn100.setGraphic(new Text("100%"));
		btn100.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btn100.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btn100.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_BOTTOM);
		btn100.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().setSpeedClicked(100);
			}
		});
		//btn100.setDisable(true);
		vBoxMenuItems.getChildren().add(btn100);
	}
	
	public void refreshSpeed(final int speed) {
		if (this.speed != speed) {
			btn25.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			btn50.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			btn10.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			btn100.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			switch(speed) {
				case 10:
					btn10.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_PRESSED);
					break;
				case 25:
					btn25.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_PRESSED);
					break;
				case 50:
					btn50.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_PRESSED);
					break;
				case 100:
					btn100.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_PRESSED);
					break;
				default:
					throw new IllegalArgumentException("Illegal speed value [" + speed + "].");
			}
			this.speed = speed;
		}
	}

	public void setRobotConnected(final boolean connected) {
		btn25.setDisable(!connected);
		btn50.setDisable(!connected);
		btn10.setDisable(!connected);
		btn100.setDisable(!connected);
		btnReset.setDisable(!connected);
		btnRestart.setDisable(!connected);
		btnToChange.setDisable(!connected);
		btnToHome.setDisable(!connected);
	}
	
	public void setProcessActive(final boolean active) {
		btnRestart.setDisable(active);
		btnToHome.setDisable(active);
		btnToChange.setDisable(active);
	}
}
