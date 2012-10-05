package eu.robojob.irscw.ui.configure.transport;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.robot.Gripper;
import eu.robojob.irscw.external.robot.GripperBody;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.IconFlowSelector;
import eu.robojob.irscw.ui.controls.IconFlowSelectorItemChangedHandler;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.model.TransportInformation;
import eu.robojob.irscw.util.UIConstants;

public class TransportGripperView extends AbstractFormView<TransportGripperPresenter> {

	private TransportInformation transportInfo;
	
	// for now we assume the robot and robotbody are fixed! 
	private Label lblGripperHead;
	private ComboBox<String> cbbGripperHeads;
	private Label lblGripper;
	private IconFlowSelector ifsGrippers;
	
	private static final int HGAP = 15;
	private static final int VGAP = 10;
	
	private static Logger logger = Logger.getLogger(TransportGripperView.class); 
	
	public TransportGripperView() {
		super();
		setVgap(VGAP);
		setHgap(HGAP);
	}
	
	public void setTransportInfo(TransportInformation transportInfo) {
		this.transportInfo = transportInfo;
	}
	
	@Override
	protected void build() {
		int column = 0;
		int row = 0;
		
		getChildren().clear();
		
		lblGripperHead = new Label(translator.getTranslation("gripperHeads"));
		add(lblGripperHead, column++, row);
		
		// for now we assume the gripper head also can't be changed, because a fixed convention is used!
		cbbGripperHeads = new ComboBox<String>();
		cbbGripperHeads.setPrefSize(UIConstants.COMBO_WIDTH/2, UIConstants.COMBO_HEIGHT);
		add(cbbGripperHeads, column++, row);
		cbbGripperHeads.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if ((oldValue == null) || (!oldValue.equals(newValue))) {
					presenter.changedGripperHead(newValue);
				}
			}
		});
		cbbGripperHeads.setDisable(true);
		
		column = 0;
		row++;
		
		lblGripper = new Label(translator.getTranslation("gripper"));
		add(lblGripper, column++, row);
		
		ifsGrippers = new IconFlowSelector();
		ifsGrippers.setPrefWidth(530);
		column = 0;
		row++;
		add(ifsGrippers, column++, row, 3, 1);
		
		row++;
		
	}

	private void refreshGripperHeads() {
		// as we assume the robot and robotBody are fixed, we can take the following values directly from the used robot
		GripperBody body = transportInfo.getRobot().getGripperBody();
		cbbGripperHeads.getItems().clear();
		for (GripperHead head : body.getGripperHeads()) {
			cbbGripperHeads.getItems().add(head.getId());
		}
		if (transportInfo.getPickStep().getRobotSettings() != null) {
			if (transportInfo.getPickStep().getRobotSettings().getGripperHead() != null) {
				cbbGripperHeads.setValue(transportInfo.getPickStep().getRobotSettings().getGripperHead().getId());
			}
		}
	}
	
	private void refreshGrippers() {
		ifsGrippers.clearItems();
		GripperBody body = transportInfo.getRobot().getGripperBody();
		int itemIndex = 0;
		for (Gripper gripper : body.getPossibleGrippers()) {
			ifsGrippers.addItem(itemIndex, gripper.getId(), gripper.getImageUrl(), new IconFlowSelectorItemChangedHandler() {
				@Override
				public void handle(MouseEvent event, int index, String name) {
					presenter.changedGripper(name);
				}
			});
			itemIndex++;
		}
		setSelectedGripper();
	}
	
	public void setSelectedGripper() {
		ifsGrippers.deselectAll();
		if (transportInfo.getPickStep().getRobotSettings() != null) {
			if (transportInfo.getPickStep().getRobotSettings().getGripper() != null) {
				ifsGrippers.setSelected(transportInfo.getPickStep().getRobotSettings().getGripper().getId());
			} 
		} 
	}
	
	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		refreshGripperHeads();
		refreshGrippers();
		setSelectedGripper();
	}

	public abstract class ChangedGripperHandler implements EventHandler<MouseEvent> {
		protected Gripper gripper;
		
		public ChangedGripperHandler(Gripper gripper) {
			this.gripper = gripper;
		}

		@Override
		public abstract void handle(MouseEvent event);
		
	}
}
