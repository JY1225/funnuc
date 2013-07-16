package eu.robojob.millassist.ui.configure.device.stacking.conveyor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorSettings;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class ConveyorRawWorkPiecePresenter extends AbstractFormPresenter<ConveyorRawWorkPieceView, ConveyorMenuPresenter> {

	private PickStep pickStep;
	private WorkPieceDimensions dimensions;
	private ConveyorSettings deviceSettings;
	
	private WorkPiece workPiece;
	
	private static Logger logger = LogManager.getLogger(ConveyorRawWorkPiecePresenter.class.getName());
	private static final String WEIGHT_ZERO = "ConveyorRawWorkPiecePresenter.weightZero";
	
	public ConveyorRawWorkPiecePresenter(final ConveyorRawWorkPieceView view, final PickStep pickStep, final ConveyorSettings deviceSettings) {
		super(view);
		this.pickStep = pickStep;
		this.deviceSettings = deviceSettings;
		this.workPiece = pickStep.getRobotSettings().getWorkPiece();
		deviceSettings.setRawWorkPiece(workPiece);	
		this.dimensions = workPiece.getDimensions();
		view.setDeviceSettings(deviceSettings);
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void recalcWeight() {
		workPiece.calculateWeight();
		getView().setWeight(workPiece.getMaterial(), workPiece.getWeight());
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
		recalculate();
	}
	
	public boolean isWeightOk() {
		return (deviceSettings.getRawWorkPiece().getWeight() > 0);
	}
	
	public void changedMaterial(final Material material) {
		if (!material.equals(workPiece.getMaterial())) {
			if (material.equals(Material.OTHER)) {
				workPiece.setMaterial(Material.OTHER);
				pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			} else {
				workPiece.setMaterial(material);
				workPiece.calculateWeight();
				pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			}
			getView().setWeight(workPiece.getMaterial(), workPiece.getWeight());
			recalculate();
		}
	}
	
	public void changedWidth(final float width) {
		logger.info("Set width [" + width + "].");
		if (width != dimensions.getWidth()) {
			dimensions.setWidth(width);	
			recalculate();
			pickStep.setRelativeTeachedOffset(null);
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		}
	}
	
	public void changedLength(final float length) {
		logger.info("Set length [" + length + "].");
		if (length != dimensions.getLength()) {
			dimensions.setLength(length);
			recalculate();
			pickStep.setRelativeTeachedOffset(null);
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		}
	}
	
	public void changedHeight(final float height) {
		logger.info("Set height [" + height + "].");
		if (height != dimensions.getHeight()) {
			dimensions.setHeight(height);
			recalculate();
			pickStep.setRelativeTeachedOffset(null);
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		}
	}
	
	public void changedAmount(final int amount) {
		logger.info("Set amount [" + amount + "].");
		if (amount != deviceSettings.getAmount()) {
			deviceSettings.setAmount(amount);
			recalculate();
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, true));
		}
	}
	
	public void recalculate() {
		getView().hideNotification();
		if (!isWeightOk()) {
			getView().showNotification(Translator.getTranslation(WEIGHT_ZERO));
		}
		getView().refresh();
		//TODO implement further
	}
	
	public void changedWeight(final float weight) {
		workPiece.setWeight(weight);
		getView().setWeight(workPiece.getMaterial(), workPiece.getWeight());
		pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
	}
	
	public void changedAmountContinous() {
		logger.info("Changed amount to continous");
		if (deviceSettings.getAmount() != -1) {
			deviceSettings.setAmount(-1);
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			recalculate();
		}
	}
	
	public void changedAmountFixedAmount() {
		logger.info("Changed amount to fixed");
		if (deviceSettings.getAmount() == -1) {
			deviceSettings.setAmount(0);
			pickStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
			recalculate();
		}
	}
	
	@Override
	public boolean isConfigured() {
		if ((dimensions != null) && (workPiece.getWeight() > 0) &&
				dimensions.getLength() >= dimensions.getWidth() &&
				((deviceSettings.getAmount() > 0) || (deviceSettings.getAmount() == -1))
				) {
			return true;
		}
		return false;
	}
	
	public void setMaxAmount() {
		
	}
}
