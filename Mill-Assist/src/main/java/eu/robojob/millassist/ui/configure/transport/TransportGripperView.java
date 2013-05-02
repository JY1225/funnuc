package eu.robojob.millassist.ui.configure.transport;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import eu.robojob.millassist.external.robot.Gripper;
import eu.robojob.millassist.external.robot.GripperBody;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.ui.controls.IconFlowSelector;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.model.TransportInformation;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class TransportGripperView extends AbstractFormView<TransportGripperPresenter> {

	private TransportInformation transportInfo;
	
	private Label lblGripperHead;
	private ComboBox<String> cbbGripperHeads;
	private Label lblGripper;
	private IconFlowSelector ifsGrippers;
	
	private static final String GRIPPERHEAD = "TransportGripperView.gripperHead";
	private static final String GRIPPER = "TransportGripperView.gripper";
	
	private static final int HGAP = 15;
	private static final int VGAP = 10;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
		
	public TransportGripperView() {
		super();
		setVgap(VGAP);
		setHgap(HGAP);
	}
	
	public void setTransportInfo(final TransportInformation transportInfo) {
		this.transportInfo = transportInfo;
	}
	
	@Override
	protected void build() {
		int column = 0;
		int row = 0;
		
		getChildren().clear();
		
		lblGripperHead = new Label(Translator.getTranslation(GRIPPERHEAD));
		add(lblGripperHead, column++, row);
		
		cbbGripperHeads = new ComboBox<String>();
		cbbGripperHeads.setPrefSize(60, UIConstants.COMBO_HEIGHT);
		add(cbbGripperHeads, column++, row);
		cbbGripperHeads.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				// selecting null is not possible
				if (newValue != null) {
					if ((oldValue == null) || (!oldValue.equals(newValue))) {
						getPresenter().changedGripperHead(newValue);
					}
				}
			}
		});
		
		column = 0;
		row++;
		
		lblGripper = new Label(Translator.getTranslation(GRIPPER));
		add(lblGripper, column++, row);
		
		ifsGrippers = new IconFlowSelector();
		ifsGrippers.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
		column = 0;
		row++;
		add(ifsGrippers, column++, row, 3, 1);
		
		row++;
	}
	

	@Override
	public void refresh() {
		refreshGripperHeads();
		refreshGrippers();
	}
	
	public void refreshGripperHeads() {
		// as we assume the robot and robotBody are fixed, we can take the following values directly from the used robot
		GripperBody body = transportInfo.getRobotSettings().getGripperBody();
		cbbGripperHeads.getItems().clear();
		for (GripperHead head : body.getGripperHeads()) {
			cbbGripperHeads.getItems().add(head.getName());
		}
		if (transportInfo.getPickStep().getRobotSettings() != null) {
			if (transportInfo.getPickStep().getRobotSettings().getGripperHead() != null) {
				cbbGripperHeads.setValue(transportInfo.getPickStep().getRobotSettings().getGripperHead().getName());
			}
		}
	}
	
	public void refreshGrippers() {
		ifsGrippers.clearItems();
		GripperHead gripperHead = transportInfo.getPickStep().getRobotSettings().getGripperHead();
		if (gripperHead != null) {
			int itemIndex = 0;
			for (final Gripper gripper : gripperHead.getPossibleGrippers()) {
				ifsGrippers.addItem(itemIndex, gripper.getName(), gripper.getImageUrl(), new EventHandler<MouseEvent>() {
					@Override
					public void handle(final MouseEvent event) {
						getPresenter().changedGripper(gripper);
					}
				});
				itemIndex++;
			}
			setSelectedGripper();
		}
	}
	
	public void setSelectedGripper() {
		ifsGrippers.deselectAll();
		if (transportInfo.getPickStep().getRobotSettings() != null) {
			if (transportInfo.getPickStep().getRobotSettings().getGripperHead().getGripper() != null) {
				ifsGrippers.setSelected(transportInfo.getPickStep().getRobotSettings().getGripperHead().getGripper().getName());
			} 
		} 
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
	}

}
