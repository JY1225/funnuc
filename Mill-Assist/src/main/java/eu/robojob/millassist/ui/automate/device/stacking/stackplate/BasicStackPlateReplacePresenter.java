package eu.robojob.millassist.ui.automate.device.stacking.stackplate;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.workpiece.WorkPiece;

public class BasicStackPlateReplacePresenter extends AbstractFormPresenter<BasicStackPlateReplaceView, BasicStackPlateMenuPresenter> implements ProcessFlowListener {

	private BasicStackPlate stackPlate;
	private ProcessFlow processFlow;
	
	public BasicStackPlateReplacePresenter(final BasicStackPlateReplaceView view, final BasicStackPlate basicStackPlate, 
			final ProcessFlow processFlow) {
		super(view);
		this.stackPlate = basicStackPlate;
		this.processFlow = processFlow;
		processFlow.addListener(this);
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void replaceRawByFinished(final int amount) {
		if(amount > 0) {
			try {
				if(amount > getNbRawWorkPiecesToReplace()) {
					throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
				} else {
					processFlow.setFinishedAmount(amount + processFlow.getFinishedAmount());
					stackPlate.placeFinishedWorkPieces(amount);
				}
			} catch (IncorrectWorkPieceDataException e) {
				getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
			}
		} 
	}

	@Override
	public void modeChanged(final ModeChangedEvent e) {
		if (!e.getMode().equals(Mode.AUTO)) {
			getView().setButtonEnabled(true);
		} else
			getView().setButtonEnabled(false);
	}
	
	@Override public void statusChanged(final StatusChangedEvent e) { }

	@Override public void dataChanged(final DataChangedEvent e) { }

	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }

	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }

	@Override
	public void unregister() {
		processFlow.removeListener(this);
	}
	
	public int getNbRawWorkPiecesToReplace() {
		// In case we put our finished pieces into a bin, we do not have to replace raw pieces by finished ones since the finished ones do not go onto the stacker
		if(processFlow.hasBinForFinishedPieces()) {
			return 0;
		}
		return stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW);
	}
	
	public int getFinishedAmount() {
		return processFlow.getFinishedAmount();
	}
}
