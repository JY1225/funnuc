package eu.robojob.millassist.ui.configure.device.stacking;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.external.device.stacking.conveyor.Conveyor;
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
	private int deviceIndex = -1;
	private int workpieceIndex = -1;
	private int layoutIndex = -1;
	private int offsetsIndex = -1;
	
	private static final String CONFIGURE_DEVICE = "StackingDeviceMenuView.configureDevice";
	private static final String CONFIGURE_WORKPIECE = "StackingDeviceMenuView.configureWorkpiece";
	private static final String CONFIGURE_OFFSETS = "StackingDeviceMenuView.configureOffsets";
	private static final String VIEW_LAYOUT = "StackingDeviceMenuView.viewLayout";
	
	public void setDeviceInfo(final DeviceInformation deviceInfo)  {
		this.deviceInfo = deviceInfo;
		build();
	}
	
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
		getMenuItem(index).setDisable(true);
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
		layoutIndex = index;
		if (deviceInfo.getDevice() instanceof Conveyor) {
			addMenuItem(index, LAYOUT_CONVEYOR_ICON, Translator.getTranslation(VIEW_LAYOUT), true, new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					getPresenter().showLayout();
				}
			});
		} else {
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

}
