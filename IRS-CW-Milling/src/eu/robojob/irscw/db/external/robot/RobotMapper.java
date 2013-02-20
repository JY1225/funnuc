package eu.robojob.irscw.db.external.robot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.robojob.irscw.db.ConnectionManager;
import eu.robojob.irscw.db.external.util.ConnectionMapper;
import eu.robojob.irscw.external.communication.socket.SocketConnection;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.Gripper;
import eu.robojob.irscw.external.robot.GripperBody;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;

public class RobotMapper {

	private static final int ROBOT_TYPE_FANUC = 1;
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
			Set<GripperBody> gripperBodies = getAllGripperBodiesByRobotId(id);
			switch (type) {
				case ROBOT_TYPE_FANUC:
					FanucRobot fanucRobot = getFanucRobot(id, name, gripperBodies);
					robots.add(fanucRobot);
					break;
				default:
					throw new IllegalStateException("Unknown robot type: [" + type + "].");
			}
		}
		return robots;
	}
	
	private FanucRobot getFanucRobot(final int id, final String name, final Set<GripperBody> gripperBodies) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM FANUCROBOT WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		FanucRobot fanucRobot = null;
		if (results.next()) {
			int socketConnectionId = results.getInt("SOCKETCONNECTION");
			SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
			fanucRobot = new FanucRobot(name, gripperBodies, null, socketConnection);
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
				gripper = new Gripper(name, height, description, imageUrl);
				gripper.setFixedHeight(fixedHeight);
				gripper.setId(id);
			}
			grippersBuffer.put(id, gripper);
		}
		return gripper;
	}
}
