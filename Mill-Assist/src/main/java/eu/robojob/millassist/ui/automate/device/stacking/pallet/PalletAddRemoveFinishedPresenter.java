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
    public void addRawWorkPieces(final int amount) {
            int nbInFlow = processFlow.getTotalAmount() - pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                    - processFlow.getFinishedAmount();

            // Add new pieces
            pallet.addRawWorkPieces(amount, processFlow.getMode().equals(ProcessFlow.Mode.AUTO));
            processFlow.setFinishedAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
            processFlow.setTotalAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                    + pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
    }

    public void removeRawWorkPieces(final int amount) {
            int nbInFlow = processFlow.getTotalAmount() - pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                    - processFlow.getFinishedAmount();

            // Add new pieces
            pallet.removeRawWorkPieces(amount);
            processFlow.setFinishedAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
            processFlow.setTotalAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                    + pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
    }

    public void addFinishedWorkPieces(final int amount) {
        int nbInFlow = processFlow.getTotalAmount() - pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                - processFlow.getFinishedAmount();

        // Add new pieces
        pallet.addFinishedWorkPieces(amount, processFlow.getMode().equals(ProcessFlow.Mode.AUTO));
        processFlow.setFinishedAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
        processFlow.setTotalAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                + pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
    }

    public void removeFinishedWorkPieces(final int amount) {
        int nbInFlow = processFlow.getTotalAmount() - pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                - processFlow.getFinishedAmount();

        // Add new pieces
        pallet.removeFinishedWorkPieces(amount);
        processFlow.setFinishedAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
        processFlow.setTotalAmount(pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                + pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
    }

    public int getFinishedAmount() {
        return processFlow.getFinishedAmount();
    }

    boolean isAutoMode() {
        return processFlow.getMode().equals(Mode.AUTO);
    }

    /**
     * Maximum pieces to remove = number of finished work pieces on the pallet.
     * 
     * @return The maximum number of pieces that can be removed
     */
    public int getMaxRawPieces(int currentFinishedAmount) {
        int result = pallet.getGridLayout().getMaxPiecesPossibleAmount() - currentFinishedAmount;
        if(currentFinishedAmount > 0) {
            int correction = (int)Math.ceil((double)(currentFinishedAmount - 1)/(double)pallet.getGridLayout().getLayers())+1;
            correction = pallet.getGridLayout().getStackingPositions().size() - correction;
            if(correction > 0 ){
                result = correction * pallet.getGridLayout().getLayers();
            }
        }
        return result;
    }

    public int getCurrentRawPieces() {
        return pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.RAW);
    }

    public int getCurrentFinishedPieces() {
        return pallet.getGridLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED);
    }

    public void changeAmounts(final int newRawAmount, final int newFinishedAmount) {
        try {
            if (newFinishedAmount + newRawAmount > pallet.getGridLayout().getMaxPiecesPossibleAmount()) {
                throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
            }
            
            if(getMaxRawPieces(newFinishedAmount) < newRawAmount) {
                throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
            }
            
            if (newFinishedAmount > getCurrentFinishedPieces()) {
                if(newRawAmount < getCurrentRawPieces()) {
                    removeRawWorkPieces(getCurrentRawPieces() - newRawAmount);
                    addFinishedWorkPieces(newFinishedAmount - getCurrentFinishedPieces());
                } else if(newRawAmount > getCurrentRawPieces()) {
                    addFinishedWorkPieces(newFinishedAmount - getCurrentFinishedPieces());
                    addRawWorkPieces(newRawAmount - getCurrentRawPieces());
                } else {
                    addFinishedWorkPieces(newFinishedAmount - getCurrentFinishedPieces());
                }
            } else {
                if (newFinishedAmount < getCurrentFinishedPieces()) {
                    removeFinishedWorkPieces(getCurrentFinishedPieces() - newFinishedAmount);
                }
                if (newRawAmount > getCurrentRawPieces()) {
                    addRawWorkPieces(newRawAmount - getCurrentRawPieces());
                } else if (newRawAmount < getCurrentRawPieces()) {
                    removeRawWorkPieces(getCurrentRawPieces() - newRawAmount);
                }
            }
            getView().hideNotification();
        } catch (IncorrectWorkPieceDataException exception) {
            getView().showNotification(exception.getLocalizedMessage(), Type.WARNING);
        }

    }

    public ProcessFlow getProcessFlow() {
        return this.processFlow;
    }

    public void setProcessFlow(ProcessFlow processFlow) {
        this.processFlow = processFlow;
    }
}
