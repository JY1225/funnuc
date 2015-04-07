package eu.robojob.millassist.external.robot;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.external.robot.RobotMapper;
import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;
import eu.robojob.millassist.positioning.RobotData.RobotIPPoint;
import eu.robojob.millassist.positioning.RobotData.RobotRefPoint;
import eu.robojob.millassist.positioning.RobotData.RobotSpecialPoint;
import eu.robojob.millassist.positioning.RobotData.RobotToolFrame;
import eu.robojob.millassist.positioning.RobotData.RobotUserFrame;
import eu.robojob.millassist.positioning.RobotPosition;

public final class RobotDataManager {
    
    //FIXME - add flag to send the data to the robot
    private static FanucRobot robot;
    private static RobotMapper robotMapper;
    private static Map<RobotIPPoint, RobotPosition> ipPoints;
    private static Map<RobotRefPoint, RobotPosition> rpPoints;
    private static Map<RobotSpecialPoint, RobotPosition> specialPoints;
    private static Map<RobotUserFrame, RobotPosition> userframes;
    private static Map<RobotToolFrame, RobotPosition> toolframes;
    
    private static Logger logger = LogManager.getLogger(RobotDataManager.class.getName());
    
    private RobotDataManager() {   
        //Do nothing
    }
    
    public static void initialize(RobotMapper robotMapper, FanucRobot robot) {
        RobotDataManager.robot = robot;
        RobotDataManager.robotMapper = robotMapper;
        createIPPoints();
        createRPPoints();
        createSpecialPoints();
        createUserframes();
        createToolframes();
    }
    
    public static void exportDataToRobot() {
        readInformationFromDatabase();
        if (robot.isConnected()) {
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
    
    private static void writeIPPoints() {
        for (RobotIPPoint ipPoint: ipPoints.keySet()) {
            try {
                logger.debug("Writing " + ipPoint + " to robot");
                robot.writeIPPoint(ipPoint, ipPoints.get(ipPoint));
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void writeRPPoints() {
        for (RobotRefPoint rpPoint: rpPoints.keySet()) {
            try {
                logger.debug("Writing " + rpPoint + " to robot");
                robot.writeRPPoint(rpPoint, rpPoints.get(rpPoint));
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void writeSpecialPoints() {
        for (RobotSpecialPoint specialPoint: specialPoints.keySet()) {
            try {
                logger.debug("Writing " + specialPoint + " to robot");
                robot.writeSpecialPoint(specialPoint, specialPoints.get(specialPoint));
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void writeUserframes() {
        for (RobotUserFrame userframe: userframes.keySet()) {
            try {
                logger.debug("Writing " + userframe + " to robot");
                robot.writeUserFrame(userframe, userframes.get(userframe));
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void writeToolframes() {
        for (RobotToolFrame toolFrame: toolframes.keySet()) {
            try {
                logger.debug("Writing " + toolFrame + " to robot");
                robot.writeToolFrame(toolFrame, toolframes.get(toolFrame));
            } catch (AbstractCommunicationException | RobotActionException| InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void readInformationFromRobot() {
        readIPPoints();
        readRPPoints();
        readSpecialPoints();
        readUserframes();
        readToolframes();
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
        if (robot.acceptsData()) {
            writeIPPoints();
            writeRPPoints();
            writeSpecialPoints();
            writeUserframes();
            writeToolframes();
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
