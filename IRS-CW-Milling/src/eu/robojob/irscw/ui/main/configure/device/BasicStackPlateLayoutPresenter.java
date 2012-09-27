package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.external.device.BasicStackPlate;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;

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
}
