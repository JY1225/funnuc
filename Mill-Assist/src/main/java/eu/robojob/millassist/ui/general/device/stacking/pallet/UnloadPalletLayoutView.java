package eu.robojob.millassist.ui.general.device.stacking.pallet;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.external.device.stacking.pallet.PalletStackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.ui.configure.device.stacking.pallet.UnloadPalletLayoutPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.shape.IDrawableObject;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;

public class UnloadPalletLayoutView<T extends AbstractFormPresenter<?, ?>> extends AbstractFormView<T> {

    private UnloadPallet unloadPallet;
    private Label nbOfPieces;
    private Label nbOfPiecesValue;
    
    private Group group;
    private Pane root;
    private VBox contentBox;
    private GridPane controls;
    private Rectangle unloadPalletRect;
    
    private Label typeLabel;
    private HBox typeBox;
    
    private Label orientationLabel;
    private HBox orientationBox;
    
    private Button optimalButton;
    private Button horizontalButton;
    private Button verticalButton;
    private Button shiftedButton;
    private Button notShiftedButton;
    
    private boolean controlsHidden = false;
    
    private float width;
    
    private static final String BACKGROUND_PALLET= "m 80.541455,97.355046 0.114276,69.676074 4.660273,5.01548 109.716416,-0.11214 4.51083,-4.50062 0.008,-70.02663 -4.51377,-4.480269 -109.983224,-0.135723 z";
    private static final String PALLET_BKG_CSS = "pallet-bkg";
    private static final String CSS_CLASS_UNLOAD_PALLET = "pallet-bkg";
    private static final String CSS_CLASS_AMOUNT = "amount-text";
    private static final String LAYOUT_TYPE = "UnloadPalletLayoutView.LayoutType";
    private static final String HORIZONTAL = "UnloadPalletLayoutView.Horizontal";
    private static final String VERTICAL = "UnloadPalletLayoutView.Vertical";
    private static final String ORIENTATION = "UnloadPalletLayoutView.Orientation";
    private static final String OPTIMAL = "UnloadPalletLayoutView.Optimal";
    private static final String SHIFTED = "UnloadPalletLayoutView.Shifted";
    private static final String NOT_SHIFTED = "UnloadPalletLayoutView.NotShifted";
    private static final double BTN_WIDTH = 80;
    private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
    
    private final float LAYOUT_VIEWPORT_WIDTH = 500.0f;
    private final float LAYOUT_VIEWPORT_HEIGHT = 320.0f;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void build() {
        nbOfPieces = new Label("Number of pieces:");
        nbOfPiecesValue = new Label();
        contentBox = new VBox();
        controls = new GridPane();
        Platform.runLater(new Thread() {
            @Override
            public void run() {
                setCache(false);
                
                if (group != null) {
                    group.getChildren().clear();
                }
                group = null;
                
                if (root != null) {
                    root.getChildren().clear();
                    getChildren().remove(root);
                }
                
                root = null;
                getContents().getChildren().clear();
                
                group = new Group();
                group.setCache(true);
                if(!controlsHidden) {
                    buildShapeBox();
                }
                
                SVGPath palletBkg = new SVGPath();
                palletBkg.setContent(BACKGROUND_PALLET);
                palletBkg.getStyleClass().add(PALLET_BKG_CSS);
                palletBkg.getTransforms().add(new Scale(8,8, 100,100));
                unloadPalletRect = new Rectangle(0, 0, unloadPallet.getLayout().getPalletLength(),unloadPallet.getLayout().getPalletWidth());
                unloadPalletRect.getStyleClass().add(CSS_CLASS_UNLOAD_PALLET);
                group.getChildren().add(unloadPalletRect);
                configureWorkPieces();
                if(LAYOUT_VIEWPORT_WIDTH / unloadPallet.getLayout().getPalletLength() < LAYOUT_VIEWPORT_HEIGHT /unloadPallet.getLayout().getPalletWidth()) {
                    Scale s = new Scale(LAYOUT_VIEWPORT_WIDTH / group.getBoundsInParent().getWidth(), LAYOUT_VIEWPORT_WIDTH / unloadPallet.getLayout().getPalletLength() *unloadPallet.getLayout().getPalletWidth() / group.getBoundsInParent().getHeight());
                    group.getTransforms().add(s);
                }
                else {
                    Scale s = new Scale(LAYOUT_VIEWPORT_HEIGHT /unloadPallet.getLayout().getPalletWidth() * unloadPallet.getLayout().getPalletLength() / group.getBoundsInParent().getWidth(), LAYOUT_VIEWPORT_HEIGHT/ group.getBoundsInParent().getHeight());
                    group.getTransforms().add(s);
                }
                
                
                root = new Pane();
                root.setPrefSize(600, 350);
                root.getChildren().clear();
                root.getChildren().add(group);
                
                group.setLayoutX(0 - group.getBoundsInParent().getMinX());
                group.setLayoutY(0 - group.getBoundsInParent().getMinY());
                nbOfPiecesValue.setText(unloadPallet.getLayout().getStackingPositions().size()+"");
                
                int row = 0;
                int column = 0;
                if(!controlsHidden) {
                    controls.add(typeLabel, column, row);
                    controls.add(typeBox, column+1, row);
                    row++;
                    if(unloadPallet.getFinishedWorkPiece().getShape() == WorkPieceShape.CYLINDRICAL) {
                        controls.add(orientationLabel, column, row);
                        controls.add(orientationBox, column+1, row);
                        row++;
                    }
                    controls.add(nbOfPieces,column, row);
                    controls.add(nbOfPiecesValue,column+1, row);
                    row++;
                    controls.setVgap(10);
                    controls.setHgap(10);
                    contentBox.getChildren().add(controls);
                }
                
                contentBox.getChildren().add(root);
                contentBox.setSpacing(20);
                getContents().add(contentBox, 0, 0);
                if(!controlsHidden) {
                    setLayoutTypeButtonValues();
                }
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        //No textfields on this screen
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        //The whole screen is refreshed!
        unloadPallet.recalculateLayout();
        unloadPallet.notifyLayoutChanged();
    }

    public UnloadPallet getUnloadPallet() {
        return unloadPallet;
    }


    public void setUnloadPallet(UnloadPallet unloadPallet) {
        this.unloadPallet = unloadPallet;
        this.width = unloadPallet.getLayout().getPalletWidth();
    }
    
    /**
     * Draw the work pieces on the unload pallet.
     */
    private synchronized void configureWorkPieces() {
        for (PalletStackingPosition stackingPosition : unloadPallet.getLayout().getStackingPositions()) {
            if (stackingPosition.getWorkPiece() != null) {
                IDrawableObject workPieceRepre = stackingPosition.getWorkPiece().getRepresentation();
                Shape workPiece = workPieceRepre.createShape();
                if(stackingPosition.getAmount() == 0) {
                    workPiece.setStroke(Color.RED);
                    workPiece.setStrokeWidth(2);
                    workPiece.getStrokeDashArray().addAll(20.0,10.0,20.0,10.0);
                    workPiece.getStyleClass().add("placeholder");
                }
                Group group2 = new Group();
                group2.getChildren().add(workPiece);
                if (workPieceRepre.needsMarkers()) {
                    Rectangle marker = createMarker(workPieceRepre);
                    group2.getChildren().add(marker);
                }
                //LayoutX - the origin of the piece (left bottom corner)
                group2.setLayoutX(stackingPosition.getPosition().getX() - workPieceRepre.getXCorrection());
                //LayoutY - the origin of the piece (left bottom corner)
                group2.setLayoutY(width - stackingPosition.getPosition().getY() - workPieceRepre.getYCorrection());
                                group.getChildren().add(group2);
                if(unloadPallet.getLayout().isRotate90()) {
                    group2.setRotate(90*-1);
                }
                Text txtAmount = new Text(stackingPosition.getAmount() + "");
                txtAmount.getStyleClass().add(CSS_CLASS_AMOUNT);
                if(stackingPosition.getAmount() == 0) {
                    txtAmount.getStyleClass().add("placeholder");
                }
                txtAmount.setX(stackingPosition.getPosition().getX() - txtAmount.getBoundsInParent().getWidth()/2);
                txtAmount.setY(width - stackingPosition.getPosition().getY() + txtAmount.getBoundsInParent().getHeight()/2);
                group.getChildren().add(txtAmount);
            }
        }
    }
    
    /**
     * Create and position the marker on the work piece representation.
     * @param workPieceRepre The work piece representation on which a marker is added
     * @return Rectangle The marker to add
     */
    private Rectangle createMarker(IDrawableObject workPieceRepre) {
        Rectangle marker = workPieceRepre.createMarker(false);
        if(unloadPallet.getLayout().isRotate90()) {
            if(unloadPallet.getLayout().getVerticalR() == -90) {
                marker.setTranslateX(workPieceRepre.getXTranslationMarker() - 10);
            }
            else {
                marker.setTranslateX(10);
            }
        } else {
            if(unloadPallet.getLayout().getHorizontalR() == 180) {
                marker.setTranslateX(workPieceRepre.getXTranslationMarker() - 10);
            }
            else {
                marker.setTranslateX(10);
            }
        }
        return marker;
    }
    
    
    /**
     * Create the buttons to determine the layout type of the unload pallet.
     */
    private void buildShapeBox() {
        typeLabel = new Label(Translator.getTranslation(LAYOUT_TYPE));
        typeBox = new HBox();
        typeBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(typeBox, new Insets(5, 0, 0, 0));
        optimalButton = createButton(Translator.getTranslation(OPTIMAL), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                ((UnloadPalletLayoutPresenter)getPresenter()).updateLayoutType(PalletLayoutType.OPTIMAL);
            }
        });
        optimalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
        typeBox.getChildren().add(optimalButton);
        if(unloadPallet.getFinishedWorkPiece().getShape() == WorkPieceShape.CUBIC) {
            horizontalButton = createButton(Translator.getTranslation(HORIZONTAL), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    ((UnloadPalletLayoutPresenter)getPresenter()).updateLayoutType(PalletLayoutType.NOT_SHIFTED_HORIZONTAL);
                }
            });
            horizontalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
            typeBox.getChildren().add(horizontalButton);
            verticalButton = createButton(Translator.getTranslation(VERTICAL), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    ((UnloadPalletLayoutPresenter)getPresenter()).updateLayoutType(PalletLayoutType.NOT_SHIFTED_VERTICAL);
                }
            });
            typeBox.getChildren().add(verticalButton);
        }
        if(unloadPallet.getFinishedWorkPiece().getShape() == WorkPieceShape.CYLINDRICAL) {
            orientationLabel = new Label(Translator.getTranslation(ORIENTATION));
            orientationBox = new HBox();
            shiftedButton= createButton(Translator.getTranslation(SHIFTED), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    ((UnloadPalletLayoutPresenter)getPresenter()).updateLayoutType(PalletLayoutType.SHIFTED_HORIZONTAL);
                }
            });
            shiftedButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
            typeBox.getChildren().add(shiftedButton);
            notShiftedButton = createButton(Translator.getTranslation(NOT_SHIFTED), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    ((UnloadPalletLayoutPresenter)getPresenter()).updateLayoutType(PalletLayoutType.NOT_SHIFTED_HORIZONTAL);
                }
            });
            typeBox.getChildren().add(notShiftedButton);
            
            horizontalButton = createButton(Translator.getTranslation(HORIZONTAL), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    ((UnloadPalletLayoutPresenter)getPresenter()).updateLayoutType(PalletLayoutType.SHIFTED_HORIZONTAL);
                }
            });
            horizontalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
            orientationBox.getChildren().add(horizontalButton);
            verticalButton = createButton(Translator.getTranslation(VERTICAL), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    ((UnloadPalletLayoutPresenter)getPresenter()).updateLayoutType(PalletLayoutType.SHIFTED_VERTICAL);
                }
            });
            horizontalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
            orientationBox.getChildren().add(verticalButton);
            verticalButton.setMaxWidth(2 * BTN_WIDTH);
            typeBox.setAlignment(Pos.CENTER);
        }
        
        typeBox.setMaxWidth(3 * BTN_WIDTH);
        typeBox.setAlignment(Pos.CENTER);
    }
    
    /**
     * Set the values of the buttons indicating the layout type of the unload pallet.
     */
    private void setLayoutTypeButtonValues() {
        verticalButton.setDisable(false);
        horizontalButton.setDisable(false);
        if(unloadPallet.getFinishedWorkPiece().getShape() == WorkPieceShape.CUBIC) {
            optimalButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            horizontalButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            verticalButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.OPTIMAL || unloadPallet.getLayout().getLayoutType() == PalletLayoutType.SHIFTED_HORIZONTAL || unloadPallet.getLayout().getLayoutType() == PalletLayoutType.SHIFTED_VERTICAL) {
                if(!optimalButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    optimalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            } else if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.NOT_SHIFTED_HORIZONTAL) {
                if(!horizontalButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    horizontalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            } else if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.NOT_SHIFTED_VERTICAL){
                if(!verticalButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    verticalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            }
        } else if(unloadPallet.getFinishedWorkPiece().getShape() == WorkPieceShape.CYLINDRICAL) {
            optimalButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            shiftedButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            notShiftedButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.OPTIMAL) {
                if(!optimalButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    optimalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
                verticalButton.setDisable(true);
                horizontalButton.setDisable(true);
            } else if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.SHIFTED_HORIZONTAL) {
                if(!shiftedButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    shiftedButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
                if(!horizontalButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    horizontalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            } else if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.SHIFTED_VERTICAL) {
                if(!shiftedButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    shiftedButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
                if(!verticalButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    verticalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            } else if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.NOT_SHIFTED_VERTICAL || unloadPallet.getLayout().getLayoutType() == PalletLayoutType.NOT_SHIFTED_HORIZONTAL){
                if(!notShiftedButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    notShiftedButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
                verticalButton.setDisable(true);
                horizontalButton.setDisable(true);
            }
        }
    }
    
    /**
     * Show/Hide the controls to edit the pallet layout. Hide is done by the automate step by default.
     * @param controlsHidden Boolean indicating whether to hide the controls or not
     */
    public void setControlsHidden(final boolean controlsHidden) {
        this.controlsHidden = controlsHidden;
    }
    

}
