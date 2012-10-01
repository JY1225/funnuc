package eu.robojob.irscw.ui.controls;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class IconFlowSelectorItem extends VBox {
	
	private String iconUrl;
	private String name;
	
	private ImageView imgvwIconVw;
	private Image imgIcon;
	
	private Label lblName;
	
	public IconFlowSelectorItem(int index, String name, String iconUrl) {
		this.iconUrl = iconUrl;
		this.name = name;
		build();
		setSelected(false);
	}
	
	private void build() {
		this.getStyleClass().add("iconflow-item");
		if (iconUrl != null) {
			imgIcon = new Image(iconUrl);
			imgvwIconVw = new ImageView(imgIcon);
			imgvwIconVw.getStyleClass().add("iconflow-item-icon");
			this.getChildren().add(imgvwIconVw);
		}
		if (name != null) {
			lblName = new Label(name);
			lblName.getStyleClass().add("iconflow-item-lbl");
			this.getChildren().add(lblName);
		}
		this.getStyleClass().remove("iconflow-item-selected");
	}

	public void setSelected(boolean selected) {
		this.getStyleClass().add("iconflow-item-selected");
	}
	
	
}
