package eu.robojob.irscw.process;

public class ProcessFlowTeacher {

	private TeachJob teachJob;
	
	public ProcessFlowTeacher(ProcessFlow processFlow) {
		this.teachJob = new TeachJob(processFlow);
	}
	
	public void nextStep() {
		teachJob.nextStep();
	}
	
	public int getCurrentStepIndex() {
		return teachJob.getCurrentStepIndex();
	}
	
	public AbstractProcessStep getCurrentStep() {
		return teachJob.getCurrentStep();
	}
	
	public boolean doesCurrentStepNeedsTeaching() {
		return false;
	}
}
