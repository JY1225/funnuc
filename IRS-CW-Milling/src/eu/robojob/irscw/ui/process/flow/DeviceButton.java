package eu.robojob.irscw.ui.process.flow;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class DeviceButton extends VBox {
	
	public enum DeviceType {
		PRE_STACKING, PRE_PROCESSING, CNC_MACHINE, POST_PROCESSING, POST_STACKING
	}
	
	private String preStackingPath = "M 45.516149,13.002635 27.32012,0.6258018 v 6.315935 C 1.6407593,8.3113848 0.51614826,34.845316 0.51614826,35.100909 l 4.44510294,-0.0033 c 0,-0.04779 2.4225812,-15.47896 22.3588688,-16.492999 v 6.758224 L 45.516149,13.002635 z";
	private String postStackingPath = "M 45.516149,22.724076 27.32012,35.100909 V 28.784974 C 1.6407593,27.415326 0.51614826,0.8813948 0.51614826,0.6258018 l 4.44510294,0.0033 c 0,0.04779 2.4225812,15.4789602 22.3588688,16.4929992 v -6.758224 l 18.196029,12.360199 z";
	private String cncMachinePath = "M 23.4375 0.53125 C 22.938603 1.6449175 22.482811 2.797146 22.125 3.96875 C 21.429494 4.0025463 20.750764 4.0285752 20.0625 4.15625 C 19.369945 4.2490556 18.672975 4.3683056 18 4.5625 C 17.33239 3.5604138 16.607249 2.5509105 15.8125 1.625 C 14.196448 2.176201 12.603589 2.8474251 11.15625 3.75 C 11.351786 4.9798086 11.603561 6.164904 11.9375 7.34375 C 11.386299 7.769422 10.787121 8.1448784 10.28125 8.625 C 9.765991 9.0941244 9.2348174 9.5311476 8.78125 10.0625 C 7.6761657 9.578355 6.4975152 9.147108 5.3125 8.78125 C 4.2599877 10.11808 3.2982394 11.550924 2.5625 13.09375 C 3.3880945 14.023684 4.2552844 14.881486 5.15625 15.6875 C 4.920749 16.344113 4.6527519 16.983007 4.46875 17.65625 C 4.3080836 18.335126 4.0826131 18.993335 4 19.6875 C 2.8098886 19.880085 1.6095044 20.16472 0.40625 20.5 C 0.22144342 22.192227 0.2211752 23.870273 0.40625 25.5625 C 1.6097727 25.897512 2.8098886 26.182415 4 26.375 C 4.0826131 27.068896 4.3080836 27.727374 4.46875 28.40625 C 4.6532884 29.079493 4.920749 29.718119 5.15625 30.375 C 4.2552844 31.181282 3.3880945 32.038816 2.5625 32.96875 C 3.2982394 34.511308 4.2599877 35.94442 5.3125 37.28125 C 6.4975152 36.915392 7.6758975 36.484413 8.78125 36 C 9.2342809 36.531084 9.765991 36.968644 10.28125 37.4375 C 10.787121 37.91789 11.386567 38.293078 11.9375 38.71875 C 11.603561 39.897864 11.352054 41.082691 11.15625 42.3125 C 12.603857 43.215075 14.19618 43.886299 15.8125 44.4375 C 16.607249 43.511321 17.33239 42.502086 18 41.5 C 18.672975 41.694194 19.370213 41.813981 20.0625 41.90625 C 20.751032 42.034193 21.429494 42.060222 22.125 42.09375 C 22.48308 43.265622 22.938334 44.417851 23.4375 45.53125 C 25.1308 45.474118 26.821584 45.271164 28.46875 44.8125 C 28.630758 43.577863 28.739539 42.393032 28.75 41.15625 C 29.40098 40.904924 30.06817 40.700088 30.6875 40.375 C 31.318364 40.074589 31.945716 39.787397 32.53125 39.40625 C 33.456088 40.180345 34.468409 40.947465 35.5 41.625 C 36.88672 40.637934 38.209161 39.520042 39.34375 38.25 C 38.811056 37.125604 38.239634 36.064987 37.59375 35.03125 C 38.003329 34.468247 38.456566 33.944036 38.8125 33.34375 C 39.171116 32.74561 39.560505 32.171502 39.84375 31.53125 C 41.042445 31.684674 42.28052 31.761133 43.53125 31.78125 C 44.164796 30.195508 44.664179 28.56079 44.9375 26.875 C 43.881769 26.217314 42.832553 25.641332 41.75 25.125 C 41.815715 24.431908 41.81049 23.725951 41.84375 23.03125 C 41.81049 22.336549 41.815447 21.630323 41.75 20.9375 C 42.832553 20.4209 43.881769 19.845186 44.9375 19.1875 C 44.664179 17.50171 44.165064 15.866992 43.53125 14.28125 C 42.28052 14.301099 41.042445 14.377289 39.84375 14.53125 C 39.560505 13.890998 39.171116 13.31689 38.8125 12.71875 C 38.456298 12.118464 38.003865 11.594253 37.59375 11.03125 C 38.239634 9.9975134 38.811056 8.9368965 39.34375 7.8125 C 38.208893 6.5424577 36.88672 5.4555474 35.5 4.46875 C 34.468141 5.1465529 33.455819 5.8818863 32.53125 6.65625 C 31.945448 6.274835 31.317827 5.9879113 30.6875 5.6875 C 30.067902 5.3624121 29.401249 5.157308 28.75 4.90625 C 28.739539 3.6694675 28.630758 2.4846367 28.46875 1.25 C 26.821316 0.79079991 25.131069 0.58838179 23.4375 0.53125 z M 22.78125 12.0625 C 24.35343 12.060991 25.92833 12.388517 27.34375 13.03125 C 29.245461 13.890909 30.868093 15.335539 32 17.09375 C 33.146659 18.853838 33.720227 20.926225 33.75 23.03125 C 33.720227 25.136006 33.146391 27.208125 32 28.96875 C 30.867825 30.727497 29.245192 32.171591 27.34375 33.03125 C 25.456523 33.888495 23.293465 34.235093 21.21875 33.90625 C 19.148058 33.630247 17.177347 32.714645 15.59375 31.34375 C 14.00318 29.973928 12.809916 28.122199 12.21875 26.125 C 11.630266 24.125924 11.630534 21.936576 12.21875 19.9375 C 12.809916 17.940838 14.003448 16.120358 15.59375 14.75 C 17.177347 13.379642 19.116808 12.432253 21.1875 12.15625 C 21.706246 12.073972 22.25719 12.063003 22.78125 12.0625 z M 22.0625 12.96875 C 21.822399 12.987596 21.582034 13.024446 21.34375 13.0625 C 19.444722 13.314363 17.642349 14.14801 16.1875 15.40625 C 14.727018 16.663954 13.6361 18.357942 13.09375 20.1875 C 12.554083 22.018131 12.554083 24.044369 13.09375 25.875 C 13.6361 27.70429 14.726482 29.39801 16.1875 30.65625 C 17.642349 31.913954 19.44499 32.747869 21.34375 33 C 23.249752 33.304435 25.209062 32.97152 26.9375 32.1875 C 28.680958 31.399725 30.211167 30.082119 31.25 28.46875 C 32.302781 26.85243 32.813172 24.965952 32.84375 23.03125 C 32.813172 21.095743 32.302781 19.209802 31.25 17.59375 C 30.211167 15.980649 28.680958 14.662775 26.9375 13.875 C 25.424882 13.188748 23.743204 12.836825 22.0625 12.96875 z M 22.78125 15 C 23.936723 14.998366 25.064372 15.253045 26.09375 15.71875 C 27.482884 16.344249 28.701632 17.399487 29.53125 18.6875 C 30.373743 19.980073 30.780313 21.477158 30.8125 23.03125 C 30.780313 24.585074 30.373474 26.082427 29.53125 27.375 C 28.701364 28.663013 27.482884 29.718251 26.09375 30.34375 C 24.721514 30.964958 23.152538 31.249717 21.625 31 C 20.110874 30.801782 18.665703 30.130841 17.5 29.125 C 16.330005 28.118086 15.495146 26.766544 15.0625 25.3125 C 14.632536 23.85926 14.632268 22.203508 15.0625 20.75 C 15.494878 19.296224 16.330005 17.975664 17.5 16.96875 C 18.665703 15.963177 20.111142 15.2917 21.625 15.09375 C 22.006951 15.031321 22.396092 15.000545 22.78125 15 z";
	private String prePocessingPath = "M 35.40625 -0.125 C 35.18451 0.369965 34.971527 0.88553897 34.8125 1.40625 C 34.50338 1.42127 34.212143 1.41202 33.90625 1.46875 C 33.598443 1.51 33.299107 1.56994 33 1.65625 C 32.703287 1.2108788 32.35322 0.755266 32 0.34375 C 31.281753 0.588728 30.58076 0.88010797 29.9375 1.28125 C 30.02441 1.8278298 30.13283 2.351066 30.28125 2.875 C 30.03627 3.064187 29.787327 3.255364 29.5625 3.46875 C 29.333493 3.677248 29.076587 3.857595 28.875 4.09375 C 28.383854 3.878572 27.870426 3.693854 27.34375 3.53125 C 26.875968 4.125396 26.451996 4.751803 26.125 5.4375 C 26.491933 5.850807 26.880818 6.235519 27.28125 6.59375 C 27.176578 6.885577 27.050528 7.169532 26.96875 7.46875 C 26.89734 7.770475 26.786716 8.066485 26.75 8.375 C 26.221062 8.4606 25.691027 8.569736 25.15625 8.71875 C 25.07411 9.470852 25.07399 10.247898 25.15625 11 C 25.691147 11.148894 26.221062 11.258158 26.75 11.34375 C 26.78671 11.65215 26.897341 11.94828 26.96875 12.25 C 27.05077 12.549218 27.176583 12.833053 27.28125 13.125 C 26.880823 13.483351 26.491929 13.867947 26.125 14.28125 C 26.451996 14.966832 26.875968 15.593353 27.34375 16.1875 C 27.870426 16.024895 28.383734 15.840294 28.875 15.625 C 29.076347 15.861036 29.333493 16.072868 29.5625 16.28125 C 29.787327 16.494757 30.03639 16.654559 30.28125 16.84375 C 30.13283 17.367799 30.024527 17.890922 29.9375 18.4375 C 30.58088 18.838647 31.281633 19.130023 32 19.375 C 32.35322 18.963365 32.703287 18.507869 33 18.0625 C 33.2991 18.14881 33.598563 18.208991 33.90625 18.25 C 34.212263 18.30686 34.50338 18.32885 34.8125 18.34375 C 34.97164 18.864581 35.18439 19.380155 35.40625 19.875 C 36.158823 19.84962 36.924177 19.735099 37.65625 19.53125 C 37.72825 18.982522 37.745347 18.455934 37.75 17.90625 C 38.03932 17.794552 38.34974 17.706984 38.625 17.5625 C 38.905387 17.428984 39.20851 17.2944 39.46875 17.125 C 39.87979 17.469044 40.322763 17.823871 40.78125 18.125 C 41.39757 17.686302 41.964483 17.189462 42.46875 16.625 C 42.231996 16.125266 41.97456 15.646935 41.6875 15.1875 C 41.869533 14.937273 42.091807 14.704291 42.25 14.4375 C 42.40938 14.17166 42.561613 13.90956 42.6875 13.625 C 43.220253 13.69319 43.78787 13.741058 44.34375 13.75 C 44.625324 13.045227 44.847277 12.31174 44.96875 11.5625 C 44.499543 11.270193 44.01239 11.010734 43.53125 10.78125 C 43.56045 10.47321 43.57895 10.183755 43.59375 9.875 C 43.57895 9.566245 43.56032 9.245425 43.53125 8.9375 C 44.01239 8.7079 44.499543 8.448552 44.96875 8.15625 C 44.847277 7.40701 44.625444 6.673524 44.34375 5.96875 C 43.78787 5.97755 43.220253 6.025323 42.6875 6.09375 C 42.561613 5.809195 42.409386 5.54709 42.25 5.28125 C 42.091693 5.014455 41.869773 4.781472 41.6875 4.53125 C 41.97456 4.07181 42.231996 3.593484 42.46875 3.09375 C 41.96437 2.529287 41.36632 2.0323229 40.75 1.59375 C 40.291394 1.8949939 39.84842 2.249589 39.4375 2.59375 C 39.17714 2.424229 38.905147 2.289766 38.625 2.15625 C 38.34962 2.011766 38.039447 1.924083 37.75 1.8125 C 37.7453 1.2628204 37.728253 0.736224 37.65625 0.1875 C 36.924057 -0.016589 36.158943 -0.099609 35.40625 -0.125 z M 35.09375 4.96875 C 35.792495 4.9680862 36.49592 5.1518618 37.125 5.4375 C 37.970206 5.819571 38.715684 6.437323 39.21875 7.21875 C 39.728377 8.001012 39.986767 8.93943 40 9.875 C 39.98673 10.810444 39.728257 11.717502 39.21875 12.5 C 38.715564 13.281666 37.970086 13.930429 37.125 14.3125 C 36.286226 14.693496 35.32835 14.83365 34.40625 14.6875 C 33.485943 14.564828 32.61007 14.171788 31.90625 13.5625 C 31.19933 12.953691 30.700246 12.137644 30.4375 11.25 C 30.175954 10.361524 30.176074 9.38848 30.4375 8.5 C 30.700246 7.612596 31.19945 6.796544 31.90625 6.1875 C 32.61007 5.578451 33.485943 5.153917 34.40625 5.03125 C 34.636803 4.9946823 34.860835 4.9689713 35.09375 4.96875 z M 15.59375 8.0625 C 15.26114 8.804947 14.98854 9.5939343 14.75 10.375 C 14.28632 10.39753 13.80259 10.414897 13.34375 10.5 C 12.88204 10.56187 12.44866 10.620533 12 10.75 C 11.55493 10.081941 11.02983 9.429773 10.5 8.8125 C 9.4226297 9.179966 8.3711396 9.6170363 7.40625 10.21875 C 7.53661 11.038622 7.71487 11.8391 7.9375 12.625 C 7.57003 12.90878 7.18099 13.14867 6.84375 13.46875 C 6.500239 13.781496 6.146131 14.083267 5.84375 14.4375 C 5.107031 14.114733 4.321263 13.806407 3.53125 13.5625 C 2.829576 14.453719 2.177994 15.408954 1.6875 16.4375 C 2.2379 17.05746 2.805603 17.650154 3.40625 18.1875 C 3.249243 18.625241 3.091417 19.051173 2.96875 19.5 C 2.861636 19.952587 2.711324 20.380977 2.65625 20.84375 C 1.862844 20.972144 1.052166 21.151479 0.25 21.375 C 0.126793 22.503153 0.126613 23.653097 0.25 24.78125 C 1.052346 25.004591 1.862844 25.184113 2.65625 25.3125 C 2.71131 25.7751 2.861636 26.20367 2.96875 26.65625 C 3.091777 27.105077 3.24925 27.530829 3.40625 27.96875 C 2.80561 28.506276 2.237893 29.098796 1.6875 29.71875 C 2.177994 30.747123 2.829576 31.67128 3.53125 32.5625 C 4.321263 32.318593 5.106851 32.041691 5.84375 31.71875 C 6.145771 32.072804 6.500239 32.374928 6.84375 32.6875 C 7.18099 33.007761 7.57021 33.247463 7.9375 33.53125 C 7.71487 34.317323 7.53679 35.117634 7.40625 35.9375 C 8.3713196 36.539221 9.4224497 36.976284 10.5 37.34375 C 11.02983 36.726297 11.55493 36.043053 12 35.375 C 12.44865 35.504461 12.88222 35.594737 13.34375 35.65625 C 13.80277 35.74154 14.28632 35.758897 14.75 35.78125 C 14.98871 36.562496 15.26096 37.320233 15.59375 38.0625 C 16.72261 38.02443 17.87064 37.899523 18.96875 37.59375 C 19.07675 36.770657 19.14927 35.980777 19.15625 35.15625 C 19.59023 34.988703 20.02461 34.872976 20.4375 34.65625 C 20.85808 34.455976 21.29714 34.2541 21.6875 34 C 22.30406 34.516066 22.96852 35.017057 23.65625 35.46875 C 24.58073 34.810703 25.46235 34.065444 26.21875 33.21875 C 25.86362 32.46915 25.46184 31.782903 25.03125 31.09375 C 25.3043 30.718409 25.63771 30.368936 25.875 29.96875 C 26.11407 29.56999 26.34242 29.17684 26.53125 28.75 C 27.33038 28.852286 28.16618 28.892838 29 28.90625 C 29.42236 27.849091 29.75529 26.78011 29.9375 25.65625 C 29.23369 25.21779 28.53421 24.812976 27.8125 24.46875 C 27.8563 24.00669 27.8528 23.525633 27.875 23.0625 C 27.8528 22.599367 27.8561 22.149387 27.8125 21.6875 C 28.53421 21.3431 29.23369 20.938453 29.9375 20.5 C 29.75529 19.37614 29.42254 18.30716 29 17.25 C 28.16618 17.2632 27.33038 17.30361 26.53125 17.40625 C 26.34242 16.979417 26.11408 16.58626 25.875 16.1875 C 25.63754 15.787307 25.30466 15.437834 25.03125 15.0625 C 25.46184 14.37334 25.86362 13.6871 26.21875 12.9375 C 25.46218 12.090806 24.58073 11.34536 23.65625 10.6875 C 22.96834 11.139366 22.30388 11.64001 21.6875 12.15625 C 21.29696 11.90197 20.85772 11.700274 20.4375 11.5 C 20.02443 11.283274 19.59042 11.167374 19.15625 11 C 19.14925 10.17548 19.07676 9.385587 18.96875 8.5625 C 17.87046 8.256367 16.72279 8.100586 15.59375 8.0625 z M 15.15625 15.75 C 16.204368 15.749002 17.24388 15.977786 18.1875 16.40625 C 19.45531 16.979357 20.5579 17.952859 21.3125 19.125 C 22.07694 20.298393 22.48015 21.659146 22.5 23.0625 C 22.4801 24.465667 22.07676 25.857504 21.3125 27.03125 C 20.55772 28.20375 19.45513 29.176893 18.1875 29.75 C 16.92934 30.321494 15.50815 30.531726 14.125 30.3125 C 12.74454 30.128493 11.43073 29.507683 10.375 28.59375 C 9.3146197 27.680537 8.5191196 26.487716 8.125 25.15625 C 7.73268 23.823536 7.73286 22.33272 8.125 21 C 8.5191196 19.668894 9.3147997 18.444816 10.375 17.53125 C 11.43073 16.617677 12.74454 16.02775 14.125 15.84375 C 14.47083 15.788899 14.806877 15.750333 15.15625 15.75 z";
	private String postProcessingPath = "m 9.7200272,-0.125 c 0.22174,0.494965 0.4347228,1.01053897 0.5937498,1.53125 0.30912,0.01502 0.600357,0.00577 0.90625,0.0625 0.307807,0.04125 0.607143,0.10119 0.90625,0.1875 0.296713,-0.4453712 0.64678,-0.900984 1,-1.3125 0.718247,0.244978 1.41924,0.53635797 2.0625,0.9375 -0.08691,0.5465798 -0.19533,1.069816 -0.34375,1.59375 0.24498,0.189187 0.493923,0.380364 0.71875,0.59375 0.229007,0.208498 0.485913,0.388845 0.6875,0.625 0.491146,-0.215178 1.004574,-0.399896 1.53125,-0.5625 0.467782,0.594146 0.891754,1.220553 1.21875,1.90625 -0.366933,0.413307 -0.755818,0.798019 -1.15625,1.15625 0.104672,0.291827 0.230722,0.575782 0.3125,0.875 0.07141,0.301725 0.182034,0.597735 0.21875,0.90625 0.528938,0.0856 1.058973,0.194736 1.59375,0.34375 0.08214,0.752102 0.08226,1.529148 0,2.28125 -0.534897,0.148894 -1.064812,0.258158 -1.59375,0.34375 -0.03671,0.3084 -0.147341,0.60453 -0.21875,0.90625 -0.08202,0.299218 -0.207833,0.583053 -0.3125,0.875 0.400427,0.358351 0.789321,0.742947 1.15625,1.15625 -0.326996,0.685582 -0.750968,1.312103 -1.21875,1.90625 -0.526676,-0.162605 -1.039984,-0.347206 -1.53125,-0.5625 -0.201347,0.236036 -0.458493,0.447868 -0.6875,0.65625 -0.224827,0.213507 -0.47389,0.373309 -0.71875,0.5625 0.14842,0.524049 0.256723,1.047172 0.34375,1.59375 -0.64338,0.401147 -1.344133,0.692523 -2.0625,0.9375 -0.35322,-0.411635 -0.703287,-0.867131 -1,-1.3125 -0.2991,0.08631 -0.598563,0.146491 -0.90625,0.1875 -0.306013,0.05686 -0.59713,0.07885 -0.90625,0.09375 -0.15914,0.520831 -0.3718898,1.036405 -0.5937498,1.53125 -0.752573,-0.02538 -1.517927,-0.139901 -2.25,-0.34375 -0.072,-0.548728 -0.089097,-1.075316 -0.09375,-1.625 -0.28932,-0.111698 -0.59974,-0.199266 -0.875,-0.34375 -0.280387,-0.133516 -0.58351,-0.2681 -0.84375,-0.4375 -0.41104,0.344044 -0.854013,0.698871 -1.3125,1 -0.61632,-0.438698 -1.183233,-0.935538 -1.6875,-1.5 0.236754,-0.499734 0.49419,-0.978065 0.78125,-1.4375 -0.182033,-0.250227 -0.404307,-0.483209 -0.5625,-0.75 -0.15938,-0.26584 -0.311613,-0.52794 -0.4375,-0.8125 -0.532753,0.06819 -1.10037,0.116058 -1.65624996,0.125 -0.281574,-0.704773 -0.503527,-1.43826 -0.625,-2.1875 0.469207,-0.292307 0.95635996,-0.551766 1.43749996,-0.78125 -0.0292,-0.30804 -0.0477,-0.597495 -0.0625,-0.90625 0.0148,-0.308755 0.03343,-0.629575 0.0625,-0.9375 -0.48114,-0.2296 -0.96829296,-0.488948 -1.43749996,-0.78125 0.121473,-0.74924 0.343306,-1.482726 0.625,-2.1875 0.55587996,0.0088 1.12349696,0.056573 1.65624996,0.125 0.125887,-0.284555 0.278114,-0.54666 0.4375,-0.8125 0.158307,-0.266795 0.380227,-0.499778 0.5625,-0.75 -0.28706,-0.45944 -0.544496,-0.937766 -0.78125,-1.4375 0.50438,-0.564463 1.10243,-1.0614271 1.71875,-1.5 0.458606,0.3012439 0.90158,0.655839 1.3125,1 0.26036,-0.169521 0.532353,-0.303984 0.8125,-0.4375 0.27538,-0.144484 0.585553,-0.232167 0.875,-0.34375 0.0047,-0.5496796 0.021747,-1.076276 0.09375,-1.625 0.732193,-0.204089 1.497307,-0.287109 2.25,-0.3125 z M 10.032527,4.96875 C 9.3337822,4.9680862 8.6303572,5.1518618 8.0012772,5.4375 c -0.845206,0.382071 -1.590684,0.999823 -2.09375,1.78125 -0.509627,0.782262 -0.768017,1.72068 -0.78125,2.65625 0.01327,0.935444 0.271743,1.842502 0.78125,2.625 0.503186,0.781666 1.248664,1.430429 2.09375,1.8125 0.838774,0.380996 1.79665,0.52115 2.7187498,0.375 0.920307,-0.122672 1.79618,-0.515712 2.5,-1.125 0.70692,-0.608809 1.206004,-1.424856 1.46875,-2.3125 0.261546,-0.888476 0.261426,-1.86152 0,-2.75 -0.262746,-0.887404 -0.76195,-1.703456 -1.46875,-2.3125 -0.70382,-0.609049 -1.579693,-1.033583 -2.5,-1.15625 -0.230553,-0.036568 -0.454585,-0.062279 -0.6875,-0.0625 z m 19.5,3.09375 c 0.33261,0.742447 0.60521,1.5314343 0.84375,2.3125 0.46368,0.02253 0.94741,0.0399 1.40625,0.125 0.46171,0.06187 0.89509,0.120533 1.34375,0.25 0.44507,-0.668059 0.97017,-1.320227 1.5,-1.9375 1.077371,0.367466 2.128861,0.8045363 3.09375,1.40625 -0.13036,0.819872 -0.30862,1.62035 -0.53125,2.40625 0.36747,0.28378 0.75651,0.52367 1.09375,0.84375 0.343511,0.312746 0.697619,0.614517 1,0.96875 0.736719,-0.322767 1.522487,-0.631093 2.3125,-0.875 0.701674,0.891219 1.353256,1.846454 1.84375,2.875 -0.5504,0.61996 -1.118103,1.212654 -1.71875,1.75 0.157007,0.437741 0.314833,0.863673 0.4375,1.3125 0.107114,0.452587 0.257426,0.880977 0.3125,1.34375 0.793406,0.128394 1.604084,0.307729 2.40625,0.53125 0.123207,1.128153 0.123387,2.278097 0,3.40625 -0.802346,0.223341 -1.612844,0.402863 -2.40625,0.53125 -0.05506,0.4626 -0.205386,0.89117 -0.3125,1.34375 -0.123027,0.448827 -0.2805,0.874579 -0.4375,1.3125 0.60064,0.537526 1.168357,1.130046 1.71875,1.75 -0.490494,1.028373 -1.142076,1.95253 -1.84375,2.84375 -0.790013,-0.243907 -1.575601,-0.520809 -2.3125,-0.84375 -0.302021,0.354054 -0.656489,0.656178 -1,0.96875 -0.33724,0.320261 -0.72646,0.559963 -1.09375,0.84375 0.22263,0.786073 0.40071,1.586384 0.53125,2.40625 -0.965069,0.601721 -2.016199,1.038784 -3.09375,1.40625 -0.52983,-0.617453 -1.05493,-1.300697 -1.5,-1.96875 -0.44865,0.129461 -0.88222,0.219737 -1.34375,0.28125 -0.45902,0.08529 -0.94257,0.102647 -1.40625,0.125 -0.23871,0.781246 -0.51096,1.538983 -0.84375,2.28125 -1.12886,-0.03807 -2.27689,-0.162977 -3.375,-0.46875 -0.108,-0.823093 -0.18052,-1.612973 -0.1875,-2.4375 -0.43398,-0.167547 -0.86836,-0.283274 -1.28125,-0.5 -0.42058,-0.200274 -0.85964,-0.40215 -1.25,-0.65625 -0.61656,0.516066 -1.28102,1.017057 -1.96875,1.46875 -0.92448,-0.658047 -1.8061,-1.403306 -2.5625,-2.25 0.35513,-0.7496 0.75691,-1.435847 1.1875,-2.125 -0.27305,-0.375341 -0.60646,-0.724814 -0.84375,-1.125 -0.23907,-0.39876 -0.46742,-0.79191 -0.65625,-1.21875 -0.79913,0.102286 -1.63493,0.142838 -2.46875,0.15625 -0.42236,-1.057159 -0.75529,-2.12614 -0.9375,-3.25 0.70381,-0.43846 1.40329,-0.843274 2.125,-1.1875 -0.0438,-0.46206 -0.0403,-0.943117 -0.0625,-1.40625 0.0222,-0.463133 0.0189,-0.913113 0.0625,-1.375 -0.72171,-0.3444 -1.42119,-0.749047 -2.125,-1.1875 0.18221,-1.12386 0.51496,-2.19284 0.9375,-3.25 0.83382,0.0132 1.66962,0.05361 2.46875,0.15625 0.18883,-0.426833 0.41717,-0.81999 0.65625,-1.21875 0.23746,-0.400193 0.57034,-0.749666 0.84375,-1.125 -0.43059,-0.68916 -0.83237,-1.3754 -1.1875,-2.125 0.75657,-0.846694 1.63802,-1.59214 2.5625,-2.25 0.68791,0.451866 1.35237,0.95251 1.96875,1.46875 0.39054,-0.25428 0.82978,-0.455976 1.25,-0.65625 0.41307,-0.216726 0.84708,-0.332626 1.28125,-0.5 0.007,-0.82452 0.07949,-1.614413 0.1875,-2.4375 1.09829,-0.306133 2.24596,-0.461914 3.375,-0.5 z m 0.4375,7.6875 c -1.048118,-9.98e-4 -2.08763,0.227786 -3.03125,0.65625 -1.26781,0.573107 -2.3704,1.546609 -3.125,2.71875 -0.76444,1.173393 -1.16765,2.534146 -1.1875,3.9375 0.0199,1.403167 0.42324,2.795004 1.1875,3.96875 0.75478,1.1725 1.85737,2.145643 3.125,2.71875 1.25816,0.571494 2.67935,0.781726 4.0625,0.5625 1.38046,-0.184007 2.69427,-0.804817 3.75,-1.71875 1.060381,-0.913213 1.855881,-2.106034 2.25,-3.4375 0.39232,-1.332714 0.39214,-2.82353 0,-4.15625 -0.394119,-1.331106 -1.189799,-2.555184 -2.25,-3.46875 -1.05573,-0.913573 -2.36954,-1.5035 -3.75,-1.6875 -0.34583,-0.05485 -0.681877,-0.09342 -1.03125,-0.09375 z";
	
	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_HEIGHT = 60;
	private static final int LABEL_WIDTH = 120;
	
	private Button mainButton;
	private SVGPath imagePath;
	private Label deviceName;
	private DeviceType type;
	private String name;
	
	public DeviceButton(String name, DeviceType type) {
		deviceName = new Label(name);
		this.name = name;
		build();
		setType(type);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private void build() {
		imagePath = new SVGPath();
		mainButton = new Button();
		mainButton.setGraphic(imagePath);
		mainButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		mainButton.setAlignment(Pos.CENTER);
		mainButton.getStyleClass().add("device-button");

		deviceName = new Label(name);
		deviceName.setPrefWidth(LABEL_WIDTH);
		deviceName.setAlignment(Pos.CENTER);
		deviceName.setTextAlignment(TextAlignment.CENTER);
		
		this.getChildren().add(mainButton);
		this.getChildren().add(deviceName);
		this.setPadding(new Insets(0, -(LABEL_WIDTH-BUTTON_WIDTH)/2, 0, -(LABEL_WIDTH-BUTTON_WIDTH)/2));
		this.setPrefWidth(BUTTON_WIDTH);
		this.setAlignment(Pos.CENTER);
	}
	
	public void setType(DeviceType type) {
		this.type = type;
		switch(type) {
			case PRE_STACKING:	
				imagePath.setContent(preStackingPath);
				imagePath.getStyleClass().add("pre-process");
				break;
			case PRE_PROCESSING:
				imagePath.setContent(prePocessingPath);
				imagePath.getStyleClass().add("pre-process");
				break;
			case CNC_MACHINE:
				imagePath.setContent(cncMachinePath);
				imagePath.getStyleClass().add("cnc-machine");
				break;
			case POST_PROCESSING:
				imagePath.setContent(postProcessingPath);
				imagePath.getStyleClass().add("post-process");
				break;
			case POST_STACKING:
				imagePath.setContent(postStackingPath);
				imagePath.getStyleClass().add("post-process");
				break;
			default:
				throw new IllegalArgumentException("Unknown Device type");
		}
	}
	
	public void animate() {
		RotateTransition rt = new RotateTransition(Duration.millis(5000), imagePath);
		rt.setFromAngle(0);
		rt.setToAngle(360);
		rt.setInterpolator(Interpolator.LINEAR);
		rt.setCycleCount(Timeline.INDEFINITE);
		rt.play();
	}
	
}
