package eu.robojob.millassist.ui.admin.device.cnc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.WorkAreaBoundary;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.millassist.external.device.processing.cnc.EWayOfOperating;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.admin.device.DeviceMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class CNCMachineConfigurePresenter extends AbstractFormPresenter<CNCMachineConfigureView, DeviceMenuPresenter> implements CNCMachineListener {

	private DeviceManager deviceManager;
	private AbstractCNCMachine cncMachine;
	
	public CNCMachineConfigurePresenter(final CNCMachineConfigureView view, final DeviceManager deviceManager) {
		super(view);
		this.deviceManager = deviceManager;
		this.cncMachine = (AbstractCNCMachine) deviceManager.getCNCMachines().iterator().next();
		getView().build();
		getView().setCNCMachine(cncMachine);
		cncMachine.addListener(this);
		getView().refresh();
		setWayOfOperating(cncMachine.getWayOfOperating());
		view.setNewDevInt(cncMachine.isUsingNewDevInt());
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
	
	public void setWayOfOperating(final EWayOfOperating wayOfOperating) {
		if ((wayOfOperating == EWayOfOperating.M_CODES) || (wayOfOperating == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			getView().setMCodeActive(true);
		} else {
			getView().setMCodeActive(false);
		}
	}
	
	public void saveData() {
		String name = getView().getName();
		String ip = getView().getIp();
		int port = getView().getPort();
		int clampingWidthR = getView().getWidthR();
		int nbFixtures = getView().getNbFixtures();
		boolean timAllowed = getView().getTIMAllowed();
		boolean machineAirblow = getView().getMachineAirblow();
		EWayOfOperating wayOfOperating = getView().getWayOfOperating();
		List<String> robotServiceInputNames = getView().getRobotServiceInputNames();
		List<String> robotServiceOutputNames = getView().getRobotServiceOutputNames();
		List<WorkAreaBoundary> boundaries = getView().getAirblowBounds();
		boolean newDevInt = getView().getNewDevInt();
		deviceManager.updateCNCMachineData(cncMachine, name, wayOfOperating, ip, port, 
				clampingWidthR, newDevInt, nbFixtures, timAllowed, machineAirblow, boundaries,
				robotServiceInputNames, robotServiceOutputNames, 
					getView().getMCodeNames(), getView().getMCodeRobotServiceInputs(), 
						getView().getMCodeRobotServiceOutputs());
		getView().refresh();
		getView().showNotificationDialog();
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
