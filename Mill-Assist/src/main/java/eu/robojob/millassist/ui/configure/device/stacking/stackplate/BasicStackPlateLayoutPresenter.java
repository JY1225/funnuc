package eu.robojob.millassist.ui.configure.device.stacking.stackplate;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlateListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.device.stacking.stackplate.BasicStackPlateLayoutView;

public class BasicStackPlateLayoutPresenter extends AbstractFormPresenter<BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter>, BasicStackPlateMenuPresenter> 
	implements BasicStackPlateListener {

	private BasicStackPlate basicStackPlate;
	
	public BasicStackPlateLayoutPresenter(final BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter> view, final BasicStackPlate basicStackPlate, final ClampingManner clampingType) {
		super(view);
		try {
			this.basicStackPlate = basicStackPlate;
			basicStackPlate.addListener(this);
			view.setBasicStackPlate(basicStackPlate);
			view.setClampingType(clampingType);
			view.build(); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return (basicStackPlate.getLayout().getStackingPositions().size() > 0);
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
		basicStackPlate.removeListener(this);
	}
}
