package eu.robojob.millassist.ui.configure.device.stacking.conveyor;

import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.ConveyorRawWorkPieceLayoutView;

public class ConveyorRawWorkPieceLayoutPresenter extends AbstractFormPresenter
	<ConveyorRawWorkPieceLayoutView<ConveyorRawWorkPieceLayoutPresenter>, ConveyorMenuPresenter>  {

	public ConveyorRawWorkPieceLayoutPresenter(final ConveyorRawWorkPieceLayoutView<ConveyorRawWorkPieceLayoutPresenter> view) {
		super(view);
		getView().build();
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

}
