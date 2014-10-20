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
import eu.robojob.millassist.db.external.util.ConnectionMapper;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.Gripper;
import eu.robojob.millassist.external.robot.Gripper.Type;
import eu.robojob.millassist.external.robot.GripperBody;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;

public class RobotMapper {

	private static final int ROBOT_TYPE_FANUC = 1;
	private static final int GRIPPER_TYPE_TWOPOINT = 1;
	private static final int GRIPPER_TYPE_VACUUM = 2;
	private ConnectionMapper connectionMapper;
	private Map<Integer, GripperBody> gripperBodiesBuffer;
	private Map<Integer, GripperHead> gripperHeadsBuffer;
	private Map<Integer, Gripper> grippersBuffer;
	
	public RobotMapper(final ConnectionMapper connectionMapper) {
		this.connectionMapper = connectionMapper;
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
			Set<GripperBody> gripperBodies = getAllGripperBodiesByRobotId(id);
			switch (type) {
				case ROBOT_TYPE_FANUC:
					FanucRobot fanucRobot = getFanucRobot(id, name, gripperBodies, payload);
					robots.add(fanucRobot);
					break;
				default:
					throw new IllegalStateException("Unknown robot type: [" + type + "].");
			}
		}
		return robots;
	}
	
	private FanucRobot getFanucRobot(final int id, final String name, final Set<GripperBody> gripperBodies, final float payload) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM FANUCROBOT WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		FanucRobot fanucRobot = null;
		if (results.next()) {
			int socketConnectionId = results.getInt("SOCKETCONNECTION");
			SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
			fanucRobot = new FanucRobot(name, gripperBodies, null, payload, socketConnection);
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
				throw new IllegalArgumentException("Unkwon gripper type: " + type);
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
}
