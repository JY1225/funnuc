package eu.robojob.irscw.ui.configure.device;

import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;

public class BasicStackPlateLayoutPresenter extends AbstractFormPresenter<BasicStackPlateLayoutView, BasicStackPlateMenuPresenter> {

	private BasicStackPlate basicStackPlate;
	
	public BasicStackPlateLayoutPresenter(BasicStackPlateLayoutView view, BasicStackPlate basicStackPlate) {
		super(view);
		this.basicStackPlate = basicStackPlate;
		view.setBasicStackPlate(basicStackPlate);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	public void notifyIncorrectWorkPieceDate() {
		//TODO implement
	}

	@Override
	public boolean isConfigured() {
		return (basicStackPlate.getLayout().getStackingPositions().size() > 0);
	}
}
