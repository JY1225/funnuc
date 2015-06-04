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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void modeChanged(ModeChangedEvent e) {
        if (!e.getMode().equals(Mode.AUTO)) {
            getView().setButtonEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void statusChanged(StatusChangedEvent e) {
        if (unloadPallet.getCurrentPutLocation() == null) {
            getView().setButtonEnabled(true); 
        } else {
            getView().setButtonEnabled(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dataChanged(DataChangedEvent e) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void finishedAmountChanged(FinishedAmountChangedEvent e) {
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionOccured(ExceptionOccuredEvent e) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister() {
        processFlow.removeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigured() {
        return false;
    }
    
    /**
     * Add a number of work pieces to the current active process flow. This will update the finished amount and the pallet.
     * @param amount The amount of work pieces that will be added
     */
    public void addWorkpieces(final int amount) {
        try{    
            if(amount > getMaxPiecesToAdd())
                throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
            addWorkPieces(amount);
        } catch(IncorrectWorkPieceDataException e) {
            getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
        }
    }
    
    /**
     * Adds work pieces to the pallet.
     * @param amount The amount of work pieces that will be added
     * @throws IncorrectWorkPieceDataException If the work piece that is added is not valid
     */
    private void addWorkPieces(int amount) throws IncorrectWorkPieceDataException { 
        unloadPallet.addWorkPieces(amount);
        getView().hideNotification();
    }
    
    /**
     * Maximum pieces to add = total amount of the process flow - the finished amount of the process flow
     * @return The maximum number of pieces that can be added
     */
    public int getMaxPiecesToAdd() {
        return unloadPallet.getMaxPiecesPerLayerAmount() * unloadPallet.getLayers() - unloadPallet.getWorkPieceAmount(WorkPiece.Type.FINISHED);
    }
    
    /**
     * Removes an amount of work pieces from the current process flow. This will update the finished amount and the pallet.
     * @param amount The amount of work pieces that will be removed
     */
    public void removeWorkPieces(final int amount) {
        try {
            if(amount > unloadPallet.getWorkPieceAmount(WorkPiece.Type.FINISHED)) {
                throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
            }
            unloadPallet.removeWorkPieces(amount);
            getView().hideNotification();
        } catch (IncorrectWorkPieceDataException e) {
            getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
        }
    }
    
    /**
     * Maximum pieces to remove = number of finished work pieces on the pallet.
     * @return The maximum number of pieces that can be removed
     */
    public int getMaxPiecesToRemove() {
        return unloadPallet.getWorkPieceAmount(WorkPiece.Type.FINISHED);
    }

}
