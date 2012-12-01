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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class OffsetView extends HBox {

	private StackPane keyboardPane;
	private GridPane formPane;
	
	private Label lblGeneralInfo;
	private Label lblOffsetLength;
	private Label lblOffsetWidth;
	private NumericTextField ntxtOffsetLength;
	private NumericTextField ntxtOffsetWidth;
	private Text txtBtnOk;
	private Button btnOk;
	
	private static final int MAX_LENGTH = 5;
		
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
		formPane = new GridPane();
		
		keyboardPane.setPrefWidth(200);
		HBox.setHgrow(formPane, Priority.ALWAYS);
		
		formPane.setAlignment(Pos.CENTER);
		formPane.setPrefSize(400, 300);
		setMargin(formPane, new Insets(0, 200, 0, 0));
		
		lblGeneralInfo = new Label();
		lblGeneralInfo.getStyleClass().add("teach-msg");
		lblGeneralInfo.setText(translator.getTranslation("offset-general-info"));
		
		lblOffsetLength = new Label();
		lblOffsetLength.getStyleClass().add("teach-msg");
		lblOffsetLength.setText(translator.getTranslation("offset-length"));
		
		lblOffsetWidth = new Label();
		lblOffsetWidth.getStyleClass().add("teach-msg");
		lblOffsetWidth.setText(translator.getTranslation("offset-width"));
		
		ntxtOffsetLength = new NumericTextField(MAX_LENGTH);
		ntxtOffsetLength.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetLength.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetLength.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> arg0, Float arg1, Float arg2) {
				presenter.setOffsetLength(arg1);
			}
		});
		
		ntxtOffsetWidth = new NumericTextField(MAX_LENGTH);
		ntxtOffsetWidth.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetWidth.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtOffsetWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> arg0, Float arg1, Float arg2) {
				presenter.setOffsetWidth(arg1);
			}
		});
		
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
		
		int rowIndex = 0;
		int columnIndex = 0;
		formPane.add(lblGeneralInfo, columnIndex++, rowIndex, 2, 1);
		rowIndex++;
		columnIndex = 0;
		formPane.add(lblOffsetLength, columnIndex++, rowIndex);
		formPane.add(ntxtOffsetLength, columnIndex++, rowIndex);
		rowIndex++;
		columnIndex = 0;
		formPane.add(lblOffsetWidth, columnIndex++, rowIndex);
		formPane.add(ntxtOffsetWidth, columnIndex++, rowIndex);
		rowIndex++;
		columnIndex = 0;
		formPane.add(btnOk, columnIndex++, rowIndex, 2, 1);
		
		getChildren().add(keyboardPane);
		getChildren().add(formPane);
	}
	
	public void setKeyboardView(Node node) {
		keyboardPane.getChildren().clear();
		keyboardPane.getChildren().add(node);
	}
	
	public void closeKeyboardView() {
		keyboardPane.getChildren().clear();
	}
	
	public void setTextFieldListener(TextFieldListener listener) {
		ntxtOffsetLength.setFocusListener(listener);
		ntxtOffsetWidth.setFocusListener(listener);
	}
	
}
