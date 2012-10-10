package eu.robojob.irscw.ui.teach;

import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.TeachJob;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;
import eu.robojob.irscw.ui.teach.flow.TeachProcessFlowPresenter;

public class TeachPresenter {

	private TeachView view;
	private TeachProcessFlowPresenter processFlowPresenter;
	private MainPresenter parent;
	private TeachJob teachJob;
	private ProcessFlowAdapter processFlowAdapter;
	
	private GeneralInfoView teachGeneralInfoView;
	private DisconnectedDevicesView teachDisconnectedDevicesView;
	private StatusView teachStatusView;
	private TeachingNeededView teachingNeededView;
	
	private TeachRunnable teachRunnable;
	private DevicesStatusThread devicesStatusThread;
	
	private boolean isTeached;
	
	private static Logger logger = Logger.getLogger(TeachPresenter.class);
	
	public TeachPresenter(TeachView view, TeachProcessFlowPresenter processFlowPresenter, ProcessFlow processFlow, DisconnectedDevicesView teachDisconnectedDevicesView,
			GeneralInfoView teachGeneralInfoView, StatusView teachStatusView, TeachingNeededView teachingNeededView) {
		this.view = view;
		this.processFlowPresenter = processFlowPresenter;
		this.teachJob = new TeachJob(processFlow);
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		view.setTop(processFlowPresenter.getView());
		this.teachRunnable = new TeachRunnable(teachJob, this);
		isTeached = false;
		this.teachDisconnectedDevicesView = teachDisconnectedDevicesView;
		this.teachGeneralInfoView = teachGeneralInfoView;
		teachGeneralInfoView.setPresenter(this);
		this.teachStatusView = teachStatusView;
		this.teachingNeededView = teachingNeededView;
		teachingNeededView.setPresenter(this);
		this.devicesStatusThread = new DevicesStatusThread(this, processFlow);
		checkDevices();
	}

	private void checkDevices() {
		view.setBottom(teachDisconnectedDevicesView);
		ThreadManager.getInstance().submit(devicesStatusThread);
	}
	
	public TeachView getView() {
		return view;
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
	}
	
	public void showDisconnectedDevices(Set<String> deviceNames) {
		logger.info("about to show " + deviceNames.size() + " disconnected devices");
		view.setBottom(teachDisconnectedDevicesView);
		teachDisconnectedDevicesView.setDisconnectedDevices(deviceNames);
	}
	
	public void showInfoMessage() {
		view.setBottom(teachGeneralInfoView);
	}
	
	public boolean isTeached() {
		return isTeached;
	}
	
	public void startFlow() {
		setTeachMode(true);
		view.setBottom(teachStatusView);
		logger.info("starten proces!");
		setStatus("Starten proces...");
		setProcessRunning(true);
		teachJob.initialize();
		ThreadManager.getInstance().submit(teachRunnable);
	}
	
	public void setStatus(String status) {
		teachStatusView.setMessage(status);
	}
	
	public void setProcessRunning(boolean running) {
		teachStatusView.setProcessPaused(!running);
	}
	
	public void continueFlow() {
		setProcessRunning(true);
		setStatus("");
		view.setBottom(teachStatusView);
		teachRunnable.teachingFinished();
	}
	
	public void exceptionOccured(Exception e){
		logger.error(e);
		processFlowPresenter.refresh();
		setStatus("FOUT: " + e);
		setProcessRunning(false);
		setTeachMode(false);
	}
	
	public void flowFinished() {
		setTeachMode(false);
		setProcessRunning(false);
		isTeached = true;
		parent.refreshStatus();
	}
	
	public void teachingNeeded() {
		logger.info("teaching needed");
		setProcessRunning(false);
		view.setBottom(teachingNeededView);
	}
	
	private void setTeachMode(boolean enable) {
		parent.setMenuBarEnabled(!enable);
	}
	
	public void pickStepInProgress(PickStep pickStep) {
		processFlowPresenter.setPickStepActive(processFlowAdapter.getTransportIndex(pickStep));
	}
	
	public void pickStepFinished(PickStep pickStep) {
		processFlowPresenter.setPickStepFinished(processFlowAdapter.getTransportIndex(pickStep));
	}
	
	public void putStepInProgress(PutStep putStep) {
		processFlowPresenter.setPutStepActive(processFlowAdapter.getTransportIndex(putStep));
	}
	
	public void putStepFinished(PutStep putStep) {
		int transportIndex = processFlowAdapter.getTransportIndex(putStep);
		if (!processFlowAdapter.getDeviceInformation(transportIndex + 1).hasProcessingStep()) {
			processFlowPresenter.setProcessingStepFinished(transportIndex+1);
		} else {
			processFlowPresenter.setPutStepFinished(transportIndex);
		}
	}
	
	public void processingInProgress(ProcessingStep processingStep) {
		processFlowPresenter.setProcessingStepActive(processFlowAdapter.getDeviceIndex(processingStep));
	}

	public void processingFinished(ProcessingStep processingStep) {
		processFlowPresenter.setProcessingStepFinished(processFlowAdapter.getDeviceIndex(processingStep));
	}
}
