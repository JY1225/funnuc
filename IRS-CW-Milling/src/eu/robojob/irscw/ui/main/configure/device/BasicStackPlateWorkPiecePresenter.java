package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.external.device.BasicStackPlate;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class BasicStackPlateWorkPiecePresenter extends AbstractFormPresenter<BasicStackPlateWorkPieceView, BasicStackPlateMenuPresenter> {

	private PickStep pickStep;
	private WorkPieceDimensions dimensions;
	
	public BasicStackPlateWorkPiecePresenter(BasicStackPlateWorkPieceView view, PickStep pickStep) {
		super(view);
		this.pickStep = pickStep;
		
		this.dimensions = ((BasicStackPlate) pickStep.getDevice()).getRawWorkPieceDimensions();
		pickStep.getRobotSettings().setWorkPieceDimensions(dimensions);
			
		view.setPickStep(pickStep);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}
	
	public void changedWidth(float width) {
		dimensions.setWidth(width);
	}
	
	public void changedLength(float length) {
		dimensions.setLength(length);
	}
	
	public void changedHeight(float height) {
		dimensions.setHeight(height);
	}
	
	public void changedAmount(int amount) {
		((BasicStackPlate) pickStep.getDevice()).setRawWorkPieceAmount(amount);
	}

}
