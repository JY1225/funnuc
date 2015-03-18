package eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;

public class ConveyorRawWorkPieceLayoutPresenter<T extends AbstractMenuPresenter<?>> extends AbstractWorkPieceLayoutPresenter<ConveyorRawWorkPieceLayoutView, T> {
	
	public ConveyorRawWorkPieceLayoutPresenter(final ConveyorRawWorkPieceLayoutView view, final ConveyorEaton conveyor) {
		super(view, conveyor);
		getConveyor().addListener(this);
		getView().setConveyorLayout(conveyor.getLayout());
		getView().build();
		refresh();
	}
	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		if (getConveyor().getLayout().getStackingPositionTrackA() != null) {
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
				refresh();
			}
		});
	}
	
	private void refresh() {
		if (getConveyor().isConnected()) {
			getView().setConnected(true);
			getView().setModeManual(!getConveyor().isModeAuto());
			getView().setLockedA(getConveyor().isTrackAInterlock());
			if (getConveyor().isTrackAMoving()) {
				if (getConveyor().isTrackASlow()) {
					getView().setRotatingA(ConveyorRawWorkPieceLayoutView.ROTATING_SLOW);	
				} else {
					getView().setRotatingA(ConveyorRawWorkPieceLayoutView.ROTATING);
				}
			} else {
				getView().setRotatingA(ConveyorRawWorkPieceLayoutView.NOT_ROTATING);
			}
			getView().setSensorsAActive(getConveyor().isTrackASensor1(), false);
			getView().setTrackBModeLoad(getConveyor().isTrackBModeLoad());
			getView().setLockedB(getConveyor().isTrackBInterlock());
			if (getConveyor().isTrackBMoving()) {
				if (getConveyor().isTrackBModeLoad()) {
					if (getConveyor().isTrackBSlow()) {
						getView().setRotatingB(ConveyorRawWorkPieceLayoutView.ROTATING_SLOW);
					} else {
						getView().setRotatingB(ConveyorRawWorkPieceLayoutView.ROTATING);
					}
				} else {
					getView().setRotatingB(ConveyorRawWorkPieceLayoutView.ROTATING_BACK);
				}
			} else {
				getView().setRotatingB(ConveyorRawWorkPieceLayoutView.NOT_ROTATING);
			}
			if (getConveyor().isTrackBModeLoad()) {
				getView().setSensorsBActive(getConveyor().isTrackBSensor1(), false);
			} else {
				getView().setSensorsBActive(false, false);
			}
		} else {
			getView().setConnected(false);
		}
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
	public void conveyorAlarmsOccured(final ConveyorAlarmsOccuredEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override public void finishedShifted(final float distance) { }
	
	@Override
	public void conveyorStatusChanged(final ConveyorEvent event) {
		refresh();
	}
	
}
