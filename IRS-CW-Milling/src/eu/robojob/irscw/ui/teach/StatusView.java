package eu.robojob.irscw.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class StatusView extends VBox {

	public enum Status {
		OK, ERROR, WARNING
	}
	
	private StatusPresenter presenter;
	
	private Label lblInfoMessage;
	private Label lblAlarmMessage;
	private Label lblErrorMessage;
	private Button btnRestart;
	
	private SVGPath infoPath;
	private SVGPath warningPath;
	private SVGPath errorPath;
	
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BTN_HEIGHT = 40;
	
	private static final String CSS_CLASS_LOADING = "loading";
	private static final String CSS_CLASS_LOADING_INACTIVE = "loading-inactive";
	private static final String CSS_CLASS_Z_REST = "lbl-z-rest";
	private static final String CSS_CLASS_Z_REST_VAL = "lbl-z-rest-val";
	private static final String CSS_CLASS_MSG_NORMAL = "message-normal";
	private static final String CSS_CLASS_MSG_ERROR = "message-error";
	
	private static final String DROPS_ANOTHER = "StatusView.dropsAnother";
	private static final String STOP = "StatusView.stop";
	
	private static final String INFO_ICON = "M 12.5,-1.9089388e-8 C 5.5964999,-1.9089388e-8 0,5.5965009 0,12.5 0,19.4035 5.5964999,25 12.5,25 19.4035,25 25,19.4035 25,12.5 25,5.5965009 19.4035,-1.9089388e-8 12.5,-1.9089388e-8 z M 14.6875,3.0625 c 0.328999,0 0.59325,0.10575 0.78125,0.3125 C 15.65675,3.58175 15.75,3.86475 15.75,4.25 15.74975,4.77625 15.59625,5.2055 15.28125,5.5625 14.966251,5.91975 14.6225,6.125 14.21875,6.125 13.88975,6.125 13.59125,6.00225 13.375,5.78125 13.15875,5.5605 13.0625,5.2605 13.0625,4.875 c 0,-0.47925 0.16225,-0.91475 0.5,-1.28125 0.3385,-0.3665 0.71125,-0.53125 1.125,-0.53125 z m -0.875,5.5 c 0.2245,0 0.40925,0.0605 0.53125,0.1875 0.1215,0.12675 0.1875,0.31825 0.1875,0.5625 0,0.592 -0.224,1.6865 -0.65625,3.3125 -1.101,4.1445 -1.65625,6.648 -1.65625,7.53125 0,0.31975 0.146,0.5 0.4375,0.5 0.3385,0 1.106,-0.435 2.28125,-1.28125 l 0.28125,0.5625 c -1.343749,1.0815 -2.4695,1.855 -3.34375,2.34375 -0.35775,0.2065 -0.69,0.3125 -1,0.3125 -0.254,0 -0.47325,-0.118 -0.65625,-0.34375 -0.18325,-0.2255 -0.25,-0.54575 -0.25,-0.96875 0,-0.61075 0.25175,-1.836 0.75,-3.6875 0.98275,-3.6275 1.46875,-5.81975 1.46875,-6.5625 0,-0.357 -0.13375,-0.53125 -0.40625,-0.53125 -0.301,0 -1.0565,0.40125 -2.25,1.21875 L 9.25,11.15625 C 11.572,9.417751 13.082,8.5625 13.8125,8.5625 z";
	private static final String WARNING_ICON_PATH = "M 12.53125 0 C 12.024848 -0.00011109284 11.509368 0.28555607 11.125 0.875 L 0.3125 17.40625 C -0.45649646 18.585582 0.23029693 19.59375 1.8125 19.59375 L 23.1875 19.59375 C 24.769964 19.59375 25.455456 18.586026 24.6875 17.40625 L 13.90625 0.875 C 13.522402 0.28511238 13.037652 0.00011075102 12.53125 0 z M 10.90625 5.21875 L 14.09375 5.21875 L 14.09375 7.40625 L 13.40625 13.375 L 11.59375 13.375 L 10.90625 7.40625 L 10.90625 5.21875 z M 11.0625 14.71875 L 13.9375 14.71875 L 13.9375 17.1875 L 11.0625 17.1875 L 11.0625 14.71875 z";
	private static final String ERROR_ICON = "M 12.3125 0 A 12.498959 12.498959 0 0 0 3.90625 3.40625 A 12.498959 12.498959 0 0 0 3.65625 21.34375 A 12.498959 12.498959 0 1 0 21.34375 3.65625 A 12.498959 12.498959 0 0 0 12.3125 0 z M 8.78125 6.3125 L 12.5 10.03125 L 16.21875 6.3125 L 18.6875 8.78125 L 14.96875 12.5 L 18.6875 16.21875 L 16.21875 18.6875 L 12.5 14.96875 L 8.78125 18.6875 L 6.3125 16.21875 L 10.03125 12.5 L 6.3125 8.78125 L 8.78125 6.3125 z";
			
	public StatusView() {
		build();
	}
	
	public void setPresenter(final StatusPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		
		this.setFillWidth(true);
		this.setPrefSize(520, 300);
		this.setAlignment(Pos.CENTER);
		
		
	}
	
	public void setInfoMessage(final String message) {
	}
	
	public void setAlarmMessage(final String message) {
	}
	
	public void setErrorMessage(final String message) {
	}
	
	public void setZRest(final double zrest) {
	}
}
