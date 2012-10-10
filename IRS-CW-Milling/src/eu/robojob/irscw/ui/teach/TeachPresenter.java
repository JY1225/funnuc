package eu.robojob.irscw.ui.teach;

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
	
	private TeachRunnable teachRunnable;
	
	private boolean isTeached;
	
	private static Logger logger = Logger.getLogger(TeachPresenter.class);
	
	public TeachPresenter(TeachView view, TeachProcessFlowPresenter processFlowPresenter, ProcessFlow processFlow) {
		this.view = view;
		this.processFlowPresenter = processFlowPresenter;
		this.teachJob = new TeachJob(processFlow);
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		view.setPresenter(this);
		view.setTop(processFlowPresenter.getView());
		this.teachRunnable = new TeachRunnable(teachJob, this);
		isTeached = false;
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
	
	public boolean isTeached() {
		return isTeached;
	}
	
	public void startFlow() {
		setTeachMode(true);
		view.showInfo("Starten proces...");
		view.setProcessPaused(false);
		teachJob.initialize();
		ThreadManager.getInstance().submit(teachRunnable);
	}
	
	public void setInfo(String info) {
		view.showInfo(info);
	}
	
	public void continueFlow() {
		view.setProcessPaused(false);
		view.showInfo("");
		teachRunnable.teachingFinished();
	}
	
	public void exceptionOccured(Exception e){
		logger.error(e);
		processFlowPresenter.refresh();
		view.showInfo("FOUT: " + e);
		view.setProcessPaused(true);
		setTeachMode(false);
	}
	
	public void flowFinished() {
		setTeachMode(false);
		view.setProcessPaused(true);
		isTeached = true;
		parent.refreshStatus();
	}
	
	public void teachingNeeded() {
		view.setProcessPaused(true);
		view.showTeachInfo();
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
