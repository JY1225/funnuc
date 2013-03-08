package eu.robojob.irscw.ui.admin.device;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class BasicStackPlateConfigureView extends AbstractFormView<BasicStackPlateConfigurePresenter> {

	private Label lblName;
	private FullTextField fulltxtName;
	private Label lblUserFrame;
	private ComboBox<String> cbbUserFrames;
	private Label lblHorizontalHoleAmount;
	private IntegerTextField itxtHorizontalHoleAmount;
	private Label lblVerticalHoleAmount;
	private IntegerTextField itxtVerticalHoleAmount;
	private Label lblHoleDiameter;
	private NumericTextField numtxtHoleDiameter;
	private Label lblStudDiameter;
	private NumericTextField numtxtStudDiameter;
	private Label lblHorizontalPadding;
	private NumericTextField numtxtHorizontalPadding;
	private Label lblVerticalPaddingTop;
	private NumericTextField numtxtVerticalPaddingTop;
	private Label lblVerticalPaddingBottom;
	private NumericTextField numtxtVerticalPaddingBottom;
	private Label lblHorizontalHoleDistance;
	private NumericTextField numtxtHorizontalHoleDistance;
	private Label lblInterferenceDistance;
	private NumericTextField numtxtInterferenceDistance;
	private Label lblOverflowPercentage;
	private NumericTextField numtxtOverflowPercentage;
	private Region spacer;
	private Button btnSave;
	
	private ObservableList<String> userFrameNames;
	
	private static final String NAME = "BasicStackPlateConfigureView.name";
	private static final String USERFRAME = "BasicStackPlateConfigureView.userFrame";
	private static final String HORIZONTALHOLEAMOUNT = "BasicStackPlateConfigureView.horizontalHoleAmount";
	private static final String VERTICALHOLEAMOUNT = "BasicStackPlateConfigureView.verticalHoleAmount";
	private static final String HOLEDIAMETER = "BasicStackPlateConfigureView.holeDiameter";
	private static final String STUDDIAMETER = "BasicStackPlateConfigureView.studDiameter";
	private static final String HORIZONTALPADDING = "BasicStackPlateConfigureView.horizontalPadding";
	private static final String VERTICALPADDINGTOP = "BasicStackPlateConfigureView.verticalPaddingTop";
	private static final String VERTICALPADDINGBOTTOM = "BasicStackPlateConfigureView.verticalPaddingBottom";
	private static final String HORIZONTALHOLEDISTANCE = "BasicStackPlateConfigureView.horizontalHoleDistance";
	private static final String INTERFERENCEDISTANCE = "BasicStackPlateConfigureView.interferenceDistance";
	private static final String OVERFLOWPERCENTAGE = "BasicStackPlateConfigureView.overflowPercentage";
	private static final String SAVE = "BasicStackPlateConfigureView.save";
	
	private BasicStackPlate basicStackPlate;
	
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";

	public BasicStackPlateConfigureView() {
		userFrameNames = FXCollections.observableArrayList();
	}
	
	@Override
	protected void build() {
		setVgap(15);
		setHgap(15);
		setPadding(new Insets(50, 0, 0, 0));
		setAlignment(Pos.TOP_CENTER);
		spacer = new Region();
		spacer.setPrefWidth(20);
		lblName = new Label(Translator.getTranslation(NAME));
		fulltxtName = new FullTextField(50);
		lblUserFrame = new Label(Translator.getTranslation(USERFRAME));
		cbbUserFrames = new ComboBox<String>();
		cbbUserFrames.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUserFrames.setItems(userFrameNames);
		lblHorizontalHoleAmount = new Label(Translator.getTranslation(HORIZONTALHOLEAMOUNT));
		itxtHorizontalHoleAmount = new IntegerTextField(3);
		lblVerticalHoleAmount = new Label(Translator.getTranslation(VERTICALHOLEAMOUNT));
		itxtVerticalHoleAmount = new IntegerTextField(3);
		lblHoleDiameter = new Label(Translator.getTranslation(HOLEDIAMETER));
		numtxtHoleDiameter = new NumericTextField(5);
		lblStudDiameter = new Label(Translator.getTranslation(STUDDIAMETER));
		numtxtStudDiameter = new NumericTextField(5);
		lblHorizontalPadding = new Label(Translator.getTranslation(HORIZONTALPADDING));
		numtxtHorizontalPadding = new NumericTextField(5);
		lblVerticalPaddingTop = new Label(Translator.getTranslation(VERTICALPADDINGTOP));
		numtxtVerticalPaddingTop = new NumericTextField(5);
		lblVerticalPaddingBottom = new Label(Translator.getTranslation(VERTICALPADDINGBOTTOM));
		numtxtVerticalPaddingBottom = new NumericTextField(5);
		lblHorizontalHoleDistance = new Label(Translator.getTranslation(HORIZONTALHOLEDISTANCE));
		numtxtHorizontalHoleDistance = new NumericTextField(5);
		lblInterferenceDistance = new Label(Translator.getTranslation(INTERFERENCEDISTANCE));
		numtxtInterferenceDistance = new NumericTextField(5);
		lblOverflowPercentage = new Label(Translator.getTranslation(OVERFLOWPERCENTAGE));
		numtxtOverflowPercentage = new NumericTextField(5);
		btnSave = createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().saveData(fulltxtName.getText(), cbbUserFrames.valueProperty().get(), Integer.parseInt(itxtHorizontalHoleAmount.getText()), 
						Integer.parseInt(itxtVerticalHoleAmount.getText()), Float.parseFloat(numtxtHoleDiameter.getText()), Float.parseFloat(numtxtStudDiameter.getText()),
						Float.parseFloat(numtxtHorizontalHoleDistance.getText()), Float.parseFloat(numtxtHorizontalPadding.getText()), 
						Float.parseFloat(numtxtVerticalPaddingTop.getText()), Float.parseFloat(numtxtVerticalPaddingBottom.getText()), 
						Float.parseFloat(numtxtInterferenceDistance.getText()), Float.parseFloat(numtxtOverflowPercentage.getText()));
			}
		});

		int row = 0;
		int column = 0;
		add(lblName, column++, row);
		add(fulltxtName, column++, row, 4, 1);
		column = 0; row++;
		add(lblUserFrame, column++, row);
		add(cbbUserFrames, column++, row, 4, 1);
		column = 0; row++;
		add(lblHorizontalHoleAmount, column++, row);
		add(itxtHorizontalHoleAmount, column++, row);
		add(spacer, column++, row);
		add(lblVerticalHoleAmount, column++, row);
		add(itxtVerticalHoleAmount, column++, row);
		column = 0; row++;
		add(lblHoleDiameter, column++, row);
		add(numtxtHoleDiameter, column++, row);
		column++;
		add(lblStudDiameter, column++, row);
		add(numtxtStudDiameter, column++, row);
		column = 0; row++;
		add(lblHorizontalHoleDistance, column++, row);
		add(numtxtHorizontalHoleDistance, column++, row);
		column++;
		add(lblHorizontalPadding, column++, row);
		add(numtxtHorizontalPadding, column++, row);
		column = 0; row++;
		add(lblVerticalPaddingTop, column++, row);
		add(numtxtVerticalPaddingTop, column++, row);
		column++;
		add(lblVerticalPaddingBottom, column++, row);
		add(numtxtVerticalPaddingBottom, column++, row);
		column = 0; row++;
		add(lblInterferenceDistance, column++, row);
		add(numtxtInterferenceDistance, column++, row);
		column++;
		add(lblOverflowPercentage, column++, row);
		add(numtxtOverflowPercentage, column++, row);
		column = 0; row++;
		add(btnSave, column++, row, 5, 1);
		GridPane.setHalignment(btnSave, HPos.CENTER);
		GridPane.setMargin(btnSave, new Insets(15, 0, 0, 0));
	}
	
	public void setUserFrames(final List<String> userFrames) {
		userFrameNames.clear();
		userFrameNames.addAll(userFrames);
	}
	
	public void setBasicStackPlate(final BasicStackPlate basicStackPlate) {
		this.basicStackPlate = basicStackPlate;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
		itxtHorizontalHoleAmount.setFocusListener(listener);
		itxtVerticalHoleAmount.setFocusListener(listener);
		numtxtHoleDiameter.setFocusListener(listener);
		numtxtStudDiameter.setFocusListener(listener);
		numtxtHorizontalPadding.setFocusListener(listener);
		numtxtVerticalPaddingTop.setFocusListener(listener);
		numtxtVerticalPaddingBottom.setFocusListener(listener);
		numtxtHorizontalHoleDistance.setFocusListener(listener);
		numtxtInterferenceDistance.setFocusListener(listener);
		numtxtOverflowPercentage.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		getPresenter().updateUserFrames();
		fulltxtName.setText(basicStackPlate.getName());
		itxtHorizontalHoleAmount.setText(basicStackPlate.getLayout().getHorizontalHoleAmount() + "");
		itxtVerticalHoleAmount.setText(basicStackPlate.getLayout().getVerticalHoleAmount() + "");
		numtxtHoleDiameter.setText(basicStackPlate.getLayout().getHoleDiameter() + "");
		numtxtStudDiameter.setText(basicStackPlate.getLayout().getStudDiameter() + "");
		numtxtHorizontalPadding.setText(basicStackPlate.getLayout().getHorizontalPadding() + "");
		numtxtVerticalPaddingTop.setText(basicStackPlate.getLayout().getVerticalPadding() + "");
		numtxtVerticalPaddingBottom.setText(basicStackPlate.getLayout().getVerticalPaddingBottom() + "");
		numtxtHorizontalHoleDistance.setText(basicStackPlate.getLayout().getHorizontalHoleDistance() + "");
		numtxtInterferenceDistance.setText(basicStackPlate.getLayout().getInterferenceDistance() + "");
		numtxtOverflowPercentage.setText(basicStackPlate.getLayout().getOverflowPercentage() + "");
		cbbUserFrames.valueProperty().set(basicStackPlate.getWorkAreas().get(0).getUserFrame().getName());
	}

}
