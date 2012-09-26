package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

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
		Clamping clamping1 = new Clamping("clamping 1", new Coordinates(10, 0, 5, 0, 0, 45), new Coordinates(5, 5, 5, 0, 0, 45), null);
		Clamping clamping2 = new Clamping("clamping 2", new Coordinates(15, 10, 5, 0, 0, 45), new Coordinates(5, 5, 5, 0, 0, 45), null);
		Clamping clamping3 = new Clamping("clamping 3", new Coordinates(5, 20, 5, 0, 0, 45), new Coordinates(5, 5, 5, 0, 0, 45), null);
		UserFrame uf1 = new UserFrame(1, 20);
		UserFrame uf2 = new UserFrame(2, 25);
		List<WorkArea> workAreas = new ArrayList<WorkArea>();
		WorkArea workArea1 = new WorkArea("main", uf1);
		workArea1.addClamping(clamping1);
		workArea1.addClamping(clamping2);
		WorkArea workArea2 = new WorkArea("second", uf2);
		workArea2.addClamping(clamping3);
		workAreas.add(workArea1);
		workAreas.add(workArea2);
		Zone zone1 = new Zone("zone 1", workAreas);
		CNCMillingMachine cncMillingMachine = new CNCMillingMachine("Mazak integrex", null);
		cncMillingMachine.addZone(zone1);
		cncMachines.put(cncMillingMachine.getId(), cncMillingMachine);
		
		// add Stacking Machine
		UserFrame uf3 = new UserFrame(3, 20);
		List<WorkArea> workAreas2 = new ArrayList<WorkArea>();
		WorkArea workArea3 = new WorkArea("main", uf3);
		workAreas2.add(workArea3);
		Zone zone2 = new Zone("Zone 2", workAreas2);
		Conveyor conveyor1 = new Conveyor("conveyor 1");
		conveyor1.addZone(zone2);
		stackingFromDevices.put(conveyor1.getId(), conveyor1);
		stackingToDevices.put(conveyor1.getId(), conveyor1);	
		
		// add Embossing Machine
		UserFrame uf4 = new UserFrame(4, 10);
		List<WorkArea> workAreas3 = new ArrayList<WorkArea>();
		WorkArea workArea4 = new WorkArea("main", uf4);
		workAreas3.add(workArea4);
		Zone zone3 = new Zone("Zone 3", workAreas3);
		EmbossingDevice embossing1 = new EmbossingDevice("embossing 1", null);
		embossing1.addZone(zone3);
		preProcessingDevices.put(embossing1.getId(), embossing1);
		
		// add Basic Stacker
		UserFrame uf5 = new UserFrame(5, 20);
		List<WorkArea> workAreas4 = new ArrayList<WorkArea>();
		WorkArea workArea5 = new WorkArea("basic stacker", uf5);
		workAreas4.add(workArea5);
		Zone zone4 = new Zone("Zone 4", workAreas4);
		BasicStackPlate basicStackPlate = new BasicStackPlate("basic stack plate",  27, 7, 10, 15, 45, 40, 35, 0, 0.25f);
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
