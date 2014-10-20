package eu.robojob.millassist.ui.automate.device.stacking.stackplate;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlateListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.device.stacking.stackplate.BasicStackPlateLayoutView;

//TODO - change listeners
public class BasicStackPlateLayoutPresenter extends AbstractFormPresenter<BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter>, BasicStackPlateMenuPresenter> 
	implements BasicStackPlateListener {
	
	private BasicStackPlate stackPlate;
	
	public BasicStackPlateLayoutPresenter(final BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter> view, 
			final BasicStackPlate basicStackPlate) {
		super(view);
		getView().setBasicStackPlate(basicStackPlate);
		this.stackPlate = basicStackPlate;
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
	
	public BasicStackPlate getStackPlate() {
		return this.stackPlate;
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

	@Override
	public void unregister() {
		stackPlate.removeListener(this);
	}

}
