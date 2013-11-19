package eu.robojob.millassist.ui.admin.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.bin.OutputBin;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class OutputBinConfigurePresenter extends AbstractFormPresenter<OutputBinConfigureView, DeviceMenuPresenter> {

	private DeviceManager deviceManager;
	private OutputBin outputBin;
	
	public OutputBinConfigurePresenter(final OutputBinConfigureView view, final DeviceManager deviceManager) {
		super(view);
		getView().build();
		this.deviceManager = deviceManager;
		for (AbstractStackingDevice device : deviceManager.getStackingToDevices()) {
			if (device instanceof OutputBin) {
				this.outputBin = (OutputBin) device;
				getView().setOutputBin(outputBin);
				break;
			}
		}
		getView().refresh();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	public void updateUserFrames() {
		List<String> userFrameNames = new ArrayList<String>();
		for (UserFrame uf : deviceManager.getAllUserFrames()) {
			userFrameNames.add(uf.getName());
		}
		getView().setUserFrames(userFrameNames);
	}
	
	// not used
	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void saveData(final String name, final String userFrame, final float x, final float y, final float z, final float w,
			final float p, final float r, final float smoothToX, final float smoothToY, final float smoothToZ) {
		deviceManager.updateOutputBinData(outputBin, name, userFrame, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ);
		getView().refresh();
	}

}
