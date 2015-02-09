package eu.robojob.millassist.ui.general.dialog;

import eu.robojob.millassist.ui.general.AbstractFormView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class NodeOverlayView extends AbstractDialogView<NodeOverlayPresenter> {

	private BorderPane vboxContents;
	
	private static final int WIDTH_PLATE = 700;
	private static final int HEIGHT_PLATE = WIDTH_PLATE / (1000/486) ;
	
	private Button deleteButton;
	private static final String deleteIconPath = "M 3.6875 1.5 C 3.6321551 1.5180584 3.5743859 1.5512948 3.53125 1.59375 L 1.59375 3.53125 C 1.423759 3.7005604 1.4751389 4.0379792 1.6875 4.25 L 7.46875 10 L 1.6875 15.75 C 1.4751389 15.962191 1.423759 16.297908 1.59375 16.46875 L 3.53125 18.40625 C 3.7020918 18.577772 4.0386598 18.55526 4.25 18.34375 L 10 12.5625 L 15.75 18.34375 C 15.962021 18.55526 16.297908 18.577772 16.46875 18.40625 L 18.40625 16.46875 C 18.57556 16.297908 18.524691 15.962191 18.3125 15.75 L 12.53125 10 L 18.3125 4.25 C 18.524691 4.0379792 18.57556 3.7005604 18.40625 3.53125 L 16.46875 1.59375 C 16.297908 1.4244396 15.962021 1.4759897 15.75 1.6875 L 10 7.46875 L 4.25 1.6875 C 4.1439896 1.5809791 4.0004088 1.5136129 3.875 1.5 C 3.8124658 1.4931936 3.7428449 1.4819416 3.6875 1.5 z ";
	
	public NodeOverlayView(Node node) {
		super("");
		double scalingFactorX = WIDTH_PLATE / node.getBoundsInLocal().getWidth();
		double scalingFactorY = HEIGHT_PLATE / node.getBoundsInLocal().getHeight();
		//Scaling only if too big
		if (scalingFactorX < 1 || scalingFactorY < 1) {
			if (scalingFactorX < scalingFactorY) {
				node.setScaleX(scalingFactorX);
				node.setScaleY(scalingFactorX);
			} else {
				node.setScaleX(scalingFactorY);
				node.setScaleY(scalingFactorY);
			}
		}
		vboxContents.setCenter(node);
		BorderPane.setAlignment(node, Pos.CENTER);
	}

	@Override
	protected Node getContents() {
		
		deleteButton = AbstractFormView.createButton(deleteIconPath, null, "", 32, 0, new EventHandler<ActionEvent>() {
			 @Override 
			 public void handle(ActionEvent actionEvent) {
				 getPresenter().setResult(true);
			 }
		});
		vboxContents = new BorderPane();
		vboxContents.setPrefWidth(WIDTH_PLATE);
		deleteButton.getGraphic().setScaleX(2);
		deleteButton.getGraphic().setScaleY(2);
		vboxContents.setPrefSize(WIDTH_PLATE, HEIGHT_PLATE);
		vboxContents.setMinSize(WIDTH_PLATE, HEIGHT_PLATE);
		vboxContents.setMaxSize(WIDTH_PLATE, HEIGHT_PLATE);
		vboxContents.setTop(deleteButton);
		BorderPane.setAlignment(deleteButton, Pos.CENTER);
		BorderPane.setMargin(deleteButton, new Insets(0, 0, 100, 0));
		setDialogSize(WIDTH_PLATE, HEIGHT_PLATE);
		return vboxContents;
	}
}
