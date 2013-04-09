package eu.robojob.irscw.external.device;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.db.external.device.DeviceMapper;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.processing.prage.PrageDevice;
import eu.robojob.irscw.external.device.stacking.AbstractStackingDevice;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.UserFrame;

public class DeviceManager {
	
	private Map<String, AbstractDevice> devicesByName;
	private Map<Integer, AbstractDevice> devicesById;
	private Map<String, AbstractCNCMachine> cncMachinesByName;
	private Map<String, AbstractProcessingDevice> preProcessingDevicesByName;
	private Map<String, AbstractProcessingDevice> postProcessingDevicesByName;
	private Map<String, AbstractStackingDevice> stackingFromDevicesByName;
	private Map<String, AbstractStackingDevice> stackingToDevicesByName;
	
	private static Logger logger = LogManager.getLogger(DeviceManager.class.getName());
	private DeviceMapper deviceMapper;
	
	public DeviceManager(final DeviceMapper deviceMapper) {
		this.deviceMapper = deviceMapper;
		this.devicesByName = new HashMap<String, AbstractDevice>();
		this.devicesById = new HashMap<Integer, AbstractDevice>();
		this.cncMachinesByName = new HashMap<String, AbstractCNCMachine>();
		this.preProcessingDevicesByName = new HashMap<String, AbstractProcessingDevice>();
		this.postProcessingDevicesByName = new HashMap<String, AbstractProcessingDevice>();
		this.stackingFromDevicesByName = new HashMap<String, AbstractStackingDevice>();
		this.stackingToDevicesByName = new HashMap<String, AbstractStackingDevice>();
		initialize();
	}
	
	private void initialize() {
		Set<AbstractDevice> allDevices;
		try {
			allDevices = deviceMapper.getAllDevices();
			for (AbstractDevice device : allDevices) {
				devicesByName.put(device.getName(), device);
				devicesById.put(device.getId(), device);
				if (device instanceof AbstractCNCMachine) {
					cncMachinesByName.put(device.getName(), (AbstractCNCMachine) device);
				} else if (device instanceof PrageDevice) {
					preProcessingDevicesByName.put(device.getName(), (PrageDevice) device);
				} else if (device instanceof BasicStackPlate) {
					stackingFromDevicesByName.put(device.getName(), (BasicStackPlate) device);
					stackingToDevicesByName.put(device.getName(), (BasicStackPlate) device);
				}
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public Set<AbstractCNCMachine> getCNCMachines() {
		return new HashSet<AbstractCNCMachine>(cncMachinesByName.values());
	}
	
	public Set<String> getCNCMachineNames() {
		return cncMachinesByName.keySet();
	}
	
	public Collection<AbstractProcessingDevice> getPreProcessingDevices() {
		return preProcessingDevicesByName.values();
	}
	
	public Set<String> getPreProcessingDeviceNames() {
		return preProcessingDevicesByName.keySet();
	}
	
	public Collection<AbstractProcessingDevice> getPostProcessingDevices() {
		return postProcessingDevicesByName.values();
	}
	
	public Set<String> getPostProcessingDeviceNames() {
		return postProcessingDevicesByName.keySet();
	}
	
	public Collection<AbstractStackingDevice> getStackingFromDevices() {
		return stackingFromDevicesByName.values();
	}
	
	public Set<String> getStackingFromDeviceNames() {
		return stackingFromDevicesByName.keySet();
	}
	
	public Collection<AbstractStackingDevice> getStackingToDevices() {
		return stackingToDevicesByName.values();
	}
	
	public Set<String> getStackingToDeviceNames() {
		return stackingToDevicesByName.keySet();
	}
	
	public Set<String> getStackingDeviceNames() {
		Set<String> ids =  new HashSet<String>(getStackingFromDeviceNames());
		Set<String> toIds = new HashSet<String>(getStackingToDeviceNames());
		ids.addAll(toIds);
		return ids;
	}
	
	public AbstractDevice getDeviceByName(final String name) {
		return devicesByName.get(name);		
	}
	
	public AbstractDevice getDeviceById(final int id) {
		return devicesById.get(id);
	}
	
	public AbstractStackingDevice getStackingFromDeviceByName(final String name) {
		return stackingFromDevicesByName.get(name);
	}
	
	public AbstractCNCMachine getCNCMachineByName(final String name) {
		return cncMachinesByName.get(name);
	}
	
	public AbstractProcessingDevice getPreProcessingDeviceByName(final String name) {
		return preProcessingDevicesByName.get(name);
	}
	
	public AbstractProcessingDevice getPostProcessingDeviceByName(final String name) {
		return postProcessingDevicesByName.get(name);
	}
	
	public AbstractStackingDevice getStackingToDeviceByName(final String name) {
		return stackingToDevicesByName.get(name);
	}
	
	public Set<UserFrame> getAllUserFrames() {
		try {
			return deviceMapper.getAllUserFrames();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void updateUserFrame(final UserFrame userFrame, final String name, final int number, final float zSafeDistance, 
			final float x, final float y, final float z, final float w, final float p, final float r) {
		try {
			deviceMapper.updateUserFrame(userFrame, name, number, zSafeDistance, x, y, z, w, p, r);
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void addUserFrame(final String name, final int number, final float zSafeDistance, final float x, final float y, final float z,
			final float w, final float p, final float r) {
		UserFrame userFrame = new UserFrame(number, name, zSafeDistance, new Coordinates(x, y, z, w, p, r));
		try {
			deviceMapper.saveUserFrame(userFrame);
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void updateBasicStackPlate(final BasicStackPlate basicStackPlate, final String name, final String userFrameName, final int horizontalHoleAmount, 
			final int verticalHoleAmount, final float holeDiameter, final float studDiameter, final float horizontalHoleDistance, final float horizontalPadding, 
			final float verticalPaddingTop, final float verticalPaddingBottom, final float interferenceDistance, final float overflowPercentage,
			final float horizontalR, final float tiltedR, final float smoothToX, final float smoothToY, final float smoothToZ,
			final float smoothFromX, final float smoothFromY, final float smoothFromZ) {
		try {
			deviceMapper.updateBasicStackPlate(basicStackPlate, name, userFrameName, horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, 
					horizontalHoleDistance, horizontalPadding, verticalPaddingTop, verticalPaddingBottom, interferenceDistance, overflowPercentage, horizontalR, tiltedR,
					smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ);
			basicStackPlate.loadDeviceSettings(basicStackPlate.getDeviceSettings());
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}		
	}
}
