package eu.robojob.millassist.process.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.bin.OutputBin;
import eu.robojob.millassist.external.device.stacking.conveyor.Conveyor;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;
import eu.robojob.millassist.external.robot.fanuc.FanucRobotPutSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.TeachedCoordinatesCalculator;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PickAfterWaitStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.PutAndWaitStep;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.workpiece.WorkPiece;

public class TeachOptimizedThread extends TeachThread {
	
	private PickStep pickFromStackingDeviceStep = null;
	private PutAndWaitStep putAndWaitOnPrageStep = null;
	private PickAfterWaitStep pickAfterWaitOnPrageStep = null;
	private PutStep putInMachineStep = null;
	private PickStep pickFromMachineStep = null;
	private PutStep putOnStackingDeviceStep = null;

	private static Logger logger = LogManager.getLogger(TeachOptimizedThread.class.getName());
	private static final int WORKPIECE_ID = 0;
	
	public TeachOptimizedThread(final ProcessFlow processFlow) {
		super(processFlow);
		pickFromStackingDeviceStep = null;
		putAndWaitOnPrageStep = null;
		pickAfterWaitOnPrageStep = null;
		putInMachineStep = null;
		pickFromMachineStep = null;
		putOnStackingDeviceStep = null;
	}

	//TODO generalize this method for more complex ProcessFlows
	//TODO check what happens with interventions!
	@Override
	public void run() {
		// This implementation will be less generic, and take into account two possible ProcessFlow layouts: 
		// -  StackingDevice - PrageDevice - CNCMillingMachine - StackingDevice 
		//    or 
		// -  StackingDevice - CNCMillingMachine - StackingDevice
		logger.debug("Started execution, processflow [" + getProcessFlow() + "].");
		setRunning(true);
		try {
			getProcessFlow().initialize();
			getProcessFlow().setMode(Mode.TEACH);
			//resetOffsets();
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
				putOnStackingDeviceStep.getRobotSettings().getGripperHead().getGripper().setWorkPiece(pickFromMachineStep.getRobotSettings().getWorkPiece());
				relTeachedOffsetFinishedWp = getFinishedWorkPieceTeachedOffset(putOnStackingDeviceStep);
				//TODO review if this offset needs formatting (depending on clamp manner...)
				logger.info("Relative offset finished work piece after added extra offset: [" + relTeachedOffsetFinishedWp + "].");
				pickFromMachineStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);
				putOnStackingDeviceStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);
				Coordinates relTeachedOffsetRawWp = null;
				Coordinates relTeachedOffsetMachineClamping = null;
				boolean knowEnough = false;
				getProcessFlow().setCurrentIndex(WORKPIECE_ID, 0);
				while ((getProcessFlow().getCurrentIndex(WORKPIECE_ID) < getProcessFlow().getProcessSteps().size()) && !knowEnough && isRunning()) {
					AbstractProcessStep step = getProcessFlow().getProcessSteps().get((getProcessFlow().getCurrentIndex(WORKPIECE_ID)));
					if (step.equals(pickFromStackingDeviceStep)) {
						pickFromStackingDeviceStep.executeStepTeached(WORKPIECE_ID, this);
						pickFromStackingDeviceStep.finalizeStep(this);
						// update relative offset for upcoming steps
						relTeachedOffsetRawWp = pickFromStackingDeviceStep.getRelativeTeachedOffset();
						if (putAndWaitOnPrageStep != null) {
							putAndWaitOnPrageStep.setRelativeTeachedOffset(relTeachedOffsetRawWp);
						}
						//putInMachineStep.setRelativeTeachedOffset(relTeachedOffsetRawWp);
					} else if (step.equals(putAndWaitOnPrageStep)) {
						//putAndWaitOnPrageStep.setRelativeTeachedOffset(null);
						putAndWaitOnPrageStep.executeStepTeached(WORKPIECE_ID, this);
						putAndWaitOnPrageStep.finalizeStep(this);
						relTeachedOffsetMachineClamping = putAndWaitOnPrageStep.getRelativeTeachedOffset();
						Coordinates offsetInMachine = new Coordinates(relTeachedOffsetMachineClamping);
						putInMachineStep.setRelativeTeachedOffset(offsetInMachine);
						//TODO what to do with y offset of Präge?
						//FIXME test and take into account clamping type!!
					} else if (step.equals(putInMachineStep)) {
						//putInMachineStep.setRelativeTeachedOffset(null);
						putInMachineStep.getRobotSettings().setFreeAfter(true);
						putInMachineStep.executeStepTeached(WORKPIECE_ID, this);
						putInMachineStep.finalizeStep(this);
						relTeachedOffsetMachineClamping = putInMachineStep.getRelativeTeachedOffset();
						knowEnough = true;
					} else if (step.equals(pickAfterWaitOnPrageStep)) {
						pickAfterWaitOnPrageStep.executeStep(WORKPIECE_ID, this);
						pickAfterWaitOnPrageStep.finalizeStep(this);
						knowEnough = true;
						pickAfterWaitOnPrageStep.getRobot().moveToHome();
					} else if (!(step instanceof InterventionStep)) {
						step.executeStep(WORKPIECE_ID, this);
					}
					getProcessFlow().setCurrentIndex(WORKPIECE_ID, getProcessFlow().getCurrentIndex(WORKPIECE_ID) + 1);
				}
				if (isRunning()) {
					Coordinates pickFromMachineOffset = new Coordinates(relTeachedOffsetMachineClamping);
					
					Coordinates wpInMachineOrientation = new Coordinates(putInMachineStep.getDevice().getLocationOrientation(putInMachineStep.getDeviceSettings().getWorkArea()));
					wpInMachineOrientation.plus(relTeachedOffsetMachineClamping);
					wpInMachineOrientation.minus(pickFromStackingDeviceStep.getRelativeTeachedOffset());
					wpInMachineOrientation.setX(0);
					wpInMachineOrientation.setY(0);
					wpInMachineOrientation.setZ(0);
					
					Coordinates wpDiffRelativeOffset = new Coordinates(relTeachedOffsetFinishedWp);
					wpDiffRelativeOffset.minus(relTeachedOffsetRawWp);
					Coordinates extraWpRelOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(wpInMachineOrientation, wpDiffRelativeOffset);
					extraWpRelOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(new Coordinates(putInMachineStep.getDevice().getLocationOrientation(putInMachineStep.getDeviceSettings().getWorkArea())), extraWpRelOffset);
					//pickFromMachineOffset.minus(relTeachedOffsetRawWp);
					//pickFromMachineOffset.plus(relTeachedOffsetFinishedWp);
					
					pickFromMachineOffset.plus(extraWpRelOffset);
					
					pickFromMachineStep.setRelativeTeachedOffset(pickFromMachineOffset);
					putOnStackingDeviceStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);		
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
	
	private Coordinates getFinishedWorkPieceTeachedOffset(final PutStep putOnStackerStep) throws AbstractCommunicationException, RobotActionException, InterruptedException, DeviceActionException {
		logger.debug("About to get teached offset of finished workpiece first.");
		AbstractStackingDevice stackingDevice = (AbstractStackingDevice) putOnStackerStep.getDevice();
		FanucRobot fRobot = (FanucRobot) putOnStackerStep.getRobot();
		FanucRobotPutSettings putSettings = (FanucRobotPutSettings) putOnStackerStep.getRobotSettings();
		// we set the first work piece as a finished
		putOnStackerStep.getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(putOnStackerStep.getProcessFlow(), putOnStackerStep, StatusChangedEvent.STARTED, WORKPIECE_ID));
		AbstractStackingDevice stackingDeviceBuffer = null;
		if (stackingDevice instanceof OutputBin) {
			// pretend to use first device for optimal teaching
			stackingDeviceBuffer = stackingDevice;
			stackingDevice = (AbstractStackingDevice) pickFromStackingDeviceStep.getDevice();
		}
		if (stackingDevice instanceof BasicStackPlate) {
			((BasicStackPlate) stackingDevice).getLayout().getStackingPositions().get(0).setWorkPiece(((BasicStackPlate) stackingDevice).getFinishedWorkPiece());
			((BasicStackPlate) stackingDevice).getLayout().getStackingPositions().get(0).setAmount(1);
		} else if (stackingDevice instanceof Conveyor) {
			// FIXME implement
			throw new IllegalStateException("Not yet implemented!");
		}  
		getProcessFlow().setFinishedAmount(1);
		Coordinates originalCoordinates = stackingDevice.getLocation(putOnStackerStep.getRobotSettings().getWorkArea(), WorkPiece.Type.FINISHED, getProcessFlow().getClampingType());
		if (putOnStackerStep.needsTeaching()) {
			Coordinates position = new Coordinates(originalCoordinates);
			logger.debug("Original coordinates: " + position + ".");
			if (putOnStackerStep.getRelativeTeachedOffset() != null) {
				logger.debug("The teached offset that will be used: [" + putOnStackerStep.getRelativeTeachedOffset() + "].");
				Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, putOnStackerStep.getRelativeTeachedOffset());
				logger.debug("The absolute offset that will be used: [" + absoluteOffset + "].");
				position.offset(absoluteOffset);
				logger.debug("Exact put location: [" + position + "].");
			}
			putSettings.setLocation(position);
		} else {
			Coordinates position = new Coordinates(originalCoordinates);
			logger.debug("Exact put location (calculated without teaching): [" + position + "].");
			putSettings.setLocation(position);
		}
		putSettings.setTeachingNeeded(true);
		putSettings.setFreeAfter(false);
		// TODO refactor, as the robot does not really have a piece
		if (!fRobot.lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + fRobot + "] was already locked by [" + fRobot.getLockingProcess() + "].");
		} else {
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), putOnStackerStep, StatusChangedEvent.PREPARE_DEVICE, WORKPIECE_ID));
			stackingDevice.prepareForPut(putOnStackerStep.getDeviceSettings());
			logger.debug("Original coordinates: " + originalCoordinates + ".");
			logger.debug("Initiating robot: [" + fRobot + "] move action.");
			Coordinates smoothPointBuffer = null;
			if (stackingDeviceBuffer != null) {
				smoothPointBuffer = putSettings.getSmoothPoint();
				putSettings.setSmoothPoint(pickFromStackingDeviceStep.getRobotSettings().getSmoothPoint());
			}
			fRobot.initiateMoveWithoutPieceNoAction(putSettings);
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), putOnStackerStep, StatusChangedEvent.EXECUTE_TEACHED, WORKPIECE_ID));
			// reset stacking device and smooth point if buffered
			if (stackingDeviceBuffer != null) {
				stackingDevice = stackingDeviceBuffer;
				putSettings.setSmoothPoint(smoothPointBuffer);
			}
			fRobot.continueMoveTillAtLocation();
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), putOnStackerStep, StatusChangedEvent.TEACHING_NEEDED, WORKPIECE_ID));
			fRobot.continueMoveTillWait();
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), putOnStackerStep, StatusChangedEvent.TEACHING_FINISHED, WORKPIECE_ID));
			Coordinates coordinates = new Coordinates(fRobot.getPosition());
			Coordinates relTeachedOffsetFinishedWp = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalCoordinates, coordinates.calculateOffset(originalCoordinates));
			logger.info("The relative teached offset (finished workpiece): [" + relTeachedOffsetFinishedWp + "].");
			fRobot.continueMoveWithoutPieceTillIPPoint();
			logger.info("In IP point");
			fRobot.finalizeMovePiece();
			logger.info("finalized move");
			putOnStackerStep.getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(putOnStackerStep.getProcessFlow(), putOnStackerStep, StatusChangedEvent.ENDED, WORKPIECE_ID));
			return relTeachedOffsetFinishedWp;
		}
	}
	
	private void initializeSteps() {
		for (AbstractProcessStep step : getProcessFlow().getProcessSteps()) {
			if ((step instanceof PickStep) && ((PickStep) step).getDevice() instanceof AbstractStackingDevice) {
				pickFromStackingDeviceStep = (PickStep) step;
			} else if ((step instanceof PutAndWaitStep) && ((PutAndWaitStep) step).getDevice() instanceof PrageDevice) {
				putAndWaitOnPrageStep = (PutAndWaitStep) step;
			} else if ((step instanceof PickAfterWaitStep) && ((PickAfterWaitStep) step).getDevice() instanceof PrageDevice) {
				pickAfterWaitOnPrageStep = (PickAfterWaitStep) step;
			} else if ((step instanceof PutStep) && ((PutStep) step).getDevice() instanceof CNCMillingMachine) {
				putInMachineStep = (PutStep) step;
			} else if ((step instanceof PickStep) && ((PickStep) step).getDevice() instanceof CNCMillingMachine) {
				pickFromMachineStep = (PickStep) step;
			} else if ((step instanceof PutStep) && ((PutStep) step).getDevice() instanceof AbstractStackingDevice) {
				putOnStackingDeviceStep = (PutStep) step;
			}
		}
	}
}
