package eu.robojob.millassist.ui.general.device.stacking.conveyor;

import eu.robojob.millassist.external.device.stacking.conveyor.Conveyor;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;

public class ConveyorRawWorkPieceLayoutPresenter<T extends AbstractMenuPresenter<?>> extends AbstractWorkPieceLayoutPresenter<ConveyorRawWorkPieceLayoutView, T> {
	
	
	public ConveyorRawWorkPieceLayoutPresenter(final ConveyorRawWorkPieceLayoutView view, final Conveyor conveyor) {
		super(view, conveyor);
		getView().setConveyorLayout(conveyor.getLayout());
		getView().build();
	}
	
	public void configureSupports() {
		
	}
	
	public void allSupportsDown() {
		
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void layoutChanged() {
		getView().refresh();
	}

	@Override
	public void unregister() {
		// TODO Auto-generated method stub
		
	}

}
