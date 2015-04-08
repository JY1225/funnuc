package eu.robojob.millassist.external.robot;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.external.robot.RobotMapper;
import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.communication.socket.SocketDisconnectedException;
import eu.robojob.millassist.external.communication.socket.SocketResponseTimedOutException;
import eu.robojob.millassist.external.communication.socket.SocketWrongResponseException;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;
import eu.robojob.millassist.positioning.RobotData.RobotIPPoint;
import eu.robojob.millassist.positioning.RobotData.RobotRefPoint;
import eu.robojob.millassist.positioning.RobotData.RobotRegister;
import eu.robojob.millassist.positioning.RobotData.RobotSpecialPoint;
import eu.robojob.millassist.positioning.RobotData.RobotToolFrame;
import eu.robojob.millassist.positioning.RobotData.RobotUserFrame;
import eu.robojob.millassist.positioning.RobotPosition;

public final class RobotDataManager {
    
    /**
     * This flag is used to check whether the IPC has already sent data to the robot in the current session. The flag
     * will thus prevent the IPC from sending data in case the connection was lost for a few seconds. The flag can also
     * be used in case the connection of the robot comes after starting the IPC.
     */
    private static boolean alreadySentThisSession = false;
    private static FanucRobot robot;
    private static RobotMapper robotMapper;
    private static Map<RobotIPPoint, RobotPosition> ipPoints;
    private static Map<RobotRefPoint, RobotPosition> rpPoints;
    private static Map<RobotSpecialPoint, RobotPosition> specialPoints;
    private static Map<RobotUserFrame, RobotPosition> userframes;
    private static Map<RobotToolFrame, RobotPosition> toolframes;
    private static Map<RobotRegister, Integer> registers;
    
    private static Logger logger = LogManager.getLogger(RobotDataManager.class.getName());
    
    private RobotDataManager() {   
        //Do nothing
    }
    
    private static void setAlreadySentDataToRobot(boolean flag) {
        RobotDataManager.alreadySentThisSession = flag;
    }
    
    public static void initialize(RobotMapper robotMapper, FanucRobot robot) {
        RobotDataManager.robot = robot;
        RobotDataManager.robotMapper = robotMapper;
        createIPPoints();
        createRPPoints();
        createSpecialPoints();
        createUserframes();
        createToolframes();
        createRegisters();
    }
    
    public static void exportDataToRobot() {
        readInformationFromDatabase();
        if (robot.isConnected() && robot.acceptsData() && !alreadySentThisSession) {
            logger.debug("Writing information to robot");
            writeInformationToRobot();
        }
    }
    
    public static void importDataFromRobot() {
        if (robot.isConnected()) {
            readInformationFromRobot();
            writeInformationToDatabase();
        }
    }

    /**
     * Create default IP points with an empty RobotPosition (0,0,0,0,0,0,0,0,0,99,99,99)
     */
    private static void createIPPoints() {
        ipPoints = new HashMap<RobotIPPoint, RobotPosition>();
        for (RobotIPPoint ipPoint: RobotIPPoint.values()) {
            ipPoints.put(ipPoint, new RobotPosition());
        }
    }
    
    /**
     * Create default RP points with an empty RobotPosition (0,0,0,0,0,0,0,0,0,99,99,99)
     */
    private static void createRPPoints() {
        rpPoints = new HashMap<RobotRefPoint, RobotPosition>();
        for (RobotRefPoint refPoint: RobotRefPoint.values()) {
            rpPoints.put(refPoint, new RobotPosition());
        }
    }
    
    /**
     * Create default special points with an empty RobotPosition (0,0,0,0,0,0,0,0,0,99,99,99)
     */
    private static void createSpecialPoints() {
       specialPoints = new HashMap<RobotSpecialPoint, RobotPosition>();
       for (RobotSpecialPoint specialPoint: RobotSpecialPoint.values()) {
           specialPoints.put(specialPoint, new RobotPosition());
       }
    }
    
    private static void createUserframes() {
        userframes = new HashMap<RobotUserFrame, RobotPosition>();
        for (RobotUserFrame userframe: RobotUserFrame.values()) {
            userframes.put(userframe, new RobotPosition());
        }
    }
    
    private static void createToolframes() {
        toolframes = new HashMap<RobotToolFrame, RobotPosition>();
        for (RobotToolFrame toolFrame: RobotToolFrame.values()) {
            toolframes.put(toolFrame, new RobotPosition());
        }
    }
    
    /**
     * Create default register with default value -1.
     */
    private static void createRegisters() {
        registers = new HashMap<RobotRegister, Integer>();
        for (RobotRegister register: RobotRegister.values()) {
            //UNINIT
            registers.put(register, -1);
        }
    }
    
    private static void readIPPoints() {
        for (RobotIPPoint ipPoint: ipPoints.keySet()) {
            try {
                robot.readIPPoint(ipPoint);
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void readRPPoints() {
        for (RobotRefPoint rpPoint: rpPoints.keySet()) {
            try {
                robot.readRPPoint(rpPoint);
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void readSpecialPoints() {
        for (RobotSpecialPoint specialPoint: specialPoints.keySet()) {
            try {
                robot.readSpecialPoint(specialPoint);
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void readUserframes() {
        for (RobotUserFrame userframe: userframes.keySet()) {
            try {
                robot.readUserFrame(userframe);
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void readToolframes() {
        for (RobotToolFrame toolFrame: toolframes.keySet()) {
            try {
                robot.readToolFrame(toolFrame);
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void readRegisters() {
        for (RobotRegister register: registers.keySet()) {
            try {
                robot.readRegister(register);
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void writeIPPoints() throws AbstractCommunicationException, RobotActionException, InterruptedException {
        for (RobotIPPoint ipPoint: ipPoints.keySet()) {
            logger.debug("Writing " + ipPoint + " to robot");
            robot.writeIPPoint(ipPoint, ipPoints.get(ipPoint));
        }
    }
    
    private static void writeRPPoints() throws AbstractCommunicationException, RobotActionException, InterruptedException {
        for (RobotRefPoint rpPoint: rpPoints.keySet()) {
            logger.debug("Writing " + rpPoint + " to robot");
            robot.writeRPPoint(rpPoint, rpPoints.get(rpPoint));
        }
    }
    
    private static void writeSpecialPoints() throws AbstractCommunicationException, RobotActionException, InterruptedException {
        for (RobotSpecialPoint specialPoint: specialPoints.keySet()) {
            logger.debug("Writing " + specialPoint + " to robot");
            robot.writeSpecialPoint(specialPoint, specialPoints.get(specialPoint));
        }
    }
    
    private static void writeUserframes() throws AbstractCommunicationException, RobotActionException, InterruptedException {
        for (RobotUserFrame userframe: userframes.keySet()) {
            logger.debug("Writing " + userframe + " to robot");
            robot.writeUserFrame(userframe, userframes.get(userframe));
        }
    }

    private static void writeToolframes() throws AbstractCommunicationException, RobotActionException, InterruptedException {
        for (RobotToolFrame toolFrame: toolframes.keySet()) {
            logger.debug("Writing " + toolFrame + " to robot");
            robot.writeToolFrame(toolFrame, toolframes.get(toolFrame));
        }
    }
    
    private static void writeRegisters() throws SocketDisconnectedException, SocketResponseTimedOutException, SocketWrongResponseException, RobotActionException, InterruptedException {
        for (RobotRegister register: registers.keySet()) {
            logger.debug("Writing " + register + " to robot");
            robot.writeRegister(register.getId(), registers.get(register));
        }
    }
    
    private static void readInformationFromRobot() {
        readIPPoints();
        readRPPoints();
        readSpecialPoints();
        readUserframes();
        readToolframes();
        readRegisters();
    }
    
    private static void writeInformationToDatabase() {
        try {
            robotMapper.saveRobotData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void readInformationFromDatabase() {
        try {
            robotMapper.readRobotData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void writeInformationToRobot() {
        try {
            writeIPPoints();
            writeRPPoints();
            writeSpecialPoints();
            writeUserframes();
            writeToolframes();
            writeRegisters();
            RobotDataManager.setAlreadySentDataToRobot(true);
        } catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
            logger.info("Exception received when sending data to robot - closing IPC");
            e.printStackTrace();
            Platform.exit();
        }
    }

    public static Map<RobotIPPoint, RobotPosition> getIpPoints() {
        return ipPoints;
    }
    
    public static void addIPPoint(RobotIPPoint ippoint, RobotPosition position) {
        RobotDataManager.ipPoints.put(ippoint, position);
    }

    public static Map<RobotRefPoint, RobotPosition> getRpPoints() {
        return rpPoints;
    }
    
    public static void addRPPoint(RobotRefPoint rppoint, RobotPosition position) {
        RobotDataManager.rpPoints.put(rppoint, position);
    }

    public static Map<RobotSpecialPoint, RobotPosition> getSpecialPoints() {
        return specialPoints;
    }
    
    public static void addSpecialPoint(RobotSpecialPoint specialPoint, RobotPosition position) {
        RobotDataManager.specialPoints.put(specialPoint, position);
    }
    
    public static Map<RobotUserFrame, RobotPosition> getUserframes() {
        return userframes;
    }
    
    public static void addUserframe(RobotUserFrame userframe, RobotPosition position) {
        RobotDataManager.userframes.put(userframe, position);
    }
    
    public static Map<RobotToolFrame, RobotPosition> getToolframes() {
        return toolframes;
    }
    
    public static void addToolFrame(RobotToolFrame toolFrame, RobotPosition position) {
        RobotDataManager.toolframes.put(toolFrame, position);
    }
    
    public static Map<RobotRegister, Integer> getRegisters() {
        return registers;
    }
    
    public static void addRegisterValue(RobotRegister register, int value) {
        RobotDataManager.registers.put(register, value);
    }
    
    public static RobotPosition getPosition(String robotDataStringId) {
        for (RobotUserFrame userframe: userframes.keySet()) {
            if (userframe.toString().equals(robotDataStringId)) {
                return userframes.get(userframe);
            }
        }
        for (RobotRefPoint rpPoint: rpPoints.keySet()) {
            if (rpPoint.toString().equals(robotDataStringId)) {
                return rpPoints.get(rpPoint);
            }
        }
        for (RobotIPPoint ipPoint: ipPoints.keySet()) {
            if (ipPoint.toString().equals(robotDataStringId)) {
                return ipPoints.get(ipPoint);
            }
        }
        for (RobotSpecialPoint specialPoint: specialPoints.keySet()) {
            if (specialPoint.toString().equals(robotDataStringId)) {
                return specialPoints.get(specialPoint);
            }
        }
        for (RobotToolFrame toolFrame: toolframes.keySet()) {
            if (toolFrame.toString().equals(robotDataStringId)) {
                return toolframes.get(toolFrame);
            }
        }
        return null;
    }

}
