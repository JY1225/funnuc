package eu.robojob.millassist.ui.configure.device.stacking.conveyor;

import eu.robojob.millassist.external.device.stacking.conveyor.Conveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorSettings;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class ConveyorRawWorkPieceOffsetPresenter extends AbstractFormPresenter<ConveyorRawWorkPieceOffsetView, ConveyorMenuPresenter> {

	private Conveyor conveyor;
	private ProcessFlow processFlow;
			
	public ConveyorRawWorkPieceOffsetPresenter(final ConveyorRawWorkPieceOffsetView view, final Conveyor conveyor, final ProcessFlow processFlow) {
		super(view);
		this.conveyor = conveyor;
		this.processFlow = processFlow;
		view.setConveyorLayout(conveyor.getLayout());
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	public void changedOffsetSupport1(final float value) {
		conveyor.getLayout().setOffsetSupport1(value);
		((ConveyorSettings) processFlow.getDeviceSettings(conveyor)).setOffsetSupport1(value);
		conveyor.loadDeviceSettings(processFlow.getDeviceSettings(conveyor));
		processFlow.processProcessFlowEvent(new DataChangedEvent(processFlow, null, false));
	}
	
	public void changedOffsetOtherSupports(final float value) {
		conveyor.getLayout().setOffsetOtherSupports(value);
		((ConveyorSettings) processFlow.getDeviceSettings(conveyor)).setOffsetOtherSupports(value);
		conveyor.loadDeviceSettings(processFlow.getDeviceSettings(conveyor));
		processFlow.processProcessFlowEvent(new DataChangedEvent(processFlow, null, false));
	}
}
