package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;

public class BasicStackPlateWorkPiecePresenter extends AbstractFormPresenter<BasicStackPlateWorkPieceView, BasicStackPlateMenuPresenter> {

	private PickStep pickStep;
	
	public BasicStackPlateWorkPiecePresenter(BasicStackPlateWorkPieceView view, PickStep pickStep) {
		super(view);
		this.pickStep = pickStep;
		view.setPickStep(pickStep);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

}
