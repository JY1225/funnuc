package eu.robojob.irscw.ui.configure.device;

import eu.robojob.irscw.external.device.ClampingType;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;

public class BasicStackPlateLayoutPresenter extends AbstractFormPresenter<BasicStackPlateLayoutView, BasicStackPlateMenuPresenter> {

	private BasicStackPlate basicStackPlate;
	
	public BasicStackPlateLayoutPresenter(BasicStackPlateLayoutView view, BasicStackPlate basicStackPlate, ClampingType clampingType) {
		super(view);
		this.basicStackPlate = basicStackPlate;
		view.setBasicStackPlate(basicStackPlate);
		view.setClampingType(clampingType);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return ((basicStackPlate.getLayout().getStackingPositions().size() > 0) && (basicStackPlate.getLayout().getStackingPositions().get(0).getWorkPiece() != null) );
	}
}
