package eu.robojob.millassist.ui.general.device.stacking.conveyor;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.stacking.conveyor.Conveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorSensorValuesChangedEvent;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;

public class ConveyorFinishedWorkPieceLayoutPresenter<T extends AbstractMenuPresenter<?>> extends AbstractWorkPieceLayoutPresenter<ConveyorFinishedWorkPieceLayoutView, T> {
	
	public ConveyorFinishedWorkPieceLayoutPresenter(final ConveyorFinishedWorkPieceLayoutView view, final Conveyor conveyor) {
		super(view, conveyor);
		getConveyor().addListener(this);
		getView().setConveyorLayout(conveyor.getLayout());
		getView().build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		if (getConveyor().getLayout().getStackingPositionsFinishedWorkPieces().size() > 0) {
			return true;
		} 
		return false;
	}

	@Override
	public void layoutChanged() {
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				getView().refresh();
			}
		});
	}

	@Override
	public void unregister() {
		getConveyor().removeListener(this);
	}

	@Override
	public void conveyorConnected(final ConveyorEvent event) {
		Platform.runLater(new Thread() {
			@Override public void run() {
				getView().setConnected(true);
			}
		});
	}

	@Override
	public void conveyorDisconnected(final ConveyorEvent event) {
		Platform.runLater(new Thread() {
			@Override public void run() {
				getView().setConnected(false);
			}
		});
	}

	@Override
	public void conveyorStatusChanged(final ConveyorEvent event) {
		Platform.runLater(new Thread() {
			@Override public void run() {
				getView().setLocked(getConveyor().isInterlockFinished());
				getView().setModeManual(!getConveyor().isModeAuto());
				getView().setMoving(getConveyor().isMovingFinished());
			}
		});
	}

	@Override public void conveyorAlarmsOccured(final ConveyorAlarmsOccuredEvent event) { }
	@Override public void sensorValuesChanged(final ConveyorSensorValuesChangedEvent event) { }

	@Override
	public void finishedShifted(final float distance) {
		getView().shiftFinishedWorkPieces(distance);
	}

}
