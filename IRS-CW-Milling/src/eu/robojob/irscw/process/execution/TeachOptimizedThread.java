package eu.robojob.irscw.process.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.external.device.processing.prage.PrageDevice;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotPutSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.TeachedCoordinatesCalculator;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.PutAndWaitStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.workpiece.WorkPiece;

public class TeachOptimizedThread extends TeachThread {
	
	private PickStep pickFromStackerStep = null;
	private PutAndWaitStep putAndWaitOnPrageStep = null;
	private PickAfterWaitStep pickAfterWaitOnPrageStep = null;
	private PutStep putInMachineStep = null;
	private PickStep pickFromMachineStep = null;
	private PutStep putOnStackerStep = null;

	private static Logger logger = LogManager.getLogger(TeachOptimizedThread.class.getName());
	private static final int WORKPIECE_ID = 0;
	
	public TeachOptimizedThread(final ProcessFlow processFlow) {
		super(processFlow);
		pickFromStackerStep = null;
		putAndWaitOnPrageStep = null;
		pickAfterWaitOnPrageStep = null;
		putInMachineStep = null;
		pickFromMachineStep = null;
		putOnStackerStep = null;
	}

	//TODO generalize this method for more complex ProcessFlows
	@Override
	public void run() {
		// This implementation will be less generic, and take into account two possible ProcessFlow layouts: 
		// -  BasicStackPlate - PrageDevice - CNCMillingMachine - BasicStackPlate 
		//    or 
		// -  BasicStackPlate - CNCMillingMachine - BasicStackPlate
		logger.debug("Started execution, processflow [" + getProcessFlow() + "].");
		setRunning(true);
		try {
			getProcessFlow().initialize();
			getProcessFlow().setMode(Mode.TEACH);
			resetOffsets();
			try {
				// process-initialization
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.PREPARE, WORKPIECE_ID));
				for (AbstractRobot robot : getProcessFlow().getRobots()) {
					robot.recalculateTCPs();
					robot.moveToHome();
				}
				for (AbstractDevice device: getProcessFlow().getDevices()) {
					device.prepareForProcess(getProcessFlow());
				}
				initializeSteps();
				Coordinates relTeachedOffsetFinishedWp = null;
				// before doing this, we fake the gripper holding a workpiece
				putOnStackerStep.getRobotSettings().getGripperHead().getGripper().setWorkPiece(pickFromMachineStep.getRobotSettings().getWorkPiece());
				relTeachedOffsetFinishedWp = getFinishedWorkPieceTeachedOffset(putOnStackerStep);
				//TODO review if this offset needs formatting (depending on clamp manner...)
				logger.info("Relative offset finished work piece after added extra offset: [" + relTeachedOffsetFinishedWp + "].");
				pickFromMachineStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);
				putOnStackerStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);
				Coordinates relTeachedOffsetRawWp = null;
				Coordinates relTeachedOffsetMachineClamping = null;
				boolean knowEnough = false;
				getProcessFlow().setCurrentIndex(WORKPIECE_ID, 0);
				while ((getProcessFlow().getCurrentIndex(WORKPIECE_ID) < getProcessFlow().getProcessSteps().size()) && !knowEnough && isRunning()) {
					AbstractProcessStep step = getProcessFlow().getProcessSteps().get((getProcessFlow().getCurrentIndex(WORKPIECE_ID)));
					if (step.equals(pickFromStackerStep)) {
						pickFromStackerStep.executeStepTeached(WORKPIECE_ID);
						pickFromStackerStep.finalizeStep();
						// update relative offset for upcoming steps
						relTeachedOffsetRawWp = pickFromStackerStep.getRelativeTeachedOffset();
						if (putAndWaitOnPrageStep != null) {
							putAndWaitOnPrageStep.setRelativeTeachedOffset(relTeachedOffsetRawWp);
						}
						putInMachineStep.setRelativeTeachedOffset(relTeachedOffsetRawWp);
					} else if (step.equals(putAndWaitOnPrageStep)) {
						putAndWaitOnPrageStep.executeStepTeached(WORKPIECE_ID);
						putAndWaitOnPrageStep.finalizeStep();
						relTeachedOffsetMachineClamping = putAndWaitOnPrageStep.getRelativeTeachedOffset();
						Coordinates offsetInMachine = new Coordinates(relTeachedOffsetMachineClamping);
						putInMachineStep.setRelativeTeachedOffset(offsetInMachine);
						//TODO what to do with y offset of Präge?
					} else if (step.equals(putInMachineStep)) {
						putInMachineStep.executeStepTeached(WORKPIECE_ID);
						putInMachineStep.finalizeStep();
						relTeachedOffsetMachineClamping = putInMachineStep.getRelativeTeachedOffset();
						knowEnough = true;
					} else if (step.equals(pickAfterWaitOnPrageStep)) {
						pickAfterWaitOnPrageStep.executeStep(WORKPIECE_ID);
						pickAfterWaitOnPrageStep.finalizeStep();
						knowEnough = true;
					} else if (!(step instanceof InterventionStep)) {
						step.executeStep(WORKPIECE_ID);
					}
					getProcessFlow().setCurrentIndex(WORKPIECE_ID, getProcessFlow().getCurrentIndex(WORKPIECE_ID) + 1);
				}
				if (isRunning()) {
					Coordinates pickFromMachineOffset = new Coordinates(relTeachedOffsetMachineClamping);
					pickFromMachineOffset.minus(relTeachedOffsetRawWp);
					pickFromMachineOffset.plus(relTeachedOffsetFinishedWp);
					pickFromMachineStep.setRelativeTeachedOffset(pickFromMachineOffset);
					putOnStackerStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);		
					for (AbstractRobot robot : getProcessFlow().getRobots()) {
						robot.moveToHome();
					}
					setRunning(false);
					getProcessFlow().setMode(Mode.READY);
				} else {
					getProcessFlow().setMode(Mode.STOPPED);
				}
			} catch (AbstractCommunicationException | RobotActionException | DeviceActionException e) {
				handleException(e);
			} catch (InterruptedException e) {
				if ((!isRunning()) || ThreadManager.isShuttingDown()) {
					logger.info("Execution of one or more steps got interrupted, so let't just stop");
					indicateStopped();
				} else {
					handleException(new Exception(Translator.getTranslation(OTHER_EXCEPTION)));

				}
			} catch (Exception e) {
				e.printStackTrace();
				handleException(new Exception(Translator.getTranslation(OTHER_EXCEPTION)));
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} catch (Throwable t) {
			logger.error(t);
			t.printStackTrace();
		}
		logger.info(toString() + " ended...");
	}
	
	private Coordinates getFinishedWorkPieceTeachedOffset(final PutStep putOnStackerStep) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		logger.debug("About to get teached offset of finished workpiece first.");
		BasicStackPlate stackPlate = (BasicStackPlate) putOnStackerStep.getDevice();
		FanucRobot fRobot = (FanucRobot) putOnStackerStep.getRobot();
		FanucRobotPutSettings putSettings = (FanucRobotPutSettings) putOnStackerStep.getRobotSettings();
		// we set the first work piece as a finished
		putOnStackerStep.getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(putOnStackerStep.getProcessFlow(), putOnStackerStep, StatusChangedEvent.STARTED, WORKPIECE_ID));
		stackPlate.getLayout().getStackingPositions().get(0).getWorkPiece().setType(WorkPiece.Type.FINISHED);
		getProcessFlow().setFinishedAmount(1);
		Coordinates originalCoordinates = stackPlate.getLocation(putOnStackerStep.getRobotSettings().getWorkArea(), WorkPiece.Type.FINISHED, getProcessFlow().getClampingType());
		putSettings.setLocation(originalCoordinates);
		putSettings.setTeachingNeeded(true);
		putSettings.setFreeAfter(false);
		// TODO refactor, as the robot does not really have a piece
		if (!fRobot.lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + fRobot + "] was already locked by [" + fRobot.getLockingProcess() + "].");
		} else {
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), putOnStackerStep, StatusChangedEvent.PREPARE_DEVICE, WORKPIECE_ID));
			stackPlate.prepareForPut(putOnStackerStep.getDeviceSettings());
			logger.debug("Original coordinates: " + originalCoordinates + ".");
			logger.debug("Initiating robot: [" + fRobot + "] move action.");
			fRobot.initiateMoveWithPieceNoAction(putSettings);
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), putOnStackerStep, StatusChangedEvent.EXECUTE_TEACHED, WORKPIECE_ID));
			fRobot.continueMoveWithPieceTillAtLocation();
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), putOnStackerStep, StatusChangedEvent.TEACHING_NEEDED, WORKPIECE_ID));
			fRobot.continueMoveWithPieceTillWait();
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), putOnStackerStep, StatusChangedEvent.TEACHING_FINISHED, WORKPIECE_ID));
			Coordinates coordinates = new Coordinates(fRobot.getPosition());
			Coordinates relTeachedOffsetFinishedWp = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalCoordinates, coordinates.calculateOffset(originalCoordinates));
			logger.info("The relative teached offset (finished workpiece): [" + relTeachedOffsetFinishedWp + "].");
			fRobot.continueMoveWithPieceTillIPPoint();
			fRobot.finalizeMoveWithPiece();
			putOnStackerStep.getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(putOnStackerStep.getProcessFlow(), putOnStackerStep, StatusChangedEvent.ENDED, WORKPIECE_ID));
			return relTeachedOffsetFinishedWp;
		}
	}
	
	private void initializeSteps() {
		for (AbstractProcessStep step : getProcessFlow().getProcessSteps()) {
			if ((step instanceof PickStep) && ((PickStep) step).getDevice() instanceof BasicStackPlate) {
				pickFromStackerStep = (PickStep) step;
			} else if ((step instanceof PutAndWaitStep) && ((PutAndWaitStep) step).getDevice() instanceof PrageDevice) {
				putAndWaitOnPrageStep = (PutAndWaitStep) step;
			} else if ((step instanceof PickAfterWaitStep) && ((PickAfterWaitStep) step).getDevice() instanceof PrageDevice) {
				pickAfterWaitOnPrageStep = (PickAfterWaitStep) step;
			} else if ((step instanceof PutStep) && ((PutStep) step).getDevice() instanceof CNCMillingMachine) {
				putInMachineStep = (PutStep) step;
			} else if ((step instanceof PickStep) && ((PickStep) step).getDevice() instanceof CNCMillingMachine) {
				pickFromMachineStep = (PickStep) step;
			} else if ((step instanceof PutStep) && ((PutStep) step).getDevice() instanceof BasicStackPlate) {
				putOnStackerStep = (PutStep) step;
			}
		}
	}
}
