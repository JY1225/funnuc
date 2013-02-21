package eu.robojob.irscw.ui.admin.device;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.util.Translator;

public class BasicStackPlateConfigureView extends AbstractFormView<BasicStackPlateConfigurePresenter> {

	private Label lblName;
	private FullTextField fulltxtName;
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
	
	private static final String NAME = "BasicStackPlateConfigureView.name";
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
	
	public BasicStackPlateConfigureView() {
		build();
	}
	
	@Override
	protected void build() {
		setVgap(15);
		setHgap(15);
		spacer = new Region();
		spacer.setPrefWidth(20);
		lblName = new Label(Translator.getTranslation(NAME));
		fulltxtName = new FullTextField(50);
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
		
		int row = 0;
		int column = 0;
		add(lblHorizontalHoleAmount, column++, row);
		add(itxtHorizontalHoleAmount, column++, row);
		add(spacer, column++, row);
		add(lblVerticalHoleAmount, column++, row);
		add(itxtVerticalHoleAmount, column++, row);
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
		
	}

}
