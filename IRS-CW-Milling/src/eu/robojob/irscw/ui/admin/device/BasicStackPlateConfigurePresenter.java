package eu.robojob.irscw.ui.admin.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.positioning.UserFrame;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

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
					final float horizontalR, final float tiltedR) {
		deviceManager.updateBasicStackPlate(basicStackPlate, name, userFrameName, horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, 
				horizontalHoleDistance, horizontalPadding, verticalPaddingTop, verticalPaddingBottom, interferenceDistance, overflowPercentage, horizontalR, tiltedR);
	}
}
