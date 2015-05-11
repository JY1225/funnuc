package eu.robojob.millassist.ui.general.flow;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.millassist.external.device.processing.cnc.EWayOfOperating;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public final class CNCMachineButton extends DeviceButton implements CNCMachineListener {

    private AbstractCNCMachine machine;
    private Integer[] mcodesForDeviceStep;
    private RotateTransition rotateTransition;

    private static final String cncMachinePath = "M 23.3125 3.90625 C 22.888437 4.8528674 22.52289 5.8478866 22.21875 6.84375 C 21.62757 6.8724768 21.022524 6.8914764 20.4375 7 C 19.848828 7.0788848 19.259529 7.1786847 18.6875 7.34375 C 18.120031 6.4919767 17.488036 5.6307739 16.8125 4.84375 C 15.438856 5.3122709 14.105238 5.8890613 12.875 6.65625 C 13.041205 7.7015874 13.247402 8.6854809 13.53125 9.6875 C 13.062729 10.049321 12.55499 10.373147 12.125 10.78125 C 11.68703 11.180006 11.229282 11.5796 10.84375 12.03125 C 9.9044283 11.619727 8.9135129 11.217229 7.90625 10.90625 C 7.0116145 12.042556 6.2191285 13.251097 5.59375 14.5625 C 6.2955054 15.352944 7.0154292 16.096138 7.78125 16.78125 C 7.5810742 17.339371 7.3439016 17.896493 7.1875 18.46875 C 7.0509336 19.045794 6.8514711 19.59746 6.78125 20.1875 C 5.7696553 20.351197 4.7415163 20.590012 3.71875 20.875 C 3.5616644 22.313393 3.5614364 23.749107 3.71875 25.1875 C 4.7417443 25.47226 5.7696553 25.711303 6.78125 25.875 C 6.8514711 26.464812 7.0509336 27.016706 7.1875 27.59375 C 7.3443576 28.166007 7.5810742 28.722901 7.78125 29.28125 C 7.0154292 29.96659 6.2955054 30.709556 5.59375 31.5 C 6.2191285 32.811175 7.0116145 34.019944 7.90625 35.15625 C 8.9135129 34.845271 9.9042003 34.474251 10.84375 34.0625 C 11.228826 34.513922 11.68703 34.882722 12.125 35.28125 C 12.55499 35.689581 13.062957 36.013179 13.53125 36.375 C 13.247402 37.377247 13.041433 38.360913 12.875 39.40625 C 14.105466 40.173439 15.438628 40.781479 16.8125 41.25 C 17.488036 40.462748 18.120031 39.570523 18.6875 38.71875 C 19.259529 38.883815 19.849056 39.015321 20.4375 39.09375 C 21.022752 39.202502 21.62757 39.221501 22.21875 39.25 C 22.523118 40.246091 22.888209 41.209861 23.3125 42.15625 C 24.751805 42.107688 26.193659 41.921114 27.59375 41.53125 C 27.731456 40.481809 27.803608 39.488765 27.8125 38.4375 C 28.365833 38.223873 28.942319 38.057575 29.46875 37.78125 C 30.004984 37.5259 30.564796 37.261475 31.0625 36.9375 C 31.848612 37.595481 32.685648 38.236595 33.5625 38.8125 C 34.741212 37.973494 35.879349 37.048286 36.84375 35.96875 C 36.39096 35.013013 35.892752 34.128676 35.34375 33.25 C 35.691892 32.771448 36.072456 32.322743 36.375 31.8125 C 36.679824 31.304081 37.009242 30.794214 37.25 30.25 C 38.26889 30.380411 39.311879 30.451651 40.375 30.46875 C 40.913514 29.120869 41.361427 27.745422 41.59375 26.3125 C 40.696379 25.753467 39.79517 25.251382 38.875 24.8125 C 38.930858 24.223372 38.940479 23.621746 38.96875 23.03125 C 38.940479 22.440754 38.93063 21.8389 38.875 21.25 C 39.79517 20.81089 40.696379 20.309033 41.59375 19.75 C 41.361427 18.317078 40.913742 16.941631 40.375 15.59375 C 39.311879 15.610621 38.26889 15.681633 37.25 15.8125 C 37.009242 15.268286 36.679824 14.758419 36.375 14.25 C 36.072228 13.739757 35.692348 13.291052 35.34375 12.8125 C 35.892752 11.933824 36.39096 11.049487 36.84375 10.09375 C 35.879121 9.014214 34.741212 8.0887778 33.5625 7.25 C 32.68542 7.8261325 31.848384 8.4667908 31.0625 9.125 C 30.564568 8.8007972 30.004528 8.5365996 29.46875 8.28125 C 28.942091 8.0049253 28.366061 7.8383993 27.8125 7.625 C 27.803608 6.5737349 27.731456 5.5806912 27.59375 4.53125 C 26.193431 4.1409299 24.752033 3.954812 23.3125 3.90625 z M 22.75 13.6875 C 24.086353 13.686218 25.421893 13.984927 26.625 14.53125 C 28.241454 15.26196 29.631629 16.505521 30.59375 18 C 31.56841 19.496075 32.068443 21.241979 32.09375 23.03125 C 32.068443 24.820293 31.568182 26.565969 30.59375 28.0625 C 29.631401 29.557435 28.241226 30.80054 26.625 31.53125 C 25.020857 32.259908 23.169758 32.529517 21.40625 32.25 C 19.646162 32.015398 17.971057 31.25901 16.625 30.09375 C 15.273015 28.929401 14.283742 27.353869 13.78125 25.65625 C 13.281038 23.957035 13.281266 22.105465 13.78125 20.40625 C 14.283742 18.709087 15.273243 17.133554 16.625 15.96875 C 17.971057 14.803946 19.646162 14.047102 21.40625 13.8125 C 21.847184 13.742564 22.304549 13.687927 22.75 13.6875 z";
    private static final String CSS_CLASS_CNCMACHINE = "cnc-machine";
    private static final String CSS_CLASS_BTN_CNCMACHINE = "btn-cnc";

    public CNCMachineButton(DeviceInformation deviceInfo) {
        super(deviceInfo);
    }
    
    @Override
    public void setDeviceInformation(DeviceInformation deviceInfo) {
        if (machine != null) {
            machine.removeListener(this);
        }
        super.setDeviceInformation(deviceInfo);
        if ((deviceInfo != null) && (deviceInfo.getDevice() != null) && (deviceInfo.getDevice().getType() == EDeviceGroup.CNC_MACHINE)) {
            rotateTransition = new RotateTransition(Duration.millis(5000), imagePath);
            rotateTransition.setFromAngle(0);
            rotateTransition.setToAngle(360);
            rotateTransition.setInterpolator(Interpolator.LINEAR);
            rotateTransition.setCycleCount(Timeline.INDEFINITE);
            machine = ((AbstractCNCMachine) deviceInfo.getDevice());
            machine.addListener(this);
            mcodesForDeviceStep = new Integer[2];
            if (machine.getWayOfOperating().equals(EWayOfOperating.M_CODES) || machine.getWayOfOperating().equals(EWayOfOperating.M_CODES_DUAL_LOAD)) {
                mcodesForDeviceStep[0] = machine.getMCodeIndex(deviceInfo.getPutStep().getDeviceSettings().getWorkArea(), true);
                mcodesForDeviceStep[1] = machine.getMCodeIndex(deviceInfo.getPickStep().getDeviceSettings().getWorkArea(), false);
                updateMCodes();
            }
        }
    }
    
    @Override
    protected void setImage() {
        imagePath.setContent(cncMachinePath);
        imagePath.getStyleClass().add(CSS_CLASS_CNCMACHINE);
        mainButton.getStyleClass().add(CSS_CLASS_BTN_CNCMACHINE);
    }
    
    @Override
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
                if ((machine.isConnected()) && ((machine.getWayOfOperating() == EWayOfOperating.M_CODES) || (machine.getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD))) {
                    if (machine.getMCodeAdapter().getActiveMCodes().size() > 0) {
                        String mCodes = "";
                        if (machine.getMCodeAdapter().getActiveMCodes().contains(mcodesForDeviceStep[0])) {
                            mCodes = "GMC " + (mcodesForDeviceStep[0] + 1);
                        }
                        if (machine.getMCodeAdapter().getActiveMCodes().contains(mcodesForDeviceStep[1])) {
                            if (mCodes.startsWith("GMC")) {
                                mCodes += "-" + (mcodesForDeviceStep[1] + 1);
                            } else {
                                mCodes = "GMC " + (mcodesForDeviceStep[1] + 1);
                            }
                        }
                        lblExtraInfo.setText(mCodes);
                        if (mCodes.equals("")) {
                            lblExtraInfo.setVisible(false);
                        } else {
                            lblExtraInfo.setVisible(true);
                        }
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
