package eu.robojob.millassist.ui.automate.device.stacking.conveyor.eaton;

import eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton;
import eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class ConveyorAmountsPresenter extends AbstractFormPresenter<ConveyorAmountsView, ConveyorMenuPresenter> {

	private ProcessFlow processFlow;
	private ConveyorEaton conveyor;
	
	public ConveyorAmountsPresenter(final ConveyorAmountsView view, final ProcessFlow processFlow, final ConveyorEaton conveyor) {
		super(view);
		getView().setProcessFlow(processFlow);
		getView().build();
		this.processFlow = processFlow;
		this.conveyor = conveyor;
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	public void changedData(final String finishedAmount, final String totalAmount) {
		if (! ((finishedAmount == null) || (totalAmount == null) || totalAmount.equals(""))) {
			if (Integer.parseInt(totalAmount) <= Integer.parseInt(finishedAmount) + 1) {
				getView().notifyIncorrectData();
			} else {
				getView().correctData();
			}
		} else {
			if (finishedAmount == null) {
				getView().notifyIncorrectData();
			} else if (totalAmount == null) {
				getView().notifyIncorrectData();
			} else if (totalAmount.equals("")) {
				getView().correctData();
			} else {
				getView().notifyIncorrectData();
			}
		}
	}
	
	public void updateAmounts(final String finishedAmount, final String totalAmount) {
		if ((totalAmount == null) || (totalAmount.equals(""))) {
			((ConveyorSettings) processFlow.getDeviceSettings(conveyor)).setAmount(-1);
			conveyor.setAmount(-1);
		} else {
			((ConveyorSettings) processFlow.getDeviceSettings(conveyor)).setAmount(Integer.parseInt(totalAmount));
			conveyor.setAmount(Integer.parseInt(totalAmount));
		}
		processFlow.setFinishedAmount(Integer.parseInt(finishedAmount));
		processFlow.processProcessFlowEvent(new DataChangedEvent(processFlow, null, false));
	}
}