package eu.robojob.millassist.ui.admin.device;

import java.util.SortedSet;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridHole;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.ui.RoboSoftAppFactory;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.util.Translator;

public class GridPlateConfigurePresenter extends AbstractFormPresenter<GridPlateConfigureView2, DeviceMenuPresenter> {

	private GridPlate selectedGridPlate;
	private DeviceManager deviceManager;
	
	public GridPlateConfigurePresenter(final GridPlateConfigureView2 view, final DeviceManager deviceManager) {
		super(view);
		this.deviceManager = deviceManager;
		getView().build();
		getView().refresh();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void disableEditMode() {
		this.selectedGridPlate = null;
	}
	
	// not used
	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void updateGridPlates() {
		getView().setGridPlates(deviceManager.getAllGridPlates());
	}
	
	public void clickedEdit(GridPlate gridPlate) {
		this.selectedGridPlate = gridPlate;
		getView().gridPlateSelected(selectedGridPlate);
	}
	
	public void clickedNew() {
		getView().reset();
		this.selectedGridPlate = null;
	}
	
	public void saveData(final String name, final float width, final float height, final float depth, final float offsetX,
			final float offsetY, final float holeLength, final float holeWidth, final SortedSet<GridHole> gridholes) {
		if(selectedGridPlate == null) {
			deviceManager.saveGridPlate(name, width, height, depth, offsetX, offsetY, holeLength, holeWidth, gridholes);
		} else {
			deviceManager.updateGridPlate(selectedGridPlate, name, width, height, depth, offsetX, offsetY, holeLength, holeWidth, gridholes);
		}
	}

	public void deleteGridPlate() {
		deviceManager.deleteGridPlate(selectedGridPlate);	
		this.selectedGridPlate = null;
	}
	
	public void setActiveGridPlateByName(String name) {
		this.selectedGridPlate = deviceManager.getGridPlateByName(name);
	}
	
	void saveAsData(final float width, final float height, final float depth, final float offsetX,
			final float offsetY, final float holeLength, final float holeWidth, final SortedSet<GridHole> gridholes) {
		ThreadManager.submit(new Runnable() {

			@Override
			public void run() {
				String name = RoboSoftAppFactory.getMainPresenter().askInputString(Translator.getTranslation(GridPlateConfigureView2.COPY), 
						Translator.getTranslation(GridPlateConfigureView2.SAVE_AS_DIALOG), 
						Translator.getTranslation(GridPlateConfigureView2.NAME));
				if(!name.equals("")) {
					try {
						deviceManager.saveGridPlate(name, width, height, depth, offsetX, offsetY, holeLength, holeWidth, gridholes);
					} catch (IllegalArgumentException e) {
						RoboSoftAppFactory.getMainPresenter().closeInputString(getView());
						return;
					}
				}
				RoboSoftAppFactory.getMainPresenter().closeInputString(getView());
			}
		});
	}
}
