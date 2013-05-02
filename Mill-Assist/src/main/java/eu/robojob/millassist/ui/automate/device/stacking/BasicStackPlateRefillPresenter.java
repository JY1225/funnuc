package eu.robojob.irscw.ui.automate.device.stacking;

import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class BasicStackPlateRefillPresenter extends AbstractFormPresenter<BasicStackPlateRefillView, BasicStackPlateMenuPresenter> {

	private BasicStackPlate stackPlate;
	private ProcessFlow processFlow;
	
	public BasicStackPlateRefillPresenter(final BasicStackPlateRefillView view, final BasicStackPlate basicStackPlate, 
			final ProcessFlow processFlow) {
		super(view);
		getView().setBasicStackPlate(basicStackPlate);
		this.stackPlate = basicStackPlate;
		this.processFlow = processFlow;
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
}