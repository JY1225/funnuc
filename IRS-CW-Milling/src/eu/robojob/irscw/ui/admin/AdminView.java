package eu.robojob.irscw.ui.admin;

import eu.robojob.irscw.ui.controls.keyboard.NumericKeyboardView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class AdminView extends StackPane {

	public static final int HEIGHT = 545;
	public static final int WIDTH = 800;
	
	protected static final double MAIN_MENU_WIDTH = 70;
	
	private static final String CSS_CLASS_MAIN = "admin-main";
	
	private HBox hBoxMain;
	private StackPane mainMenu;
	private StackPane spContent;
	private StackPane keyboardPane;
	
	private AdminPresenter presenter;
	
	public AdminView() {
		super();
		build();
	}
	
	public void setPresenter(final AdminPresenter presenter) {
		this.presenter = presenter;
	}
	
	public AdminPresenter getPresenter() {
		return presenter;
	}
	
	private void build() {
		setPrefSize(WIDTH, HEIGHT);
		getStyleClass().add(CSS_CLASS_MAIN);
		hBoxMain = new HBox();
		hBoxMain.setPrefSize(WIDTH, HEIGHT);
		this.getChildren().add(hBoxMain);
		mainMenu = new StackPane();
		mainMenu.setPrefWidth(MAIN_MENU_WIDTH);
		spContent = new StackPane();
		hBoxMain.getChildren().add(mainMenu);
		hBoxMain.getChildren().add(spContent);
		keyboardPane = new StackPane();
		keyboardPane.setMaxHeight(300);
		StackPane.setAlignment(keyboardPane, Pos.BOTTOM_LEFT);
	}
	
	public void setMainMenu(final Node mainMenuNode) {
		mainMenu.getChildren().clear();
		mainMenu.getChildren().add(mainMenuNode);
	}
	
	public void setContent(final Node contentNode) {
		spContent.getChildren().clear();
		spContent.getChildren().add(contentNode);
	}
	
	public void showKeyboardPane(final Node keyboardNode, final boolean top) {
		if (top) {
			StackPane.setAlignment(keyboardPane, Pos.TOP_LEFT);
		} else {
			StackPane.setAlignment(keyboardPane, Pos.BOTTOM_LEFT);
		}
		getChildren().remove(keyboardPane);
		keyboardPane.getChildren().clear();
		keyboardPane.getChildren().add(keyboardNode);
		getChildren().add(keyboardPane);
		if (keyboardNode instanceof NumericKeyboardView) {
			keyboardPane.setMaxWidth(240);
			keyboardPane.setMaxHeight(300);
		} else {
			keyboardPane.setMaxWidth(USE_PREF_SIZE);
			keyboardPane.setMaxHeight(250);
		}
	}
	
	public void closeKeyboard() {
		getChildren().remove(keyboardPane);
		requestFocus();
	}
}
