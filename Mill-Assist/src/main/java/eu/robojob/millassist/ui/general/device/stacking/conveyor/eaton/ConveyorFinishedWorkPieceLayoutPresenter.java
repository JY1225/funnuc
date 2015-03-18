package eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;

public class ConveyorFinishedWorkPieceLayoutPresenter<T extends AbstractMenuPresenter<?>> extends AbstractWorkPieceLayoutPresenter<ConveyorFinishedWorkPieceLayoutView, T> {
	
	public ConveyorFinishedWorkPieceLayoutPresenter(final ConveyorFinishedWorkPieceLayoutView view, final ConveyorEaton conveyor) {
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
		if (getConveyor().getLayout().getStackingPositionTrackB() != null) {
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
				getView().setTrackBModeLoad(getConveyor().isTrackBModeLoad());
				getView().setLockedB(getConveyor().isTrackBInterlock());
				getView().setSensorBActive(getConveyor().isTrackBSensor1());
				if (getConveyor().isTrackBMoving()) {
					if (getConveyor().isTrackBModeLoad()) {
						getView().setRotatingB(ConveyorRawWorkPieceLayoutView.ROTATING);
					} else {
						getView().setRotatingB(ConveyorRawWorkPieceLayoutView.ROTATING_BACK);
					}
				} else {
					getView().setRotatingB(ConveyorRawWorkPieceLayoutView.NOT_ROTATING);
				}
			}
		});
	}

	@Override public void conveyorAlarmsOccured(final ConveyorAlarmsOccuredEvent event) { }

	@Override
	public void finishedShifted(final float distance) {
		
	}

}
