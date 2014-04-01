package eu.robojob.millassist.ui.general.flow;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import eu.robojob.millassist.external.device.DeviceType;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine.WayOfOperating;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.ui.general.model.DeviceInformation;
import eu.robojob.millassist.util.Translator;

public class DeviceButton extends VBox implements CNCMachineListener {
	
	private String preStackingPath = "M 26.875 2.34375 L 26.875 8.03125 C 3.7635748 9.263933 2.78125 33.144966 2.78125 33.375 L 6.78125 33.375 C 6.78125 33.33199 8.9323413 19.443885 26.875 18.53125 L 26.875 24.625 L 43.28125 13.5 L 26.875 2.34375 z";
	private String postStackingPath = "M 2.78125 2.34375 C 2.78125 2.5737837 3.7635751 26.454816 26.875 27.6875 L 26.875 33.375 L 43.28125 22.25 L 26.875 11.125 L 26.875 17.1875 C 8.9323411 16.274865 6.78125 2.386761 6.78125 2.34375 L 2.78125 2.34375 z";
	private String cncMachinePath = "M 23.3125 3.90625 C 22.888437 4.8528674 22.52289 5.8478866 22.21875 6.84375 C 21.62757 6.8724768 21.022524 6.8914764 20.4375 7 C 19.848828 7.0788848 19.259529 7.1786847 18.6875 7.34375 C 18.120031 6.4919767 17.488036 5.6307739 16.8125 4.84375 C 15.438856 5.3122709 14.105238 5.8890613 12.875 6.65625 C 13.041205 7.7015874 13.247402 8.6854809 13.53125 9.6875 C 13.062729 10.049321 12.55499 10.373147 12.125 10.78125 C 11.68703 11.180006 11.229282 11.5796 10.84375 12.03125 C 9.9044283 11.619727 8.9135129 11.217229 7.90625 10.90625 C 7.0116145 12.042556 6.2191285 13.251097 5.59375 14.5625 C 6.2955054 15.352944 7.0154292 16.096138 7.78125 16.78125 C 7.5810742 17.339371 7.3439016 17.896493 7.1875 18.46875 C 7.0509336 19.045794 6.8514711 19.59746 6.78125 20.1875 C 5.7696553 20.351197 4.7415163 20.590012 3.71875 20.875 C 3.5616644 22.313393 3.5614364 23.749107 3.71875 25.1875 C 4.7417443 25.47226 5.7696553 25.711303 6.78125 25.875 C 6.8514711 26.464812 7.0509336 27.016706 7.1875 27.59375 C 7.3443576 28.166007 7.5810742 28.722901 7.78125 29.28125 C 7.0154292 29.96659 6.2955054 30.709556 5.59375 31.5 C 6.2191285 32.811175 7.0116145 34.019944 7.90625 35.15625 C 8.9135129 34.845271 9.9042003 34.474251 10.84375 34.0625 C 11.228826 34.513922 11.68703 34.882722 12.125 35.28125 C 12.55499 35.689581 13.062957 36.013179 13.53125 36.375 C 13.247402 37.377247 13.041433 38.360913 12.875 39.40625 C 14.105466 40.173439 15.438628 40.781479 16.8125 41.25 C 17.488036 40.462748 18.120031 39.570523 18.6875 38.71875 C 19.259529 38.883815 19.849056 39.015321 20.4375 39.09375 C 21.022752 39.202502 21.62757 39.221501 22.21875 39.25 C 22.523118 40.246091 22.888209 41.209861 23.3125 42.15625 C 24.751805 42.107688 26.193659 41.921114 27.59375 41.53125 C 27.731456 40.481809 27.803608 39.488765 27.8125 38.4375 C 28.365833 38.223873 28.942319 38.057575 29.46875 37.78125 C 30.004984 37.5259 30.564796 37.261475 31.0625 36.9375 C 31.848612 37.595481 32.685648 38.236595 33.5625 38.8125 C 34.741212 37.973494 35.879349 37.048286 36.84375 35.96875 C 36.39096 35.013013 35.892752 34.128676 35.34375 33.25 C 35.691892 32.771448 36.072456 32.322743 36.375 31.8125 C 36.679824 31.304081 37.009242 30.794214 37.25 30.25 C 38.26889 30.380411 39.311879 30.451651 40.375 30.46875 C 40.913514 29.120869 41.361427 27.745422 41.59375 26.3125 C 40.696379 25.753467 39.79517 25.251382 38.875 24.8125 C 38.930858 24.223372 38.940479 23.621746 38.96875 23.03125 C 38.940479 22.440754 38.93063 21.8389 38.875 21.25 C 39.79517 20.81089 40.696379 20.309033 41.59375 19.75 C 41.361427 18.317078 40.913742 16.941631 40.375 15.59375 C 39.311879 15.610621 38.26889 15.681633 37.25 15.8125 C 37.009242 15.268286 36.679824 14.758419 36.375 14.25 C 36.072228 13.739757 35.692348 13.291052 35.34375 12.8125 C 35.892752 11.933824 36.39096 11.049487 36.84375 10.09375 C 35.879121 9.014214 34.741212 8.0887778 33.5625 7.25 C 32.68542 7.8261325 31.848384 8.4667908 31.0625 9.125 C 30.564568 8.8007972 30.004528 8.5365996 29.46875 8.28125 C 28.942091 8.0049253 28.366061 7.8383993 27.8125 7.625 C 27.803608 6.5737349 27.731456 5.5806912 27.59375 4.53125 C 26.193431 4.1409299 24.752033 3.954812 23.3125 3.90625 z M 22.75 13.6875 C 24.086353 13.686218 25.421893 13.984927 26.625 14.53125 C 28.241454 15.26196 29.631629 16.505521 30.59375 18 C 31.56841 19.496075 32.068443 21.241979 32.09375 23.03125 C 32.068443 24.820293 31.568182 26.565969 30.59375 28.0625 C 29.631401 29.557435 28.241226 30.80054 26.625 31.53125 C 25.020857 32.259908 23.169758 32.529517 21.40625 32.25 C 19.646162 32.015398 17.971057 31.25901 16.625 30.09375 C 15.273015 28.929401 14.283742 27.353869 13.78125 25.65625 C 13.281038 23.957035 13.281266 22.105465 13.78125 20.40625 C 14.283742 18.709087 15.273243 17.133554 16.625 15.96875 C 17.971057 14.803946 19.646162 14.047102 21.40625 13.8125 C 21.847184 13.742564 22.304549 13.687927 22.75 13.6875 z";
	private String prePocessingPath = "M 34.125 1.78125 C 33.925434 2.2267185 33.736874 2.6876101 33.59375 3.15625 C 33.315542 3.169768 33.056553 3.16769 32.78125 3.21875 C 32.504223 3.255875 32.237946 3.297321 31.96875 3.375 C 31.701708 2.974166 31.380398 2.5891144 31.0625 2.21875 C 30.416077 2.4392302 29.766434 2.7014722 29.1875 3.0625 C 29.26572 3.5544219 29.366422 3.9972094 29.5 4.46875 C 29.279518 4.6390183 29.077344 4.8392026 28.875 5.03125 C 28.668893 5.2188982 28.431428 5.3812105 28.25 5.59375 C 27.807968 5.4000898 27.349008 5.2088436 26.875 5.0625 C 26.453996 5.5972314 26.075546 6.1641227 25.78125 6.78125 C 26.111489 7.1532264 26.452111 7.5213421 26.8125 7.84375 C 26.71829 8.1063943 26.60485 8.3557038 26.53125 8.625 C 26.46698 8.8965524 26.376794 9.1598365 26.34375 9.4375 C 25.867705 9.51454 25.387549 9.6158874 24.90625 9.75 C 24.83232 10.426892 24.83222 11.135608 24.90625 11.8125 C 25.387657 11.946504 25.867705 12.016717 26.34375 12.09375 C 26.37679 12.37131 26.466982 12.665952 26.53125 12.9375 C 26.60507 13.206796 26.718299 13.455998 26.8125 13.71875 C 26.452115 14.041266 26.111486 14.378027 25.78125 14.75 C 26.075546 15.367023 26.453996 15.934017 26.875 16.46875 C 27.349008 16.322405 27.80786 16.162514 28.25 15.96875 C 28.431212 16.181182 28.668893 16.374956 28.875 16.5625 C 29.077344 16.754656 29.279626 16.892228 29.5 17.0625 C 29.366422 17.534144 29.265824 18.00808 29.1875 18.5 C 29.766542 18.861032 30.415969 19.123271 31.0625 19.34375 C 31.380398 18.973278 31.701708 18.557082 31.96875 18.15625 C 32.23794 18.23393 32.504331 18.275592 32.78125 18.3125 C 33.056661 18.36367 33.315542 18.39284 33.59375 18.40625 C 33.736976 18.874998 33.925326 19.335889 34.125 19.78125 C 34.802315 19.75841 35.497384 19.652214 36.15625 19.46875 C 36.22105 18.974895 36.21456 18.494716 36.21875 18 C 36.479138 17.899472 36.783516 17.848786 37.03125 17.71875 C 37.283598 17.598586 37.547034 17.46496 37.78125 17.3125 C 38.151186 17.62214 38.556111 17.947734 38.96875 18.21875 C 39.523438 17.823922 40.014909 17.383016 40.46875 16.875 C 40.255671 16.425239 40.039604 15.975991 39.78125 15.5625 C 39.945079 15.337295 40.138876 15.146362 40.28125 14.90625 C 40.424692 14.666994 40.574201 14.412354 40.6875 14.15625 C 41.166977 14.21762 41.655958 14.273202 42.15625 14.28125 C 42.409666 13.646954 42.609424 12.986816 42.71875 12.3125 C 42.296463 12.049424 41.870526 11.800285 41.4375 11.59375 C 41.46378 11.316514 41.48668 11.059129 41.5 10.78125 C 41.48668 10.50337 41.46366 10.214632 41.4375 9.9375 C 41.870526 9.73086 42.296463 9.5130718 42.71875 9.25 C 42.609424 8.5756841 42.409774 7.9155466 42.15625 7.28125 C 41.655958 7.28917 41.166977 7.313416 40.6875 7.375 C 40.574201 7.1189005 40.424697 6.895506 40.28125 6.65625 C 40.138773 6.4161345 39.945295 6.1939498 39.78125 5.96875 C 40.039604 5.555254 40.255671 5.1372606 40.46875 4.6875 C 40.014808 4.1794833 39.492188 3.7384657 38.9375 3.34375 C 38.524754 3.6148695 38.119828 3.9090051 37.75 4.21875 C 37.515676 4.0661811 37.283382 3.9639144 37.03125 3.84375 C 36.783408 3.7137144 36.479252 3.6316747 36.21875 3.53125 C 36.21455 3.0365384 36.22105 2.5563516 36.15625 2.0625 C 35.497276 1.8788199 34.802423 1.8041019 34.125 1.78125 z M 33.84375 6.375 C 34.47262 6.3744026 35.090078 6.5241757 35.65625 6.78125 C 36.416935 7.125114 37.10974 7.7029657 37.5625 8.40625 C 38.021164 9.1102857 38.23809 9.9392368 38.25 10.78125 C 38.23806 11.623149 38.021056 12.452002 37.5625 13.15625 C 37.109632 13.859749 36.416827 14.437386 35.65625 14.78125 C 34.901353 15.124146 34.04864 15.256535 33.21875 15.125 C 32.390473 15.014595 31.602188 14.642109 30.96875 14.09375 C 30.332522 13.545822 29.892721 12.830129 29.65625 12.03125 C 29.420858 11.231621 29.420966 10.330882 29.65625 9.53125 C 29.892721 8.7325864 30.33263 8.0168896 30.96875 7.46875 C 31.602188 6.9206058 32.390473 6.5479003 33.21875 6.4375 C 33.426247 6.404589 33.634126 6.375199 33.84375 6.375 z M 16.28125 9.15625 C 15.981901 9.8244523 15.745936 10.547041 15.53125 11.25 C 15.113938 11.27028 14.694206 11.26716 14.28125 11.34375 C 13.865711 11.39943 13.466294 11.44598 13.0625 11.5625 C 12.661937 10.961247 12.195597 10.368046 11.71875 9.8125 C 10.749116 10.143219 9.805901 10.552208 8.9375 11.09375 C 9.054824 11.831635 9.205883 12.54269 9.40625 13.25 C 9.075527 13.505402 8.709766 13.743178 8.40625 14.03125 C 8.09709 14.312721 7.803393 14.58744 7.53125 14.90625 C 6.868203 14.61576 6.148511 14.313266 5.4375 14.09375 C 4.805993 14.895847 4.222694 15.761808 3.78125 16.6875 C 4.27661 17.245464 4.771917 17.766389 5.3125 18.25 C 5.171193 18.643967 5.0479 19.033556 4.9375 19.4375 C 4.8411 19.844828 4.705816 20.239754 4.65625 20.65625 C 3.942184 20.771805 3.190699 20.923831 2.46875 21.125 C 2.357863 22.140338 2.357701 23.172162 2.46875 24.1875 C 3.190861 24.388507 3.942184 24.571952 4.65625 24.6875 C 4.7058 25.10384 4.841097 25.467678 4.9375 25.875 C 5.048224 26.278944 5.1712 26.668371 5.3125 27.0625 C 4.771924 27.546273 4.276603 28.098291 3.78125 28.65625 C 4.222694 29.581786 4.805993 30.385402 5.4375 31.1875 C 6.148511 30.967984 6.868041 30.728147 7.53125 30.4375 C 7.803069 30.756149 8.09709 31.031185 8.40625 31.3125 C 8.709766 31.600735 9.075689 31.807092 9.40625 32.0625 C 9.205883 32.769966 9.054986 33.512121 8.9375 34.25 C 9.806063 34.791549 10.748954 35.169281 11.71875 35.5 C 12.195597 34.944292 12.661937 34.319998 13.0625 33.71875 C 13.466285 33.835265 13.865873 33.944638 14.28125 34 C 14.694368 34.07676 15.113938 34.07363 15.53125 34.09375 C 15.746089 34.796871 15.981739 35.48821 16.28125 36.15625 C 17.297224 36.12199 18.355451 35.993946 19.34375 35.71875 C 19.44095 34.977966 19.493718 34.273324 19.5 33.53125 C 19.890582 33.380458 20.284649 33.288803 20.65625 33.09375 C 21.034772 32.913503 21.429926 32.72869 21.78125 32.5 C 22.336154 32.964459 22.943543 33.405976 23.5625 33.8125 C 24.394532 33.220258 25.16299 32.543275 25.84375 31.78125 C 25.524133 31.10661 25.168781 30.495238 24.78125 29.875 C 25.026995 29.537193 25.317689 29.235167 25.53125 28.875 C 25.746413 28.516116 25.955053 28.165406 26.125 27.78125 C 26.844217 27.87331 27.593312 27.894179 28.34375 27.90625 C 28.723874 26.954807 29.023511 26.011474 29.1875 25 C 28.554071 24.605386 27.930789 24.216053 27.28125 23.90625 C 27.32067 23.490396 27.32377 23.07307 27.34375 22.65625 C 27.32377 22.23943 27.32049 21.821948 27.28125 21.40625 C 27.930789 21.09629 28.554071 20.738358 29.1875 20.34375 C 29.023511 19.332276 28.724036 18.357694 28.34375 17.40625 C 27.593312 17.41813 26.844217 17.470125 26.125 17.5625 C 25.955053 17.17835 25.746422 16.827634 25.53125 16.46875 C 25.317536 16.108576 25.027319 15.7753 24.78125 15.4375 C 25.168781 14.817256 25.524133 14.20589 25.84375 13.53125 C 25.162837 12.769225 24.394532 12.092074 23.5625 11.5 C 22.943381 11.906679 22.335992 12.379134 21.78125 12.84375 C 21.429764 12.614898 21.034448 12.430246 20.65625 12.25 C 20.284487 12.054946 19.890753 11.931886 19.5 11.78125 C 19.4937 11.039182 19.44096 10.334528 19.34375 9.59375 C 18.355289 9.3182303 17.297386 9.1905274 16.28125 9.15625 z M 15.90625 16.0625 C 16.849556 16.061601 17.775742 16.270632 18.625 16.65625 C 19.766029 17.172046 20.75836 18.038823 21.4375 19.09375 C 22.125496 20.149804 22.482135 21.393231 22.5 22.65625 C 22.48209 23.9191 22.125334 25.162379 21.4375 26.21875 C 20.758198 27.274 19.765867 28.140454 18.625 28.65625 C 17.492656 29.170595 16.213585 29.384803 14.96875 29.1875 C 13.726336 29.021894 12.543907 28.44754 11.59375 27.625 C 10.639407 26.803108 9.917208 25.729569 9.5625 24.53125 C 9.209412 23.331807 9.209574 21.980698 9.5625 20.78125 C 9.917208 19.583255 10.639569 18.509709 11.59375 17.6875 C 12.543907 16.865284 13.726336 16.32185 14.96875 16.15625 C 15.279997 16.10689 15.591814 16.0628 15.90625 16.0625 z";
	private String postProcessingPath = "M 11 1.78125 C 10.322576 1.804102 9.627724 1.87882 8.96875 2.0625 C 8.90396 2.556352 8.91045 3.036539 8.90625 3.53125 C 8.645748 3.631675 8.341592 3.713715 8.09375 3.84375 C 7.841618 3.963915 7.609324 4.066181 7.375 4.21875 C 7.005172 3.909005 6.600245 3.61487 6.1875 3.34375 C 5.632812 3.738466 5.110192 4.179483 4.65625 4.6875 C 4.869329 5.137261 5.085396 5.555254 5.34375 5.96875 C 5.179704 6.19395 4.986226 6.416135 4.84375 6.65625 C 4.700303 6.895506 4.550798 7.118901 4.4375 7.375 C 3.958022 7.31341 3.469042 7.28915 2.96875 7.28125 C 2.715225 7.915547 2.515576 8.575684 2.40625 9.25 C 2.828536 9.513072 3.254474 9.73086 3.6875 9.9375 C 3.66134 10.214632 3.63832 10.503371 3.625 10.78125 C 3.63832 11.05913 3.66122 11.316514 3.6875 11.59375 C 3.254474 11.800286 2.828536 12.049424 2.40625 12.3125 C 2.515576 12.986816 2.715333 13.646954 2.96875 14.28125 C 3.469042 14.273202 3.958022 14.21762 4.4375 14.15625 C 4.550798 14.412354 4.700308 14.666994 4.84375 14.90625 C 4.986124 15.146362 5.17992 15.337296 5.34375 15.5625 C 5.085396 15.975992 4.869329 16.425239 4.65625 16.875 C 5.11009 17.383016 5.601562 17.823922 6.15625 18.21875 C 6.568888 17.947734 6.973814 17.62214 7.34375 17.3125 C 7.577966 17.46496 7.841402 17.598586 8.09375 17.71875 C 8.341484 17.848786 8.645862 17.899472 8.90625 18 C 8.91043 18.494716 8.90395 18.974895 8.96875 19.46875 C 9.627616 19.652214 10.322684 19.75841 11 19.78125 C 11.199674 19.33589 11.388024 18.874998 11.53125 18.40625 C 11.809458 18.39284 12.068338 18.36367 12.34375 18.3125 C 12.620668 18.275592 12.88706 18.23393 13.15625 18.15625 C 13.423292 18.557082 13.744602 18.973279 14.0625 19.34375 C 14.70903 19.123271 15.358458 18.861032 15.9375 18.5 C 15.859176 18.00808 15.758578 17.534144 15.625 17.0625 C 15.845374 16.892228 16.047656 16.754656 16.25 16.5625 C 16.456106 16.374956 16.693788 16.181182 16.875 15.96875 C 17.317139 16.162515 17.775992 16.322406 18.25 16.46875 C 18.671004 15.934018 19.049454 15.367024 19.34375 14.75 C 19.013514 14.378026 18.672884 14.041266 18.3125 13.71875 C 18.4067 13.455998 18.51993 13.175546 18.59375 12.90625 C 18.658018 12.634702 18.74821 12.37131 18.78125 12.09375 C 19.257294 12.016717 19.737343 11.915255 20.21875 11.78125 C 20.29278 11.104358 20.29268 10.426891 20.21875 9.75 C 19.737451 9.615888 19.257294 9.51454 18.78125 9.4375 C 18.748206 9.159837 18.65802 8.896553 18.59375 8.625 C 18.52015 8.355704 18.40671 8.106394 18.3125 7.84375 C 18.672889 7.521342 19.01351 7.153226 19.34375 6.78125 C 19.049454 6.164123 18.671004 5.597232 18.25 5.0625 C 17.775992 5.208844 17.317031 5.40009 16.875 5.59375 C 16.693572 5.381211 16.456106 5.218898 16.25 5.03125 C 16.047656 4.839203 15.845482 4.639018 15.625 4.46875 C 15.758578 3.99721 15.85928 3.554422 15.9375 3.0625 C 15.358566 2.701472 14.708922 2.43923 14.0625 2.21875 C 13.744602 2.589115 13.423292 2.974166 13.15625 3.375 C 12.887054 3.29732 12.620776 3.25587 12.34375 3.21875 C 12.068446 3.1677 11.809458 3.16977 11.53125 3.15625 C 11.388126 2.68761 11.199566 2.226719 11 1.78125 z M 11.28125 6.375 C 11.490874 6.3752 11.698752 6.40459 11.90625 6.4375 C 12.734526 6.5479 13.522812 6.920606 14.15625 7.46875 C 14.79237 8.01689 15.232279 8.732587 15.46875 9.53125 C 15.704033 10.330881 15.704141 11.231622 15.46875 12.03125 C 15.232279 12.83013 14.792478 13.545822 14.15625 14.09375 C 13.522812 14.64211 12.734526 15.014595 11.90625 15.125 C 11.07636 15.256535 10.223647 15.124146 9.46875 14.78125 C 8.708173 14.437385 8.015367 13.85975 7.5625 13.15625 C 7.103944 12.452002 6.88694 11.62315 6.875 10.78125 C 6.88691 9.9392375 7.103836 9.110286 7.5625 8.40625 C 8.015259 7.702966 8.708065 7.125114 9.46875 6.78125 C 10.034922 6.524176 10.652379 6.374403 11.28125 6.375 z M 28.84375 9.15625 C 27.82761 9.190528 26.800961 9.31823 25.8125 9.59375 C 25.7153 10.334528 25.6313 11.039182 25.625 11.78125 C 25.234247 11.931887 24.840511 12.054947 24.46875 12.25 C 24.090552 12.430247 23.695236 12.614898 23.34375 12.84375 C 22.789008 12.379134 22.212869 11.90668 21.59375 11.5 C 20.761718 12.092074 19.962163 12.769226 19.28125 13.53125 C 19.600867 14.20589 19.956219 14.817256 20.34375 15.4375 C 20.097681 15.775301 19.807464 16.108576 19.59375 16.46875 C 19.378578 16.827634 19.169947 17.17835 19 17.5625 C 18.280783 17.470125 17.531688 17.41813 16.78125 17.40625 C 16.400964 18.357694 16.101489 19.332276 15.9375 20.34375 C 16.570929 20.738358 17.194211 21.09629 17.84375 21.40625 C 17.80451 21.821948 17.80123 22.23943 17.78125 22.65625 C 17.80123 23.07307 17.80433 23.490396 17.84375 23.90625 C 17.194211 24.216053 16.570929 24.605386 15.9375 25 C 16.101489 26.011474 16.401126 26.954807 16.78125 27.90625 C 17.531688 27.894179 18.280783 27.87331 19 27.78125 C 19.169947 28.165406 19.378587 28.516116 19.59375 28.875 C 19.807311 29.235167 20.098005 29.537193 20.34375 29.875 C 19.956219 30.495238 19.600867 31.10661 19.28125 31.78125 C 19.96201 32.543275 20.761718 33.220258 21.59375 33.8125 C 22.212707 33.405976 22.788846 32.964459 23.34375 32.5 C 23.695074 32.72869 24.090228 32.913503 24.46875 33.09375 C 24.840349 33.288803 25.234418 33.380458 25.625 33.53125 C 25.631282 34.273324 25.7153 34.977966 25.8125 35.71875 C 26.800799 35.993946 27.82778 36.12199 28.84375 36.15625 C 29.14326 35.48821 29.37891 34.796871 29.59375 34.09375 C 30.01106 34.07363 30.46188 34.07676 30.875 34 C 31.29038 33.944638 31.65871 33.835265 32.0625 33.71875 C 32.46306 34.319998 32.9294 34.944292 33.40625 35.5 C 34.37604 35.169281 35.35019 34.791549 36.21875 34.25 C 36.10126 33.512121 35.91911 32.769966 35.71875 32.0625 C 36.04931 31.807092 36.41523 31.600735 36.71875 31.3125 C 37.02791 31.031185 37.35318 30.756149 37.625 30.4375 C 38.28821 30.728147 38.97649 30.967984 39.6875 31.1875 C 40.31901 30.385402 40.9023 29.581786 41.34375 28.65625 C 40.84839 28.098291 40.35308 27.546273 39.8125 27.0625 C 39.9538 26.668371 40.07677 26.278944 40.1875 25.875 C 40.28391 25.467678 40.41915 25.10384 40.46875 24.6875 C 41.18282 24.571952 41.93414 24.388507 42.65625 24.1875 C 42.7673 23.172162 42.76713 22.140338 42.65625 21.125 C 41.9343 20.923831 41.18282 20.771805 40.46875 20.65625 C 40.41919 20.239754 40.2839 19.844828 40.1875 19.4375 C 40.0771 19.033556 39.95381 18.643967 39.8125 18.25 C 40.35309 17.766389 40.84839 17.245464 41.34375 16.6875 C 40.9023 15.761809 40.31901 14.895848 39.6875 14.09375 C 38.97649 14.313266 38.28805 14.61576 37.625 14.90625 C 37.35286 14.587439 37.02791 14.312722 36.71875 14.03125 C 36.41523 13.743178 36.04947 13.505402 35.71875 13.25 C 35.91911 12.54269 36.10143 11.831635 36.21875 11.09375 C 35.35035 10.552208 34.37588 10.14322 33.40625 9.8125 C 32.9294 10.368045 32.46306 10.961247 32.0625 11.5625 C 31.6587 11.44598 31.29054 11.39943 30.875 11.34375 C 30.46204 11.26716 30.01106 11.23903 29.59375 11.21875 C 29.37906 10.515791 29.1431 9.824452 28.84375 9.15625 z M 29.21875 16.0625 C 29.53318 16.0628 29.845 16.10688 30.15625 16.15625 C 31.39866 16.32185 32.58109 16.865284 33.53125 17.6875 C 34.48543 18.509709 35.20779 19.583255 35.5625 20.78125 C 35.91542 21.980698 35.91558 23.331807 35.5625 24.53125 C 35.20779 25.729569 34.48559 26.803108 33.53125 27.625 C 32.58109 28.44754 31.39866 29.021894 30.15625 29.1875 C 28.91141 29.384803 27.632345 29.170595 26.5 28.65625 C 25.359133 28.140454 24.366802 27.274 23.6875 26.21875 C 22.999666 25.162379 22.64291 23.9191 22.625 22.65625 C 22.642865 21.393231 22.999504 20.149804 23.6875 19.09375 C 24.36664 18.038823 25.358971 17.172046 26.5 16.65625 C 27.349255 16.270632 28.27544 16.061602 29.21875 16.0625 z";
	private String outputBinPath = "m 348.51805,310.4438 c -3.54911,0.10847 -6.22395,1.19331 -6.28125,1.21875 l 1,2.25 c 0.0245,-0.0109 8.47455,-2.26606 13.53125,7.71875 l -3.46875,1.53125 10.5,6.53125 2.1875,-12.15625 -3.21875,1.4375 c -3.68207,-7.22627 -9.68685,-8.67071 -14.25,-8.53125 z m 19.60771,14.91719 9.11104,5.22637 -13.71415,7.73906 -15.33027,-8.20528 5.63007,-3.06757 -3.53378,-2.28379 -6.25122,3.37881 0.37881,12.25587 19.86405,11.65546 16.8704,-10.58281 -0.28759,-13.30234 -12.26983,-7.1377 z";
	//private String conveyorFromPath = "M 1.375 8 L 1.375 13 L 12.0625 13 L 12.0625 8 L 1.375 8 z M 14.75 8 L 14.75 13 L 24.21875 13 L 24.21875 8 L 14.75 8 z M 26.90625 8 L 26.90625 13 C 29.06604 13.075 31.248 12.80872 33.375 13.25 C 33.46376 13.3109 33.5485 13.3561 33.625 13.375 C 34.16033 13.50729 34.42055 12.70283 34.8125 12.375 C 35.64619 11.39648 36.46486 10.40402 37.3125 9.4375 C 35.60963 8.50594 33.72771 8.0971 31.8125 8 C 31.17411 7.9676 30.51221 7.9713 29.875 8 L 26.90625 8 z M 39.46875 10.96875 C 38.43732 12.26665 37.35866 13.52642 36.3125 14.8125 C 37.97879 16.46915 38.489 18.89119 38.625 21.15625 L 43.65625 21.15625 C 43.46837 17.4334 42.23596 13.58337 39.46875 10.96875 z M 28 17.0625 C 24.56039 17.31329 22.16715 21.25897 23.4375 24.4375 C 24.1447 26.60695 26.33014 27.9515 28.53125 28 C 29.53175 28.02204 30.54021 27.77726 31.40625 27.21875 C 34.31296 25.60445 34.92359 21.2557 32.625 18.875 C 31.62469 17.73573 30.15524 17.1173 28.65625 17.0625 C 28.44212 17.0545 28.21403 17.0469 28 17.0625 z M 38.65625 23.84375 C 38.45709 26.06815 37.85409 28.45088 36.0625 29.9375 C 37.12494 31.20838 38.1826 32.48329 39.25 33.75 C 42.01158 31.22584 43.41594 27.52369 43.65625 23.84375 L 38.65625 23.84375 z M 33.75 31.40625 C 32.40267 31.94315 30.97114 32.11245 29.53125 32.03125 L 26.90625 32.03125 L 26.90625 37.03125 C 27.60302 37.02305 28.29656 37.0565 29 37.0625 C 31.11032 37.08037 33.23636 37.01882 35.21875 36.1875 C 35.70004 35.80939 37.70052 35.63528 36.65625 34.84375 C 35.68615 33.69909 34.72774 32.54435 33.75 31.40625 z M 9.375 32.03125 L 9.375 37.03125 L 14.0625 37.03125 L 14.0625 32.03125 L 9.375 32.03125 z M 16.75 32.03125 L 16.75 37.03125 L 24.21875 37.03125 L 24.21875 32.03125 L 16.75 32.03125 z";
	private String conveyorFromPath = "M 3.84375 7.96875 L 3.84375 12.96875 L 11.8125 12.96875 L 11.8125 7.96875 L 3.84375 7.96875 z M 14.5 7.96875 L 14.5 12.96875 L 21.71875 12.96875 L 21.71875 7.96875 L 14.5 7.96875 z M 24.40625 7.96875 L 24.40625 12.96875 C 26.56604 13.04375 28.748 12.77747 30.875 13.21875 C 30.9638 13.27965 31.0485 13.32485 31.125 13.34375 C 31.66033 13.47604 31.92055 12.67158 32.3125 12.34375 C 33.14619 11.36523 33.96486 10.37277 34.8125 9.40625 C 33.10963 8.47469 31.22771 8.06585 29.3125 7.96875 C 28.67411 7.93635 28.01221 7.94005 27.375 7.96875 L 24.40625 7.96875 z M 36.96875 10.9375 C 35.93732 12.2354 34.85866 13.49517 33.8125 14.78125 C 35.47879 16.4379 35.989 18.85994 36.125 21.125 L 41.15625 21.125 C 40.96837 17.40215 39.73596 13.55212 36.96875 10.9375 z M 25.5 17.03125 C 22.06039 17.28204 19.66715 21.22772 20.9375 24.40625 C 21.6447 26.5757 23.83014 27.92025 26.03125 27.96875 C 27.03175 27.99075 28.04021 27.74601 28.90625 27.1875 C 31.81296 25.5732 32.42359 21.22445 30.125 18.84375 C 29.12469 17.70448 27.65524 17.08605 26.15625 17.03125 C 25.94212 17.02325 25.71403 17.01565 25.5 17.03125 z M 36.15625 23.8125 C 35.95709 26.0369 35.35409 28.41963 33.5625 29.90625 C 34.62494 31.17713 35.6826 32.45204 36.75 33.71875 C 39.51158 31.19459 40.91594 27.49244 41.15625 23.8125 L 36.15625 23.8125 z M 31.25 31.375 C 29.90267 31.9119 28.47114 32.0812 27.03125 32 L 24.40625 32 L 24.40625 37 C 25.10302 36.992 25.79656 37.02525 26.5 37.03125 C 28.61032 37.04915 30.73636 36.98755 32.71875 36.15625 C 33.20004 35.77814 35.20052 35.60403 34.15625 34.8125 C 33.18615 33.66784 32.22774 32.5131 31.25 31.375 z M 8 32 L 8 37 L 11.5625 37 L 11.5625 32 L 8 32 z M 14.25 32 L 14.25 37 L 21.71875 37 L 21.71875 32 L 14.25 32 z";
	private String conveyorToPath = "m 348.21969,312.85683 0,5 -7.96875,0 0,-5 7.96875,0 z m -10.65625,0 0,5 -7.21875,0 0,-5 7.21875,0 z m -9.90625,0 0,5 c -2.15979,0.075 -4.34175,-0.19128 -6.46875,0.25 -0.0888,0.0609 -0.1735,0.1061 -0.25,0.125 -0.53533,0.13229 -0.79555,-0.67217 -1.1875,-1 -0.83369,-0.97852 -1.65236,-1.97098 -2.5,-2.9375 1.70287,-0.93156 3.58479,-1.3404 5.5,-1.4375 0.63839,-0.0324 1.30029,-0.0287 1.9375,0 l 2.96875,0 z m -12.5625,2.96875 c 1.03143,1.2979 2.11009,2.55767 3.15625,3.84375 -1.66629,1.65665 -2.1765,4.07869 -2.3125,6.34375 l -5.03125,0 c 0.18788,-3.72285 1.42029,-7.57288 4.1875,-10.1875 z m 11.46875,6.09375 c 3.43961,0.25079 5.83285,4.19647 4.5625,7.375 -0.7072,2.16945 -2.89264,3.514 -5.09375,3.5625 -1.0005,0.022 -2.00896,-0.22274 -2.875,-0.78125 -2.90671,-1.6143 -3.51734,-5.96305 -1.21875,-8.34375 1.00031,-1.13927 2.46976,-1.7577 3.96875,-1.8125 0.21413,-0.008 0.44222,-0.0156 0.65625,0 z m -10.65625,6.78125 c 0.19916,2.2244 0.80216,4.60713 2.59375,6.09375 -1.06244,1.27088 -2.1201,2.54579 -3.1875,3.8125 -2.76158,-2.52416 -4.16594,-6.22631 -4.40625,-9.90625 l 5,0 z m 4.90625,7.5625 c 1.34733,0.5369 2.77886,0.7062 4.21875,0.625 l 2.625,0 0,5 c -0.69677,-0.008 -1.39031,0.0252 -2.09375,0.0312 -2.11032,0.0179 -4.23636,-0.0437 -6.21875,-0.875 -0.48129,-0.37811 -2.48177,-0.55222 -1.4375,-1.34375 0.9701,-1.14466 1.92851,-2.2994 2.90625,-3.4375 z m 23.25,0.625 0,5 -3.5625,0 0,-5 3.5625,0 z m -6.25,0 0,5 -7.46875,0 0,-5 7.46875,0 z";
	
	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_HEIGHT = 60;
	private static final int LABEL_WIDTH = 120;
		
	private static final String UNKNOWN_DEVICE = "DeviceButton.unknownDevice";
	private static final String CSS_CLASS_UNKNOWN_DEVICE = "unknown-device";
	private static final String CSS_CLASS_DEVICE_UNFOCUSSED = "device-unfocussed";
	private static final String CSS_CLASS_PREPROCESS = "pre-process";
	private static final String CSS_CLASS_POSTPROCESS = "post-process";
	private static final String CSS_CLASS_CNCMACHINE = "cnc-machine";
	private static final String CSS_CLASS_BTN_PREPROCESS = "btn-pre";
	private static final String CSS_CLASS_BTN_POSTPROCESS = "btn-post";
	private static final String CSS_CLASS_BTN_CNCMACHINE = "btn-cnc";
	private static final String CSS_CLASS_BUTTON_SHAPE = "button-shape";
	private static final String CSS_CLASS_DEVICE_BUTTON = "device-button";
	private static final String CSS_CLASS_DEVICE_INFO_LABEL = "device-info-label";
	private static final String CSS_CLASS_DEVICE_LABEL = "device-label";
	private static final String CSS_CLASS_DEVICE_BUTTON_WRAPPER = "device-button-wrapper";
	private static final String CSS_CLASS_UNCLICKABLE = "unclickable";
	private static final String CSS_CLASS_UNCLICKABLE_NAME = "label-unclickable";
	private static final String CSS_CLASS_UNCLICKABLE_INFO = "label-info-unclickable";
	
	private Label lblExtraInfo;
	private Button mainButton;
	private SVGPath imagePath;
	private Label deviceName;
	
	private CNCMillingMachine machine;
	
	private RotateTransition rotateTransition;
	
	private DeviceInformation deviceInfo;
	
	public DeviceButton(final DeviceInformation deviceInfo) {
		build();
		setDeviceInformation(deviceInfo);
	}
	
	public void setDeviceInformation(final DeviceInformation deviceInfo) {
		if (machine != null) {
			machine.removeListener(this);
		}
		this.deviceInfo = deviceInfo;
		if (deviceInfo.getDevice() != null) {
			deviceName.setText(deviceInfo.getDevice().getName());
		} else {
			deviceName.setText(Translator.getTranslation(UNKNOWN_DEVICE));
			deviceName.getStyleClass().add(CSS_CLASS_UNKNOWN_DEVICE);
		}
		setImage();
		if ((deviceInfo != null) && (deviceInfo.getDevice() != null) && (deviceInfo.getDevice().getType() == DeviceType.CNC_MACHINE)) {
			rotateTransition = new RotateTransition(Duration.millis(5000), imagePath);
			rotateTransition.setFromAngle(0);
			rotateTransition.setToAngle(360);
			rotateTransition.setInterpolator(Interpolator.LINEAR);
			rotateTransition.setCycleCount(Timeline.INDEFINITE);
			machine = ((CNCMillingMachine) deviceInfo.getDevice());
			machine.addListener(this);
			updateMCodes();
		}
	}
	
	public DeviceInformation getDeviceInformation() {
		return deviceInfo;
	}
	
	private void build() {
		lblExtraInfo = new Label("");
		//lblExtraInfo.setPrefWidth(LABEL_WIDTH);
		lblExtraInfo.setPrefHeight(20);
		lblExtraInfo.setWrapText(true);
		lblExtraInfo.setAlignment(Pos.TOP_CENTER);
		lblExtraInfo.setTextAlignment(TextAlignment.CENTER);
		lblExtraInfo.getStyleClass().add(CSS_CLASS_DEVICE_INFO_LABEL);
		VBox.setMargin(lblExtraInfo, new Insets(0, 0, 8, 0));
		
		imagePath = new SVGPath();
		imagePath.getStyleClass().add(CSS_CLASS_BUTTON_SHAPE);
		mainButton = new Button();
		StackPane pane = new StackPane();
		pane.getChildren().add(imagePath);
		mainButton.setGraphic(pane);
		mainButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		mainButton.setAlignment(Pos.CENTER);
		mainButton.getStyleClass().add(CSS_CLASS_DEVICE_BUTTON);

		deviceName = new Label();
		deviceName.setPrefWidth(LABEL_WIDTH);
		deviceName.setWrapText(true);
		deviceName.setAlignment(Pos.TOP_CENTER);
		deviceName.setTextAlignment(TextAlignment.CENTER);
		deviceName.getStyleClass().add(CSS_CLASS_DEVICE_LABEL);
		VBox.setMargin(deviceName, new Insets(5, 0, 0, 0));
		
		this.getChildren().add(lblExtraInfo);
		lblExtraInfo.setVisible(false);
		this.getChildren().add(mainButton);
		this.getChildren().add(deviceName);
		this.setPadding(new Insets(-8, -(LABEL_WIDTH - BUTTON_WIDTH) / 2 + 1, 0, -(LABEL_WIDTH - BUTTON_WIDTH) / 2));
		this.setPrefWidth(BUTTON_WIDTH);
		this.setAlignment(Pos.CENTER);
		
		this.getStyleClass().add(CSS_CLASS_DEVICE_BUTTON_WRAPPER);
	}
	
	private void setImage() {
		switch(deviceInfo.getType()) {
			case BASIC_STACK_PLATE:
			case STACKING:	
				if (deviceInfo.getPutStep() == null) {
					imagePath.setContent(preStackingPath);
					imagePath.getStyleClass().add(CSS_CLASS_PREPROCESS);
					mainButton.getStyleClass().add(CSS_CLASS_BTN_PREPROCESS);
				} else {
					if (deviceInfo.getPickStep() == null) {
						imagePath.setContent(postStackingPath);
						imagePath.getStyleClass().add(CSS_CLASS_POSTPROCESS);
						mainButton.getStyleClass().add(CSS_CLASS_BTN_POSTPROCESS);
					} else {
						throw new IllegalStateException("Unknown stacking-device type [" + deviceInfo.getType() + "].");
					}
				}
				break;
			case CONVEYOR_EATON:
			case CONVEYOR:
				if (deviceInfo.getPutStep() == null) {
					imagePath.setContent(conveyorFromPath);
					imagePath.getStyleClass().add(CSS_CLASS_PREPROCESS);
					mainButton.getStyleClass().add(CSS_CLASS_BTN_PREPROCESS);
				} else {
					if (deviceInfo.getPickStep() == null) {
						imagePath.setContent(conveyorToPath);
						imagePath.getStyleClass().add(CSS_CLASS_POSTPROCESS);
						mainButton.getStyleClass().add(CSS_CLASS_BTN_POSTPROCESS);
					} else {
						throw new IllegalStateException("Unknown stacking-device type [" + deviceInfo.getType() + "].");
					}
				}
				break;
			case OUTPUT_BIN:
				imagePath.setContent(outputBinPath);
				imagePath.getStyleClass().add(CSS_CLASS_POSTPROCESS);
				mainButton.getStyleClass().add(CSS_CLASS_BTN_POSTPROCESS);
				break;
			case PRE_PROCESSING:
				imagePath.setContent(prePocessingPath);
				imagePath.getStyleClass().add(CSS_CLASS_PREPROCESS);
				mainButton.getStyleClass().add(CSS_CLASS_BTN_PREPROCESS);
				break;
			case CNC_MACHINE:
				imagePath.setContent(cncMachinePath);
				imagePath.getStyleClass().add(CSS_CLASS_CNCMACHINE);
				mainButton.getStyleClass().add(CSS_CLASS_BTN_CNCMACHINE);
				break;
			case POST_PROCESSING:
				imagePath.setContent(postProcessingPath);
				imagePath.getStyleClass().add(CSS_CLASS_POSTPROCESS);
				mainButton.getStyleClass().add(CSS_CLASS_BTN_POSTPROCESS);
				break;
			default:
				throw new IllegalArgumentException("Unknown Device type [" + deviceInfo.getType() + "].");
		}
	}
	
	public void animate(final boolean animate) {
		if (animate) {
			if (rotateTransition != null) {
				rotateTransition.play();
			}
		} else {
			if (rotateTransition != null) {
				rotateTransition.stop();
			}
		}
	}
	
	public void setOnAction(final EventHandler<ActionEvent> handler) {
		mainButton.setOnAction(handler);
	}
	
	public void setFocussed(final boolean focussed) {
		this.getStyleClass().remove(CSS_CLASS_DEVICE_UNFOCUSSED);
		if (!focussed) {
			this.getStyleClass().add(CSS_CLASS_DEVICE_UNFOCUSSED);
		}
	}
	
	public void setClickable(final boolean clickable) {
		this.getStyleClass().remove(CSS_CLASS_UNCLICKABLE);
		mainButton.getStyleClass().remove(CSS_CLASS_UNCLICKABLE);
		deviceName.getStyleClass().remove(CSS_CLASS_UNCLICKABLE_NAME);
		lblExtraInfo.getStyleClass().remove(CSS_CLASS_UNCLICKABLE_INFO);
		if (!clickable) {
			this.getStyleClass().add(CSS_CLASS_UNCLICKABLE);
			mainButton.getStyleClass().add(CSS_CLASS_UNCLICKABLE);
			deviceName.getStyleClass().add(CSS_CLASS_UNCLICKABLE_NAME);
			lblExtraInfo.getStyleClass().add(CSS_CLASS_UNCLICKABLE_INFO);
		}
	}

	@Override public void cNCMachineConnected(final CNCMachineEvent event) {
		updateMCodes();
	}
	@Override public void cNCMachineDisconnected(final CNCMachineEvent event) {
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				lblExtraInfo.setText("");
				lblExtraInfo.setVisible(false);
			}
		});
	}
	@Override public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) { }

	@Override
	public void cNCMachineStatusChanged(final CNCMachineEvent event) {
		updateMCodes();
	}
	
	private void updateMCodes() {
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				if ((machine.isConnected()) && (machine.getWayOfOperating() == WayOfOperating.M_CODES)) {
					if (machine.getMCodeAdapter().getActiveMCodes().size() > 0) {
						String mCodes = "GMC";
						for (int i : machine.getMCodeAdapter().getActiveMCodes()) {
							mCodes = mCodes + "-" + (i + 1);
						}
						lblExtraInfo.setText(mCodes);
						lblExtraInfo.setVisible(true);
					} else {
						lblExtraInfo.setText("");
						lblExtraInfo.setVisible(false);
					}
				}
			}
		});
	}
	
	@Override
	public void unregister() {
		machine.removeListener(this);
	}
}
