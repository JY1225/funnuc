package eu.robojob.irscw.ui.main.configure.device;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.irscw.ui.main.configure.AbstractMenuView;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class DeviceMenuView extends AbstractMenuView<DeviceMenuPresenter>{

	protected DeviceInformation deviceInfo;
	
	private static final String pickPath = "M 4.6875 0.09375 C 2.161259 0.09375 0.09375 2.161495 0.09375 4.6875 C 0.09375 7.213504 2.161259 9.25 4.6875 9.25 C 7.0056475 9.25 8.9192752 7.517486 9.21875 5.28125 L 17.21875 5.28125 L 14.9375 7.40625 L 17.1875 7.40625 L 20.0625 4.71875 L 20.0625 4.65625 L 17.1875 2 L 14.9375 2 L 17.1875 4.0625 L 9.21875 4.0625 C 8.9192752 1.826023 7.0056475 0.09375 4.6875 0.09375 z";
	private static final String putPath = "m 15.161617,0.03741328 c -2.464229,0 -4.501754,1.83791802 -4.82032,4.21483102 l -5.5044426,0 -2.4140913,-2.209641 -2.34332005,0 2.06023435,1.918692 1.0301172,0.935755 0.00785,0.0079 -0.015727,0 -0.031454,0.03145 0,0.01573 -3.05889762,2.846583 2.35118352,0 2.4534088,-2.248958 5.4651257,0 c 0.318565,2.376913 2.35609,4.214831 4.820319,4.214831 2.684658,0 4.859637,-2.182339 4.859637,-4.8675 -2.52e-4,-2.68491 -2.174979,-4.85963702 -4.859637,-4.85963702 z M 3.1619311,4.9049143 l 0.00785,-0.0079 -0.039317,0 0.031454,0.0079 z";
	
	public DeviceMenuView(DeviceInformation deviceInfo) {
		this.deviceInfo = deviceInfo;
		build();
	}
	
	@Override
	protected void build() {
		int index = 0;
		if (deviceInfo.getPutStep() != null) {
			addMenuItem(index++, putPath, translator.getTranslation("ConfigurePut"), true, new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					presenter.configurePut();
				}
			});
		}
		if (deviceInfo.getPickStep() != null) {
			addMenuItem(index++, pickPath, translator.getTranslation("ConfigurePick"), true, new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					presenter.configurePick();
				}
			});
		}
	}

}
