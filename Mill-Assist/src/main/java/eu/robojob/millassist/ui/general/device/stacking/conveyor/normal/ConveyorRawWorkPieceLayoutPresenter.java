package eu.robojob.millassist.ui.general.device.stacking.conveyor.normal;

import java.util.List;

import javafx.application.Platform;

import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorSensorValuesChangedEvent;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;

public class ConveyorRawWorkPieceLayoutPresenter<T extends AbstractMenuPresenter<?>> extends AbstractWorkPieceLayoutPresenter<ConveyorRawWorkPieceLayoutView, T> {
	
	public ConveyorRawWorkPieceLayoutPresenter(final ConveyorRawWorkPieceLayoutView view, final Conveyor conveyor) {
		super(view, conveyor);
		getConveyor().addListener(this);
		getView().setConveyorLayout(conveyor.getLayout());
		getView().build();
	}
	
	public void configureSupports() {
		getConveyor().configureSupports();
	}
	
	public void allSupportsDown() {
		getConveyor().allSupportsDown();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		if (getConveyor().getLayout().getStackingPositionsRawWorkPieces().size() > 0) {
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
				getView().setModeManual(!getConveyor().isModeAuto());
				getView().updateSupportStatus();
				getView().setMoving(getConveyor().isMovingRaw());
				getView().setLocked(getConveyor().isInterlockRaw());
			}
		});
	}
	
	public void hideButtons() {
		getView().hideButtons();
	}

	@Override
	public void conveyorAlarmsOccured(final ConveyorAlarmsOccuredEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sensorValuesChanged(final ConveyorSensorValuesChangedEvent event) {
		Platform.runLater(new Thread() {
			@Override public void run() {
				List<Integer> sensorValues = event.getSensorValues();
				getView().setSensorValues(sensorValues);
			}
		});
	}

	@Override public void finishedShifted(final float distance) { }
}
