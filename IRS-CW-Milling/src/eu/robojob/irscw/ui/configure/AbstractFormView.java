package eu.robojob.irscw.ui.configure;

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

public abstract class AbstractFormView<T extends AbstractFormPresenter<?, ?>> extends GridPane {

	private T presenter;
	
	private static final int ICON_WIDTH = 20;
	private static final int ICON_MARGIN = 6;
	
	private static final String CSS_CLASS_FORM_BUTTON_ICON = "form-button-icon";
	private static final String CSS_CLASS_FORM_BUTTON_LABEL = "form-button-label";
	private static final String CSS_CLASS_FORM_BUTTON_PANEL = "form-button-panel";
	private static final String CSS_CLASS_FORM_BUTTON = "form-button";
	
	public AbstractFormView() {
		super();
		setAlignment(Pos.CENTER);
		this.setPrefWidth(ConfigureView.WIDTH - ConfigureView.WIDTH_BOTTOM_LEFT);
		this.setPrefHeight(ConfigureView.HEIGHT_BOTTOM);
	}
	
	protected abstract void build();
	
	public abstract void setTextFieldListener(TextFieldListener listener);
	
	public void setPresenter(final T presenter) {
		this.presenter = presenter;
	}
	
	public T getPresenter() {
		return presenter;
	}
	
	public Button createButton(final String iconPath, final String iconClass, final String text, final double width, final double height, final EventHandler<ActionEvent> handler, final double iconWidth) {
		Button button = new Button();
		HBox hbox = new HBox();
		StackPane iconPane = new StackPane();
		SVGPath icon = new SVGPath();
		icon.setContent(iconPath);
		icon.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_ICON, iconClass);
		hbox.setAlignment(Pos.CENTER_LEFT);
		iconPane.getChildren().add(icon);
		iconPane.setPrefSize(iconWidth + 2 * ICON_MARGIN, height);
		hbox.getChildren().add(iconPane);
		Label label = new Label(text);
		label.getStyleClass().add(CSS_CLASS_FORM_BUTTON_LABEL);
		label.setPrefSize(width - iconWidth - 3 * ICON_MARGIN, height);
		label.setAlignment(Pos.CENTER);
		HBox.setMargin(label, new Insets(0, ICON_MARGIN, 0, 0));
		hbox.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		hbox.setPrefSize(width, height);
		hbox.getStyleClass().add(CSS_CLASS_FORM_BUTTON_PANEL);
		button.setOnAction(handler);
		button.setGraphic(hbox);
		button.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
		return button;
	}
	
	public Button createButton(final String iconPath, final String iconClass, final String text, final double width, final double height, final EventHandler<ActionEvent> handler) {
		return createButton(iconPath, iconClass, text, width, height, handler, ICON_WIDTH);
	}
	
	public abstract void refresh();
}
