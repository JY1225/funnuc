package eu.robojob.millassist.ui.admin.device;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout.HoleOrientation;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class GridPlateConfigurePresenter extends AbstractFormPresenter<GridPlateConfigureView, DeviceMenuPresenter> {

	private GridPlateLayout selectedGridPlate;
	private int selectedOrientation;
	private DeviceManager deviceManager;
	private boolean editMode;
	
	public GridPlateConfigurePresenter(final GridPlateConfigureView view, final DeviceManager deviceManager) {
		super(view);
		this.deviceManager = deviceManager;
		this.editMode = false;
		this.selectedOrientation = 0;
		getView().build();
		getView().refresh();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void disableEditMode() {
		this.selectedGridPlate = null;
		this.editMode = false;
	}
	
	// not used
	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void updateGridPlates() {
		getView().setGridPlates(deviceManager.getAllGridPlateNames());
	}
	
	public void clickedEdit(String gridPlateName) {
		//If we are already in editMode - a reset is sent to disable the details screen
		if (editMode) {
			getView().reset();
			this.selectedGridPlate = null;
			editMode = false;	
		} else {
			this.selectedGridPlate = deviceManager.getGridPlateByName(gridPlateName);
			getView().gridPlateSelected(selectedGridPlate);
			getView().showFormEdit();
			editMode = true;
		}
	}
	
	public void clickedNew() {
		getView().reset();
		if (!editMode) {
			this.selectedGridPlate = null;
			getView().showFormNew();
			editMode = true;
		} else {
			editMode = false;
		}
	}
	
	public void saveData(final String name, final float posFirstX, final float posFirstY, final float offsetX, final float offsetY,
			final int nbRows, final int nbColumns, final float height, final float holeLength, final float holeWidth,
			final float length, final float width, final float horizontalPadding, final float verticalPaddingTop, 
			final float verticalPaddingBottom, final float horizontalR, final float tiltedR, final float smoothToX,
			final float smoothToY, final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ) {
		if(selectedGridPlate == null) {
			deviceManager.saveGridPlate(name, posFirstX, posFirstY, offsetX, offsetY, nbRows, nbColumns, height, 
					holeLength, holeWidth, length, width, horizontalPadding, verticalPaddingTop, verticalPaddingBottom, 
					horizontalR, tiltedR, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ, selectedOrientation);
		} else {
			deviceManager.updateGridPlate(selectedGridPlate, name, posFirstX, posFirstY, offsetX, offsetY, nbRows, nbColumns, height, 
					holeLength, holeWidth, length, width, horizontalPadding, verticalPaddingTop, verticalPaddingBottom, 
					horizontalR, tiltedR, smoothToX, smoothToY, smoothToZ, smoothFromX, smoothFromY, smoothFromZ, selectedOrientation);
		}
	}

	public void changedOrientation(HoleOrientation orientation) {
		this.selectedOrientation = orientation.getId();
		getView().setOrientation(selectedOrientation);
	}

	public void deleteGridPlate() {
		deviceManager.deleteGridPlate(selectedGridPlate);	
	}
	

}
