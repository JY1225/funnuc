package eu.robojob.millassist.ui.general.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import eu.robojob.millassist.util.SizeManager;

public abstract class AbstractDialogView<T extends AbstractDialogPresenter<?, ?>> extends StackPane {
	
	private StackPane spOverlay;
	private VBox vBoxDialog;
	private T presenter;
	private HBox hBoxTitle;
	private Label lblTitle;
	private String title;
	private Node contents;
	private double height;
	
	private static final String CSS_CLASS_OVERLAY = "overlay";
	private static final String CSS_CLASS_DIALOG = "dialog";
	private static final String CSS_CLASS_TITLE = "dialog-title";
	private static final String CSS_CLASS_TITLE_LABEL = "dialog-title-label";
	
	private static final int PADDING = 10;
	protected static final int TITLE_HEIGHT = 40;
	
	public AbstractDialogView(String title, double height) {
	    this.height = height;
		this.title = title;
		build();
	}
	
	public void setPresenter(final T presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		vBoxDialog = new VBox();
		if (!title.equals("")) {
			// Title
			lblTitle = new Label();
			lblTitle.setText(title);
			lblTitle.getStyleClass().add(CSS_CLASS_TITLE_LABEL);
			lblTitle.setPrefHeight(TITLE_HEIGHT - PADDING);
			lblTitle.setPrefWidth(Double.MAX_VALUE - 2*PADDING);
			lblTitle.setTextAlignment(TextAlignment.CENTER);
			hBoxTitle = new HBox();
			hBoxTitle.setSpacing(PADDING);
			hBoxTitle.getChildren().addAll(lblTitle);
			hBoxTitle.getStyleClass().add(CSS_CLASS_TITLE);
			hBoxTitle.setAlignment(Pos.BOTTOM_CENTER);
			hBoxTitle.setPrefHeight(TITLE_HEIGHT);
			HBox.setHgrow(lblTitle, Priority.ALWAYS);
			vBoxDialog.getChildren().add(hBoxTitle);
			// Contents
			contents = getContents(height);
			vBoxDialog.getStyleClass().add(CSS_CLASS_DIALOG);
			vBoxDialog.getChildren().add(contents);
		} else {
			// Contents
			contents = getContents(height);
			vBoxDialog.getChildren().add(contents);
			vBoxDialog.setAlignment(Pos.CENTER);
		}
		spOverlay = new StackPane();
		spOverlay.getStyleClass().add(CSS_CLASS_OVERLAY);
		spOverlay.setPrefSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		spOverlay.setMaxSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		spOverlay.setMinSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		getChildren().addAll(spOverlay, vBoxDialog);
		StackPane.setAlignment(vBoxDialog, Pos.CENTER);
		setAlignment(Pos.CENTER);
	}
	
	protected abstract Node getContents(double height);
	
	public T getPresenter() {
		return presenter;
	}
	
	public void setTitle(final String title) {
		this.title = title;
		lblTitle.setText(title);
	}
	
	public void setDialogSize(final double width, final double height) {
		vBoxDialog.setPrefSize(width, height);
		vBoxDialog.setMaxSize(width, height);
		vBoxDialog.setMinSize(width, height);
		if (!title.equals("")) {
			lblTitle.setMinWidth(width);
			hBoxTitle.setPrefWidth(width);
		}
	}
	
	public void setDialogWidth(final double width) {
		vBoxDialog.setPrefWidth(width);
		vBoxDialog.setMaxWidth(width);
		vBoxDialog.setMinWidth(width);
		if (!title.equals("")) {
			lblTitle.setMinWidth(width);
		}
	}
	
	public void setPosition(final Pos position) {
		StackPane.setAlignment(vBoxDialog, position);
		StackPane.setMargin(vBoxDialog, new Insets(50, 0, 0, 0));
		setAlignment(position);
	}
	
	public void setPosition(final Pos position, final Insets insets) {
		StackPane.setAlignment(vBoxDialog, position);
		StackPane.setMargin(vBoxDialog, insets);
		setAlignment(position);
	}
}
