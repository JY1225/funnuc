package eu.robojob.millassist.ui.configure.device.processing.cnc;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.stacking.BasicStackPlate;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.PickAfterWaitStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class CNCMillingMachineWorkPiecePresenter extends AbstractFormPresenter<CNCMillingMachineWorkPieceView, CNCMillingMachineMenuPresenter> implements ProcessFlowListener  {

	private PickStep pickStep;
	private static final String DIMENSIONS_DO_NOT_MATCH = "CNCMillingMachineWorkPiecePresenter.dimensionsDoNotMatch";
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
			WorkPieceDimensions myDimensions = pickStep.getRobotSettings().getWorkPiece().getDimensions();
			WorkPieceDimensions prevDimensions = getPreviousPickDimensions();
			if ((myDimensions.getWidth() > 0) && (myDimensions.getLength() > 0) && (myDimensions.getHeight() > 0) && (myDimensions.getWidth() <= prevDimensions.getWidth()) && (myDimensions.getLength() <= prevDimensions.getLength()) 
					&& (myDimensions.getHeight() <= prevDimensions.getHeight())) {
				return true;
			}
		}
		return false;
	}
	
	public void recalculate() {
		if (pickStep.getRobotSettings().getWorkPiece().getDimensions() != null) {
			WorkPieceDimensions myDimensions = pickStep.getRobotSettings().getWorkPiece().getDimensions();
			WorkPieceDimensions prevDimensions = getPreviousPickDimensions();
			if ((myDimensions.getWidth() <= 0) || (myDimensions.getLength() <= 0) || (myDimensions.getHeight() <= 0)) {
				getView().showNotification(Translator.getTranslation(INCORRECT_DATA));
			} else if ((myDimensions.getWidth() > prevDimensions.getWidth()) || (myDimensions.getLength() > prevDimensions.getLength())
					 || (myDimensions.getHeight() > prevDimensions.getHeight())) {
				getView().showNotification(Translator.getTranslation(DIMENSIONS_DO_NOT_MATCH));
			} else {
				getView().hideNotification();
			}
		} else {
			getView().showNotification(Translator.getTranslation(INCORRECT_DATA));
		}
	}
	
	public void changedWidth(final float width) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setWidth(width);
		pickStep.setRelativeTeachedOffset(null);
		recalculate();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void resetWidth() {
		WorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		changedWidth(prevPickDimensions.getWidth());
		getView().refresh();
	}
	
	public void changedLength(final float length) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setLength(length);
		pickStep.setRelativeTeachedOffset(null);
		recalculate();
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void resetLength() {
		WorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		changedLength(prevPickDimensions.getLength());
		getView().refresh();
	}
	
	public void changedHeight(final float height) {
		pickStep.getRobotSettings().getWorkPiece().getDimensions().setHeight(height);
		pickStep.setRelativeTeachedOffset(null);
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
	}
	
	public void resetHeight() {
		WorkPieceDimensions prevPickDimensions = getPreviousPickDimensions();
		changedHeight(prevPickDimensions.getHeight());
		getView().refresh();
	}
	
	private WorkPieceDimensions getPreviousPickDimensions() {
		int pickIndex = pickStep.getProcessFlow().getStepIndex(pickStep);
		for (int i = pickIndex - 1; i >= 0; i--) {
			AbstractProcessStep step = pickStep.getProcessFlow().getStep(i);
			if ((step instanceof PickStep) && !(step instanceof PickAfterWaitStep)) {
				return ((PickStep) step).getRobotSettings().getWorkPiece().getDimensions();
			}
		}
		throw new IllegalArgumentException("Couldn't find previous pick step");
	}


	@Override
	public void modeChanged(final ModeChangedEvent e) { }

	@Override
	public void statusChanged(final StatusChangedEvent e) { }

	@Override
	public void dataChanged(final DataChangedEvent e) {
		if (e.getStep() instanceof PickStep) {
			PickStep pickStep = (PickStep) e.getStep();
			if (pickStep.getDevice() instanceof BasicStackPlate) {
				this.pickStep.getRobotSettings().getWorkPiece().getDimensions().setLength(pickStep.getRobotSettings().getWorkPiece().getDimensions().getLength());
				this.pickStep.getRobotSettings().getWorkPiece().getDimensions().setWidth(pickStep.getRobotSettings().getWorkPiece().getDimensions().getWidth());
				this.pickStep.getRobotSettings().getWorkPiece().getDimensions().setHeight(pickStep.getRobotSettings().getWorkPiece().getDimensions().getHeight());
				this.pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(this.pickStep.getProcessFlow(), this.pickStep, true));
			}
		}
	}

	@Override
	public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }

	@Override
	public void exceptionOccured(final ExceptionOccuredEvent e) { }
}
