package eu.robojob.irscw.ui.configure.device.stacking;

import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class BasicStackPlateLayoutPresenter extends AbstractFormPresenter<BasicStackPlateLayoutView, BasicStackPlateMenuPresenter> {

	private BasicStackPlate basicStackPlate;
	
	public BasicStackPlateLayoutPresenter(final BasicStackPlateLayoutView view, final BasicStackPlate basicStackPlate, final ClampingManner clampingType) {
		super(view);
		this.basicStackPlate = basicStackPlate;
		view.setBasicStackPlate(basicStackPlate);
		view.setClampingType(clampingType);
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return ((basicStackPlate.getLayout().getStackingPositions().size() > 0) && (basicStackPlate.getLayout().getStackingPositions().get(0).getWorkPiece() != null));
	}
}
