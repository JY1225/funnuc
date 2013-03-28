package eu.robojob.irscw.ui.automate.device.stacking;

import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class BasicStackPlateRefillPresenter extends AbstractFormPresenter<BasicStackPlateRefillView, BasicStackPlateMenuPresenter> {

	public BasicStackPlateRefillPresenter(final BasicStackPlateRefillView view, final BasicStackPlate basicStackPlate) {
		super(view);
		getView().setBasicStackPlate(basicStackPlate);
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	public void refill(final int amount) {
		
	}
}
