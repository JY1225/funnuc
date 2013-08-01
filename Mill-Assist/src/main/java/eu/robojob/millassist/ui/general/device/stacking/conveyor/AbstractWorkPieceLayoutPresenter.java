package eu.robojob.millassist.ui.general.device.stacking.conveyor;

import eu.robojob.millassist.external.device.stacking.conveyor.Conveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;

public abstract class AbstractWorkPieceLayoutPresenter<S extends AbstractWorkPieceLayoutView<?>, T extends AbstractMenuPresenter<?>> extends AbstractFormPresenter<S, T> 
	implements ConveyorListener { 
	
	private Conveyor conveyor;
	
	public AbstractWorkPieceLayoutPresenter(final S view, final Conveyor conveyor) {
		super(view);
		this.conveyor = conveyor;
	}

	public Conveyor getConveyor() {
		return conveyor;
	}

	public void setConveyor(final Conveyor conveyor) {
		this.conveyor = conveyor;
	}

}
