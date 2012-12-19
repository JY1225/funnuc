package eu.robojob.irscw.ui;

import javafx.scene.layout.StackPane;

public class PopUpView<T extends AbstractPopUpPresenter<?>> extends StackPane {
	
	private double topLeftX;
	private double topLeftY;
	
	private double width;
	private double height;
	
	private T presenter;
	
	private static final String CLASSNAME = "popup";
	
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
		this.getStyleClass().add(CLASSNAME);
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
