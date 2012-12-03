package eu.robojob.irscw.ui.configure.process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.util.UIConstants;

public class ProcessOpenView extends AbstractFormView<ProcessOpenPresenter> {
	
	private ListView<String> lvProcesses;
	private Button btnLoad;
	
	private static String openIconPath = "M 10 0 C 4.4775 0 0 4.4762499 0 10 C 0 15.52125 4.4775 20 10 20 C 15.5225 20 20 15.52125 20 10 C 20 4.4762499 15.5225 0 10 0 z M 10 5 L 14.96875 9.96875 L 11.25 9.96875 L 11.25 15 L 8.75 15 L 8.75 9.96875 L 5 9.96875 L 10 5 z ";

	private static final double LIST_VIEW_WIDTH = 350;
	private static final double LIST_VIEW_HEIGHT = 175;
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3.5;
	
	public ProcessOpenView() {
		build();
	}

	@Override
	protected void build() {
		lvProcesses = new ListView<String>();
		lvProcesses.setPrefSize(LIST_VIEW_WIDTH, LIST_VIEW_HEIGHT);
		lvProcesses.getStyleClass().add("processes-list-view");
		HBox btnHBox = new HBox();
		btnLoad = createButton(openIconPath, "btn-load", translator.getTranslation("load"), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		btnHBox.getChildren().add(btnLoad);
		btnHBox.setAlignment(Pos.CENTER_RIGHT);
		HBox.setMargin(btnLoad, new Insets(20, 0, 0, 0));
		int row = 0;
		int column = 0;
		add(lvProcesses, column++, row);
		column = 0;
		row++;
		add(btnHBox, column++, row);
		ObservableList<String> listItems = FXCollections.observableArrayList("Mazak Private Show Demo 1", "Mazak Private Show Demo 2 (onder)", "Mazak Private Show Demo 2 (boven)");
		lvProcesses.setItems(listItems);
	}

	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
}
