package eu.robojob.irscw.db.external.device;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.db.ConnectionManager;
import eu.robojob.irscw.db.GeneralMapper;
import eu.robojob.irscw.db.external.util.ConnectionMapper;
import eu.robojob.irscw.external.communication.socket.SocketConnection;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.external.device.processing.prage.PrageDevice;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateLayout;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.UserFrame;

public class DeviceMapper {
	
	private static final int DEVICE_TYPE_CNCMILLING = 1;
	private static final int DEVICE_TYPE_STACKPLATE = 2;
	private static final int DEVICE_TYPE_PRAGE = 3;
	private static final int CLAMPING_TYPE_CENTRUM = 1;
	private static final int CLAMPING_TYPE_FIXED = 2;
	private static final int CLAMPING_TYPE_NONE = 3;
	
	private GeneralMapper generalMapper;
	private ConnectionMapper connectionMapper;
	
	public DeviceMapper(final GeneralMapper generalMapper, final ConnectionMapper connectionMapper) {
		this.generalMapper = generalMapper;
		this.connectionMapper = connectionMapper;
	}
	
	public Set<AbstractDevice> getAllDevices() throws SQLException {
		Statement stmt = ConnectionManager.getConnection().createStatement();
		ResultSet results = stmt.executeQuery("SELECT * FROM DEVICE");
		HashSet<AbstractDevice> devices = new HashSet<AbstractDevice>();
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			int type = results.getInt("TYPE");
			Set<Zone> zones = getAllZonesByDeviceId(id);
			switch (type) {
				case DEVICE_TYPE_CNCMILLING:
					CNCMillingMachine cncMillingMachine = getCNCMillingMachine(id, name, zones);
					devices.add(cncMillingMachine);
					break;
				case DEVICE_TYPE_STACKPLATE:
					BasicStackPlate basicStackPlate = getBasicStackPlate(id, name, zones);
					devices.add(basicStackPlate);
					break;
				case DEVICE_TYPE_PRAGE:
					PrageDevice prageDevice = new PrageDevice(name, zones);
					prageDevice.setId(id);
					devices.add(prageDevice);
					break;
				default:
					throw new IllegalStateException("Unknown device type: [" + type + "].");
			}
		}
		return devices;
	}
	
	private BasicStackPlate getBasicStackPlate(final int id, final String name, final Set<Zone> zones) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM STACKPLATE WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		BasicStackPlate stackPlate = null;
		if (results.next()) {
			int horizontalHoleAmount = results.getInt("HORIZONTALHOLEAMOUNT");
			int verticalHoleAmount = results.getInt("VERTICALHOLEAMOUNT");
			float holeDiameter = results.getFloat("HOLEDIAMETER");
			float studDiameter = results.getFloat("STUDDIAMETER");
			float horizontalPadding = results.getFloat("HORIZONTALPADDING");
			float verticalPaddingTop = results.getFloat("VERTICALPADDINGTOP");
			float verticalPaddingBottom = results.getFloat("VERTICALPADDINGBOTTOM");
			float horizontalHoleDistance = results.getFloat("HORIZONTALHOLEDISTANCE");
			float interferenceDistance = results.getFloat("INTERFERENCEDISTANCE");
			float overflowPercentage = results.getFloat("OVERFLOWPERCENTAGE");
			float horizontalR = results.getFloat("HORIZONTAL_R");
			float tiltedR = results.getFloat("TILTED_R");
			BasicStackPlateLayout layout = new BasicStackPlateLayout(horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPaddingTop, 
					verticalPaddingBottom, horizontalHoleDistance, interferenceDistance, overflowPercentage, horizontalR, tiltedR);
			stackPlate = new BasicStackPlate(name, zones, layout);
			stackPlate.setId(id);
		}
		return stackPlate;
	}
	
	private CNCMillingMachine getCNCMillingMachine(final int id, final String name, final Set<Zone> zones) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM CNCMILLINGMACHINE WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		CNCMillingMachine cncMillingMachine = null;
		if (results.next()) {
			int deviceInterfaceId = results.getInt("DEVICEINTERFACE");
			float clampingLengthR = results.getFloat("CLAMPING_LENGTH_R");
			float clampingWidthR = results.getFloat("CLAMPING_WIDTH_R");
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICEINTERFACE WHERE ID = ?");
			stmt2.setInt(1, deviceInterfaceId);
			ResultSet results2 = stmt2.executeQuery();
			if (results2.next()) {
				int socketConnectionId = results2.getInt("SOCKETCONNECTION");
				SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
				cncMillingMachine = new CNCMillingMachine(name, zones, socketConnection, clampingLengthR, clampingWidthR);
				cncMillingMachine.setId(id);
			}
		}
		return cncMillingMachine;
	}
	
	private Set<Zone> getAllZonesByDeviceId(final int deviceId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ZONE WHERE DEVICE = ?");
		stmt.setInt(1, deviceId);
		ResultSet results = stmt.executeQuery();
		Set<Zone> zones = new HashSet<Zone>();
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			Set<WorkArea> workAreas = getAllWorkAreasByZoneId(id);
			Zone zone = new Zone(name, workAreas);
			zone.setId(id);
			zones.add(zone);
		}
		return zones;
	}
	
	private Set<WorkArea> getAllWorkAreasByZoneId(final int zoneId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM WORKAREA WHERE ZONE = ?");
		stmt.setInt(1, zoneId);
		ResultSet results = stmt.executeQuery();
		Set<WorkArea> workAreas = new HashSet<WorkArea>();
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			int userFrameId = results.getInt("USERFRAME");
			UserFrame userFrame = generalMapper.getUserFrameById(userFrameId);
			Set<Clamping> possibleClampings = getClampingsByWorkAreaId(id);
			WorkArea workArea = new WorkArea(name, userFrame, possibleClampings);
			workArea.setId(id);
			workAreas.add(workArea);
		}
		return workAreas;
	}
	
	private Set<Clamping> getClampingsByWorkAreaId(final int workAreaId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM WORKAREA_CLAMPING WHERE WORKAREA = ?");
		stmt.setInt(1, workAreaId);
		ResultSet results = stmt.executeQuery();
		Set<Clamping> clampings = new HashSet<Clamping>();
		while (results.next()) {
			int clampingId = results.getInt("CLAMPING");
			Clamping clamping = getClampingById(clampingId);
			clampings.add(clamping);
		}
		return clampings;
	}
	
	private Clamping getClampingById(final int clampingId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM CLAMPING WHERE ID = ?");
		stmt.setInt(1, clampingId);
		ResultSet results = stmt.executeQuery();
		Clamping clamping = null;
		if (results.next()) {
			int type = results.getInt("TYPE");
			int relativePositionId = results.getInt("RELATIVE_POSITION");
			Coordinates relativePosition = generalMapper.getCoordinatesById(0, relativePositionId);
			int smoothToId = results.getInt("SMOOTH_TO");
			Coordinates smoothTo = generalMapper.getCoordinatesById(0, smoothToId);
			int smoothFromId = results.getInt("SMOOTH_FROM");
			Coordinates smoothFrom = generalMapper.getCoordinatesById(0, smoothFromId);
			float height = results.getFloat("HEIGHT");
			String imageUrl = results.getString("IMAGE_URL");
			String name = results.getString("NAME");
			switch(type) {
				case CLAMPING_TYPE_CENTRUM:
					clamping = new Clamping(Clamping.Type.CENTRUM, name, height, relativePosition, smoothTo, smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED:
					clamping = new Clamping(Clamping.Type.FIXED, name, height, relativePosition, smoothTo, smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_NONE:
					clamping = new Clamping(Clamping.Type.NONE, name, height, relativePosition, smoothTo, smoothFrom, imageUrl);
					break;
				default:
					throw new IllegalStateException("Unknown clamping type: [" + type + "].");
			}
			clamping.setId(clampingId);
		}
		return clamping;
	}
	
	public Set<UserFrame> getAllUserFrames() throws SQLException {
		return generalMapper.getAllUserFrames();
	}
	
	public UserFrame getUserFrameByName(final String name) throws SQLException {
		return generalMapper.getUserFrameByName(name);
	}
	
	public void updateUserFrame(final UserFrame userFrame, final String name, final int number, final float zSafeDistance, 
			final float x, final float y, final float z, final float w, final float p, final float r) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		if ((!userFrame.getName().equals(name)) || (userFrame.getNumber() != number) || (userFrame.getzSafeDistance() != zSafeDistance) ||
				(userFrame.getLocation().getX() != x) || (userFrame.getLocation().getY() != y) || (userFrame.getLocation().getZ() != z) 
					|| (userFrame.getLocation().getW() != w) || (userFrame.getLocation().getP() != p) || (userFrame.getLocation().getR() != r)) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE USERFRAME SET NAME = ?, NUMBER = ?, ZSAFE = ? WHERE ID = ?");
			stmt.setString(1, name);
			stmt.setInt(2, number);
			stmt.setFloat(3, zSafeDistance);
			stmt.setInt(4, userFrame.getId());
			stmt.executeUpdate();
			userFrame.setName(name);
			userFrame.setNumber(number);
			userFrame.setzSafeDistance(zSafeDistance);
			userFrame.getLocation().setX(x);
			userFrame.getLocation().setY(y);
			userFrame.getLocation().setZ(z);
			userFrame.getLocation().setW(w);
			userFrame.getLocation().setP(p);
			userFrame.getLocation().setR(r);
			generalMapper.saveCoordinates(userFrame.getLocation());
		}
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void updateUserFrame(final UserFrame userFrame) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE USERFRAME SET NAME = ?, NUMBER = ?, ZSAFE = ? WHERE ID = ?");
		stmt.setString(1, userFrame.getName());
		stmt.setInt(2, userFrame.getNumber());
		stmt.setFloat(3, userFrame.getzSafeDistance());
		stmt.setInt(4, userFrame.getId());
		stmt.executeUpdate();
		generalMapper.saveCoordinates(userFrame.getLocation());
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void saveUserFrame(final UserFrame userFrame) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		generalMapper.saveCoordinates(userFrame.getLocation());
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO USERFRAME (NUMBER, ZSAFE, LOCATION, NAME) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, userFrame.getNumber());
		stmt.setFloat(2, userFrame.getzSafeDistance());
		stmt.setInt(3, userFrame.getLocation().getId());
		stmt.setString(4, userFrame.getName());
		stmt.executeUpdate();
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			userFrame.setId(resultSet.getInt(1));
		}
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void updateWorkArea(final WorkArea workArea) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE WORKAREA SET ZONE = ?, USERFRAME = ?, NAME = ? WHERE ID = ?");
		stmt.setInt(1, workArea.getZone().getId());
		stmt.setInt(2, workArea.getUserFrame().getId());
		stmt.setString(3, workArea.getName());
		stmt.setInt(4, workArea.getId());
		stmt.executeUpdate();
	}
	
	public void updateBasicStackPlate(final BasicStackPlate basicStackPlate, final String name, final String userFrameName, final int horizontalHoleAmount, final int verticalHoleAmount, 
			final float holeDiameter, final float studDiameter, final float horizontalHoleDistance, final float horizontalPadding, 
			final float verticalPaddingTop, final float verticalPaddingBottom, final float interferenceDistance, final float overflowPercentage,
			final float horizontalR, final float tiltedR, final float smoothToX, final float smoothToY, final float smoothToZ,
			final float smoothFromX, final float smoothFromY, final float smoothFromZ) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		if ((!basicStackPlate.getWorkAreas().get(0).getUserFrame().getName().equals(userFrameName))) {
			UserFrame newUserFrame = getUserFrameByName(userFrameName);
			basicStackPlate.getWorkAreas().get(0).setUserFrame(newUserFrame);
			updateWorkArea(basicStackPlate.getWorkAreas().get(0));
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE STACKPLATE SET HORIZONTALHOLEAMOUNT = ?, VERTICALHOLEAMOUNT = ?, HOLEDIAMETER = ?, " +
				"STUDDIAMETER = ?, HORIZONTALPADDING = ?, VERTICALPADDINGTOP = ?, VERTICALPADDINGBOTTOM = ?, HORIZONTALHOLEDISTANCE = ?, INTERFERENCEDISTANCE = ?, " +
				" OVERFLOWPERCENTAGE = ?, HORIZONTAL_R = ?, TILTED_R = ? WHERE ID = ?");
		stmt.setInt(1, horizontalHoleAmount);
		stmt.setInt(2, verticalHoleAmount);
		stmt.setFloat(3, holeDiameter);
		stmt.setFloat(4, studDiameter);
		stmt.setFloat(5, horizontalPadding);
		stmt.setFloat(6, verticalPaddingTop);
		stmt.setFloat(7, verticalPaddingBottom);
		stmt.setFloat(8, horizontalHoleDistance);
		stmt.setFloat(9, interferenceDistance);
		stmt.setFloat(10, overflowPercentage);
		stmt.setFloat(11, horizontalR);
		stmt.setFloat(12, tiltedR);
		stmt.setInt(13, basicStackPlate.getId());
		stmt.execute();
		PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("UPDATE DEVICE SET NAME = ? WHERE ID = ?");
		stmt2.setString(1, name);
		stmt2.setInt(2, basicStackPlate.getId());
		stmt2.execute();
		Coordinates smoothTo = basicStackPlate.getWorkAreas().get(0).getActiveClamping().getSmoothToPoint();
		Coordinates smoothFrom = basicStackPlate.getWorkAreas().get(0).getActiveClamping().getSmoothFromPoint();
		smoothTo.setX(smoothToX);
		smoothTo.setY(smoothToY);
		smoothTo.setZ(smoothToZ);
		smoothFrom.setX(smoothFromX);
		smoothFrom.setY(smoothFromY);
		smoothFrom.setZ(smoothFromZ);
		generalMapper.saveCoordinates(smoothTo);
		generalMapper.saveCoordinates(smoothFrom);
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
		basicStackPlate.setName(name);
		basicStackPlate.getLayout().setHorizontalHoleAmount(horizontalHoleAmount);
		basicStackPlate.getLayout().setVerticalHoleAmount(verticalHoleAmount);
		basicStackPlate.getLayout().setHoleDiameter(holeDiameter);
		basicStackPlate.getLayout().setStudDiameter(studDiameter);
		basicStackPlate.getLayout().setHorizontalPadding(horizontalPadding);
		basicStackPlate.getLayout().setVerticalPadding(verticalPaddingTop);
		basicStackPlate.getLayout().setVerticalPaddingBottom(verticalPaddingBottom);
		basicStackPlate.getLayout().setHorizontalHoleDistance(horizontalHoleDistance);
		basicStackPlate.getLayout().setInterferenceDistance(interferenceDistance);
		basicStackPlate.getLayout().setOverflowPercentage(overflowPercentage);
		basicStackPlate.getLayout().setHorizontalR(horizontalR);
		basicStackPlate.getLayout().setTiltedR(tiltedR);
	}
	
	public void updateCNCMachine(final CNCMillingMachine cncMachine, final String name, final String ipAddress, 
			final int port, final String workAreaName, final String userFramename) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		cncMachine.getWorkAreas().get(0).setName(workAreaName);
		cncMachine.getWorkAreas().get(0).setUserFrame(getUserFrameByName(userFramename));
		updateWorkArea(cncMachine.getWorkAreas().get(0));
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE SOCKETCONNECTION " +
				"SET IPADDRESS = ?, PORTNR = ?, NAME = ? WHERE ID = ?");
		stmt.setString(1, ipAddress);
		stmt.setInt(2, port);
		stmt.setString(3, name);
		stmt.setInt(4, cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().getId());
		stmt.execute();
		PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("UPDATE DEVICE SET NAME = ? WHERE ID = ?");
		stmt2.setString(1, name);
		stmt2.setInt(2, cncMachine.getId());
		stmt2.execute();
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
		cncMachine.setName(name);
		cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().setIpAddress(ipAddress);
		cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().setPortNumber(port);
		cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().setName(name);
		cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().disconnect();
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
}
