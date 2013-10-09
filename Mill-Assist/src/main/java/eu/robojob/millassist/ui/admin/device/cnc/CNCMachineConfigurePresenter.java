package eu.robojob.millassist.ui.admin.device.cnc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine.WayOfOperating;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.admin.device.DeviceMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class CNCMachineConfigurePresenter extends AbstractFormPresenter<CNCMachineConfigureView, DeviceMenuPresenter> implements CNCMachineListener {

	private DeviceManager deviceManager;
	private CNCMillingMachine cncMachine;
	
	public CNCMachineConfigurePresenter(final CNCMachineConfigureView view, final DeviceManager deviceManager) {
		super(view);
		this.deviceManager = deviceManager;
		this.cncMachine = (CNCMillingMachine) deviceManager.getCNCMachines().iterator().next();
		getView().build();
		getView().setCNCMachine(cncMachine);
		cncMachine.addListener(this);
		getView().refresh();
		setWayOfOperating(cncMachine.getWayOfOperating());
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		getView().setTextFieldListener(listener);
	}
	
	public void updateUserFrames() {
		Set<String> userFrameNames = new HashSet<String>();
		for (UserFrame uf : deviceManager.getAllUserFrames()) {
			userFrameNames.add(uf.getName());
		}
		getView().setUserFrameNames(userFrameNames);
	}
	
	public void setWayOfOperating(final WayOfOperating wayOfOperating) {
		if (wayOfOperating == WayOfOperating.M_CODES) {
			getView().setMCodeActive(true);
		} else {
			getView().setMCodeActive(false);
		}
	}
	
	public void saveData() {
		String name = getView().getName();
		String ip = getView().getIp();
		int port = getView().getPort();
		String workAreaName = getView().getWA1();
		String userFrameName = getView().getUserFrameName();
		int clampingWidthR = getView().getWidthR();
		WayOfOperating wayOfOperating = getView().getWayOfOperating();
		List<String> robotServiceInputNames = getView().getRobotServiceInputNames();
		List<String> robotServiceOutputNames = getView().getRobotServiceOutputNames();
		deviceManager.updateCNCMachineData(cncMachine, name, wayOfOperating, ip, port, workAreaName, userFrameName, 
				clampingWidthR, robotServiceInputNames, robotServiceOutputNames, 
					getView().getMCodeNames(), getView().getMCodeRobotServiceInputs(), 
						getView().getMCodeRobotServiceOutputs());
		getView().refresh();
	}

	@Override
	public void cNCMachineConnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().refreshStatus();
			}
		});
	}

	@Override
	public void cNCMachineDisconnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().refreshStatus();
			}
		});
	}

	@Override 
	public void cNCMachineStatusChanged(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().refreshStatus();
			}
		});
	}

	@Override public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) { }

	@Override
	public void unregister() {
		cncMachine.removeListener(this);
	}

}
