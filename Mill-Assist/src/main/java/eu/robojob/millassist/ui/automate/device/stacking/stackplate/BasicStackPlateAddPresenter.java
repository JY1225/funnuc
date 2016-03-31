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

    /**
     * Adds work pieces to the pallet.
     *
     * @param amount
     *            The amount of work pieces that will be added
     * @throws IncorrectWorkPieceDataException
     *             If the work piece that is added is not valid
     */
    public void addRawWorkPieces(final int amount) {
        int nbInFlow = processFlow.getTotalAmount() - stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                - processFlow.getFinishedAmount();

        // Add new pieces
        stackPlate.addRawWorkPieces(amount, processFlow.getMode().equals(ProcessFlow.Mode.AUTO));
        processFlow.setFinishedAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
        processFlow.setTotalAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                + stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
    }

    public void removeRawWorkPieces(final int amount) {
        int nbInFlow = processFlow.getTotalAmount() - stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                - processFlow.getFinishedAmount();

        // Add new pieces
        stackPlate.removeRawWorkPieces(amount);
        processFlow.setFinishedAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
        processFlow.setTotalAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                + stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
    }

    public void addFinishedWorkPieces(final int amount) {
        int nbInFlow = processFlow.getTotalAmount() - stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                - processFlow.getFinishedAmount();

        // Add new pieces
        stackPlate.addFinishedWorkPieces(amount, processFlow.getMode().equals(ProcessFlow.Mode.AUTO));
        processFlow.setFinishedAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
        processFlow.setTotalAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                + stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
    }

    public void removeFinishedWorkPieces(final int amount) {
        int nbInFlow = processFlow.getTotalAmount() - stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                - processFlow.getFinishedAmount();

        // Add new pieces
        stackPlate.removeFinishedWorkPieces(amount);
        processFlow.setFinishedAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED));
        processFlow.setTotalAmount(stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW)
                + stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED) + nbInFlow);
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
    public int getMaxRawPieces(final int currentFinishedAmount) {
        int result = stackPlate.getLayout().getMaxPiecesPossibleAmount() - currentFinishedAmount;
        if(currentFinishedAmount > 0) {
            int correction = (int)Math.ceil((double)(currentFinishedAmount - 1)/(double)stackPlate.getLayout().getLayers())+1;
            correction = stackPlate.getLayout().getStackingPositions().size() - correction;
            if(correction > 0 ){
                result = correction * stackPlate.getLayout().getLayers();
            }
        }
        return result;
    }

    public int getCurrentRawPieces() {
        return stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.RAW);
    }

    public int getCurrentFinishedPieces() {
        return stackPlate.getLayout().getWorkPieceAmount(WorkPiece.Type.FINISHED);
    }

    public void changeAmounts(final int newRawAmount, final int newFinishedAmount) {
        try {
            if ((newFinishedAmount + newRawAmount) > stackPlate.getLayout().getMaxPiecesPossibleAmount()) {
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

    public ProcessFlow getProcessFlow() {
        return this.processFlow;
    }

    public void setProcessFlow(final ProcessFlow processFlow) {
        this.processFlow = processFlow;
    }
}
