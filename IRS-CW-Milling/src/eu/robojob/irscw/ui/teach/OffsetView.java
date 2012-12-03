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
	private static final int INFO_LABEL_HEIGHT = 50;
	private static final int FORM_LABEL_WIDTH = 300;
	private static final int FORM_LABEL_HEIGHT = 40;
	
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BTN_HEIGHT = 40;
	
	private Translator translator = Translator.getInstance();
	
	private OffsetPresenter presenter;
	
	public OffsetView() {
		build();
	}
	
	public void setPresenter(OffsetPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		
		keyboardPane = new StackPane();
		formPane = new VBox();
		
		keyboardPane.setPrefWidth(230);
		HBox.setHgrow(formPane, Priority.ALWAYS);
		
		formPane.setAlignment(Pos.CENTER);
		formPane.setPrefSize(FORM_WIDTH, 300);
		setMargin(formPane, new Insets(0, 0, 0, 0));
		
		lblGeneralInfo = new Label();
		lblGeneralInfo.getStyleClass().add("teach-msg");
		lblGeneralInfo.setText(translator.getTranslation("offset-general-info"));
		lblGeneralInfo.setPrefSize(FORM_WIDTH, INFO_LABEL_HEIGHT);
		lblGeneralInfo.setWrapText(true);
		
		lblOffsetLength = new Label();
		lblOffsetLength.getStyleClass().add("teach-msg");
		lblOffsetLength.setText(translator.getTranslation("offset-length"));
		
		lblOffsetWidth = new Label();
		lblOffsetWidth.getStyleClass().add("teach-msg");
		lblOffsetWidth.setText(translator.getTranslation("offset-width"));
		
		HBox hboxLength = new HBox();
		hboxLength.setAlignment(Pos.CENTER_RIGHT);
		hboxLength.setPrefWidth(375);
		hboxLength.setMinWidth(375);
		hboxLength.setMaxWidth(375);
		ntxtOffsetLength = new NumericTextField(MAX_LENGTH);
		ntxtOffsetLength.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetLength.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetLength.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> arg0, Float arg1, Float arg2) {
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
		hboxWidth.setPrefWidth(375);
		hboxWidth.setMinWidth(375);
		hboxWidth.setMaxWidth(375);
		ntxtOffsetWidth.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetWidth.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> arg0, Float arg1, Float arg2) {
				presenter.setOffsetWidth(arg2);
			}
		});
		HBox.setMargin(ntxtOffsetWidth, new Insets(0, 40, 0, 20));
		hboxWidth.getChildren().add(lblOffsetWidth);
		hboxWidth.getChildren().add(ntxtOffsetWidth);
		
		HBox hboxBtn = new HBox();
		txtBtnOk = new Text();
		txtBtnOk.setText(translator.getTranslation("continue"));
		txtBtnOk.getStyleClass().add("teach-btn-text");
		btnOk = new Button();
		btnOk.getStyleClass().add("teach-btn");
		btnOk.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnOk.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.clickedOk();
			}
			
		});
		btnOk.setGraphic(txtBtnOk);
		hboxBtn.getChildren().add(btnOk);
		hboxBtn.setPrefWidth(375);
		hboxBtn.setMinWidth(375);
		hboxBtn.setMaxWidth(375);
		hboxBtn.setAlignment(Pos.CENTER_RIGHT);
		HBox.setMargin(btnOk, new Insets(20, 40, 0, 20));
		
		int rowIndex = 0;
		int columnIndex = 0;
		formPane.getChildren().add(lblGeneralInfo);
		rowIndex++;
		columnIndex = 0;
		formPane.getChildren().add(hboxLength);
		rowIndex++;
		columnIndex = 0;
		formPane.getChildren().add(hboxWidth);
		rowIndex++;
		columnIndex = 0;
		formPane.getChildren().add(hboxBtn);
		
		getChildren().add(keyboardPane);
		getChildren().add(formPane);
	}
	
	public void setKeyboardView(Node node) {
		keyboardPane.getChildren().clear();
		keyboardPane.getChildren().add(node);
	}
	
	public void closeKeyboardView(Node node) {
		keyboardPane.getChildren().remove(node);
	}
	
	public void setTextFieldListener(TextFieldListener listener) {
		ntxtOffsetLength.setFocusListener(listener);
		ntxtOffsetWidth.setFocusListener(listener);
	}
	
}
