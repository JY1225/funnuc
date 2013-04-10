package eu.robojob.irscw.ui.admin.device.cnc;

import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.positioning.UserFrame;
import eu.robojob.irscw.ui.admin.device.DeviceMenuPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class CNCMachineConfigurePresenter extends AbstractFormPresenter<CNCMachineConfigureView, DeviceMenuPresenter> implements CNCMachineListener {

	private DeviceManager deviceManager;
	private CNCMillingMachine cncMachine;
	
	public CNCMachineConfigurePresenter(final CNCMachineConfigureView view, final DeviceManager deviceManager) {
		super(view);
		this.deviceManager = deviceManager;
		this.cncMachine = (CNCMillingMachine) deviceManager.getCNCMachines().iterator().next();
		getView().setCNCMachine(cncMachine);
		cncMachine.addListener(this);
		getView().build();
		getView().refresh();
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
	
	public void saveData(final String name, final String ip, final int port, final String workAreaName, final String userFramename,
			final float clampingLengthR, final float clampingWidthR) {
		deviceManager.updateCNCMachineData(cncMachine, name, ip, port, workAreaName, userFramename, clampingLengthR, clampingWidthR);
		getView().refresh();
	}

	@Override
	public void cNCMachineConnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().refresh();
			}
		});
	}

	@Override
	public void cNCMachineDisconnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().refresh();
			}
		});
	}

	@Override 
	public void cNCMachineStatusChanged(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().refresh();
			}
		});
	}

	@Override public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) { }

}
