package eu.robojob.irscw.ui.main.configure;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.util.Translator;

public abstract class AbstractFormView<T extends AbstractFormPresenter<?>> extends GridPane {

	protected T presenter;
	protected Translator translator = Translator.getInstance();
	
	private static final int ICON_WIDTH = 20;
	private static final int ICON_MARGIN = 6;
	
	public AbstractFormView() {
		super();
		build();
		setAlignment(Pos.CENTER);
		this.setPrefWidth(ConfigureView.WIDTH - ConfigureView.WIDTH_BOTTOM_LEFT);
		this.setPrefHeight(ConfigureView.HEIGHT_BOTTOM);
	}
	
	protected abstract void build();
	
	public abstract void setTextFieldListener(TextFieldListener listener);
	
	public void setPresenter(T presenter) {
		this.presenter = presenter;
	}
	
	public Button createButton(String iconPath, String iconClass, String text, double width, double height, EventHandler<ActionEvent> handler) {
		Button button = new Button();
		HBox hbox = new HBox();
		StackPane iconPane = new StackPane();
		SVGPath icon = new SVGPath();
		icon.setContent(iconPath);
		icon.getStyleClass().addAll("form-button-icon", iconClass);
		hbox.setAlignment(Pos.CENTER_LEFT);
		iconPane.getChildren().add(icon);
		iconPane.setPrefSize(ICON_WIDTH + 2* ICON_MARGIN, height);
		hbox.getChildren().add(iconPane);
		Label label = new Label(text);
		label.getStyleClass().add("form-button-label");
		label.setPrefSize(width - ICON_WIDTH - 3*ICON_MARGIN, height);
		label.setAlignment(Pos.CENTER);
		HBox.setMargin(label, new Insets(0, ICON_MARGIN, 0, 0));
		hbox.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		hbox.setPrefSize(width, height);
		hbox.getStyleClass().addAll("form-button-panel");
		button.setOnAction(handler);
		button.setGraphic(hbox);
		button.getStyleClass().add("form-button");
		return button;
	}
}
