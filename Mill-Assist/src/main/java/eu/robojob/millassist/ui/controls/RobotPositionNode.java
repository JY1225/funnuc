package eu.robojob.millassist.ui.controls;

import eu.robojob.millassist.positioning.Config;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.RobotPosition;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class RobotPositionNode extends GridPane {
    
    private Label lblName;
    private String name;
    private Label cfgLabel;
    private Label lblX, lblY, lblZ, lblW, lblP, lblR;
    private NumericTextField ntxtX, ntxtY, ntxtZ, ntxtW, ntxtP, ntxtR;
    private static final int NTXT_LENGTH = 10;
    private static final String CSS_ROBOTPOSITION_NAME = "robotposition-name";
   
    public RobotPositionNode(String name, RobotPosition position) {
        initComponents();
        build();
        setPosition(position);
        setName(name);
    }
    
    private void initComponents() {
        ntxtX = new NumericTextField(NTXT_LENGTH);
        ntxtX.setEditable(false);
        ntxtX.setPrefWidth(100);
        ntxtX.setMaxWidth(100);
        ntxtY = new NumericTextField(NTXT_LENGTH);
        ntxtY.setEditable(false);
        ntxtY.setPrefWidth(100);
        ntxtY.setMaxWidth(100);
        ntxtZ = new NumericTextField(NTXT_LENGTH);
        ntxtZ.setEditable(false);
        ntxtZ.setPrefWidth(100);
        ntxtZ.setMaxWidth(100);
        ntxtW = new NumericTextField(NTXT_LENGTH);
        ntxtW.setEditable(false);
        ntxtW.setPrefWidth(100);
        ntxtW.setMaxWidth(100);
        ntxtP = new NumericTextField(NTXT_LENGTH);
        ntxtP.setEditable(false);
        ntxtP.setPrefWidth(100);
        ntxtP.setMaxWidth(100);
        ntxtR = new NumericTextField(NTXT_LENGTH);
        ntxtR.setEditable(false);
        ntxtR.setPrefWidth(100);
        ntxtR.setMaxWidth(100);
        lblX = new Label("X");
        lblY = new Label("Y");
        lblZ = new Label("Z");
        lblW = new Label("W");
        lblP = new Label("P");
        lblR = new Label("R");   
        lblName = new Label();
        cfgLabel = new Label();
        lblName.getStyleClass().add(CSS_ROBOTPOSITION_NAME);
    }
    
    private void build() {
        setVgap(10);
        setHgap(15);
        
        int column = 0;
        int row = 0;
        
        add(lblName, column, row, 4, 1);
        row++; column =0;
        add(lblX, column++, row);
        add(ntxtX, column++, row);
        add(lblY, column++, row);
        add(ntxtY, column++, row);
        add(lblZ, column++, row);
        add(ntxtZ, column++, row);
        row++;column = 0;
        add(lblW, column++, row);
        add(ntxtW, column++, row);
        add(lblP, column++, row);
        add(ntxtP, column++, row);
        add(lblR, column++, row);
        add(ntxtR, column++, row);
        row++; column = 0;
        add(cfgLabel, column, row, 2,1);

    }
    
    private void setName(String name) {
        this.name = name;
        lblName.setText(name);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setPosition(RobotPosition position) {
        Coordinates location = position.getPosition();
        ntxtX.setText("" + location.getX());
        ntxtY.setText("" + location.getY());
        ntxtZ.setText("" + location.getZ());
        ntxtW.setText("" + location.getW());
        ntxtP.setText("" + location.getP());
        ntxtR.setText("" + location.getR());
        setConfig(position.getConfiguration());
    }
    
    private void setConfig(Config config) {
        String configString = "CONF: ";
        if (config.getCfgFlip() == 1) {
            configString += "F";
        } else {
            configString += "N";
        }
        if (config.getCfgUp() == 1) {
            configString += "U";
        } else {
            configString += "D";
        }
        if (config.getCfgFront() == 1) {
            configString += "T";
        } else {
            configString += "B";
        }
        configString += " " + config.getCfgTurn1() + "" + config.getCfgTurn2() + "" + config.getCfgTurn3();
        cfgLabel.setText(configString);
    }

    public void setTextListeners(TextInputControlListener listener) {
        ntxtX.setFocusListener(listener);
        ntxtY.setFocusListener(listener);
        ntxtZ.setFocusListener(listener);
        ntxtW.setFocusListener(listener);
        ntxtP.setFocusListener(listener);
        ntxtR.setFocusListener(listener);
    }
}
