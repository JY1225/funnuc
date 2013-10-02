package eu.robojob.millassist.ui.configure.transport;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import eu.robojob.millassist.external.robot.Gripper;
import eu.robojob.millassist.external.robot.Gripper.Type;
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
	private Label lblInnerOuter;
	private Button btnInner;
	private Button btnOuter;
	
	private HBox hboxButtonsInnerOuter;
	
	private static final String GRIPPERHEAD = "TransportGripperView.gripperHead";
	private static final String GRIPPER = "TransportGripperView.gripper";
	private static final String INNER_OUTER = "TransportGripperView.innerOuter";
	private static final String INNER = "TransportGripperView.inner";
	private static final String OUTER = "TransportGripperView.outer";
	
	private static final int HGAP = 15;
	private static final int VGAP = 10;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	
	//private static final String PATH_OUTER = "m 7.875,9.90625 0,8.5625 0,8.625 21.25,0 0,-8.625 0,-8.5625 -21.25,0 z m 21.25,8.5625 4.1875,2.875 0,-2.09375 3.59375,0 0,-1.53125 -3.59375,0 0,-2.0625 -4.1875,2.8125 z m -21.25,0 -4.1875,-2.8125 0,2.0625 -3.59375,0 0,1.53125 3.59375,0 0,2.09375 4.1875,-2.875 z";
	//private static final String PATH_INNER = "M 7.875 9.90625 L 7.875 27.09375 L 29.125 27.09375 L 29.125 9.90625 L 7.875 9.90625 z M 10.375 12.40625 L 26.625 12.40625 L 26.625 18.3125 L 26.625 24.59375 L 10.375 24.59375 L 10.375 18.3125 L 10.375 12.40625 z M 10.375 18.3125 L 14.59375 21.125 L 14.59375 19.0625 L 18.1875 19.0625 L 18.1875 17.53125 L 14.59375 17.53125 L 14.59375 15.40625 L 10.375 18.3125 z M 26.625 18.3125 L 22.40625 15.40625 L 22.40625 17.53125 L 18.8125 17.53125 L 18.8125 19.0625 L 22.40625 19.0625 L 22.40625 21.125 L 26.625 18.3125 z";
		
	public TransportGripperView() {
		super();
		getContents().setVgap(VGAP);
		getContents().setHgap(HGAP);
	}
	
	public void setTransportInfo(final TransportInformation transportInfo) {
		this.transportInfo = transportInfo;
	}
	
	@Override
	protected void build() {
		int column = 0;
		int row = 0;
		
		getContents().getChildren().clear();
		
		lblGripperHead = new Label(Translator.getTranslation(GRIPPERHEAD));
		getContents().add(lblGripperHead, column++, row);
		
		cbbGripperHeads = new ComboBox<String>();
		cbbGripperHeads.setPrefSize(60, UIConstants.COMBO_HEIGHT);
		cbbGripperHeads.setMinSize(60, UIConstants.COMBO_HEIGHT);
		getContents().add(cbbGripperHeads, column++, row);
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
		getContents().add(lblGripper, column++, row);
		
		ifsGrippers = new IconFlowSelector();
		ifsGrippers.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
		column = 0;
		row++;
		getContents().add(ifsGrippers, column++, row, 4, 1);
		
		row++;
		column = 0;
		
		lblInnerOuter = new Label(Translator.getTranslation(INNER_OUTER));
		getContents().add(lblInnerOuter, column++, row);
		btnOuter = createButton(Translator.getTranslation(OUTER), UIConstants.BUTTON_HEIGHT*2.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().changedClampingManner(false);
			}
		});
		btnOuter.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnInner = createButton(Translator.getTranslation(INNER), UIConstants.BUTTON_HEIGHT*2.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().changedClampingManner(true);
			}
		});
		btnInner.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		
		hboxButtonsInnerOuter = new HBox();
		hboxButtonsInnerOuter.getChildren().addAll(btnOuter, btnInner);
		hboxButtonsInnerOuter.setSpacing(0);
		getContents().add(hboxButtonsInnerOuter, column++, row);
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
				Gripper gripper = transportInfo.getPickStep().getRobotSettings().getGripperHead().getGripper();
				ifsGrippers.setSelected(gripper.getName());
				btnInner.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
				btnOuter.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
				if (gripper.getType() == Type.TWOPOINT) {
					lblInnerOuter.setDisable(false);
					hboxButtonsInnerOuter.setDisable(false);
					if (transportInfo.getPickStep().getRobotSettings().isGripInner()) {
						btnInner.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
					} else {
						btnOuter.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
					}
				} else {
					lblInnerOuter.setDisable(true);
					hboxButtonsInnerOuter.setDisable(true);
				}
			} 
		} 
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
	}

}
