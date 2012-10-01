package eu.robojob.irscw.ui.controls;

import javafx.geometry.Pos;
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
	
	private static final double IMG_WIDTH = 100;
	private static final double WIDTH = 130;
	private static final double HEIGHT = 130;
	
	public IconFlowSelectorItem(int index, String name, String iconUrl) {
		this.iconUrl = iconUrl;
		this.name = name;
		build();
		setSelected(false);
		setPrefSize(WIDTH, HEIGHT);
		setAlignment(Pos.CENTER);
	}
	
	private void build() {
		this.getStyleClass().add("iconflow-item");
		if (iconUrl != null) {
			imgIcon = new Image(iconUrl, IMG_WIDTH, 83, true, true);
			imgvwIconVw = new ImageView(imgIcon);
			imgvwIconVw.getStyleClass().add("iconflow-item-icon");
			this.getChildren().add(imgvwIconVw);
		}
		if (name != null) {
			lblName = new Label(name);
			lblName.setPrefWidth(WIDTH);
			lblName.getStyleClass().add("iconflow-item-lbl");
			this.getChildren().add(lblName);
		}
		this.getStyleClass().remove("iconflow-item-selected");
	}

	public void setSelected(boolean selected) {
		this.getStyleClass().add("iconflow-item-selected");
	}
	
	
}
