package eu.robojob.millassist.ui.configure.device.processing.cnc;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.PickAfterWaitStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.DimensionsChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.RoundDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;

public class CNCMillingMachineWorkPiecePresenter extends AbstractFormPresenter<CNCMillingMachineWorkPieceView, CNCMillingMachineMenuPresenter> implements ProcessFlowListener  {

	private PickStep pickStep;
	private static final String DIMENSIONS_DO_NOT_MATCH = "CNCMillingMachineWorkPiecePresenter.dimensionsDoNotMatch";
	private static final String WEIGHTS_DO_NOT_MATCH = "CNCMillingMachineWorkPiecePresenter.weightsDoNotMatch";
	private static final String INCORRECT_DATA = "CNCMillingMachineWorkPiecePresenter.incorrectData";
	
	public CNCMillingMachineWorkPiecePresenter(final CNCMillingMachineWorkPieceView view, final PickStep pickStep, final DeviceSettings deviceSettings) {
		super(view);
		this.pickStep = pickStep;
		pickStep.getProcessFlow().addListener(this);
		view.setPickStep(pickStep);
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		if (pickStep.getRobotSettings().getWorkPiece().getDimensions() != null) {
			IWorkPieceDimensions myDimensions = pickStep.getRobotSettings().getWorkPiece().getDimensions();
			IWorkPieceDimensions prevDimensions = getPreviousPickDimensions();
			float weight = pickStep.getRobotSettings().getWorkPiece().getWeight();
			float prevWeight = getPreviousWorkPiece().getWeight();
			if (myDimensions.isValidDimension() && myDimensions.compareTo(prevDimensions) <= 0 
					&&	(pickStep.getRobotSettings().getWorkPiece().getWeight() > 0) &&
					((weight <= prevWeight) || ((weight > prevWeight) && (Math.abs(weight - prevWeight) < 0.01)))) {
				return true;
			}
		}
		return false;
	}
	
	public void recalculate() {
		if (pickStep.getRobotSettings().getWorkPiece().getDimensions() != null) {
			IWorkPieceDimensions myDimensions = pickStep.getRobotSettings().getWorkPiece().getDimensions();
			float weight = pickStep.getRobotSettings().getWorkPiece().getWeight();
			float prevWeight = getPreviousWorkPiece().getWeight();
			IWorkPieceDimensions prevDimensions = getPreviousPickDimensions();
			if (!myDimensions.isValidDimension() || (weight <= 0)) {
				getView().showNotification(Translator.getTranslation(INCORRECT_DATA), Type.WARNING);
			//FIXME - add check using the coordination changes
			} else if (myDimensions.compareTo(prevDimensions) > 0) {
				getView().showNotification(Translator.getTranslation(DIMENSIONS_DO_NOT_MATCH), Type.WARNING);
			} else if ((weight > prevWeight) && (Math.abs(weight - prevWeight) > 0.01)) {
				getView().showNotification(Translator.getTranslation(WEIGHTS_DO_NOT_MATCH), Type.WARNING);
			} else {
				getView().hideNotification();
			}
		} else {
			getView().showNotification(Translator.getTranslation(INCORRECT_DATA), Type.WARNING);
		}
	}
	
	public void changedDiameterWidth(final float value) {
		if (pickStep.getRobotSettings().getWorkPiece().getShape().equals(WorkPieceShape.CYLINDRICAL)) {
			changedDiameter(value);
		} else {
			changedWidth(value);
		}
		pickStep.getProcessFlow().processProcessFlowEvent(new DimensionsChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	private void changedWidth(final float width) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setDimension(Dimensions.WIDTH, width);
		pickStep.setRelativeTeachedOffset(null);
		recalculate();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	private void changedDiameter(final float diameter) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setDimension(Dimensions.DIAMETER, diameter);
		pickStep.setRelativeTeachedOffset(null);
		recalculate();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void resetWidth() {
		IWorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		if (prevPickDimensions instanceof RoundDimensions) {
			resetDiameter();
		} else {
			changedWidth(prevPickDimensions.getDimension(Dimensions.WIDTH));
			getView().refresh();
		}
		pickStep.getProcessFlow().processProcessFlowEvent(new DimensionsChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	private void resetDiameter() {
		IWorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		changedDiameter(prevPickDimensions.getDimension(Dimensions.DIAMETER));
		getView().refresh();
	}
	
	public void changedLength(final float length) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setDimension(Dimensions.LENGTH, length);
		pickStep.setRelativeTeachedOffset(null);
		recalculate();
		pickStep.getProcessFlow().processProcessFlowEvent(new DimensionsChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void resetLength() {
		IWorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		changedLength(prevPickDimensions.getDimension(Dimensions.LENGTH));
		pickStep.getProcessFlow().processProcessFlowEvent(new DimensionsChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		getView().refresh();
	}
	
	public void changedHeight(final float height) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setDimension(Dimensions.HEIGHT, height);
		//pickStep.setRelativeTeachedOffset(null);
		recalculate();
		pickStep.getProcessFlow().processProcessFlowEvent(new DimensionsChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
	}
	
	public void resetHeight() {
		IWorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		changedHeight(prevPickDimensions.getDimension(Dimensions.HEIGHT));
		getView().refresh();
	}
	
	public void changedWeight(final float weight) {
		pickStep.getRobotSettings().getWorkPiece().setWeight(weight);
		getView().refresh();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
	}
	
	public void resetWeight() {
		pickStep.getRobotSettings().getWorkPiece().setWeight(getPreviousWorkPiece().getWeight());
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		getView().refresh();
	}
	
	public void calcWeight() {
		pickStep.getRobotSettings().getWorkPiece().calculateWeight();
		getView().refresh();
	}
	
	private IWorkPieceDimensions getPreviousPickDimensions() {
		int pickIndex = pickStep.getProcessFlow().getStepIndex(pickStep);
		for (int i = pickIndex - 1; i >= 0; i--) {
			AbstractProcessStep step = pickStep.getProcessFlow().getStep(i);
			if ((step instanceof PickStep) && !(step instanceof PickAfterWaitStep)) {
				return ((PickStep) step).getRobotSettings().getWorkPiece().getDimensions();
			}
		}
		throw new IllegalArgumentException("Couldn't find previous pick step");
	}

	private WorkPiece getPreviousWorkPiece() {
		int pickIndex = pickStep.getProcessFlow().getStepIndex(pickStep);
		for (int i = pickIndex - 1; i >= 0; i--) {
			AbstractProcessStep step = pickStep.getProcessFlow().getStep(i);
			if ((step instanceof PickStep) && !(step instanceof PickAfterWaitStep)) {
				if (!(((PickStep) step).getDevice() instanceof ReversalUnit)) {
					return ((PickStep) step).getRobotSettings().getWorkPiece();
				}		
			}
		}
		throw new IllegalArgumentException("Couldn't find previous work piece");
	}

	@Override
	public void modeChanged(final ModeChangedEvent e) { }

	@Override
	public void statusChanged(final StatusChangedEvent e) { }

	@Override
	public void dataChanged(final DataChangedEvent e) {
		if (e.getStep() instanceof PickStep) {
			PickStep pickStep = (PickStep) e.getStep();
			if (pickStep.getDevice() instanceof AbstractStackingDevice) {
				IWorkPieceDimensions currentDimensions = this.pickStep.getRobotSettings().getWorkPiece().getDimensions();
				if (e.isReTeachingNeeded()) {
					IWorkPieceDimensions newDimensions = pickStep.getRobotSettings().getWorkPiece().getDimensions();
					boolean reteach = false;
					if (currentDimensions.compareTo(newDimensions) != 0) {
						reteach = true;
						this.pickStep.setRelativeTeachedOffset(null);
					}
					this.pickStep.getProcessFlow().revisitProcessFlowWorkPieces();
					this.pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(this.pickStep.getProcessFlow(), this.pickStep, reteach));
				}
				this.pickStep.getRobotSettings().getWorkPiece().setMaterial(pickStep.getRobotSettings().getWorkPiece().getMaterial());
				this.pickStep.getRobotSettings().getWorkPiece().setWeight(pickStep.getRobotSettings().getWorkPiece().getWeight());
				this.pickStep.getRobotSettings().getWorkPiece().setShape(pickStep.getRobotSettings().getWorkPiece().getShape());
			}
		}
	}

	@Override
	public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }

	@Override
	public void exceptionOccured(final ExceptionOccuredEvent e) { }

	@Override
	public void unregister() {
		pickStep.getProcessFlow().removeListener(this);
	}
	
	@Override public void dimensionChanged(DimensionsChangedEvent e) {	
	}
}
