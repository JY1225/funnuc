package eu.robojob.millassist.ui.configure.device.stacking.stackplate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout.HoleOrientation;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class BasicStackPlateRawWorkPiecePresenter extends AbstractFormPresenter<BasicStackPlateRawWorkPieceView, BasicStackPlateMenuPresenter> {

	private AbstractStackPlateDeviceSettings deviceSettings;
	private PickStep pickStep;
	private WorkPieceDimensions dimensions;
	private WorkPieceOrientation orientation;
	private WorkPiece workPiece;
		
	private static Logger logger = LogManager.getLogger(BasicStackPlateRawWorkPiecePresenter.class.getName());
	private static final String WEIGHT_ZERO = "BasicStackPlateRawWorkPiecePresenter.weightZero";
	private static final String STUD_HEIGHT_NOT_OK = "BasicStackPlateRawWorkPiecePresenter.studHeightNotOK";
	private static final String GRIDPLATE_NOT_OK = "BasicStackPlateRawWorkPiecePresenter.gridplateNotOK";
	private static final String AMOUNT_NOT_OK = "BasicStackPlateRawWorkPiecePresenter.amountNotOK";
	
	public BasicStackPlateRawWorkPiecePresenter(final BasicStackPlateRawWorkPieceView view, final PickStep pickStep, final AbstractStackPlateDeviceSettings deviceSettings) {
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
		view.build();
		recalculate();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void recalcWeight() {
		workPiece.calculateWeight();
		getView().setWeight(workPiece.getMaterial(), workPiece.getWeight());
		pickStep.getProcessFlow().revisitProcessFlowWorkPieces();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		recalculate();
	}
	
	public boolean isWeightOk() {
		return (deviceSettings.getRawWorkPiece().getWeight() > 0);
	}
	
	public boolean isStudHeightOk() {
		return deviceSettings.getStudHeight() >= 0;
	}
	
	public boolean isGridPlateOK() {
		if (getStackPlate().hasGridPlate()) {
			GridPlateLayout layout = ((GridPlateLayout) getStackPlate().getLayout());
			//In case the gridplate is oriented with an angle of 45°, the workpiece has to be like that as well
			if(deviceSettings.getOrientation() == WorkPieceOrientation.TILTED && layout.getHoleOrientation() != HoleOrientation.TILTED) {
				return false;
			} 
			if(deviceSettings.getOrientation() != WorkPieceOrientation.TILTED && layout.getHoleOrientation() == HoleOrientation.TILTED) {
				return false;
			} 
			if(deviceSettings.getOrientation() == WorkPieceOrientation.DEG90) {
				if(dimensions.getWidth() > layout.getHoleLength())
					return false;
				if(dimensions.getLength() > layout.getHoleWidth())
					return false;
			} else {
				if(dimensions.getLength() > layout.getHoleLength())
					return false;
				if(dimensions.getWidth() > layout.getHoleWidth())
					return false;
			} 
			return true;
		} else {
			return true;
		}
	}
	
	private boolean isAmountOk() {
		return (deviceSettings.getAmount() > 0);
	}
	
	public void changedMaterial(final Material material) {
		if (!material.equals(workPiece.getMaterial())) {
			if (material.equals(Material.OTHER)) {
				workPiece.setMaterial(Material.OTHER);
				pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			} else {
				workPiece.setMaterial(material);
				workPiece.calculateWeight();
				pickStep.getProcessFlow().revisitProcessFlowWorkPieces();
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
			clearTeachedOffsets();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		}
	}
	
	public void changedStudHeight(final float studHeight) {
		logger.info("Changed stud height [" + studHeight + "].");
		if (studHeight != 	deviceSettings.getStudHeight()) {
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
			pickStep.getProcessFlow().processProcessFlowEvent(new FinishedAmountChangedEvent(pickStep.getProcessFlow(), 0, amount));
			deviceSettings.setAmount(amount);
			recalculate();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		}
	}
	
	public void recalculate() {
		try {
			pickStep.getProcessFlow().setFinishedAmount(0);
			getStackPlate().getLayout().configureStackingPositions(deviceSettings.getRawWorkPiece(), deviceSettings.getOrientation(), deviceSettings.getLayers());
			getStackPlate().getLayout().initRawWorkPieces(deviceSettings.getRawWorkPiece(), deviceSettings.getAmount());
			if ((deviceSettings.getOrientation() == WorkPieceOrientation.DEG90) || 
					((deviceSettings.getOrientation() == WorkPieceOrientation.TILTED) && (getStackPlate().getBasicLayout().getTiltedR() < getStackPlate().getBasicLayout().getHorizontalR() && (!getStackPlate().getBasicLayout().isRightAligned()))) ||
					((deviceSettings.getOrientation() == WorkPieceOrientation.TILTED) && (getStackPlate().getBasicLayout().getTiltedR() > getStackPlate().getBasicLayout().getHorizontalR() && (getStackPlate().getBasicLayout().isRightAligned())))) {
				pickStep.getProcessFlow().getClampingType().setChanged(true);
			} else {
				pickStep.getProcessFlow().getClampingType().setChanged(false);
			}
			getView().hideNotification();
			if (!isWeightOk()) {
				getView().showNotification(Translator.getTranslation(WEIGHT_ZERO), Type.WARNING);
			} else if (!isStudHeightOk()) {
				getView().showNotification(Translator.getTranslation(STUD_HEIGHT_NOT_OK), Type.WARNING);
			} else if (!isGridPlateOK()) {
				getView().showNotification(Translator.getTranslation(GRIDPLATE_NOT_OK),  Type.WARNING);
			} else if (!isAmountOk()) {
				getView().showNotification(Translator.getTranslation(AMOUNT_NOT_OK), Type.WARNING);
			}
		} catch (IncorrectWorkPieceDataException e) {
			getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
		}
		getStackPlate().notifyLayoutChanged();
	}
	
	public void changedOrientation(final WorkPieceOrientation orientation) {
		logger.info("Set orientation [" + orientation + "].");
		if (!orientation.equals(deviceSettings.getOrientation())) {
			deviceSettings.setOrientation(orientation);
			this.orientation = orientation;
			recalculate();
			getView().refresh();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		}
	}
	
	public void changedWeight(final float weight) {
		workPiece.setWeight(weight);
		getView().setWeight(workPiece.getMaterial(), workPiece.getWeight());
		pickStep.getProcessFlow().revisitProcessFlowWorkPieces();
		recalculate();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
	}

	@Override
	public boolean isConfigured() {
		BasicStackPlate plate = getStackPlate();
		if ((dimensions != null) && (orientation != null) && (plate.getLayout().getStackingPositions() != null)
				&& (plate.getLayout().getStackingPositions().size() > 0) && (workPiece.getWeight() > 0) 
				&& (deviceSettings.getStudHeight() >= 0)
				&& (isAmountOk())) {
			return true;
		}
		return false;
	}
	
	public void setMaxAmount() {
		BasicStackPlate plate = getStackPlate();
		deviceSettings.setAmount(plate.getLayout().getMaxPiecesPossibleAmount());
		plate.loadDeviceSettings(deviceSettings);
		recalculate();
		getView().refresh();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		//plate.notifyLayoutChanged();
	}
	
	private BasicStackPlate getStackPlate() {
		return ((BasicStackPlate) pickStep.getDevice());
	}

	public AbstractStackPlateDeviceSettings getDeviceSettings() {
		return this.deviceSettings;
	}
	
}
