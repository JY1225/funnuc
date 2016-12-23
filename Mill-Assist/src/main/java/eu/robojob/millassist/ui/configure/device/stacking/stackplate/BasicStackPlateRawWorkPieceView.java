package eu.robojob.millassist.ui.configure.device.stacking.stackplate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.ui.configure.device.stacking.AbstractRawWorkPiecePresenter;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.PropertyManager;
import eu.robojob.millassist.util.PropertyManager.Setting;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;

public class BasicStackPlateRawWorkPieceView extends AbstractFormView<AbstractRawWorkPiecePresenter<?>> {

    protected static final String WIDTH_ICON = "M 42.25 0.03125 A 0.30003001 0.30003001 0 0 0 42.21875 0.0625 A 0.30003001 0.30003001 0 0 0 42.1875 0.0625 A 0.30003001 0.30003001 0 0 0 42.15625 0.09375 A 0.30003001 0.30003001 0 0 0 42.03125 0.25 A 0.30003001 0.30003001 0 0 0 42 0.3125 A 0.30003001 0.30003001 0 0 0 42 0.34375 L 42 9.6875 L 9.9375 24.78125 L 24.625 31.78125 L 56.71875 16.625 A 0.30003001 0.30003001 0 0 0 56.78125 16.71875 A 0.30003001 0.30003001 0 0 0 56.90625 16.8125 A 0.30003001 0.30003001 0 0 0 57.03125 16.8125 A 0.30003001 0.30003001 0 0 0 57.21875 16.71875 A 0.30003001 0.30003001 0 0 0 57.28125 16.5 A 0.30003001 0.30003001 0 0 0 57.28125 16.46875 L 57.28125 6.9375 A 0.30003001 0.30003001 0 0 0 57.28125 6.8125 A 0.30003001 0.30003001 0 0 0 57.1875 6.6875 A 0.30003001 0.30003001 0 0 0 57 6.625 A 0.30003001 0.30003001 0 0 0 56.90625 6.65625 A 0.30003001 0.30003001 0 0 0 56.8125 6.6875 A 0.30003001 0.30003001 0 0 0 56.6875 6.9375 L 56.6875 9.4375 L 56.15625 9 L 53.15625 6.40625 L 52.34375 5.71875 L 52.71875 6.71875 L 53.03125 7.59375 L 46.6875 4.46875 L 47.34375 4.25 L 48.34375 3.875 L 47.28125 3.71875 L 43.375 3.125 L 42.59375 3 L 42.59375 0.34375 A 0.30003001 0.30003001 0 0 0 42.59375 0.3125 A 0.30003001 0.30003001 0 0 0 42.4375 0.0625 A 0.30003001 0.30003001 0 0 0 42.40625 0.0625 A 0.30003001 0.30003001 0 0 0 42.25 0.03125 z M 42.59375 3.125 L 43.125 3.59375 L 46.15625 6.1875 L 46.9375 6.875 L 46.59375 5.875 L 46.21875 4.90625 L 52.6875 8.0625 L 51.9375 8.34375 L 50.96875 8.6875 L 52 8.875 L 55.9375 9.5 L 56.6875 9.59375 L 56.6875 16.46875 A 0.30003001 0.30003001 0 0 0 56.6875 16.5 L 42.59375 9.875 L 42.59375 3.125 z M 57.21875 17.03125 L 24.9375 32.28125 L 24.9375 48.25 L 57.21875 33.0625 L 57.21875 17.03125 z M 9.5625 25.25 L 9.5625 41.28125 L 24.34375 48.25 L 24.34375 32.3125 L 9.5625 25.25 z";
    protected static final String LENGTH_ICON = "M 41.9375 -0.28125 A 0.30003 0.30003 0 0 0 41.90625 -0.25 A 0.30003 0.30003 0 0 0 41.875 -0.21875 A 0.30003 0.30003 0 0 0 41.75 -0.0625 A 0.30003 0.30003 0 0 0 41.71875 0.03125 L 41.71875 2.65625 L 40.625 2.78125 L 35.71875 3.4375 L 34.40625 3.625 L 35.625 4.09375 L 36.71875 4.53125 L 14.40625 15.0625 L 14.84375 13.84375 L 15.3125 12.625 L 14.3125 13.46875 L 10.5 16.625 L 9.875 17.125 L 9.875 15.21875 A 0.3750375 0.3750375 0 0 0 9.8125 15 A 0.3750375 0.3750375 0 0 0 9.4375 14.84375 A 0.3750375 0.3750375 0 0 0 9.125 15.21875 L 9.125 24.78125 A 0.3750375 0.3750375 0 0 0 9.875 24.78125 L 9.875 17.375 L 10.75 17.21875 L 11.25 17.15625 A 0.30189829 0.30189829 0 0 0 11.34375 17.15625 L 11.375 17.125 A 0.30189829 0.30189829 0 0 0 11.40625 17.125 L 15.6875 16.5625 L 17 16.375 L 15.75 15.90625 L 14.8125 15.53125 L 36.96875 5.0625 L 36.5625 6.15625 L 36.0625 7.40625 L 37.0625 6.5625 L 40.90625 3.375 L 41.71875 2.71875 L 41.71875 9.59375 A 0.30003 0.30003 0 0 0 41.71875 9.65625 A 0.30003 0.30003 0 0 0 41.78125 9.8125 L 10 24.78125 L 24.65625 31.78125 L 56.84375 16.5625 L 42.28125 9.71875 A 0.30003 0.30003 0 0 0 42.3125 9.65625 A 0.30003 0.30003 0 0 0 42.3125 9.59375 L 42.3125 0.03125 A 0.30003 0.30003 0 0 0 42.28125 -0.09375 A 0.30003 0.30003 0 0 0 42.28125 -0.125 A 0.30003 0.30003 0 0 0 42 -0.28125 A 0.30003 0.30003 0 0 0 41.9375 -0.28125 z M 57.28125 17.03125 L 25 32.28125 L 25 48.25 L 57.28125 33.0625 L 57.28125 17.03125 z M 9.59375 25.25 L 9.59375 41.28125 L 24.40625 48.25 L 24.40625 32.3125 L 9.59375 25.25 z";
    private static final String HEIGTH_ICON = "M 42.15625 9.625 L 10 24.75 L 24.65625 31.78125 L 56.875 16.53125 L 42.15625 9.625 z M 57.28125 17 L 25 32.25 L 25 48.25 L 57.28125 33.0625 L 57.28125 17 z M 0.3125 20.78125 A 0.3750375 0.3750375 0 0 0 0.15625 20.90625 A 0.3750375 0.3750375 0 0 0 0.34375 21.5 L 1.96875 22.25 L 1.75 23 L 0.5625 26.8125 L 0.25 27.78125 L 1.03125 27.0625 L 1.59375 26.53125 L 1.59375 32.5 L 1.03125 31.90625 L 0.3125 31.15625 L 0.59375 32.1875 L 1.59375 36.03125 L 1.78125 36.75 L 1.09375 36.40625 A 0.3750375 0.3750375 0 0 0 0.90625 36.375 A 0.38305158 0.38305158 0 0 0 0.75 37.125 L 9.4375 41.15625 A 0.37791833 0.37791833 0 0 0 9.625 41.1875 L 9.625 41.25 L 24.40625 48.21875 L 24.40625 32.28125 L 9.625 25.25 L 9.625 40.40625 L 1.875 36.78125 L 2.125 36.03125 L 3.28125 32.25 L 3.625 31.21875 L 2.84375 31.96875 L 2.21875 32.5625 L 2.21875 26.46875 L 2.8125 27.125 L 3.5625 27.875 L 3.28125 26.84375 L 2.28125 23 L 2.09375 22.3125 L 8.96875 25.53125 A 0.38173637 0.38173637 0 0 0 9.4375 24.96875 A 0.38173637 0.38173637 0 0 0 9.28125 24.84375 L 0.65625 20.8125 A 0.3750375 0.3750375 0 0 0 0.4375 20.78125 A 0.3750375 0.3750375 0 0 0 0.375 20.78125 A 0.3750375 0.3750375 0 0 0 0.34375 20.78125 A 0.3750375 0.3750375 0 0 0 0.3125 20.78125 z";
    private static final String HORIZONTAL_ICON = "M 3.3125 3.28125 L 3.3125 14.4375 L 20.0625 14.4375 L 20.0625 3.28125 L 3.3125 3.28125 z ";
    private static final String TILTED_ICON = "M 11.90625 0.03125 L 0.0625 11.875 L 7.96875 19.78125 L 19.8125 7.9375 L 11.90625 0.03125 z ";
    private static final String VERTICAL_ICON = "m 6.109375,17.234375 11.15625,0 0,-16.75 -11.15625,0 0,16.75 z";
    private static final String CYLINDER_DIAMETER = "m 8.4357379,16.70846 -0.015644,31.60486 c 0,1.83398 5.4596541,3.99378 12.0288061,3.99378 6.56915,0 11.900559,-2.2207 11.900559,-4.05469 l -0.0441,-31.48556 c -1.78515,1.45145 -6.433942,2.50296 -11.83022,2.50296 -5.509607,0 -10.311542,-1.07226 -12.0394961,-2.56135 z M 32.081029,14.60227 c 0,1.93077 -5.272392,3.53256 -11.683023,3.53256 -6.410638,0 -11.7873951,-1.567 -11.7873951,-3.49777 0,-1.93078 5.3767571,-3.49778 11.7873951,-3.49778 6.410631,0 11.683023,1.53222 11.683023,3.46299 z m -23.4103001,-0.02421 0,-10.2948892 M 32.018289,14.57536 l 0,-10.2416999 m -21.235196,1.41328 18.646656,0 0,0.21668 -18.646656,0 z m -0.309491,0.11481 c -0.46117,-0.00712 -1.3836751,0 -1.3836751,0 l 3.1787431,-0.85175 -0.880021,0.88003 c 0,0 -0.609922,-0.023566 -0.915047,-0.02828 z m 0.02376,0.0155 -1.3836661,0 3.1787421,0.85174 -0.880021,-0.88002 z m 19.757157,-0.005 1.38366,0 -3.178726,0.85174 0.880016,-0.88002 z m -0.0239,-0.0155 1.38366,0 -3.178725,-0.85175 0.880025,0.88003 z";
    private static final String CYLINDER_HEIGHT = "m 59.12577,42.48719 0,26.662364 -0.182734,0 0,-26.662364 z m -0.09784,-0.326464 c 0.007,-0.46117 0,-1.383675 0,-1.383675 l 0.85175,3.178743 -0.88003,-0.880021 c 0,0 0.0236,-0.609922 0.0283,-0.915047 z m -0.0155,0.02376 0,-1.383666 -0.85174,3.178742 0.88002,-0.880021 z m -0.0058,28.24891 0,1.38366 -0.85174,-3.178726 0.88002,0.880016 z m 0.0155,-0.0239 0,1.38366 0.85175,-3.178725 -0.88003,0.880025 z m -8.407166,1.723808 10.2417,0 m -10.401569,-31.720323 10.29489,0 M 50.17359,38.263197 c 0,1.930767 -5.272392,3.532557 -11.683023,3.532557 -6.410638,0 -11.787395,-1.567 -11.787395,-3.497767 0,-1.93078 5.376757,-3.49778 11.787395,-3.49778 6.410631,0 11.683023,1.53222 11.683023,3.46299 z m -23.645291,2.106187 -0.01564,31.60486 c 0,1.83398 5.459654,3.99378 12.028806,3.99378 6.56915,0 11.900559,-2.2207 11.900559,-4.05469 l -0.0441,-31.48556 c -1.78515,1.45145 -6.433942,2.50296 -11.83022,2.50296 -5.509607,0 -10.311542,-1.07226 -12.039496,-2.56135 z";

    private StackPane iconWidthPane;
    private SVGPath workPieceWidthPath, workPieceHeightPath, workPieceLengthPath, workPieceDiameterPath, workPieceCylinderHeightPath;
    private StackPane iconLengthPane;
    private StackPane iconHeightPane;

    private Label lblWorkPieceWidth;
    private Label lblWorkPieceLength;
    private Label lblWorkPieceHeight;
    private Label lblStudHeight;
    private Label lblWorkPieceWeight;
    private Label lblShape;

    private Label lblLayers;
    private Label lblWorkPieceAmount;
    private Button btnMaxAmount;

    private Label lblMaterial;
    private Button btnCalc;

    private NumericTextField ntxtWorkPieceWidth;
    private NumericTextField ntxtWorkPieceLength;
    private NumericTextField ntxtWorkPieceHeight;
    private NumericTextField ntxtStudHeight;
    private NumericTextField ntxtWorkPieceWeight;
    private IntegerTextField itxtWorkPieceAmount;
    private IntegerTextField itxtLayers;

    private HBox orientationsBox;
    private Button btnHorizontal, btnTilted, btnVertical;
    private HBox materialsBox;
    private Button btnAl, btnFe, btnCu, btnOther;
    private HBox shapeBox;
    private Button btnCubic, btnCylindric;

    private Region spacer;

    protected static final int MAX_INTEGER_LENGTH = 6;
    private static final int HGAP = 10;
    private static final int VGAP = 0;
    private static final double BTN_WIDTH = 80;
    private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
    protected static final double ICON_PANE_WIDTH = 55;
    protected static final double ICON_PANE_HEIGHT = 40;

    protected static final String WIDTH = "BasicStackPlateWorkPieceView.width";
    protected static final String LENGTH = "BasicStackPlateWorkPieceView.length";
    private static final String DIAMETER = "BasicStackPlateWorkPieceView.diameter";
    private static final String HEIGHT = "BasicStackPlateWorkPieceView.height";
    private static final String WEIGHT = "BasicStackPlateWorkPieceView.weight";
    private static final String HORIZONTAL = "BasicStackPlateWorkPieceView.horizontal";
    private static final String TILTED = "BasicStackPlateWorkPieceView.tilted";
    private static final String AMOUNT = "BasicStackPlateWorkPieceView.amount";
    private static final String MAX = "BasicStackPlateWorkPieceView.max";
    private static final String MATERIAL = "BasicStackPlateWorkPieceView.material";
    private static final String CALC = "BasicStackPlateWorkPieceView.calc";
    private static final String OTHER = "BasicStackPlateWorkPieceView.other";
    private static final String STACKS = "BasicStackPlateWorkPieceView.layers";
    private static final String STUDS = "BasicStackPlateWorkPieceView.studHeight";
    private static final String SHAPE = "BasicStackPlateWorkPieceView.shape";
    private static final String CYLINDER_SHAPE = "BasicStackPlateWorkPieceView.cylinderShape";
    private static final String CUBIC_SHAPE = "BasicStackPlateWorkPieceView.cubicShape";

    private static final String CSS_CLASS_BUTTON_ORIENTATION = "btn-orientation";
    private static final String CSS_CLASS_CYLINDER = "cylinder-svg";

    @Override
    public void build() {

        int row = 0;
        int column = 1;

        buildIcons();

        if (hasRoundPieces()) {
            buildShapeBox();
            getContents().add(lblShape, column++, row);
            getContents().add(shapeBox, column, row++, 4, 1);
        }
        column = 0;

        iconLengthPane = new StackPane();
        iconLengthPane.getChildren().add(workPieceLengthPath);
        iconLengthPane.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
        iconLengthPane.setMaxSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
        StackPane.setAlignment(workPieceLengthPath, Pos.BOTTOM_RIGHT);
        getContents().add(iconLengthPane, column++, row);
        lblWorkPieceLength = new Label(Translator.getTranslation(LENGTH));
        getContents().add(lblWorkPieceLength, column++, row);
        ntxtWorkPieceLength = new NumericTextField(MAX_INTEGER_LENGTH);
        ntxtWorkPieceLength.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtWorkPieceLength.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtWorkPieceLength.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
                getPresenter().changedLength(newValue);
            }
        });
        getContents().add(ntxtWorkPieceLength, column++, row);
        column = 0;
        row++;

        workPieceWidthPath = new SVGPath();
        workPieceWidthPath.setContent(WIDTH_ICON);
        workPieceWidthPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
        iconWidthPane = new StackPane();
        iconWidthPane.getChildren().add(workPieceWidthPath);
        iconWidthPane.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
        iconWidthPane.setMaxSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
        StackPane.setAlignment(workPieceWidthPath, Pos.BOTTOM_RIGHT);
        getContents().add(iconWidthPane, column++, row);
        lblWorkPieceWidth = new Label(Translator.getTranslation(WIDTH));
        getContents().add(lblWorkPieceWidth, column++, row);
        ntxtWorkPieceWidth = new NumericTextField(MAX_INTEGER_LENGTH);
        ntxtWorkPieceWidth.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtWorkPieceWidth.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtWorkPieceWidth.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
                getPresenter().changedWidth(newValue);
            }
        });
        getContents().add(ntxtWorkPieceWidth, column++, row);
        column = 0;
        row++;


        iconHeightPane = new StackPane();
        iconHeightPane.getChildren().add(workPieceHeightPath);
        iconHeightPane.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
        iconHeightPane.setMaxSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
        StackPane.setAlignment(workPieceHeightPath, Pos.BOTTOM_RIGHT);
        getContents().add(iconHeightPane, column++, row);
        lblWorkPieceHeight = new Label(Translator.getTranslation(HEIGHT));
        getContents().add(lblWorkPieceHeight, column++, row);
        ntxtWorkPieceHeight = new NumericTextField(MAX_INTEGER_LENGTH);
        ntxtWorkPieceHeight.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtWorkPieceHeight.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtWorkPieceHeight.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
                getPresenter().changedHeight(newValue);
            }
        });
        getContents().add(ntxtWorkPieceHeight, column++, row);

        column = 0;
        row++;

        materialsBox = new HBox();
        materialsBox.setAlignment(Pos.CENTER_LEFT);
        lblMaterial = new Label(Translator.getTranslation(MATERIAL));
        lblMaterial.getStyleClass().add(CSS_CLASS_FORM_LABEL);
        GridPane.setMargin(materialsBox, new Insets(5, 0, 0, 0));
        btnAl = createButton("Al", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                getPresenter().changedMaterial(Material.AL);
            }
        });
        btnAl.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
        materialsBox.getChildren().add(btnAl);
        btnCu = createButton("Cu", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                getPresenter().changedMaterial(Material.CU);
            }
        });
        btnCu.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
        materialsBox.getChildren().add(btnCu);
        btnFe = createButton("Fe", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                getPresenter().changedMaterial(Material.FE);
            }
        });
        btnFe.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
        materialsBox.getChildren().add(btnFe);
        btnOther = createButton(Translator.getTranslation(OTHER), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                getPresenter().changedMaterial(Material.OTHER);
            }
        });
        btnOther.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
        materialsBox.setMaxWidth(4 * BTN_WIDTH);
        materialsBox.getChildren().add(btnOther);

        column++;
        getContents().add(lblMaterial, column++, row);
        getContents().add(materialsBox, column++, row, 5, 1);
        GridPane.setMargin(materialsBox, new Insets(10, 0, 0, 0));

        column = 0; row++;
        Region spacer3 = new Region();
        spacer3.setMinSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
        spacer3.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
        spacer3.setMaxSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
        getContents().add(spacer3, column++, row);
        GridPane.setMargin(spacer3, new Insets(10, 0, 10, 0));
        lblStudHeight = new Label(Translator.getTranslation(STUDS));
        ntxtStudHeight = new NumericTextField(5);
        ntxtStudHeight.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtStudHeight.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtStudHeight.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
                getPresenter().changedStudHeight(newValue);
            }
        });
        getContents().add(lblStudHeight, column++, row);
        getContents().add(ntxtStudHeight, column++, row);


        column = 4; //row++;
        lblWorkPieceWeight = new Label(Translator.getTranslation(WEIGHT));
        getContents().add(lblWorkPieceWeight, column++, row);
        ntxtWorkPieceWeight = new NumericTextField(MAX_INTEGER_LENGTH);
        ntxtWorkPieceWeight.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtWorkPieceWeight.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        ntxtWorkPieceWeight.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
                getPresenter().changedWeight(newValue);
            }
        });
        getContents().add(ntxtWorkPieceWeight, column++, row);
        btnCalc = createButton(Translator.getTranslation(CALC), UIConstants.BUTTON_HEIGHT * 1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().recalcWeight();
            }
        });
        getContents().add(btnCalc, column++, row, 2, 1);

        spacer = new Region();
        spacer.setPrefSize(18, BTN_HEIGHT);
        getContents().add(spacer, 3, 0);
        orientationsBox = new HBox();
        btnHorizontal = createButton(HORIZONTAL_ICON, CSS_CLASS_BUTTON_ORIENTATION, Translator.getTranslation(HORIZONTAL), BTN_WIDTH*0.9, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                getPresenter().changedOrientation(0);
            }
        });
        btnHorizontal.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
        orientationsBox.getChildren().add(btnHorizontal);
        btnTilted = createButton(TILTED_ICON, CSS_CLASS_BUTTON_ORIENTATION, Translator.getTranslation(TILTED), BTN_WIDTH*0.9, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                getPresenter().changedOrientation(45);
            }
        });
        btnTilted.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
        orientationsBox.getChildren().add(btnTilted);
        orientationsBox.setAlignment(Pos.CENTER_LEFT);
        btnVertical = createButton(VERTICAL_ICON, CSS_CLASS_BUTTON_ORIENTATION, "90°", BTN_WIDTH*0.9, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                getPresenter().changedOrientation(90);
            }
        });
        btnVertical.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
        orientationsBox.getChildren().add(btnVertical);
        orientationsBox.setAlignment(Pos.CENTER_LEFT);
        orientationsBox.setPrefWidth(3 * BTN_WIDTH * 0.9);
        orientationsBox.setMaxWidth(3 * BTN_WIDTH * 0.9);
        orientationsBox.setMinWidth(3 * BTN_WIDTH * 0.9);

        row = 0;
        if (hasRoundPieces()) {
            row = 1;
        }
        column = 4;
        getContents().add(orientationsBox, column, row, 3, 1);
        column = 4;
        row++;

        lblLayers = new Label(Translator.getTranslation(STACKS));
        getContents().add(lblLayers, column++, row);
        itxtLayers = new IntegerTextField(MAX_INTEGER_LENGTH);
        itxtLayers.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        itxtLayers.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        itxtLayers.setOnChange(new ChangeListener<Integer>() {
            @Override
            public void changed(final ObservableValue<? extends Integer> observable, final Integer oldValue, final Integer newValue) {
                getPresenter().changedLayers(newValue);
            }
        });
        getContents().add(itxtLayers, column++, row);

        column = 4;
        row++;
        lblWorkPieceAmount = new Label(Translator.getTranslation(AMOUNT));
        getContents().add(lblWorkPieceAmount, column++, row);
        itxtWorkPieceAmount = new IntegerTextField(MAX_INTEGER_LENGTH);
        itxtWorkPieceAmount.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        itxtWorkPieceAmount.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
        itxtWorkPieceAmount.setOnChange(new ChangeListener<Integer>() {
            @Override
            public void changed(final ObservableValue<? extends Integer> observable, final Integer oldValue, final Integer newValue) {
                getPresenter().changedAmount(newValue);
            }
        });
        getContents().add(itxtWorkPieceAmount, column++, row);

        btnMaxAmount = new Button();
        Label lblMaxAmount = new Label(Translator.getTranslation(MAX));
        lblMaxAmount.getStyleClass().add(CSS_CLASS_FORM_BUTTON_LABEL);
        btnMaxAmount.setGraphic(lblMaxAmount);
        btnMaxAmount.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
        btnMaxAmount.setPrefSize(UIConstants.BUTTON_HEIGHT * 1.5, UIConstants.BUTTON_HEIGHT);
        btnMaxAmount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().setMaxAmount();
            }
        });
        getContents().add(btnMaxAmount, column++, row);
        getContents().setHgap(HGAP);
        getContents().setVgap(VGAP);
        hideNotification();

        refresh();
    }

    private void buildIcons() {
        workPieceLengthPath = new SVGPath();
        workPieceLengthPath.setContent(LENGTH_ICON);
        workPieceLengthPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
        workPieceHeightPath = new SVGPath();
        workPieceHeightPath.setContent(HEIGTH_ICON);
        workPieceHeightPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
        workPieceWidthPath = new SVGPath();
        workPieceWidthPath.setContent(WIDTH_ICON);
        workPieceWidthPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
        workPieceDiameterPath = new SVGPath();
        workPieceDiameterPath.setContent(CYLINDER_DIAMETER);
        workPieceDiameterPath.getStyleClass().addAll(CSS_CLASS_FORM_ICON, CSS_CLASS_CYLINDER);
        workPieceCylinderHeightPath = new SVGPath();
        workPieceCylinderHeightPath.setContent(CYLINDER_HEIGHT);
        workPieceCylinderHeightPath.getStyleClass().addAll(CSS_CLASS_FORM_ICON, CSS_CLASS_CYLINDER);
    }

    private void buildShapeBox() {
        lblShape = new Label(Translator.getTranslation(SHAPE));
        shapeBox = new HBox();
        shapeBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(shapeBox, new Insets(5, 0, 0, 0));
        btnCubic = createButton(Translator.getTranslation(CUBIC_SHAPE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                setShape(WorkPieceShape.CUBIC);
                getPresenter().changedShape(WorkPieceShape.CUBIC);
                showShapeView(WorkPieceShape.CUBIC);
            }
        });
        btnCubic.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
        shapeBox.getChildren().add(btnCubic);
        btnCylindric = createButton(Translator.getTranslation(CYLINDER_SHAPE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                setShape(WorkPieceShape.CYLINDRICAL);
                getPresenter().changedShape(WorkPieceShape.CYLINDRICAL);
                showShapeView(WorkPieceShape.CYLINDRICAL);
            }
        });
        btnCylindric.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
        shapeBox.getChildren().add(btnCylindric);
        shapeBox.setMaxWidth(2 * BTN_WIDTH);
        shapeBox.setAlignment(Pos.CENTER);
    }

    @Override
    public void setTextFieldListener(final TextInputControlListener listener) {
        itxtWorkPieceAmount.setFocusListener(listener);
        itxtLayers.setFocusListener(listener);
        ntxtWorkPieceHeight.setFocusListener(listener);
        ntxtWorkPieceLength.setFocusListener(listener);
        ntxtWorkPieceWeight.setFocusListener(listener);
        ntxtWorkPieceWidth.setFocusListener(listener);
        ntxtStudHeight.setFocusListener(listener);
    }

    @Override
    public void refresh() {
        AbstractStackPlateDeviceSettings settings = getPresenter().getDeviceSettings();
        setDimensions(settings.getRawWorkPiece().getDimensions());
        itxtWorkPieceAmount.setText("" + settings.getAmount());
        itxtLayers.setText("" + settings.getLayers());
        if (!getPresenter().hasGridPlate()) {
            setOrientation(settings.getOrientation());
        }
        setWeight(settings.getRawWorkPiece().getMaterial(), settings.getRawWorkPiece().getWeight());
        ntxtStudHeight.setText(settings.getStudHeight() + "");
        showOrientation(!getPresenter().hasGridPlate());
        if (hasRoundPieces()) {
            setShape(settings.getRawWorkPiece().getShape());
            showShapeView(settings.getRawWorkPiece().getShape());
        }
    }

    private void setOrientation(final float orientation) {
        btnHorizontal.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnTilted.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnVertical.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        if (orientation == 0) {
            btnHorizontal.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
        } else if (orientation == 45){
            btnTilted.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
        } else {
            btnVertical.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
        }
    }

    private void setDimensions(final IWorkPieceDimensions workPieceDimensions) {
        float widthDiameter = workPieceDimensions.getDimension(Dimensions.WIDTH);
        if (widthDiameter == -1) {
            widthDiameter = workPieceDimensions.getDimension(Dimensions.DIAMETER);
        }
        if (widthDiameter > 0) {
            ntxtWorkPieceWidth.setText("" + widthDiameter);
        } else {
            ntxtWorkPieceWidth.setText("");
        }
        float length = workPieceDimensions.getDimension(Dimensions.LENGTH);
        if (length > 0) {
            ntxtWorkPieceLength.setText("" + length);
        } else {
            ntxtWorkPieceLength.setText("");
        }
        float height = workPieceDimensions.getDimension(Dimensions.HEIGHT);
        if (height > 0) {
            ntxtWorkPieceHeight.setText("" + height);
        } else {
            ntxtWorkPieceHeight.setText("");
        }
    }

    public void setWeight(final WorkPiece.Material material, final float weight) {
        ntxtWorkPieceWeight.setText("" + weight);
        ntxtWorkPieceWeight.cleanText();
        btnAl.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnCu.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnFe.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnOther.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        if (material.equals(Material.OTHER)) {
            btnOther.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
            btnCalc.setDisable(true);
        } else {
            btnCalc.setDisable(false);
            if (material.equals(Material.AL)) {
                btnAl.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
            } else if (material.equals(Material.CU)) {
                btnCu.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
            } else if (material.equals(Material.FE)) {
                btnFe.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
            }
        }
    }

    private void showOrientation(final boolean flag) {
        btnVertical.setVisible(flag);
        btnHorizontal.setVisible(flag);
        btnTilted.setVisible(flag);
    }

    private void setShape(final WorkPieceShape shape) {
        btnCubic.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnCylindric.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        if (shape.equals(WorkPieceShape.CUBIC)) {
            btnCubic.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
        } else {
            btnCylindric.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
        }
    }

    private boolean hasRoundPieces() {
        return PropertyManager.hasSettingValue(Setting.ROUND_PIECES, "true");
    }

    private void showShapeView(final WorkPieceShape shape) {
        if (shape.equals(WorkPieceShape.CUBIC)) {
            iconWidthPane.getChildren().clear();
            iconWidthPane.getChildren().add(workPieceWidthPath);
            iconHeightPane.getChildren().clear();
            iconHeightPane.getChildren().add(workPieceHeightPath);
            lblWorkPieceWidth.setText(Translator.getTranslation(WIDTH));
            setCubicFieldsVisible(true);
        } else if (shape.equals(WorkPieceShape.CYLINDRICAL)) {
            iconWidthPane.getChildren().clear();
            iconWidthPane.getChildren().add(workPieceDiameterPath);
            iconHeightPane.getChildren().clear();
            iconHeightPane.getChildren().add(workPieceCylinderHeightPath);
            lblWorkPieceWidth.setText(Translator.getTranslation(DIAMETER));
            setCubicFieldsVisible(false);
        }
    }

    private void setCubicFieldsVisible(final boolean visible) {
        orientationsBox.setVisible(visible);
        orientationsBox.setManaged(visible);
        lblWorkPieceLength.setVisible(visible);
        ntxtWorkPieceLength.setVisible(visible);
        iconLengthPane.setVisible(visible);
        lblWorkPieceLength.setManaged(visible);
        ntxtWorkPieceLength.setManaged(visible);
        iconLengthPane.setManaged(visible);
    }
}
