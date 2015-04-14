package eu.robojob.millassist.ui.configure.device.stacking.stackplate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;

public class BasicStackPlateRawWorkPiecePresenter extends AbstractFormPresenter<BasicStackPlateRawWorkPieceView, BasicStackPlateMenuPresenter> {

	private AbstractStackPlateDeviceSettings deviceSettings;
	private PickStep pickStep;
	private float orientation;
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
		orientation = deviceSettings.getOrientation();
		deviceSettings.setOrientation(orientation);
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
			GridPlate gridplate = ((GridPlateLayout) getStackPlate().getLayout()).getGridPlate();
			if (workPiece.getShape().equals(WorkPieceShape.CYLINDRICAL)) {
				if(workPiece.getDimensions().getDimension(Dimensions.DIAMETER) > gridplate.getHoleLength())
					return false;
				if(workPiece.getDimensions().getDimension(Dimensions.DIAMETER) > gridplate.getHoleWidth())
					return false;
			} else {
				if(workPiece.getDimensions().getDimension(Dimensions.LENGTH) > gridplate.getHoleLength())
					return false;
				if(workPiece.getDimensions().getDimension(Dimensions.WIDTH) > gridplate.getHoleWidth())
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
		if (workPiece.getShape().equals(WorkPieceShape.CYLINDRICAL)) {
			changedDiameter(width);
		} else {
			if (width != workPiece.getDimensions().getDimension(Dimensions.WIDTH)) {
				logger.info("Set width [" + width + "].");
				workPiece.getDimensions().setDimension(Dimensions.WIDTH, width);
				recalculate();
				clearTeachedOffsets();
				pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
			}
		}
	}
	
	private void changedDiameter(final float diameter) {
		if (diameter != workPiece.getDimensions().getDimension(Dimensions.DIAMETER)) {
			logger.info("Set diameter [" + diameter + "].");
			workPiece.getDimensions().setDimension(Dimensions.DIAMETER, diameter);
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
			recalculate();
			clearTeachedOffsets();
			
			setMaxAmount();
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
		if (length != workPiece.getDimensions().getDimension(Dimensions.LENGTH)) {
			workPiece.getDimensions().setDimension(Dimensions.LENGTH, length);
			recalculate();
			clearTeachedOffsets();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		}
	}
	
	public void changedHeight(final float height) {
		logger.info("Set height [" + height + "].");
		if (height != workPiece.getDimensions().getDimension(Dimensions.HEIGHT)) {
			workPiece.getDimensions().setDimension(Dimensions.HEIGHT, height);
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
			getStackPlate().getLayout().configureStackingPositions(deviceSettings.getRawWorkPiece(), deviceSettings.getFinishedWorkPiece(), deviceSettings.getOrientation(), deviceSettings.getLayers());
			getStackPlate().getLayout().initRawWorkPieces(deviceSettings.getRawWorkPiece(), deviceSettings.getAmount());
			if ((deviceSettings.getOrientation() == 90) || 
					((deviceSettings.getOrientation() == 45) && (getStackPlate().getBasicLayout().getTiltedR() < getStackPlate().getBasicLayout().getHorizontalR() && (!getStackPlate().getBasicLayout().isRightAligned()))) ||
					((deviceSettings.getOrientation() == 45) && (getStackPlate().getBasicLayout().getTiltedR() > getStackPlate().getBasicLayout().getHorizontalR() && (getStackPlate().getBasicLayout().isRightAligned())))) {
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
	
	public void changedOrientation(final float orientation) {
		logger.info("Set orientation [" + orientation + "].");
		if (orientation != deviceSettings.getOrientation()) {
			deviceSettings.setOrientation(orientation);
			this.orientation = orientation;
			recalculate();
			getView().refresh();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		}
	}
	
	public void changedShape(final WorkPieceShape shape) {
		if (!shape.equals(deviceSettings.getRawWorkPiece().getShape())) {
			logger.info("Set shape [" + shape + "].");		
			//TODO - set new workPiece for raw and finished - revisit entire flow 
			changeWorkPiece(shape);
			if (deviceSettings.getFinishedWorkPiece() != null) {
				deviceSettings.getFinishedWorkPiece().transformPiece(shape);
			}
			recalculate();
			getView().refresh();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		}
	}
	
	private void changeWorkPiece(final WorkPieceShape shape) {
		this.workPiece.transformPiece(shape);
		deviceSettings.getRawWorkPiece().transformPiece(shape);
		this.pickStep.getRobotSettings().getWorkPiece().transformPiece(shape);
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
		if ((workPiece.getDimensions() != null) && (plate.getLayout().getRawStackingPositions() != null)
				&& (plate.getLayout().getRawStackingPositions().size() > 0) && (workPiece.getWeight() > 0) 
				&& (deviceSettings.getStudHeight() >= 0)
				&& (isAmountOk()) 
				&& isGridPlateOK()) {
			return true;
		}
		return false;
	}
	
	public void setMaxAmount() {
		BasicStackPlate plate = getStackPlate();
		plate.loadDeviceSettings(deviceSettings);
		deviceSettings.setAmount(plate.getLayout().getMaxPiecesPossibleAmount());
		recalculate();
		getView().refresh();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		plate.notifyLayoutChanged();
	}
	
	private BasicStackPlate getStackPlate() {
		return ((BasicStackPlate) pickStep.getDevice());
	}

	public AbstractStackPlateDeviceSettings getDeviceSettings() {
		return this.deviceSettings;
	}
	
	public boolean hasGridPlate() {
		return getStackPlate().hasGridPlate();
	}	
}
