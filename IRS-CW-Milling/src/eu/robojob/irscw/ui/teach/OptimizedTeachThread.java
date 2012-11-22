package eu.robojob.irscw.ui.teach;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPutSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.PutAndWaitStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.workpiece.WorkPiece;

public class OptimizedTeachThread extends TeachThread {
	
	private static final Logger logger = Logger.getLogger(OptimizedTeachThread.class);

	public OptimizedTeachThread(ProcessFlow processFlow) {
		super(processFlow);
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

			// In each case: we will start with teaching the finished workPiece
			PickStep pickFromStackerStep = null;
			PutAndWaitStep putAndWaitOnPrageStep = null;
			PickAfterWaitStep pickAfterWaitOnPrageStep = null;
			PutStep putInMachineStep = null;
			PickStep pickFromMachineStep = null;
			PutStep putOnStackerStep = null;
			
			Coordinates teachedOffsetFinishedWp = null;
	
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
			teachedOffsetFinishedWp = getFinishedWorkPieceTeachedOffset(putOnStackerStep);
			
			Coordinates teachedOffsetRawWp = null;
			Coordinates teachedOffsetMachineClamping = null;
			
			// we now execute the first step: pick from basic stack plate
			boolean knowEnough = false;
			while(processFlow.hasStep() && !knowEnough) {
				AbstractProcessStep step = processFlow.getCurrentStep();
				
				if (step.equals(pickFromStackerStep)) {
					pickFromStackerStep.prepareForTeaching();
					pickFromStackerStep.teachingFinished();
					teachedOffsetRawWp = pickFromStackerStep.getTeachedOffset();
				} else if (step.equals(putAndWaitOnPrageStep)) {
					putAndWaitOnPrageStep.prepareForTeaching();
					putAndWaitOnPrageStep.teachingFinished();
					teachedOffsetMachineClamping = putAndWaitOnPrageStep.getTeachedOffset();
					Coordinates offsetInMachine = new Coordinates(teachedOffsetMachineClamping);
					putInMachineStep.setTeachedOffset(offsetInMachine);
					// note that when teached on the Präge-Device the teached Y-offset should not be used
					// for now we take it and assume it's nearly zero
					//TODO think about this (in the machine we want to be sure the piece is clamped in the center)
				} else if (step.equals(putInMachineStep)) {
					putInMachineStep.prepareForTeaching();
					putInMachineStep.teachingFinished();
					teachedOffsetMachineClamping = putInMachineStep.getTeachedOffset();
					knowEnough = true;
				} else if (step.equals(pickAfterWaitOnPrageStep)) {
					pickAfterWaitOnPrageStep.executeStep();
					knowEnough = true;
				} else {
					step.executeStep();
				}
				processFlow.nextStep();
			}
			Coordinates pickFromMachineOffset = new Coordinates(teachedOffsetMachineClamping);
			pickFromMachineOffset.minus(teachedOffsetRawWp);
			pickFromMachineOffset.plus(teachedOffsetFinishedWp);
			pickFromMachineStep.setTeachedOffset(pickFromMachineOffset);
			putOnStackerStep.setTeachedOffset(teachedOffsetFinishedWp);
			logger.info("ended optimized teach thread!");
			
			this.running = false;

			processFlow.setMode(Mode.READY);
		} catch (CommunicationException | RobotActionException | InterruptedException | DeviceActionException e) {
			e.printStackTrace();
			notifyException(e);
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
		Coordinates originalCoordinates = stackPlate.getLocation(putOnStackerStep.getRobotSettings().getWorkArea(), WorkPiece.Type.FINISHED);
		putSettings.setLocation(originalCoordinates);
		logger.info("Original coordinates: " + originalCoordinates);
		fRobot.teachedMoveNoWait(putSettings, false);
		Coordinates coordinates = new Coordinates(fRobot.getPosition());
		teachedOffsetFinishedWp = coordinates.calculateOffset(originalCoordinates);
		logger.info("Teached offset: " + teachedOffsetFinishedWp);
		fRobot.moveAway();
		return teachedOffsetFinishedWp;
	}
	
}
