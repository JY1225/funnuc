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

public class BasicStackPlateAddPresenter extends AbstractFormPresenter<BasicStackPlateAddView, BasicStackPlateMenuPresenter> implements ProcessFlowListener {

	private BasicStackPlate stackPlate;
	private ProcessFlow processFlow;
	
	public BasicStackPlateAddPresenter(final BasicStackPlateAddView view, final BasicStackPlate basicStackPlate, 
			final ProcessFlow processFlow) {
		super(view);
		this.stackPlate = basicStackPlate;
		this.processFlow = processFlow;
		if(processFlow.hasBinForFinishedPieces()) {
			view.disableReplaceFinishedBox();
		}
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
	
	public void addWorkpieces(final int amount, boolean replaceFinishedPieces) {
		try{	
			if(amount > getMaxPiecesToAdd(replaceFinishedPieces))
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
			int nbInFlow = processFlow.getTotalAmount() - stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW) - processFlow.getFinishedAmount();
			System.out.println("NB IN FLOW: " + nbInFlow + " = " + processFlow.getTotalAmount() + " - " + stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW) + " - " + processFlow.getFinishedAmount());
			//Replace finished workpieces by new ones
			if(replaceFinishedPieces) {
				stackPlate.getLayout().removeFinishedFromTable();
			}
			//Add new pieces
			addWorkPieces(amount, (replaceFinishedPieces && processFlow.getMode().equals(ProcessFlow.Mode.AUTO)));
			processFlow.setFinishedAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
			processFlow.setTotalAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW) + stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);	
		} catch(IncorrectWorkPieceDataException e) {
			getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
		}
	}
	
	private int getMaxPiecesToAdd(boolean replaceFinished) {
		if(replaceFinished)
			return getMaxAddAmount() + getMaxFinishedToReplaceAmount();
		else
			return getMaxAddAmount();
	}
	
	//Add new workpieces and leave the finished ones on the table
	private void addWorkPieces(int amount, boolean resetFirst) throws IncorrectWorkPieceDataException { 
		stackPlate.addWorkPieces(amount, resetFirst);	
		getView().hideNotification();
	}
	
	public int getMaxFinishedToReplaceAmount() {
		return stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED);
	}
	
	public int getMaxAddAmount() {
		int amount = stackPlate.getLayout().getMaxPiecesPossibleAmount() - getMaxFinishedToReplaceAmount() - stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW);
		if(processFlow.getMode().equals(ProcessFlow.Mode.AUTO) && (!processFlow.hasBinForFinishedPieces())) {
			//We assume 2 pieces are being processed
			amount -= 2;
		} 
		return amount;
	}

	@Override
	public void modeChanged(final ModeChangedEvent e) {
		if (!e.getMode().equals(Mode.AUTO)) {
			getView().setButtonEnabled(true);
		}
	}

	@Override
	public void statusChanged(final StatusChangedEvent e) {
		if ((stackPlate.getCurrentPickLocation() == null) && (stackPlate.getCurrentPutLocation() == null)) {
			getView().setButtonEnabled(true); 
		} else {
			getView().setButtonEnabled(false);
		}
	}

	@Override public void dataChanged(final DataChangedEvent e) { }

	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }

	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }

	@Override
	public void unregister() {
		processFlow.removeListener(this);
	}
	
	public int getFinishedAmount() {
		return processFlow.getFinishedAmount();
	}
}
