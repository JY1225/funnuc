package eu.robojob.irscw.ui.process.flow;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.ProcessFlow;

//for now, we only allow one row, in the future, multiple rows could be possible
// to accomplish this, more HBox's are to be added and the components should be
// distributed amongst them	
public class ProcessFlowView extends VBox  {

	private HBox row1;
	private ProcessFlow processFlow;
	
	private ProcessFlowPresenter presenter;
	
	public ProcessFlowView(ProcessFlow processFlow) {
		this.processFlow = processFlow;
		buildView();
	}
	
	public ProcessFlowView() {
		this.processFlow = new ProcessFlow();
		buildView();
	}
	
	public void setPresenter(ProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}

	private void buildView() {
		AbstractDevice previousDevice = null;
		for(AbstractProcessStep step : processFlow.getProcessSteps()) {
			if (previousDevice == null) {
				// first step
			} else if (step.getDevice() != previousDevice) {
				// insert arrow and new device-representation
			}
		}
	}
}

