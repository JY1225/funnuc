package eu.robojob.millassist.ui.configure.device.processing.cnc;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.ui.configure.device.DeviceMenuView;
import eu.robojob.millassist.util.Translator;

public class CNCMillingMachineMenuView extends DeviceMenuView {

	private static final String WORKPIECE_ICON = "M 6.25 0 L 4 3.375 L 5.65625 3.375 L 5.65625 6.25 L 6.875 6.25 L 6.875 3.375 L 8.5625 3.375 L 6.25 0 z M 0 7.5 L 0 16.875 L 12.5 16.875 L 12.5 7.5 L 0 7.5 z M 16.65625 9.90625 L 16.65625 11.625 L 13.75 11.625 L 13.75 12.78125 L 16.65625 12.78125 L 16.65625 14.46875 L 20 12.1875 L 16.65625 9.90625 z";
	private static final String WORKPIECE = "CNCMillingMachineMenuView.workpiece";
	
	private int workPieceIndex;
	
	@Override
	protected void build() {
		super.build();
		workPieceIndex = getMenuItemsSize();
		addMenuItem(workPieceIndex, WORKPIECE_ICON, Translator.getTranslation(WORKPIECE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				((CNCMillingMachineMenuPresenter) getPresenter()).configureWorkPiece();
			}
		});
	}
	
	public void setWorkPieceActive() {
		setMenuItemSelected(workPieceIndex);
	}
}
