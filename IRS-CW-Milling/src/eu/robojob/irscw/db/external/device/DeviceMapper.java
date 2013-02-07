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
			BasicStackPlateLayout layout = new BasicStackPlateLayout(horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPaddingTop, 
					verticalPaddingBottom, horizontalHoleDistance, interferenceDistance, overflowPercentage);
			stackPlate = new BasicStackPlate(name, zones, layout);
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
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICEINTERFACE WHERE ID = ?");
			stmt2.setInt(1, deviceInterfaceId);
			ResultSet results2 = stmt2.executeQuery();
			if (results2.next()) {
				int socketConnectionId = results2.getInt("SOCKETCONNECTION");
				SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
				cncMillingMachine = new CNCMillingMachine(name, zones, socketConnection);
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
			Coordinates relativePosition = generalMapper.getCoordinatesById(relativePositionId);
			int smoothToId = results.getInt("SMOOTH_TO");
			Coordinates smoothTo = generalMapper.getCoordinatesById(smoothToId);
			int smoothFromId = results.getInt("SMOOTH_FROM");
			Coordinates smoothFrom = generalMapper.getCoordinatesById(smoothFromId);
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
}
