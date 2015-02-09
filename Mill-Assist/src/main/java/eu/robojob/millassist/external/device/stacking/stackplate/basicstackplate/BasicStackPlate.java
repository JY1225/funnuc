package eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

/**
 * This device needs to maintain state (position contents change during process-execution). 
 * Basic Stack Plate: one layer, can contain Raw of Finished workPieces with same orientation and dimensions
 */
public class BasicStackPlate extends AbstractStackPlate {

	public static final float STUD_HEIGHT = 30;
	private List<BasicStackPlateListener> listeners;
	private BasicStackPlateLayout basicLayout;
		
	private static Logger logger = LogManager.getLogger(BasicStackPlate.class.getName());	
	
	public BasicStackPlate(final String name, final Set<Zone> zones, final BasicStackPlateLayout layout) {
		super(name, zones);
		this.listeners = new ArrayList<BasicStackPlateListener>();
		setLayout(layout);
		this.basicLayout = layout;
		setRawWorkPiece(new WorkPiece(Type.RAW, new WorkPieceDimensions(), Material.OTHER, 0.0f));
	}
	
	public BasicStackPlate(final String name, final BasicStackPlateLayout layout) {
		this(name, new HashSet<Zone>(), layout);
	}
	
	@Override public void prepareForPick(final DevicePickSettings pickSettings, final int processId) { }
	@Override public void prepareForPut(final DevicePutSettings putSettings, final int processId) { }
	@Override public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) { }
	@Override public void interventionFinished(final DeviceInterventionSettings interventionSettings) { }
	@Override public void releasePiece(final DevicePickSettings pickSettings) { }
	@Override public void grabPiece(final DevicePutSettings putSettings) { }
	@Override public void interruptCurrentAction() { }
	@Override public void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	
	@Override
	public EDeviceGroup getType() {
		return EDeviceGroup.BASIC_STACK_PLATE;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void prepareForProcess(final ProcessFlow process) throws AbstractCommunicationException, InterruptedException {}
	
	@Override
	public void notifyLayoutChanged() {
		for (BasicStackPlateListener listener : getListeners()) {
			listener.layoutChanged();
		}
	}
	
	public BasicStackPlateLayout getBasicLayout() {
		return this.basicLayout;
	}
	
	public void addListener(final BasicStackPlateListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(final BasicStackPlateListener listener) {
		this.listeners.remove(listener);
	}
	
	public void clearListeners() {
		this.listeners.clear();
	}

	public List<BasicStackPlateListener> getListeners() {
		return this.listeners;
	}
	
	public boolean hasGridPlate() {
		return (getLayout() instanceof GridPlateLayout);
	}
	
	public void setGridPlate(GridPlate gridPlate) {
		AbstractStackPlateDeviceSettings deviceSettings = getDeviceSettings();
		if(gridPlate != null) {
			logger.debug("Adding gridplate [" + gridPlate.getName() + "] to stackplate.");
			deviceSettings.setGridId(gridPlate.getId());
			setLayout(new GridPlateLayout(gridPlate));
			loadDeviceSettings(deviceSettings);
		} else {
			logger.debug("Gridplate removed from stackplate.");
			deviceSettings.setGridId(0);
			setLayout(getBasicLayout());
		}
	}

	public synchronized void addWorkPieces(final int amount, boolean reset) throws IncorrectWorkPieceDataException {
		getLayout().placeRawWorkPieces(getRawWorkPiece(), amount, reset);
		notifyLayoutChanged();
	}
	
	public float getR(double orientation) {
		if(orientation == 45)
			return basicLayout.getTiltedR();
		else
			return basicLayout.getHorizontalR();
	}
}
