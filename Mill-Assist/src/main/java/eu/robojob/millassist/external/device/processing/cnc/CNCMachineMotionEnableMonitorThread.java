package eu.robojob.millassist.external.device.processing.cnc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.threading.MonitoringThread;

/**
 * Class to check the state of the device interface. In case the the OK flag while preparing for put or pick suddenly 
 * switches to NOT OK, then the robot has to stop its movement. This class will check the state of the device interface
 * and will notify the robot in case he has to stop its actions.
 * 
 * @since v2.8.0
 * @version v2.8.0
 */
public class CNCMachineMotionEnableMonitorThread implements Runnable, MonitoringThread {

    public static final boolean PUT_ACTION = true;
    public static final boolean PICK_ACTION = false;
    
    /**
     * Machine to monitor
     */
    private final AbstractCNCMachine cncMachine;
    /**
     * Robot to control
     */
    private AbstractRobot robot;
    /**
     * Flag to indicate whether the thread is alive or not
     */
    private boolean alive;
    /**
     * Flag indicating that the robot was interrupted or not
     */
    private boolean isInterrupted;
    /**
     * Flag to indicate the action to monitor is a put (true) or a pick action (false)
     */
    private final boolean putActionToMonitor;
    /**
     * Boolean indicating whether this thread is currently monitoring
     */
    private boolean busyMonitoring;
    private Object syncObject;
    private Logger logger = LogManager.getLogger(CNCMachineMotionEnableMonitorThread.class.getName());

    /**
     * Constructor
     * 
     * @param cncMachine 
     *          machine to monitor
     * @param putActionToMonitor
     *          flag to indicate which action to monitor (put - true or pick - false)
     */
    public CNCMachineMotionEnableMonitorThread(final AbstractCNCMachine cncMachine, final boolean putActionToMonitor) {
        this.alive = true;
        this.cncMachine = cncMachine;
        this.putActionToMonitor = putActionToMonitor;
        this.isInterrupted = false;
        this.busyMonitoring = false;
        syncObject = new Object();
    }
    
    public void startExecution(AbstractRobot robotToControl) {
        this.robot = robotToControl;
        busyMonitoring = true;
        logger.debug("Started monitoring in " + toString());
        synchronized (syncObject) {
            syncObject.notify();
        }
    }
    
    @Override
    public void run() {
        while (alive) {
            if (busyMonitoring) {
                if (cncMachine.isConnected() && robot != null) {
                    if (putActionToMonitor) {
                        checkStatusDrop(CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PUT_OK);
                        //checkStatusDropV1();
                    } else {
                        //checkStatusDropV1();
                        checkStatusDrop(CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PICK_OK);
                    }
                }
            } else {
                synchronized (syncObject) {
                    try {
                        stopMonitoring();
                        syncObject.wait();
                    } catch (InterruptedException exception) {
                        stopMonitoring();
                    }
                }
            }
        }
        logger.info(toString() + " ended...");
    }
    
    private void checkStatusDrop(int commandToMonitor) {
        // Check that the status OK goes from OK to NOK
        try {
            boolean allowed = cncMachine.waitForStatusGoneDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, commandToMonitor, 1);

            // in case nothing goes wrong, we are going to wait forever (or until the next time the prepare is reset)

            // Status of device interface is gone, so stop robot movement if not stopped already
            if (!isInterrupted && alive && allowed) {
                robot.enableMovement(false);
                this.isInterrupted = true;
                // Start checking until the status is back OK or abort 
                cncMachine.waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, commandToMonitor);
                this.isInterrupted = false;
                robot.enableMovement(true);
            }
        } catch (DeviceActionException exception) {
            logger.error(exception);
            exception.printStackTrace();
        } catch (InterruptedException exception) {
            stopMonitoring();
        } catch (AbstractCommunicationException exception) {
            logger.error(exception);
            exception.printStackTrace();
        } catch (RobotActionException exception) {
            logger.error(exception);
            exception.printStackTrace();
        }     
    }
    
    public void stopMonitoring() {
        logger.debug("Stopped monitoring in " + toString());
        busyMonitoring = false;
        this.isInterrupted = false;
    }

    @Override
    public void stopExecution() {
        stopMonitoring();
        this.alive = false;
    }
    
    @Override
    public String toString() {
        String name = "CNCMachineMotionEnableMonitorThread for ";
        if (putActionToMonitor) {
            name += "put action";
        } else {
            name += "pick action";
        }
        return name;
    }
}
