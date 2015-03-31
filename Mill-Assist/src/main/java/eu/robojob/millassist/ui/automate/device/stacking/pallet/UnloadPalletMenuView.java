package eu.robojob.millassist.ui.automate.device.stacking.pallet;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.util.Translator;

public class UnloadPalletMenuView extends AbstractMenuView<UnloadPalletMenuPresenter>{

    private static final String LAYOUT_ICON = "m 4.3125,0.12499999 c -1.9028305,0 -3.5,1.56110911 -3.5,3.50000001 0,1.938891 1.5971695,3.5 3.5,3.5 1.9028305,0 3.46875,-1.561109 3.46875,-3.5 0,-1.9388909 -1.5659195,-3.50000001 -3.46875,-3.50000001 z m 12.125,0 c -1.90283,0 -3.46875,1.56110911 -3.46875,3.50000001 0,1.938891 1.56592,3.5 3.46875,3.5 1.902831,0 3.5,-1.561109 3.5,-3.5 0,-1.9388909 -1.597169,-3.50000001 -3.5,-3.50000001 z M 4.3125,1.3125 c 1.309224,0 2.3125,1.0393364 2.3125,2.3125 0,1.273164 -1.003276,2.3125 -2.3125,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.2731636 1.034526,-2.3125 2.34375,-2.3125 z m 12.125,0 c 1.309224,0 2.34375,1.0393364 2.34375,2.3125 0,1.273164 -1.034526,2.3125 -2.34375,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.2731636 1.034526,-2.3125 2.34375,-2.3125 z m -12.125,9.03125 c -2.4090408,0 -4.375,1.965959 -4.375,4.375 0,2.409041 1.9659592,4.34375 4.375,4.34375 2.4090408,0 4.34375,-1.934709 4.34375,-4.34375 0,-2.409041 -1.9347092,-4.375 -4.34375,-4.375 z m 12.125,0.875 c -1.90283,0 -3.46875,1.561109 -3.46875,3.5 0,1.938891 1.56592,3.46875 3.46875,3.46875 1.902831,0 3.5,-1.529859 3.5,-3.46875 0,-1.938891 -1.597169,-3.5 -3.5,-3.5 z m 0,1.15625 c 1.309224,0 2.34375,1.070586 2.34375,2.34375 0,1.273164 -1.034526,2.3125 -2.34375,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.273164 1.034526,-2.34375 2.34375,-2.34375 z";
    private static final String ADD_REMOVE_ICON = "M 6.09375 0 L 4.125 2.9375 L 1.875 6.5 L 4.53125 6.5 L 4.53125 12.90625 L 7.59375 12.90625 L 7.59375 6.5 L 10.28125 6.5 L 8 2.9375 L 6.09375 0 z M 12.40625 7.09375 L 12.40625 13.5 L 9.71875 13.5 L 12 17.0625 L 13.90625 20 L 15.84375 17.0625 L 18.125 13.5 L 15.46875 13.5 L 15.46875 7.09375 L 12.40625 7.09375 z";
    private static final String VIEW_LAYOUT = "StackingDeviceMenuView.viewLayout";
    private static final String ADD_PIECES = "UnloadPalletMenuView.addRemoveWorkpieces";
    
    @Override
    protected void build() {
        addMenuItem(0, LAYOUT_ICON, Translator.getTranslation(VIEW_LAYOUT), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                getPresenter().showLayout();
            }
        });
        addMenuItem(1, ADD_REMOVE_ICON, Translator.getTranslation(ADD_PIECES), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                getPresenter().showAddRemove();
            }
        });
    }
    
    public void setLayoutActive() {
        setMenuItemSelected(0);
    }
    
   public void setAddRemoveActive() {
        setMenuItemSelected(1);
    }

}
