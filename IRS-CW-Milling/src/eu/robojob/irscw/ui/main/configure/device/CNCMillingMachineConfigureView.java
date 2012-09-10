package eu.robojob.irscw.ui.main.configure.device;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.configure.AbstractFormView;

public class CNCMillingMachineConfigureView extends AbstractFormView<CNCMillingMachineConfigurePresenter> {

	private Label lblMachine;
	private ComboBox<String> cbbMachine;
	private Label lblClampingName;
	private FullTextField ftxtClampingName;
	private Label lblDeltaX;
	private Label lblDeltaY;
	private Label lblDeltaZ;
	private Label lblDeltaR;
	
	@Override
	protected void build() {
		lblMachine = new Label(translator.getTranslation("CNCMillingMachineConfigureView.machine"));
		cbbMachine = new ComboBox<String>();
		lblClampingName = new Label(translator.getTranslation("CNCMillingMachineConfigureView.clampingName"));
		ftxtClampingName = new FullTextField(30);
		lblDeltaX = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaX"));
		lblDeltaY = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaY"));
		lblDeltaZ = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaZ"));
		lblDeltaR = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaR"));
	}

	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		
	}

}
