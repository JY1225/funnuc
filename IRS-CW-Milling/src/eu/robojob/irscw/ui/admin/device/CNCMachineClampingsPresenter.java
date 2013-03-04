package eu.robojob.irscw.ui.admin.device;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class CNCMachineClampingsPresenter extends AbstractFormPresenter<CNCMachineClampingsView, DeviceMenuPresenter> {

	private DeviceManager deviceManager;
	private boolean editMode;
	
	public CNCMachineClampingsPresenter(final CNCMachineClampingsView view, final DeviceManager deviceManager) {
		super(view);
		getView().build();
		this.editMode = false;
		this.deviceManager = deviceManager;
		Set<String> clampingNames = new HashSet<String>();
		for (Clamping clamping : deviceManager.getCNCMachines().iterator().next().getWorkAreas().get(0).getClampings()) {
			clampingNames.add(clamping.getName());
		}
		getView().setClampingNames(clampingNames);
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
	
	private Clamping getClampingByName(final String clampingName) {
		for (Clamping clamping : deviceManager.getCNCMachines().iterator().next().getWorkAreas().get(0).getClampings()) {
			if (clamping.getName().equals(clampingName)) {
				return clamping;
			}
		}
		return null;
	}

	public void clickedEdit(final String selectedClampingName) {
		if (editMode) {
			getView().reset();
			editMode = false;
		} else {
			getView().clampingSelected(getClampingByName(selectedClampingName));
			getView().showFormEdit();
			editMode = true;
		}
	}
	
	public void clickedNew() {
		getView().reset();
		if (!editMode) {
			getView().showFormNew();
			editMode = true;
		} else {
			editMode = false;
		}
	}
}
