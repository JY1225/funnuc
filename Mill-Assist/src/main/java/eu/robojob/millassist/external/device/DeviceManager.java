package eu.robojob.millassist.external.device;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.external.device.DeviceMapper;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine.WayOfOperating;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.bin.OutputBin;
import eu.robojob.millassist.external.device.stacking.conveyor.AbstractConveyor;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.process.ProcessFlowManager;

public class DeviceManager {
	
	private Map<String, AbstractDevice> devicesByName;
	private Map<Integer, AbstractDevice> devicesById;
	private Map<String, AbstractCNCMachine> cncMachinesByName;
	private Map<String, AbstractProcessingDevice> preProcessingDevicesByName;
	private Map<String, AbstractProcessingDevice> postProcessingDevicesByName;
	private Map<String, AbstractStackingDevice> stackingFromDevicesByName;
	private Map<String, AbstractStackingDevice> stackingToDevicesByName;
	private Map<String, GridPlateLayout> gridPlatesByName;
	private Map<Integer, GridPlateLayout> gridPlatesById;
	private ProcessFlowManager processFlowManager;
	
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
		this.gridPlatesByName = new HashMap<String, GridPlateLayout>();
		this.gridPlatesById = new HashMap<Integer, GridPlateLayout>();
		initialize();
	}
	
	public void setProcessFlowManager(final ProcessFlowManager processFlowManager) {
		this.processFlowManager = processFlowManager;
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
				} else if (device.getType().equals(DeviceType.PRE_PROCESSING)) {
					preProcessingDevicesByName.put(device.getName(), (AbstractProcessingDevice) device);
				} else if (device.getType().equals(DeviceType.POST_PROCESSING)) {
					postProcessingDevicesByName.put(device.getName(), (AbstractProcessingDevice) device);
				} else if (device instanceof AbstractStackingDevice) {
					if (!(device instanceof OutputBin)) {
						stackingFromDevicesByName.put(device.getName(), (AbstractStackingDevice) device);
					}
					stackingToDevicesByName.put(device.getName(), (AbstractStackingDevice) device);
				}
			}
			Set<GridPlateLayout> allGridPlates = deviceMapper.getAllGridPlates();
			for(GridPlateLayout gridPlate: allGridPlates) {
				gridPlatesByName.put(gridPlate.getName(), gridPlate);	
				gridPlatesById.put(gridPlate.getId(), gridPlate);
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
	
	public void refresh() {
		cncMachinesByName.clear();
		preProcessingDevicesByName.clear();
		stackingFromDevicesByName.clear();
		stackingToDevicesByName.clear();
		for (AbstractDevice device : devicesById.values()) {
			devicesByName.put(device.getName(), device);
			if (device instanceof AbstractCNCMachine) {
				cncMachinesByName.put(device.getName(), (AbstractCNCMachine) device);
			} else if (device.getType().equals(DeviceType.PRE_PROCESSING)) {
				preProcessingDevicesByName.put(device.getName(), (AbstractProcessingDevice) device);
			} else if (device.getType().equals(DeviceType.POST_PROCESSING)) {
				postProcessingDevicesByName.put(device.getName(), (AbstractProcessingDevice) device);
			} else if (device instanceof AbstractStackingDevice) {
				if (!(device instanceof OutputBin)) {
					stackingFromDevicesByName.put(device.getName(), (AbstractStackingDevice) device);
				}
				stackingToDevicesByName.put(device.getName(), (AbstractStackingDevice) device);
			}
		}
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
	
	public Set<AbstractConveyor> getConveyors() {
		Set<AbstractConveyor> conveyors = new HashSet<AbstractConveyor>();
		for (AbstractDevice device : getStackingFromDevices()) {
			if (device instanceof AbstractConveyor) {
				conveyors.add((AbstractConveyor) device);
			}
		}
		return conveyors;
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
	
	public GridPlateLayout getGridPlateByID(final int ID) {
		return gridPlatesById.get(ID);
	}
	
	public GridPlateLayout getGridPlateByName(final String name) {
		return gridPlatesByName.get(name);
	}

	public Collection<GridPlateLayout> getAllGridPlates() {
		return gridPlatesByName.values();
	}
	
	public Set<String> getAllGridPlateNames() {
		return gridPlatesByName.keySet();
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
			final float horizontalR, final float tiltedR, final float maxOverflow, final float minOverlap, final float studHeight, final float smoothToX, final float smoothToY, final float smoothToZ,
			final float smoothFromX, final float smoothFromY, final float smoothFromZ) {
		try {
			deviceMapper.updateBasicStackPlate(basicStackPlate, name, userFrameName, horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, 
					horizontalHoleDistance, horizontalPadding, verticalPaddingTop, verticalPaddingBottom, interferenceDistance, overflowPercentage, horizontalR, tiltedR,
					maxOverflow, minOverlap, studHeight, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ);
			basicStackPlate.loadDeviceSettings(basicStackPlate.getDeviceSettings());
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}		
	}
	
	public void saveGridPlate(final String name, final float posFirstX, final float posFirstY, final float offsetX, final float offsetY,
			final int nbRows, final int nbColumns, final float height, final float holeLength, final float holeWidth,
			final float length, final float width, final float posX, final float posY, final float smoothToX,
			final float smoothToY, final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ, int orientation) {
		GridPlateLayout gridPlate = new GridPlateLayout(name, length, width, height, posFirstX, posFirstY, holeLength, holeWidth, offsetX, offsetY, nbColumns, nbRows,
				posX, posY, orientation);
		try {
			if(!gridPlatesByName.containsKey(name)) {
				deviceMapper.saveGridPlate(gridPlate, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ);
				gridPlatesByName.put(name, gridPlate);
				gridPlatesById.put(gridPlate.getId(), gridPlate);
				refresh();	
			} else {
				logger.error("Plate name already exists");
				throw new IllegalArgumentException("Plate name already exists: " + name);
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void updateGridPlate(final GridPlateLayout gridPlate, final String name, final float posFirstX, final float posFirstY, final float offsetX, final float offsetY,
			final int nbRows, final int nbColumns, final float height, final float holeLength, final float holeWidth,
			final float length, final float width, final float posX, final float posY, final float smoothToX,
			final float smoothToY, final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ, int orientation) {
		try {
			if(!gridPlate.getName().equals(name)) {
				gridPlatesByName.remove(gridPlate.getName());
				gridPlatesByName.put(name, gridPlate);
			}
			deviceMapper.updateGridPlate(gridPlate, name, posFirstX, posFirstY, offsetX, offsetY, nbRows, nbColumns, height, 
					holeLength, holeWidth, length, width, posX, posY, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, 
					smoothFromZ, orientation);
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void deleteGridPlate(final GridPlateLayout gridPlate) {
		try{
			deviceMapper.deleteGridPlate(gridPlate);
			gridPlatesByName.remove(gridPlate.getName());
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void updatePrageDeviceData(final PrageDevice prageDevice, final String name, final Clamping.Type type, final float relPosX, final float relPosY, 
			final float relPosZ, final float relPosR, final float smoothToX, final float smoothToY, final float smoothToZ,
			final float smoothFromX, final float smoothFromY, final float smoothFromZ, final int widthOffsetR) {
		try {
			deviceMapper.updatePrageDevice(prageDevice, name, type, relPosX, relPosY, relPosZ, relPosR, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ, widthOffsetR);
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void updateOutputBinData(final OutputBin outputBin, final String name, final String userFrame, final float x, 
			final float y, final float z, final float w, final float p, final float r, final float smoothToX, final float smoothToY,
			final float smoothToZ) {
		try {
			deviceMapper.updateOutputBin(outputBin, name, userFrame, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ);
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void updateCNCMachineData(final AbstractCNCMachine cncMachine, final String name, final WayOfOperating wayOfOperating,
			final String ipAddress, final int port, final int clampingWidthR, final boolean newDevInt, final List<String> robotServiceInputNames,
			final List<String> robotServiceOutputNames, final List<String> mCodeNames,
			final List<Set<Integer>> mCodeRobotServiceInputs, final List<Set<Integer>> mCodeRobotServiceOutputs) {
		try {
			deviceMapper.updateCNCMachine(cncMachine, name, wayOfOperating, ipAddress, port,
					clampingWidthR, newDevInt, robotServiceInputNames, robotServiceOutputNames, mCodeNames, mCodeRobotServiceInputs,
					mCodeRobotServiceOutputs);
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}	
	}
	
	public void updateReversalUnitData(final ReversalUnit reversalUnit, final String name, final String userFrame, final float x, 
			final float y, final float z, final float w, final float p, final float r, final float smoothToX, final float smoothToY,
			final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ, final float stationHeight) {
		try {
			deviceMapper.updateReversalUnit(reversalUnit, name, userFrame, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, 
					smoothFromX, smoothFromY, smoothFromZ, stationHeight);
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void updateClamping(final Clamping clamping, final String name, final Clamping.Type type, final float height, 
			final String imagePath, final float x, final float y, final float z, final float w, final float p, 
			final float r, final float smoothToX, 
			final float smoothToY, final float smoothToZ, final float smoothFromX, final float smoothFromY, 
			final float smoothFromZ, final Clamping.FixtureType fixtureType) {
		try {
			deviceMapper.updateClamping(clamping, name, type, height, imagePath, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, 
				smoothFromX, smoothFromY, smoothFromZ, fixtureType);
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void saveClamping(final String name, final Clamping.Type type, final float height, final String imagePath, final float x, 
			final float y, final float z, final float w, final float p, final float r, final float smoothToX, final float smoothToY, 
			final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ, final Clamping.FixtureType fixtureType) {
		try {
			Clamping clamping = new Clamping(type, name, height, new Coordinates(x, y, z, w, p, r), 
					new Coordinates(smoothToX, smoothToY, smoothToZ, 0, 0, 0), 
					new Coordinates(smoothFromX, smoothFromY, smoothFromZ, 0, 0, 0), imagePath, fixtureType);
			Set<WorkArea> workAreas = new HashSet<WorkArea>();
			for (AbstractCNCMachine cncMachine : getCNCMachines()) {
				for (WorkArea workArea : cncMachine.getWorkAreas()) {
					workAreas.add(workArea);
				}
			}
			deviceMapper.saveClamping(clamping, workAreas);
			for (WorkArea workArea : workAreas) {
				workArea.addClamping(clamping);
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void deleteClamping(final Clamping clamping) {
		for (DeviceSettings deviceSettings : processFlowManager.getActiveProcessFlow().getDeviceSettings().values()) {
			for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
				if (entry.getValue().equals(clamping)) {
					entry.setValue(null);
				}
			}
		}
		for (AbstractDevice device : devicesById.values()) {
			for (WorkArea workArea : device.getWorkAreas()) {
				workArea.getClampings().remove(clamping);
			}
		}
		try {
			deviceMapper.deleteClamping(clamping);
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
}

