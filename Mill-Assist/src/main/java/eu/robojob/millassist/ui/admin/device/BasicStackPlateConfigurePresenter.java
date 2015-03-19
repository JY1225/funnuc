package eu.robojob.millassist.ui.admin.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class BasicStackPlateConfigurePresenter extends AbstractFormPresenter<BasicStackPlateConfigureView, DeviceMenuPresenter> {
	
	private DeviceManager deviceManager;
	private BasicStackPlate basicStackPlate;
	
	public BasicStackPlateConfigurePresenter(final BasicStackPlateConfigureView view, final DeviceManager deviceManager) {
		super(view);
		this.deviceManager = deviceManager;
		for (AbstractDevice device : deviceManager.getStackingFromDevices()) {
			if (device instanceof BasicStackPlate) {
				this.basicStackPlate = (BasicStackPlate) device;
				getView().setBasicStackPlate(basicStackPlate);
				break;
			}
		}
		getView().build();
		getView().refresh();
	}
	
	public void updateUserFrames() {
		List<String> userFrameNames = new ArrayList<String>();
		for (UserFrame uf : deviceManager.getAllUserFrames()) {
			userFrameNames.add(uf.getName());
		}
		getView().setUserFrames(userFrameNames);
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	public void saveData(final String name, final String userFrameName, final int horizontalHoleAmount, final int verticalHoleAmount, 
			final float holeDiameter, final float studDiameter, final float horizontalHoleDistance, final float horizontalPadding, 
				final float verticalPaddingTop, final float verticalPaddingBottom, final float interferenceDistance, final float overflowPercentage,
					final float horizontalR, final float tiltedR, final float maxOverflow, final float maxUnderflow, final float minOverlap, final float studHeight,
						final float smoothToX, final float smoothToY, final float smoothToZ, final float smoothFromX, final float smoothFromY, 
						final float smoothFromZ) {
		deviceManager.updateBasicStackPlate(basicStackPlate, name, userFrameName, horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, 
				horizontalHoleDistance, horizontalPadding, verticalPaddingTop, verticalPaddingBottom, interferenceDistance, overflowPercentage, horizontalR, tiltedR,
					maxOverflow, maxUnderflow, minOverlap, studHeight, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ);
	}
}
