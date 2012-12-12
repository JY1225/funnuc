package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.communication.SocketConnection.Type;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.processing.cnc.CNCMillingMachine;
import eu.robojob.irscw.external.device.processing.prage.PrageDevice;
import eu.robojob.irscw.external.device.stacking.AbstractStackingDevice;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateLayout;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.UserFrame;

public class DeviceManager {
	
	private Map<String, AbstractCNCMachine> cncMachines;
	private Map<String, AbstractProcessingDevice> preProcessingDevices;
	private Map<String, AbstractProcessingDevice> postProcessingDevices;
	private Map<String, AbstractStackingDevice> stackingFromDevices;
	private Map<String, AbstractStackingDevice> stackingToDevices;
	
	private static Logger logger = LogManager.getLogger(DeviceManager.class.getName());
	
	public static final String IRS_M_BASIC = "IRS M Basic";
	public static final String PRAGE_DEVICE = "Präge";
	public static final String MAZAK_VRX = "Mazak VRX J500";
	
	private static final String CNC_IP = "cnc-ip";
	private static final String CNC_PORT = "cnc-port";
	
	private RobotManager robotManager;
	
	private Properties properties;
	
	public DeviceManager(final RobotManager robotManager, final Properties properties) {
		this.cncMachines = new HashMap<String, AbstractCNCMachine>();
		this.preProcessingDevices = new HashMap<String, AbstractProcessingDevice>();
		this.postProcessingDevices = new HashMap<String, AbstractProcessingDevice>();
		this.stackingFromDevices = new HashMap<String, AbstractStackingDevice>();
		this.stackingToDevices = new HashMap<String, AbstractStackingDevice>();
		this.robotManager = robotManager;
		this.properties = properties;
		initialize();
	}
	
	//TODO build up devices with data from external source (database)
	private void initialize() {
		
		// CNC Milling machine
		Clamping shunkClamping = new Clamping(eu.robojob.irscw.external.device.Clamping.Type.CENTRUM, "Shunk", 0, new Coordinates(1, -1f, 2.5f, 0, 0, 0), new Coordinates(0, 0, 10, 0, 0, 0), null);
		UserFrame machineUserFrame = new UserFrame(3, 5);
		List<WorkArea> machineWAs = new ArrayList<WorkArea>();
		WorkArea machineMainWA = new WorkArea("Mazak VRX J500 Main", machineUserFrame);
		machineMainWA.addClamping(shunkClamping);
		machineMainWA.setActiveClamping(shunkClamping);
		machineWAs.add(machineMainWA);
		Zone machineMainZone = new Zone("Mazak VRX Main J500 Zone", machineWAs);
		SocketConnection cncSocketCon = new SocketConnection(Type.CLIENT, "Mazak VRX J500", properties.getProperty(CNC_IP), Integer.parseInt(properties.getProperty(CNC_PORT)));  // other: 6
		CNCMillingMachine cncMillingMachine = new CNCMillingMachine("Mazak VRX J500", cncSocketCon);
		cncMillingMachine.addZone(machineMainZone);
		cncMachines.put(cncMillingMachine.getId(), cncMillingMachine);
		
		// Basic Stacker
		Clamping stackerClamping = new Clamping(eu.robojob.irscw.external.device.Clamping.Type.NONE, "Stacker", 25, new Coordinates(0, 0, 0, 0, 0, 0), new Coordinates(2, 10, 10, 0, 0, 0), null);
		UserFrame stackerUserFrame = new UserFrame(1, 100);
		List<WorkArea> stackerWAs = new ArrayList<WorkArea>();
		WorkArea stackerWA = new WorkArea("IRS M Basic", stackerUserFrame);
		stackerWA.addClamping(stackerClamping);
		stackerWA.setActiveClamping(stackerClamping);
		stackerWAs.add(stackerWA);
		Zone stackerMainZone = new Zone("IRS M Basic Zone", stackerWAs);
		BasicStackPlateLayout basicStackPlateLayout = new BasicStackPlateLayout(27, 7, 10f, 15f, 45f, 40f, 26f, 35f, 1f, 0.5f);
		BasicStackPlate basicStackPlate = new BasicStackPlate("IRS M Basic", basicStackPlateLayout);
		basicStackPlate.addZone(stackerMainZone);
		stackingFromDevices.put(basicStackPlate.getId(), basicStackPlate);
		stackingToDevices.put(basicStackPlate.getId(), basicStackPlate);
		
		// Praege Device
		float x = Float.parseFloat(properties.getProperty("prage-x"));
		float y = Float.parseFloat(properties.getProperty("prage-y"));
		float z = Float.parseFloat(properties.getProperty("prage-z"));
		Clamping praegeClamping = new Clamping(eu.robojob.irscw.external.device.Clamping.Type.FIXED, "Clamping 5", 25, new Coordinates(x, y, z, 0, 0, 90), new Coordinates(0, 2, 5, 0, 0, 0), null);
		List<WorkArea> praegeWAs = new ArrayList<WorkArea>();
		WorkArea praegeWA = new WorkArea("Präge", stackerUserFrame);
		praegeWAs.add(praegeWA);
		praegeClamping.addRelatedClamping(shunkClamping);
		shunkClamping.addRelatedClamping(praegeClamping);
		praegeWA.addClamping(praegeClamping);
		praegeWA.setActiveClamping(praegeClamping);
		Zone praegeZone = new Zone("Zone 3", praegeWAs);
		PrageDevice prageDevice = new PrageDevice("Präge", robotManager.getRobotById("Fanuc M20iA"));
		prageDevice.addZone(praegeZone);
		preProcessingDevices.put(prageDevice.getId(), prageDevice);
		
	}
	
	public Collection<AbstractCNCMachine> getCNCMachines() {
		return cncMachines.values();
	}
	
	public Set<String> getCNCMachineIds() {
		return cncMachines.keySet();
	}
	
	public Collection<AbstractProcessingDevice> getPreProcessingDevices() {
		return preProcessingDevices.values();
	}
	
	public Set<String> getPreProcessingDeviceIds() {
		return preProcessingDevices.keySet();
	}
	
	public Collection<AbstractProcessingDevice> getPostProcessingDevices() {
		return postProcessingDevices.values();
	}
	
	public Set<String> getPostProcessingDeviceIds() {
		return postProcessingDevices.keySet();
	}
	
	public Collection<AbstractStackingDevice> getStackingFromDevices() {
		return stackingFromDevices.values();
	}
	
	public Set<String> getStackingFromDeviceIds() {
		return stackingFromDevices.keySet();
	}
	
	public Collection<AbstractStackingDevice> getStackingToDevices() {
		return stackingToDevices.values();
	}
	
	public Set<String> getStackingToDeviceIds() {
		return stackingToDevices.keySet();
	}
	
	public Set<String> getStackingDeviceIds() {
		Set<String> ids =  new HashSet<String>(getStackingFromDeviceIds());
		Set<String> toIds = new HashSet<String>(getStackingToDeviceIds());
		ids.addAll(toIds);
		return ids;
	}
	
	public AbstractDevice getDeviceById(final String id) {
		AbstractStackingDevice stackingFrom = getStackingFromDeviceById(id);
		AbstractStackingDevice stackingTo = getStackingToDeviceById(id);
		AbstractCNCMachine cncMachine = getCNCMachineById(id);
		AbstractProcessingDevice prePocessing = getPreProcessingDeviceById(id);
		AbstractProcessingDevice postProcessing = getPostProcessingDeviceById(id);
		if ((stackingFrom == null) && (stackingTo == null) && (cncMachine == null) && (prePocessing == null) && (postProcessing == null)) {
			logger.info("no device found with id: " + id);
			return null; 
		} else if (stackingFrom != null) {
			return stackingFrom;
		} else if (stackingTo != null) {
			return stackingTo;
		} else if (cncMachine != null) {
			return cncMachine;
		} else if (prePocessing != null) {
			return prePocessing;
		} else {
			return postProcessing;
		}
		
	}
	
	public AbstractStackingDevice getStackingFromDeviceById(final String id) {
		return stackingFromDevices.get(id);
	}
	
	public AbstractCNCMachine getCNCMachineById(final String id) {
		return cncMachines.get(id);
	}
	
	public AbstractProcessingDevice getPreProcessingDeviceById(final String id) {
		return preProcessingDevices.get(id);
	}
	
	public AbstractProcessingDevice getPostProcessingDeviceById(final String id) {
		return postProcessingDevices.get(id);
	}
	
	public AbstractStackingDevice getStackingToDeviceById(final String id) {
		return stackingToDevices.get(id);
	}
}
