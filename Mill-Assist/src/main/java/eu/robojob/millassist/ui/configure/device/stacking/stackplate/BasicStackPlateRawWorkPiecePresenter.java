package eu.robojob.millassist.ui.configure.device.stacking.stackplate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlateSettings;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class BasicStackPlateRawWorkPiecePresenter extends AbstractFormPresenter<BasicStackPlateRawWorkPieceView, BasicStackPlateMenuPresenter> {

	private BasicStackPlateSettings deviceSettings;
	private PickStep pickStep;
	private WorkPieceDimensions dimensions;
	private WorkPieceOrientation orientation;
	private WorkPiece workPiece;
		
	private static Logger logger = LogManager.getLogger(BasicStackPlateRawWorkPiecePresenter.class.getName());
	private static final String WEIGHT_ZERO = "BasicStackPlateRawWorkPiecePresenter.weightZero";
	private static final String STUD_HEIGHT_NOT_OK = "BasicStackPlateRawWorkPiecePresenter.studHeightNotOK";
	
	public BasicStackPlateRawWorkPiecePresenter(final BasicStackPlateRawWorkPieceView view, final PickStep pickStep, final BasicStackPlateSettings deviceSettings) {
		super(view);
		this.pickStep = pickStep;	
		this.deviceSettings = deviceSettings;
		this.workPiece = pickStep.getRobotSettings().getWorkPiece();
		deviceSettings.setRawWorkPiece(workPiece);	
		this.dimensions = workPiece.getDimensions();
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
	
	public void recalcWeight() {
		workPiece.calculateWeight();
		getView().setWeight(workPiece.getMaterial(), workPiece.getWeight());
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		recalculate();
	}
	
	public boolean isWeightOk() {
		return (deviceSettings.getRawWorkPiece().getWeight() > 0);
	}
	
	public boolean isStudHeightOk() {
		return (deviceSettings.getStudHeight() >= 0);
	}
	
	public void changedMaterial(final Material material) {
		if (!material.equals(workPiece.getMaterial())) {
			if (material.equals(Material.OTHER)) {
				workPiece.setMaterial(Material.OTHER);
				pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			} else {
				workPiece.setMaterial(material);
				workPiece.calculateWeight();
				pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			}
			getView().setWeight(workPiece.getMaterial(), workPiece.getWeight());
			recalculate();
		}
	}
	
	public void changedWidth(final float width) {
		logger.info("Set width [" + width + "].");
		if (width != dimensions.getWidth()) {
			dimensions.setWidth(width);	
			recalculate();
			clearTeachedOffsets();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		}
	}
	
	private void clearTeachedOffsets() {
		for (AbstractProcessStep step : pickStep.getProcessFlow().getProcessSteps()) {
			if (step instanceof AbstractTransportStep) {
				((AbstractTransportStep) step).setRelativeTeachedOffset(null);
			}
		}
	}
	
	public void changedLength(final float length) {
		logger.info("Set length [" + length + "].");
		if (length != dimensions.getLength()) {
			dimensions.setLength(length);
			recalculate();
			clearTeachedOffsets();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		}
	}
	
	public void changedHeight(final float height) {
		logger.info("Set height [" + height + "].");
		if (height != dimensions.getHeight()) {
			dimensions.setHeight(height);
			recalculate();
			//clearTeachedOffsets();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		}
	}
	
	public void changedStudHeight(final float studHeight) {
		logger.info("Changed stud height [" + studHeight + "].");
		if (studHeight != deviceSettings.getStudHeight()) {
			deviceSettings.setStudHeight(studHeight);
			((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		}
	}
	
	public void changedLayers(final int layers) {
		logger.info("Set layers [" + layers + "].");
		if (layers != deviceSettings.getLayers()) {
			deviceSettings.setLayers(layers);
			recalculate();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		}
	}
	
	public void changedAmount(final int amount) {
		logger.info("Set amount [" + amount + "].");
		if (amount != deviceSettings.getAmount()) {
			deviceSettings.setAmount(amount);
			recalculate();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		}
	}
	
	public void recalculate() {
		try {
			((BasicStackPlate) pickStep.getDevice()).getLayout().configureStackingPositions(deviceSettings.getRawWorkPiece(), deviceSettings.getOrientation(), deviceSettings.getLayers());
			((BasicStackPlate) pickStep.getDevice()).getLayout().placeRawWorkPieces(deviceSettings.getRawWorkPiece(), deviceSettings.getAmount());
			pickStep.getProcessFlow().getClampingType().setChanged((deviceSettings.getOrientation() == WorkPieceOrientation.DEG90));
			// FIXME: in principe ook hier, als de hoek voor 45° kleiner is dan de hoek voor 90°!!
			getView().hideNotification();
			if (!isWeightOk()) {
				getView().showNotification(Translator.getTranslation(WEIGHT_ZERO));
			} else if (!isStudHeightOk()) {
				getView().showNotification(Translator.getTranslation(STUD_HEIGHT_NOT_OK));
			}
		} catch (IncorrectWorkPieceDataException e) {
			getView().showNotification(e.getLocalizedMessage());
		}
		((BasicStackPlate) pickStep.getDevice()).notifyLayoutChanged();
	}
	
	public void changedOrientation(final WorkPieceOrientation orientation) {
		logger.info("Set orientation [" + orientation + "].");
		if (!orientation.equals(deviceSettings.getOrientation())) {
			deviceSettings.setOrientation(orientation);
			this.orientation = orientation;
			recalculate();
			getView().refresh();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			//((BasicStackPlate) pickStep.getDevice()).notifyLayoutChanged();
		}
	}
	
	public void changedWeight(final float weight) {
		workPiece.setWeight(weight);
		getView().setWeight(workPiece.getMaterial(), workPiece.getWeight());
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
	}

	@Override
	public boolean isConfigured() {
		BasicStackPlate plate = ((BasicStackPlate) pickStep.getDevice());
		if ((dimensions != null) && (orientation != null) && (plate.getLayout().getStackingPositions() != null)
				&& (plate.getLayout().getStackingPositions().size() > 0) && (workPiece.getWeight() > 0) && (deviceSettings.getStudHeight() >= 0)
				) {
			return true;
		}
		return false;
	}
	
	public void setMaxAmount() {
		BasicStackPlate plate = ((BasicStackPlate) pickStep.getDevice());
		deviceSettings.setAmount(plate.getLayout().getMaxRawWorkPiecesAmount());
		((BasicStackPlate) pickStep.getDevice()).loadDeviceSettings(deviceSettings);
		recalculate();
		getView().refresh();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		//((BasicStackPlate) pickStep.getDevice()).notifyLayoutChanged();
	}

}
