package eu.robojob.millassist.ui;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.robot.RobotManager;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlowManager;
import eu.robojob.millassist.process.ProcessFlowTimer;
import eu.robojob.millassist.ui.admin.AdminPresenter;
import eu.robojob.millassist.ui.admin.AdminView;
import eu.robojob.millassist.ui.admin.MainMenuPresenter;
import eu.robojob.millassist.ui.admin.MainMenuView;
import eu.robojob.millassist.ui.admin.SubMenuAdminView;
import eu.robojob.millassist.ui.admin.device.BasicStackPlateConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.BasicStackPlateConfigureView;
import eu.robojob.millassist.ui.admin.device.CNCMachineClampingsPresenter;
import eu.robojob.millassist.ui.admin.device.CNCMachineClampingsView;
import eu.robojob.millassist.ui.admin.device.DeviceAdminPresenter;
import eu.robojob.millassist.ui.admin.device.DeviceMenuPresenter;
import eu.robojob.millassist.ui.admin.device.DeviceMenuView;
import eu.robojob.millassist.ui.admin.device.GridPlateConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.GridPlateConfigureView;
import eu.robojob.millassist.ui.admin.device.OutputBinConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.OutputBinConfigureView;
import eu.robojob.millassist.ui.admin.device.PalletConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.PalletConfigureView;
import eu.robojob.millassist.ui.admin.device.PalletLayoutConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.PalletLayoutConfigureView;
import eu.robojob.millassist.ui.admin.device.PrageDeviceConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.PrageDeviceConfigureView;
import eu.robojob.millassist.ui.admin.device.ReversalUnitConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.ReversalUnitConfigureView;
import eu.robojob.millassist.ui.admin.device.UnloadPalletConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.UnloadPalletConfigureView;
import eu.robojob.millassist.ui.admin.device.UserFramesConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.UserFramesConfigureView;
import eu.robojob.millassist.ui.admin.device.cnc.CNCMachineConfigurePresenter;
import eu.robojob.millassist.ui.admin.device.cnc.CNCMachineConfigureView;
import eu.robojob.millassist.ui.admin.general.EmailAdminPresenter;
import eu.robojob.millassist.ui.admin.general.EmailAdminView;
import eu.robojob.millassist.ui.admin.general.GeneralAdminPresenter;
import eu.robojob.millassist.ui.admin.general.GeneralMenuPresenter;
import eu.robojob.millassist.ui.admin.general.GeneralMenuView;
import eu.robojob.millassist.ui.admin.robot.RobotAdminPresenter;
import eu.robojob.millassist.ui.admin.robot.RobotConfigurePresenter;
import eu.robojob.millassist.ui.admin.robot.RobotConfigureView;
import eu.robojob.millassist.ui.admin.robot.RobotDataPresenter;
import eu.robojob.millassist.ui.admin.robot.RobotDataView;
import eu.robojob.millassist.ui.admin.robot.RobotGripperPresenter;
import eu.robojob.millassist.ui.admin.robot.RobotGripperView;
import eu.robojob.millassist.ui.admin.robot.RobotMenuPresenter;
import eu.robojob.millassist.ui.admin.robot.RobotMenuView;
import eu.robojob.millassist.ui.alarms.AlarmsPopUpPresenter;
import eu.robojob.millassist.ui.alarms.AlarmsPopUpView;
import eu.robojob.millassist.ui.automate.AutomatePresenter;
import eu.robojob.millassist.ui.automate.AutomateStatusPresenter;
import eu.robojob.millassist.ui.automate.AutomateStatusView;
import eu.robojob.millassist.ui.automate.TimingView;
import eu.robojob.millassist.ui.automate.flow.AutomateProcessFlowPresenter;
import eu.robojob.millassist.ui.automate.flow.AutomateProcessFlowView;
import eu.robojob.millassist.ui.configure.ConfigurePresenter;
import eu.robojob.millassist.ui.configure.device.DeviceMenuFactory;
import eu.robojob.millassist.ui.configure.flow.ConfigureProcessFlowPresenter;
import eu.robojob.millassist.ui.configure.process.ProcessConfigurePresenter;
import eu.robojob.millassist.ui.configure.process.ProcessConfigureView;
import eu.robojob.millassist.ui.configure.process.ProcessMenuPresenter;
import eu.robojob.millassist.ui.configure.process.ProcessMenuView;
import eu.robojob.millassist.ui.configure.process.ProcessOpenPresenter;
import eu.robojob.millassist.ui.configure.process.ProcessOpenView;
import eu.robojob.millassist.ui.configure.process.ProcessSavePresenter;
import eu.robojob.millassist.ui.configure.process.ProcessSaveView;
import eu.robojob.millassist.ui.configure.transport.TransportMenuFactory;
import eu.robojob.millassist.ui.controls.keyboard.FullKeyboardPresenter;
import eu.robojob.millassist.ui.controls.keyboard.FullKeyboardView;
import eu.robojob.millassist.ui.controls.keyboard.FullKeyboardView.KeyboardType;
import eu.robojob.millassist.ui.controls.keyboard.NegativeNumericKeyboardView;
import eu.robojob.millassist.ui.controls.keyboard.NumericKeyboardPresenter;
import eu.robojob.millassist.ui.controls.keyboard.NumericKeyboardView;
import eu.robojob.millassist.ui.general.MainContentView;
import eu.robojob.millassist.ui.general.flow.ProcessFlowView;
import eu.robojob.millassist.ui.general.model.ProcessFlowAdapter;
import eu.robojob.millassist.ui.general.status.DisconnectedDevicesView;
import eu.robojob.millassist.ui.general.status.StatusPresenter;
import eu.robojob.millassist.ui.general.status.StatusView;
import eu.robojob.millassist.ui.menu.MenuBarPresenter;
import eu.robojob.millassist.ui.menu.MenuBarView;
import eu.robojob.millassist.ui.robot.RobotPopUpPresenter;
import eu.robojob.millassist.ui.robot.RobotPopUpView;
import eu.robojob.millassist.ui.teach.GeneralInfoPresenter;
import eu.robojob.millassist.ui.teach.GeneralInfoView;
import eu.robojob.millassist.ui.teach.TeachPresenter;
import eu.robojob.millassist.ui.teach.TeachStatusPresenter;
import eu.robojob.millassist.ui.teach.TeachStatusView;
import eu.robojob.millassist.ui.teach.flow.TeachProcessFlowPresenter;
import eu.robojob.millassist.ui.teach.flow.TeachProcessFlowView;

public final class RoboSoftAppFactory {

    private static MainPresenter mainPresenter;
    private static MenuBarPresenter menuBarPresenter;
    private static ConfigurePresenter configurePresenter;
    private static ProcessConfigurePresenter processConfigurationPresenter;
    private static TeachPresenter teachPresenter;
    private static AutomatePresenter automatePresenter;
    private static ConfigureProcessFlowPresenter configureProcessFlowPresenter;
    private static TeachProcessFlowPresenter teachProcessFlowPresenter;
    private static AutomateProcessFlowPresenter automateProcessFlowPresenter;
    private static AlarmsPopUpPresenter alarmsPopUpPresenter;
    private static RobotPopUpPresenter robotPopUpPresenter;
    private static ProcessMenuPresenter processConfigurationMenuPresenter;
    private static ProcessOpenPresenter processOpenPresenter;
    private static ProcessSavePresenter processSavePresenter;
    private static AdminPresenter adminPresenter;
    private static MainMenuPresenter mainMenuPresenter;
    private static GeneralAdminPresenter generalAdminPresenter;
    private static EmailAdminPresenter emailAdminPresenter;
    private static RobotAdminPresenter robotAdminPresenter;
    private static RobotConfigurePresenter robotConfigurePresenter;
    private static RobotGripperPresenter robotGripperPresenter;
    private static RobotDataPresenter robotDataPresenter;
    private static DeviceAdminPresenter deviceAdminPresenter;
    private static UserFramesConfigurePresenter userFramesConfigurePresenter;
    private static BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter;
    private static UnloadPalletConfigurePresenter unloadPalletConfigurePresenter;
    private static PalletLayoutConfigurePresenter palletLayoutConfigurePresenter;
    private static CNCMachineConfigurePresenter cncMachineConfigurePresenter;
    private static CNCMachineClampingsPresenter cncMachineClampingsPresenter;
    private static PrageDeviceConfigurePresenter prageDeviceConfigurePresenter;
    private static OutputBinConfigurePresenter outputBinConfigurePresenter;
    private static GridPlateConfigurePresenter gridPlateConfigurePresenter;
    private static ReversalUnitConfigurePresenter reversalUnitConfigurePresenter;
    private static PalletConfigurePresenter palletConfigurePresenter;
    private static eu.robojob.millassist.ui.automate.device.DeviceMenuFactory automateDeviceMenuFactory;
    private static eu.robojob.millassist.ui.teach.transport.TransportMenuFactory teachTransportMenuFactory;

    private static ProcessFlow processFlow;
    private static ProcessFlowTimer processFlowTimer;

    private static ProcessFlowManager processFlowManager;
    private static DeviceManager deviceManager;
    private static RobotManager robotManager;

    private static DeviceMenuFactory deviceMenuFactory;
    private static TransportMenuFactory transportMenuFactory;

    private static KeyboardType keyboardType;

    private RoboSoftAppFactory() {	}

    public static void intialize(final DeviceManager deviceManager, final RobotManager robotManager,
            final ProcessFlowManager processFlowManager, final KeyboardType keyboardType) {
        RoboSoftAppFactory.deviceManager = deviceManager;
        RoboSoftAppFactory.robotManager = robotManager;
        RoboSoftAppFactory.processFlowManager = processFlowManager;
        RoboSoftAppFactory.keyboardType = keyboardType;
    }

    public static MainPresenter getMainPresenter() {
        if (mainPresenter == null) {
            MainView mainView = new MainView();
            MainPresenter tmpMainPresenter = new MainPresenter(mainView, getMenuBarPresenter(), getConfigurePresenter(), getTeachPresenter(), getAutomatePresenter(),
                    getAlarmsPopUpPresenter(), getRobotPopUpPresenter(), getAdminPresenter());
            tmpMainPresenter.loadProcessFlow(getProcessFlow());
            mainPresenter = tmpMainPresenter;
        }
        return mainPresenter;
    }

    private static AlarmsPopUpPresenter getAlarmsPopUpPresenter() {
        if (alarmsPopUpPresenter == null) {
            AlarmsPopUpView view = new AlarmsPopUpView();
            alarmsPopUpPresenter = new AlarmsPopUpPresenter(view, getProcessFlow(), deviceManager, robotManager);
        }
        return alarmsPopUpPresenter;
    }

    private static MenuBarPresenter getMenuBarPresenter() {
        if (menuBarPresenter == null) {
            MenuBarView processMenuBarView = new MenuBarView();
            menuBarPresenter = new MenuBarPresenter(processMenuBarView);
        }
        return menuBarPresenter;
    }

    private static ProcessFlowTimer getProcessFlowTimer() {
        if (processFlowTimer == null) {
            processFlowTimer = new ProcessFlowTimer(getProcessFlow());
        }
        return processFlowTimer;
    }

    private static ConfigurePresenter getConfigurePresenter() {
        if (configurePresenter == null) {
            MainContentView processConfigureView = new MainContentView();
            configurePresenter = new ConfigurePresenter(processConfigureView, getKeyboardPresenter(), getNegativeNumericKeyboardPresenter(), getConfigureProcessFlowPresenter(),
                    getProcessConfigurationMenuPresenter(), getDeviceMenuFactory(), getTransportMenuFactory(), deviceManager);
        }
        return configurePresenter;
    }

    private static TeachPresenter getTeachPresenter() {
        if (teachPresenter == null) {
            MainContentView view = new MainContentView();
            DisconnectedDevicesView disconnectedDevicesView = new DisconnectedDevicesView();
            teachPresenter = new TeachPresenter(view, getTeachProcessFlowPresenter(), getProcessFlow(), disconnectedDevicesView, getGeneralInfoPresenter(), getTeachStatusPresenter(), processFlowManager,
                    getTeachTransportMenuFactory(), getKeyboardPresenter(), getNegativeNumericKeyboardPresenter());
        }
        return teachPresenter;
    }

    private static eu.robojob.millassist.ui.teach.transport.TransportMenuFactory getTeachTransportMenuFactory() {
        if (teachTransportMenuFactory == null) {
            teachTransportMenuFactory = new eu.robojob.millassist.ui.teach.transport.TransportMenuFactory(getProcessFlow(), new ProcessFlowAdapter(getProcessFlow()));
        }
        return teachTransportMenuFactory;
    }

    private static GeneralInfoPresenter getGeneralInfoPresenter() {
        GeneralInfoView generalInfoView = new GeneralInfoView(getProcessFlow());
        GeneralInfoPresenter generalInfoPresenter = new GeneralInfoPresenter(generalInfoView);
        return generalInfoPresenter;
    }

    private static TeachStatusPresenter getTeachStatusPresenter() {
        TeachStatusView teachStatusView = new TeachStatusView();
        TeachStatusPresenter teachStatusPresenter = new TeachStatusPresenter(teachStatusView, getStatusPresenter());
        return teachStatusPresenter;
    }

    private static StatusPresenter getStatusPresenter() {
        StatusView statusView = new StatusView();
        StatusPresenter statusPresenter = new StatusPresenter(statusView);
        return statusPresenter;
    }

    private static AutomateStatusPresenter getAutomateStatusPresenter() {
        AutomateStatusView automateStatusView = new AutomateStatusView();
        TimingView timingView = new TimingView();
        AutomateStatusPresenter automateStatusPresenter = new AutomateStatusPresenter(automateStatusView, getStatusPresenter(), timingView);
        return automateStatusPresenter;
    }

    private static AutomatePresenter getAutomatePresenter() {
        if (automatePresenter == null) {
            MainContentView view = new MainContentView();
            DisconnectedDevicesView disconnectedDevicesView = new DisconnectedDevicesView();
            automatePresenter = new AutomatePresenter(view, getAutomateProcessFlowPresenter(), disconnectedDevicesView,
                    getProcessFlow(), getProcessFlowTimer(), getAutomateStatusPresenter(), getAutomateDeviceMenuFactory(), getNumericKeyboardPresenter());
        }
        return automatePresenter;
    }

    private static eu.robojob.millassist.ui.automate.device.DeviceMenuFactory getAutomateDeviceMenuFactory() {
        if (automateDeviceMenuFactory == null) {
            automateDeviceMenuFactory = new eu.robojob.millassist.ui.automate.device.DeviceMenuFactory(getProcessFlow());
        }
        return automateDeviceMenuFactory;
    }

    private static RobotPopUpPresenter getRobotPopUpPresenter() {
        if (robotPopUpPresenter == null) {
            RobotPopUpView view = new RobotPopUpView();
            // TODO review: now fixed robot
            robotPopUpPresenter = new RobotPopUpPresenter(view, (FanucRobot) robotManager.getRobotByName("Fanuc M20iA"), getProcessFlow());
        }
        return robotPopUpPresenter;
    }

    public static FullKeyboardPresenter getKeyboardPresenter() {
        FullKeyboardView keyboardView = new FullKeyboardView(keyboardType);
        FullKeyboardPresenter keyboardPresenter = new FullKeyboardPresenter(keyboardView);
        return keyboardPresenter;
    }

    private static NumericKeyboardPresenter getNumericKeyboardPresenter() {
        NumericKeyboardView numericKeyboardView = new NumericKeyboardView();
        NumericKeyboardPresenter numericKeyboardPresenter = new NumericKeyboardPresenter(numericKeyboardView);
        return numericKeyboardPresenter;
    }

    private static NumericKeyboardPresenter getNegativeNumericKeyboardPresenter() {
        NegativeNumericKeyboardView numericKeyboardView = new NegativeNumericKeyboardView();
        NumericKeyboardPresenter numericKeyboardPresenter = new NumericKeyboardPresenter(numericKeyboardView);
        return numericKeyboardPresenter;
    }

    private static ProcessConfigurePresenter getProcessConfigurePresenter() {
        if (processConfigurationPresenter == null) {
            ProcessConfigureView processConfigurationView = new ProcessConfigureView();
            processConfigurationPresenter = new ProcessConfigurePresenter(processConfigurationView, getProcessFlow(), deviceManager);
        }
        return processConfigurationPresenter;
    }

    private static ConfigureProcessFlowPresenter getConfigureProcessFlowPresenter() {
        if (configureProcessFlowPresenter == null) {
            ProcessFlowView processFlowView = new ProcessFlowView(1);
            configureProcessFlowPresenter = new ConfigureProcessFlowPresenter(processFlowView);
        }
        return configureProcessFlowPresenter;
    }

    private static TeachProcessFlowPresenter getTeachProcessFlowPresenter() {
        if (teachProcessFlowPresenter == null) {
            TeachProcessFlowView processFlowView = new TeachProcessFlowView(1);
            teachProcessFlowPresenter = new TeachProcessFlowPresenter(processFlowView);
        }
        return teachProcessFlowPresenter;
    }

    private static AutomateProcessFlowPresenter getAutomateProcessFlowPresenter() {
        if (automateProcessFlowPresenter == null) {
            AutomateProcessFlowView processFlowView = new AutomateProcessFlowView(deviceManager.getCNCMachines().iterator().next().getWayOfOperating().getNbOfSides());
            automateProcessFlowPresenter = new AutomateProcessFlowPresenter(processFlowView, getAutomateDeviceMenuFactory());
        }
        return automateProcessFlowPresenter;
    }

    private static ProcessMenuPresenter getProcessConfigurationMenuPresenter() {
        if (processConfigurationMenuPresenter == null) {
            ProcessMenuView processConfigurationMenuView = new ProcessMenuView();
            processConfigurationMenuPresenter = new ProcessMenuPresenter(processConfigurationMenuView, getProcessConfigurePresenter(), getProcessSavePresenter(),
                    getProcessOpenPresenter(), getProcessFlow(), processFlowManager);
        }
        return processConfigurationMenuPresenter;
    }

    private static ProcessSavePresenter getProcessSavePresenter() {
        if (processSavePresenter == null) {
            ProcessSaveView processSaveView = new ProcessSaveView();
            processSavePresenter = new ProcessSavePresenter(processSaveView, processFlowManager, getProcessFlow());
        }
        return processSavePresenter;
    }

    private static ProcessOpenPresenter getProcessOpenPresenter() {
        if (processOpenPresenter == null) {
            ProcessOpenView processOpenView = new ProcessOpenView();
            //TODO update!
            processOpenPresenter = new ProcessOpenPresenter(processOpenView, getProcessFlow(), processFlowManager);
        }
        return processOpenPresenter;
    }

    public static ProcessFlow getProcessFlow() {
        if (processFlow == null) {
            processFlow = processFlowManager.getLastProcessFlow();
            if (processFlow == null) {
                processFlow = processFlowManager.createNewProcessFlow();
            }
            processFlow.initialize();
            processFlowManager.setActiveProcessFlow(processFlow);
        }
        return processFlow;
    }

    private static DeviceMenuFactory getDeviceMenuFactory() {
        if (deviceMenuFactory == null) {
            deviceMenuFactory = new DeviceMenuFactory(deviceManager);
        }
        return deviceMenuFactory;
    }

    private static TransportMenuFactory getTransportMenuFactory() {
        if (transportMenuFactory == null) {
            transportMenuFactory = new TransportMenuFactory(getProcessFlow());
        }
        return transportMenuFactory;
    }

    private static AdminPresenter getAdminPresenter() {
        if (adminPresenter == null) {
            AdminView view = new AdminView();
            adminPresenter = new AdminPresenter(view, getMainMenuPresenter(), getKeyboardPresenter(), getNegativeNumericKeyboardPresenter());
        }
        return adminPresenter;
    }

    private static MainMenuPresenter getMainMenuPresenter() {
        if (mainMenuPresenter == null) {
            MainMenuView view = new MainMenuView();
            mainMenuPresenter = new MainMenuPresenter(view, getGeneralAdminPresenter(), getRobotAdminPresenter(), getDeviceAdminPresenter());
        }
        return mainMenuPresenter;
    }

    private static GeneralAdminPresenter getGeneralAdminPresenter() {
        if (generalAdminPresenter == null) {
            SubMenuAdminView view = new SubMenuAdminView();
            GeneralMenuView menuView = new GeneralMenuView();
            GeneralMenuPresenter generalMenuPresenter = new GeneralMenuPresenter(menuView, getEmailAdminPresenter());
            generalAdminPresenter = new GeneralAdminPresenter(view, generalMenuPresenter);
        }
        return generalAdminPresenter;
    }
    private static EmailAdminPresenter getEmailAdminPresenter() {
        if(emailAdminPresenter == null) {
            EmailAdminView emailAdminView = new EmailAdminView();
            emailAdminPresenter = new EmailAdminPresenter(emailAdminView);
        }
        return emailAdminPresenter;
    }


    private static DeviceAdminPresenter getDeviceAdminPresenter() {
        if (deviceAdminPresenter == null) {
            SubMenuAdminView view = new SubMenuAdminView();
            DeviceMenuView menuView = new DeviceMenuView();
            DeviceMenuPresenter deviceMenuPresenter = new DeviceMenuPresenter(menuView, getUserFramesConfigurePresenter(), getBasicStackPlateConfigurePresenter(), getUnloadPalletConfigurePresenter(), getPalletLayoutConfigurePresenter(),
                    getCNCMachineConfigurePresenter(), getCNCMachineClampingsPresenter(), getPrageDeviceConfigurePresenter(),
                    getOutputBinConfigurePresenter(), getGridPlateConfigurePresenter(), getReversalUnitConfigurePresenter(),getPalletConfigurePresenter() ,deviceManager);
            deviceAdminPresenter = new DeviceAdminPresenter(view, deviceMenuPresenter);
        }
        return deviceAdminPresenter;
    }

    private static OutputBinConfigurePresenter getOutputBinConfigurePresenter() {
        OutputBinConfigureView view = new OutputBinConfigureView();
        outputBinConfigurePresenter = new OutputBinConfigurePresenter(view, deviceManager);
        return outputBinConfigurePresenter;
    }

    private static GridPlateConfigurePresenter getGridPlateConfigurePresenter() {
        GridPlateConfigureView view = new GridPlateConfigureView();
        gridPlateConfigurePresenter = new GridPlateConfigurePresenter(view, deviceManager);
        return gridPlateConfigurePresenter;
    }

    private static UserFramesConfigurePresenter getUserFramesConfigurePresenter() {
        if (userFramesConfigurePresenter == null) {
            UserFramesConfigureView view = new UserFramesConfigureView();
            userFramesConfigurePresenter = new UserFramesConfigurePresenter(view, deviceManager);
        }
        return userFramesConfigurePresenter;
    }

    private static BasicStackPlateConfigurePresenter getBasicStackPlateConfigurePresenter() {
        if (basicStackPlateConfigurePresenter == null) {
            BasicStackPlateConfigureView view = new BasicStackPlateConfigureView();
            basicStackPlateConfigurePresenter = new BasicStackPlateConfigurePresenter(view, deviceManager);
        }
        return basicStackPlateConfigurePresenter;
    }

    private static UnloadPalletConfigurePresenter getUnloadPalletConfigurePresenter() {
        if (unloadPalletConfigurePresenter == null) {
            UnloadPalletConfigureView view = new UnloadPalletConfigureView();
            unloadPalletConfigurePresenter = new UnloadPalletConfigurePresenter(view, deviceManager);
        }
        return unloadPalletConfigurePresenter;
    }

    private static PalletConfigurePresenter getPalletConfigurePresenter() {
        if (palletConfigurePresenter == null) {
            PalletConfigureView view = new PalletConfigureView();
            palletConfigurePresenter = new PalletConfigurePresenter(view, deviceManager);
        }
        return palletConfigurePresenter;
    }

    private static PalletLayoutConfigurePresenter getPalletLayoutConfigurePresenter() {
        if (palletLayoutConfigurePresenter == null) {
            PalletLayoutConfigureView view = new PalletLayoutConfigureView();
            palletLayoutConfigurePresenter= new PalletLayoutConfigurePresenter(view, deviceManager);
        }
        return palletLayoutConfigurePresenter;
    }

    private static CNCMachineConfigurePresenter getCNCMachineConfigurePresenter() {
        if (cncMachineConfigurePresenter == null) {
            CNCMachineConfigureView view = new CNCMachineConfigureView();
            cncMachineConfigurePresenter = new CNCMachineConfigurePresenter(view, deviceManager);
        }
        return cncMachineConfigurePresenter;
    }

    private static CNCMachineClampingsPresenter getCNCMachineClampingsPresenter() {
        if (cncMachineClampingsPresenter == null) {
            CNCMachineClampingsView view = new CNCMachineClampingsView(deviceManager);
            cncMachineClampingsPresenter = new CNCMachineClampingsPresenter(view, deviceManager);
        }
        return cncMachineClampingsPresenter;
    }

    private static PrageDeviceConfigurePresenter getPrageDeviceConfigurePresenter() {
        if (prageDeviceConfigurePresenter == null) {
            PrageDeviceConfigureView view = new PrageDeviceConfigureView();
            prageDeviceConfigurePresenter = new PrageDeviceConfigurePresenter(view, deviceManager);
        }
        return prageDeviceConfigurePresenter;
    }

    private static ReversalUnitConfigurePresenter getReversalUnitConfigurePresenter() {
        if (reversalUnitConfigurePresenter == null) {
            ReversalUnitConfigureView view = new ReversalUnitConfigureView();
            reversalUnitConfigurePresenter = new ReversalUnitConfigurePresenter(view, deviceManager);
        }
        return reversalUnitConfigurePresenter;
    }

    private static RobotAdminPresenter getRobotAdminPresenter() {
        if (robotAdminPresenter == null) {
            SubMenuAdminView view = new SubMenuAdminView();
            RobotMenuView menuView = new RobotMenuView();
            RobotMenuPresenter robotMenuPresenter = new RobotMenuPresenter(menuView, getRobotConfigurePresenter(), getRobotGripperPresenter(),
                    getRobotDataPresenter());
            robotAdminPresenter = new RobotAdminPresenter(view, robotMenuPresenter);
        }
        return robotAdminPresenter;
    }

    private static RobotConfigurePresenter getRobotConfigurePresenter() {
        if (robotConfigurePresenter == null) {
            RobotConfigureView view = new RobotConfigureView();
            robotConfigurePresenter = new RobotConfigurePresenter(view, robotManager);
        }
        return robotConfigurePresenter;
    }

    private static RobotGripperPresenter getRobotGripperPresenter() {
        if (robotGripperPresenter == null) {
            RobotGripperView view = new RobotGripperView();
            robotGripperPresenter = new RobotGripperPresenter(view, robotManager);
        }
        return robotGripperPresenter;
    }

    private static RobotDataPresenter getRobotDataPresenter() {
        if (robotDataPresenter == null) {
            RobotDataView view = new RobotDataView();
            robotDataPresenter = new RobotDataPresenter(view, robotManager);
        }
        return robotDataPresenter;
    }
}