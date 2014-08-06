package eu.robojob.millassist.ui.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.ui.general.PopUpView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class RobotPopUpView extends PopUpView<RobotPopUpPresenter> {

	private VBox vBoxMenuItems1;
	private VBox vBoxMenuItems2;
	
	private Button btnReset;
	
	private Button btnRestart;
	private Button btnToHome;
	private Button btnToChange;
	private Button btnOpenA;
	private Button btnCloseA;
	private Button btnOpenB;
	private Button btnCloseB;
	private Button btnToCustomPos;
	
	private Button btn5;
	private Button btn10;
	private Button btn25;
	private Button btn50;
	private Button btn75;
	private Button btn100;
	
	private static final int BUTTON_HEIGHT = UIConstants.BUTTON_HEIGHT + 5;
	private static final int WIDTH = BUTTON_HEIGHT * 4;
	private static final int AMOUNT_OF_ITEMS = 7;
	private static final int HEIGHT = AMOUNT_OF_ITEMS * BUTTON_HEIGHT;
	
	private static final String RESET = "RobotPopUpView.reset";
	private static final String RESTART = "RobotPopUpView.restart";
	private static final String TO_HOME = "RobotPopUpView.toHome";
	private static final String TO_CHANGE = "RobotPopUpView.toChange";
	private static final String OPEN_A = "RobotPopUpView.openA";
	private static final String CLOSE_A = "RobotPopUpView.closeA";
	private static final String OPEN_B = "RobotPopUpView.openB";
	private static final String CLOSE_B = "RobotPopUpView.closeB";
	private static final String TO_CUSTOM_POS = "RobotPopUpView.toCustomPos";
	
	private static final String CSS_CLASS_POPUP_BUTTON = "pop-up-btn";
	private static final String CSS_CLASS_POPUP_BUTTON_BOTTOM = "pop-up-btn-bottom";
	private static final String CSS_CLASS_POPUP_BUTTON_PRESSED = "pop-up-btn-pressed";
	
	private static final int TOP_LEFT_X = 65;
	private static final int TOP_LEFT_Y = 0;
	
	private static Logger logger = LogManager.getLogger(RobotPopUpView.class.getName());
	
	private int speed;
	
	public RobotPopUpView() {
		super(TOP_LEFT_X, TOP_LEFT_Y, WIDTH * 2 - 1, HEIGHT);
		final Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File("settings.properties")));
			if (properties.containsKey("to-custom-pos") && properties.get("to-custom-pos").equals("true")) {
				vBoxMenuItems2.getChildren().add(btnToCustomPos);
				setHeight((AMOUNT_OF_ITEMS + 1) * BUTTON_HEIGHT);
				setPrefHeight((AMOUNT_OF_ITEMS + 1) * BUTTON_HEIGHT);
				setMaxHeight((AMOUNT_OF_ITEMS + 1) * BUTTON_HEIGHT);
				//btnCloseB.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_BOTTOM);
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@Override
	protected void build() {
		super.build();
		HBox hbox = new HBox();
		
		vBoxMenuItems1 = new VBox();
		vBoxMenuItems2 = new VBox();
		
		hbox.setSpacing(-1);
		hbox.getChildren().addAll(vBoxMenuItems1, vBoxMenuItems2);
		
		this.getChildren().add(hbox);
		
		btnReset = new Button();
		btnReset.setGraphic(new Text(Translator.getTranslation(RESET)));
		btnReset.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnReset.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnReset.getStyleClass().add("pop-up-first");
		btnReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().resetClicked();
			}
		});
		
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
		
		btnToHome = new Button();
		btnToHome.setGraphic(new Text(Translator.getTranslation(TO_HOME)));
		btnToHome.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnToHome.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnToHome.getStyleClass().add("pop-up-first");
		btnToHome.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().toHomeClicked();
			}
		});
		
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
		
		btn5 = new Button();
		btn5.setGraphic(new Text("5%"));
		btn5.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btn5.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btn5.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().setSpeedClicked(5);
			}
		});
		
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
		
		btn75 = new Button();
		btn75.setGraphic(new Text("75%"));
		btn75.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btn75.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btn75.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().setSpeedClicked(75);
			}
		});
		
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

		btnOpenA = new Button();
		btnOpenA.setGraphic(new Text(Translator.getTranslation(OPEN_A)));
		btnOpenA.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnOpenA.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnOpenA.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().openGripperA();
			}
		});
		
		btnCloseA = new Button();
		btnCloseA.setGraphic(new Text(Translator.getTranslation(CLOSE_A)));
		btnCloseA.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnCloseA.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnCloseA.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().closeGripperA();
			}
		});
		
		btnOpenB = new Button();
		btnOpenB.setGraphic(new Text(Translator.getTranslation(OPEN_B)));
		btnOpenB.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnOpenB.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnOpenB.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().openGripperB();
			}
		});
		
		btnCloseB = new Button();
		btnCloseB.setGraphic(new Text(Translator.getTranslation(CLOSE_B)));
		btnCloseB.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnCloseB.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnCloseB.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_BOTTOM);
		btnCloseB.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().closeGripperB();
			}
		});
		
		btnToCustomPos = new Button();
		btnToCustomPos.setGraphic(new Text(Translator.getTranslation(TO_CUSTOM_POS)));
		btnToCustomPos.setPrefSize(WIDTH, BUTTON_HEIGHT);
		btnToCustomPos.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		btnToCustomPos.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_BOTTOM);
		btnToCustomPos.setTranslateY(-1);
		btnToCustomPos.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().toCustomPosition();
			}
		});
		
		vBoxMenuItems1.getChildren().add(btnReset);
		vBoxMenuItems1.getChildren().add(btn5);
		vBoxMenuItems1.getChildren().add(btn10);
		vBoxMenuItems1.getChildren().add(btn25);
		vBoxMenuItems1.getChildren().add(btn50);
		vBoxMenuItems1.getChildren().add(btn75);
		vBoxMenuItems1.getChildren().add(btn100);
				
		vBoxMenuItems2.getChildren().add(btnToHome);
		vBoxMenuItems2.getChildren().add(btnRestart);
		vBoxMenuItems2.getChildren().add(btnToChange);
		vBoxMenuItems2.getChildren().add(btnOpenA);
		vBoxMenuItems2.getChildren().add(btnCloseA);
		vBoxMenuItems2.getChildren().add(btnOpenB);
		vBoxMenuItems2.getChildren().add(btnCloseB);
		
	}
	
	public void refreshSpeed(final int speed) {
		if (this.speed != speed) {
			btn5.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			btn25.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			btn50.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			btn75.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			btn10.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			btn100.getStyleClass().remove(CSS_CLASS_POPUP_BUTTON_PRESSED);
			switch(speed) {
				case 5:
					btn5.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_PRESSED);
					break;
				case 10:
					btn10.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_PRESSED);
					break;
				case 25:
					btn25.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_PRESSED);
					break;
				case 50:
					btn50.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_PRESSED);
					break;
				case 75:
					btn75.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_PRESSED);
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
		btn5.setDisable(!connected);
		btn25.setDisable(!connected);
		btn50.setDisable(!connected);
		btn10.setDisable(!connected);
		btn75.setDisable(!connected);
		btn100.setDisable(!connected);
		btnOpenA.setDisable(!connected);
		btnCloseA.setDisable(!connected);
		btnOpenB.setDisable(!connected);
		btnCloseB.setDisable(!connected);
		btnReset.setDisable(!connected);
		btnRestart.setDisable(!connected);
		btnToChange.setDisable(!connected);
		btnToHome.setDisable(!connected);
		btnToCustomPos.setDisable(!connected);
	}
	
	public void setProcessActive(final boolean active) {
		btnOpenA.setDisable(active);
		btnCloseA.setDisable(active);
		btnOpenB.setDisable(active);
		btnCloseB.setDisable(active);
		btnRestart.setDisable(active);
		btnToHome.setDisable(active);
		btnToChange.setDisable(active);
		btnToCustomPos.setDisable(active);
	}
}
