package eu.robojob.millassist.ui.admin.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class ReversalUnitConfigurePresenter extends AbstractFormPresenter<ReversalUnitConfigureView, DeviceMenuPresenter> {

	private DeviceManager deviceManager;
	private ReversalUnit reversalUnit;
	
	public ReversalUnitConfigurePresenter(final ReversalUnitConfigureView view, final DeviceManager deviceManager) {
		super(view);
		getView().build();
		this.deviceManager = deviceManager;
		for (AbstractProcessingDevice device : deviceManager.getPostProcessingDevices()) {
			if (device instanceof ReversalUnit) {
				this.reversalUnit = (ReversalUnit) device;
				getView().setReversalUnit(reversalUnit);
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
			final float p, final float r, final float smoothToX, final float smoothToY, final float smoothToZ, 
			final float smoothFromX, final float smoothFromY, final float smoothFromZ,
			final float stationLength, final float stationFixtureWidth, final float stationHeight,	
			final Map<ApproachType, Boolean> allowedApproaches, final float addedXValue) {
		deviceManager.updateReversalUnitData(reversalUnit, name, userFrame, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, 
				smoothFromX, smoothFromY, smoothFromZ, stationLength, stationFixtureWidth, stationHeight, allowedApproaches, addedXValue);
		getView().refresh();
	}

}
