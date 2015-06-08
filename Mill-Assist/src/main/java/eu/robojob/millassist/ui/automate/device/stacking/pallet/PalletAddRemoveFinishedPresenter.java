package eu.robojob.millassist.ui.automate.device.stacking.pallet;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
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

public class PalletAddRemoveFinishedPresenter extends
        AbstractFormPresenter<PalletAddRemoveFinishedView, PalletMenuPresenter> implements ProcessFlowListener {

    private ProcessFlow processFlow;
    private Pallet pallet;

    public PalletAddRemoveFinishedPresenter(PalletAddRemoveFinishedView view, Pallet pallet, ProcessFlow processFlow) {
        super(view);
        this.pallet = pallet;
        this.processFlow = processFlow;
        this.processFlow.addListener(this);
        getView().build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modeChanged(ModeChangedEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void statusChanged(StatusChangedEvent e) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dataChanged(DataChangedEvent e) {
    }

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
    public void exceptionOccured(ExceptionOccuredEvent e) {
    }

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
     * Adds work pieces to the pallet.
     * 
     * @param amount
     *            The amount of work pieces that will be added
     * @throws IncorrectWorkPieceDataException
     *             If the work piece that is added is not valid
     */
    public void addWorkpieces(final int amount, boolean replaceFinishedPieces) {
        try {
            if (amount > getMaxPiecesToAdd(replaceFinishedPieces))
                throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
            int nbInFlow = processFlow.getTotalAmount() - pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                    - processFlow.getFinishedAmount();
            // Replace finished workpieces by raw ones
            if (replaceFinishedPieces) {
                pallet.getGridLayout().removeFinishedFromTable();
            }
            // Add new pieces
            addWorkPieces(amount, processFlow.getMode().equals(ProcessFlow.Mode.AUTO));
            processFlow.setFinishedAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
            processFlow.setTotalAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                    + pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
        } catch (IncorrectWorkPieceDataException e) {
            getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
        }
    }

    /**
     * Maximum pieces to add = total amount of the process flow - the finished amount of the process flow
     * 
     * @return The maximum number of pieces that can be added
     */
    public int getMaxPiecesToAdd(boolean replaceFinished) {
        if (replaceFinished)
            return getMaxAddAmount() + getMaxFinishedToReplaceAmount();
        else
            return getMaxAddAmount();
    }
    
    private void addWorkPieces(int amount, boolean resetFirst) throws IncorrectWorkPieceDataException { 
        pallet.addWorkPieces(amount, resetFirst);   
        getView().hideNotification();
    }
    
    public int getMaxFinishedToReplaceAmount() {
        return pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED);
    }
    
    public int getMaxAddAmount() {
        int amount = pallet.getGridLayout().getMaxPiecesPossibleAmount() - getMaxFinishedToReplaceAmount() - pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW);
        int nbInFlow = processFlow.getTotalAmount() - pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW) - processFlow.getFinishedAmount();
        if(!processFlow.hasBinForFinishedPieces()) {
            amount -= nbInFlow;
        } 
        return amount;
    }

    /**
     * Maximum pieces to remove = number of finished work pieces on the pallet.
     * 
     * @return The maximum number of pieces that can be removed
     */
    public void replaceRawByFinished(final int amount) {
        if(amount > 0) {
            try {
                if(amount > getNbRawWorkPiecesToReplace()) {
                    throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
                } else {
                    processFlow.setFinishedAmount(amount + processFlow.getFinishedAmount());
                    pallet.placeFinishedWorkPieces(amount, processFlow.hasBinForFinishedPieces() || processFlow.hasUnloadPalletForFinishedPieces());
                }
            } catch (IncorrectWorkPieceDataException e) {
                getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
            }
        } 
    }

    public int getNbRawWorkPiecesToReplace() {
        // In case we put our finished pieces into a bin, we do not have to replace raw pieces by finished ones since the finished ones do not go onto the stacker
//      if(processFlow.hasBinForFinishedPieces()) {
//          return 0;
//      }
        return pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW);
    }
    
    public int getFinishedAmount() {
        return processFlow.getFinishedAmount();
    }
    
    boolean isAutoMode() {
        return processFlow.getMode().equals(Mode.AUTO);
    }
}
