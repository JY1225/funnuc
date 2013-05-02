package eu.robojob.irscw.ui.controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class IconFlowSelectorItem extends VBox {
		
	private static final double IMG_WIDTH = 100;
	private static final double IMG_HEIGHT = 90;
	private static final double WIDTH = 120;
	private static final double HEIGHT = 120;
	
	//TODO improve css names (just item, ...)
	private static final String CSS_CLASS_ICONFLOW_ITEM = "iconflow-item";
	private static final String CSS_CLASS_ICONFLOW_ITEM_SELECTED = "iconflow-item-selected";
	private static final String CSS_CLASS_ICONFLOW_ITEM_ICON = "iconflow-item-icon";
	private static final String CSS_CLASS_ICONFLOW_ITEM_ICON_SELECTED = "iconflow-item-icon-selected";
	private static final String CSS_CLASS_ICONFLOW_ITEM_LABEL = "iconflow-item-lbl";
		
	private String iconUrl;
	private String name;
	private ImageView imgvwIconVw;
	private Image imgIcon;
	private Label lblName;
	
	public IconFlowSelectorItem(final int index, final String name, final String iconUrl) {
		this.iconUrl = iconUrl;
		this.name = name;
		build();
		setSelected(false);
		setPrefSize(WIDTH, HEIGHT);
		setMinSize(WIDTH, HEIGHT);
		setMaxSize(WIDTH, HEIGHT);
		setAlignment(Pos.CENTER);
	}
	
	private void build() {
		this.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM);
		if (iconUrl != null) {
			imgIcon = new Image(iconUrl, IMG_WIDTH, IMG_HEIGHT, true, true);
			imgvwIconVw = new ImageView(imgIcon);
			imgvwIconVw.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM_ICON);
			this.getChildren().add(imgvwIconVw);
		}
		if (name != null) {
			lblName = new Label(name);
			lblName.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM_LABEL);
			this.getChildren().add(lblName);
		}
		this.getStyleClass().remove(CSS_CLASS_ICONFLOW_ITEM_SELECTED);
		this.setPadding(new Insets(3, 3, 3, 3));
	}

	public void setSelected(final boolean selected) {
		this.getStyleClass().remove(CSS_CLASS_ICONFLOW_ITEM_SELECTED);
		imgvwIconVw.getStyleClass().remove(CSS_CLASS_ICONFLOW_ITEM_ICON_SELECTED);
		if (selected) {
			this.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM_SELECTED);
			imgvwIconVw.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM_ICON_SELECTED);
		}
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(final String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
