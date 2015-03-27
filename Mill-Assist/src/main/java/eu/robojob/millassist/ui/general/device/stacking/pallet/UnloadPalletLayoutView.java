package eu.robojob.millassist.ui.general.device.stacking.pallet;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.ui.configure.device.stacking.pallet.UnloadPalletLayoutPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.shape.IDrawableObject;
import eu.robojob.millassist.util.SizeManager;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;

public class UnloadPalletLayoutView<T extends AbstractFormPresenter<?, ?>> extends AbstractFormView<UnloadPalletLayoutPresenter> {

    private UnloadPallet unloadPallet;
    private Label nbOfPieces;
    private Label nbOfPiecesValue;
    
    private Group group;
    private Pane root;
    private VBox contentBox;
    private GridPane controls;
    private Rectangle unloadPalletRect;
    
    private Label orientationLabel;
    private HBox orientationBox;
    
    private Button optimalButton;
    private Button horizontalButton;
    private Button verticalButton;
    private Button shiftedButton;
    private Button notShiftedButton;
    
    private float width;
    
    private static final String BACKGROUND_PALLET= "m 80.541455,97.355046 0.114276,69.676074 4.660273,5.01548 109.716416,-0.11214 4.51083,-4.50062 0.008,-70.02663 -4.51377,-4.480269 -109.983224,-0.135723 z";
    private static final String PALLET_BKG_CSS = "pallet-bkg";
    private static final String CSS_CLASS_UNLOAD_PALLET = "pallet-bkg";
    private static final String LAYOUT_TYPE = "UnloadPalletLayoutView.LayoutType";
    private static final String HORIZONTAL = "UnloadPalletLayoutView.Horizontal";
    private static final String VERTICAL = "UnloadPalletLayoutView.Vertical";
    private static final String OPTIMAL = "UnloadPalletLayoutView.Optimal";
    private static final String SHIFTED = "UnloadPalletLayoutView.Shifted";
    private static final String NOT_SHIFTED = "UnloadPalletLayoutView.NotShifted";
    private static final double BTN_WIDTH = 80;
    private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
    
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
                
                buildShapeBox();
                
                SVGPath palletBkg = new SVGPath();
                palletBkg.setContent(BACKGROUND_PALLET);
                palletBkg.getStyleClass().add(PALLET_BKG_CSS);
                palletBkg.getTransforms().add(new Scale(8,8, 100,100));
                unloadPalletRect = new Rectangle(0, 0, unloadPallet.getLayout().getPalletLength(),unloadPallet.getLayout().getPalletWidth());
                unloadPalletRect.getStyleClass().add(CSS_CLASS_UNLOAD_PALLET);
                group.getChildren().add(unloadPalletRect);
                configureWorkPieces();
                Scale s = new Scale(600 / group.getBoundsInParent().getWidth(), 350 / group.getBoundsInParent().getHeight());
                group.getTransforms().add(s);
                
                root = new Pane();
                root.setPrefSize(600, 350);
                root.getChildren().clear();
                root.getChildren().add(group);      
                
                group.setLayoutX(0 - group.getBoundsInParent().getMinX());
                group.setLayoutY(0 - group.getBoundsInParent().getMinY());
                
                
                controls.add(orientationLabel, 0, 0);
                controls.add(orientationBox, 1, 0);
                controls.add(nbOfPieces,0, 1);
                controls.add(nbOfPiecesValue,1, 1);
                controls.setVgap(10);
                controls.setHgap(10);
                contentBox.getChildren().addAll(controls, root);
                contentBox.setSpacing(20);
                getContents().add(contentBox, 0, 0);
                setLayoutTypeButtonValues();
            }
        });
    }
    
    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void refresh() {
        unloadPallet.getLayout().calculateLayoutForWorkPiece(unloadPallet.getFinishedWorkPiece());
        unloadPallet.getLayout().initFinishedWorkPieces(unloadPallet.getFinishedWorkPiece());
        nbOfPiecesValue.setText(unloadPallet.getLayout().getStackingPositions().size()+"");
        
    }

    public UnloadPallet getUnloadPallet() {
        return unloadPallet;
    }


    public void setUnloadPallet(UnloadPallet unloadPallet) {
        this.unloadPallet = unloadPallet;
        this.width = unloadPallet.getLayout().getPalletWidth();
    }
    
    private synchronized void configureWorkPieces() {
        for (StackingPosition stackingPosition : unloadPallet.getLayout().getStackingPositions()) {
            if (stackingPosition.getWorkPiece() != null) {
                IDrawableObject workPieceRepre = stackingPosition.getWorkPiece().getRepresentation();
                Shape workPiece = workPieceRepre.createShape();
                Group group2 = new Group();
                group2.getChildren().add(workPiece);
                
                //LayoutX - the origin of the piece (left bottom corner)
                group2.setLayoutX(stackingPosition.getPosition().getX() - workPieceRepre.getXCorrection());
                //LayoutY - the origin of the piece (left bottom corner)
                group2.setLayoutY(width - stackingPosition.getPosition().getY() - workPieceRepre.getYCorrection());
                                group.getChildren().add(group2);
                if(unloadPallet.getLayout().isRotate90()) {
                    group2.setRotate(90*-1);
                }
            }
        }
    }
    
    private void buildShapeBox() {
        orientationLabel = new Label(Translator.getTranslation(LAYOUT_TYPE));
        orientationBox = new HBox();
        orientationBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(orientationBox, new Insets(5, 0, 0, 0));
        optimalButton = createButton(Translator.getTranslation(OPTIMAL), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                unloadPallet.getLayout().setLayoutType(PalletLayoutType.OPTIMAL);;
                getPresenter().layoutChanged();
            }
        });
        optimalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
        orientationBox.getChildren().add(optimalButton);
        if(unloadPallet.getFinishedWorkPiece().getShape() == WorkPieceShape.CUBIC) {
            horizontalButton = createButton(Translator.getTranslation(HORIZONTAL), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    unloadPallet.getLayout().setLayoutType(PalletLayoutType.HORIZONTAL);
                    getPresenter().layoutChanged();
                }
            });
            horizontalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
            orientationBox.getChildren().add(horizontalButton);
            verticalButton = createButton(Translator.getTranslation(VERTICAL), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    unloadPallet.getLayout().setLayoutType(PalletLayoutType.VERTICAL);
                    getPresenter().layoutChanged();
                }
            });
            orientationBox.getChildren().add(verticalButton);
        }
        if(unloadPallet.getFinishedWorkPiece().getShape() == WorkPieceShape.CYLINDRICAL) {
            shiftedButton= createButton(Translator.getTranslation(SHIFTED), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    unloadPallet.getLayout().setLayoutType(PalletLayoutType.SHIFTED);
                    getPresenter().layoutChanged();
                }
            });
            shiftedButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
            orientationBox.getChildren().add(shiftedButton);
            notShiftedButton = createButton(Translator.getTranslation(NOT_SHIFTED), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent event) {
                    unloadPallet.getLayout().setLayoutType(PalletLayoutType.VERTICAL);
                    getPresenter().layoutChanged();
                }
            });
            orientationBox.getChildren().add(notShiftedButton);
        }
        
        orientationBox.setMaxWidth(3 * BTN_WIDTH);
        orientationBox.setAlignment(Pos.CENTER);
    }
    
    private void setLayoutTypeButtonValues() {
        if(unloadPallet.getFinishedWorkPiece().getShape() == WorkPieceShape.CUBIC) {
            optimalButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            horizontalButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            verticalButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.OPTIMAL) {
                if(!optimalButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    optimalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            } else if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.HORIZONTAL) {
                if(!horizontalButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    horizontalButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            } else if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.VERTICAL){
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
            } else if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.SHIFTED) {
                if(!shiftedButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    shiftedButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            } else if(unloadPallet.getLayout().getLayoutType() == PalletLayoutType.VERTICAL || unloadPallet.getLayout().getLayoutType() == PalletLayoutType.HORIZONTAL){
                if(!notShiftedButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    notShiftedButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            }
        }
    }
    

}
