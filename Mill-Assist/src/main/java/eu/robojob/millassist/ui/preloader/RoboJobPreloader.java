package eu.robojob.millassist.ui.preloader;

import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class RoboJobPreloader extends BorderPane {

	private ImageView imageVw;
	private Image image;
	private Timeline tl;
	
	private static final String CSS_PRELOADER = "preloader";
	
	public RoboJobPreloader() {
		build();
	}
	
	private void build() {
		this.setPrefSize(800, 600);
		this.setMinSize(800, 600);
		this.setMaxSize(800, 600);
		getStyleClass().add(CSS_PRELOADER);
		image = new Image("images/robojoblogo.png", 402, 52, true, true);
		imageVw = new ImageView(image);
		
		Rectangle rect = new Rectangle();
		rect.setX(-26);
		rect.setY(-26);
		rect.setWidth(200);
		rect.setHeight(150);
		rect.getTransforms().add(new Rotate(105));
		
		rect.getStyleClass().add("shine");
		
		Pane rectPane = new Pane();
		rectPane.getChildren().add(rect);
		rectPane.setPrefSize(402, 52);
		rectPane.setMinSize(402, 52);
		rectPane.setMaxSize(402, 52);
		
		Pane rectBgPane = new Pane();
		rectBgPane.setPrefSize(402, 52);
		rectBgPane.setMinSize(402, 52);
		rectBgPane.setMaxSize(402, 52);
		
		rectBgPane.getChildren().add(rectPane);
		
		StackPane sp = new StackPane();
		sp.getChildren().add(imageVw);
		sp.getChildren().add(rectBgPane);
		sp.setPrefSize(402, 52);
		sp.setMinSize(402, 52);
		sp.setMaxSize(402, 52);
		
		TranslateTransition trans = new TranslateTransition(Duration.millis(1750));
		trans.setInterpolator(Interpolator.EASE_BOTH);
		trans.setNode(rect);
		trans.setCycleCount(Timeline.INDEFINITE);
		trans.setFromX(0);
		trans.setToX(500);
		
		trans.play();
		setCenter(sp);

		/*Spot light = new Spot();
		light.setY(26);
		light.setZ(100);
		light.setPointsAtY(26);
		light.setPointsAtZ(0);
		light.setSpecularExponent(4);
		Lighting l = new Lighting();
		l.setSpecularConstant(0.15);
		l.setSpecularExponent(2);
		l.setLight(light);
		l.setSurfaceScale(0.5);
		l.setDiffuseConstant(7);
		imageVw.setEffect(l);
	    
	    Bloom bloom = new Bloom();
        bloom.setThreshold(0.9);
        bloom.setInput(l);
      
        Reflection r = new Reflection();
        r.setTopOpacity(0.07);
        r.setBottomOpacity(0);
        r.setFraction(0.7);
        
        r.setInput(bloom);
        
	    imageVw.setEffect(r);
	    
	    tl = new Timeline();

	    tl.setCycleCount(Timeline.INDEFINITE);
	    tl.setAutoReverse(false);
	    KeyValue keyVal = new KeyValue(light.xProperty(), -200, Interpolator.LINEAR);
	    KeyValue keyVal2 = new KeyValue(light.pointsAtXProperty(), -200, Interpolator.LINEAR);
	    KeyFrame frame1 = new KeyFrame(new Duration(0), keyVal, keyVal2);
	    KeyValue keyVal10 = new KeyValue(light.xProperty(), 602, Interpolator.LINEAR);
	    KeyValue keyVal11 = new KeyValue(light.pointsAtXProperty(), 602, Interpolator.LINEAR);
	    KeyFrame frame2 = new KeyFrame(new Duration(1750), keyVal10, keyVal11);
	    tl.getKeyFrames().addAll(frame1, frame2);
	    
	    tl.play();*/
	}
	
	public void stopAnimation() {
		tl.stop();
	}

}
