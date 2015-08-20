package eu.robojob.millassist.ui.general.device.stacking.pallet;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.external.device.stacking.stackplate.StackPlateStackingPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.shape.IDrawableObject;
import eu.robojob.millassist.util.SizeManager;

public class PalletLayoutView<T extends AbstractFormPresenter<?, ?>> extends AbstractFormView<T> {

    private Pallet pallet;

    private Group group;
    private Pane root;
    private VBox contentBox;
    private Rectangle unloadPalletRect;
    private static final String BACKGROUND_PALLET = "m 80.541455,97.355046 0.114276,69.676074 4.660273,5.01548 109.716416,-0.11214 4.51083,-4.50062 0.008,-70.02663 -4.51377,-4.480269 -109.983224,-0.135723 z";
    private static final String PALLET_BKG_CSS = "pallet-bkg";
    private static final String CSS_CLASS_UNLOAD_PALLET = "pallet-bkg";
    private static final String CSS_CLASS_GRIDPLATE = "gridplate";
    private static final String CSS_CLASS_AMOUNT = "amount-text";

    private float LAYOUT_VIEWPORT_WIDTH = 500.0f;
    private float LAYOUT_VIEWPORT_HEIGHT = SizeManager.HEIGHT_BOTTOM * 0.9f;

    private static final int PADDING = 15;

    public PalletLayoutView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void build() {
        Platform.runLater(new Thread() {
            @Override
            public void run() {
                setCache(false);
                if (group != null) {
                    group.getChildren().clear();
                }

                if (root != null) {
                    root.getChildren().clear();
                    getChildren().remove(root);
                }

                root = null;
                getContents().getChildren().clear();

                group = null;
                group = new Group();
                group.setCache(true);

                SVGPath palletBkg = new SVGPath();
                palletBkg.setContent(BACKGROUND_PALLET);
                palletBkg.getStyleClass().add(PALLET_BKG_CSS);
                palletBkg.getTransforms().add(new Scale(8, 8, 100, 100));
                unloadPalletRect = new Rectangle(0, 0, pallet.getPalletLayout().getPalletLength(), pallet
                        .getPalletLayout().getPalletWidth());
                unloadPalletRect.getStyleClass().add(CSS_CLASS_UNLOAD_PALLET);
                group.getChildren().add(unloadPalletRect);

                // ADD grid
                if (pallet.getGridLayout().getStackingPositions().size() > 0) {
                    GridPlateLayout layout = (GridPlateLayout) pallet.getGridLayout();
                    group.getChildren().add(getGridPlateView(layout.getGridPlate()));
                    configureWorkPieces();
                }

                if (LAYOUT_VIEWPORT_WIDTH / pallet.getPalletLayout().getPalletLength() < LAYOUT_VIEWPORT_HEIGHT
                        / pallet.getPalletLayout().getPalletWidth()) {
                    Scale s = new Scale(LAYOUT_VIEWPORT_WIDTH / group.getBoundsInParent().getWidth(),
                            LAYOUT_VIEWPORT_WIDTH / pallet.getPalletLayout().getPalletLength()
                                    * pallet.getPalletLayout().getPalletWidth() / group.getBoundsInParent().getHeight());
                    group.getTransforms().add(s);
                } else {
                    Scale s = new Scale(LAYOUT_VIEWPORT_HEIGHT / pallet.getPalletLayout().getPalletWidth()
                            * pallet.getPalletLayout().getPalletLength() / group.getBoundsInParent().getWidth(),
                            LAYOUT_VIEWPORT_HEIGHT / group.getBoundsInParent().getHeight());
                    group.getTransforms().add(s);
                }

                root = new Pane();
                root.setPrefSize(600, 350);
                root.getChildren().clear();
                root.getChildren().add(group);
                group.setLayoutX(0 - group.getBoundsInParent().getMinX());
                group.setLayoutY(0 - group.getBoundsInParent().getMinY());

                contentBox = new VBox();
                contentBox.getChildren().add(root);
                contentBox.setSpacing(20);
                setPadding(new Insets(PADDING));
                getContents().add(contentBox, 0, 0);
            }
        });
    }

    private synchronized void configureWorkPieces() {
        for (StackPlateStackingPosition stackingPosition : pallet.getGridLayout().getStackingPositions()) {
            if (stackingPosition.getWorkPiece() != null) {
                IDrawableObject workPieceRepre = stackingPosition.getWorkPiece().getRepresentation();
                Shape workPiece = workPieceRepre.createShape();
                Group group2 = new Group();
                group2.getChildren().add(workPiece);
                if (workPieceRepre.needsMarkers()) {
                    Rectangle marker = createMarker(stackingPosition, workPieceRepre);
                    group2.getChildren().add(marker);
                }
                // LayoutX - the origin of the piece (left bottom corner)
                group2.setLayoutX(stackingPosition.getPosition().getX() - workPieceRepre.getXCorrection());
                // LayoutY - the origin of the piece (left bottom corner)
                group2.setLayoutY(pallet.getPalletLayout().getPalletWidth() - stackingPosition.getPosition().getY() - workPieceRepre.getYCorrection());
                group2.setRotate(stackingPosition.getOrientation() * -1);
                group.getChildren().add(group2);
                Text txtAmount = new Text(stackingPosition.getAmount() + "");
                txtAmount.getStyleClass().add(CSS_CLASS_AMOUNT);
                txtAmount.setX(stackingPosition.getPosition().getX() - txtAmount.getBoundsInParent().getWidth() / 2);
                txtAmount.setY(pallet.getPalletLayout().getPalletWidth() - stackingPosition.getPosition().getY()
                        + txtAmount.getBoundsInParent().getHeight() / 2);
                group.getChildren().add(txtAmount);
            }
        }
    }

    private Rectangle createMarker(StackPlateStackingPosition stackingPosition, IDrawableObject workPiece) {
        Rectangle marker = null;
        // if (!basicStackPlate.hasGridPlate()) {
        if (stackingPosition.getOrientation() == 0) {
            marker = workPiece.createMarker(false);
            if (pallet.getHorizontalR() < 0) {
                marker.setTranslateX(workPiece.getXTranslationMarker());
            } else {
                marker.setTranslateX(5);
            }
        } else if (stackingPosition.getOrientation() == 90) {
            marker = workPiece.createMarker(true);
            if (pallet.getHorizontalR() < 0) {
                marker.setTranslateY(workPiece.getYTranslationMarker());
            } else {
                marker.setTranslateY(5);
            }
        } else {
            float deltaR = pallet.getTiltedR() - pallet.getHorizontalR();
            if (deltaR > 0) {
                if (isRightGrid(stackingPosition.getOrientation())) {
                    marker = workPiece.createMarker(true);
                    if (pallet.getTiltedR() > 0 && stackingPosition.getOrientation() < 90) {
                        marker.setTranslateY(workPiece.getYTranslationMarker());
                    } else {
                        marker.setTranslateY(5);
                    }
                } else {
                    marker = workPiece.createMarker(false);
                    if (pallet.getTiltedR() > 0 && stackingPosition.getOrientation() < 90) {
                        marker.setTranslateX(5);
                    } else {
                        marker.setTranslateX(workPiece.getXTranslationMarker());
                    }
                }
            } else {
                if (isRightGrid(stackingPosition.getOrientation())) {
                    marker = workPiece.createMarker(false);
                    if (pallet.getTiltedR() > 0 && stackingPosition.getOrientation() < 90) {
                        marker.setTranslateX(5);
                    } else {
                        marker.setTranslateX(workPiece.getXTranslationMarker());
                    }
                } else {
                    marker = workPiece.createMarker(true);
                    if (pallet.getTiltedR() > 0 && stackingPosition.getOrientation() < 90) {
                        marker.setTranslateY(5);
                    } else {
                        marker.setTranslateY(workPiece.getYTranslationMarker());
                    }
                }
            }
        }
        return marker;
    }

    private boolean isRightGrid(double orientation) {
        if (orientation >= 90) {
            return true;
        } else {
            return false;
        }
    }

    private Shape getGridPlateView(GridPlate gridPlate) {
        Shape gridPlateShape = gridPlate.createShape();
        gridPlateShape.getStyleClass().add(CSS_CLASS_GRIDPLATE);
        gridPlateShape.relocate(gridPlate.getOffsetX(),
                (pallet.getPalletLayout().getPalletWidth() - (gridPlate.getOffsetY() + gridPlate.getHeight())));
        return gridPlateShape;
    }

    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    public Pallet getPallet() {
        return this.pallet;
    }

    public void setPallet(Pallet pallet) {
        this.pallet = pallet;
    }

}
