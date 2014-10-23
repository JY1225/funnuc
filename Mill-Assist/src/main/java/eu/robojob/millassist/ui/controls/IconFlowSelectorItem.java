package eu.robojob.millassist.ui.controls;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import eu.robojob.millassist.util.UIConstants;

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
	private String extraInfo;
	private ImageView imgvwIconVw;
	private Image imgIcon;
	private Label lblName;
	
	private boolean isSelected;
	
	public IconFlowSelectorItem(final int index, final String name, final String iconUrl, final String extraInfo) {
		this.iconUrl = iconUrl;
		this.name = name;
		this.extraInfo = extraInfo;
		this.isSelected = false;
		build();
		setSelected(false);
		setPrefSize(WIDTH, HEIGHT);
		setMinSize(WIDTH, HEIGHT);
		setMaxSize(WIDTH, HEIGHT);
		setAlignment(Pos.CENTER);
	}
	
	public IconFlowSelectorItem(final int index, final String name, final String iconUrl) {
		this(index, name, iconUrl, null);
	}
	
	private void build() {
		StackPane stPane = new StackPane();		
		this.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM);
		String url = iconUrl;
		if (url != null) {
			url = url.replace("file:///", "");
		}
		if ((url != null) && ((new File(url)).exists() || getClass().getClassLoader().getResource(url) != null)) {
			imgIcon = new Image(iconUrl, IMG_WIDTH, IMG_HEIGHT, true, true);
		} else {
			imgIcon = new Image(UIConstants.IMG_NOT_FOUND_URL, IMG_WIDTH, IMG_HEIGHT, true, true);
		}
		imgvwIconVw = new ImageView(imgIcon);
		imgvwIconVw.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM_ICON);
		stPane.getChildren().add(imgvwIconVw);
		if(extraInfo != null) {
			Text extraText = new Text(extraInfo);
			extraText.setFont(Font.font("Open Sans Semibold", FontWeight.SEMI_BOLD, 12));
			extraText.setFill(Color.WHITE);
			stPane.getChildren().add(extraText);
			StackPane.setAlignment(extraText, Pos.TOP_LEFT);
		}
		this.getChildren().add(stPane);
		if (name != null) {
			lblName = new Label(name);
			lblName.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM_LABEL);
			this.getChildren().add(lblName);
		}
		
		this.getStyleClass().remove(CSS_CLASS_ICONFLOW_ITEM_SELECTED);
		this.setPadding(new Insets(3, 3, 3, 3));
	}

	public void setSelected(final boolean selected) {
		this.isSelected = selected;
		this.getStyleClass().remove(CSS_CLASS_ICONFLOW_ITEM_SELECTED);
		imgvwIconVw.getStyleClass().remove(CSS_CLASS_ICONFLOW_ITEM_ICON_SELECTED);
		if (selected) {
			this.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM_SELECTED);
			imgvwIconVw.getStyleClass().add(CSS_CLASS_ICONFLOW_ITEM_ICON_SELECTED);
		}
	}
	
	public void setExtraInfo(final String extraInfo) {
		this.extraInfo = extraInfo;
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
	
	public boolean isSelected() {
		return this.isSelected;
	}

}
