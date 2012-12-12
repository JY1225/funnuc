package eu.robojob.irscw.process;


public abstract class AbstractJob {
	
	//private static Logger logger = LogManager.getLogger(AbstractJob.class.getName());
	
	private int currentStepIndex;
	
	private ProcessFlow processFlow;
	
	public AbstractJob(ProcessFlow processFlow) {
		this.processFlow = processFlow;
		initialize();
	}
	
	public void initialize() {
		currentStepIndex = 0;
	}
	
	public void restart() {
		this.processFlow.incrementFinishedAmount();
		currentStepIndex = 0;
	}
	
	public boolean hasNextStep() {
		if (processFlow.getProcessSteps().size() > currentStepIndex + 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasStep() {
		if (processFlow.getProcessSteps().size() > currentStepIndex) {
			return true;
		} else {
			return false;
		}
	}
	
	public void nextStep() {
		currentStepIndex++;
	}
	
	public AbstractProcessStep getCurrentStep() {
		return processFlow.getStep(currentStepIndex);
	}
	
	public int getCurrentStepIndex() {
		return currentStepIndex;
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}
}
