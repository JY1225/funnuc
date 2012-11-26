package eu.robojob.irscw.ui.teach;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPutSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.TeachedCoordinatesCalculator;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.PutAndWaitStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.workpiece.WorkPiece;

public class OptimizedTeachThread extends TeachThread {
	
	private static final Logger logger = Logger.getLogger(OptimizedTeachThread.class);
	private TeachedCoordinatesCalculator calculator;

	public OptimizedTeachThread(ProcessFlow processFlow) {
		super(processFlow);
		this.calculator = new TeachedCoordinatesCalculator();
	}

	@Override
	public void run() {
		
		// if the device, corresponding to the first PICK step has a fixed Pick position (which for now, is the case) 
		// then the relationship between the grippers and the pieces they take can be teached on this device, providing the dimensions (width / height) 
		// of the workpiece don't change during the process (they can be placed on the same location on this first device, their center's are on the same location (except the height))
		// (which for now, is the case)
		
		// when this has been done, all that's left to teach is the position of the put location of clampings where the put location is not fixed
		// and when this is done for a certain clamping, related clampings are updated automatically. 
		
		// this last part is done by going through the process and when no teaching is needed in the future, the used should be able to switch to the automate view
		
		
		
		// This implementation will be less generic, and take into account two possible ProcessFlow layouts: 
		// -  BasicStackPlate - PrageDevice - CNCMillingMachine - BasicStackPlate 
		//    or 
		// -  BasicStackPlate - CNCMillingMachine - BasicStackPlate
		
		
		try {
			this.running = true;
			
			processFlow.initialize();
			processFlow.setMode(Mode.TEACH);
			
			for (AbstractProcessStep step: processFlow.getProcessSteps()) {
				if (step instanceof AbstractTransportStep) {
					((AbstractTransportStep) step).setRelativeTeachedOffset(null);
				}
			}

			for (AbstractRobot robot :processFlow.getRobots()) {
				robot.restartProgram();
				robot.setSpeed(10);
				robot.recalculateTCPs();
			}
			for (AbstractDevice device: processFlow.getDevices()) {
				device.prepareForProcess(processFlow);
			}
			
			// In each case: we will start with teaching the finished workPiece
			PickStep pickFromStackerStep = null;
			PutAndWaitStep putAndWaitOnPrageStep = null;
			PickAfterWaitStep pickAfterWaitOnPrageStep = null;
			PutStep putInMachineStep = null;
			PickStep pickFromMachineStep = null;
			PutStep putOnStackerStep = null;
			
			Coordinates relTeachedOffsetFinishedWp = null;
	
			for(AbstractProcessStep step : processFlow.getProcessSteps()) {
				if ((step instanceof PickStep) && ((PickStep) step).getDevice().getId().equals(DeviceManager.IRS_M_BASIC)) {
					pickFromStackerStep = (PickStep) step;
				} 
				else if ((step instanceof PutAndWaitStep) && ((PutAndWaitStep) step).getDevice().getId().equals(DeviceManager.PRAGE_DEVICE)) {
					putAndWaitOnPrageStep = (PutAndWaitStep) step;
				} 
				else if ((step instanceof PickAfterWaitStep) && ((PickAfterWaitStep) step).getDevice().getId().equals(DeviceManager.PRAGE_DEVICE)) {
					pickAfterWaitOnPrageStep = (PickAfterWaitStep) step;
				}
				else if ((step instanceof PutStep) && ((PutStep) step).getDevice().getId().equals(DeviceManager.MAZAK_VRX)) {
					putInMachineStep = (PutStep) step;
				}
				else if ((step instanceof PickStep) && ((PickStep) step).getDevice().getId().equals(DeviceManager.MAZAK_VRX)) {
					pickFromMachineStep = (PickStep) step;
				}
				else if ((step instanceof PutStep) && ((PutStep) step).getDevice().getId().equals(DeviceManager.IRS_M_BASIC)) {
					putOnStackerStep = (PutStep) step;
				}
			}
			
			
			// before doing this, we fake the gripper holding a workpiece
			putOnStackerStep.getRobotSettings().getGripperHead().getGripper().setWorkPiece(pickFromMachineStep.getRobotSettings().getWorkPiece());
			relTeachedOffsetFinishedWp = getFinishedWorkPieceTeachedOffset(putOnStackerStep);
			
			pickFromMachineStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);
			putOnStackerStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);
			
			Coordinates relTeachedOffsetRawWp = null;
			Coordinates relTeachedOffsetMachineClamping = null;
			
			// we now execute the first step: pick from basic stack plate
			boolean knowEnough = false;
			while(processFlow.hasStep() && !knowEnough) {
				AbstractProcessStep step = processFlow.getCurrentStep();
				
				if (step.equals(pickFromStackerStep)) {
					pickFromStackerStep.prepareForTeaching();
					pickFromStackerStep.teachingFinished();
					relTeachedOffsetRawWp = pickFromStackerStep.getRelativeTeachedOffset();
					if (putAndWaitOnPrageStep != null) {
						// preset the teached offset of the präge and cnc so teaching will go easier here!
						putAndWaitOnPrageStep.setRelativeTeachedOffset(relTeachedOffsetRawWp);
					}
					putInMachineStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);
				} else if (step.equals(putAndWaitOnPrageStep)) {
					putAndWaitOnPrageStep.prepareForTeaching();
					putAndWaitOnPrageStep.teachingFinished();
					relTeachedOffsetMachineClamping = putAndWaitOnPrageStep.getRelativeTeachedOffset();
					Coordinates offsetInMachine = new Coordinates(relTeachedOffsetMachineClamping);
					putInMachineStep.setRelativeTeachedOffset(offsetInMachine);
					// note that when teached on the Präge-Device the teached Y-offset should not be used
					// for now we take it and assume it's nearly zero
					//TODO think about this (in the machine we want to be sure the piece is clamped in the center)
				} else if (step.equals(putInMachineStep)) {
					putInMachineStep.prepareForTeaching();
					putInMachineStep.teachingFinished();
					relTeachedOffsetMachineClamping = putInMachineStep.getRelativeTeachedOffset();
					knowEnough = true;
				} else if (step.equals(pickAfterWaitOnPrageStep)) {
					pickAfterWaitOnPrageStep.executeStep();
					knowEnough = true;
				} else {
					step.executeStep();
				}
				processFlow.nextStep();
			}
			Coordinates pickFromMachineOffset = new Coordinates(relTeachedOffsetMachineClamping);
			pickFromMachineOffset.minus(relTeachedOffsetRawWp);
			pickFromMachineOffset.plus(relTeachedOffsetFinishedWp);
			pickFromMachineStep.setRelativeTeachedOffset(pickFromMachineOffset);
			putOnStackerStep.setRelativeTeachedOffset(relTeachedOffsetFinishedWp);
			logger.info("ended optimized teach thread!");
			
			this.running = false;

			processFlow.setMode(Mode.READY);
		} catch (CommunicationException | RobotActionException | DeviceActionException e) {
			e.printStackTrace();
			notifyException(e);
			processFlow.setMode(Mode.STOPPED);
		} catch (InterruptedException e) {
			e.printStackTrace();
			processFlow.setMode(Mode.STOPPED);
		}
	}
	
	private Coordinates getFinishedWorkPieceTeachedOffset(PutStep putOnStackerStep) throws CommunicationException, RobotActionException, InterruptedException {
		logger.info("About to get teached offset of finished workpiece");
		Coordinates teachedOffsetFinishedWp = null;
		BasicStackPlate stackPlate = (BasicStackPlate) putOnStackerStep.getDevice();
		FanucRobot fRobot = (FanucRobot) putOnStackerStep.getRobot();
		FanucRobotPutSettings putSettings = (FanucRobotPutSettings) putOnStackerStep.getRobotSettings();
		// we set the first workpiece to be finished
		stackPlate.getLayout().getStackingPositions().get(0).getWorkPiece().setType(WorkPiece.Type.FINISHED);
		processFlow.setFinishedAmount(1);
		logger.info("First taking care of setting first stacking position to finished workpiece and amount of finished workpieces to one");
		Coordinates originalCoordinates = stackPlate.getLocation(putOnStackerStep.getRobotSettings().getWorkArea(), WorkPiece.Type.FINISHED, processFlow.getClampingType());
		putSettings.setLocation(originalCoordinates);
		logger.info("Original coordinates: " + originalCoordinates);
		fRobot.teachedMoveNoWait(putSettings, false);
		Coordinates coordinates = new Coordinates(fRobot.getPosition());
		teachedOffsetFinishedWp = coordinates.calculateOffset(originalCoordinates);
		Coordinates relTeachedOffsetFinishedWp = calculator.calculateRelativeTeachedOffset(originalCoordinates, teachedOffsetFinishedWp);
		logger.info("Teached offset (relative): " + relTeachedOffsetFinishedWp);
		fRobot.moveAway();
		return relTeachedOffsetFinishedWp;
	}
	
}
