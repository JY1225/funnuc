package eu.robojob.irscw.ui.process.flow;

import org.apache.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import eu.robojob.irscw.external.device.AbstractDevice;

//for now, we only allow one row, in the future, multiple rows could be possible
// to accomplish this, more HBox's are to be added and the components should be
// distributed amongst them	
public class ProcessFlowView extends VBox  {

	private HBox row1;
	
	private static Logger logger = Logger.getLogger(ProcessFlowView.class);
	
	private ProcessFlowPresenter presenter;
	
	private static final int BUTTON_HEIGHT = 40;
	
	public ProcessFlowView() {
		buildView();
	}
	
	public void setPresenter(ProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}

	private void buildView() {
		row1 = new HBox();
		getChildren().add(row1);
		AbstractDevice previousDevice = null;
		//row1.getChildren().add(new ProcessFlowTransportButton());
		row1.getChildren().add(new TransportButton());
	}
}

