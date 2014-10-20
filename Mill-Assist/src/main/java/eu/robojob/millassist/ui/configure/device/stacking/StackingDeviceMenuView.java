package eu.robojob.millassist.ui.configure.device.stacking;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.external.device.stacking.conveyor.AbstractConveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.ui.general.model.DeviceInformation;
import eu.robojob.millassist.util.Translator;

public class StackingDeviceMenuView extends AbstractMenuView<AbstractStackingDeviceMenuPresenter> {

	private DeviceInformation deviceInfo;
	
	private static final String FROM_ICON = "m 12.0625,-0.21875 0,2.8125 C 0.64945154,3.202482 0.15625,14.980153 0.15625,15.09375 l 1.96875,0 c 0,-0.02124 1.0769284,-6.861816 9.9375,-7.3125 l 0,3 8.09375,-5.5 -8.09375,-5.5 z";
	private static final String TO_ICON = "M 0.15625 -0.21875 C 0.15625 -0.105153 0.64945154 11.672518 12.0625 12.28125 L 12.0625 15.09375 L 20.15625 9.59375 L 12.0625 4.09375 L 12.0625 7.09375 C 3.2019284 6.643066 2.125 -0.19751 2.125 -0.21875 L 0.15625 -0.21875 z";
	private static final String WORKPIECE_ICON = "M 6.25 0 L 4 3.375 L 5.65625 3.375 L 5.65625 6.25 L 6.875 6.25 L 6.875 3.375 L 8.5625 3.375 L 6.25 0 z M 0 7.5 L 0 16.875 L 12.5 16.875 L 12.5 7.5 L 0 7.5 z M 16.65625 9.90625 L 16.65625 11.625 L 13.75 11.625 L 13.75 12.78125 L 16.65625 12.78125 L 16.65625 14.46875 L 20 12.1875 L 16.65625 9.90625 z";
	private static final String LAYOUT_ICON = "m 4.3125,0.12499999 c -1.9028305,0 -3.5,1.56110911 -3.5,3.50000001 0,1.938891 1.5971695,3.5 3.5,3.5 1.9028305,0 3.46875,-1.561109 3.46875,-3.5 0,-1.9388909 -1.5659195,-3.50000001 -3.46875,-3.50000001 z m 12.125,0 c -1.90283,0 -3.46875,1.56110911 -3.46875,3.50000001 0,1.938891 1.56592,3.5 3.46875,3.5 1.902831,0 3.5,-1.561109 3.5,-3.5 0,-1.9388909 -1.597169,-3.50000001 -3.5,-3.50000001 z M 4.3125,1.3125 c 1.309224,0 2.3125,1.0393364 2.3125,2.3125 0,1.273164 -1.003276,2.3125 -2.3125,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.2731636 1.034526,-2.3125 2.34375,-2.3125 z m 12.125,0 c 1.309224,0 2.34375,1.0393364 2.34375,2.3125 0,1.273164 -1.034526,2.3125 -2.34375,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.2731636 1.034526,-2.3125 2.34375,-2.3125 z m -12.125,9.03125 c -2.4090408,0 -4.375,1.965959 -4.375,4.375 0,2.409041 1.9659592,4.34375 4.375,4.34375 2.4090408,0 4.34375,-1.934709 4.34375,-4.34375 0,-2.409041 -1.9347092,-4.375 -4.34375,-4.375 z m 12.125,0.875 c -1.90283,0 -3.46875,1.561109 -3.46875,3.5 0,1.938891 1.56592,3.46875 3.46875,3.46875 1.902831,0 3.5,-1.529859 3.5,-3.46875 0,-1.938891 -1.597169,-3.5 -3.5,-3.5 z m 0,1.15625 c 1.309224,0 2.34375,1.070586 2.34375,2.34375 0,1.273164 -1.034526,2.3125 -2.34375,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.273164 1.034526,-2.34375 2.34375,-2.34375 z";
	private static final String LAYOUT_CONVEYOR_ICON = "m 14.78125,99.3125 -0.65625,0 0,2.46875 1.34375,0 0,-1.78125 1.75,0 0,-1.375 -2.4375,0 z M 19.9375,100 l 2.6875,0 0,-1.375 -2.6875,0 z m 5.375,0 2.71875,0 0,-1.375 -2.71875,0 z m 5.40625,0 1.3125,0 0,0.0312 1.34375,0 0,-0.71875 0,-0.6875 -0.6875,0 -1.96875,0 z m 1.3125,5.4375 1.34375,0 0,-2.6875 -1.34375,0 z m -17.90625,1.75 1.34375,0 0,-2.71875 -1.34375,0 z m 17.90625,1.65625 -0.65625,0 0,1.34375 1.3125,0 0.6875,0 0,-0.6875 0,-1.34375 -1.34375,0 z m -16.875,1.34375 2.71875,0 0,-1.34375 -2.71875,0 z m 5.40625,0 2.6875,0 0,-1.34375 -2.6875,0 z m 5.40625,0 2.6875,0 0,-1.34375 -2.6875,0 z";
	private static final String OFFSETS_ICON = "m 4.25,-2.53125 -2.25,3.375 1.65625,0 0,2.875 1.21875,0 0,-2.875 1.6875,0 -2.3125,-3.375 z M 0,6.71875 0,8.5 l 15.65625,0 0,-1.78125 L 0,6.71875 z M 10.75,11.5 l 0,2.875 -1.65625,0 2.25,3.375 2.3125,-3.375 -1.6875,0 0,-2.875 -1.21875,0 z";
	private static final String PICK_ICON = "M 4.6875 0.09375 C 2.161259 0.09375 0.09375 2.161495 0.09375 4.6875 C 0.09375 7.213504 2.161259 9.25 4.6875 9.25 C 7.0056475 9.25 8.9192752 7.517486 9.21875 5.28125 L 17.21875 5.28125 L 14.9375 7.40625 L 17.1875 7.40625 L 20.0625 4.71875 L 20.0625 4.65625 L 17.1875 2 L 14.9375 2 L 17.1875 4.0625 L 9.21875 4.0625 C 8.9192752 1.826023 7.0056475 0.09375 4.6875 0.09375 z";
	private static final String PUT_ICON = "m 15.161617,0.03741328 c -2.464229,0 -4.501754,1.83791802 -4.82032,4.21483102 l -5.5044426,0 -2.4140913,-2.209641 -2.34332005,0 2.06023435,1.918692 1.0301172,0.935755 0.00785,0.0079 -0.015727,0 -0.031454,0.03145 0,0.01573 -3.05889762,2.846583 2.35118352,0 2.4534088,-2.248958 5.4651257,0 c 0.318565,2.376913 2.35609,4.214831 4.820319,4.214831 2.684658,0 4.859637,-2.182339 4.859637,-4.8675 -2.52e-4,-2.68491 -2.174979,-4.85963702 -4.859637,-4.85963702 z M 3.1619311,4.9049143 l 0.00785,-0.0079 -0.039317,0 0.031454,0.0079 z";
	
	private int deviceIndex = -1;
	private int workpieceIndex = -1;
	private int layoutIndex = -1;
	private int offsetsIndex = -1;
	private int pickIndex = -1;
	private int putIndex = -1;
	
	private static final String CONFIGURE_DEVICE = "StackingDeviceMenuView.configureDevice";
	private static final String CONFIGURE_WORKPIECE = "StackingDeviceMenuView.configureWorkpiece";
	private static final String CONFIGURE_OFFSETS = "StackingDeviceMenuView.configureOffsets";
	private static final String VIEW_LAYOUT = "StackingDeviceMenuView.viewLayout";
	private static final String PICK = "StackingDeviceMenuView.pick";
	private static final String PUT = "StackingDeviceMenuView.put";
	
	public void setDeviceInfo(final DeviceInformation deviceInfo)  {
		this.deviceInfo = deviceInfo;
		build();
	}
	
	//FIXME: refactor, to much specific code here
	@Override
	protected void build() {
		// three menu-items will be used: 
		// - configure stacking device
		// - work-piece configuration
		// - layout
		int index = 0;
		String iconPath = null;
		boolean putStep = false;
		if (deviceInfo.hasPickStep())  {
			iconPath = FROM_ICON;
			if (deviceInfo.hasPutStep()) {
				throw new IllegalStateException("Currently not supported.");
			}
		} else {
			iconPath = TO_ICON;
			putStep = true;
			if (!deviceInfo.hasPutStep()) {
				throw new IllegalStateException("No pick or put step found for [" + deviceInfo.getDevice() + "]");
			}
		}
		deviceIndex = index;
		addMenuItem(index, iconPath, Translator.getTranslation(CONFIGURE_DEVICE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().configureDevice();
			}
		});
		index++;
		if (!putStep) {
			workpieceIndex = index;
			addMenuItem(index, WORKPIECE_ICON, Translator.getTranslation(CONFIGURE_WORKPIECE), true, new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					getPresenter().configureWorkPiece();
				}
			});
			index++;
			if (deviceInfo.getDevice() instanceof Conveyor) {
				offsetsIndex = index;
				addMenuItem(index, OFFSETS_ICON, Translator.getTranslation(CONFIGURE_OFFSETS), true, new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent event) {
						getPresenter().configureOffsets();
					}
				});
				index++;
			}
		}
		if (putStep) {
			putIndex = index;
			addMenuItem(index, PUT_ICON, Translator.getTranslation(PUT), true, new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					getPresenter().configurePut();
				}
			});
		} else {
			pickIndex = index;
			addMenuItem(index, PICK_ICON, Translator.getTranslation(PICK), true, new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					getPresenter().configurePick();
				}
			});
		}
		index++;
		layoutIndex = index;
		if (deviceInfo.getDevice() instanceof AbstractConveyor) {
			addMenuItem(index, LAYOUT_CONVEYOR_ICON, Translator.getTranslation(VIEW_LAYOUT), true, new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					getPresenter().showLayout();
				}
			});
		} else if (deviceInfo.getDevice() instanceof BasicStackPlate){
			addMenuItem(index, LAYOUT_ICON, Translator.getTranslation(VIEW_LAYOUT), true, new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					getPresenter().showLayout();
				}
			});
		}
		
	}
	
	public void setConfigureDeviceActive()  {
		if (deviceIndex == -1) {
			throw new IllegalStateException("No device menu item present");
		} else {
			setMenuItemSelected(deviceIndex);
		}
	}
	
	public void setConfigureWorkPieceActive() {
		if (workpieceIndex == -1)  {
			throw new IllegalStateException("No work piece menu item present");
		} else {
			setMenuItemSelected(workpieceIndex);
		}
	}
	
	public void setConfigureOffsetsActive() {
		if (offsetsIndex == -1)  {
			throw new IllegalStateException("No offsets item present");
		} else {
			setMenuItemSelected(offsetsIndex);
		}
	}
	
	public void setViewLayoutActive() {
		if (layoutIndex == -1) {
			throw new IllegalStateException("No layout menu item present");
		} else {
			setMenuItemSelected(layoutIndex);
		}
	}
	
	public void setConfigurePickActive() {
		if (pickIndex == -1) {
			throw new IllegalStateException("No configure smooth pick item present");
		} else {
			setMenuItemSelected(pickIndex);
		}
	}
	
	public void setConfigurePutActive() {
		if (putIndex == -1) {
			throw new IllegalStateException("No configure smooth put item present");
		} else {
			setMenuItemSelected(putIndex);
		}
	}

}
