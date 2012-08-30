package eu.robojob.irscw.ui.process.flow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.ui.process.flow.DeviceButton.DeviceType;

//for now, we only allow one row, in the future, multiple rows could be possible
// to accomplish this, more HBox's are to be added and the components should be
// distributed amongst them	
public class ProcessFlowView extends GridPane  {
	
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
		
		AbstractDevice previousDevice = null;
		setPadding(new Insets(20, 20, 20, 20));
		
		int row = 0;
		int column = 0;
		
		DeviceButton device1 = new DeviceButton("Conveyor", DeviceType.PRE_STACKING);
		add(device1, column++, row);
		
		TransportButton transport1 = new TransportButton();
		transport1.setLeftQuestionMarkActive(true);
		transport1.setRightQuestionMarkActive(true);
		add(transport1, column++, row);
		
		setMargin(transport1, new Insets(10, 0, 0, 0));
		
		DeviceButton device2 = new DeviceButton("Pregen-apparaat", DeviceType.PRE_PROCESSING);
		add(device2, column++, row);
		
		TransportButton transport2 = new TransportButton();
		transport2.setRightQuestionMarkActive(true);
		add(transport2, column++, row);
		
		setMargin(transport2, new Insets(10, 0, 0, 0));
		
		DeviceButton device3 = new DeviceButton("CNC Machine", DeviceType.CNC_MACHINE);
		add(device3, column++, row);
		
		TransportButton transport3 = new TransportButton();
		transport3.setLeftQuestionMarkActive(true);
		transport3.setRightQuestionMarkActive(true);
		add(transport3, column++, row);
		
		setMargin(transport3, new Insets(10, 0, 0, 0));
		
		DeviceButton device4 = new DeviceButton("Conveyor", DeviceType.POST_STACKING);
		add(device4, column++, row);

		device1.toFront();
		device2.toFront();
		device3.toFront();
		device3.animate();
		device4.toFront();
		
		this.setAlignment(Pos.CENTER);
	}
}

