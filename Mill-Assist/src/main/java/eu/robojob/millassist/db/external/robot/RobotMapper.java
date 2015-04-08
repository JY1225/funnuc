package eu.robojob.millassist.db.external.robot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.robojob.millassist.db.ConnectionManager;
import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.db.external.util.ConnectionMapper;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.external.robot.Gripper;
import eu.robojob.millassist.external.robot.RobotDataManager;
import eu.robojob.millassist.external.robot.Gripper.Type;
import eu.robojob.millassist.external.robot.RobotData.RobotIPPoint;
import eu.robojob.millassist.external.robot.RobotData.RobotRefPoint;
import eu.robojob.millassist.external.robot.RobotData.RobotRegister;
import eu.robojob.millassist.external.robot.RobotData.RobotSpecialPoint;
import eu.robojob.millassist.external.robot.RobotData.RobotToolFrame;
import eu.robojob.millassist.external.robot.RobotData.RobotUserFrame;
import eu.robojob.millassist.external.robot.GripperBody;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;
import eu.robojob.millassist.positioning.RobotPosition;

public class RobotMapper {

	private static final int ROBOT_TYPE_FANUC = 1;
	private static final int GRIPPER_TYPE_TWOPOINT = 1;
	private static final int GRIPPER_TYPE_VACUUM = 2;
	private ConnectionMapper connectionMapper;
	private GeneralMapper generalMapper;
	private Map<Integer, GripperBody> gripperBodiesBuffer;
	private Map<Integer, GripperHead> gripperHeadsBuffer;
	private Map<Integer, Gripper> grippersBuffer;
	
	public RobotMapper(final GeneralMapper generalMapper, final ConnectionMapper connectionMapper) {
		this.connectionMapper = connectionMapper;
		this.generalMapper = generalMapper;
		gripperBodiesBuffer = new HashMap<Integer, GripperBody>();
		gripperHeadsBuffer = new HashMap<Integer, GripperHead>();
		grippersBuffer = new HashMap<Integer, Gripper>();
	}
	
	public void clearBuffers() {
		gripperBodiesBuffer.clear();
		gripperHeadsBuffer.clear();
		grippersBuffer.clear();
	}
	
	public Set<AbstractRobot> getAllRobots() throws SQLException {
		Statement stmt = ConnectionManager.getConnection().createStatement();
		ResultSet results = stmt.executeQuery("SELECT * FROM ROBOT");
		HashSet<AbstractRobot> robots = new HashSet<AbstractRobot>();
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			int type = results.getInt("TYPE");
			float payload = results.getFloat("PAYLOAD");
			boolean acceptData = results.getBoolean("ACCEPT_DATA");
			Set<GripperBody> gripperBodies = getAllGripperBodiesByRobotId(id);
			switch (type) {
				case ROBOT_TYPE_FANUC:
					FanucRobot fanucRobot = getFanucRobot(id, name, gripperBodies, payload, acceptData);
					robots.add(fanucRobot);
					break;
				default:
					throw new IllegalStateException("Unknown robot type: [" + type + "].");
			}
		}
		return robots;
	}
	
	private FanucRobot getFanucRobot(final int id, final String name, final Set<GripperBody> gripperBodies, final float payload, boolean acceptData) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM FANUCROBOT WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		FanucRobot fanucRobot = null;
		if (results.next()) {
			int socketConnectionId = results.getInt("SOCKETCONNECTION");
			SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
			fanucRobot = new FanucRobot(name, gripperBodies, null, payload, socketConnection, acceptData);
			fanucRobot.setId(id);
		}
		return fanucRobot;
	}
	
	private Set<GripperBody> getAllGripperBodiesByRobotId(final int id) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT GRIPPERBODY FROM ROBOT_GRIPPERBODY WHERE ROBOT = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		Set<GripperBody> gripperBodies = new HashSet<GripperBody>();
		while (results.next()) {
			int gripperBodyId = results.getInt("GRIPPERBODY");
			GripperBody gripperBody = getGripperBodyById(gripperBodyId);
			gripperBodies.add(gripperBody);
		}
		return gripperBodies;
	}
	
	private GripperBody getGripperBodyById(final int id) throws SQLException {
		GripperBody gripperBody = null;
		gripperBody = gripperBodiesBuffer.get(id);
		if (gripperBody == null) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM GRIPPERBODY WHERE ID = ?");
			stmt.setInt(1, id);
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				String name = results.getString("NAME");
				String description = results.getString("DESCRIPTION");
				Set<GripperHead> gripperHeads = getGripperHeadsByGripperBodyId(id);
				gripperBody = new GripperBody(name, description, gripperHeads);
				gripperBody.setId(id);
			}
			gripperBodiesBuffer.put(id, gripperBody);
		}
		return gripperBody;
	}
	
	private Set<GripperHead> getGripperHeadsByGripperBodyId(final int gripperBodyId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM GRIPPERHEAD WHERE GRIPPERBODY = ?");
		stmt.setInt(1, gripperBodyId);
		ResultSet results = stmt.executeQuery();
		Set<GripperHead> gripperHeads = new HashSet<GripperHead>();
		while (results.next()) {
			int id = results.getInt("ID");
			GripperHead gripperHead = null;
			gripperHead = gripperHeadsBuffer.get(id);
			if (gripperHead == null) {
				String name = results.getString("NAME");
				Set<Gripper> grippers = getGrippersByGripperHeadId(id);
				gripperHead = new GripperHead(name, grippers, null);
				gripperHead.setId(id);
				gripperHeadsBuffer.put(id, gripperHead);
			}
			gripperHeads.add(gripperHead);
		}
		return gripperHeads;
	}
	
	private Set<Gripper> getGrippersByGripperHeadId(final int gripperBodyId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT GRIPPER FROM GRIPPERHEAD_GRIPPER WHERE GRIPPERHEAD = ?");
		stmt.setInt(1, gripperBodyId);
		ResultSet results = stmt.executeQuery();
		Set<Gripper> grippers = new HashSet<Gripper>();
		while (results.next()) {
			int id = results.getInt("GRIPPER");
			Gripper gripper = getGripperById(id);
			grippers.add(gripper);
		}
		return grippers;
	}
	
	private Gripper getGripperById(final int id) throws SQLException {
		Gripper gripper = null;
		gripper = grippersBuffer.get(id);
		if (gripper == null) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM GRIPPER WHERE ID = ?");
			stmt.setInt(1, id);
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				float height = results.getFloat("HEIGHT");
				boolean fixedHeight = results.getBoolean("FIXEDHEIGHT");
				String name = results.getString("NAME");
				String description = results.getString("DESCRIPTION");
				String imageUrl = results.getString("IMAGE_URL");
				int typeId = results.getInt("TYPE");
				Gripper.Type type = Gripper.Type.TWOPOINT;
				if (typeId == GRIPPER_TYPE_TWOPOINT) {
					type = Gripper.Type.TWOPOINT;
				} else if (typeId == GRIPPER_TYPE_VACUUM) {
					type = Gripper.Type.VACUUM;
				} else {
					throw new IllegalArgumentException("Unkown gripper type id: " + typeId);
				}
				gripper = new Gripper(name, type, height, description, imageUrl);
				gripper.setFixedHeight(fixedHeight);
				gripper.setId(id);
			}
			grippersBuffer.put(id, gripper);
		}
		return gripper;
	}
	
	public void updateRobotData(final FanucRobot robot, final String name, final String ip, final int port, 
			final boolean hasGripperHeadA, final boolean hasGripperHeadB, final boolean hasGripperHeadC, final boolean hasGripperHeadD,
				final float payload) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		if (!robot.getName().equals(name) || (robot.getPayload() != payload)) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE ROBOT SET NAME = ?, PAYLOAD = ? WHERE ID = ?");
			stmt.setString(1, name);
			stmt.setFloat(2,  payload);
			stmt.setInt(3, robot.getId());
			stmt.executeUpdate();
			robot.setName(name);
			robot.setPayload(payload);
		}
		if ((!robot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().getIpAddress().equals(ip)) 
				|| (robot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().getPortNumber() != port)
				|| (!robot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().getName().equals(name))) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE SOCKETCONNECTION SET IPADDRESS = ?, PORTNR = ?, NAME = ? WHERE ID = ?");
			stmt.setString(1, ip);
			stmt.setInt(2, port);
			stmt.setString(3, name);
			stmt.setInt(4, robot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().getId());
			stmt.executeUpdate();
			robot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().setName(name);
			robot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().setPortNumber(port);
			robot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().setIpAddress(ip);
			robot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().disconnect();
		}
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
		// TODO updating of gripper heads
	}
	
	public static void updateRobotAcceptDataFlag(final FanucRobot robot, final boolean acceptsData) throws SQLException {
	    ConnectionManager.getConnection().setAutoCommit(false);
	    try {
	        PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE ROBOT SET ACCEPT_DATA = ? WHERE ID = ?");
	        stmt.setBoolean(1, acceptsData);
	        stmt.setInt(2, robot.getId());
	        stmt.executeUpdate();
	        robot.setAcceptData(acceptsData);
	        ConnectionManager.getConnection().commit();
	    } catch (SQLException e) {
	        ConnectionManager.getConnection().rollback();
	    } finally {
	        ConnectionManager.getConnection().setAutoCommit(true);
	    }
	}
	
	public void updateGripperData(final Gripper gripper, final String name, final Gripper.Type type, final String imgUrl, final float height, final boolean fixedHeight, 
			final boolean headA, final boolean headB, final boolean headC, final boolean headD) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		if ((!gripper.getName().equals(name)) || (!gripper.getImageUrl().equals(imgUrl)) || (gripper.getHeight() != height)
				|| (gripper.isFixedHeight() != fixedHeight) || (gripper.getType() != type)) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE GRIPPER SET HEIGHT = ?, FIXEDHEIGHT = ?, NAME = ?, IMAGE_URL = ?, TYPE = ? WHERE ID = ?");
			stmt.setFloat(1, height);
			stmt.setBoolean(2, fixedHeight);
			stmt.setString(3, name);
			stmt.setString(4, imgUrl);
			int gripperTypeId = GRIPPER_TYPE_TWOPOINT;
			if (type == Type.TWOPOINT) {
				gripperTypeId = GRIPPER_TYPE_TWOPOINT;
			} else if (type == Type.VACUUM) {
				gripperTypeId = GRIPPER_TYPE_VACUUM;
			} else {
				throw new IllegalArgumentException("Unknown gripper type: " + type);
			}
			stmt.setInt(5, gripperTypeId);
			stmt.setInt(6, gripper.getId());
			stmt.executeUpdate();
			gripper.setName(name);
			gripper.setImageUrl(imgUrl);
			gripper.setHeight(height);
			gripper.setFixedHeight(fixedHeight);
			gripper.setType(type);
		}
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
		// TODO updating of gripper head compatibility
	}
	
	public void saveGripper(final Gripper gripper, final GripperHead... heads) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO GRIPPER (NAME, IMAGE_URL, HEIGHT, FIXEDHEIGHT, TYPE) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, gripper.getName());
		stmt.setString(2, gripper.getImageUrl());
		stmt.setFloat(3, gripper.getHeight());
		stmt.setBoolean(4, gripper.isFixedHeight());
		int typeInt = GRIPPER_TYPE_TWOPOINT;
		if (gripper.getType() == Type.TWOPOINT) {
			typeInt = GRIPPER_TYPE_TWOPOINT;
		} else if (gripper.getType() == Type.VACUUM) {
			typeInt = GRIPPER_TYPE_VACUUM;
		}
		stmt.setInt(5, typeInt);
		try {
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				gripper.setId(resultSet.getInt(1));
				for (GripperHead head : heads) {
					PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("INSERT INTO GRIPPERHEAD_GRIPPER (GRIPPERHEAD, GRIPPER) VALUES (?, ?)");
					stmt2.setInt(1, head.getId());
					stmt2.setInt(2, gripper.getId());
					stmt2.executeUpdate();
				}
				ConnectionManager.getConnection().commit();
			}
		} catch (SQLException e) {
			ConnectionManager.getConnection().rollback();
			throw e;
		} finally {
			ConnectionManager.getConnection().setAutoCommit(true);
		}
	}
	
	public void deleteGripper(final Gripper gripper) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		try {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM GRIPPERHEAD_GRIPPER WHERE GRIPPER = ?");
			stmt.setInt(1, gripper.getId());
			stmt.executeUpdate();
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("DELETE FROM GRIPPER WHERE ID = ?");
			stmt2.setInt(1, gripper.getId());
			stmt2.executeUpdate();
			ConnectionManager.getConnection().commit();
		} catch (SQLException e) {
			ConnectionManager.getConnection().rollback();
			throw e;
		} finally {
			ConnectionManager.getConnection().setAutoCommit(true);
		}
	}
	
	/**
	 * Overwrite the robot points that are currently stored in the database by the ones read from the robot.
	 * 
	 * @throws SQLException
	 */
	public void saveRobotData() throws SQLException {
	    // delete all information from database IPPoints, RPPoints, SpecialPoints
	    ConnectionManager.getConnection().setAutoCommit(false);
	    try {
	        deleteRobotPoints();
	        saveIPPoints(RobotDataManager.getIpPoints());
	        saveRPPoints(RobotDataManager.getRpPoints());
	        saveSpecialPoints(RobotDataManager.getSpecialPoints());
	        saveUserframes(RobotDataManager.getUserframes());
	        saveToolFrames(RobotDataManager.getToolframes());
	        saveRegisters(RobotDataManager.getRegisters());
	    } catch (SQLException e) {
	        ConnectionManager.getConnection().rollback();
	    } finally {
	        ConnectionManager.getConnection().setAutoCommit(true);
	    }
	}

	private void saveIPPoints(final Map<RobotIPPoint, RobotPosition> ipPoints) throws SQLException {
	    try {
	        PreparedStatement stmtIP = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROB_IP_POINT(UF_NR, TF_NR, POS_TYPE, LOCATION, CONFIG) VALUES (?,?,?,?,?)");
	        //Re-use stmt for all IP points + do not forget to save CONFIG
	        for (RobotIPPoint ipPoint: ipPoints.keySet()) {
	            stmtIP.setInt(1, ipPoint.getUfNr());
	            stmtIP.setInt(2, ipPoint.getTfNr());
	            stmtIP.setInt(3, ipPoint.getPosType().getId());
	            generalMapper.saveCoordinates(ipPoints.get(ipPoint).getPosition());
	            stmtIP.setInt(4, ipPoints.get(ipPoint).getPosition().getId());
	            generalMapper.saveConfig(ipPoints.get(ipPoint).getConfiguration());
	            stmtIP.setInt(5, ipPoints.get(ipPoint).getConfiguration().getId());
	            stmtIP.executeUpdate();
	        }
	        ConnectionManager.getConnection().commit();
	    } catch (SQLException e) {
	        ConnectionManager.getConnection().rollback();
	    }
	}

	private void saveRPPoints(final Map<RobotRefPoint, RobotPosition> rpPoints) throws SQLException {
        try {
            PreparedStatement stmtRP = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROB_RP_POINT(UF_NR, TF_NR, ORIGINAL_TF_NR, LOCATION, CONFIG) VALUES (?,?,?,?,?)");
            //Re-use stmt for all RP points
            for (RobotRefPoint rpPoint: rpPoints.keySet()) {
                stmtRP.setInt(1, rpPoint.getUfNr());
                stmtRP.setInt(2, rpPoint.getTfNr());
                stmtRP.setInt(3, rpPoint.getOriginalTfNr());
                generalMapper.saveCoordinates(rpPoints.get(rpPoint).getPosition());
                stmtRP.setInt(4, rpPoints.get(rpPoint).getPosition().getId());
                generalMapper.saveConfig(rpPoints.get(rpPoint).getConfiguration());
                stmtRP.setInt(5, rpPoints.get(rpPoint).getConfiguration().getId());
                stmtRP.executeUpdate();
            }
            ConnectionManager.getConnection().commit();
        } catch (SQLException e) {
            ConnectionManager.getConnection().rollback();
        }
	}

	private void saveSpecialPoints(final Map<RobotSpecialPoint, RobotPosition> specialPoints) throws SQLException {
        try {
            PreparedStatement stmtSpecial = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROB_SPECIAL_POINT(SPEC_ID, LOCATION, CONFIG) VALUES (?,?,?)");
            //Re-use stmt for all IP points
            for (RobotSpecialPoint specialPoint: specialPoints.keySet()) {
                stmtSpecial.setInt(1, specialPoint.getId());
                generalMapper.saveCoordinates(specialPoints.get(specialPoint).getPosition());
                stmtSpecial.setInt(2, specialPoints.get(specialPoint).getPosition().getId());
                generalMapper.saveConfig(specialPoints.get(specialPoint).getConfiguration());
                stmtSpecial.setInt(3, specialPoints.get(specialPoint).getConfiguration().getId());
                stmtSpecial.executeUpdate();
            }
            ConnectionManager.getConnection().commit();
        } catch (SQLException e) {
            ConnectionManager.getConnection().rollback();
        }
	}

	private void saveUserframes(final Map<RobotUserFrame, RobotPosition> userframes) throws SQLException {
	    try {
	        PreparedStatement stmtUf = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROB_USERFRAME(UF_NR, LOCATION, CONFIG) VALUES (?,?,?)");
	        //Re-use stmt for all userframes
	        for (RobotUserFrame userframe: userframes.keySet()) {
	            stmtUf.setInt(1, userframe.getUfNr());
	            generalMapper.saveCoordinates(userframes.get(userframe).getPosition());
	            stmtUf.setInt(2, userframes.get(userframe).getPosition().getId());
	            generalMapper.saveConfig(userframes.get(userframe).getConfiguration());
	            stmtUf.setInt(3, userframes.get(userframe).getConfiguration().getId());
	            stmtUf.executeUpdate();
	        }
	        ConnectionManager.getConnection().commit();
	    } catch (SQLException e) {
	        ConnectionManager.getConnection().rollback();
	    }
	}

	private void saveToolFrames(final Map<RobotToolFrame, RobotPosition> toolFrames) throws SQLException {
	    try {
	        PreparedStatement stmtTf = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROB_TOOLFRAME(TF_NR, LOCATION, CONFIG) VALUES (?,?,?)");
	        //Re-use stmt for all userframes
	        for (RobotToolFrame toolFrame: toolFrames.keySet()) {
	            stmtTf.setInt(1, toolFrame.getTfNr());
	            generalMapper.saveCoordinates(toolFrames.get(toolFrame).getPosition());
	            stmtTf.setInt(2, toolFrames.get(toolFrame).getPosition().getId());
	            generalMapper.saveConfig(toolFrames.get(toolFrame).getConfiguration());
	            stmtTf.setInt(3, toolFrames.get(toolFrame).getConfiguration().getId());
	            stmtTf.executeUpdate();
	        }
	        ConnectionManager.getConnection().commit();
	    } catch (SQLException e) {
	        ConnectionManager.getConnection().rollback();
	    }
	}

	private void saveRegisters(final Map<RobotRegister, Integer> registers) throws SQLException {
	    try {
	        PreparedStatement stmtReg = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROB_REGISTER(REG_ID, VALUE) VALUES (?,?)");
	        //Re-use stmt for all userframes
	        for (RobotRegister register: registers.keySet()) {
	            stmtReg.setInt(1, register.getId());
	            stmtReg.setInt(2, registers.get(register));
	            stmtReg.executeUpdate();
	        }
	        ConnectionManager.getConnection().commit();
	    } catch (SQLException e) {
	        ConnectionManager.getConnection().rollback();
	    }
	}

	private void deleteRobotPoints() throws SQLException {
	    ConnectionManager.getConnection().setAutoCommit(false);
	    try {
	        PreparedStatement stmtRP = ConnectionManager.getConnection().prepareStatement("DELETE FROM ROB_IP_POINT");
	        PreparedStatement stmtIP = ConnectionManager.getConnection().prepareStatement("DELETE FROM ROB_RP_POINT");
	        PreparedStatement stmtSpecial = ConnectionManager.getConnection().prepareStatement("DELETE FROM ROB_SPECIAL_POINT");
	        PreparedStatement stmtUserframe = ConnectionManager.getConnection().prepareStatement("DELETE FROM ROB_USERFRAME");
	        PreparedStatement stmtToolframe = ConnectionManager.getConnection().prepareStatement("DELETE FROM ROB_TOOLFRAME");
	        PreparedStatement stmtRegister = ConnectionManager.getConnection().prepareStatement("DELETE FROM ROB_REGISTER");
	        stmtRP.executeUpdate();
	        stmtIP.executeUpdate();
	        stmtSpecial.executeUpdate();
	        stmtUserframe.executeUpdate();
	        stmtToolframe.executeUpdate();
	        stmtRegister.executeUpdate();
	        ConnectionManager.getConnection().commit();
	        ConnectionManager.getConnection().setAutoCommit(true);
	    } catch (SQLException e) {
	        ConnectionManager.getConnection().rollback();
	    } 
	}
	
	public void readRobotData() throws SQLException {
	    readIPPoints();
	    readRPPoints();
	    readSpecialPoints();
	    readUserframes();
	    readToolframes();
	    readRegisters();
	}

	private void readIPPoints() throws SQLException {
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROB_IP_POINT");
	    ResultSet results = stmt.executeQuery();
	    while (results.next()) {
	        int ufNr = results.getInt("UF_NR");
	        int tfNr = results.getInt("TF_NR");
	        ApproachType posType = ApproachType.getById(results.getInt("POS_TYPE"));
	        RobotPosition position = new RobotPosition(generalMapper.getCoordinatesById(0, results.getInt("LOCATION")),
	                generalMapper.getConfigById(results.getInt("CONFIG")));
	        RobotIPPoint ippoint = RobotIPPoint.getIPPoint(ufNr, tfNr, posType);
	        RobotDataManager.addIPPoint(ippoint, position);
	    }
	}

	private void readRPPoints() throws SQLException {
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROB_RP_POINT");
	    ResultSet results = stmt.executeQuery();
	    while (results.next()) {
	        int ufNr = results.getInt("UF_NR");
	        int tfNr = results.getInt("TF_NR");
	        int originalTfNr = results.getInt("ORIGINAL_TF_NR");
	        RobotPosition position = new RobotPosition(generalMapper.getCoordinatesById(0, results.getInt("LOCATION")),
	                generalMapper.getConfigById(results.getInt("CONFIG")));
	        RobotRefPoint rppoint = RobotRefPoint.getRPPoint(ufNr, tfNr, originalTfNr);
	        RobotDataManager.addRPPoint(rppoint, position);
	    }
	}

	private void readSpecialPoints() throws SQLException {
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROB_SPECIAL_POINT");
	    ResultSet results = stmt.executeQuery();
	    while (results.next()) {
	        RobotSpecialPoint specialPoint = RobotSpecialPoint.getById(results.getInt("SPEC_ID"));
	        RobotPosition position = new RobotPosition(generalMapper.getCoordinatesById(0, results.getInt("LOCATION")),
	                generalMapper.getConfigById(results.getInt("CONFIG")));
	        RobotDataManager.addSpecialPoint(specialPoint, position);
	    }
	}

	private void readUserframes() throws SQLException {
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROB_USERFRAME");
	    ResultSet results = stmt.executeQuery();
	    while (results.next()) {
	        RobotUserFrame userframe = RobotUserFrame.getByUserFrameNr(results.getInt("UF_NR"));
	        RobotPosition position = new RobotPosition(generalMapper.getCoordinatesById(0, results.getInt("LOCATION")),
	                generalMapper.getConfigById(results.getInt("CONFIG")));
	        RobotDataManager.addUserframe(userframe, position);
	    }
	}

	private void readToolframes() throws SQLException {
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROB_TOOLFRAME");
	    ResultSet results = stmt.executeQuery();
	    while (results.next()) {
	        RobotToolFrame toolFrame = RobotToolFrame.getByToolFrameNr(results.getInt("TF_NR"));
	        RobotPosition position = new RobotPosition(generalMapper.getCoordinatesById(0, results.getInt("LOCATION")),
	                generalMapper.getConfigById(results.getInt("CONFIG")));
	        RobotDataManager.addToolFrame(toolFrame, position);
	    }
	}

	private void readRegisters() throws SQLException {
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROB_REGISTER");
	    ResultSet results = stmt.executeQuery();
	    while (results.next()) {
	        RobotRegister register = RobotRegister.getById(results.getInt("REG_ID"));
	        int value = results.getInt("VALUE");
	        RobotDataManager.addRegisterValue(register, value);
	    }
	}
}
