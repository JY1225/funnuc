package eu.robojob.millassist.ui.configure.device.stacking.conveyor.normal;

import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.normal.ConveyorFinishedWorkPieceLayoutView;

public class ConveyorFinishedWorkPieceLayoutPresenter extends eu.robojob.millassist.ui.general.device.stacking.conveyor.normal.ConveyorFinishedWorkPieceLayoutPresenter<ConveyorMenuPresenter> 
	implements ProcessFlowListener {

	public ConveyorFinishedWorkPieceLayoutPresenter(final ConveyorFinishedWorkPieceLayoutView view, final Conveyor conveyor, final PutStep putStep) {
		super(view, conveyor);
		putStep.getProcessFlow().addListener(this);
	}

	
	@Override
	public void dataChanged(final DataChangedEvent e) {
		if (((e.getStep() instanceof PickStep) && ((PickStep) e.getStep()).getDevice() instanceof AbstractCNCMachine)) {
			try {
				getConveyor().getLayout().configureFinishedWorkPieceStackingPositions();
			} catch (IncorrectWorkPieceDataException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }

	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }
	
	@Override public void modeChanged(final ModeChangedEvent e) { }

	@Override public void statusChanged(final StatusChangedEvent e) { }

}
