package eu.robojob.millassist.db.external.device;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.robojob.millassist.db.ConnectionManager;
import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.db.external.util.ConnectionMapper;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine.WayOfOperating;
import eu.robojob.millassist.external.device.processing.cnc.mcode.GenericMCode;
import eu.robojob.millassist.external.device.processing.cnc.mcode.MCodeAdapter;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.external.device.stacking.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.BasicStackPlateLayout;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.UserFrame;

public class DeviceMapper {
	
	private static final int DEVICE_TYPE_CNCMILLING = 1;
	private static final int DEVICE_TYPE_STACKPLATE = 2;
	private static final int DEVICE_TYPE_PRAGE = 3;
	private static final int CLAMPING_TYPE_CENTRUM = 1;
	private static final int CLAMPING_TYPE_FIXED = 2;
	private static final int CLAMPING_TYPE_NONE = 3;
	private static final int WAYOFOPERATING_STARTSTOP = 1;
	private static final int WAYOFOPERATING_MCODES = 2;
	
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
					AbstractCNCMachine cncMillingMachine = getCNCMillingMachine(id, name, zones);
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
	
	private AbstractCNCMachine getCNCMillingMachine(final int id, final String name, final Set<Zone> zones) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM CNCMILLINGMACHINE WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		AbstractCNCMachine cncMillingMachine = null;
		if (results.next()) {
			int deviceInterfaceId = results.getInt("DEVICEINTERFACE");
			float clampingLengthR = results.getFloat("CLAMPING_LENGTH_R");
			float clampingWidthR = results.getFloat("CLAMPING_WIDTH_R");
			int wayOfOperatingInt = results.getInt("WAYOFOPERATING");
			WayOfOperating wayOfOperating;
			if (wayOfOperatingInt == 1) {
				wayOfOperating = WayOfOperating.START_STOP;
			} else if (wayOfOperatingInt == 2) {
				wayOfOperating = WayOfOperating.M_CODES;
			} else {
				 throw new IllegalStateException("Unkown way of operating!");
			}
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICEINTERFACE WHERE ID = ?");
			stmt2.setInt(1, deviceInterfaceId);
			ResultSet results2 = stmt2.executeQuery();
			if (results2.next()) {
				int socketConnectionId = results2.getInt("SOCKETCONNECTION");
				SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
				cncMillingMachine = new CNCMillingMachine(name, wayOfOperating, getMCodeAdapter(id), zones, socketConnection, clampingLengthR, clampingWidthR);
				cncMillingMachine.setId(id);
			}
		}
		return cncMillingMachine;
	}
	
	public MCodeAdapter getMCodeAdapter(final int cncMachineId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM MCODEADAPTER WHERE ID = ?");
		stmt.setInt(1, cncMachineId);
		ResultSet results = stmt.executeQuery();
		MCodeAdapter mCodeAdapter = null;
		if (results.next()) {
			String robotServiceInput1Name = results.getString("ROBOTSERVICEINPUT1");
			String robotServiceInput2Name = results.getString("ROBOTSERVICEINPUT2");
			String robotServiceInput3Name = results.getString("ROBOTSERVICEINPUT3");
			String robotServiceInput4Name = results.getString("ROBOTSERVICEINPUT4");
			String robotServiceInput5Name = results.getString("ROBOTSERVICEINPUT5");
			String robotServiceOutput1Name = results.getString("ROBOTSERVICEOUTPUT1");
			List<String> robotServiceInputNames = new ArrayList<String>();
			robotServiceInputNames.add(robotServiceInput1Name);
			robotServiceInputNames.add(robotServiceInput2Name);
			robotServiceInputNames.add(robotServiceInput3Name);
			robotServiceInputNames.add(robotServiceInput4Name);
			robotServiceInputNames.add(robotServiceInput5Name);
			List<String> robotServiceOutputNames = new ArrayList<String>();
			robotServiceOutputNames.add(robotServiceOutput1Name);
			mCodeAdapter = new MCodeAdapter(getMCodes(cncMachineId), robotServiceInputNames, robotServiceOutputNames);
		}
		return mCodeAdapter;
	}
	
	public List<GenericMCode> getMCodes(final int cncMachineId) throws SQLException{
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM MCODE WHERE MCODEADAPTER = ?");
		stmt.setInt(1, cncMachineId);
		ResultSet results = stmt.executeQuery();
		List<GenericMCode> mCodes = new ArrayList<GenericMCode>();
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			boolean usesRobotServiceInput1 = results.getBoolean("ROBOTSERVICEINPUT1");
			boolean usesRobotServiceInput2 = results.getBoolean("ROBOTSERVICEINPUT2");
			boolean usesRobotServiceInput3 = results.getBoolean("ROBOTSERVICEINPUT3");
			boolean usesRobotServiceInput4 = results.getBoolean("ROBOTSERVICEINPUT4");
			boolean usesRobotServiceInput5 = results.getBoolean("ROBOTSERVICEINPUT5");
			Set<Integer> robotServiceInputsUsed = new HashSet<Integer>();
			if (usesRobotServiceInput1) {
				robotServiceInputsUsed.add(0);
			}
			if (usesRobotServiceInput2) {
				robotServiceInputsUsed.add(1);
			}
			if (usesRobotServiceInput3) {
				robotServiceInputsUsed.add(2);
			}
			if (usesRobotServiceInput4) {
				robotServiceInputsUsed.add(3);
			}
			if (usesRobotServiceInput5) {
				robotServiceInputsUsed.add(4);
			}
			boolean usesRobotServiceOutput1 = results.getBoolean("ROBOTSERVICEOUTPUT1");
			Set<Integer> robotServiceOutputsUsed = new HashSet<Integer>();
			if (usesRobotServiceOutput1) {
				robotServiceOutputsUsed.add(0);
			}
			int index = results.getInt("INDEX");
			GenericMCode mcode = new GenericMCode(id, index, name, robotServiceInputsUsed, robotServiceOutputsUsed);
			mCodes.add(index, mcode);
		}
		return mCodes;
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
	
	public void updateCNCMachine(final CNCMillingMachine cncMachine, final String name, final WayOfOperating wayOfOperating,
			final String ipAddress, final int port, final String workAreaName, final String userFramename, 
				final float clampingLengthR, final float clampingWidthR, final List<String> robotServiceInputNames, 
					final List<String> robotServiceOutputNames, final List<String> mCodeNames, 
						final List<Set<Integer>> mCodeRobotServiceInputs, final List<Set<Integer>> mCodeRobotServiceOutputs) throws SQLException {
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
		PreparedStatement stmt3 = ConnectionManager.getConnection().prepareStatement("UPDATE CNCMILLINGMACHINE SET CLAMPING_LENGTH_R = ?, CLAMPING_WIDTH_R = ? , WAYOFOPERATING = ? WHERE ID = ?");
		stmt3.setFloat(1, clampingLengthR);
		stmt3.setFloat(2, clampingWidthR);
		int wayOfOperatingInt = WAYOFOPERATING_STARTSTOP;
		if (wayOfOperating == WayOfOperating.M_CODES) {
			wayOfOperatingInt = WAYOFOPERATING_MCODES;
		} else if (wayOfOperating == WayOfOperating.START_STOP) {
			wayOfOperatingInt = WAYOFOPERATING_STARTSTOP;
		} else {
			throw new IllegalStateException("Unknown way of operating: " + cncMachine.getWayOfOperating());
		}
		stmt3.setInt(3, wayOfOperatingInt);
		stmt3.setInt(4, cncMachine.getId());
		stmt3.execute();
		if (cncMachine.getMCodeAdapter() != null) {
			updateMCodeAdapter(cncMachine.getId(), cncMachine.getMCodeAdapter(), robotServiceInputNames, 
					robotServiceOutputNames, mCodeNames, mCodeRobotServiceInputs, mCodeRobotServiceOutputs);
		} else {
			cncMachine.setMCodeAdapter(saveMCodeAdapter(cncMachine.getId(), robotServiceInputNames, 
					robotServiceOutputNames, mCodeNames, mCodeRobotServiceInputs, mCodeRobotServiceOutputs));
		}
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
		cncMachine.setName(name);
		cncMachine.setWayOfOperating(wayOfOperating);
		cncMachine.setClampingLengthR(clampingLengthR);
		cncMachine.setClampingWidthR(clampingWidthR);
		cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().setIpAddress(ipAddress);
		cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().setPortNumber(port);
		cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().setName(name);
		cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().disconnect();
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void updateMCodeAdapter(final int id, final MCodeAdapter mCodeAdapter, final List<String> robotServiceInputNames, 
					final List<String> robotServiceOutputNames, final List<String> mCodeNames, 
						final List<Set<Integer>> mCodeRobotServiceInputs, final List<Set<Integer>> mCodeRobotServiceOutputs) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE MCODEADAPTER " +
				"SET ROBOTSERVICEINPUT1 = ?, ROBOTSERVICEINPUT2 = ?, ROBOTSERVICEINPUT3 = ?, " +
				"ROBOTSERVICEINPUT4 = ?, ROBOTSERVICEINPUT5 = ?, ROBOTSERVICEOUTPUT1 = ? WHERE ID = ?");
		stmt.setString(1, robotServiceInputNames.get(0));
		stmt.setString(2, robotServiceInputNames.get(1));
		stmt.setString(3, robotServiceInputNames.get(2));
		stmt.setString(4, robotServiceInputNames.get(3));
		stmt.setString(5, robotServiceInputNames.get(4));
		stmt.setString(6, robotServiceOutputNames.get(0));
		stmt.setInt(7, id);
		stmt.executeUpdate();
		for (GenericMCode mCode : mCodeAdapter.getGenericMCodes()) {
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("UPDATE MCODE " +
					"SET NAME = ?, ROBOTSERVICEINPUT1 = ?, ROBOTSERVICEINPUT2 = ?, ROBOTSERVICEINPUT3 = ?, " +
					"ROBOTSERVICEINPUT4 = ?, ROBOTSERVICEINPUT5 = ?, ROBOTSERVICEOUTPUT1 = ? WHERE ID = ?");
			stmt2.setString(1, mCodeNames.get(mCode.getIndex()));
			stmt2.setBoolean(2, mCodeRobotServiceInputs.get(mCode.getIndex()).contains(0));
			stmt2.setBoolean(3, mCodeRobotServiceInputs.get(mCode.getIndex()).contains(1));
			stmt2.setBoolean(4, mCodeRobotServiceInputs.get(mCode.getIndex()).contains(2));
			stmt2.setBoolean(5, mCodeRobotServiceInputs.get(mCode.getIndex()).contains(3));
			stmt2.setBoolean(6, mCodeRobotServiceInputs.get(mCode.getIndex()).contains(4));
			stmt2.setBoolean(7, mCodeRobotServiceOutputs.get(mCode.getIndex()).contains(0));
			stmt2.setInt(8, mCode.getId());
			stmt2.executeUpdate();
			mCode.setName(mCodeNames.get(mCode.getIndex()));
			mCode.setRobotServiceInputsRequired(mCodeRobotServiceInputs.get(mCode.getIndex()));
			mCode.setRobotServiceOutputsUsed(mCodeRobotServiceOutputs.get(mCode.getIndex()));
		}
		mCodeAdapter.setRobotServiceInputNames(robotServiceInputNames);
		mCodeAdapter.setRobotServiceOutputNames(robotServiceOutputNames);
	}
	
	public MCodeAdapter saveMCodeAdapter(final int id, final List<String> robotServiceInputNames, 
			final List<String> robotServiceOutputNames, final List<String> mCodeNames, 
			final List<Set<Integer>> mCodeRobotServiceInputs, final List<Set<Integer>> mCodeRobotServiceOutputs) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO MCODEADAPTER " +
				"(ID, ROBOTSERVICEINPUT1, ROBOTSERVICEINPUT2, ROBOTSERVICEINPUT3, " +
				"ROBOTSERVICEINPUT4, ROBOTSERVICEINPUT5, ROBOTSERVICEOUTPUT1) VALUS (?, ?, ?, ?, ?, ?, ?)");
		stmt.setInt(1, id);
		stmt.setString(2, robotServiceInputNames.get(0));
		stmt.setString(3, robotServiceInputNames.get(1));
		stmt.setString(4, robotServiceInputNames.get(2));
		stmt.setString(5, robotServiceInputNames.get(3));
		stmt.setString(6, robotServiceInputNames.get(4));
		stmt.setString(7, robotServiceOutputNames.get(0));
		stmt.executeUpdate();
		MCodeAdapter mCodeAdapter = new MCodeAdapter(null, robotServiceInputNames, robotServiceOutputNames);
		List<GenericMCode> mCodes = new ArrayList<GenericMCode>();
		for (int i = 0; i < mCodeNames.size(); i++) {
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("INSERT INTO MCODE " +
					"(INDEX, NAME, MCODEADAPTER, ROBOTSERVICEINPUT1, ROBOTSERVICEINPUT2, ROBOTSERVICEINPUT3, " +
					"ROBOTSERVICEINPUT4, ROBOTSERVICEINPUT5, ROBOTSERVICEOUTPUT1) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			stmt2.setInt(1, i);
			stmt2.setString(2, mCodeNames.get(i));
			stmt2.setInt(3, id);
			stmt2.setBoolean(4, mCodeRobotServiceInputs.get(i).contains(0));
			stmt2.setBoolean(5, mCodeRobotServiceInputs.get(i).contains(1));
			stmt2.setBoolean(6, mCodeRobotServiceInputs.get(i).contains(2));
			stmt2.setBoolean(7, mCodeRobotServiceInputs.get(i).contains(3));
			stmt2.setBoolean(8, mCodeRobotServiceInputs.get(i).contains(4));
			stmt2.setBoolean(9, mCodeRobotServiceOutputs.get(i).contains(0));
			stmt2.executeUpdate();
			ResultSet resultSet = stmt2.getGeneratedKeys();
			if (resultSet.next()) {
				GenericMCode mcode = new GenericMCode(resultSet.getInt(1), i, mCodeNames.get(i), mCodeRobotServiceInputs.get(i), mCodeRobotServiceOutputs.get(i));
				mCodes.add(mcode);
			}
		}
		mCodeAdapter.setGenericMCodes(mCodes);
		return mCodeAdapter;
	}
}
