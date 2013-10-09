package eu.robojob.millassist.ui.admin.device;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class PrageDeviceConfigurePresenter extends AbstractFormPresenter<PrageDeviceConfigureView, DeviceMenuPresenter> {

	private DeviceManager deviceManager;
	private PrageDevice prageDevice;
	
	public PrageDeviceConfigurePresenter(final PrageDeviceConfigureView view, final DeviceManager deviceManager) {
		super(view);
		getView().build();
		this.deviceManager = deviceManager;
		for (AbstractProcessingDevice device : deviceManager.getPreProcessingDevices()) {
			if (device instanceof PrageDevice) {
				this.prageDevice = (PrageDevice) device;
				getView().setPrageDevice(prageDevice);
				break;
			}
		}
		getView().refresh();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	public void updateUserFrames() {
		Set<String> userFrameNames = new HashSet<String>();
		for (UserFrame frame : deviceManager.getAllUserFrames()) {
			userFrameNames.add(frame.getName());
		}
		getView().setUserFrameNames(userFrameNames);
	}
	
	public void saveData(final String name, final float relPosX, final float relPosY, 
			final float relPosZ, final float relPosR, final float smoothToX, final float smoothToY, final float smoothToZ,
			final float smoothFromX, final float smoothFromY, final float smoothFromZ, final int widthOffsetR) {
		deviceManager.updatePrageDeviceData(prageDevice, name, relPosX, relPosY, relPosZ, relPosR, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ, widthOffsetR);
		getView().refresh();
	}
}
