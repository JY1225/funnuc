package eu.robojob.millassist.ui.automate.device.stacking.stackplate;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class BasicStackPlateRefillPresenter extends AbstractFormPresenter<BasicStackPlateRefillView, BasicStackPlateMenuPresenter> implements ProcessFlowListener {

	private BasicStackPlate stackPlate;
	private ProcessFlow processFlow;
	
	public BasicStackPlateRefillPresenter(final BasicStackPlateRefillView view, final BasicStackPlate basicStackPlate, 
			final ProcessFlow processFlow) {
		super(view);
		getView().setBasicStackPlate(basicStackPlate);
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

	public void refill(final int amount) {
		try {
			stackPlate.replaceFinishedWorkPieces(amount);
			processFlow.setFinishedAmount(processFlow.getFinishedAmount() - amount);
			getView().hideNotification();
		} catch (IncorrectWorkPieceDataException e) {
			getView().showNotification(e.getLocalizedMessage());
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
}
