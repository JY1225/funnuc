package eu.robojob.millassist.ui.admin.device;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.EFixtureType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.ui.RoboSoftAppFactory;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.util.Translator;

public class CNCMachineClampingsPresenter extends AbstractFormPresenter<CNCMachineClampingsView, DeviceMenuPresenter> {

	private Clamping selectedClamping;
	private DeviceManager deviceManager;
	private boolean editMode;
	
	public CNCMachineClampingsPresenter(final CNCMachineClampingsView view, final DeviceManager deviceManager) {
		super(view);
		this.editMode = false;
		this.deviceManager = deviceManager;
		getView().setDeviceManager(deviceManager);
		getView().refresh();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void disableEditMode() {
		this.selectedClamping = null;
		this.editMode = false;
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void selectedClamping(final Clamping clamping) {
		if (!editMode) {
			selectedClamping = clamping;
			getView().clampingSelected(clamping);
		}
	}

	public void clickedEdit() {
		if (editMode) {
			getView().reset();
			editMode = false;
		} else {
			getView().showFormEdit();
			editMode = true;
		}
	}
	
	public void clickedNew() {
		getView().reset();
		if (!editMode) {
			selectedClamping = null;
			getView().showFormNew();
			editMode = true;
		} else {
			editMode = false;
		}
	}
	
	public void clickedCopy() {
		selectedClamping = null;
	}
	
	public void deleteClamping() {
		deviceManager.deleteClamping(selectedClamping);
		selectedClamping = null;
		editMode = false;
		getView().refresh();
	}
	
	void copyClamping(final float height, final String imagePath, final float x, final float y, final float z, final float w, final float p, final float r, 
			final float smoothToX, final float smoothToY, final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ, 
			final Clamping.Type clampingType, final EFixtureType fixtureType, final Coordinates bottomAirblowCoord, final Coordinates topAirblowCoord) {
		ThreadManager.submit(new Runnable() {

			@Override
			public void run() {
				String name = RoboSoftAppFactory.getMainPresenter().askInputString(Translator.getTranslation(CNCMachineClampingsView.COPY), 
						Translator.getTranslation(CNCMachineClampingsView.SAVE_AS_DIALOG), 
						Translator.getTranslation(CNCMachineClampingsView.NAME));
				if(!name.equals("")) {
					deviceManager.saveClamping(name, clampingType, height, imagePath, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, 
							smoothFromX, smoothFromY, smoothFromZ, fixtureType, bottomAirblowCoord, topAirblowCoord);
				}
				selectedClamping = null;
				editMode = false;
				RoboSoftAppFactory.getMainPresenter().closeInputString(getView());
			}

		});
	}
	
	void updateClamping(final String name, final float height, final String imagePath, final float x, 
			final float y, final float z, final float w, final float p, final float r, final float smoothToX, final float smoothToY, 
			final float smoothToZ, final float smoothFromX, final float smoothFromY, final float smoothFromZ, 
			final Clamping.Type clampingType, final EFixtureType fixtureType, final Coordinates bottomAirblowCoord, final Coordinates topAirblowCoord) {
		if (selectedClamping == null) {
			deviceManager.saveClamping(name, clampingType, height, imagePath, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, 
					smoothFromX, smoothFromY, smoothFromZ, fixtureType, bottomAirblowCoord, topAirblowCoord);
		} else {
			deviceManager.updateClamping(selectedClamping, name, clampingType, height, imagePath, x, y, z, w, p, r, smoothToX, smoothToY, smoothToZ, 
				smoothFromX, smoothFromY, smoothFromZ, fixtureType, bottomAirblowCoord, topAirblowCoord);
		}
		selectedClamping = null;
		editMode = false;
		getView().refresh();
	}
}
