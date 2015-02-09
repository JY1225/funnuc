package eu.robojob.millassist.ui.controls;

import eu.robojob.millassist.ui.general.AbstractFormView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * This class represents a box containing buttons linked to different operations like save, save as, delete
 */
public class OperationBox extends StackPane {

	private Button btnSave, btnCopy, btnDelete;
	
	private static final int BTN_WIDTH = 120;
	private static final int BTN_HEIGHT = 40;
	
	private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";	
	
	public OperationBox() {
		build();
	}
	
	private void build() {
		initComponents();
		getChildren().addAll(btnSave, btnCopy, btnDelete);
		this.setAlignment(Pos.CENTER);
		StackPane.setAlignment(btnDelete, Pos.CENTER_LEFT);
		StackPane.setAlignment(btnCopy, Pos.CENTER);
		StackPane.setAlignment(btnSave, Pos.CENTER_RIGHT);
		setPadding(new Insets(10,20,10,20));
	}
	
	private void initComponents() {
		btnSave = AbstractFormView.createButton(SAVE_PATH, null, "Save", BTN_WIDTH, BTN_HEIGHT, null);
		btnCopy = AbstractFormView.createButton(ADD_PATH, null, "Save As", BTN_WIDTH+40, BTN_HEIGHT, null);
		btnDelete = AbstractFormView.createButton(DELETE_ICON_PATH, null, "Delete", BTN_WIDTH, BTN_HEIGHT, null);
		btnDelete.getStyleClass().add("delete-btn");
	}
	
	public void addSaveAction(final EventHandler<ActionEvent> handler) {
		btnSave.setOnAction(handler);
	}
	
	public void addDeleteAction(final EventHandler<ActionEvent> handler) {
		btnDelete.setOnAction(handler);
	}
	
	public void addSaveAsAction(final EventHandler<ActionEvent> handler) {
		btnCopy.setOnAction(handler);
	}
	
	public void disableSaveButton(boolean disabled) {
		btnSave.setDisable(disabled);
	}
	
	public void disableDeleteButton(boolean disabled) {
		btnDelete.setDisable(disabled);
	}
	
	public void disableSaveAsButton(boolean disabled) {
		btnCopy.setDisable(disabled);
	}
}
