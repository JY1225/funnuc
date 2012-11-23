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
	
	public static final double ICON_WIDTH = 49.5;
	public static final double ICON_HEIGHT = 55.813;
	
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BTN_HEIGHT = 40;
	
	private int totalAmount;
	private int finishedAmount;
	
	private StackPane top;
	private VBox bottom;
	private HBox bottomTop;
	private HBox bottomBottom;
	private static final String cycleTimePath = "M 19.15625 0.0625 C 16.413031 0.0625 14.1875 2.293732 14.1875 5.03125 C 14.1875 6.657231 15.016008 8.0965197 16.21875 9 C 14.011349 9.3947391 11.973187 10.233434 10.15625 11.40625 L 9.09375 9.96875 L 9.75 9.46875 C 10.030734 9.2621181 10.145556 8.8475111 9.9375 8.5625 L 9.3125 7.6875 C 9.104442 7.4024908 8.658585 7.3544428 8.375 7.5625 L 5.46875 9.71875 C 5.183741 9.9268075 5.107293 10.30874 5.3125 10.59375 L 5.96875 11.4375 C 6.176807 11.72251 6.560164 11.800382 6.84375 11.59375 L 7.53125 11.09375 L 8.5625 12.5 C 4.83743 15.562429 2.46875 20.234646 2.46875 25.4375 C 2.46875 34.658991 9.9304816 42.09375 19.15625 42.09375 C 28.376314 42.09235 35.842324 34.661841 35.84375 25.4375 C 35.84375 20.254598 33.514777 15.62493 29.8125 12.5625 L 30.84375 11.125 L 31.5 11.625 C 31.782159 11.833057 32.219469 11.755185 32.4375 11.46875 L 33.03125 10.625 C 33.239306 10.34284 33.201235 9.9580575 32.90625 9.75 L 30 7.59375 C 29.716415 7.3856928 29.307507 7.4337408 29.09375 7.71875 L 28.5 8.59375 C 28.291942 8.8773346 28.328589 9.2606925 28.625 9.46875 L 29.34375 10 L 28.25 11.4375 C 26.425938 10.24901 24.355203 9.3990141 22.125 9 C 23.333442 8.0950941 24.12215 6.655806 24.125 5.03125 C 24.125 2.292306 21.888067 0.0625 19.15625 0.0625 z M 19.15625 1.375 C 21.188368 1.3779 22.8409 3.000556 22.84375 5.03125 C 22.84085 6.729908 21.686853 8.1378359 20.125 8.5625 L 20.125 6.0625 L 20.96875 6.0625 C 21.319311 6.0625 21.625 5.756811 21.625 5.40625 L 21.625 4.375 C 21.625 4.023013 21.319311 3.71875 20.96875 3.71875 L 17.34375 3.71875 C 16.991763 3.71875 16.71875 4.023013 16.71875 4.375 L 16.71875 5.40625 C 16.71875 5.756811 16.991763 6.09375 17.34375 6.09375 L 18.21875 6.09375 L 18.21875 8.59375 C 16.648346 8.1676603 15.501425 6.731333 15.5 5.03125 C 15.5029 3.000556 17.122704 1.37785 19.15625 1.375 z M 19.15625 12.5625 C 26.252992 12.57675 31.990025 18.339332 32 25.4375 C 31.99 32.534243 26.252992 38.266999 19.15625 38.28125 C 12.05523 38.26699 6.32105 32.537093 6.3125 25.4375 C 6.32389 18.337905 12.05523 12.5739 19.15625 12.5625 z M 19.15625 13.84375 C 18.477925 13.84375 17.9375 14.385603 17.9375 15.0625 C 17.9375 15.739398 18.476501 16.3125 19.15625 16.3125 C 19.834573 16.3125 20.40625 15.739398 20.40625 15.0625 C 20.40625 14.385603 19.831723 13.84375 19.15625 13.84375 z M 13.875 15.25 C 13.716619 15.26968 13.52178 15.290205 13.375 15.375 C 12.78788 15.714162 12.595487 16.475381 12.9375 17.0625 L 17.03125 26.65625 C 17.715273 27.83904 19.220611 28.243672 20.40625 27.5625 C 21.587615 26.879902 21.962423 25.34189 21.28125 24.15625 L 15.0625 15.8125 C 14.809198 15.37216 14.350141 15.19095 13.875 15.25 z M 24.25 15.25 C 23.857137 15.27767 23.495007 15.445551 23.28125 15.8125 C 22.943513 16.399622 23.131629 17.192088 23.71875 17.53125 C 24.301594 17.870411 25.069937 17.649621 25.40625 17.0625 C 25.743987 16.476806 25.52747 15.715587 24.9375 15.375 C 24.716262 15.247814 24.485717 15.233401 24.25 15.25 z M 10.15625 19.03125 C 9.7633726 19.05844 9.369116 19.22769 9.15625 19.59375 C 8.815662 20.183719 9.037879 20.974764 9.625 21.3125 C 10.212118 21.651661 10.942087 21.430871 11.28125 20.84375 C 11.618986 20.255204 11.429445 19.526661 10.84375 19.1875 C 10.62358 19.059781 10.391975 19.014933 10.15625 19.03125 z M 27.96875 19.03125 C 27.810458 19.05087 27.647492 19.10306 27.5 19.1875 C 26.915731 19.526661 26.721913 20.256629 27.0625 20.84375 C 27.400236 21.432296 28.167155 21.654512 28.75 21.3125 C 29.33712 20.976188 29.499686 20.183719 29.15625 19.59375 C 28.900809 19.153409 28.443625 18.972399 27.96875 19.03125 z M 8.8125 24.1875 C 8.131325 24.1875 7.59375 24.762027 7.59375 25.4375 C 7.59515 26.114399 8.135601 26.622149 8.8125 26.625 C 9.492247 26.6264 10.061074 26.117249 10.0625 25.4375 C 10.0596 24.757752 9.486548 24.1875 8.8125 24.1875 z M 29.5 24.1875 C 28.828802 24.1875 28.28125 24.762027 28.28125 25.4375 C 28.28125 26.117249 28.828802 26.627849 29.5 26.625 C 30.1826 26.6279 30.71875 26.097298 30.71875 25.4375 C 30.71875 24.757752 30.169775 24.190349 29.5 24.1875 z M 10.0625 29.375 C 9.9044296 29.39447 9.771423 29.415915 9.625 29.5 C 9.037879 29.839162 8.784413 30.60323 9.125 31.1875 C 9.465586 31.774621 10.225379 31.996837 10.8125 31.65625 C 11.401045 31.317089 11.621836 30.589969 11.28125 30 C 11.026878 29.561798 10.536707 29.316618 10.0625 29.375 z M 28.0625 29.375 C 27.669748 29.40295 27.274476 29.633049 27.0625 30 C 26.723338 30.587121 26.91003 31.317089 27.5 31.65625 C 28.084269 31.995413 28.815662 31.774621 29.15625 31.1875 C 29.496836 30.600379 29.334271 29.842012 28.75 29.5 C 28.529829 29.373883 28.298151 29.358234 28.0625 29.375 z M 13.90625 33.15625 C 13.513568 33.1808 13.149476 33.379487 12.9375 33.75 C 12.598338 34.339969 12.789306 35.088363 13.375 35.4375 C 13.963546 35.765261 14.720487 35.592821 15.0625 35 C 15.400236 34.405755 15.241946 33.658787 14.65625 33.3125 C 14.435545 33.187452 14.141858 33.14152 13.90625 33.15625 z M 24.1875 33.15625 C 24.029675 33.17602 23.864461 33.227705 23.71875 33.3125 C 23.131629 33.648812 22.939236 34.41288 23.28125 35 C 23.618986 35.58712 24.350379 35.778087 24.9375 35.4375 C 25.52462 35.096914 25.74541 34.314421 25.40625 33.71875 C 25.150809 33.295512 24.660973 33.096931 24.1875 33.15625 z M 19.15625 34.5 C 18.476501 34.5 17.9375 35.074527 17.9375 35.75 C 17.9375 36.42975 18.479351 37 19.15625 37 C 19.833148 37 20.40625 36.428325 20.40625 35.75 C 20.40625 35.073103 19.834574 34.5 19.15625 34.5 z";
	private static final String cylceTimePassedPath = "M 19.8125 8.5 C 18.847205 8.5 18.1875 9.2847054 18.1875 10.25 C 18.1875 11.202579 18.578041 11.94768 20.03125 11.96875 C 26.933986 12.06321 32.96875 17.856413 32.96875 25.09375 C 32.96875 32.388849 27.013854 38.5 19.71875 38.5 C 12.756795 38.5 6.977374 33.152735 6.46875 26.25 L 9.46875 26.25 L 4.71875 18.46875 L 0.0625 26.25 L 2.9375 26.25 C 3.452299 34.969244 10.671415 41.96875 19.5625 41.96875 C 28.786008 41.96875 36.34375 34.441893 36.34375 25.21875 C 36.34375 16.103866 29.012056 8.6740213 19.9375 8.5 C 19.90045 8.49784 19.85065 8.5 19.8125 8.5 z M 19.71875 15 C 18.896963 15 18.1875 15.803573 18.1875 16.625 L 18.1875 25.375 L 13.875 29.625 C 13.239224 30.260416 13.272645 31.332973 13.875 31.96875 C 14.477354 32.604167 15.552083 32.604167 16.1875 31.96875 L 21 27.1875 C 21.03705 27.15009 21.06069 27.13371 21.09375 27.09375 C 21.356415 26.825634 21.4375 26.435967 21.4375 26.03125 L 21.4375 16.625 C 21.4375 15.803573 20.758928 15 19.9375 15 L 19.71875 15 z M 26.34375 17.78125 C 25.866196 17.87853 25.53125 18.336717 25.53125 18.84375 C 25.53125 19.423219 25.983031 19.90625 26.5625 19.90625 C 27.141968 19.90625 27.625 19.423219 27.625 18.84375 C 27.625 18.264284 27.141968 17.78125 26.5625 17.78125 C 26.49007 17.78125 26.411972 17.76735 26.34375 17.78125 z M 29 24.1875 C 28.420733 24.1875 27.84375 24.639483 27.84375 25.21875 C 27.84375 25.798016 28.420733 26.25 29 26.25 C 29.579266 26.25 30.0625 25.798016 30.0625 25.21875 C 30.0625 24.639483 29.579266 24.1875 29 24.1875 z M 26.5625 30.34375 C 25.983031 30.34375 25.53125 30.826782 25.53125 31.40625 C 25.53125 31.985717 25.983031 32.4375 26.5625 32.4375 C 27.141968 32.4375 27.625 31.985717 27.625 31.40625 C 27.625 30.826782 27.141968 30.34375 26.5625 30.34375 z M 19.71875 33.59375 C 19.139282 33.59375 18.625 34.076782 18.625 34.65625 C 18.625 35.235718 19.139282 35.8125 19.71875 35.8125 C 20.298218 35.8125 20.75 35.235718 20.75 34.65625 C 20.75 34.076782 20.298218 33.59375 19.71875 33.59375 z";
	private static final String timeTillPausePath = "M 18.875 8.6875 C 10.492226 8.8180594 3.424332 15.301651 2.6875 23.84375 C 1.906544 32.954071 8.6421766 40.995295 17.75 41.78125 C 26.862821 42.562206 34.870295 35.765322 35.65625 26.65625 C 35.74871 25.572908 35.73496 24.539609 35.625 23.5 L 31.53125 22 C 31.894863 23.380732 32.063702 24.808066 31.9375 26.3125 C 31.322731 33.358598 25.111098 38.626025 18.0625 38.03125 C 11.016403 37.412732 5.811474 31.203598 6.40625 24.15625 C 7.023518 17.107652 13.171402 11.903974 20.21875 12.5 C 23.252609 12.766149 25.967015 14.071987 28 16.03125 L 25.96875 17.75 L 34.75 20.9375 L 33.09375 11.71875 L 30.875 13.625 C 28.209753 10.937261 24.632218 9.0998687 20.5625 8.75 C 19.99373 8.701112 19.433853 8.678797 18.875 8.6875 z M 19.15625 14.15625 C 18.466509 14.15625 17.9375 14.68526 17.9375 15.375 L 17.34375 24.59375 L 13.5625 27.03125 C 12.965224 27.37737 12.77888 28.156472 13.125 28.75 C 13.468621 29.348524 14.248974 29.53362 14.84375 29.1875 L 18.9375 27.09375 C 19.00497 27.10375 19.085027 27.125 19.15625 27.125 C 20.19211 27.1262 21.03125 26.25586 21.03125 25.21875 L 20.40625 15.375 C 20.40625 14.68526 19.84599 14.15625 19.15625 14.15625 z M 14.09375 15.5 C 13.932678 15.51995 13.774319 15.538775 13.625 15.625 C 13.027725 15.96987 12.81138 16.777725 13.15625 17.375 C 13.50362 17.973525 14.246475 18.15987 14.84375 17.8125 C 15.442276 17.46763 15.68862 16.722275 15.34375 16.125 C 15.085097 15.677044 14.576967 15.440139 14.09375 15.5 z M 23.875 15.5 C 23.524709 15.56257 23.185555 15.789736 23 16.125 C 22.661378 16.724775 22.891467 17.46763 23.46875 17.8125 C 24.067275 18.15737 24.84138 17.972276 25.1875 17.375 C 25.53362 16.778974 25.287275 15.96987 24.6875 15.625 C 24.538494 15.53878 24.41078 15.519855 24.25 15.5 C 24.129416 15.4851 23.991764 15.47915 23.875 15.5 z M 10.4375 19.09375 C 10.078715 19.15606 9.75649 19.351533 9.5625 19.6875 C 9.21763 20.284775 9.4364726 21.092629 10.03125 21.4375 C 10.631025 21.78362 11.34263 21.568525 11.6875 20.96875 C 12.031121 20.371476 11.846026 19.59612 11.25 19.25 C 11.100369 19.16378 10.973632 19.113704 10.8125 19.09375 C 10.691653 19.07878 10.557096 19.07298 10.4375 19.09375 z M 27.5 19.09375 C 27.339376 19.1137 27.177757 19.163785 27.03125 19.25 C 26.432724 19.59737 26.278881 20.372724 26.625 20.96875 C 26.97112 21.564776 27.683974 21.78487 28.28125 21.4375 C 28.878525 21.093878 29.12862 20.284775 28.78125 19.6875 C 28.524471 19.239543 27.98187 19.03389 27.5 19.09375 z M 9.3125 24 C 8.620261 24 8.0625 24.530258 8.0625 25.21875 C 8.0625 25.90974 8.624009 26.5 9.3125 26.5 C 10.00224 26.5 10.592501 25.908489 10.59375 25.21875 C 10.59245 24.529009 10.00349 24.00125 9.3125 24 z M 29 24 C 28.307761 24.0025 27.78125 24.531508 27.78125 25.21875 C 27.78245 25.908489 28.309011 26.5 29 26.5 C 29.689741 26.5013 30.25 25.90974 30.25 25.21875 C 30.25 24.530258 29.689741 24 29 24 z M 10.5 28.90625 C 10.338753 28.92816 10.180881 29.035346 10.03125 29.125 C 9.4339746 29.463623 9.216381 30.232718 9.5625 30.8125 C 9.904871 31.411023 10.655224 31.624871 11.25 31.28125 C 11.848525 30.942627 12.09487 30.129775 11.75 29.53125 C 11.491347 29.084231 10.983745 28.840532 10.5 28.90625 z M 27.46875 28.96875 C 27.12035 29.02404 26.817584 29.207231 26.625 29.53125 C 26.282629 30.126026 26.432724 30.93638 27.03125 31.28125 C 27.627275 31.62362 28.437629 31.408525 28.78125 30.8125 C 29.119873 30.213975 28.86603 29.452377 28.28125 29.125 C 28.131306 29.04035 27.974003 28.988157 27.8125 28.96875 C 27.691374 28.9542 27.584883 28.95032 27.46875 28.96875 z M 14.15625 32.5 C 14.07726 32.5027 13.983408 32.54176 13.90625 32.5625 C 13.597616 32.64544 13.329935 32.826362 13.15625 33.125 C 12.812629 33.723524 13.028974 34.500129 13.625 34.84375 C 14.222275 35.182373 15.001379 34.961029 15.34375 34.375 C 15.68987 33.776475 15.441026 33.06487 14.84375 32.71875 C 14.619773 32.589892 14.393231 32.491996 14.15625 32.5 z M 23.90625 32.5625 C 23.745393 32.58228 23.618069 32.632845 23.46875 32.71875 C 22.870226 33.06487 22.650132 33.777724 23 34.375 C 23.342372 34.971025 24.090225 35.191119 24.6875 34.84375 C 25.284775 34.500129 25.53362 33.711028 25.1875 33.125 C 24.927911 32.677043 24.388823 32.503166 23.90625 32.5625 z M 19.15625 33.84375 C 18.466509 33.84375 17.9375 34.40276 17.9375 35.09375 C 17.9375 35.78474 18.466509 36.34375 19.15625 36.34375 C 19.84599 36.34375 20.40625 35.78474 20.40625 35.09375 C 20.40625 34.40276 19.84599 33.84375 19.15625 33.84375 z M 33.0625 37.9375 L 33.0625 49.1875 L 35.875 49.1875 L 35.875 37.9375 L 33.0625 37.9375 z M 38.65625 37.9375 L 38.65625 49.1875 L 41.5 49.1875 L 41.5 37.9375 L 38.65625 37.9375 z";
	private static final String timeTillFinishedPath = "M 19 8.03125 C 10.337463 8.1661674 3.0426729 14.860322 2.28125 23.6875 C 1.4742306 33.101866 8.4319667 41.406565 17.84375 42.21875 C 27.260697 43.025769 35.531565 36.006824 36.34375 26.59375 C 36.439295 25.474253 36.426133 24.418053 36.3125 23.34375 L 32.0625 21.78125 C 32.438248 23.208061 32.630415 24.695358 32.5 26.25 C 31.864715 33.531251 25.408834 38.989626 18.125 38.375 C 10.843748 37.73584 5.4791239 31.282543 6.09375 24 C 6.7316182 16.716166 13.092457 11.352833 20.375 11.96875 C 23.51011 12.243782 26.305418 13.60035 28.40625 15.625 L 26.3125 17.40625 L 35.375 20.65625 L 33.6875 11.21875 L 31.375 13.125 C 28.620804 10.347562 24.92429 8.4552948 20.71875 8.09375 C 20.130998 8.0432305 19.577503 8.0222558 19 8.03125 z M 19.28125 13.6875 C 18.56849 13.6875 17.96875 14.255991 17.96875 14.96875 L 17.375 24.46875 L 13.53125 27.03125 C 12.914042 27.388922 12.673579 28.136666 13.03125 28.75 C 13.386339 29.3685 14.166624 29.576422 14.78125 29.21875 L 19.0625 27.03125 C 19.13222 27.041554 19.20765 27.09375 19.28125 27.09375 C 20.35168 27.095057 21.21875 26.227972 21.21875 25.15625 L 20.5625 14.96875 C 20.5625 14.255991 19.99401 13.6875 19.28125 13.6875 z M 14.03125 15.0625 C 13.864802 15.083119 13.716803 15.129655 13.5625 15.21875 C 12.945292 15.57513 12.70612 16.382792 13.0625 17 C 13.421462 17.6185 14.226542 17.796462 14.84375 17.4375 C 15.462249 17.08112 15.66888 16.304709 15.3125 15.6875 C 15.045215 15.224594 14.530594 15.000642 14.03125 15.0625 z M 24.15625 15.0625 C 23.800499 15.132377 23.504247 15.341047 23.3125 15.6875 C 22.962577 16.307291 23.122202 17.08112 23.71875 17.4375 C 24.33725 17.79388 25.142328 17.617208 25.5 17 C 25.857672 16.384083 25.651041 15.57513 25.03125 15.21875 C 24.87727 15.129655 24.697395 15.083019 24.53125 15.0625 C 24.406641 15.047111 24.274834 15.039209 24.15625 15.0625 z M 10.25 18.75 C 9.8781399 18.819608 9.5129635 19.090321 9.3125 19.4375 C 8.9561201 20.054708 9.1978739 20.83112 9.8125 21.1875 C 10.432291 21.545172 11.20612 21.338541 11.5625 20.71875 C 11.917588 20.101541 11.740917 19.295172 11.125 18.9375 C 10.970375 18.848405 10.791508 18.770619 10.625 18.75 C 10.500119 18.734538 10.373953 18.726802 10.25 18.75 z M 27.90625 18.75 C 27.740267 18.770617 27.588897 18.848404 27.4375 18.9375 C 26.819 19.296462 26.611078 20.102833 26.96875 20.71875 C 27.326422 21.334667 28.101542 21.546462 28.71875 21.1875 C 29.335958 20.832411 29.546462 20.054708 29.1875 19.4375 C 28.922151 18.974593 28.404202 18.688142 27.90625 18.75 z M 9.09375 23.84375 C 8.3784079 23.84375 7.8125 24.444782 7.8125 25.15625 C 7.8125 25.870301 8.3822817 26.4375 9.09375 26.4375 C 9.8065095 26.4375 10.404959 25.86901 10.40625 25.15625 C 10.404956 24.44349 9.8078009 23.845041 9.09375 23.84375 z M 29.4375 23.84375 C 28.722158 23.846363 28.15625 24.446073 28.15625 25.15625 C 28.157557 25.86901 28.723449 26.4375 29.4375 26.4375 C 30.15026 26.438807 30.75 25.870301 30.75 25.15625 C 30.75 24.444782 30.15026 23.84375 29.4375 23.84375 z M 10.3125 28.9375 C 10.145871 28.960133 9.9671249 29.094854 9.8125 29.1875 C 9.1952917 29.537424 8.954829 30.307119 9.3125 30.90625 C 9.6662972 31.524749 10.510374 31.730089 11.125 31.375 C 11.7435 31.025076 11.95013 30.212249 11.59375 29.59375 C 11.326465 29.131813 10.812388 28.86959 10.3125 28.9375 z M 27.875 29 C 27.514974 29.057129 27.16776 29.258918 26.96875 29.59375 C 26.614952 30.208376 26.819 31.01862 27.4375 31.375 C 28.053417 31.728798 28.832411 31.522167 29.1875 30.90625 C 29.537423 30.28775 29.323047 29.525803 28.71875 29.1875 C 28.563802 29.100019 28.385642 29.020054 28.21875 29 C 28.093581 28.984964 27.995009 28.98096 27.875 29 z M 14.125 32.65625 C 14.043371 32.65905 13.923483 32.66608 13.84375 32.6875 C 13.524816 32.773208 13.241982 33.003896 13.0625 33.3125 C 12.707412 33.931 12.946583 34.707411 13.5625 35.0625 C 14.179708 35.412424 14.958702 35.199338 15.3125 34.59375 C 15.670172 33.97525 15.460958 33.201422 14.84375 32.84375 C 14.612297 32.710592 14.36989 32.647981 14.125 32.65625 z M 24.1875 32.6875 C 24.021274 32.70794 23.873053 32.754971 23.71875 32.84375 C 23.100251 33.201422 22.857205 33.976542 23.21875 34.59375 C 23.572548 35.209667 24.414041 35.421462 25.03125 35.0625 C 25.648458 34.707411 25.857672 33.918088 25.5 33.3125 C 25.231748 32.849593 24.686178 32.626187 24.1875 32.6875 z M 19.28125 34.03125 C 18.56849 34.03125 17.96875 34.59845 17.96875 35.3125 C 17.96875 36.026551 18.56849 36.625 19.28125 36.625 C 19.99401 36.625 20.5625 36.026551 20.5625 35.3125 C 20.5625 34.59845 19.99401 34.03125 19.28125 34.03125 z M 45.34375 34.59375 L 32.71875 47.34375 L 28 42.625 L 23.71875 46.9375 L 32.53125 55.875 L 49.5625 38.84375 L 45.34375 34.59375 z";

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
	
	private Translator translator;
	
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
		this.translator = Translator.getInstance();
		totalAmount = -1;
		finishedAmount = 0;
		build();
	}
	
	public void setPresenter(AutomatePresenter presenter) {
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
		double circleBackR = PROGRESS_RADIUS*2 + 14;
		pane.setPrefSize(200, circleBackR);
		pane.setMaxSize(200, circleBackR);
		pane.setMinSize(200, circleBackR);
		pane.setPadding(new Insets(0, (200-circleBackR)/2, 0, (200-circleBackR)/2));
		circleBack.setPrefSize(circleBackR, circleBackR);
		circleBack.getStyleClass().add("circle-back");
		circleBack.toBack();
		piePiecePath = new Path();
		piePiecePath.getStyleClass().add("automate-progress");
		piePiecePath.setStrokeType(StrokeType.INSIDE);
		Pane pane2 = new Pane();
		pane2.getChildren().add(piePiecePath);
		pane2.setPrefSize(PROGRESS_RADIUS*2, PROGRESS_RADIUS*2);
		pane2.setMaxSize(PROGRESS_RADIUS*2, PROGRESS_RADIUS*2);
		pane2.setMinSize(PROGRESS_RADIUS*2, PROGRESS_RADIUS*2);
		StackPane.setMargin(pane2, new Insets(8, 7, 8, 7));
		pane.getChildren().add(circleBack);
		pane.getChildren().add(pane2);
		pane.setAlignment(Pos.CENTER);
		pane2.setTranslateX(PROGRESS_RADIUS);
		pane2.setTranslateY(PROGRESS_RADIUS);
		lblAmountFinished = new Label();
		lblTotalAmount = new Label();
		lblTotalAmount.setPrefWidth(PROGRESS_RADIUS*2);
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
		Text txtStart = new Text(translator.getTranslation("play"));
		txtStart.getStyleClass().add("automate-btn-text");
		btnStart.setGraphic(txtStart);
		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.clickedStart();
			}
		});
		btnReset = new Button();
		btnReset.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnReset.getStyleClass().add("automate-btn");
		Text txtRestart = new Text(translator.getTranslation("reset-flow"));
		txtRestart.getStyleClass().add("automate-btn-text");
		btnReset.setGraphic(txtRestart);
		btnReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.clickedReset();
			}
		});
		HBox.setMargin(btnReset, new Insets(0, 15, 0, 0));
		btnPause = new Button();
		btnPause.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnPause.getStyleClass().add("automate-btn");
		Text txtPause = new Text(translator.getTranslation("pause"));
		txtPause.getStyleClass().add("automate-btn-text");
		btnPause.setGraphic(txtPause);
		btnPause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.clickedPause();
			}
		});
		btnStop = new Button();
		btnStop.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnStop.getStyleClass().add("automate-btn");
		Text txtStop = new Text(translator.getTranslation("stop"));
		txtStop.getStyleClass().add("automate-btn-text");
		btnStop.setGraphic(txtStop);
		HBox.setMargin(btnStop, new Insets(0, 15, 0, 0));
		btnStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
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
		cycleTimeShape.setContent(cycleTimePath);
		cycleTimeShape.getStyleClass().add("automate-icon");
		Pane iconPane = new Pane();
		iconPane.setPrefSize(33.375, ICON_HEIGHT);
		iconPane.setMaxSize(33.375, ICON_HEIGHT);
		iconPane.getChildren().add(cycleTimeShape);
		lblCycleTimeMessage = new Label(translator.getTranslation("cycletime"));
		lblCycleTimeMessage.getStyleClass().add("automate-info-lbl");
		lblCycleTime = new Label();
		lblCycleTime.getStyleClass().add("automate-time-lbl");
		setCycleTime(null);
		vboxCycleTime.getChildren().addAll(iconPane, lblCycleTimeMessage, lblCycleTime);
		vboxCycleTime.setAlignment(Pos.BOTTOM_CENTER);
		vboxCycleTime.setPrefWidth(200);
		vboxCycleTime.setPrefHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_TOP);
		bottomBottom.getChildren().add(vboxCycleTime);
		
		VBox vboxCycleTimePassed = new VBox();
		vboxCycleTimePassed.getStyleClass().add("time-vbox");
		cycleTimePassedShape = new SVGPath();
		cycleTimePassedShape.setContent(cylceTimePassedPath);
		cycleTimePassedShape.getStyleClass().add("automate-icon");
		Pane iconPane2 = new Pane();
		iconPane2.setPrefSize(36.281, ICON_HEIGHT);
		iconPane2.setMaxSize(36.281, ICON_HEIGHT);
		iconPane2.getChildren().add(cycleTimePassedShape);
		lblCycleTimePassedMessage = new Label(translator.getTranslation("cycletimepassed"));
		lblCycleTimePassedMessage.getStyleClass().add("automate-info-lbl");
		lblCycleTimePassed = new Label();
		lblCycleTimePassed.getStyleClass().addAll("automate-time-lbl", "blue-time");
		setCycleTimePassed(null);
		vboxCycleTimePassed.getChildren().addAll(iconPane2, lblCycleTimePassedMessage, lblCycleTimePassed);
		vboxCycleTimePassed.setAlignment(Pos.BOTTOM_CENTER);
		vboxCycleTimePassed.setPrefWidth(200);
		vboxCycleTimePassed.setPrefHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_TOP);
		bottomBottom.getChildren().add(vboxCycleTimePassed);
		
		VBox vboxTimeTillPause = new VBox();
		vboxTimeTillPause.getStyleClass().add("time-vbox");
		timeTillPauseShape = new SVGPath();
		timeTillPauseShape.setContent(timeTillPausePath);
		timeTillPauseShape.getStyleClass().add("automate-icon");
		Pane iconPane3 = new Pane();
		iconPane3.setPrefSize(38.874, ICON_HEIGHT);
		iconPane3.setMaxSize(38.874, ICON_HEIGHT);
		iconPane3.getChildren().add(timeTillPauseShape);
		lblTimeTillPauseMessage = new Label(translator.getTranslation("timetillpause"));
		lblTimeTillPauseMessage.getStyleClass().add("automate-info-lbl");
		lblTimeTillPause = new Label();
		lblTimeTillPause.getStyleClass().add("automate-time-lbl");
		setTimeTillPause(null);
		vboxTimeTillPause.getChildren().addAll(iconPane3, lblTimeTillPauseMessage, lblTimeTillPause);
		vboxTimeTillPause.setAlignment(Pos.BOTTOM_CENTER);
		vboxTimeTillPause.setPrefWidth(200);
		vboxTimeTillPause.setPrefHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_TOP);
		bottomBottom.getChildren().add(vboxTimeTillPause);
		
		VBox vboxTimeTillFinished = new VBox();
		vboxTimeTillFinished.getStyleClass().add("time-vbox");
		timeTillFinishedShape = new SVGPath();
		timeTillFinishedShape.setContent(timeTillFinishedPath);
		timeTillFinishedShape.getStyleClass().add("automate-icon");
		Pane iconPane4 = new Pane();
		iconPane4.setPrefSize(47.345, ICON_HEIGHT);
		iconPane4.setMaxSize(47.345, ICON_HEIGHT);
		iconPane4.getChildren().add(timeTillFinishedShape);
		lblTimeTillFinishedMessage = new Label(translator.getTranslation("timetillfinished"));
		lblTimeTillFinishedMessage.getStyleClass().add("automate-info-lbl");
		lblTimeTillFinished = new Label();
		lblTimeTillFinished.getStyleClass().addAll("automate-time-lbl", "automate-time-hl-lbl");
		setTimeTillFinished(null);
		vboxTimeTillFinished.getChildren().addAll(iconPane4, lblTimeTillFinishedMessage, lblTimeTillFinished);
		vboxTimeTillFinished.setAlignment(Pos.BOTTOM_CENTER);
		vboxTimeTillFinished.setPrefWidth(200);
		vboxTimeTillFinished.setPrefHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_TOP);
		bottomBottom.getChildren().add(vboxTimeTillFinished);
		
		setStatus(translator.getTranslation("status-first"));
		piePiecePath.getStyleClass().add("automate-progress-green");
		setProcessStopped();
		
	}
	
	public void setCycleTime(String timeString) {
		if (timeString == null) {
			lblCycleTime.setText(translator.getTranslation("unknown"));
		} else {
			lblCycleTime.setText(timeString);
		}
	}
	
	public void setStatus(String message) {
		lblMessage.setText(message);
	}

	public void setAlarmStatus(String message) {
		lblAlarmMessage.setText(message);
		lblAlarmMessage.setVisible(true);
	}
	
	public void hideAlarmMessage() {
		lblAlarmMessage.setVisible(false);
	}
	
	private void setPercentage(int percentage) {
		
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
		double corner = ((percentaged)/ 100) * (Math.PI * 2);
		
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
	
	public void setCycleTimePassed(String timeString) {
		if (timeString == null) {
			lblCycleTimePassed.setText(translator.getTranslation("unknown"));
		} else {
			lblCycleTimePassed.setText(timeString);
		}
	}
	
	public void setTimeTillPause(String timeString) {
		if (timeString == null) {
			lblTimeTillPause.setText(translator.getTranslation("unknown"));
		} else {
			lblTimeTillPause.setText(timeString);
		}
	}
	
	public void setTimeTillFinished(String timeString) {
		if (timeString == null) {
			lblTimeTillFinished.setText(translator.getTranslation("unknown"));
		} else {
			lblTimeTillFinished.setText(timeString);
		}
	}
	
	public void setTotalAmount(int amount) {
		totalAmount = amount;
		lblTotalAmount.setText("/" + amount);
	}
	
	public void setFinishedAmount(int amount) {
		finishedAmount = amount;
		lblAmountFinished.setText("" + amount);
		if ((totalAmount >= 0) && (finishedAmount >= 0)) {
			setPercentage((int) Math.floor(((double) finishedAmount/ (double) totalAmount) * 100));
		}
	}
	
	public void addNodeToTop(Node node) {
		this.top.getChildren().add(node);
	}
	
	public void removeNodeFromTop(Node node) {
		this.top.getChildren().remove(node);
	}
	
	public void setTop(Node node) {
		this.top.getChildren().clear();
		this.top.getChildren().add(node);
	}
	
	public void addNodeToBottom(Node node) {
		this.bottom.getChildren().add(node);
	}
	
	public void removeNodeFromBottom(Node node) {
		this.bottom.getChildren().remove(node);
	}
	
	public void setBottom(Node bottom) {
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
	
	public void setZRest(double zrest) {
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
