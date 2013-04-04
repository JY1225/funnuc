package eu.robojob.irscw.ui.automate.device.stacking;

import javafx.application.Platform;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateListener;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;
import eu.robojob.irscw.ui.general.device.stacking.BasicStackPlateLayoutView;

public class BasicStackPlateLayoutPresenter extends AbstractFormPresenter<BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter>, BasicStackPlateMenuPresenter> 
	implements BasicStackPlateListener {
	
	private BasicStackPlate stackPlate;
	
	public BasicStackPlateLayoutPresenter(final BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter> view, 
			final BasicStackPlate basicStackPlate) {
		super(view);
		getView().setBasicStackPlate(basicStackPlate);
		this.stackPlate = basicStackPlate;
		this.stackPlate.clearListeners();
		this.stackPlate.addListener(this);
		getView().build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	@Override
	public void layoutChanged() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().build();
			}
		});
	}

}
