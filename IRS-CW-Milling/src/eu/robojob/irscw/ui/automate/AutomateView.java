package eu.robojob.irscw.ui.automate;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class AutomateView extends VBox {

	public enum Status {
		OK, WARNING, ERROR
	}
	
	public static final int HEIGHT_TOP = 245;
	public static final int HEIGHT_BOTTOM_TOP = 185;
	public static final int HEIGHT_BOTTOM = 300;
	public static final int WIDTH = 800;
	public static final int PROGRESS_RADIUS = 70;
	public static final int PROGRESS_RADIUS_INNER = 0;
	public static final double ICON_WIDTH = 43;
	public static final double ICON_HEIGHT = 50;
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3;
	private static final double BTN_HEIGHT = 40;
	
	private static final String START = "AutomateView.start";
	private static final String RESET = "AutomateView.reset";
	private static final String PAUSE = "AutomateView.pause";
	private static final String STOP = "AutomateView.stop";
	private static final String CYCLE_TIME = "AutomateView.cycleTime";
	private static final String CYCLE_TIME_PASSED = "AutomateView.cyleTimePassed";
	private static final String TIME_TILL_INTERVENTION = "AutomateView.timeTillIntervention";
	private static final String TIME_TILL_FINISHED = "AutomateView.timeTillFinished";
	private static final String DEFAULT_STATUTS_MSG = "AutomateView.defaultStatusMessage";
	private static final String UNKNOWN = "AutomateView.unknown";
	
	private int totalAmount;
	private int finishedAmount;
	
	private StackPane top;
	private VBox bottom;
	private HBox bottomTop;
	private HBox bottomBottom;
	private static final String CYCLE_TIME_ICON = "M 15.09375 0 C 12.614456 0 10.59375 2.025857 10.59375 4.5 C 10.59375 5.969546 11.350474 7.2459437 12.4375 8.0625 C 10.442473 8.4192614 8.5796305 9.1900203 6.9375 10.25 L 6 8.96875 L 6.59375 8.5 C 6.8474737 8.3132479 6.9380391 7.94509 6.75 7.6875 L 6.1875 6.90625 C 5.9994591 6.648662 5.6000515 6.593211 5.34375 6.78125 L 2.71875 8.71875 C 2.4611617 8.9067899 2.3770359 9.2736607 2.5625 9.53125 L 3.15625 10.28125 C 3.3442899 10.538839 3.7124478 10.593002 3.96875 10.40625 L 4.5625 9.96875 L 5.5 11.25 C 2.133318 14.017794 0 18.23521 0 22.9375 C 0 31.271796 6.7555901 38 15.09375 38 C 23.426754 37.9988 30.154961 31.274372 30.15625 22.9375 C 30.15625 18.253242 28.064831 14.080295 24.71875 11.3125 L 25.65625 10 L 26.25 10.4375 C 26.505013 10.62554 26.896696 10.571378 27.09375 10.3125 L 27.625 9.53125 C 27.813038 9.2762365 27.766606 8.9380399 27.5 8.75 L 24.875 6.8125 C 24.618699 6.62446 24.255691 6.648661 24.0625 6.90625 L 23.53125 7.71875 C 23.343208 7.975052 23.357107 8.3119591 23.625 8.5 L 24.28125 8.96875 L 23.3125 10.28125 C 21.663931 9.2071037 19.765636 8.4231251 17.75 8.0625 C 18.842177 7.2446547 19.559925 5.968258 19.5625 4.5 C 19.5625 2.024568 17.56274 0 15.09375 0 z M 15.09375 1.1875 C 16.930359 1.1902 18.403674 2.664677 18.40625 4.5 C 18.40355 6.035231 17.380338 7.303693 15.96875 7.6875 L 15.96875 5.4375 L 16.71875 5.4375 C 17.035584 5.4375 17.3125 5.160584 17.3125 4.84375 L 17.3125 3.90625 C 17.3125 3.588128 17.035584 3.3125 16.71875 3.3125 L 13.4375 3.3125 C 13.119378 3.3125 12.875 3.588128 12.875 3.90625 L 12.875 4.84375 C 12.875 5.160584 13.119378 5.4375 13.4375 5.4375 L 14.25 5.4375 L 14.25 7.71875 C 12.830684 7.333654 11.782538 6.036519 11.78125 4.5 C 11.78395 2.664677 13.255851 1.190076 15.09375 1.1875 z M 15.09375 11.3125 C 21.507717 11.32538 26.678485 16.522243 26.6875 22.9375 C 26.6785 29.351469 21.507717 34.51837 15.09375 34.53125 C 8.6759175 34.518362 3.4764775 29.354045 3.46875 22.9375 C 3.4790443 16.520953 8.6759175 11.322803 15.09375 11.3125 z M 15.09375 12.46875 C 14.480687 12.46875 13.96875 12.950727 13.96875 13.5625 C 13.96875 14.174274 14.4794 14.6875 15.09375 14.6875 C 15.706811 14.6875 16.21875 14.174274 16.21875 13.5625 C 16.21875 12.950727 15.704236 12.46875 15.09375 12.46875 z M 10.3125 13.71875 C 10.169357 13.73654 9.976409 13.767116 9.84375 13.84375 C 9.3131172 14.150281 9.1596411 14.844368 9.46875 15.375 L 13.15625 24.03125 C 13.774463 25.100244 15.147181 25.459386 16.21875 24.84375 C 17.286457 24.226824 17.615638 22.85282 17 21.78125 L 11.375 14.25 C 11.146068 13.852025 10.741928 13.665381 10.3125 13.71875 z M 19.6875 13.71875 C 19.332434 13.74376 19.005691 13.918355 18.8125 14.25 C 18.507258 14.780635 18.688116 15.474719 19.21875 15.78125 C 19.745519 16.087781 20.414794 15.905634 20.71875 15.375 C 21.023994 14.845656 20.845707 14.151569 20.3125 13.84375 C 20.112547 13.7288 19.900538 13.703748 19.6875 13.71875 z M 6.9375 17.15625 C 6.5824216 17.18082 6.2236362 17.325408 6.03125 17.65625 C 5.7234298 18.189458 5.9381157 18.913507 6.46875 19.21875 C 6.9993807 19.52528 7.6622181 19.311884 7.96875 18.78125 C 8.2739929 18.249328 8.0918458 17.587781 7.5625 17.28125 C 7.3635125 17.165819 7.1505459 17.141503 6.9375 17.15625 z M 23.03125 17.15625 C 22.888187 17.17398 22.758301 17.204933 22.625 17.28125 C 22.096943 17.587781 21.910932 18.250616 22.21875 18.78125 C 22.523992 19.313172 23.22323 19.527857 23.75 19.21875 C 24.280634 18.914794 24.435394 18.189458 24.125 17.65625 C 23.894135 17.258274 23.460437 17.103061 23.03125 17.15625 z M 5.71875 21.8125 C 5.1031106 21.8125 4.625 22.327014 4.625 22.9375 C 4.62627 23.549275 5.1069752 23.997423 5.71875 24 C 6.3330986 24.0013 6.8737111 23.551851 6.875 22.9375 C 6.87238 22.32315 6.3279479 21.8125 5.71875 21.8125 z M 24.4375 21.8125 C 23.830876 21.8125 23.34375 22.327014 23.34375 22.9375 C 23.34375 23.551851 23.830876 24.002575 24.4375 24 C 25.054426 24.0026 25.53125 23.533819 25.53125 22.9375 C 25.53125 22.32315 25.042835 21.815075 24.4375 21.8125 z M 6.875 26.5 C 6.7321369 26.5176 6.6010858 26.517757 6.46875 26.59375 C 5.9381157 26.900283 5.7234309 27.596942 6.03125 28.125 C 6.3390683 28.655635 7.0006152 28.870319 7.53125 28.5625 C 8.0631708 28.255969 8.2765689 27.595709 7.96875 27.0625 C 7.738851 26.666458 7.3035837 26.447235 6.875 26.5 z M 23.125 26.5 C 22.770035 26.52527 22.410332 26.730853 22.21875 27.0625 C 21.91222 27.593134 22.091791 28.255969 22.625 28.5625 C 23.153056 28.869033 23.81718 28.65563 24.125 28.125 C 24.432819 27.594366 24.278059 26.902858 23.75 26.59375 C 23.551011 26.479767 23.33798 26.484847 23.125 26.5 z M 10.34375 29.90625 C 9.9888481 29.92844 9.660331 30.102635 9.46875 30.4375 C 9.1622191 30.970709 9.3144062 31.653203 9.84375 31.96875 C 10.375673 32.264978 11.065892 32.098286 11.375 31.5625 C 11.680243 31.025427 11.529346 30.375471 11 30.0625 C 10.800529 29.949482 10.55669 29.892938 10.34375 29.90625 z M 19.625 29.90625 C 19.482359 29.92412 19.350442 29.985872 19.21875 30.0625 C 18.688116 30.366455 18.503392 31.031867 18.8125 31.5625 C 19.117742 32.093134 19.781866 32.27657 20.3125 31.96875 C 20.843134 31.660932 21.02528 30.944612 20.71875 30.40625 C 20.487886 30.023731 20.05292 29.852638 19.625 29.90625 z M 15.09375 31.125 C 14.4794 31.125 13.96875 31.639514 13.96875 32.25 C 13.96875 32.864352 14.481976 33.375 15.09375 33.375 C 15.705524 33.375 16.21875 32.863064 16.21875 32.25 C 16.21875 31.638227 15.706813 31.125 15.09375 31.125 z";
	private static final String CYCLE_TIME_PASSED_ICON = "M 17.84375 7.75 C 16.971326 7.75 16.375 8.4400755 16.375 9.3125 C 16.375 10.173432 16.749104 10.855958 18.0625 10.875 C 24.301127 10.96037 29.75 16.208963 29.75 22.75 C 29.75 29.343242 24.343244 34.84375 17.75 34.84375 C 11.457851 34.84375 6.2409396 30.019877 5.78125 23.78125 L 8.5 23.78125 L 4.21875 16.75 L 0 23.78125 L 2.59375 23.78125 C 3.0590206 31.661621 9.589322 38 17.625 38 C 25.961119 38 32.78125 31.179541 32.78125 22.84375 C 32.78125 14.605804 26.170248 7.907279 17.96875 7.75 C 17.93526 7.748 17.878233 7.75 17.84375 7.75 z M 17.75 13.625 C 17.007276 13.625 16.375 14.351352 16.375 15.09375 L 16.375 23 L 12.46875 26.84375 C 11.894142 27.418033 11.924348 28.36289 12.46875 28.9375 C 13.013152 29.511785 13.988216 29.511785 14.5625 28.9375 L 18.9375 24.625 C 18.97099 24.59119 18.97012 24.56737 19 24.53125 C 19.237395 24.288929 19.3125 23.95953 19.3125 23.59375 L 19.3125 15.09375 C 19.3125 14.351352 18.711149 13.625 17.96875 13.625 L 17.75 13.625 z M 23.75 16.125 C 23.318391 16.21292 23.03125 16.635498 23.03125 17.09375 C 23.03125 17.617469 23.413781 18.0625 23.9375 18.0625 C 24.461218 18.0625 24.90625 17.617469 24.90625 17.09375 C 24.90625 16.570034 24.461218 16.125 23.9375 16.125 C 23.87203 16.125 23.811659 16.11244 23.75 16.125 z M 26.15625 21.90625 C 25.632715 21.90625 25.09375 22.320214 25.09375 22.84375 C 25.09375 23.367285 25.632715 23.78125 26.15625 23.78125 C 26.679786 23.78125 27.125 23.367285 27.125 22.84375 C 27.125 22.320214 26.679786 21.90625 26.15625 21.90625 z M 23.9375 27.46875 C 23.413781 27.46875 23.03125 27.913782 23.03125 28.4375 C 23.03125 28.961217 23.413781 29.375 23.9375 29.375 C 24.461218 29.375 24.90625 28.961217 24.90625 28.4375 C 24.90625 27.913782 24.461218 27.46875 23.9375 27.46875 z M 17.75 30.40625 C 17.226282 30.40625 16.78125 30.851282 16.78125 31.375 C 16.78125 31.898718 17.226282 32.4375 17.75 32.4375 C 18.273717 32.4375 18.6875 31.898718 18.6875 31.375 C 18.6875 30.851282 18.273717 30.40625 17.75 30.40625 z";
	private static final String TIME_TILL_PAUSE_ICON = "M 14.6875 7.75 C 7.1112274 7.867998 0.7284419 13.717231 0.0625 21.4375 C -0.6433206 29.671322 5.4246863 36.945911 13.65625 37.65625 C 21.892331 38.36207 29.133411 32.201443 29.84375 23.96875 C 29.92732 22.989635 29.91188 22.064588 29.8125 21.125 L 26.125 19.78125 C 26.45363 21.029142 26.61406 22.296557 26.5 23.65625 C 25.944378 30.024447 20.307955 34.787553 13.9375 34.25 C 7.5693037 33.690989 2.8686972 28.088077 3.40625 21.71875 C 3.964131 15.348294 9.5369227 10.648817 15.90625 11.1875 C 18.648223 11.428043 21.100107 12.604237 22.9375 14.375 L 21.09375 15.9375 L 29.03125 18.8125 L 27.53125 10.46875 L 25.53125 12.1875 C 23.122426 9.7583474 19.896924 8.097457 16.21875 7.78125 C 15.704701 7.73706 15.192585 7.74214 14.6875 7.75 z M 14.9375 12.6875 C 14.314118 12.6875 13.84375 13.157869 13.84375 13.78125 L 13.3125 22.125 L 9.875 24.3125 C 9.335188 24.62532 9.18718 25.338575 9.5 25.875 C 9.810561 26.41594 10.493697 26.59407 11.03125 26.28125 L 14.75 24.375 C 14.81098 24.384 14.873129 24.40625 14.9375 24.40625 C 15.8737 24.40735 16.625 23.62483 16.625 22.6875 L 16.0625 13.78125 C 16.0625 13.15787 15.56088 12.6875 14.9375 12.6875 z M 10.375 13.90625 C 10.229424 13.92428 10.072453 13.922066 9.9375 14 C 9.397689 14.31169 9.219559 15.053938 9.53125 15.59375 C 9.845199 16.134691 10.491438 16.31395 11.03125 16 C 11.572191 15.68831 11.811691 15.008561 11.5 14.46875 C 11.266232 14.063891 10.811727 13.852149 10.375 13.90625 z M 19.21875 13.90625 C 18.90216 13.9628 18.573954 14.165741 18.40625 14.46875 C 18.100207 15.01082 18.322007 15.68831 18.84375 16 C 19.384691 16.31169 20.09343 16.133562 20.40625 15.59375 C 20.71907 15.055067 20.479576 14.31169 19.9375 14 C 19.80283 13.92207 19.676562 13.924194 19.53125 13.90625 C 19.422272 13.89278 19.32428 13.88741 19.21875 13.90625 z M 7.0625 17.15625 C 6.7382336 17.21256 6.4565766 17.383856 6.28125 17.6875 C 5.9695598 18.227312 6.1499469 18.938309 6.6875 19.25 C 7.229571 19.56282 7.87581 19.385821 8.1875 18.84375 C 8.498061 18.303939 8.319932 17.59407 7.78125 17.28125 C 7.646015 17.20333 7.55188 17.174284 7.40625 17.15625 C 7.2970302 17.14272 7.1705898 17.13748 7.0625 17.15625 z M 22.46875 17.15625 C 22.323579 17.17428 22.194912 17.203333 22.0625 17.28125 C 21.521558 17.5952 21.37468 18.305067 21.6875 18.84375 C 22.00032 19.382432 22.647687 19.563949 23.1875 19.25 C 23.727312 18.939437 23.93895 18.227312 23.625 17.6875 C 23.392926 17.282641 22.904259 17.102149 22.46875 17.15625 z M 6.03125 21.59375 C 5.405611 21.59375 4.90625 22.065247 4.90625 22.6875 C 4.90625 23.31201 5.4089984 23.84375 6.03125 23.84375 C 6.6546307 23.84375 7.1863712 23.310879 7.1875 22.6875 C 7.18633 22.064118 6.6557605 21.59488 6.03125 21.59375 z M 23.84375 21.59375 C 23.218111 21.59605 22.75 22.066377 22.75 22.6875 C 22.7511 23.310879 23.219241 23.84375 23.84375 23.84375 C 24.467131 23.84495 24.96875 23.31201 24.96875 22.6875 C 24.96875 22.065247 24.467131 21.59375 23.84375 21.59375 z M 7.125 26 C 6.9792665 26.0198 6.8227351 26.137722 6.6875 26.21875 C 6.1476892 26.524794 5.9684309 27.225999 6.28125 27.75 C 6.5906819 28.290941 7.2436967 28.466811 7.78125 28.15625 C 8.322191 27.850205 8.56169 27.103442 8.25 26.5625 C 8.016232 26.158488 7.5622043 25.940605 7.125 26 z M 22.4375 26.0625 C 22.122619 26.11247 21.861556 26.269654 21.6875 26.5625 C 21.378068 27.100052 21.521558 27.84456 22.0625 28.15625 C 22.601182 28.465681 23.314439 28.288682 23.625 27.75 C 23.931045 27.209059 23.716015 26.514631 23.1875 26.21875 C 23.051982 26.14224 22.895964 26.08004 22.75 26.0625 C 22.640522 26.04936 22.542459 26.04584 22.4375 26.0625 z M 10.40625 29.25 C 10.33486 29.2524 10.257234 29.29376 10.1875 29.3125 C 9.9085597 29.38747 9.688224 29.542595 9.53125 29.8125 C 9.220687 30.353441 9.398818 31.064438 9.9375 31.375 C 10.477312 31.681044 11.190569 31.498402 11.5 30.96875 C 11.81282 30.427809 11.571065 29.78157 11.03125 29.46875 C 10.828821 29.352287 10.620431 29.242767 10.40625 29.25 z M 19.21875 29.3125 C 19.073369 29.33038 18.978702 29.391112 18.84375 29.46875 C 18.302808 29.78157 18.090043 30.428938 18.40625 30.96875 C 18.715683 31.507432 19.397689 31.688949 19.9375 31.375 C 20.477311 31.064438 20.71907 30.342148 20.40625 29.8125 C 20.171635 29.407641 19.654896 29.258875 19.21875 29.3125 z M 14.9375 30.46875 C 14.314118 30.46875 13.84375 30.96924 13.84375 31.59375 C 13.84375 32.218261 14.314118 32.75 14.9375 32.75 C 15.56088 32.75 16.0625 32.218261 16.0625 31.59375 C 16.0625 30.96924 15.56088 30.46875 14.9375 30.46875 z M 27.5 34.1875 L 27.5 44.34375 L 30.0625 44.34375 L 30.0625 34.1875 L 27.5 34.1875 z M 32.5625 34.1875 L 32.5625 44.34375 L 35.125 44.34375 L 35.125 34.1875 L 32.5625 34.1875 z";
	private static final String TIME_TILL_FINISH_ICON = "M 14.3125 7.75 C 6.9318243 7.8691726 0.71124938 13.765453 0.0625 21.5625 C -0.62509862 29.87821 5.2934497 37.220097 13.3125 37.9375 C 21.33595 38.65034 28.3705 32.439569 29.0625 24.125 C 29.143911 23.136148 29.159315 22.198932 29.0625 21.25 L 25.4375 19.875 C 25.757646 21.135302 25.923617 22.470535 25.8125 23.84375 C 25.271223 30.27528 19.76849 35.074149 13.5625 34.53125 C 7.3587101 33.96668 2.7888248 28.276421 3.3125 21.84375 C 3.855978 15.409939 9.2638601 10.67471 15.46875 11.21875 C 18.139934 11.461686 20.522544 12.649126 22.3125 14.4375 L 20.53125 16.03125 L 28.25 18.90625 L 26.8125 10.5625 L 24.84375 12.25 C 22.497113 9.7966887 19.333214 8.1006026 15.75 7.78125 C 15.249222 7.7366261 14.804546 7.7420554 14.3125 7.75 z M 14.53125 12.75 C 13.923963 12.75 13.40625 13.24542 13.40625 13.875 L 12.90625 22.25 L 9.625 24.53125 C 9.0991249 24.847182 8.9140063 25.489492 9.21875 26.03125 C 9.5212938 26.577571 10.195075 26.784682 10.71875 26.46875 L 14.34375 24.53125 C 14.403153 24.540348 14.468541 24.5625 14.53125 24.5625 C 15.44328 24.563648 16.1875 23.821652 16.1875 22.875 L 15.625 13.875 C 15.625 13.24542 15.138537 12.75 14.53125 12.75 z M 10.0625 13.9375 C 9.9206826 13.955714 9.7877196 14.015048 9.65625 14.09375 C 9.1303749 14.40854 8.9463563 15.11107 9.25 15.65625 C 9.5558437 16.202571 10.224125 16.379571 10.75 16.0625 C 11.276975 15.74771 11.459894 15.045181 11.15625 14.5 C 10.928517 14.091115 10.487952 13.882861 10.0625 13.9375 z M 18.6875 13.9375 C 18.384392 13.999225 18.132123 14.193978 17.96875 14.5 C 17.670608 15.047461 17.804228 15.74771 18.3125 16.0625 C 18.839476 16.37729 19.539005 16.20143 19.84375 15.65625 C 20.148495 15.11221 19.965576 14.40854 19.4375 14.09375 C 19.306306 14.015048 19.141559 13.955624 19 13.9375 C 18.89383 13.923906 18.788536 13.916928 18.6875 13.9375 z M 6.84375 17.21875 C 6.5269169 17.280237 6.2020494 17.505837 6.03125 17.8125 C 5.7276063 18.35768 5.9450748 19.06021 6.46875 19.375 C 6.9968258 19.690932 7.6651063 19.484961 7.96875 18.9375 C 8.2712929 18.392319 8.1185251 17.690932 7.59375 17.375 C 7.462006 17.296298 7.2981185 17.236963 7.15625 17.21875 C 7.0498486 17.205094 6.9493607 17.198257 6.84375 17.21875 z M 21.875 17.21875 C 21.733579 17.236964 21.628994 17.296298 21.5 17.375 C 20.973024 17.692071 20.789005 18.39346 21.09375 18.9375 C 21.398495 19.48154 22.067875 19.692071 22.59375 19.375 C 23.119625 19.06135 23.274594 18.35768 22.96875 17.8125 C 22.742667 17.403614 22.299266 17.164111 21.875 17.21875 z M 5.84375 21.71875 C 5.2342625 21.71875 4.78125 22.24656 4.78125 22.875 C 4.78125 23.505721 5.2375631 24 5.84375 24 C 6.4510371 24 6.96765 23.504581 6.96875 22.875 C 6.9676424 22.245419 6.4521374 21.71989 5.84375 21.71875 z M 23.1875 21.71875 C 22.578013 21.721047 22.09375 22.247701 22.09375 22.875 C 22.094858 23.504581 22.579113 24 23.1875 24 C 23.794787 24.001148 24.3125 23.505721 24.3125 22.875 C 24.3125 22.24656 23.794787 21.71875 23.1875 21.71875 z M 6.90625 26.21875 C 6.7642784 26.238739 6.6004939 26.355666 6.46875 26.4375 C 5.9428747 26.746588 5.7265063 27.408288 6.03125 27.9375 C 6.3326931 28.48382 7.0700749 28.6574 7.59375 28.34375 C 8.1207259 28.034662 8.3036437 27.32757 8 26.78125 C 7.7722672 26.373221 7.3321658 26.158765 6.90625 26.21875 z M 21.875 26.25 C 21.56825 26.300463 21.263311 26.485493 21.09375 26.78125 C 20.792306 27.324149 20.973024 28.02896 21.5 28.34375 C 22.024775 28.65626 22.666206 28.48154 22.96875 27.9375 C 23.266892 27.391179 23.108625 26.736323 22.59375 26.4375 C 22.461731 26.360229 22.298446 26.267714 22.15625 26.25 C 22.049603 26.236715 21.97725 26.233182 21.875 26.25 z M 10.15625 29.5 C 10.086699 29.502473 9.9741843 29.512303 9.90625 29.53125 C 9.6345111 29.606958 9.4029227 29.78991 9.25 30.0625 C 8.9474571 30.608821 9.1314749 31.31135 9.65625 31.625 C 10.182125 31.934088 10.854806 31.722416 11.15625 31.1875 C 11.460995 30.641179 11.275875 29.972182 10.75 29.65625 C 10.552797 29.538632 10.364902 29.492696 10.15625 29.5 z M 18.71875 29.53125 C 18.577122 29.549305 18.44397 29.577831 18.3125 29.65625 C 17.785525 29.972182 17.598206 30.64232 17.90625 31.1875 C 18.207694 31.73154 18.911624 31.942071 19.4375 31.625 C 19.963375 31.31135 20.148495 30.597416 19.84375 30.0625 C 19.615193 29.653614 19.143635 29.477092 18.71875 29.53125 z M 14.53125 30.71875 C 13.923963 30.71875 13.40625 31.21303 13.40625 31.84375 C 13.40625 32.474471 13.923963 33 14.53125 33 C 15.138537 33 15.625 32.474471 15.625 31.84375 C 15.625 31.21303 15.138537 30.71875 14.53125 30.71875 z M 36.75 31.1875 L 26 42.46875 L 21.96875 38.28125 L 18.3125 42.09375 L 25.8125 50 L 40.34375 34.96875 L 36.75 31.1875 z";

	private SVGPath cycleTimeShape;
	private SVGPath cycleTimePassedShape;
	private SVGPath timeTillPauseShape;
	private SVGPath timeTillFinishedShape;
	
	private Region loading;
	
	private Label lblZRest;
	private Label lblZRestValue;

	private Label lblCycleTimeMessage;
	private Label lblCycleTimePassedMessage;
	private Label lblTimeTillPauseMessage;
	private Label lblTimeTillFinishedMessage;
	
	private Label lblCycleTime;
	private Label lblCycleTimePassed;
	private Label lblTimeTillPause;
	private Label lblTimeTillFinished;
	
	private Label lblAmountFinished;
	private Label lblTotalAmount;
		
	private Path piePiecePath;
	
	private Label lblMessage;
	private Label lblAlarmMessage;
	
	private Button btnPause;
	private Button btnStart;
	private Button btnReset;
	private Button btnStop;
	
	private HBox btnHBox;
	
	private AutomatePresenter presenter;
		
	public AutomateView() {
		totalAmount = -1;
		finishedAmount = 0;
		build();
	}
	
	public void setPresenter(final AutomatePresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		
		// ----- general structure -----
		// top (processflow)
		top = new StackPane();
		top.setPrefHeight(HEIGHT_TOP);
		top.setPrefWidth(WIDTH);
		top.getStyleClass().add("top-panel");
		// bottom (dashboard)
		bottomTop = new HBox();
		bottomTop.setPrefHeight(HEIGHT_BOTTOM_TOP);
		bottomTop.setAlignment(Pos.CENTER);
		bottom = new VBox();
		bottom.setAlignment(Pos.TOP_CENTER);
		bottom.setPrefHeight(HEIGHT_BOTTOM);
		bottom.setPrefWidth(WIDTH);
		VBox.setVgrow(bottom, Priority.ALWAYS);
		bottom.getStyleClass().add("automate-bottom");
		this.getChildren().addAll(top, bottom);
		bottom.getChildren().add(bottomTop);
		bottomBottom = new HBox();
		bottom.getChildren().add(bottomBottom);
		
		
		// ----- bottom-top-left: status -----
		lblMessage = new Label();
		lblMessage.getStyleClass().addAll("teach-msg", "message-normal");
		lblMessage.setPrefSize(260, 100);
		lblMessage.setWrapText(true);
		lblAlarmMessage = new Label();
		lblAlarmMessage.getStyleClass().addAll("teach-msg", "message-error");
		lblAlarmMessage.setPrefSize(260, 100);
		lblAlarmMessage.setWrapText(true);
		StackPane spMessages = new StackPane();
		spMessages.setPrefWidth(300);
		spMessages.getChildren().add(lblMessage);
		spMessages.getChildren().add(lblAlarmMessage);
		lblAlarmMessage.setVisible(false);
		
		// ----- bottom-top-center: amount -----
		StackPane pane = new StackPane();
		Region circleBack = new Region();
		double circleBackR = PROGRESS_RADIUS * 2 + 14;
		pane.setPrefSize(200, circleBackR);
		pane.setMaxSize(200, circleBackR);
		pane.setMinSize(200, circleBackR);
		pane.setPadding(new Insets(0, (200 - circleBackR) / 2, 0, (200 - circleBackR) / 2));
		circleBack.setPrefSize(circleBackR, circleBackR);
		circleBack.getStyleClass().add("circle-back");
		circleBack.toBack();
		piePiecePath = new Path();
		piePiecePath.getStyleClass().add("automate-progress");
		piePiecePath.setStrokeType(StrokeType.INSIDE);
		Pane pane2 = new Pane();
		pane2.getChildren().add(piePiecePath);
		pane2.setPrefSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		pane2.setMaxSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		pane2.setMinSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		StackPane.setMargin(pane2, new Insets(8, 7, 8, 7));
		pane.getChildren().add(circleBack);
		pane.getChildren().add(pane2);
		pane.setAlignment(Pos.CENTER);
		pane2.setTranslateX(PROGRESS_RADIUS);
		pane2.setTranslateY(PROGRESS_RADIUS);
		lblAmountFinished = new Label();
		lblTotalAmount = new Label();
		lblTotalAmount.setPrefWidth(PROGRESS_RADIUS * 2);
		lblTotalAmount.getStyleClass().add("lbl-total");
		lblAmountFinished.getStyleClass().add("finished-amount");
		pane.getChildren().add(lblAmountFinished);
		pane.getChildren().add(lblTotalAmount);
		StackPane.setMargin(lblTotalAmount, new Insets(80, 0, 0, 40));
		
		// ----- bottom top right: z-rest and buttons
		// z-rest (and loading)
		loading = new Region();
		loading.setPrefSize(40, 40);
		loading.setMaxSize(40, 40);
		lblZRest = new Label();
		lblZRest.getStyleClass().add("lbl-z-rest");
		lblZRest.setPrefSize(100, 40);
		lblZRest.setMaxSize(100, 40);
		lblZRest.setMinSize(100, 40);
		lblZRest.setText("zakt nog");
		lblZRestValue = new Label();
		lblZRestValue.getStyleClass().add("lbl-z-rest");
		lblZRestValue.getStyleClass().add("lbl-z-rest-val");
		lblZRestValue.setPrefSize(100, 40);
		lblZRestValue.setMaxSize(100, 40);
		lblZRestValue.setMinSize(100, 40);
		HBox hboxZRest = new HBox();
		hboxZRest.setPrefSize(280, 40);
		hboxZRest.setMaxSize(280, 40);
		hboxZRest.setMinSize(280, 40);
		hboxZRest.setAlignment(Pos.CENTER);
		hboxZRest.getChildren().add(lblZRest);
		hboxZRest.getChildren().add(loading);
		hboxZRest.getChildren().add(lblZRestValue);
		setMargin(hboxZRest, new Insets(0, 0, 10, 0));
		lblZRestValue.setVisible(false);
		lblZRest.setVisible(false);
		// buttons
		btnStart = new Button();
		btnStart.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnStart.getStyleClass().add("automate-btn");
		Text txtStart = new Text(Translator.getTranslation(START));
		txtStart.getStyleClass().add("automate-btn-text");
		btnStart.setGraphic(txtStart);
		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.clickedStart();
			}
		});
		btnReset = new Button();
		btnReset.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnReset.getStyleClass().add("automate-btn");
		Text txtRestart = new Text(Translator.getTranslation(RESET));
		txtRestart.getStyleClass().add("automate-btn-text");
		btnReset.setGraphic(txtRestart);
		btnReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.clickedReset();
			}
		});
		HBox.setMargin(btnReset, new Insets(0, 15, 0, 0));
		btnPause = new Button();
		btnPause.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnPause.getStyleClass().add("automate-btn");
		Text txtPause = new Text(Translator.getTranslation(PAUSE));
		txtPause.getStyleClass().add("automate-btn-text");
		btnPause.setGraphic(txtPause);
		btnPause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.clickedPause();
			}
		});
		btnStop = new Button();
		btnStop.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnStop.getStyleClass().add("automate-btn");
		Text txtStop = new Text(Translator.getTranslation(STOP));
		txtStop.getStyleClass().add("automate-btn-text");
		btnStop.setGraphic(txtStop);
		HBox.setMargin(btnStop, new Insets(0, 15, 0, 0));
		btnStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.clickedStop();
			}
		});
		btnHBox = new HBox();
		btnHBox.setPrefWidth(300);
		btnHBox.setMaxWidth(300);
		btnHBox.setMinWidth(300);
		btnHBox.setAlignment(Pos.CENTER);
		setNotRunningButtons();
		VBox rightVBox = new VBox();
		rightVBox.getChildren().add(hboxZRest);
		rightVBox.getChildren().add(btnHBox);
		rightVBox.setPrefWidth(300);
		rightVBox.setMaxWidth(300);
		rightVBox.setMinWidth(300);
		rightVBox.setAlignment(Pos.CENTER);
		
		bottomTop.getChildren().add(spMessages);
		bottomTop.getChildren().add(pane);
		bottomTop.getChildren().add(rightVBox);
		
		
		// bottom-bottom
		VBox vboxCycleTime = new VBox();
		vboxCycleTime.getStyleClass().add("time-vbox");
		cycleTimeShape = new SVGPath();
		cycleTimeShape.setContent(CYCLE_TIME_ICON);
		cycleTimeShape.getStyleClass().add("automate-icon");
		Pane iconPane = new Pane();
		iconPane.setPrefSize(30.156, ICON_HEIGHT);
		iconPane.setMaxSize(30.156, ICON_HEIGHT);
		iconPane.getChildren().add(cycleTimeShape);
		lblCycleTimeMessage = new Label(Translator.getTranslation(CYCLE_TIME));
		lblCycleTimeMessage.getStyleClass().add("automate-info-lbl");
		lblCycleTime = new Label();
		lblCycleTime.getStyleClass().add("automate-time-lbl");
		setCycleTime(null);
		vboxCycleTime.getChildren().addAll(iconPane, lblCycleTimeMessage, lblCycleTime);
		VBox.setMargin(iconPane, new Insets(7, 0, 0, 0));
		vboxCycleTime.setAlignment(Pos.TOP_CENTER);
		vboxCycleTime.setPrefWidth(200);
		vboxCycleTime.setPrefHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_TOP);
		bottomBottom.getChildren().add(vboxCycleTime);
		
		VBox vboxCycleTimePassed = new VBox();
		vboxCycleTimePassed.getStyleClass().add("time-vbox");
		cycleTimePassedShape = new SVGPath();
		cycleTimePassedShape.setContent(CYCLE_TIME_PASSED_ICON);
		cycleTimePassedShape.getStyleClass().add("automate-icon");
		Pane iconPane2 = new Pane();
		iconPane2.setPrefSize(32.781, ICON_HEIGHT);
		iconPane2.setMaxSize(32.781, ICON_HEIGHT);
		iconPane2.getChildren().add(cycleTimePassedShape);
		lblCycleTimePassedMessage = new Label(Translator.getTranslation(CYCLE_TIME_PASSED));
		lblCycleTimePassedMessage.getStyleClass().add("automate-info-lbl");
		lblCycleTimePassed = new Label();
		lblCycleTimePassed.getStyleClass().addAll("automate-time-lbl", "blue-time");
		setCycleTimePassed(null);
		vboxCycleTimePassed.getChildren().addAll(iconPane2, lblCycleTimePassedMessage, lblCycleTimePassed);
		VBox.setMargin(iconPane2, new Insets(7, 0, 0, 0));
		vboxCycleTimePassed.setAlignment(Pos.TOP_CENTER);
		vboxCycleTimePassed.setPrefWidth(200);
		vboxCycleTimePassed.setPrefHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_TOP);
		bottomBottom.getChildren().add(vboxCycleTimePassed);
		
		VBox vboxTimeTillPause = new VBox();
		vboxTimeTillPause.getStyleClass().add("time-vbox");
		timeTillPauseShape = new SVGPath();
		timeTillPauseShape.setContent(TIME_TILL_PAUSE_ICON);
		timeTillPauseShape.getStyleClass().add("automate-icon");
		Pane iconPane3 = new Pane();
		iconPane3.setPrefSize(35.118, ICON_HEIGHT);
		iconPane3.setMaxSize(35.118, ICON_HEIGHT);
		iconPane3.getChildren().add(timeTillPauseShape);
		lblTimeTillPauseMessage = new Label(Translator.getTranslation(TIME_TILL_INTERVENTION));
		lblTimeTillPauseMessage.getStyleClass().add("automate-info-lbl");
		lblTimeTillPause = new Label();
		lblTimeTillPause.getStyleClass().add("automate-time-lbl");
		setTimeTillPause(null);
		vboxTimeTillPause.getChildren().addAll(iconPane3, lblTimeTillPauseMessage, lblTimeTillPause);
		VBox.setMargin(iconPane3, new Insets(7, 0, 0, 0));
		vboxTimeTillPause.setAlignment(Pos.TOP_CENTER);
		vboxTimeTillPause.setPrefWidth(200);
		vboxTimeTillPause.setPrefHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_TOP);
		bottomBottom.getChildren().add(vboxTimeTillPause);
		
		VBox vboxTimeTillFinished = new VBox();
		vboxTimeTillFinished.getStyleClass().add("time-vbox");
		timeTillFinishedShape = new SVGPath();
		timeTillFinishedShape.setContent(TIME_TILL_FINISH_ICON);
		timeTillFinishedShape.getStyleClass().add("automate-icon");
		Pane iconPane4 = new Pane();
		iconPane4.setPrefSize(40.336, ICON_HEIGHT);
		iconPane4.setMaxSize(40.336, ICON_HEIGHT);
		iconPane4.getChildren().add(timeTillFinishedShape);
		lblTimeTillFinishedMessage = new Label(Translator.getTranslation(TIME_TILL_FINISHED));
		lblTimeTillFinishedMessage.getStyleClass().add("automate-info-lbl");
		lblTimeTillFinished = new Label();
		lblTimeTillFinished.getStyleClass().addAll("automate-time-lbl", "automate-time-hl-lbl");
		setTimeTillFinished(null);
		vboxTimeTillFinished.getChildren().addAll(iconPane4, lblTimeTillFinishedMessage, lblTimeTillFinished);
		VBox.setMargin(iconPane4, new Insets(7, 0, 0, 0));
		vboxTimeTillFinished.setAlignment(Pos.TOP_CENTER);
		vboxTimeTillFinished.setPrefWidth(200);
		vboxTimeTillFinished.setPrefHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_TOP);
		bottomBottom.getChildren().add(vboxTimeTillFinished);
		setStatus(Translator.getTranslation(DEFAULT_STATUTS_MSG));
		piePiecePath.getStyleClass().add("automate-progress-green");
		setProcessStopped();
		
	}
	
	public void setStatus(final String message) {
		lblMessage.setText(message);
	}

	public void setAlarmStatus(final String message) {
		lblAlarmMessage.setText(message);
		lblAlarmMessage.setVisible(true);
	}
	
	public void hideAlarmMessage() {
		lblAlarmMessage.setVisible(false);
	}
	
	private void setPercentage(final int percentage) {
		
		double percentaged = percentage;
		
		if (percentage == 100) {
			percentaged = 99.999;
		}
		
		if ((percentaged < 0) || (percentaged > 100)) {
			throw new IllegalArgumentException("Illegal percentage value");
		}
		
		piePiecePath.getElements().clear();
		
		double endX = 0;
		double endY = 0;
		double endXInner = 0;
		double endYInner = 0;
		double corner = ((percentaged) / 100) * (Math.PI * 2);
		
		endX = PROGRESS_RADIUS * Math.sin(corner);
		endXInner = PROGRESS_RADIUS_INNER * Math.sin(corner);
		endY = PROGRESS_RADIUS * Math.cos(corner);
		endYInner = PROGRESS_RADIUS_INNER * Math.cos(corner);
		
		MoveTo moveTo = new MoveTo(0, -PROGRESS_RADIUS);
		LineTo vLine = new LineTo(0, -PROGRESS_RADIUS_INNER);
		ArcTo innerArc = new ArcTo();
		innerArc.setX(endXInner);
		innerArc.setY(-endYInner);
		innerArc.setRadiusX(PROGRESS_RADIUS_INNER);
		innerArc.setRadiusY(PROGRESS_RADIUS_INNER);
		if (percentage > 50) {
			innerArc.setLargeArcFlag(true);
		} else {
			innerArc.setLargeArcFlag(false);
		}
		innerArc.setSweepFlag(true);
		
		LineTo dLine = new LineTo(endX, -endY);
		MoveTo moveTo2 = new MoveTo(endX, -endY);
		ArcTo arc = new ArcTo();
		arc.setX(0);
		arc.setY(-PROGRESS_RADIUS);
		arc.setRadiusX(PROGRESS_RADIUS);
		arc.setRadiusY(PROGRESS_RADIUS);
		if (percentage > 50) {
			arc.setLargeArcFlag(true);
		} else {
			arc.setLargeArcFlag(false);
		}
		arc.setSweepFlag(false);
		
		piePiecePath.getElements().add(moveTo);
		piePiecePath.getElements().add(vLine);
		piePiecePath.getElements().add(innerArc);
		piePiecePath.getElements().add(dLine);
		piePiecePath.getElements().add(moveTo2);
		piePiecePath.getElements().add(arc);
	}
	
	public void setCycleTimePassed(final String timeString) {
		if (timeString == null) {
			lblCycleTimePassed.setText(Translator.getTranslation(UNKNOWN));
		} else {
			lblCycleTimePassed.setText(timeString);
		}
	}
	
	public void setTimeTillPause(final String timeString) {
		if (timeString == null) {
			lblTimeTillPause.setText(Translator.getTranslation(UNKNOWN));
		} else {
			lblTimeTillPause.setText(timeString);
		}
	}
	
	public void setTimeTillFinished(final String timeString) {
		if (timeString == null) {
			lblTimeTillFinished.setText(Translator.getTranslation(UNKNOWN));
		} else {
			lblTimeTillFinished.setText(timeString);
		}
	}

	public void setCycleTime(final String timeString) {
		if (timeString == null) {
			lblCycleTime.setText(Translator.getTranslation(UNKNOWN));
		} else {
			lblCycleTime.setText(timeString);
		}
	}
	
	public void setTotalAmount(final int amount) {
		totalAmount = amount;
		lblTotalAmount.setText("/" + amount);
	}
	
	public void setFinishedAmount(final int amount) {
		finishedAmount = amount;
		lblAmountFinished.setText("" + amount);
		if ((totalAmount >= 0) && (finishedAmount >= 0)) {
			setPercentage((int) Math.floor(((double) finishedAmount / (double) totalAmount) * 100));
		}
	}
	
	public void addNodeToTop(final Node node) {
		this.top.getChildren().add(node);
	}
	
	public void removeNodeFromTop(final Node node) {
		this.top.getChildren().remove(node);
	}
	
	public void setTop(final Node node) {
		this.top.getChildren().clear();
		this.top.getChildren().add(node);
	}
	
	public void addNodeToBottom(final Node node) {
		this.bottom.getChildren().add(node);
	}
	
	public void removeNodeFromBottom(final Node node) {
		this.bottom.getChildren().remove(node);
	}
	
	public void setBottom(final Node bottom) {
		this.bottom.getChildren().clear();
		this.bottom.getChildren().add(bottom);
	}
	
	public void setProcessPaused() {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		loading.getStyleClass().add("loading");
	}
	
	public void setProcessRunning() {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		loading.getStyleClass().add("loading");
	}
	
	public void setProcessStopped() {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		loading.getStyleClass().add("loading-inactive");
	}
	
	public void setZRest(final double zrest) {
		if (zrest > 0) {
			lblZRestValue.setText(zrest + " mm");
			lblZRestValue.setVisible(true);
			lblZRest.setVisible(true);
		} else {
			lblZRestValue.setVisible(false);
			lblZRest.setVisible(false);
		}
	}
	
	public void setRunningButtons() {
		btnHBox.getChildren().clear();
		btnHBox.getChildren().add(btnStop);
		btnHBox.getChildren().add(btnPause);
	}
	
	public void setNotRunningButtons() {
		btnHBox.getChildren().clear();
		btnHBox.getChildren().add(btnReset);
		btnHBox.getChildren().add(btnStart);
	}
	
}
