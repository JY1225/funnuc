package eu.robojob.millassist.ui.automate.device.stacking.pallet;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
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

public class UnloadPalletAddRemoveFinishedPresenter extends AbstractFormPresenter<UnloadPalletAddRemoveFinishedView, UnloadPalletMenuPresenter> implements ProcessFlowListener{

    private UnloadPallet unloadPallet;
    private ProcessFlow processFlow;
    
    public UnloadPalletAddRemoveFinishedPresenter(UnloadPalletAddRemoveFinishedView view, UnloadPallet unloadPallet, ProcessFlow processFlow) {
        super(view);
        this.unloadPallet = unloadPallet;
        this.processFlow = processFlow;
        this.processFlow.addListener(this);
        getView().build();
    }
    
    @Override
    public void modeChanged(ModeChangedEvent e) {
        if (!e.getMode().equals(Mode.AUTO)) {
            getView().setButtonEnabled(true);
        }
    }

    @Override
    public void statusChanged(StatusChangedEvent e) {
        if (unloadPallet.getCurrentPutLocation() == null) {
            getView().setButtonEnabled(true); 
        } else {
            getView().setButtonEnabled(false);
        }
    }

    @Override
    public void dataChanged(DataChangedEvent e) {}

    @Override
    public void finishedAmountChanged(FinishedAmountChangedEvent e) {}

    @Override
    public void exceptionOccured(ExceptionOccuredEvent e) {}

    @Override
    public void unregister() {
        processFlow.removeListener(this);
    }

    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

    @Override
    public boolean isConfigured() {
        return false;
    }
    
    public void addWorkpieces(final int amount) {
        try{    
            if(amount > getMaxPiecesToAdd())
                throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
            int nbInFlow = processFlow.getTotalAmount() - processFlow.getFinishedAmount();
            
            //Add new pieces 
            addWorkPieces(amount, processFlow.getMode().equals(ProcessFlow.Mode.AUTO));
            processFlow.setFinishedAmount(unloadPallet.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
            processFlow.setTotalAmount(unloadPallet.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
        } catch(IncorrectWorkPieceDataException e) {
            getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
        }
    }
    
    private void addWorkPieces(int amount, boolean resetFirst) throws IncorrectWorkPieceDataException { 
        unloadPallet.addWorkPieces(amount, resetFirst);
        getView().hideNotification();
    }
    
    public int getMaxPiecesToAdd() {
        int amount = unloadPallet.getLayout().getMaxPiecesPossibleAmount() - unloadPallet.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED);
        return amount;
    }
    
    public void removeWorkPieces(final int amount) {
        try {
            if(amount > unloadPallet.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED)) {
                throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
            }
            int nbInFlow = processFlow.getTotalAmount() - processFlow.getFinishedAmount();
            unloadPallet.removeWorkPieces(amount);
            getView().hideNotification();
            processFlow.setFinishedAmount(unloadPallet.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
            processFlow.setTotalAmount(nbInFlow - unloadPallet.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
        } catch (IncorrectWorkPieceDataException e) {
            getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
        }
    }
    
    public int getMaxPiecesToRemove() {
        return unloadPallet.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED);
    }

}
