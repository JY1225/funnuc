package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.communication.SocketConnection.Type;
import eu.robojob.irscw.external.device.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine;
import eu.robojob.irscw.external.device.stacking.AbstractStackingDevice;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateLayout;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.UserFrame;

public class DeviceManager {
	
	private Map<String, AbstractCNCMachine> cncMachines;
	private Map<String, AbstractProcessingDevice> preProcessingDevices;
	private Map<String, AbstractProcessingDevice> postProcessingDevices;
	private Map<String, AbstractStackingDevice> stackingFromDevices;
	private Map<String, AbstractStackingDevice> stackingToDevices;
	
	private static Logger logger = Logger.getLogger(DeviceManager.class);
	
	//TODO enforce unique ids
	public DeviceManager() {
		cncMachines = new HashMap<String, AbstractCNCMachine>();
		preProcessingDevices = new HashMap<String, AbstractProcessingDevice>();
		postProcessingDevices = new HashMap<String, AbstractProcessingDevice>();
		stackingFromDevices = new HashMap<String, AbstractStackingDevice>();
		stackingToDevices = new HashMap<String, AbstractStackingDevice>();
		initialize();
	}
	
	private void initialize() {
		
		// add CNC Milling machine
		Clamping clamping1 = new Clamping("Clamping 1", 52.7f, new Coordinates(0, 0, 90.5f, 0, 0, 0), new Coordinates(0, 0, 50, 0, 0, 0), null);
		Clamping clamping2 = new Clamping("Clamping 2", 52.7f, new Coordinates(0, 0, 90.5f, 0, 0, 0), new Coordinates(0, 0, 50, 0, 0, 0), null);
		Clamping clamping3 = new Clamping("Clamping 3", 52.7f, new Coordinates(0, 0, 90.5f, 0, 0, 0), new Coordinates(0, 0, 50, 0, 0, 0), null);
		UserFrame uf3 = new UserFrame(3, 20);
		List<WorkArea> workAreas = new ArrayList<WorkArea>();
		WorkArea workArea1 = new WorkArea("Mazak VRX Main", uf3);
		workArea1.addClamping(clamping1);
		workArea1.addClamping(clamping2);
		workArea1.addClamping(clamping3);
		workArea1.setActiveClamping(clamping1);
		/*WorkArea workArea2 = new WorkArea("Mazak VRX Second", uf2);
		workArea2.addClamping(clamping3);*/
		workAreas.add(workArea1);
	//	workAreas.add(workArea2);
		Zone zone1 = new Zone("zone 1", workAreas);
		//SocketConnection cncSocketCon = new SocketConnection(Type.CLIENT, "cnc socket", "192.168.200.4", 2010);  // other: 6
		SocketConnection cncSocketCon = new SocketConnection(Type.CLIENT, "cnc socket", "10.10.40.12", 2010);  // other: 6
		CNCMillingMachine cncMillingMachine = new CNCMillingMachine("Mazak VRX J500", cncSocketCon);
		cncMillingMachine.addZone(zone1);
		cncMachines.put(cncMillingMachine.getId(), cncMillingMachine);
		
		// add Stacking Machine
		/*UserFrame uf3 = new UserFrame(3, 20);
		List<WorkArea> workAreas2 = new ArrayList<WorkArea>();
		WorkArea workArea3 = new WorkArea("main", uf3);
		workAreas2.add(workArea3);
		Zone zone2 = new Zone("Zone 2", workAreas2);
		Conveyor conveyor1 = new Conveyor("conveyor 1");
		conveyor1.addZone(zone2);
		stackingFromDevices.put(conveyor1.getId(), conveyor1);
		stackingToDevices.put(conveyor1.getId(), conveyor1);	*/
		
		// add Embossing Machine
		UserFrame uf4 = new UserFrame(4, 10);
		List<WorkArea> workAreas3 = new ArrayList<WorkArea>();
		WorkArea workArea4 = new WorkArea("main", uf4);
		workAreas3.add(workArea4);
		/*Zone zone3 = new Zone("Zone 3", workAreas3);
		EmbossingDevice embossing1 = new EmbossingDevice("embossing 1", null);
		embossing1.addZone(zone3);
		preProcessingDevices.put(embossing1.getId(), embossing1);*/
		
		// add Basic Stacker
		UserFrame uf1 = new UserFrame(1, 20);
		List<WorkArea> workAreas4 = new ArrayList<WorkArea>();
		Clamping clamping4 = new Clamping("Clamping 4", 25, new Coordinates(0, 0, 0, 0, 0, 0), new Coordinates(2, 10, 10, 0, 0, 0), null);
		WorkArea workArea5 = new WorkArea("IRS M Basic", uf1);
		workArea5.addClamping(clamping4);
		workArea5.setActiveClamping(clamping4);
		workAreas4.add(workArea5);
		Zone zone4 = new Zone("Zone 4", workAreas4);
		BasicStackPlateLayout basicStackPlateLayout = new BasicStackPlateLayout(27, 7, 10f, 15f, 45f, 40f, 26f, 35f, 5f, 0.35f);
		BasicStackPlate basicStackPlate = new BasicStackPlate("IRS M Basic", basicStackPlateLayout);
		basicStackPlate.addZone(zone4);
		stackingFromDevices.put(basicStackPlate.getId(), basicStackPlate);
		stackingToDevices.put(basicStackPlate.getId(), basicStackPlate);
		
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
	
	public AbstractDevice getDeviceById(String id) {
		AbstractStackingDevice stackingFrom = getStackingFromDeviceById(id);
		AbstractStackingDevice stackingTo = getStackingToDeviceById(id);
		AbstractCNCMachine cncMachine = getCNCMachineById(id);
		AbstractProcessingDevice prePocessing = getPreProcessingDeviceById(id);
		AbstractProcessingDevice postProcessing = getPostProcessingDeviceById(id);
		if ((stackingFrom == null) && (stackingTo == null) && (cncMachine == null) && (prePocessing == null) && (postProcessing == null)) {
			logger.info("no device found with id: " + id);
			return null; 
		} 
		else if (stackingFrom != null) {
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
	
	public AbstractStackingDevice getStackingFromDeviceById(String id) {
		return stackingFromDevices.get(id);
	}
	
	public AbstractCNCMachine getCNCMachineById(String id) {
		return cncMachines.get(id);
	}
	
	public AbstractProcessingDevice getPreProcessingDeviceById(String id) {
		return preProcessingDevices.get(id);
	}
	
	public AbstractProcessingDevice getPostProcessingDeviceById(String id) {
		return postProcessingDevices.get(id);
	}
	
	public AbstractStackingDevice getStackingToDeviceById(String id) {
		return stackingToDevices.get(id);
	}
}
