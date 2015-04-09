package eu.robojob.millassist.external.device;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.external.device.DeviceMapper;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.EWayOfOperating;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.bin.OutputBin;
import eu.robojob.millassist.external.device.stacking.conveyor.AbstractConveyor;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridHole;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.UserFrame;

public class DeviceManager {
	
	private Map<String, AbstractDevice> devicesByName;
	private Map<Integer, AbstractDevice> devicesById;
	private Map<String, AbstractCNCMachine> cncMachinesByName;
	private Map<String, AbstractProcessingDevice> preProcessingDevicesByName;
	private Map<String, AbstractProcessingDevice> postProcessingDevicesByName;
	private Map<String, AbstractStackingDevice> stackingFromDevicesByName;
	private Map<String, AbstractStackingDevice> stackingToDevicesByName;
	private Map<String, GridPlate> gridPlatesByName;
	private Map<Integer, GridPlate> gridPlatesById;
    private Map<Integer, PalletLayout> palletLayoutsById;
    private Map<String, PalletLayout> palletLayoutsByName;
	
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
		this.gridPlatesByName = new HashMap<String, GridPlate>();
		this.gridPlatesById = new HashMap<Integer, GridPlate>();
		this.palletLayoutsById = new HashMap<Integer, PalletLayout>();
		this.palletLayoutsByName = new HashMap<String, PalletLayout>();
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
				} else if (device.getType().equals(EDeviceGroup.PRE_PROCESSING)) {
					preProcessingDevicesByName.put(device.getName(), (AbstractProcessingDevice) device);
				} else if (device.getType().equals(EDeviceGroup.POST_PROCESSING)) {
					postProcessingDevicesByName.put(device.getName(), (AbstractProcessingDevice) device);
				} else if (device instanceof AbstractStackingDevice) {
					if (!(device instanceof OutputBin) && !(device instanceof UnloadPallet)) {
						stackingFromDevicesByName.put(device.getName(), (AbstractStackingDevice) device);
					}
					stackingToDevicesByName.put(device.getName(), (AbstractStackingDevice) device);
				}
			}
			Set<GridPlate> allGridPlates = deviceMapper.getAllGridPlates();
			for(GridPlate gridPlate: allGridPlates) {
				gridPlatesByName.put(gridPlate.getName(), gridPlate);	
				gridPlatesById.put(gridPlate.getId(), gridPlate);
			}
			Set<PalletLayout> allPalletLayouts = deviceMapper.getAllPalletLayouts();
            for(PalletLayout layout: allPalletLayouts) {   
                palletLayoutsById.put(layout.getId(), layout);
                palletLayoutsByName.put(layout.getName(), layout);
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
			} else if (device.getType().equals(EDeviceGroup.PRE_PROCESSING)) {
				preProcessingDevicesByName.put(device.getName(), (AbstractProcessingDevice) device);
			} else if (device.getType().equals(EDeviceGroup.POST_PROCESSING)) {
				postProcessingDevicesByName.put(device.getName(), (AbstractProcessingDevice) device);
			} else if (device instanceof AbstractStackingDevice) {
				if (!(device instanceof OutputBin) && !(device instanceof UnloadPallet)) {
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
	
	public GridPlate getGridPlateByID(final int ID) {
		return gridPlatesById.get(ID);
	}
	
	public GridPlate getGridPlateByName(final String name) {
		return gridPlatesByName.get(name);
	}

	public Collection<GridPlate> getAllGridPlates() {
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
			final float horizontalR, final float tiltedR, final float maxOverflow, final float maxUnderflow, final float minOverlap, final float studHeight, final float smoothToX, final float smoothToY, final float smoothToZ,
			final float smoothFromX, final float smoothFromY, final float smoothFromZ) {
		try {
			deviceMapper.updateBasicStackPlate(basicStackPlate, name, userFrameName, horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, 
					horizontalHoleDistance, horizontalPadding, verticalPaddingTop, verticalPaddingBottom, interferenceDistance, overflowPercentage, horizontalR, tiltedR,
					maxOverflow, maxUnderflow, minOverlap, studHeight, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ);
			basicStackPlate.loadDeviceSettings(basicStackPlate.getDeviceSettings());
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}		
	}
	
	public void updateUnloadPallet(final UnloadPallet unloadPallet, final String name, final String userFrameName, final String stdPalletLayoutName) {
	    try {
            deviceMapper.updateUnloadPallet(unloadPallet, name, userFrameName, getPalletLayoutByName(stdPalletLayoutName));
            refresh();
	    } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
	}
	
	public void savePalletLayout(final String name, final float width, final float length, final float height, final float border, final float xOffset, final float yOffset, final float minInterferenceDistance, final float horizontalR, final float verticalR) throws IllegalArgumentException{
	    PalletLayout layout = new PalletLayout(name, width, length, height, border, xOffset, yOffset, minInterferenceDistance, horizontalR, verticalR);
	    try {
	        if(!palletLayoutsByName.containsKey(name)) {
	            deviceMapper.savePalletLayout(layout);
	            palletLayoutsById.put(layout.getId(), layout);
	            palletLayoutsByName.put(layout.getName(), layout);
	            refresh();
	        } else {
                logger.error("Pallet Layout name already exists");
                throw new IllegalArgumentException("Pallet Layout name already exists: " + name);
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
	}
	
	public void updatePalletLayout(final PalletLayout layout, final String name, final float width, final float length, final float height, final float border, final float xOffset, final float yOffset, final float minInterferenceDistance, final float horizontalR, final float verticalR) throws IllegalArgumentException{
	    if(!layout.getName().equals(name)) {
	        if(!palletLayoutsByName.containsKey(name)) {
                palletLayoutsByName.remove(layout.getName());
                palletLayoutsByName.put(name, layout);
            } else {
                logger.error("Pallet Layout name already exists");
                throw new IllegalArgumentException("Pallet Layout name already exists: " + name);
            }
	    }
        try {
            deviceMapper.updatePalletLayout(layout, name, width, length,
                    height, border, xOffset, yOffset, minInterferenceDistance,
                    horizontalR, verticalR);
            refresh();
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }
	
	public void deletePalletLayout(final PalletLayout layout) {
	    try{
	        List<Integer> defaultLayouts = deviceMapper.getDefaultPalletLayouts();
	        if(defaultLayouts.contains(layout.getId())){
	            throw new IllegalArgumentException("Cannot delete this layout, it is the default layout for the pallet");
	        }
	        else {
	            deviceMapper.deletePalletLayout(layout);
	            palletLayoutsByName.remove(layout.getName());
	            palletLayoutsById.remove(layout.getId());
	        }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
	}
	
	public Collection<PalletLayout> getAllPalletLayouts() {
	    return palletLayoutsById.values();
	}
	
	public PalletLayout getPalletLayoutById(final int id) {
	    return palletLayoutsById.get(id);
	}
	
	public PalletLayout getPalletLayoutByName(final String name) {
        return palletLayoutsByName.get(name);
    }
	
	public Set<String> getAllPalletLayoutNames() {
	    Set<String> result = palletLayoutsByName.keySet();
	    return result;
	}
	
	public void saveGridPlate(final String name, final float width, final float height, final float depth, final float offsetX, final float offsetY, 
			final float holeLength, final float holeWidth, final SortedSet<GridHole> gridholes) {
		GridPlate gridPlate = new GridPlate(name, width, height, gridholes);
		gridPlate.setOffsetX(offsetX);
		gridPlate.setOffsetY(offsetY);
		gridPlate.setDepth(depth);
		gridPlate.setHoleLength(holeLength);
		gridPlate.setHoleWidth(holeWidth);
		try {
			if(!gridPlatesByName.containsKey(name)) {
				deviceMapper.saveGridPlate(gridPlate);
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
	
	public void updateGridPlate(final GridPlate gridPlate, final String name, final float width, final float height, final float depth, final float offsetX, 
			final float offsetY, final float holeLength, final float holeWidth, final SortedSet<GridHole> gridholes) {
		try {
			if(!gridPlate.getName().equals(name)) {
				gridPlatesByName.remove(gridPlate.getName());
				gridPlatesByName.put(name, gridPlate);
			}
			deviceMapper.updateGridPlate(gridPlate, name, width, height, depth, offsetX, offsetY, holeLength, holeWidth, gridholes);
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void deleteGridPlate(final GridPlate gridPlate) {
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
	
	public void updateCNCMachineData(final AbstractCNCMachine cncMachine, final String name, final EWayOfOperating wayOfOperating,
			final String ipAddress, final int port, final int clampingWidthR, final boolean newDevInt, final int nbFixtures, final float rRoundPieces,
			final boolean timAllowed, final boolean machineAirblow, final List<WorkAreaBoundary> airblowBounds, final List<String> robotServiceInputNames, 
			final List<String> robotServiceOutputNames, final List<String> mCodeNames,	final List<Set<Integer>> mCodeRobotServiceInputs, 
			final List<Set<Integer>> mCodeRobotServiceOutputs) {
		try {
			deviceMapper.updateCNCMachine(cncMachine, name, wayOfOperating, ipAddress, port, clampingWidthR, 
					newDevInt, nbFixtures, rRoundPieces, timAllowed, machineAirblow, airblowBounds, robotServiceInputNames, 
					robotServiceOutputNames, mCodeNames, mCodeRobotServiceInputs, mCodeRobotServiceOutputs);
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}	
	}
	
	public void updateReversalUnitData(final ReversalUnit reversalUnit, final String name, final String userFrame, final float x, 
			final float y, final float z, final float w, final float p, final float r, final float smoothToX, final float smoothToY,
			final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ, 
			final float stationLength, final float stationFixtureWidth, final float stationHeight,
			final Map<ApproachType, Boolean> allowedApproaches, final float addedXValue) {
		try {
			deviceMapper.updateReversalUnit(reversalUnit, name, userFrame, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, 
					smoothFromX, smoothFromY, smoothFromZ, stationLength, stationFixtureWidth, stationHeight, allowedApproaches, addedXValue);
			refresh();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	public void updateClamping(final Clamping clamping, final String name, final Clamping.Type type, final float height, 
			final String imagePath, final float x, final float y, final float z, final float w, final float p, 
			final float r, final float smoothToX, final float smoothToY, final float smoothToZ, final float smoothFromX, final float smoothFromY, 
			final float smoothFromZ, final EFixtureType fixtureType, final Coordinates bottomAirblowCoord, final Coordinates topAirblowCoord,
			final int waNr) throws ClampingInUseException {
		try { 
			boolean workAreaChanged = false;
			for (AbstractCNCMachine cncMachine : getCNCMachines()) {
				for (WorkAreaManager workArea : cncMachine.getWorkAreaManagers()) {					
					//TODO - getWorkAreaNr naar boven 
					for (Clamping cl : workArea.getClampings()) {
						if (cl.getId() == clamping.getId()) {
							if (workArea.getWorkAreaNr() == waNr) {
								deviceMapper.updateClamping(cl, name, type, height, imagePath, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, 
										smoothFromX, smoothFromY, smoothFromZ, fixtureType, bottomAirblowCoord, topAirblowCoord);
							} else {
								workAreaChanged = true;
							}
						}
					}
				}
			}
			if (workAreaChanged) {
				deleteClamping(clamping);
				saveClamping(name, type, height, imagePath, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, 
							smoothFromX, smoothFromY, smoothFromZ, fixtureType, bottomAirblowCoord, topAirblowCoord, waNr);
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void saveClamping(final String name, final Clamping.Type type, final float height, final String imagePath, final float x, 
			final float y, final float z, final float w, final float p, final float r, final float smoothToX, final float smoothToY, 
			final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ, final EFixtureType fixtureType,
			final Coordinates bottomAirblowCoord, final Coordinates topAirblowCoord, final int waNr) {
		try {
			Clamping clamping = new Clamping(type, name, height, new Coordinates(x, y, z, w, p, r), 
					new Coordinates(smoothToX, smoothToY, smoothToZ, 0, 0, 0), 
					new Coordinates(smoothFromX, smoothFromY, smoothFromZ, 0, 0, 0), imagePath, fixtureType);
			clamping.setDefaultAirblowPoints(new AirblowSquare(bottomAirblowCoord, topAirblowCoord));
			Set<WorkAreaManager> workAreas = new HashSet<WorkAreaManager>();
			for (AbstractCNCMachine cncMachine : getCNCMachines()) {
				for (WorkAreaManager workArea : cncMachine.getWorkAreaManagers()) {
					if (workArea.getWorkAreaNr() == waNr) {
						workAreas.add(workArea);
					}
				}
			}
			deviceMapper.saveClamping(clamping, workAreas);
			for (WorkAreaManager workArea : workAreas) {
				workArea.addClamping(clamping);
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void deleteClamping(final Clamping clamping) throws ClampingInUseException {
		try {
			if (!deviceMapper.hasClamping(clamping.getId())) {
				for (AbstractDevice device : devicesById.values()) {
					for (WorkAreaManager workArea : device.getWorkAreaManagers()) {
						Set<Clamping> tmpClampings = new HashSet<Clamping>(workArea.getClampings());
						for (Clamping cl: workArea.getClampings()) {
							if (cl.getId() == clamping.getId()) {
								tmpClampings.remove(cl);
							}
						}
						workArea.setClampings(tmpClampings);
					}
				}
				deviceMapper.deleteClamping(clamping);
			}	else {
				throw new ClampingInUseException(clamping.getName());
			}
		}
		catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
}

