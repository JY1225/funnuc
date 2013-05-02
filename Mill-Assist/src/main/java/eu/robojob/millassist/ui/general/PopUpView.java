package eu.robojob.millassist.ui.general;

import javafx.scene.layout.StackPane;

public class PopUpView<T extends AbstractPopUpPresenter<?>> extends StackPane {
	
	private double topLeftX;
	private double topLeftY;
	
	private double width;
	private double height;
	
	private T presenter;
	
	private static final String CSS_CLASS_POPUP = "popup";
	
	public PopUpView(final double topLeftX, final double topLeftY, final double width, final double height) {
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.width = width;
		this.height = height;
		build();
		this.setTranslateX(topLeftX);
		this.setTranslateY(topLeftY);
	}
	
	protected void build() {
		this.setPrefSize(width, height);
		this.setMaxSize(width, height);
		this.getStyleClass().add(CSS_CLASS_POPUP);
	}
	
	public void setPresenter(final T presenter) {
		this.presenter = presenter;
	}
	
	public T getPresenter() {
		return presenter;
	}
	
	public double getTopLeftX() {
		return topLeftX;
	}
	
	public double getTopLeftY() {
		return topLeftY;
	}
}
