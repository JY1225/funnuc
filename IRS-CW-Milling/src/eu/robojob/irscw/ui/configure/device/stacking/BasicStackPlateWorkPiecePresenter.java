package eu.robojob.irscw.ui.configure.device.stacking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateSettings;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.event.DataChangedEvent;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class BasicStackPlateWorkPiecePresenter extends AbstractFormPresenter<BasicStackPlateWorkPieceView, BasicStackPlateMenuPresenter> {

	private BasicStackPlateSettings deviceSettings;
	private PickStep pickStep;
	private WorkPieceDimensions dimensions;
	private WorkPieceOrientation orientation;
		
	private static Logger logger = LogManager.getLogger(BasicStackPlateWorkPiecePresenter.class.getName());
	
	public BasicStackPlateWorkPiecePresenter(final BasicStackPlateWorkPieceView view, final PickStep pickStep, final BasicStackPlateSettings deviceSettings) {
		super(view);
		this.pickStep = pickStep;	
		this.deviceSettings = deviceSettings;
		this.dimensions = pickStep.getRobotSettings().getWorkPiece().getDimensions();
		if (dimensions == null) {
			dimensions = new WorkPieceDimensions();
			pickStep.getRobotSettings().getWorkPiece().setDimensions(dimensions);
		}
		deviceSettings.setDimensions(dimensions);		
		orientation = deviceSettings.getOrientation();
		if (orientation == null) {
			orientation = WorkPieceOrientation.HORIZONTAL;
			deviceSettings.setOrientation(orientation);
		}
		view.setPickStep(pickStep);
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void changedWidth(final float width) {
		logger.info("Set width [" + width + "].");
		dimensions.setWidth(width);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		pickStep.setRelativeTeachedOffset(null);
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void changedLength(final float length) {
		logger.info("Set length [" + length + "].");
		dimensions.setLength(length);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		pickStep.setRelativeTeachedOffset(null);
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void changedHeight(final float height) {
		logger.info("Set height [" + height + "].");
		dimensions.setHeight(height);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		pickStep.setRelativeTeachedOffset(null);
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void changedAmount(final int amount) {
		logger.info("Set amount [" + amount + "].");
		deviceSettings.setAmount(amount);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		pickStep.getProcessFlow().setTotalAmount(amount);
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void changedOrientation(final WorkPieceOrientation orientation) {
		logger.info("Set orientation [" + orientation + "].");
		deviceSettings.setOrientation(orientation);
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		getView().refresh();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
	}

	@Override
	public boolean isConfigured() {
		BasicStackPlate plate = ((BasicStackPlate) pickStep.getDevice());
		if ((dimensions != null) && (orientation != null) && (plate.getLayout().getStackingPositions() != null)
				&& (plate.getLayout().getStackingPositions().size() > 0) && (plate.getLayout().getStackingPositions().get(0).getWorkPiece() != null)
			) {
			return true;
		}
		return false;
	}
	
	public void setMaxAmount() {
		BasicStackPlate plate = ((BasicStackPlate) pickStep.getDevice());
		deviceSettings.setAmount(plate.getLayout().getStackingPositions().size());
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		pickStep.getProcessFlow().setTotalAmount(plate.getLayout().getStackingPositions().size());
		getView().refresh();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}

}
