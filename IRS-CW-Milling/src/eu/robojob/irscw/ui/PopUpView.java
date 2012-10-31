package eu.robojob.irscw.ui;

import javafx.scene.layout.StackPane;
import eu.robojob.irscw.util.Translator;

public class PopUpView<T extends AbstractPopUpPresenter> extends StackPane {
	
	private double topLeftX;
	private double topLeftY;
	
	private double width;
	private double height;
	
	protected T presenter;
	protected Translator translator;
	
	public PopUpView(double topLeftX, double topLeftY, double width, double height) {
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.width = width;
		this.height = height;
		this.translator = Translator.getInstance();
		build();
		this.setTranslateX(topLeftX);
		this.setTranslateY(topLeftY);
	}
	
	protected void build() {
		this.setPrefSize(width, height);
		this.setMaxSize(width, height);
		this.getStyleClass().add("popup");
	}
	
	public void setPresenter(T presenter) {
		this.presenter = presenter;
	}
	
	public double getTopLeftX() {
		return topLeftX;
	}
	
	public double getTopLeftY() {
		return topLeftY;
	}
}
