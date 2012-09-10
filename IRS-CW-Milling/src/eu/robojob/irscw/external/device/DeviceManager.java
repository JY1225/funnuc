package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.UserFrame;

public class DeviceManager {
	
	private Map<String, AbstractCNCMachine> cncMachines;
	private Map<String, AbstractProcessingDevice> preProcessingDevices;
	private Map<String, AbstractProcessingDevice> postProcessingDevices;
	private Map<String, AbstractStackingDevice> stackingFromDevices;
	private Map<String, AbstractStackingDevice> stackingToDevices;
	
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
		for (AbstractStackingDevice device : stackingFromDevices.values()) {
			if (device.getId().equals(id)) {
				return device;
			}
		}
		for (AbstractProcessingDevice device : preProcessingDevices.values()) {
			if (device.getId().equals(id)) {
				return device;
			}
		}
		for (AbstractCNCMachine device : cncMachines.values()) {
			if (device.getId().equals(id)) {
				return device;
			}
		}
		for(AbstractProcessingDevice device : postProcessingDevices.values()) {
			if (device.getId().equals(id)) {
				return device;
			}
		}
		for (AbstractStackingDevice device : stackingToDevices.values()) {
			if (device.getId().equals(id)) {
				return device;
			}
		}
		return null;
	}
}
