package eu.robojob.irscw.ui.process.flow;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.process.flow.DeviceButton.DeviceType;

//for now, we only allow one row, in the future, multiple rows could be possible
// to accomplish this, more HBox's are to be added and the components should be
// distributed amongst them	
public class ProcessFlowView extends GridPane  {
	
	private static Logger logger = Logger.getLogger(ProcessFlowView.class);
	
	private ProcessFlowPresenter presenter;
		
	public ProcessFlowView() {
		buildView();
	}
	
	public void setPresenter(ProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}

	private void buildView() {
		setPadding(new Insets(20, 20, 20, 20));
	}
	
	public void addDevice(String id, DeviceType type) {
		
	}
	
	public void addTransport(String id, boolean leftQuestionMark, boolean rightQuestionMark) {
		
	}
}

