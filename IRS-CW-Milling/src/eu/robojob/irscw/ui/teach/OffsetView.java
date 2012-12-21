package eu.robojob.irscw.ui.teach;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.robojob.irscw.ui.configure.ConfigureView;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class OffsetView extends HBox {

	private StackPane keyboardPane;
	private VBox formPane;
	
	private Label lblGeneralInfo;
	private Label lblOffsetLength;
	private Label lblOffsetWidth;
	private NumericTextField ntxtOffsetLength;
	private NumericTextField ntxtOffsetWidth;
	private Text txtBtnOk;
	private Button btnOk;
	
	private static final int MAX_LENGTH = 5;
	private static final int FORM_WIDTH = 570;
	private static final int FORM_HEIGHT = 300;
	private static final int INFO_LABEL_HEIGHT = 50;
	private static final int FORM_CONTENT_WIDTH = 375;
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BTN_HEIGHT = 40;
	
	private static final String GENERAL_INFO = "OffsetView.offsetGeneralInfo";
	private static final String OFFSET_LENGTH = "OffsetView.offsetLength";
	private static final String OFFSET_WIDTH = "OffsetView.offsetWidth";
	private static final String CONTINUE = "OffsetView.continue";
		
	private OffsetPresenter presenter;
	
	public OffsetView() {
		build();
	}
	
	public void setPresenter(final OffsetPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		keyboardPane = new StackPane();
		formPane = new VBox();
		keyboardPane.setPrefWidth(ConfigureView.WIDTH_BOTTOM_LEFT);
		HBox.setHgrow(formPane, Priority.ALWAYS);
		formPane.setAlignment(Pos.CENTER);
		formPane.setPrefSize(FORM_WIDTH, FORM_HEIGHT);
		setMargin(formPane, new Insets(0, 0, 0, 0));
		lblGeneralInfo = new Label();
		lblGeneralInfo.getStyleClass().add(TeachView.CSS_CLASS_TEACH_MESSAGE);
		lblGeneralInfo.setText(Translator.getTranslation(GENERAL_INFO));
		lblGeneralInfo.setPrefSize(FORM_WIDTH, INFO_LABEL_HEIGHT);
		lblGeneralInfo.setWrapText(true);
		lblOffsetLength = new Label();
		lblOffsetLength.getStyleClass().add(TeachView.CSS_CLASS_TEACH_MESSAGE);
		lblOffsetLength.setText(Translator.getTranslation(OFFSET_LENGTH));
		lblOffsetWidth = new Label();
		lblOffsetWidth.getStyleClass().add(TeachView.CSS_CLASS_TEACH_MESSAGE);
		lblOffsetWidth.setText(Translator.getTranslation(OFFSET_WIDTH));
		HBox hboxLength = new HBox();
		hboxLength.setAlignment(Pos.CENTER_RIGHT);
		hboxLength.setPrefWidth(FORM_CONTENT_WIDTH);
		hboxLength.setMinWidth(FORM_CONTENT_WIDTH);
		hboxLength.setMaxWidth(FORM_CONTENT_WIDTH);
		ntxtOffsetLength = new NumericTextField(MAX_LENGTH);
		ntxtOffsetLength.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetLength.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetLength.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				presenter.setOffsetLength(arg2);
			}
		});
		HBox.setMargin(ntxtOffsetLength, new Insets(0, 40, 0, 20));
		hboxLength.getChildren().add(lblOffsetLength);
		hboxLength.getChildren().add(ntxtOffsetLength);
		VBox.setMargin(hboxLength, new Insets(5, 0, 5, 0));
		HBox hboxWidth = new HBox();
		ntxtOffsetWidth = new NumericTextField(MAX_LENGTH);
		hboxWidth.setAlignment(Pos.CENTER_RIGHT);
		hboxWidth.setPrefWidth(FORM_CONTENT_WIDTH);
		hboxWidth.setMinWidth(FORM_CONTENT_WIDTH);
		hboxWidth.setMaxWidth(FORM_CONTENT_WIDTH);
		ntxtOffsetWidth.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetWidth.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				presenter.setOffsetWidth(arg2);
			}
		});
		HBox.setMargin(ntxtOffsetWidth, new Insets(0, 40, 0, 20));
		hboxWidth.getChildren().add(lblOffsetWidth);
		hboxWidth.getChildren().add(ntxtOffsetWidth);
		
		HBox hboxBtn = new HBox();
		txtBtnOk = new Text();
		txtBtnOk.setText(Translator.getTranslation(CONTINUE));
		txtBtnOk.getStyleClass().add(TeachView.CSS_CLASS_TEACH_BUTTON_TEXT);
		btnOk = new Button();
		btnOk.getStyleClass().add(TeachView.CSS_CLASS_TEACH_BUTTON);
		btnOk.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnOk.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.clickedOk();
			}
			
		});
		btnOk.setGraphic(txtBtnOk);
		hboxBtn.getChildren().add(btnOk);
		hboxBtn.setPrefWidth(FORM_CONTENT_WIDTH);
		hboxBtn.setMinWidth(FORM_CONTENT_WIDTH);
		hboxBtn.setMaxWidth(FORM_CONTENT_WIDTH);
		hboxBtn.setAlignment(Pos.CENTER_RIGHT);
		HBox.setMargin(btnOk, new Insets(20, 40, 0, 20));
		formPane.getChildren().add(lblGeneralInfo);
		formPane.getChildren().add(hboxLength);
		formPane.getChildren().add(hboxWidth);
		formPane.getChildren().add(hboxBtn);
		getChildren().add(keyboardPane);
		getChildren().add(formPane);
	}
	
	public void setKeyboardView(final Node node) {
		keyboardPane.getChildren().clear();
		keyboardPane.getChildren().add(node);
	}
	
	public void closeKeyboardView(final Node node) {
		keyboardPane.getChildren().remove(node);
	}
	
	public void setTextFieldListener(final TextFieldListener listener) {
		ntxtOffsetLength.setFocusListener(listener);
		ntxtOffsetWidth.setFocusListener(listener);
	}
}
