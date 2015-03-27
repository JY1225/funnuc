package eu.robojob.millassist.db.external.device;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import eu.robojob.millassist.db.ConnectionManager;
import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.db.external.util.ConnectionMapper;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.EFixtureType;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.AbstractDevice.DeviceType;
import eu.robojob.millassist.external.device.Clamping.Type;
import eu.robojob.millassist.external.device.WorkAreaManager;
import eu.robojob.millassist.external.device.WorkAreaBoundary;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineSocketCommunication;
import eu.robojob.millassist.external.device.processing.cnc.ECNCOption;
import eu.robojob.millassist.external.device.processing.cnc.EWayOfOperating;
import eu.robojob.millassist.external.device.processing.cnc.mcode.GenericMCode;
import eu.robojob.millassist.external.device.processing.cnc.mcode.MCodeAdapter;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachineDevIntv2;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.stacking.bin.OutputBin;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorLayout;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridHole;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.UserFrame;

public class DeviceMapper {
	
	private static final int CLAMPING_TYPE_CENTRUM = 1;
	private static final int CLAMPING_TYPE_FIXED_XP = 2;
	private static final int CLAMPING_TYPE_NONE = 3;
	private static final int CLAMPING_TYPE_FIXED_XM = 4;
	private static final int CLAMPING_TYPE_FIXED_YP = 5;
	private static final int CLAMPING_TYPE_FIXED_YM = 6;
	
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
			DeviceType type = DeviceType.getTypeById(results.getInt("TYPE"));
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
					PrageDevice prageDevice = getPrageDevice(id, name, zones);
					devices.add(prageDevice);
					break;
				case DEVICE_TYPE_CONVEYOR:
					Conveyor conveyor = getConveyor(id, name, zones);
					devices.add(conveyor);
					break;
				case DEVICE_TYPE_CONVEYOR_EATON:
					eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton conveyorEaton = getConveyorEaton(id, name, zones);
					devices.add(conveyorEaton);
					break;
				case DEVICE_TYPE_BIN:
					OutputBin bin = getOutputBin(id, name, zones);
					devices.add(bin);
					break;
				case DEVICE_TYPE_REVERSAL_UNIT:
					ReversalUnit reversalUnit = getReversalUnit(id, name, zones);
					devices.add(reversalUnit);
					break;
				case DEVICE_TYPE_PALLET:
				    UnloadPallet pallet = getPallet(id, name, zones);
				    devices.add(pallet);
				    break;
				default:
					throw new IllegalStateException("Unknown device type: [" + type + "].");
			}
		}
		return devices;
	}
	
	private UnloadPallet getPallet(final int id, final String name, final Set<Zone> zones) throws SQLException {
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM PALLET WHERE ID = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        UnloadPallet pallet = null;
        if(results.next()) {
            float palletWidth=results.getFloat("WIDTH");
            float palletLength=results.getFloat("LENGTH");
            float palletFreeBorder=results.getFloat("BORDER");
            float minXGap=results.getFloat("OFFSET_X");
            float minYGap=results.getFloat("OFFSET_Y");
            float minInterferenceDistance = results.getFloat("MIN_INTERFERENCE");
            PalletLayout layout = new PalletLayout(palletWidth, palletLength, palletFreeBorder, minXGap, minYGap, minInterferenceDistance);
            pallet = new UnloadPallet(name, zones, layout);
            pallet.setId(id);
        }
        return pallet;
	}
	
	public void updateUnloadPallet(final UnloadPallet unloadPallet, final String name, final String userFrameName, final float width, final float length, final float border, final float xOffset, final float yOffset, final float minInterferenceDistance) throws SQLException {
	    
	    ConnectionManager.getConnection().setAutoCommit(false);
	    if ((!unloadPallet.getWorkAreaManagers().get(0).getUserFrame().getName().equals(userFrameName))) {
            UserFrame newUserFrame = getUserFrameByName(userFrameName);
            unloadPallet.getWorkAreaManagers().get(0).setUserFrame(newUserFrame);
            updateWorkArea(unloadPallet.getWorkAreaManagers().get(0));
        }
	    
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE DEVICE SET NAME = ? WHERE ID = ?");
        stmt.setString(1, name);
        stmt.setInt(2, unloadPallet.getId());
        stmt.execute();
        
        PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("UPDATE PALLET "+
                "SET WIDTH = ?, LENGTH = ?, BORDER = ?, OFFSET_X = ?, OFFSET_Y = ?, MIN_INTERFERENCE = ? WHERE ID = ?");
        stmt2.setFloat(1, width);
        stmt2.setFloat(2, length);
        stmt2.setFloat(3, border);
        stmt2.setFloat(4, xOffset);
        stmt2.setFloat(5, yOffset);
        stmt2.setFloat(6, minInterferenceDistance);
        stmt2.setInt(7, unloadPallet.getId());
        stmt2.execute();
        unloadPallet.getLayout().setPalletWidth(width);
        unloadPallet.getLayout().setPalletLength(length);
        unloadPallet.getLayout().setPalletFreeBorder(border);
        unloadPallet.getLayout().setMinXGap(xOffset);
        unloadPallet.getLayout().setMinYGap(yOffset);
        unloadPallet.getLayout().setMinInterferenceDistance(minInterferenceDistance);
        unloadPallet.setName(name);
        ConnectionManager.getConnection().commit();
        ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	private OutputBin getOutputBin(final int id, final String name, final Set<Zone> zones) throws SQLException {
		OutputBin bin = new OutputBin(name, zones);
		bin.setId(id);
		return bin;
	}
	
	private eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton getConveyorEaton(final int id, final String name, final Set<Zone> zones) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM CONVEYOREATON WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton conveyor = null;
		if (results.next()) {
			int workAreaAID = results.getInt("WORK_AREA_A");
			int workAreaBID = results.getInt("WORK_AREA_B");
			float nomSpeedA = results.getFloat("NOMSPEED_A");
			float nomSpeedB = results.getFloat("NOMSPEED_B");
			float nomSpeedASlow = results.getFloat("NOMSPEED_A_SLOW");
			float nomSpeedBSlow = results.getFloat("NOMSPEED_B_SLOW");
			float minWorkPieceWidth = results.getFloat("MIN_WP_WIDTH");
			float maxWorkPieceWidth = results.getFloat("MAX_WP_WIDTH");
			float trackWidth = results.getFloat("TRACK_WIDTH");
			float supportWidth = results.getFloat("SUPPORT_WIDTH");
			float xPosSensor1 = results.getFloat("X_POS_SENSOR_1");
			float xPosSensor2 = results.getFloat("X_POS_SENSOR_2");
			float sideWidth = results.getFloat("SIDE_WIDTH");
			eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorLayout layout = new eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorLayout(minWorkPieceWidth, maxWorkPieceWidth, trackWidth, supportWidth, xPosSensor1, xPosSensor2, sideWidth);
			int socketConnectionId = results.getInt("SOCKETCONNECTION");
			SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
			WorkAreaManager workAreaA = null;
			WorkAreaManager workAreaB = null;
			for (Zone zone : zones) {
				for (WorkAreaManager workArea : zone.getWorkAreaManagers()) {
					if (workArea.getId() == workAreaAID) {
						workAreaA = workArea;
					}
					if (workArea.getId() == workAreaBID) {
						workAreaB = workArea;
					}
				}
			}
			conveyor = new eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton(name, zones, workAreaA, workAreaB, layout, socketConnection, nomSpeedA, nomSpeedASlow, nomSpeedB, nomSpeedBSlow);
			conveyor.setId(id);
		}
		return conveyor;
	}
	
	private PrageDevice getPrageDevice(final int id, final String name, final Set<Zone> zones) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM PRAGEFIX WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		PrageDevice prageDevice = null;
		if (results.next()) {
			int clampingWidthR = results.getInt("CLAMPING_WIDTH_R");
			prageDevice = new PrageDevice(name, zones, clampingWidthR);
			prageDevice.setId(id);
		}
		return prageDevice;
	}
	
	private ReversalUnit getReversalUnit(final int id, final String name, final Set<Zone> zones) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM REVERSALUNIT WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		ReversalUnit reversalUnit = null;
		if (results.next()) {
			float stationHeight = results.getFloat("STATION_HEIGHT");
			float stationLength = results.getFloat("STATION_LENGTH");
			float stationFixtureWidth = results.getFloat("STATION_FIXTURE_WIDTH");
			float addedX = results.getFloat("EXTRA_X");
			reversalUnit = new ReversalUnit(name, zones, stationLength, stationFixtureWidth, stationHeight, addedX);
			reversalUnit.setId(id);
			getAllowedApproachTypes(id, reversalUnit);
		}
		return reversalUnit;
	}
	
	private void getAllowedApproachTypes(final int id, final ReversalUnit reversalUnit) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ALLOWED_APPROACHTYPE_DEVICE WHERE DEVICE_ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			ApproachType approachType = ApproachType.getById(results.getInt("APPROACHTYPE"));
			boolean isAllowed = results.getBoolean("IS_ALLOWED");
			reversalUnit.addApproachType(approachType, isAllowed);
		}
	}
	
	private Conveyor getConveyor(final int id, final String name, final Set<Zone> zones) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM CONVEYOR WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		Conveyor conveyor = null;
		if (results.next()) {
			int rawWorkAreaId = results.getInt("RAWWORKAREA");
			int finishedWorkAreaId = results.getInt("FINISHEDWORKAREA");
			float nomSpeed1 = results.getFloat("NOMSPEED1");
			float nomSpeed2 = results.getFloat("NOMSPEED2");
			int rawTrackAmount = results.getInt("RAWTRACKAMOUNT");
			float rawTrackWidth = results.getFloat("RAWTRACKWIDTH");
			float spaceBetweenTracks = results.getFloat("SPACEBETWEENTRACKS");
			float supportWidth = results.getFloat("SUPPORTWIDTH");
			float finishedConveyorWidth = results.getFloat("FINISHEDCONVEYORWIDTH");
			float interferenceDistance = results.getFloat("INTERFERENCEDISTANCE");
			float maxWorkPieceLength = results.getFloat("MAXWORKPIECELENGTH");
			float rawConveyorLength = results.getFloat("RAWCONVEYORLENGTH");
			float finishedConveyorLength = results.getFloat("FINISHEDCONVEYORLENGTH");
			float maxOverlap = results.getFloat("MAXOVERLAP");
			float minDistRaw = results.getFloat("MIN_DIST_RAW");
			float minDistFinished = results.getFloat("MIN_DIST_FINISHED");
			int socketConnectionId = results.getInt("SOCKETCONNECTION");
			ConveyorLayout layout = new ConveyorLayout(rawTrackAmount, rawTrackWidth, spaceBetweenTracks, supportWidth, 
					finishedConveyorWidth, interferenceDistance, maxWorkPieceLength, rawConveyorLength, 
					finishedConveyorLength, maxOverlap, minDistRaw, minDistFinished);
			WorkAreaManager rawWorkArea = null;
			WorkAreaManager finishedWorkArea = null;
			for (Zone zone : zones) {
				for (WorkAreaManager workArea : zone.getWorkAreaManagers()) {
					if (workArea.getId() == rawWorkAreaId) {
						rawWorkArea = workArea;
					}
					if (workArea.getId() == finishedWorkAreaId) {
						finishedWorkArea = workArea;
					}
				}
			}
			SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
			conveyor = new Conveyor(name, zones, rawWorkArea, finishedWorkArea, layout, socketConnection, nomSpeed1, nomSpeed2);
			conveyor.setId(id);
		}
		return conveyor;
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
			double maxOverflow = results.getDouble("MAX_OVERFLOW");
			double maxUnderflow = results.getDouble("MAX_UNDERFLOW");
			double minOverlap = results.getDouble("MIN_OVERLAP");
			BasicStackPlateLayout layout = new BasicStackPlateLayout(horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPaddingTop, 
					verticalPaddingBottom, horizontalHoleDistance, interferenceDistance, overflowPercentage, horizontalR, tiltedR, maxOverflow, maxUnderflow, minOverlap);
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
			int clampingWidthR = results.getInt("CLAMPING_WIDTH_R");
			boolean usesNewDevInt = results.getBoolean("NEW_DEV_INT");
			int nbFixtures = results.getInt("NB_FIXTURES");
			float rRoundPieces = results.getFloat("R_ROUND_PIECES");
			EWayOfOperating wayOfOperating = EWayOfOperating.getWayOfOperatingById(results.getInt("WAYOFOPERATING"));
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICEINTERFACE WHERE ID = ?");
			stmt2.setInt(1, deviceInterfaceId);
			ResultSet results2 = stmt2.executeQuery();
			if (results2.next()) {
				int socketConnectionId = results2.getInt("SOCKETCONNECTION");
				SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
				if(usesNewDevInt) {
					cncMillingMachine = new CNCMillingMachineDevIntv2(name, wayOfOperating, getMCodeAdapter(id), zones, socketConnection, clampingWidthR, nbFixtures, rRoundPieces);
				} else {
					cncMillingMachine = new CNCMillingMachine(name, wayOfOperating, getMCodeAdapter(id), zones, socketConnection, clampingWidthR, nbFixtures, rRoundPieces);
				}
				Map<ECNCOption, Boolean> cncOptions = getCNCOptions(id);
				if (cncOptions.get(ECNCOption.TIM_ALLOWED) != null) {
					cncMillingMachine.setTIMAllowed(cncOptions.get(ECNCOption.TIM_ALLOWED));
				}
				if (cncOptions.get(ECNCOption.MACHINE_AIRBLOW) != null) {
					cncMillingMachine.setMachineAirblow(cncOptions.get(ECNCOption.MACHINE_AIRBLOW));
				}
				if (cncOptions.get(ECNCOption.WORKNUMBER_SEARCH) != null) {
				    cncMillingMachine.setWorkNumberSearch(cncOptions.get(ECNCOption.WORKNUMBER_SEARCH));
				}
				cncMillingMachine.setId(id);
			}
		}
		return cncMillingMachine;
	}
	
	private Map<ECNCOption, Boolean> getCNCOptions(final int id) throws SQLException {
		Map<ECNCOption, Boolean> resultMap = new HashMap<ECNCOption, Boolean>();
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM CNC_OPTION WHERE CNC_ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			ECNCOption option = ECNCOption.getCNCOptionById(results.getInt("OPTION_ID"));
			boolean value = results.getBoolean("OPTION_VALUE");
			resultMap.put(option, value);
		}
		return resultMap;
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
			int zoneNr = results.getInt("ZONE_NR");
			Set<WorkAreaManager> workAreas = getAllWorkAreasByZoneId(id);
			Zone zone;
			zone = new Zone(name, workAreas, zoneNr);
			zone.setId(id);
			zones.add(zone);
		}
		return zones;
	}
	
	private Set<WorkAreaManager> getAllWorkAreasByZoneId(final int zoneId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(""
				+ "SELECT * FROM WORKAREA "
				+ "WHERE ZONE = ?"
				+ "ORDER BY USERFRAME");
		stmt.setInt(1, zoneId);
		ResultSet results = stmt.executeQuery();
		Set<WorkAreaManager> workAreas = new HashSet<WorkAreaManager>();
		while (results.next()) {
			int id = results.getInt("ID");
			int userFrameId = results.getInt("USERFRAME");
			UserFrame userFrame = generalMapper.getUserFrameById(userFrameId);
			Set<Clamping> possibleClampings = getClampingsByWorkAreaId(id);
			WorkAreaManager workArea = new WorkAreaManager(userFrame, possibleClampings);
			workArea.setId(id);
			AirblowSquare boundaries = getWorkAreaBoundaries(id);
			if (boundaries != null) {
				workArea.setWorkAreaBoundary(new WorkAreaBoundary(workArea, boundaries));
			}
			getAllSimpleWorkAreasByManagerId(workArea);
			workAreas.add(workArea);
		}
		return workAreas;
	}
	
	private void getAllSimpleWorkAreasByManagerId(final WorkAreaManager waManager) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(""
				+ "SELECT * FROM SIMPLE_WORKAREA "
				+ "WHERE WORKAREA = ?"
				+ "ORDER BY SEQ_NB");
		stmt.setInt(1, waManager.getId());
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			int sequenceNb = results.getInt("SEQ_NB");
			SimpleWorkArea workArea = new SimpleWorkArea(waManager, name, sequenceNb);
			workArea.setId(id);
		}
	}
	
	private AirblowSquare getWorkAreaBoundaries(final int workAreaId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(""
				+ "SELECT * FROM WORKAREA_BOUNDARIES "
				+ "WHERE WORKAREA_ID = ?");
		stmt.setInt(1, workAreaId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			Coordinates bottomCoord = generalMapper.getCoordinatesById(0, results.getInt("BOTTOMCOORD"));
			Coordinates topCoord = generalMapper.getCoordinatesById(0, results.getInt("TOPCOORD"));
			return new AirblowSquare(bottomCoord, topCoord);
		}
		return null;
	}
	
	private Set<Clamping> getClampingsByWorkAreaId(final int workAreaId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM WORKAREA_CLAMPING WHERE WORKAREA = ? ORDER BY ID");
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
			int fixtureTypeInt = results.getInt("FIXTURE_TYPE");
			EFixtureType fixtureType = EFixtureType.getFixtureTypeFromCodeValue(fixtureTypeInt);
			switch(type) {
				case CLAMPING_TYPE_CENTRUM:
					clamping = new Clamping(Clamping.Type.CENTRUM, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
					break;
				case CLAMPING_TYPE_FIXED_XP:
					clamping = new Clamping(Clamping.Type.FIXED_XP, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
					break;
				case CLAMPING_TYPE_NONE:
					clamping = new Clamping(Clamping.Type.NONE, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
					break;
				case CLAMPING_TYPE_FIXED_XM:
					clamping = new Clamping(Clamping.Type.FIXED_XM, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
					break;
				case CLAMPING_TYPE_FIXED_YP:
					clamping = new Clamping(Clamping.Type.FIXED_YP, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
					break;
				case CLAMPING_TYPE_FIXED_YM:
					clamping = new Clamping(Clamping.Type.FIXED_YM, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
					break;
				default:
					throw new IllegalStateException("Unknown clamping type: [" + type + "].");
			}
			Integer bottomCoordAirblow = new Integer(results.getInt("AIRBLOW_BOTTOM"));
			Integer topCoordAirblow = new Integer(results.getInt("AIRBLOW_TOP"));
			if (bottomCoordAirblow != null && topCoordAirblow != null) {
				clamping.setDefaultAirblowPoints(new AirblowSquare(generalMapper.getCoordinatesById(0, bottomCoordAirblow), generalMapper.getCoordinatesById(0, topCoordAirblow)));
			}
			clamping.setId(clampingId);
		}
		return clamping;
	}
	
	public Set<UserFrame> getAllUserFrames() throws SQLException {
		return generalMapper.getAllUserFrames();
	}
	
	public Set<GridPlate> getAllGridPlates() throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM GRIDPLATE");
		ResultSet results = stmt.executeQuery();
		Set<GridPlate> gridPlates = new HashSet<GridPlate>();
		while (results.next()) {
			int id = results.getInt("ID");
			gridPlates.add(getGridPlateByID(id));
		}
		return gridPlates;
	}
	

	private GridPlate getGridPlateByID(int gridPlateId) throws SQLException {
		GridPlate gridplate = null;
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM GRIDPLATE WHERE ID = ?");
		stmt.setInt(1, gridPlateId);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			String name = results.getString("NAME");
			float length = results.getFloat("LENGTH");
			float width = results.getFloat("WIDTH");
			float height = results.getFloat("HEIGHT");
			float holeLength = results.getFloat("HOLE_LENGTH");
			float holeWidth = results.getFloat("HOLE_WIDTH");
			float posX = results.getFloat("OFFSET_X");
			float posY = results.getFloat("OFFSET_Y");
			gridplate = new GridPlate(name, length, width);
			gridplate.setId(gridPlateId);
			gridplate.setDepth(height);
			gridplate.setOffsetX(posX);
			gridplate.setOffsetY(posY);
			gridplate.setHoleLength(holeLength);
			gridplate.setHoleWidth(holeWidth);
			getGridHoles(gridplate);
		}
		stmt.close();
		return gridplate;
	}
	
	public GridPlate getGridPlateByName(final String gridPlateName) throws SQLException {
		GridPlate gridplate = null;
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM GRIDPLATE WHERE NAME = ?");
		stmt.setString(1, gridPlateName);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			int id = results.getInt("ID");
			float length = results.getFloat("LENGTH");
			float width = results.getFloat("WIDTH");
			float height = results.getFloat("HEIGHT");
			float holeLength = results.getFloat("HOLE_LENGTH");
			float holeWidth = results.getFloat("HOLE_WIDTH");
			float posX = results.getFloat("OFFSET_X");
			float posY = results.getFloat("OFFSET_Y");
			gridplate = new GridPlate(gridPlateName, length, width);
			gridplate.setId(id);
			gridplate.setDepth(height);
			gridplate.setOffsetX(posX);
			gridplate.setOffsetY(posY);
			gridplate.setHoleLength(holeLength);
			gridplate.setHoleWidth(holeWidth);
			getGridHoles(gridplate);
		}
		stmt.close();
		return gridplate;
	}
	
	private void getGridHoles(GridPlate gridPlate) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM GRIDHOLE WHERE PLATE_ID = ?");
		stmt.setInt(1, gridPlate.getId());
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			float angle = results.getFloat("ORIENTATION");
			float xBottom = results.getFloat("X_BOTTOM");
			float yBottom = results.getFloat("Y_BOTTOM");
			GridHole hole = new GridHole(xBottom, yBottom, angle);
			gridPlate.addHole(hole);
		}
		stmt.close();
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
	
	public void updateWorkArea(final WorkAreaManager workArea) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE WORKAREA SET ZONE = ?, USERFRAME = ? WHERE ID = ?");
		stmt.setInt(1, workArea.getZone().getId());
		stmt.setInt(2, workArea.getUserFrame().getId());
		stmt.setInt(3, workArea.getId());
		stmt.executeUpdate();
	}
	
	public void updateBasicStackPlate(final BasicStackPlate basicStackPlate, final String name, final String userFrameName, final int horizontalHoleAmount, final int verticalHoleAmount, 
			final float holeDiameter, final float studDiameter, final float horizontalHoleDistance, final float horizontalPadding, 
			final float verticalPaddingTop, final float verticalPaddingBottom, final float interferenceDistance, final float overflowPercentage,
			final float horizontalR, final float tiltedR, final float maxOverlow, final float maxUnderflow, final float minOverlap, final float studHeight,
			final float smoothToX, final float smoothToY, final float smoothToZ,
			final float smoothFromX, final float smoothFromY, final float smoothFromZ) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		if ((!basicStackPlate.getWorkAreaManagers().get(0).getUserFrame().getName().equals(userFrameName))) {
			UserFrame newUserFrame = getUserFrameByName(userFrameName);
			basicStackPlate.getWorkAreaManagers().get(0).setUserFrame(newUserFrame);
			updateWorkArea(basicStackPlate.getWorkAreaManagers().get(0));
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE STACKPLATE SET HORIZONTALHOLEAMOUNT = ?, VERTICALHOLEAMOUNT = ?, HOLEDIAMETER = ?, " +
				"STUDDIAMETER = ?, HORIZONTALPADDING = ?, VERTICALPADDINGTOP = ?, VERTICALPADDINGBOTTOM = ?, HORIZONTALHOLEDISTANCE = ?, INTERFERENCEDISTANCE = ?, " +
				" OVERFLOWPERCENTAGE = ?, HORIZONTAL_R = ?, TILTED_R = ?, MAX_OVERFLOW = ?, MIN_OVERLAP = ?, MAX_UNDERFLOW = ? "
				+ "WHERE ID = ?");
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
		stmt.setFloat(13, maxOverlow);
		stmt.setFloat(14, minOverlap);
		stmt.setFloat(15, maxUnderflow);
		stmt.setInt(16, basicStackPlate.getId());
		stmt.execute();
		PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("UPDATE DEVICE SET NAME = ? WHERE ID = ?");
		stmt2.setString(1, name);
		stmt2.setInt(2, basicStackPlate.getId());
		stmt2.execute();
		Coordinates smoothTo = basicStackPlate.getWorkAreas().get(0).getDefaultClamping().getSmoothToPoint();
		Coordinates smoothFrom = basicStackPlate.getWorkAreas().get(0).getDefaultClamping().getSmoothFromPoint();
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
		basicStackPlate.getBasicLayout().setHorizontalHoleAmount(horizontalHoleAmount);
		basicStackPlate.getBasicLayout().setVerticalHoleAmount(verticalHoleAmount);
		basicStackPlate.getBasicLayout().setHoleDiameter(holeDiameter);
		basicStackPlate.getBasicLayout().setStudDiameter(studDiameter);
		basicStackPlate.getBasicLayout().setHorizontalPadding(horizontalPadding);
		basicStackPlate.getBasicLayout().setVerticalPaddingTop(verticalPaddingTop);
		basicStackPlate.getBasicLayout().setVerticalPaddingBottom(verticalPaddingBottom);
		basicStackPlate.getBasicLayout().setHorizontalHoleDistance(horizontalHoleDistance);
		basicStackPlate.getBasicLayout().setInterferenceDistance(interferenceDistance);
		basicStackPlate.getBasicLayout().setOverflowPercentage(overflowPercentage);
		basicStackPlate.getBasicLayout().setMaxOverflow(maxOverlow);
		basicStackPlate.getBasicLayout().setMaxUnderflow(maxUnderflow);
		basicStackPlate.getBasicLayout().setMinOverlap(minOverlap);
		basicStackPlate.getBasicLayout().setHorizontalR(horizontalR);
		basicStackPlate.getBasicLayout().setTiltedR(tiltedR);
		PreparedStatement stmt3 = ConnectionManager.getConnection().prepareStatement("UPDATE CLAMPING SET HEIGHT = ? WHERE ID = ?");
		stmt3.setFloat(1, studHeight);
		stmt3.setInt(2, basicStackPlate.getWorkAreas().get(0).getDefaultClamping().getId());
		stmt3.execute();
		basicStackPlate.getWorkAreas().get(0).getDefaultClamping().setDefaultHeight(studHeight);
	}
	
	public void updateCNCMachine(final AbstractCNCMachine cncMachine, final String name, final EWayOfOperating wayOfOperating,
			final String ipAddress, final int port, final int clampingWidthR, final boolean newDevInt, final int nbFixtures, final float rRoundPieces, final boolean timAllowed,
			final boolean machineAirblow, final List<WorkAreaBoundary> airblowBounds, final List<String> robotServiceInputNames, 
			final List<String> robotServiceOutputNames, final List<String> mCodeNames, 
			final List<Set<Integer>> mCodeRobotServiceInputs, final List<Set<Integer>> mCodeRobotServiceOutputs, final boolean workNumberSearch) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE SOCKETCONNECTION " +
				"SET IPADDRESS = ?, PORTNR = ?, NAME = ? WHERE ID = ?");
		stmt.setString(1, ipAddress);
		stmt.setInt(2, port);
		stmt.setString(3, name);
		CNCMachineSocketCommunication cncSocketComm = cncMachine.getCNCMachineSocketCommunication();
		stmt.setInt(4, cncSocketComm.getExternalCommunicationThread().getSocketConnection().getId());
		stmt.execute();
		PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("UPDATE DEVICE SET NAME = ? WHERE ID = ?");
		stmt2.setString(1, name);
		stmt2.setInt(2, cncMachine.getId());
		stmt2.execute();
		PreparedStatement stmt3 = ConnectionManager.getConnection().
				prepareStatement("UPDATE CNCMILLINGMACHINE SET CLAMPING_WIDTH_R = ? , WAYOFOPERATING = ?, NEW_DEV_INT = ?, NB_FIXTURES = ?, R_ROUND_PIECES = ? WHERE ID = ?");
		stmt3.setInt(1, clampingWidthR);
		stmt3.setInt(2, wayOfOperating.getId());
		stmt3.setBoolean(3, newDevInt);
		stmt3.setInt(4, nbFixtures);
		stmt3.setFloat(5, rRoundPieces);
		stmt3.setInt(6, cncMachine.getId());
		stmt3.execute();
		updateCNCOption(ECNCOption.TIM_ALLOWED, timAllowed, cncMachine.getId());
		updateCNCOption(ECNCOption.MACHINE_AIRBLOW, machineAirblow, cncMachine.getId());
		updateCNCOption(ECNCOption.WORKNUMBER_SEARCH, workNumberSearch, cncMachine.getId());
//		if (cncMachine.getMCodeAdapter() != null) {
//			deleteMCodeAdapter(cncMachine.getMCodeAdapter().getI);
//			updateMCodeAdapter(cncMachine.getId(), cncMachine.getMCodeAdapter(), robotServiceInputNames, 
//					robotServiceOutputNames, mCodeNames, mCodeRobotServiceInputs, mCodeRobotServiceOutputs);
//		} else {
		deleteMCodeAdapter(cncMachine.getId());
			cncMachine.setMCodeAdapter(saveMCodeAdapter(cncMachine.getId(), robotServiceInputNames, 
					robotServiceOutputNames, mCodeNames, mCodeRobotServiceInputs, mCodeRobotServiceOutputs));
//		}
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
		cncMachine.setName(name);
		cncMachine.setWayOfOperating(wayOfOperating);
		cncMachine.setClampingWidthR(clampingWidthR);
		cncMachine.setNbFixtures(nbFixtures);
		cncMachine.setRRoundPieces(rRoundPieces);
		cncMachine.setTIMAllowed(timAllowed);
		cncMachine.setMachineAirblow(machineAirblow);
		cncMachine.setWorkNumberSearch(workNumberSearch);
		saveAirblowBound(cncMachine, airblowBounds);
		cncSocketComm.getExternalCommunicationThread().getSocketConnection().setIpAddress(ipAddress);
		cncSocketComm.getExternalCommunicationThread().getSocketConnection().setPortNumber(port);
		cncSocketComm.getExternalCommunicationThread().getSocketConnection().setName(name);
		cncSocketComm.getExternalCommunicationThread().getSocketConnection().disconnect();
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	private void updateCNCOption(ECNCOption cncOption, final boolean value, final int cnc_id) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(
				"UPDATE CNC_OPTION SET OPTION_VALUE = ? WHERE CNC_ID = ? AND OPTION_ID = ?");
		stmt.setBoolean(1, value);
		stmt.setInt(2, cnc_id);
		stmt.setInt(3, cncOption.getId());
		stmt.execute();
	}
	
	private void saveAirblowBound(final AbstractCNCMachine cncMachine, final List<WorkAreaBoundary> airblowBounds) throws SQLException {
		for(WorkAreaBoundary workareaBound: airblowBounds) {
			if (cncMachine.getWorkAreaByName(workareaBound.getWorkArea().getName()).getBoundaries() != null) {
				generalMapper.saveCoordinates(workareaBound.getBoundary().getBottomCoord());
				generalMapper.saveCoordinates(workareaBound.getBoundary().getTopCoord());
			} else {
				PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(""
						+ "INSERT INTO WORKAREA_BOUNDARIES (WORKAREA_ID, BOTTOMCOORD, TOPCOORD) VALUES (?, ?, ?)");
				stmt.setInt(1, workareaBound.getWorkArea().getId());
				generalMapper.saveCoordinates(workareaBound.getBoundary().getBottomCoord());
				generalMapper.saveCoordinates(workareaBound.getBoundary().getTopCoord());
				stmt.setInt(2, workareaBound.getBoundary().getBottomCoord().getId());
				stmt.setInt(3, workareaBound.getBoundary().getTopCoord().getId());
				stmt.executeUpdate();
				cncMachine.getWorkAreaByName(workareaBound.getWorkArea().getName()).setWorkAreaBoundary(workareaBound);
			}
		}
	}
	
	public void updatePrageDevice(final PrageDevice prageDevice, final String name, final Clamping.Type type, final float relPosX, final float relPosY, 
			final float relPosZ, final float relPosR, final float smoothToX, final float smoothToY, final float smoothToZ,
			final float smoothFromX, final float smoothFromY, final float smoothFromZ, final int widthOffsetR) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		Clamping clamping = prageDevice.getWorkAreas().get(0).getDefaultClamping();
		updateClamping(clamping, clamping.getName(), type, clamping.getHeight(), clamping.getImageUrl(), relPosX, relPosY, relPosZ, clamping.getRelativePosition().getW(),
				clamping.getRelativePosition().getP(), relPosR, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ, clamping.getFixtureType());
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE DEVICE " +
				"SET NAME = ? WHERE ID = ?");
		stmt.setString(1, name);
		stmt.setInt(2, prageDevice.getId());
		stmt.execute();
		PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("UPDATE PRAGEFIX "+
				"SET CLAMPING_WIDTH_R = ? WHERE ID = ?");
		stmt2.setInt(1, widthOffsetR);
		stmt2.setInt(2, prageDevice.getId());
		stmt2.execute();
		prageDevice.setName(name);
		prageDevice.setClampingWidthDeltaR(widthOffsetR);
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void updateOutputBin(final OutputBin outputBin, final String name, final String userFrame, final float x, final float y,
			final float z, final float w, final float p, final float r, final float smoothToX, final float smoothToY, 
			final float smoothToZ) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		if ((!outputBin.getWorkAreaManagers().get(0).getUserFrame().getName().equals(userFrame))) {
			UserFrame newUserFrame = getUserFrameByName(userFrame);
			outputBin.getWorkAreaManagers().get(0).setUserFrame(newUserFrame);
			updateWorkArea(outputBin.getWorkAreaManagers().get(0));
		}
		Coordinates c = outputBin.getWorkAreas().get(0).getDefaultClamping().getRelativePosition();
		c.setX(x);
		c.setY(y);
		c.setZ(z);
		c.setW(w);
		c.setP(p);
		c.setR(r);
		generalMapper.saveCoordinates(c);
		Coordinates smoothTo = outputBin.getWorkAreas().get(0).getDefaultClamping().getSmoothToPoint();
		smoothTo.setX(smoothToX);
		smoothTo.setY(smoothToY);
		smoothTo.setZ(smoothToZ);
		generalMapper.saveCoordinates(smoothTo);
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void updateReversalUnit(final ReversalUnit reversalUnit, final String name, final String userFrame, final float x, final float y,
			final float z, final float w, final float p, final float r, final float smoothToX, final float smoothToY, 
			final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ, 
			final float stationLength, final float stationFixtureWidth, final float stationHeight,
			final Map<ApproachType, Boolean> allowedApproaches, final float addedX) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		if ((!reversalUnit.getWorkAreaManagers().get(0).getUserFrame().getName().equals(userFrame))) {
			UserFrame newUserFrame = getUserFrameByName(userFrame);
			reversalUnit.getWorkAreaManagers().get(0).setUserFrame(newUserFrame);
			updateWorkArea(reversalUnit.getWorkAreaManagers().get(0));
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE REVERSALUNIT " +
				"SET STATION_LENGTH = ?, STATION_FIXTURE_WIDTH = ?, STATION_HEIGHT = ?, EXTRA_X = ? WHERE ID = ?");
		stmt.setFloat(1, stationLength);
		stmt.setFloat(2, stationFixtureWidth);
		stmt.setFloat(3, stationHeight);
		stmt.setFloat(4, addedX);
		stmt.setInt(5, reversalUnit.getId());
		stmt.execute();
		reversalUnit.setAddedX(addedX);
		saveAllowedApproaches(allowedApproaches, reversalUnit.getId());
		reversalUnit.setAllowedApproachTypes(allowedApproaches);
		reversalUnit.setStationHeight(stationHeight);
		Coordinates c = reversalUnit.getWorkAreas().get(0).getDefaultClamping().getRelativePosition();
		c.setX(x);
		c.setY(y);
		c.setZ(z);
		c.setW(w);
		c.setP(p);
		c.setR(r);
		generalMapper.saveCoordinates(c);
		Coordinates smoothTo = reversalUnit.getWorkAreas().get(0).getDefaultClamping().getSmoothToPoint();
		smoothTo.setX(smoothToX);
		smoothTo.setY(smoothToY);
		smoothTo.setZ(smoothToZ);
		generalMapper.saveCoordinates(smoothTo);
		Coordinates smoothFrom = reversalUnit.getWorkAreas().get(0).getDefaultClamping().getSmoothFromPoint();
		smoothFrom.setX(smoothFromX);
		smoothFrom.setY(smoothFromY);
		smoothFrom.setZ(smoothFromZ);
		generalMapper.saveCoordinates(smoothFrom);
		PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("UPDATE DEVICE SET NAME = ? WHERE ID = ?");
		stmt2.setString(1, name);
		stmt2.setInt(2, reversalUnit.getId());
		stmt2.execute();
		reversalUnit.setName(name);
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	private void saveAllowedApproaches(final Map<ApproachType, Boolean> allowedApproaches, final int id) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE ALLOWED_APPROACHTYPE_DEVICE " +
				"SET IS_ALLOWED = ? WHERE DEVICE_ID = ? AND APPROACHTYPE = ?");
		stmt.setInt(2, id);
		for (Entry<ApproachType, Boolean> entry: allowedApproaches.entrySet()) {
			stmt.setInt(3, entry.getKey().getId());
			stmt.setBoolean(1, entry.getValue());
			stmt.execute();
		}
	}
	
	public void deleteGridPlate(GridPlate gridPlate) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM GRIDPLATE WHERE ID = ?");
		stmt.setInt(1, gridPlate.getId());
		stmt.executeUpdate();
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void updateGridPlate(final GridPlate gridPlate, final String name, final float width, final float height, final float depth, 
			final float offsetX, final float offsetY, final float holeLength, final float holeWidth, final SortedSet<GridHole> gridholes) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE GRIDPLATE SET " +
				"NAME = ?, LENGTH = ?, WIDTH = ?, HEIGHT = ?, HOLE_LENGTH = ?, HOLE_WIDTH = ?, OFFSET_X = ?, OFFSET_Y = ? WHERE ID = ?");
		stmt.setString(1, name);
		stmt.setFloat(2, width);
		stmt.setFloat(3, height);
		stmt.setFloat(4, depth);
		stmt.setFloat(5, holeLength);
		stmt.setFloat(6, holeWidth);
		stmt.setFloat(7, offsetX);
		stmt.setFloat(8, offsetY);
		stmt.setInt(9, gridPlate.getId());
		gridPlate.setName(name);
		gridPlate.setWidth(width);
		gridPlate.setHeight(height);
		gridPlate.setDepth(depth);
		gridPlate.setOffsetX(offsetX);
		gridPlate.setOffsetY(offsetY);
		gridPlate.setGridHoles(gridholes);
		gridPlate.setHoleLength(holeLength);
		gridPlate.setHoleWidth(holeWidth);
		deleteGridHoles(gridPlate.getId());
		saveGridHoles(gridholes, gridPlate.getId());
		stmt.executeUpdate();
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void saveGridPlate(final GridPlate gridPlate) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO GRIDPLATE " +
			"(NAME, LENGTH, WIDTH, HEIGHT, HOLE_LENGTH, HOLE_WIDTH, OFFSET_X, OFFSET_Y)"
			+ "VALUES (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);		
		stmt.setString(1, gridPlate.getName());
		stmt.setFloat(2, gridPlate.getWidth());
		stmt.setFloat(3, gridPlate.getHeight());
		stmt.setFloat(4, gridPlate.getDepth());
		stmt.setFloat(5, gridPlate.getHoleLength());
		stmt.setFloat(6, gridPlate.getHoleWidth());
		stmt.setFloat(7, gridPlate.getOffsetX());
		stmt.setFloat(8, gridPlate.getOffsetY());
		stmt.executeUpdate();
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			int id = resultSet.getInt(1);
			gridPlate.setId(id);
			try {
				saveGridHoles(gridPlate.getGridHoles(), id);
				ConnectionManager.getConnection().commit();
				ConnectionManager.getConnection().setAutoCommit(true);
			} catch (Exception e) {
				throw e;
			}
		}
	}
	
	private void deleteGridHoles(final int gridId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM GRIDHOLE WHERE PLATE_ID = ?");
		stmt.setInt(1, gridId);
		stmt.executeUpdate();
	}
	
	private void saveGridHoles(final SortedSet<GridHole> gridHoles, final int gridId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO GRIDHOLE " +
				"(PLATE_ID, ORIENTATION, X_BOTTOM, Y_BOTTOM)"
				+ "VALUES (?,?,?,?)");	
		for (GridHole hole: gridHoles) {
			stmt.setInt(1, gridId);
			stmt.setFloat(2, hole.getAngle());
			stmt.setFloat(3, hole.getX());
			stmt.setFloat(4, hole.getY());
			stmt.executeUpdate();
		}
		ConnectionManager.getConnection().commit();
	}
	
	private MCodeAdapter saveMCodeAdapter(final int id, final List<String> robotServiceInputNames, 
			final List<String> robotServiceOutputNames, final List<String> mCodeNames, 
			final List<Set<Integer>> mCodeRobotServiceInputs, final List<Set<Integer>> mCodeRobotServiceOutputs) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO MCODEADAPTER " +
				"(ID, ROBOTSERVICEINPUT1, ROBOTSERVICEINPUT2, ROBOTSERVICEINPUT3, " +
				"ROBOTSERVICEINPUT4, ROBOTSERVICEINPUT5, ROBOTSERVICEOUTPUT1) VALUES (?, ?, ?, ?, ?, ?, ?)");
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
	
	private void deleteMCodeAdapter(final int id) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement(""
				+ "DELETE FROM MCODEADAPTER WHERE ID = ?");
		stmt.setInt(1, id);
		stmt.executeUpdate();
	}
	
	private void updateClamping(final Clamping clamping, final String name, final Clamping.Type type, final float height, 
			final String imagePath, final float x, final float y, final float z, final float w, final float p, 
			final float r, final float smoothToX, final float smoothToY, final float smoothToZ, final float smoothFromX, 
			final float smoothFromY, final float smoothFromZ, final EFixtureType fixtureType) throws SQLException {
		updateClamping(clamping, name, type, height, imagePath, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ, fixtureType, null, null);
	}
	
	public void updateClamping(final Clamping clamping, final String name, final Clamping.Type type, final float height, 
			final String imagePath, final float x, final float y, final float z, final float w, final float p, 
			final float r, final float smoothToX, final float smoothToY, final float smoothToZ, final float smoothFromX, 
			final float smoothFromY, final float smoothFromZ, final EFixtureType fixtureType,
			final Coordinates bottomAirblowCoord, final Coordinates topAirblowCoord) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		Coordinates relPos = clamping.getRelativePosition();
		relPos.setX(x);
		relPos.setY(y);
		relPos.setZ(z);
		relPos.setW(w);
		relPos.setP(p);
		relPos.setR(r);
		generalMapper.saveCoordinates(relPos);
		Coordinates smoothTo = clamping.getSmoothToPoint();
		smoothTo.setX(smoothToX);
		smoothTo.setY(smoothToY);
		smoothTo.setZ(smoothToZ);
		generalMapper.saveCoordinates(smoothTo);
		Coordinates smoothFrom = clamping.getSmoothFromPoint();
		smoothFrom.setX(smoothFromX);
		smoothFrom.setY(smoothFromY);
		smoothFrom.setZ(smoothFromZ);
		generalMapper.saveCoordinates(smoothFrom);
		if (bottomAirblowCoord != null && topAirblowCoord != null) {
			generalMapper.saveCoordinates(bottomAirblowCoord);		
			generalMapper.saveCoordinates(topAirblowCoord);
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE CLAMPING " +
				"SET NAME = ?, TYPE = ?, HEIGHT = ?, IMAGE_URL = ?, FIXTURE_TYPE = ?, AIRBLOW_BOTTOM = ?, AIRBLOW_TOP = ? WHERE ID = ?");
		stmt.setString(1, name);
		int typeInt = 0;
		if (type == Type.CENTRUM) {
			typeInt = CLAMPING_TYPE_CENTRUM;
		} else if (type == Type.FIXED_XP) {
			typeInt = CLAMPING_TYPE_FIXED_XP;
		} else if (type == Type.NONE) {
			typeInt = CLAMPING_TYPE_NONE;
		} else if (type == Type.FIXED_XM) {
			typeInt = CLAMPING_TYPE_FIXED_XM;
		} else if (type == Type.FIXED_YP) {
			typeInt = CLAMPING_TYPE_FIXED_YP;
		} else if (type == Type.FIXED_YM) {
			typeInt = CLAMPING_TYPE_FIXED_YM;
		} else {
			throw new IllegalStateException("Unknown clamping type: " + type);
		}
		stmt.setInt(2, typeInt);
		stmt.setFloat(3, height);
		stmt.setString(4, imagePath);
		stmt.setInt(5, fixtureType.getCode());
		if (bottomAirblowCoord != null) {
			stmt.setInt(6, bottomAirblowCoord.getId());
		} else {
			stmt.setNull(6, Types.INTEGER);
		}
		if (topAirblowCoord != null) {
			stmt.setInt(7, topAirblowCoord.getId());
		} else {
			stmt.setNull(7, Types.INTEGER);
		}
		stmt.setInt(8, clamping.getId());
		stmt.executeUpdate();
		clamping.setName(name);
		clamping.setType(type);
		clamping.setHeight(height);
		clamping.setImageUrl(imagePath);
		clamping.setFixtureType(fixtureType);
		if (bottomAirblowCoord != null && topAirblowCoord != null) {
			clamping.setDefaultAirblowPoints(new AirblowSquare(bottomAirblowCoord, topAirblowCoord));
		}
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void saveClamping(final Clamping clamping, final Set<WorkAreaManager> workAreas) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		generalMapper.saveCoordinates(clamping.getRelativePosition());
		generalMapper.saveCoordinates(clamping.getSmoothToPoint());
		generalMapper.saveCoordinates(clamping.getSmoothFromPoint());
		generalMapper.saveCoordinates(clamping.getDefaultAirblowPoints().getBottomCoord());
		generalMapper.saveCoordinates(clamping.getDefaultAirblowPoints().getTopCoord());
		int typeInt = 0;
		if (clamping.getType() == Type.CENTRUM) {
			typeInt = CLAMPING_TYPE_CENTRUM;
		} else if (clamping.getType() == Type.FIXED_XP) {
			typeInt = CLAMPING_TYPE_FIXED_XP;
		} else if (clamping.getType() == Type.NONE) {
			typeInt = CLAMPING_TYPE_NONE;
		} else if (clamping.getType() == Type.FIXED_XM) {
			typeInt = CLAMPING_TYPE_FIXED_XM;
		} else if (clamping.getType() == Type.FIXED_YP) {
			typeInt = CLAMPING_TYPE_FIXED_YP;
		} else if (clamping.getType() == Type.FIXED_YM) {
			typeInt = CLAMPING_TYPE_FIXED_YM;
		} else {
			throw new IllegalStateException("Unknown clamping type: " + clamping.getType());
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO CLAMPING (NAME, TYPE, RELATIVE_POSITION, " +
				"SMOOTH_TO, SMOOTH_FROM, IMAGE_URL, HEIGHT, FIXTURE_TYPE, AIRBLOW_BOTTOM, AIRBLOW_TOP) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, clamping.getName());
		stmt.setInt(2, typeInt);
		stmt.setInt(3, clamping.getRelativePosition().getId());
		stmt.setInt(4, clamping.getSmoothToPoint().getId());
		stmt.setInt(5, clamping.getSmoothFromPoint().getId());
		stmt.setString(6, clamping.getImageUrl());
		stmt.setFloat(7, clamping.getHeight());
		stmt.setInt(8, clamping.getFixtureType().getCode());
		stmt.setInt(9, clamping.getDefaultAirblowPoints().getBottomCoord().getId());
		stmt.setInt(10, clamping.getDefaultAirblowPoints().getTopCoord().getId());
		try {
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				clamping.setId(resultSet.getInt(1));
				for (WorkAreaManager workArea : workAreas) {
					PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("INSERT INTO WORKAREA_CLAMPING (WORKAREA, CLAMPING) VALUES (?, ?)");
					stmt2.setInt(1, workArea.getId());
					stmt2.setInt(2, clamping.getId());
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
	
	public void deleteClamping(final Clamping clamping) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		try {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM WORKAREA_CLAMPING WHERE CLAMPING = ?");
			stmt.setInt(1, clamping.getId());
			stmt.executeUpdate();
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("DELETE FROM CLAMPING WHERE ID = ?");
			stmt2.setInt(1, clamping.getId());
			stmt2.executeUpdate();
			ConnectionManager.getConnection().commit();
		} catch (SQLException e) {
			ConnectionManager.getConnection().rollback();
			throw e;
		} finally {
			ConnectionManager.getConnection().setAutoCommit(true);
		}
	}
	
	public boolean hasClamping(final int id) throws SQLException {
		try {
			PreparedStatement stmt = ConnectionManager.getConnection()
					.prepareStatement(" SELECT DEVICESETTINGS_WORKAREA_CLAMPING.ID "
					+ " FROM DEVICESETTINGS_WORKAREA_CLAMPING "
					+ " JOIN WORKAREA_CLAMPING ON "
					+ " WORKAREA_CLAMPING.ID = DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA_CLAMPING "
					+ " WHERE WORKAREA_CLAMPING.CLAMPING = ? ");
			stmt.setInt(1, id);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			throw e;
		}
	}

}