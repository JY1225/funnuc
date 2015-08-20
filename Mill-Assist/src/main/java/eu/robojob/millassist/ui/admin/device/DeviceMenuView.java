package eu.robojob.millassist.ui.admin.device;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.util.Translator;

public class DeviceMenuView extends AbstractMenuView<DeviceMenuPresenter> {

    private static final String USERFRAMES = "DeviceMenuView.userframes";
    private static final String BASICSTACKPLATE = "DeviceMenuView.basicStackPlate";
    private static final String PRAGE = "DeviceMenuView.prage";
    private static final String CNCMACHINE = "DeviceMenuView.cncMachine";
    private static final String CNCMACHINE_CLAMPINGS = "DeviceMenuView.cncMachineClampings";
    private static final String OUTPUT_BIN = "DeviceMenuView.outputBin";
    private static final String GRIDPLATE = "DeviceMenuView.gridPlate";
    private static final String REVERSALUNIT = "DeviceMenuView.reversalUnit";
    private static final String UNLOADPALLET = "DeviceMenuView.unloadPallet";
    private static final String PALLETLAYOUT = "DeviceMenuView.palletLayout";
    private static final String PALLET = "DeviceMenuView.pallet";

    public DeviceMenuView() {
        build();
    }

    @Override
    protected void build() {
        this.getStyleClass().add("admin-menu");
        int index = 0;
        addTextMenuItem(index++, Translator.getTranslation(USERFRAMES), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configureUserFrames();
            }
        });
        addTextMenuItem(index++, Translator.getTranslation(BASICSTACKPLATE), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configureBasicStackPlate();
            }
        });
        addTextMenuItem(index++, Translator.getTranslation(CNCMACHINE), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configureCNCMachine();
            }
        });
        addTextMenuItem(index++, Translator.getTranslation(CNCMACHINE_CLAMPINGS), true,
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent arg0) {
                        getPresenter().configureCNCMachineClampings();
                    }
                });
        addTextMenuItem(index++, Translator.getTranslation(PRAGE), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configurePrage();
            }
        });
        addTextMenuItem(index++, Translator.getTranslation(OUTPUT_BIN), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configureOutputBin();
            }
        });
        addTextMenuItem(index++, Translator.getTranslation(GRIDPLATE), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configureGridPlate();
            }
        });
        addTextMenuItem(index++, Translator.getTranslation(REVERSALUNIT), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configureReversalUnit();
            }
        });
        addTextMenuItem(index++, Translator.getTranslation(UNLOADPALLET), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configureUnloadPallet();
            }
        });

        addTextMenuItem(index++, Translator.getTranslation(PALLETLAYOUT), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configurePalletLayout();
            }
        });

        addTextMenuItem(index++, Translator.getTranslation(PALLET), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configurePallet();
            }
        });
    }

    public void disablePrageMenuItem() {
        getMenuItem(4).setVisible(false);
        getMenuItem(4).setManaged(false);
    }

    public void disableBasicStackPlateMenuItem() {
        getMenuItem(1).setVisible(false);
        getMenuItem(1).setManaged(false);
    }

    public void disableBinMenuItem() {
        getMenuItem(5).setVisible(false);
        getMenuItem(5).setManaged(false);
    }

    public void disableGridPlateMenuItem() {
        getMenuItem(6).setVisible(false);
        getMenuItem(6).setManaged(false);
    }

    public void disableReversalUnitMenuItem() {
        getMenuItem(7).setVisible(false);
        getMenuItem(7).setManaged(false);
    }

    public void disableUnloadPalletMenuItem() {
        getMenuItem(8).setVisible(false);
        getMenuItem(8).setManaged(false);
    }
    
    public void disablePalletLayoutMenuItem() {
        getMenuItem(9).setVisible(false);
        getMenuItem(9).setManaged(false);
    }

    public void disablePalletMenuItem() {
        getMenuItem(10).setVisible(false);
        getMenuItem(10).setManaged(false);
    }

    public void setConfigureUserFramesActive() {
        setMenuItemSelected(0);
    }

    public void setConfigureBasicStackPlateActive() {
        setMenuItemSelected(1);
    }

    public void setConfigureCNCMachineActive() {
        setMenuItemSelected(2);
    }

    public void setConfigureClampingsActive() {
        setMenuItemSelected(3);
    }

    public void setConfigurePrageActive() {
        setMenuItemSelected(4);
    }

    public void setConfigureOutputBinActive() {
        setMenuItemSelected(5);
    }

    public void setConfigureGridPlateActive() {
        setMenuItemSelected(6);
    }

    public void setConfigureReversalUnitActive() {
        setMenuItemSelected(7);
    }

    public void setConfigureUnloadPalletActive() {
        setMenuItemSelected(8);
    }

    public void setConfigurePalletLayout() {
        setMenuItemSelected(9);
    }

    public void setConfigurePallet() {
        setMenuItemSelected(10);
    }

}
