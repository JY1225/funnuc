package eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton;

import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorListener;
import eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;

public abstract class AbstractWorkPieceLayoutPresenter<S extends AbstractWorkPieceLayoutView<?>, T extends AbstractMenuPresenter<?>> extends AbstractFormPresenter<S, T> 
implements ConveyorListener { 

private ConveyorEaton conveyor;

public AbstractWorkPieceLayoutPresenter(final S view, final ConveyorEaton conveyor) {
	super(view);
	this.conveyor = conveyor;
}

public ConveyorEaton getConveyor() {
	return conveyor;
}

public void setConveyor(final ConveyorEaton conveyor) {
	this.conveyor = conveyor;
}

}
